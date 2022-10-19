package dev.ishikawa.lovejvm.vm;

import static dev.ishikawa.lovejvm.vm.RawSystem.heapManager;
import static dev.ishikawa.lovejvm.vm.RawSystem.methodAreaManager;

import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.attr.AttrCode;
import dev.ishikawa.lovejvm.rawclass.attr.AttrName;
import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantFieldref;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantInterfaceMethodref;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodref;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantPoolLoadableEntry;
import dev.ishikawa.lovejvm.rawclass.field.RawField;
import dev.ishikawa.lovejvm.rawclass.method.RawMethod;
import dev.ishikawa.lovejvm.rawclass.method.exceptionhandler.ExceptionInfo;
import dev.ishikawa.lovejvm.rawclass.type.JvmType;
import dev.ishikawa.lovejvm.rawclass.type.RawArrayClass;
import dev.ishikawa.lovejvm.rawobject.RawObject;
import dev.ishikawa.lovejvm.util.ByteUtil;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class RawThread {
  private final String name;
  // REFACTOR: have a stack in thread level.
  private final Deque<Frame> frames = new ArrayDeque<>();
  private int pc = 0;

  public RawThread(String name) {
    this.name = name;
  }

  /**
   * init the thread with the given method.
   * Note: initial frame must be a static method, and static method doesn't have a receiver.
   */
  public RawThread init(RawMethod entryPoint) {
    stackUp(entryPoint, 0);
    return this;
  }

  /**
   * In order for JVM to invoke a method.
   * This is used for initializing Class, Field, Method object, and resolivng Dynamic Const.
   * */
  public void invoke(RawMethod method, List<Word> arguments) {
    stackUp(method, 0);

    var locals = new Word[method.getLocalsSize()];
    for (int i = 0; i < arguments.size(); i++) {
      locals[i] = arguments.get(i);
    }

    currentFrame().setLocals(locals);
    this.run();
  }

  /**
   * add a new frame. move necessary words from current operandStack to new frame's locals
   *
   * @param {int} pcToReturn next pc in current frame. not in the next frame. the caller of stackUp
   *     has to calculate pcToReturn beforehand.
   * @return true when stackUp actually adds a new frame
   */
  private void stackUp(RawMethod nextMethod, int pcToReturn) {
    pc = methodAreaManager.lookupCodeSectionAddress(nextMethod);

    if (nextMethod.isAbstract()) {
      throw new RuntimeException(
          String.format("abstract method is called. method: %s", nextMethod.getName().getLabel()));
    }

    if (nextMethod.isNative()) {
      // note: arguments are popped in this handle method
      List<Word> result = RawSystem.nativeMethodHandler.handle(nextMethod, currentFrame());
      pushFromTail(result, currentFrame().getOperandStack());
      pc = pcToReturn;
      return;
    }

    Frame newFrame = new Frame(this, nextMethod);

    if (currentFrame() != null) {
      // NOTE: store previous PC on the bottom of operandStack
      // so that it can go back to previous PC when calling stackDown
      newFrame.getOperandStack().push(Word.of(pcToReturn));

      int transitWordSize = nextMethod.getTransitWordSize();
      List<JvmType> argumentsTypeInfo = nextMethod.getArgumentsToTranswer();
      Collections.reverse(argumentsTypeInfo); // push arguments from the tail
      int pos = 0;

      for (JvmType aType : argumentsTypeInfo) {
        for (int j = 0; j < aType.wordSize(); j++) {
          newFrame.getLocals()[transitWordSize - 1 - (aType.wordSize() - 1) - pos + j] =
              currentFrame().getOperandStack().pop();
        }
        pos += aType.wordSize();
      }
    } else {
      // this pcToReturn won't be used because pcToReturn is for returning to the previous frame
      newFrame.getOperandStack().push(Word.of(pcToReturn));

      // this is the entry point method.
      // REFACTOR: need to pass initial args passed from command line to this method.
      // main(String[] args) <- this args.
    }

    frames.push(newFrame);
  }

  private void stackDown() {
    // set next line of previousFrame's pc.
    pc = currentFrame().getOperandStack().getLast().getValue();
    // release from mem
    this.frames.pop();
  }

  private boolean canStackDown() {
    return currentFrame() != this.frames.getLast();
  }

  private Frame currentFrame() {
    return this.frames.peek();
  }

  // REFACTOR
  private Frame previousFrame() {
    var tmp = this.frames.pop();
    var ret = this.frames.peek();
    this.frames.push(tmp);
    return ret;
  }

  public void run() {
    while (true) {
      var instruction = Instruction.findBy(methodAreaManager.lookupByte(pc));
      dump(name, pc, instruction, this);

      Deque<Word> operandStack = currentFrame().getOperandStack();
      Word[] locals = currentFrame().getLocals();
      ConstantPool constantPool = currentFrame().getMethod().getKlass().getConstantPool();

      switch (instruction) {
        case NOP:
          {
            pc += 1;
            break;
          }
        case ACONST_NULL:
          {
            // push "null"(objectId = 0)
            operandStack.push(Word.of(RawObject.NULL_ID));
            pc = pc + 1;
            break;
          }
        case ICONST_M1:
          {
            operandStack.push(Word.of(-1));
            pc = pc + 1;
            break;
          }
        case ICONST_0:
          {
            operandStack.push(Word.of(0));
            pc = pc + 1;
            break;
          }
        case ICONST_1:
          {
            operandStack.push(Word.of(1));
            pc = pc + 1;
            break;
          }
        case ICONST_2:
          {
            operandStack.push(Word.of(2));
            pc = pc + 1;
            break;
          }
        case ICONST_3:
          {
            operandStack.push(Word.of(3));
            pc = pc + 1;
            break;
          }
        case ICONST_4:
          {
            operandStack.push(Word.of(4));
            pc = pc + 1;
            break;
          }
        case ICONST_5:
          {
            operandStack.push(Word.of(5));
            pc = pc + 1;
            break;
          }
        case LCONST_0:
          {
            pushFromTail(Word.of(0L), operandStack);
            pc = pc + 1;
            break;
          }
        case LCONST_1:
          {
            pushFromTail(Word.of(1L), operandStack);
            pc = pc + 1;
            break;
          }
        case FCONST_0:
          {
            operandStack.push(Word.of(0.0f));
            pc = pc + 1;
            break;
          }
        case FCONST_1:
          {
            operandStack.push(Word.of(1.0f));
            pc = pc + 1;
            break;
          }
        case FCONST_2:
          {
            operandStack.push(Word.of(2.0f));
            pc = pc + 1;
            break;
          }
        case DCONST_0:
          {
            pushFromTail(Word.of(0.0D), operandStack);
            pc = pc + 1;
            break;
          }
        case DCONST_1:
          {
            pushFromTail(Word.of(1.0D), operandStack);
            pc = pc + 1;
            break;
          }
        case BIPUSH:
          {
            // push a byte(1bit:sign, 7bit:number) onto the stack as an integer (sign expand)
            var a = (int) methodAreaManager.lookupByte(pc + 1);
            operandStack.push(Word.of(a));
            pc = pc + 2;
            break;
          }
        case SIPUSH: // sipush
          {
            // push a short(2 bytes) onto the stack as an integer
            operandStack.push(Word.of(peekTwoBytes()));
            pc = pc + 3;
            break;
          }
        case LDC:
          {
            /*
             * push a loadable constant entry(of #index) from a constant pool onto the stack.
             * this loadable value should be 1 word with LDC instruction
             */
            var index = Byte.toUnsignedInt(methodAreaManager.lookupByte(pc + 1));

            ConstantPoolLoadableEntry entry =
                (ConstantPoolLoadableEntry) constantPool.findByIndex(index);
            entry.resolve(constantPool);

            Word word = entry.loadableValue().get(0);
            operandStack.push(word);
            pc = pc + 2;
            break;
          }
        case LDC_W:
          {
            /*
             * ldc_w works in a basically same way as ldc.
             * the difference is the size of entry index. ldc = 1byte, ldc_w = 2bytes.
             * This is for a large ConstantPool.
             *
             * This loadable value should be 1 word with LDC_W
             */
            var index = peekTwoBytes();

            ConstantPoolLoadableEntry entry =
                (ConstantPoolLoadableEntry) constantPool.findByIndex(index);
            entry.resolve(constantPool);

            Word word = entry.loadableValue().get(0);
            operandStack.push(word);
            pc = pc + 3;
            break;
          }
        case LDC2_W:
          {
            /*
             * push a loadable constant entry(of #index) from a constant pool onto the stack.
             * the index is 2 bytes, and the loadable value is 2 words.
             */
            var index = peekTwoBytes();

            ConstantPoolLoadableEntry entry =
                (ConstantPoolLoadableEntry) constantPool.findByIndex(index);
            entry.resolve(constantPool);

            List<Word> words = entry.loadableValue();
            pushFromTail(words, operandStack);
            pc = pc + 3;
            break;
          }
        case ILOAD:
        case FLOAD:
        case ALOAD:
          {
            var index = Byte.toUnsignedInt(methodAreaManager.lookupByte(pc + 1));
            operandStack.push(locals[index]);
            pc = pc + 2;
            break;
          }
        case LLOAD:
        case DLOAD:
          {
            var index = Byte.toUnsignedInt(methodAreaManager.lookupByte(pc + 1));
            operandStack.push(locals[index + 1]);
            operandStack.push(locals[index]);
            pc = pc + 2;
            break;
          }
        case ILOAD_0:
          {
            operandStack.push(locals[0]);
            pc = pc + 1;
            break;
          }
        case ILOAD_1:
          {
            operandStack.push(locals[1]);
            pc = pc + 1;
            break;
          }
        case ILOAD_2:
          {
            operandStack.push(locals[2]);
            pc = pc + 1;
            break;
          }
        case ILOAD_3:
          {
            operandStack.push(locals[3]);
            pc = pc + 1;
            break;
          }
        case LLOAD_0:
        case DLOAD_0:
          {
            operandStack.push(locals[1]);
            operandStack.push(locals[0]);
            pc = pc + 1;
            break;
          }
        case LLOAD_1:
        case DLOAD_1:
          {
            operandStack.push(locals[2]);
            operandStack.push(locals[1]);
            pc = pc + 1;
            break;
          }
        case LLOAD_2:
        case DLOAD_2:
          {
            operandStack.push(locals[3]);
            operandStack.push(locals[2]);
            pc = pc + 1;
            break;
          }
        case LLOAD_3:
        case DLOAD_3:
          {
            operandStack.push(locals[4]);
            operandStack.push(locals[3]);
            pc = pc + 1;
            break;
          }
        case FLOAD_0:
          {
            operandStack.push(locals[0]);
            pc = pc + 1;
            break;
          }
        case FLOAD_1:
          {
            operandStack.push(locals[1]);
            pc = pc + 1;
            break;
          }
        case FLOAD_2:
          {
            operandStack.push(locals[2]);
            pc = pc + 1;
            break;
          }
        case FLOAD_3:
          {
            operandStack.push(locals[3]);
            pc = pc + 1;
            break;
          }
        case ALOAD_0:
          {
            operandStack.push(locals[0]);
            pc = pc + 1;
            break;
          }
        case ALOAD_1:
          {
            operandStack.push(locals[1]);
            pc = pc + 1;
            break;
          }
        case ALOAD_2:
          {
            operandStack.push(locals[2]);
            pc = pc + 1;
            break;
          }
        case ALOAD_3:
          {
            operandStack.push(locals[3]);
            pc = pc + 1;
            break;
          }
        case IALOAD:
        case LALOAD:
        case FALOAD:
        case DALOAD:
        case AALOAD:
        case BALOAD:
        case CALOAD:
        case SALOAD:
          {
            var position = operandStack.pop().getValue();
            var arrayObjectId = operandStack.pop().getValue();

            var arrayObject = heapManager.lookupObject(arrayObjectId);
            var value = heapManager.getElement(arrayObject, position);

            pushFromTail(value, operandStack);

            pc = pc + 1;
            break;
          }
        case LSTORE:
        case DSTORE:
          {
            var index = Byte.toUnsignedInt(methodAreaManager.lookupByte(pc + 1));
            locals[index] = operandStack.pop();
            locals[index + 1] = operandStack.pop();
            pc = pc + 2;
            break;
          }
        case ISTORE:
        case FSTORE:
        case ASTORE:
          {
            var index = Byte.toUnsignedInt(methodAreaManager.lookupByte(pc + 1));
            locals[index] = operandStack.pop();
            pc = pc + 2;
            break;
          }
        case ISTORE_0:
        case FSTORE_0:
        case ASTORE_0:
          {
            locals[0] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case ISTORE_1:
        case FSTORE_1:
        case ASTORE_1:
          {
            locals[1] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case ISTORE_2:
        case FSTORE_2:
        case ASTORE_2:
          {
            locals[2] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case ISTORE_3:
        case FSTORE_3:
        case ASTORE_3:
          {
            locals[3] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case LSTORE_0:
        case DSTORE_0:
          {
            locals[0] = operandStack.pop();
            locals[1] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case LSTORE_1:
        case DSTORE_1:
          {
            locals[1] = operandStack.pop();
            locals[2] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case LSTORE_2:
        case DSTORE_2:
          {
            locals[2] = operandStack.pop();
            locals[3] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case LSTORE_3:
        case DSTORE_3:
          {
            locals[3] = operandStack.pop();
            locals[4] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case LASTORE:
        case DASTORE:
          {
            var v1 = currentFrame().getOperandStack().pop();
            var v2 = currentFrame().getOperandStack().pop();
            List<Word> value = List.of(v1, v2);

            int position = currentFrame().getOperandStack().pop().getValue();
            int objectId = currentFrame().getOperandStack().pop().getValue();
            RawObject rawObject = heapManager.lookupObject(objectId);

            heapManager.setElement(rawObject, position, value);
            pc += 1;
            break;
          }
        case IASTORE:
        case FASTORE:
        case AASTORE:
        case BASTORE:
        case CASTORE:
        case SASTORE:
          {
            List<Word> value = List.of(currentFrame().getOperandStack().pop());

            int position = currentFrame().getOperandStack().pop().getValue();
            int objectId = currentFrame().getOperandStack().pop().getValue();
            RawObject rawObject = heapManager.lookupObject(objectId);

            heapManager.setElement(rawObject, position, value);
            pc += 1;
            break;
          }
        case POP:
          {
            currentFrame().getOperandStack().pop();
            pc += 1;
            break;
          }
        case POP2:
          {
            currentFrame().getOperandStack().pop();
            currentFrame().getOperandStack().pop();
            pc += 1;
            break;
          }
        case DUP:
          {
            // [v1] -> [copy(v1), v1]
            Word v1 = currentFrame().getOperandStack().pop();
            currentFrame().getOperandStack().push(Word.of(v1));
            currentFrame().getOperandStack().push(v1);
            pc += 1;
            break;
          }
        case DUP_X1:
          {
            // [v2, v1] -> [copy(v1), v2, v1]
            Word v1 = currentFrame().getOperandStack().pop();
            Word v2 = currentFrame().getOperandStack().pop();
            operandStack.push(Word.of(v1));
            operandStack.push(v2);
            operandStack.push(v1);
            pc += 1;
            break;
          }
        case DUP_X2:
          {
            // [v3, v2, v1] -> [copy(v1), v3 ,v2, v1]
            Word v1 = currentFrame().getOperandStack().pop();
            Word v2 = currentFrame().getOperandStack().pop();
            Word v3 = currentFrame().getOperandStack().pop();
            operandStack.push(Word.of(v1));
            operandStack.push(v3);
            operandStack.push(v2);
            operandStack.push(v1);
            pc += 1;
            break;
          }
        case DUP2:
          {
            // [v2, v1] -> [copy(v2), copy(v1), v2, v1]
            Word v1 = currentFrame().getOperandStack().pop();
            Word v2 = currentFrame().getOperandStack().pop();
            operandStack.push(Word.of(v2));
            operandStack.push(Word.of(v1));
            operandStack.push(v2);
            operandStack.push(v1);
            pc += 1;
            break;
          }
        case DUP2_X1:
          {
            // [v3, v2, v1] -> [copy(v2), copy(v1), v3, v2, v1]
            Word v1 = currentFrame().getOperandStack().pop();
            Word v2 = currentFrame().getOperandStack().pop();
            Word v3 = currentFrame().getOperandStack().pop();
            operandStack.push(Word.of(v2));
            operandStack.push(Word.of(v1));
            operandStack.push(v3);
            operandStack.push(v2);
            operandStack.push(v1);
            pc += 1;
            break;
          }
        case DUP2_X2:
          {
            // [v4, v3, v2, v1] -> [copy(v2), copy(v1), v4, v3, v2, v1]
            Word v1 = currentFrame().getOperandStack().pop();
            Word v2 = currentFrame().getOperandStack().pop();
            Word v3 = currentFrame().getOperandStack().pop();
            Word v4 = currentFrame().getOperandStack().pop();
            operandStack.push(Word.of(v2));
            operandStack.push(Word.of(v1));
            operandStack.push(v4);
            operandStack.push(v3);
            operandStack.push(v2);
            operandStack.push(v1);
            pc += 1;
            break;
          }
        case SWAP:
          {
            // [v2, v1] -> [v1, v2]
            Word v1 = currentFrame().getOperandStack().pop();
            Word v2 = currentFrame().getOperandStack().pop();
            operandStack.push(v1);
            operandStack.push(v2);
            pc += 1;
            break;
          }
        case IADD:
          {
            int right = operandStack.pop().getValue();
            int left = operandStack.pop().getValue();
            operandStack.push(Word.of(left + right));
            pc = pc + 1;
            break;
          }
        case LADD:
          {
            // [v2, v1, v4, v3] -> left = Long(v1, v2), right = Long(v3, v4)
            int v3 = operandStack.pop().getValue();
            int v4 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            long left = ByteUtil.concatToLong(v1, v2);
            long right = ByteUtil.concatToLong(v3, v4);
            pushFromTail(Word.of(left + right), operandStack);

            pc = pc + 1;
            break;
          }
        case FADD:
          {
            float right = ByteUtil.convertToFloat(operandStack.pop().getValue());
            float left = ByteUtil.convertToFloat(operandStack.pop().getValue());
            operandStack.push(Word.of(left + right));
            pc = pc + 1;
            break;
          }
        case DADD:
          {
            // [v2, v1, v4, v3] -> left = Double(v1, v2), right = Double(v3, v4)
            int v3 = operandStack.pop().getValue();
            int v4 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            double left = ByteUtil.concatToDouble(v1, v2);
            double right = ByteUtil.concatToDouble(v3, v4);
            pushFromTail(Word.of(left + right), operandStack);

            pc = pc + 1;
            break;
          }
        case ISUB:
          {
            int right = operandStack.pop().getValue();
            int left = operandStack.pop().getValue();
            operandStack.push(Word.of(left - right));
            pc = pc + 1;
            break;
          }
        case LSUB:
          {
            // [v2, v1, v4, v3] -> left = Long(v1, v2), right = Long(v3, v4)
            int v3 = operandStack.pop().getValue();
            int v4 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            long left = ByteUtil.concatToLong(v1, v2);
            long right = ByteUtil.concatToLong(v3, v4);
            pushFromTail(Word.of(left - right), operandStack);

            pc = pc + 1;
            break;
          }
        case FSUB:
          {
            float right = ByteUtil.convertToFloat(operandStack.pop().getValue());
            float left = ByteUtil.convertToFloat(operandStack.pop().getValue());
            operandStack.push(Word.of(left - right));
            pc = pc + 1;
            break;
          }
        case DSUB:
          {
            // [v2, v1, v4, v3] -> left = Double(v1, v2), right = Double(v3, v4)
            int v3 = operandStack.pop().getValue();
            int v4 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            double left = ByteUtil.concatToDouble(v1, v2);
            double right = ByteUtil.concatToDouble(v3, v4);
            pushFromTail(Word.of(left - right), operandStack);

            pc = pc + 1;
            break;
          }
        case IMUL:
          {
            int right = operandStack.pop().getValue();
            int left = operandStack.pop().getValue();
            operandStack.push(Word.of(left * right));
            pc = pc + 1;
            break;
          }
        case LMUL:
          {
            // [v2, v1, v4, v3] -> left = Long(v1, v2), right = Long(v3, v4)
            int v3 = operandStack.pop().getValue();
            int v4 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            long left = ByteUtil.concatToLong(v1, v2);
            long right = ByteUtil.concatToLong(v3, v4);
            pushFromTail(Word.of(left * right), operandStack);

            pc = pc + 1;
            break;
          }
        case FMUL:
          {
            float right = ByteUtil.convertToFloat(operandStack.pop().getValue());
            float left = ByteUtil.convertToFloat(operandStack.pop().getValue());
            operandStack.push(Word.of(left * right));
            pc = pc + 1;
            break;
          }
        case DMUL:
          {
            // [v2, v1, v4, v3] -> left = Double(v1, v2), right = Double(v3, v4)
            int v3 = operandStack.pop().getValue();
            int v4 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            double left = ByteUtil.concatToDouble(v1, v2);
            double right = ByteUtil.concatToDouble(v3, v4);
            long result = Double.doubleToLongBits(left * right);
            pushFromTail(Word.of(result), operandStack);
            pc = pc + 1;
            break;
          }
        case IDIV:
          {
            int right = operandStack.pop().getValue();
            int left = operandStack.pop().getValue();
            operandStack.push(Word.of(left / right));
            pc = pc + 1;
            break;
          }
        case LDIV:
          {
            // [v2, v1, v4, v3] -> left = Long(v1, v2), right = Long(v3, v4)
            int v3 = operandStack.pop().getValue();
            int v4 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            long left = ByteUtil.concatToLong(v1, v2);
            long right = ByteUtil.concatToLong(v3, v4);
            pushFromTail(Word.of(left / right), operandStack);

            pc = pc + 1;
            break;
          }
        case FDIV:
          {
            float right = ByteUtil.convertToFloat(operandStack.pop().getValue());
            float left = ByteUtil.convertToFloat(operandStack.pop().getValue());
            operandStack.push(Word.of(left / right));
            pc = pc + 1;
            break;
          }
        case DDIV:
          {
            // [v2, v1, v4, v3] -> left = Double(v1, v2), right = Double(v3, v4)
            int v3 = operandStack.pop().getValue();
            int v4 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            double left = ByteUtil.concatToDouble(v1, v2);
            double right = ByteUtil.concatToDouble(v3, v4);
            long result = Double.doubleToLongBits(left / right);
            pushFromTail(Word.of(result), operandStack);
            pc = pc + 1;
            break;
          }
        case IREM:
          {
            int right = operandStack.pop().getValue();
            int left = operandStack.pop().getValue();
            operandStack.push(Word.of(left % right));
            pc = pc + 1;
            break;
          }
        case LREM:
          {
            // [v2, v1, v4, v3] -> left = Long(v1, v2), right = Long(v3, v4)
            int v3 = operandStack.pop().getValue();
            int v4 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            long left = ByteUtil.concatToLong(v1, v2);
            long right = ByteUtil.concatToLong(v3, v4);
            pushFromTail(Word.of(left % right), operandStack);
            pc = pc + 1;
            break;
          }
        case FREM:
          {
            float right = ByteUtil.convertToFloat(operandStack.pop().getValue());
            float left = ByteUtil.convertToFloat(operandStack.pop().getValue());
            operandStack.push(Word.of(left % right));
            pc = pc + 1;
            break;
          }
        case DREM:
          {
            // [v2, v1, v4, v3]
            int v3 = operandStack.pop().getValue();
            int v4 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            double left = ByteUtil.concatToDouble(v1, v2);
            double right = ByteUtil.concatToDouble(v3, v4);
            pushFromTail(Word.of(left % right), operandStack);

            pc = pc + 1;
            break;
          }
        case INEG:
          {
            int a = operandStack.pop().getValue();
            operandStack.push(Word.of(-1 * a));
            pc = pc + 1;
            break;
          }
        case LNEG:
          {
            int v1 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            long a = ByteUtil.concatToLong(v1, v2);
            pushFromTail(Word.of(-1 * a), operandStack);

            pc = pc + 1;
            break;
          }
        case FNEG:
          {
            float a = ByteUtil.convertToFloat(operandStack.pop().getValue());
            operandStack.push(Word.of(-1 * a));
            pc = pc + 1;
            break;
          }
        case DNEG:
          {
            int v1 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            double a = ByteUtil.concatToDouble(v1, v2);
            pushFromTail(Word.of(-1 * a), operandStack);

            pc = pc + 1;
            break;
          }
        case ISHL: // int shift left
          {
            int right = operandStack.pop().getValue();
            int left = operandStack.pop().getValue();
            operandStack.push(Word.of(left << right));
            pc = pc + 1;
            break;
          }
        case LSHL: // long shift left
          {
            // [v2, v1, v3] -> left = Long(v1, v2), right = Int(v3)
            int right = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            long left = ByteUtil.concatToLong(v1, v2);
            pushFromTail(Word.of(left << right), operandStack);

            pc = pc + 1;
            break;
          }
        case ISHR: // int shift right
          {
            int right = operandStack.pop().getValue();
            int left = operandStack.pop().getValue();
            operandStack.push(Word.of(left >> right));
            pc = pc + 1;
            break;
          }
        case LSHR: // long shift right
          {
            // [v2, v1, v3] -> left = Long(v1, v2), right = Int(v3)
            int right = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            long left = ByteUtil.concatToLong(v1, v2);
            pushFromTail(Word.of(left >> right), operandStack);

            pc = pc + 1;
            break;
          }
        case IUSHR: // int logical shift right
          {
            int right = operandStack.pop().getValue();
            int left = operandStack.pop().getValue();
            operandStack.push(Word.of(left >>> right));
            pc = pc + 1;
            break;
          }
        case LUSHR: // long logical shift right
          {
            // [v2, v1, v3] -> left = Long(v1, v2), right = Int(v3) -> (left >>> right)
            int right = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            long left = ByteUtil.concatToLong(v1, v2);
            pushFromTail(Word.of(left >>> right), operandStack);

            pc = pc + 1;
            break;
          }
        case IAND:
          {
            int right = operandStack.pop().getValue();
            int left = operandStack.pop().getValue();
            operandStack.push(Word.of(left & right));
            pc = pc + 1;
            break;
          }
        case LAND: // land
          {
            // [v2, v1, v4, v3] -> left = Long(v1, v2), right = Long(v3, v4)
            int v3 = operandStack.pop().getValue();
            int v4 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            long left = ByteUtil.concatToLong(v1, v2);
            long right = ByteUtil.concatToLong(v3, v4);
            pushFromTail(Word.of(left & right), operandStack);

            pc = pc + 1;
            break;
          }
        case IOR:
          {
            int right = operandStack.pop().getValue();
            int left = operandStack.pop().getValue();
            operandStack.push(Word.of(left | right));
            pc = pc + 1;
            break;
          }
        case LOR:
          {
            // [v2, v1, v4, v3] -> left = Long(v1, v2), right = Long(v3, v4)
            int v3 = operandStack.pop().getValue();
            int v4 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            long left = ByteUtil.concatToLong(v1, v2);
            long right = ByteUtil.concatToLong(v3, v4);
            pushFromTail(Word.of(left | right), operandStack);

            pc = pc + 1;
            break;
          }
        case IXOR:
          {
            int right = operandStack.pop().getValue();
            int left = operandStack.pop().getValue();
            operandStack.push(Word.of(left ^ right));
            pc = pc + 1;
            break;
          }
        case LXOR:
          {
            // [v2, v1, v4, v3] -> left = Long(v1, v2), right = Long(v3, v4)
            int v3 = operandStack.pop().getValue();
            int v4 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            long left = ByteUtil.concatToLong(v1, v2);
            long right = ByteUtil.concatToLong(v3, v4);
            pushFromTail(Word.of(left ^ right), operandStack);

            pc = pc + 1;
            break;
          }
        case IINC:
          {
            int index = Byte.toUnsignedInt(methodAreaManager.lookupByte(pc + 1));
            byte incVal = methodAreaManager.lookupByte(pc + 2);
            locals[index] = Word.of(locals[index].getValue() + incVal);
            pc = pc + 3;
            break;
          }
        case I2L:
          {
            var a = operandStack.pop().getValue();
            pushFromTail(Word.of((long) a), operandStack);

            pc += 1;
            break;
          }
        case I2F:
          {
            // 10 -> 10.0f
            var a = operandStack.pop().getValue();
            operandStack.push(Word.of((float) a));
            pc += 1;
            break;
          }
        case I2D:
          {
            // 10 -> 10.0D
            var a = operandStack.pop().getValue();
            pushFromTail(Word.of((double) a), operandStack);

            pc += 1;
            break;
          }
        case L2I:
          {
            var v1 = operandStack.pop().getValue();
            var v2 = operandStack.pop().getValue();
            long a = ByteUtil.concatToLong(v1, v2);
            Word word = Word.of((int) a);
            operandStack.push(word);
            pc += 1;
            break;
          }
        case L2F:
          {
            var v1 = operandStack.pop().getValue();
            var v2 = operandStack.pop().getValue();
            var a = ByteUtil.concatToLong(v1, v2);
            Word word = Word.of((float) a);
            operandStack.push(word);
            pc += 1;
            break;
          }
        case L2D:
          {
            // truncate long to int
            var v1 = operandStack.pop().getValue();
            var v2 = operandStack.pop().getValue();
            var a = ByteUtil.concatToLong(v1, v2);
            List<Word> words = Word.of((double) a);
            pushFromTail(words, operandStack);

            pc += 1;
            break;
          }
        case F2I:
          {
            float a = ByteUtil.convertToFloat(operandStack.pop().getValue());
            operandStack.push(Word.of((int) a));
            pc += 1;
            break;
          }
        case F2L:
          {
            float a = ByteUtil.convertToFloat(operandStack.pop().getValue());
            pushFromTail(Word.of((long) a), operandStack);

            pc += 1;
            break;
          }
        case F2D:
          {
            float a = ByteUtil.convertToFloat(operandStack.pop().getValue());
            pushFromTail(Word.of((double) a), operandStack);

            pc += 1;
            break;
          }
        case D2I:
          {
            var v1 = operandStack.pop().getValue();
            var v2 = operandStack.pop().getValue();
            double a = ByteUtil.concatToDouble(v1, v2);
            operandStack.push(Word.of((int) a));
            pc += 1;
            break;
          }
        case D2L:
          {
            var v1 = operandStack.pop().getValue();
            var v2 = operandStack.pop().getValue();
            double a = ByteUtil.concatToDouble(v1, v2);
            pushFromTail(Word.of((long) a), operandStack);

            pc += 1;
            break;
          }
        case D2F:
          {
            var v1 = operandStack.pop().getValue();
            var v2 = operandStack.pop().getValue();
            double a = ByteUtil.concatToDouble(v1, v2);
            operandStack.push(Word.of((float) a));
            pc += 1;
            break;
          }
        case I2B:
          {
            var a = operandStack.pop().getValue();
            operandStack.push(Word.of((byte) a));
            pc += 1;
            break;
          }
        case I2C:
          {
            Word word = Word.of(((byte) operandStack.pop().getValue()));
            operandStack.push(word);
            pc += 1;
            break;
          }
        case I2S:
          {
            Word word = Word.of(((short) operandStack.pop().getValue()));
            operandStack.push(word);
            pc += 1;
            break;
          }
        case LCMP:
          {
            // if(left == right) push(0)
            // if(left > right>  push(1)
            // if(left < right>  push(-1)
            int v3 = operandStack.pop().getValue();
            int v4 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            long left = ByteUtil.concatToLong(v1, v2);
            long right = ByteUtil.concatToLong(v3, v4);

            Word word = Word.of(Long.compare(left, right));
            operandStack.push(word);
            pc += 1;
            break;
          }
        case FCMPL:
          {
            float right = ByteUtil.convertToFloat(operandStack.pop().getValue());
            float left = ByteUtil.convertToFloat(operandStack.pop().getValue());

            Word word = Word.of(Float.compare(left, right));
            operandStack.push(word);
            pc += 1;
            break;
          }
        case FCMPG:
          {
            float right = ByteUtil.convertToFloat(operandStack.pop().getValue());
            float left = ByteUtil.convertToFloat(operandStack.pop().getValue());

            Word word = Word.of(Float.compare(right, left));
            operandStack.push(word);
            pc += 1;
            break;
          }
        case DCMPL:
          {
            int v3 = operandStack.pop().getValue();
            int v4 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            double left = ByteUtil.concatToDouble(v1, v2);
            double right = ByteUtil.concatToDouble(v3, v4);
            Word word = Word.of(Double.compare(left, right));
            operandStack.push(word);
            pc += 1;
            break;
          }
        case DCMPG:
          {
            int v3 = operandStack.pop().getValue();
            int v4 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            double left = ByteUtil.concatToDouble(v1, v2);
            double right = ByteUtil.concatToDouble(v3, v4);
            Word word = Word.of(Double.compare(right, left));
            operandStack.push(word);
            pc += 1;
            break;
          }
        case IFEQ:
          {
            // if (value == 0) then jump to jumpTo
            int value = operandStack.pop().getValue();
            int jumpTo = peekTwoBytes();

            if (value == 0) {
              pc = pc + jumpTo;
            } else {
              pc = pc + 3;
            }
            break;
          }
        case IFNE:
          {
            // if (value != 0) then jump to jumpTo
            int value = operandStack.pop().getValue();
            int jumpTo = peekTwoBytes();

            if (value != 0) {
              pc = pc + jumpTo;
            } else {
              pc = pc + 3;
            }
            break;
          }
        case IFLT:
          {
            // if (value < 0) then jump to jumpTo
            int value = operandStack.pop().getValue();
            int jumpTo = peekTwoBytes();

            if (value < 0) {
              pc = pc + jumpTo;
            } else {
              pc = pc + 3;
            }
            break;
          }
        case IFGE:
          {
            // if (value >= 0) then jump to jumpTo
            int value = operandStack.pop().getValue();
            int jumpTo = peekTwoBytes();

            if (value >= 0) {
              pc = pc + jumpTo;
            } else {
              pc = pc + 3;
            }
            break;
          }
        case IFGT:
          {
            // if (value > 0) then jump to jumpTo
            int value = operandStack.pop().getValue();
            int jumpTo = peekTwoBytes();

            if (value > 0) {
              pc = pc + jumpTo;
            } else {
              pc = pc + 3;
            }
            break;
          }
        case IFLE:
          {
            // if (value <= 0) then jump to jumpTo
            int value = operandStack.pop().getValue();
            int jumpTo = peekTwoBytes();

            if (value <= 0) {
              pc = pc + jumpTo;
            } else {
              pc = pc + 3;
            }
            break;
          }
        case IF_ICMPEQ:
        case IF_ACMPEQ:
          {
            // if (left == right) then jump to jumpTo
            int right = operandStack.pop().getValue();
            int left = operandStack.pop().getValue();
            int jumpTo = peekTwoBytes();

            if (left == right) {
              pc = pc + jumpTo;
            } else {
              pc = pc + 3;
            }
            break;
          }
        case IF_ICMPNE:
        case IF_ACMPNE:
          {
            // if (left != right) then jump to jumpTo
            int right = operandStack.pop().getValue();
            int left = operandStack.pop().getValue();
            int jumpTo = peekTwoBytes();

            if (left != right) {
              pc = pc + jumpTo;
            } else {
              pc = pc + 3;
            }
            break;
          }
        case IF_ICMPLT:
          {
            // if (left < right) then jump to jumpTo
            int right = operandStack.pop().getValue();
            int left = operandStack.pop().getValue();
            int jumpTo = peekTwoBytes();

            if (left < right) {
              pc = pc + jumpTo;
            } else {
              pc = pc + 3;
            }
            break;
          }
        case IF_ICMPGE:
          {
            // if (left >= right) then jump to jumpTo
            int right = operandStack.pop().getValue();
            int left = operandStack.pop().getValue();
            int jumpTo = peekTwoBytes();

            if (left >= right) {
              pc = pc + jumpTo;
            } else {
              pc = pc + 3;
            }
            break;
          }
        case IF_ICMPGT:
          {
            // if (left > right) then jump to jumpTo
            int right = operandStack.pop().getValue();
            int left = operandStack.pop().getValue();
            int jumpTo = peekTwoBytes();

            if (left > right) {
              pc = pc + jumpTo;
            } else {
              pc = pc + 3;
            }
            break;
          }
        case IF_ICMPLE:
          {
            // if (left <= right) then jump to jumpTo
            int right = operandStack.pop().getValue();
            int left = operandStack.pop().getValue();
            int jumpTo = peekTwoBytes();

            if (left <= right) {
              pc = pc + jumpTo;
            } else {
              pc = pc + 3;
            }
            break;
          }
        case GOTO:
          {
            int offset = peekTwoBytes();
            pc += offset;
            break;
          }
        case JSR:
          {
            throw new RuntimeException("JSR is not implemented yet");
          }
        case RET:
          {
            throw new RuntimeException("RET is not implemented yet");
          }
        case TABLESWITCH:
          {
            var index = operandStack.pop().getValue();
            var tableswitchOpAddress = pc;

            // padding
            //  0  1  2  3  4
            //                   57 -> 2+57 = 59. it means jumping to 59 as default.
            //       op pd  default     high      low  offset1  offset2  offset3 getstatic(next op)
            // 2A BE AA 00 00000039 00000000 00000002 0000001A 00000020 0000002B B2
            // 00 00 00 pc de
            // 00 00 pc 00 de
            // 00 pc 00 00 de
            // pc 00 00 00 de
            var codeSectionAddress =
                methodAreaManager.lookupCodeSectionAddress(currentFrame().getMethod());
            var paddingByteNum = 3 - ((pc - codeSectionAddress) % 4);
            pc += paddingByteNum;

            // default(32bits)
            var defaultOffset = peekFourBytes();
            pc += 4;

            // lowBytes(32bits)
            var low = peekFourBytes();
            pc += 4;

            // highBytes(32bits)
            var high = peekFourBytes();
            pc += 4;

            // if index is out of the range:[low, high],
            // then jump to tableswitch's opcode address + defaultValue
            if (index < low || index > high) {
              pc = tableswitchOpAddress + defaultOffset;
              break;
            }

            // offset entries
            var numOfOffsets = high - low + 1;
            var offsetTable = new int[numOfOffsets];

            for (int i = 0; i < numOfOffsets; i++) {
              var offset = peekFourBytes();
              pc += 4;
              offsetTable[i] = offset;
            }

            var offset = offsetTable[index - low];

            pc = tableswitchOpAddress + offset;
            break;
          }
        case LOOKUPSWITCH:
          {
            throw new RuntimeException("LOOKUPSWITCH is not implemented yet");
          }
        case IRETURN:
        case FRETURN:
        case ARETURN:
          {
            // ireturn/freturn/areturn returns one word
            // these operand codes must be able to stackDown
            var word = currentFrame().getOperandStack().pop();
            previousFrame().getOperandStack().push(word);
            stackDown();
            break;
          }
        case LRETURN: // lreturn
        case DRETURN: // dreturn
          {
            // TODO: test here
            // lreturn/dreturn returns two words
            // these operand codes must be able to stackDown
            var v1 = currentFrame().getOperandStack().pop();
            var v2 = currentFrame().getOperandStack().pop();
            previousFrame().getOperandStack().push(v2);
            previousFrame().getOperandStack().push(v1);
            stackDown();
            break;
          }
        case RETURN:
          {
            if (canStackDown()) {
              stackDown();
              break;
            }
            // this thread is finished
            return;
          }
        case GETSTATIC:
          {
            var index = peekTwoBytes();
            ConstantFieldref fieldRef = (ConstantFieldref) constantPool.findByIndex(index);
            fieldRef.resolve(constantPool);

            RawClass rawClass = methodAreaManager.lookupClass(fieldRef.getConstantClassRef());
            RawSystem.classLinker.link(rawClass);
            RawSystem.classInitializer.initialize(rawClass);

            RawField rawField = fieldRef.getRawField();

            List<Word> staticFieldValue = methodAreaManager.getStaticFieldValue(rawClass, rawField);
            pushFromTail(staticFieldValue, operandStack);

            pc = pc + 3;
            break;
          }
        case PUTSTATIC:
          {
            var index = peekTwoBytes();
            ConstantFieldref fieldRef = (ConstantFieldref) constantPool.findByIndex(index);
            fieldRef.resolve(constantPool);

            RawClass rawClass = methodAreaManager.lookupClass(fieldRef.getConstantClassRef());
            RawSystem.classLinker.link(rawClass);
            RawSystem.classInitializer.initialize(rawClass);

            RawField rawField = fieldRef.getRawField();

            JvmType jvmTypeName =
                JvmType.findByJvmSignature(fieldRef.getNameAndType().getDescriptor().getLabel());
            List<Word> words = retrieveData(jvmTypeName, operandStack);

            methodAreaManager.putStaticFieldValue(rawClass, rawField, words);

            pc = pc + 3;
            break;
          }
        case GETFIELD:
          {
            var index = peekTwoBytes();
            ConstantFieldref fieldRef = (ConstantFieldref) constantPool.findByIndex(index);
            fieldRef.resolve(constantPool);

            int objectId = operandStack.pop().getValue();
            RawObject object = heapManager.lookupObject(objectId);
            RawField rawField = fieldRef.getRawField();

            // REFACTOR: validate if the value is suitable for the field
            var words = heapManager.getValue(object, rawField);
            pushFromTail(words, operandStack);

            pc += 3;
            break;
          }
        case PUTFIELD:
          {
            var index = peekTwoBytes();
            ConstantFieldref fieldRef = (ConstantFieldref) constantPool.findByIndex(index);
            fieldRef.resolve(constantPool);

            // retrieve value to set
            JvmType jvmTypeName =
                JvmType.findByJvmSignature(fieldRef.getNameAndType().getDescriptor().getLabel());
            List<Word> words = retrieveData(jvmTypeName, operandStack);

            // retrieve the object's address
            int objectId = operandStack.pop().getValue();
            var object = RawSystem.heapManager.lookupObject(objectId);

            RawField rawField = fieldRef.getRawField();

            // set the value into field's address
            // REFACTOR: validate if the value is suitable for the field
            RawSystem.heapManager.setValue(object, rawField, words);

            pc += 3;
            break;
          }
        case INVOKEVIRTUAL:
          {
            // REFACTOR
            var index = peekTwoBytes();
            ConstantMethodref methodRef = (ConstantMethodref) constantPool.findByIndex(index);
            methodRef.resolve(constantPool);
            RawMethod rawMethod = methodRef.getRawMethod();

            RawMethod selectedMethod = findMethodToInvoke(rawMethod, operandStack);

            var nextPc = pc + 3;
            this.stackUp(selectedMethod, nextPc);

            // no need to update pc. pc is modified in stackUp
            break;
          }
        case INVOKESPECIAL:
        case INVOKESTATIC:
          {
            // REFACTOR
            var index = peekTwoBytes();
            ConstantMethodref methodRef = (ConstantMethodref) constantPool.findByIndex(index);
            methodRef.resolve(constantPool);
            RawMethod rawMethod = methodRef.getRawMethod();

            // TODO: no need to call findMethodToInvoke?

            var nextPc = pc + 3;
            this.stackUp(rawMethod, nextPc);

            break;
          }

        case INVOKEINTERFACE:
          {
            var index = peekTwoBytes();
            // count = num of args. no need to be used.
            var _count = methodAreaManager.lookupByte(pc + 3);
            var _no_used1 = methodAreaManager.lookupByte(pc + 4);

            ConstantInterfaceMethodref methodRef =
                (ConstantInterfaceMethodref) constantPool.findByIndex(index);
            methodRef.resolve(constantPool);
            RawMethod rawMethod = methodRef.getRawMethod();

            RawMethod selectedMethod = findMethodToInvoke(rawMethod, operandStack);

            var nextPc = pc + 5;
            this.stackUp(selectedMethod, nextPc);

            break;
          }
        case INVOKEDYNAMIC:
          {
            // TODO: implementation
            var index = peekTwoBytes();
            var _no_used1 = methodAreaManager.lookupByte(pc + 3);
            var _no_used2 = methodAreaManager.lookupByte(pc + 4);

            throw new RuntimeException("invokedynamic is not implemented yet");
            //            break;
          }
        case NEW:
          {
            // new instruction just allocates the necessary mem → objectref(=objectId)
            var index = peekTwoBytes();
            ConstantClass classRef = (ConstantClass) constantPool.findByIndex(index);
            classRef.resolve(constantPool);

            RawClass rawClass = methodAreaManager.lookupClass(classRef);
            int objectId = RawSystem.heapManager.newObject(rawClass);

            operandStack.push(Word.of(objectId));
            pc += 3;
            break;
          }
        case NEWARRAY:
          {
            /*
             * https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-6.html#jvms-6.5.newarray
             * newarray just allocates the necessary mem → objectref(=objectId)
             */
            // REFACTOR
            JvmType arrType;
            var arrTypeCode = methodAreaManager.lookupByte(pc + 1);
            switch (arrTypeCode) {
              case 4:
                arrType = JvmType.BOOLEAN;
                break;
              case 5:
                arrType = JvmType.CHAR;
                break;
              case 6:
                arrType = JvmType.FLOAT;
                break;
              case 7:
                arrType = JvmType.DOUBLE;
                break;
              case 8:
                arrType = JvmType.BYTE;
                break;
              case 9:
                arrType = JvmType.SHORT;
                break;
              case 10:
                arrType = JvmType.INT;
                break;
              case 11:
                arrType = JvmType.LONG;
                break;
              default:
                throw new RuntimeException(
                    String.format("unexpected arrTypeCode: %d", arrTypeCode));
            }

            int arrSize = operandStack.pop().getValue();
            int objectId =
                RawSystem.heapManager.newArrayObject(
                    RawArrayClass.lookupOrCreatePrimaryRawArrayClass(arrType, 1), arrSize);

            operandStack.push(Word.of(objectId));
            pc += 2;
            break;
          }
        case ANEWARRAY:
          {
            // anewarray just allocates the necessary mem → objectref(=objectId)
            ConstantClass classRef = (ConstantClass) constantPool.findByIndex(peekTwoBytes());
            classRef.resolve(constantPool);

            RawClass elementRawClass =
                methodAreaManager.lookupOrLoadClass(classRef.getName().getLabel());
            var rawArrayClass =
                RawArrayClass.lookupOrCreateComplexRawArrayClass(elementRawClass, 1);
            int arrSize = operandStack.pop().getValue();
            int objectId = RawSystem.heapManager.newArrayObject(rawArrayClass, arrSize);

            operandStack.push(Word.of(objectId));
            pc += 3;
            break;
          }
        case ARRAYLENGTH:
          {
            int objectId = operandStack.pop().getValue();
            var word = Word.of(heapManager.lookupObject(objectId).getArrSize());

            operandStack.push(word);
            pc += 1;
            break;
          }
        case ATHROW:
          {
            int exceptionObjectId = operandStack.pop().getValue();
            RawClass exceptionClass = heapManager.lookupObject(exceptionObjectId).getRawClass();

            // 1. make current stack empty
            for (int i = 0; i < (operandStack.size() - 1); i++) {
              operandStack.pop();
            }
            // 2. set a reference to the exception
            operandStack.push(Word.of(exceptionObjectId));

            // 3. lookup exception handler in current stackframe
            ExceptionInfo exceptionInfo =
                lookupExceptionHandler(Objects.requireNonNull(exceptionClass));

            // 4. jump to the exception handler
            pc += exceptionInfo.getHandlerPc();
            break;
          }
        case CHECKCAST:
          {
            int index = peekTwoBytes();
            ConstantClass classRef = (ConstantClass) constantPool.findByIndex(index);
            classRef.resolve(constantPool);
            // TODO: Class objectからrawClassを引けるようにする...
            RawClass castToClass = methodAreaManager.lookupClass(classRef);

            int objectId = operandStack.pop().getValue();
            if (objectId == RawObject.NULL_ID) {
              // nullは何にでもcastできる
            } else {
              RawClass castFromClass = heapManager.lookupObject(objectId).getRawClass();

              boolean isCastable = Objects.requireNonNull(castFromClass).isCastableTo(castToClass);

              if (!isCastable) {
                throw new RuntimeException("ClassCastException");
              }
            }

            operandStack.push(Word.of(objectId));
            pc += 3;
            break;
          }
        case INSTANCEOF:
          {
            int index = peekTwoBytes();
            ConstantClass classRef = (ConstantClass) constantPool.findByIndex(index);
            classRef.resolve(constantPool);

            int objectId = operandStack.pop().getValue();
            RawClass rawClass = heapManager.lookupObject(objectId).getRawClass();

            boolean isInstanceOf =
                classRef
                    .getName()
                    .getLabel()
                    .equals(Objects.requireNonNull(rawClass).getBinaryName());

            operandStack.push(Word.of(isInstanceOf));

            pc += 3;
            break;
          }
        case MONITORENTER:
          {
            throw new RuntimeException("MONITORENTER is not implemented yet");
            //            int objectId = operandStack.pop().getValue();
            //            pc += 1;
            //            break;
          }
        case MONITOREXIT:
          {
            throw new RuntimeException("MONITOREXIT is not implemented yet");
            //            int objectId = operandStack.pop().getValue();
            //            pc += 1;
            //            break;
          }
        case WIDE:
          {
            throw new RuntimeException("WIDE is not implemented yet");
          }
        case MULTIANEWARRAY:
          {
            ConstantClass classRef = (ConstantClass) constantPool.findByIndex(peekTwoBytes());
            classRef.resolve(constantPool);
            RawClass rawClass = methodAreaManager.lookupClass(classRef);
            int depth = methodAreaManager.lookupByte(pc + 3);

            int[] sizeList = new int[depth];
            for (int i = 0; i < depth; i++) {
              sizeList[sizeList.length - 1 - i] = operandStack.pop().getValue();
            }

            int objectId = createMultiArray(rawClass.asRawArrayClass(), depth, sizeList);

            operandStack.push(Word.of(objectId));

            pc += 4;
            break;
          }
        case IFNULL:
          {
            // if value is not null, jump to jumpTo
            int right = operandStack.pop().getValue();
            int jumpTo = peekTwoBytes();

            if (right == RawObject.NULL_ID) {
              pc = pc + jumpTo;
            } else {
              pc = pc + 3;
            }
            break;
          }
        case IFNONNULL:
          {
            // if value is not null, jump to jumpTo
            int right = operandStack.pop().getValue();
            int jumpTo = peekTwoBytes();

            if (right != RawObject.NULL_ID) {
              pc = pc + jumpTo;
            } else {
              pc = pc + 3;
            }
            break;
          }
        case GOTO_W:
          {
            throw new RuntimeException("GOTO_W is not implemented yet");
          }
        case JSR_W:
          {
            throw new RuntimeException("JSR_W is not implemented yet");
          }
        case BREAKPOINT:
        case IMPDEP1:
        case IMPDEP2:
          {
            // 0xca:
            // reserved for breakpoints in Java debuggers;
            // should not appear in any class file
            //
            // 0xfe, 0xff
            // reserved for implementation-dependent operations within debuggers;
            // should not appear in any class file
            pc += 1;
            break;
          }
          //        default:
          //          throw new RuntimeException(String.format("unrecognized instruction %x",
          // instruction));
      }
    }
  }

  private int peekTwoBytes() {
    return ByteUtil.concatToShort(
        methodAreaManager.lookupByte(pc + 1), methodAreaManager.lookupByte(pc + 2));
  }

  private int peekFourBytes() {
    return ByteUtil.concatToInt(
        methodAreaManager.lookupByte(pc + 1),
        methodAreaManager.lookupByte(pc + 2),
        methodAreaManager.lookupByte(pc + 3),
        methodAreaManager.lookupByte(pc + 4));
  }

  /**
   * When retrieving data from operandStack, it sometimes depends on what type is the data how many
   * words to pop
   */
  private List<Word> retrieveData(JvmType jvmTypeName, Deque<Word> operandStack) {
    List<Word> words;
    switch (jvmTypeName) {
      case LONG:
      case DOUBLE:
        // pop 2 words
        words = List.of(operandStack.pop(), operandStack.pop());
        break;
      default:
        // pop 1 word
        words = List.of(operandStack.pop());
    }
    return words;
  }

  // 3. if it's found, jump to the instruction area
  // 4. if it's not found, pop current stackframe, and lookup exception handler in the previous
  // stackframe.
  // 5. if no exception handler is found until popping all stackframes, system's exception handler
  // is used.
  private ExceptionInfo lookupExceptionHandler(@NotNull RawClass exceptionClass) {
    Optional<ExceptionInfo> exceptionInfo =
        currentFrame().getMethod().getAttrs().findAllBy(AttrName.CODE).stream()
            .findFirst()
            .flatMap(
                it -> {
                  int offset =
                      methodAreaManager.getOffsetFromCodeSectionToPc(
                          currentFrame().getMethod(), pc);
                  return ((AttrCode) it)
                      .getAttrBody()
                      .getExceptionInfoTable()
                      .findBy(exceptionClass.getBinaryName(), offset);
                });

    if (exceptionInfo.isPresent()) {
      return exceptionInfo.get();
    }

    if (canStackDown()) {
      stackDown();
      return lookupExceptionHandler(exceptionClass);
    } else {
      // no more stackframe.
      // handle this exception with system exception handler
      throw new RuntimeException(
          String.format("No exception handler was found. for: %s", exceptionClass.getBinaryName()));
    }
  }

  /**
   * @param rawArrayClass the root array's class
   * @param depth how many layers to create this time. [1, dimension]
   * @param sizeList list of item size for each layer. ex: int[1][4][2] -> [1, 4, 2]. int[3][9][][] -> [3, 9]
   * @return int objectId of the root array object
   * */
  private int createMultiArray(RawArrayClass rawArrayClass, int depth, int[] sizeList) {
    // this dimension means how many dimentions to actually create here.
    // this should be less than tha actual dimension defiend in the type info, and more than or
    // equal to 1.
    // the dimention of classRef.
    // ex:
    // bipush 3
    // multianewarray [[I 1
    // -> this will create new init [3][]
    // int[][] a = new int[3][];  => [null, null, null]
    //
    // ex:
    // bipush 3
    // bipush 4
    // multianewarray [[I 2
    // -> this will create new init [3][4]
    // int[][] b = new int[3][4]; => [[0, 0, 0, 0], [0, 0, 0, 0], [0, 0, 0, 0]]
    //
    // int[][] c = new int[][]; ... this is invalid.

    // top levelの要素数. dimensionの文だけpopする。少なくとも1かいはpopできる
    int size = sizeList[0];
    int objectId = RawSystem.heapManager.newArrayObject(rawArrayClass, size);
    RawObject rawObject = heapManager.lookupObject(objectId);

    // depthに応じて子要素を作る
    // 子要素がnon array(primitive or class)であれば完了
    // 子要素がarrayであってもdepthが1ならここまでで終了
    // ? MyObject[][] a = new MyObject[3][4];
    if (depth > 1) {
      createMultiArrayHelper(rawObject, sizeList, 1);
    }

    return objectId;
  }

  private void createMultiArrayHelper(RawObject parentObject, int[] sizeList, int depth) {
    String currentClassBinaryName = parentObject.getRawClass().getBinaryName().substring(1);
    int size = parentObject.getArrSize();

    if (!currentClassBinaryName.startsWith("[")) {
      // currentClassBinaryNameがarrayではない = 特に何も作る必要はない
    } else {
      // currentClassBinaryNameがarrayの場合 = そのchild array objectをsize分作ってset
      RawArrayClass childClass =
          methodAreaManager.lookupOrLoadClass(currentClassBinaryName).asRawArrayClass();
      int nextChildrenSize = sizeList[depth]; // operandStack.pop().getValue();
      for (int i = 0; i < size; i++) {
        int childObjectId = heapManager.newArrayObject(childClass, nextChildrenSize);
        RawObject childRawObject = heapManager.lookupObject(childObjectId);
        createMultiArrayHelper(childRawObject, sizeList, depth + 1);
        heapManager.setElement(parentObject, i, List.of(Word.of(childObjectId)));
      }
    }
  }

  private void pushFromTail(List<Word> words, Deque<Word> operandStack) {
    var mutable = words.stream().collect(Collectors.toList());
    Collections.reverse(mutable);
    mutable.forEach(operandStack::push);
  }

  private int peekReceiverObjectId(RawMethod rawMethod, Deque<Word> operandStack) {
    int numOfWordsToTransit = rawMethod.getTransitWordSize();

    Deque<Word> tmp = new ArrayDeque(numOfWordsToTransit);
    for (int i = 0; i < numOfWordsToTransit; i++) {
      tmp.push(operandStack.pop());
    }

    int receiverObjectId = tmp.getFirst().getValue();

    for (int i = 0; i < numOfWordsToTransit; i++) {
      operandStack.push(tmp.pop());
    }

    return receiverObjectId;
  }

  private RawMethod findMethodToInvoke(RawMethod rawMethod, Deque<Word> operandStack) {
    int receiverObjectId = peekReceiverObjectId(rawMethod, operandStack);
    RawClass receiverClass = heapManager.lookupObject(receiverObjectId).getRawClass();
    return methodAreaManager.selectMethod(receiverClass, rawMethod);
  }

  private void dump(String name, int pc, Instruction inst, RawThread thread) {
    int stackSize = thread.frames.size() - 1;
    System.out.printf(
        "%s[%s] stack#=%d, pc = %2d, inst = %x(%s), frame=%s%n",
        "  ".repeat(stackSize),
        name,
        stackSize,
        pc,
        inst.getOperandCode(),
        inst.name(),
        this.currentFrame());
  }
}
