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
import dev.ishikawa.lovejvm.rawobject.RawObject;
import dev.ishikawa.lovejvm.util.ByteUtil;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
   * init the thread by putting the initial frame. Note: initial frame must be a static method, and
   * static method doesn't have a receiver.
   */
  public RawThread init(RawMethod entryPoint) {
    stackUp(entryPoint, 0, entryPoint.getTransitWordSize(false));
    return this;
  }

  /** invoke a method forcefully. This is used for initializing Class, Field, Method object */
  public void invoke(RawMethod method, List<Word> arguments) {
    stackUp(method, 0, 0);

    var locals = new Word[method.getLocalsSize()];
    for (int i = 0; i < arguments.size(); i++) {
      locals[i] = arguments.get(i);
    }

    currentFrame().setLocals(locals);
    this.run();
  }

  /**
   * add a new frame. copy necessary locals from current frame to the new frame
   *
   * @param {int} pcToReturn next pc in current frame. not in the next frame. the caller of stackUp
   *     has to calculate pcToReturn beforehand.
   * @return true when stackUp actually adds a new frame
   */
  private boolean stackUp(RawMethod nextMethod, int pcToReturn, int numOfWordsToTransit) {
    pc = methodAreaManager.lookupCodeSectionAddress(nextMethod);

    if (pc < 0) {
      if (nextMethod.isAbstract()) {
        throw new RuntimeException(
            String.format(
                "abstract method is called. method: %s", nextMethod.getName().getLabel()));
      } else if (nextMethod.isNative()) {
        List<Word> result = RawSystem.nativeMethodHandler.handle(nextMethod, currentFrame());
        result.forEach(it -> currentFrame().getOperandStack().push(it));
        pc = pcToReturn;
        return false;
      }
    }

    Frame newFrame = new Frame(this, nextMethod);

    if (currentFrame() != null) {
      // NOTE: store previous PC on the bottom of operandStack
      // so that it can go back to previous PC when calling stackDown
      newFrame.getOperandStack().push(Word.of(pcToReturn));

      // REFACTOR: Ideally, Not copying the words,
      //  but use the unified global stack per one thread!!

      // load locals from currentFrame into the new frame
      var tmpQueue = new ArrayDeque<Word>(numOfWordsToTransit);
      for (int i = 0; i < numOfWordsToTransit; i++) {
        tmpQueue.add(currentFrame().getOperandStack().pop());
      }
      for (int i = 0; i < numOfWordsToTransit; i++) {
        var word = tmpQueue.pop();
        currentFrame().getOperandStack().push(word);
        newFrame.getLocals()[numOfWordsToTransit - i - 1] =
            word; // put each word from the last position
      }
    } else {
      // this pcToReturn won't be used because pcToReturn is for returning to the previous
      // frame
      newFrame.getOperandStack().push(Word.of(pcToReturn));

      // this is the entry point method.
      // REFACTOR: need to pass initial args passed from command line to this method.
      // main(String[] args) <- this args.
    }

    frames.push(newFrame);

    return true;
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
    // REFACTOR: refactor. most of these logics should be in a separate place or in LFrame
    while (true) {
      byte instruction = methodAreaManager.lookupByte(pc);
      dump(name, pc, instruction, this);

      Deque<Word> operandStack = currentFrame().getOperandStack();
      Word[] locals = currentFrame().getLocals();
      ConstantPool constantPool = currentFrame().getMethod().getKlass().getConstantPool();

      switch (instruction) {
        case (byte) 0x00: // nop
          {
            pc += 1;
            break;
          }
        case (byte) 0x01: // aconst_null
          {
            // push "null"(objectId = 0)
            operandStack.push(Word.of((byte) 0x00));
            pc = pc + 1;
            break;
          }
        case (byte) 0x02: // iconst_m1
          {
            operandStack.push(Word.of(-1));
            pc = pc + 1;
            break;
          }
        case (byte) 0x03: // iconst_0
          {
            operandStack.push(Word.of(0));
            pc = pc + 1;
            break;
          }
        case (byte) 0x04: // iconst_1
          {
            operandStack.push(Word.of(1));
            pc = pc + 1;
            break;
          }
        case (byte) 0x05: // iconst_2
          {
            operandStack.push(Word.of(2));
            pc = pc + 1;
            break;
          }
        case (byte) 0x06: // iconst_3
          {
            operandStack.push(Word.of(3));
            pc = pc + 1;
            break;
          }
        case (byte) 0x07: // iconst_4
          {
            operandStack.push(Word.of(4));
            pc = pc + 1;
            break;
          }
        case (byte) 0x08: // iconst_5
          {
            operandStack.push(Word.of(5));
            pc = pc + 1;
            break;
          }
        case (byte) 0x09: // lconst_0
          {
            Word.of(0L).forEach(operandStack::push);
            pc = pc + 1;
            break;
          }
        case (byte) 0x0a: // lconst_1
          {
            Word.of(1L).forEach(operandStack::push);
            pc = pc + 1;
            break;
          }
        case (byte) 0x0b: // fconst_0
          {
            operandStack.push(Word.of(0.0f));
            pc = pc + 1;
            break;
          }
        case (byte) 0x0c: // fconst_1
          {
            operandStack.push(Word.of(1.0f));
            pc = pc + 1;
            break;
          }
        case (byte) 0x0d: // fconst_2
          {
            operandStack.push(Word.of(2.0f));
            pc = pc + 1;
            break;
          }
        case (byte) 0x0e: // dconst_0
          {
            Word.of(0.0D).forEach(operandStack::push);
            pc = pc + 1;
            break;
          }
        case (byte) 0x0f: // dconst_1
          {
            Word.of(1.0D).forEach(operandStack::push);
            pc = pc + 1;
            break;
          }
        case (byte) 0x10: // bipush
          {
            /** push a byte onto the stack as an integer */
            operandStack.push(Word.of(methodAreaManager.lookupByte(pc + 1)));
            pc = pc + 2;
            break;
          }
        case (byte) 0x11: // sipush
          {
            /** push a short onto the stack as an integer */
            var word = Word.of(peekTwoBytes());
            operandStack.push(word);
            pc = pc + 3;
            break;
          }
        case (byte) 0x12: // ldc
          {
            /**
             * push a constant #index from a constant pool ( String, as objectId int, float, Class,
             * java.lang.invoke.MethodType, as ??? java.lang.invoke.MethodHandle, as ??? or a
             * dynamically-computed constant as ??? ) onto the stack
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
        case (byte) 0x13: // ldc_w
          {
            /**
             * ldc_w works in a basically same way as ldc. the difference is the size of entry
             * index. ldc = 1byte, ldc_w = 2bytes.
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
        case (byte) 0x14: // ldc2_w
          {
            /**
             * push a constant #index from a constant pool ( double, long, or a dynamically-computed
             * constant ) onto the stack
             */
            var index = peekTwoBytes();
            ConstantPoolLoadableEntry entry =
                (ConstantPoolLoadableEntry) constantPool.findByIndex(index);

            entry.resolve(constantPool);

            List<Word> words = entry.loadableValue();
            for (Word word : words) {
              operandStack.push(word);
            }

            pc = pc + 3;
            break;
          }
        case (byte) 0x15: // iload
        case (byte) 0x17: // fload
        case (byte) 0x19: // aload
          {
            var id = Byte.toUnsignedInt(methodAreaManager.lookupByte(pc + 1));
            operandStack.push(locals[id]);
            pc = pc + 2;
            break;
          }
        case (byte) 0x16: // lload
        case (byte) 0x18: // dload
          {
            var id = Byte.toUnsignedInt(methodAreaManager.lookupByte(pc + 1));
            operandStack.push(locals[id]);
            operandStack.push(locals[id + 1]);
            pc = pc + 2;
            break;
          }
        case (byte) 0x1a: // iload_0
          {
            operandStack.push(locals[0]);
            pc = pc + 1;
            break;
          }
        case (byte) 0x1b: // iload_1
          {
            operandStack.push(locals[1]);
            pc = pc + 1;
            break;
          }
        case (byte) 0x1c: // iload_2
          {
            operandStack.push(locals[2]);
            pc = pc + 1;
            break;
          }
        case (byte) 0x1d: // iload_3
          {
            operandStack.push(locals[3]);
            pc = pc + 1;
            break;
          }
        case (byte) 0x1e: // lload_0
          {
            operandStack.push(locals[0]);
            operandStack.push(locals[1]);
            pc = pc + 1;
            break;
          }
        case (byte) 0x1f: // lload_1
          {
            operandStack.push(locals[1]);
            operandStack.push(locals[2]);
            pc = pc + 1;
            break;
          }
        case (byte) 0x20: // lload_2
          {
            operandStack.push(locals[2]);
            operandStack.push(locals[3]);
            pc = pc + 1;
            break;
          }
        case (byte) 0x21: // lload_3
          {
            operandStack.push(locals[3]);
            operandStack.push(locals[4]);
            pc = pc + 1;
            break;
          }
        case (byte) 0x22: // fload_0
          {
            operandStack.push(locals[0]);
            pc = pc + 1;
            break;
          }
        case (byte) 0x23: // fload_1
          {
            operandStack.push(locals[1]);
            pc = pc + 1;
            break;
          }
        case (byte) 0x24: // fload_2
          {
            operandStack.push(locals[2]);
            pc = pc + 1;
            break;
          }
        case (byte) 0x25: // fload_3
          {
            operandStack.push(locals[3]);
            pc = pc + 1;
            break;
          }
        case (byte) 0x26: // dload_0
          {
            operandStack.push(locals[0]);
            operandStack.push(locals[1]);
            pc = pc + 1;
            break;
          }
        case (byte) 0x27: // dload_1
          {
            operandStack.push(locals[1]);
            operandStack.push(locals[2]);
            pc = pc + 1;
            break;
          }
        case (byte) 0x28: // dload_2
          {
            operandStack.push(locals[2]);
            operandStack.push(locals[3]);
            pc = pc + 1;
            break;
          }
        case (byte) 0x29: // dload_3
          {
            operandStack.push(locals[3]);
            operandStack.push(locals[4]);
            pc = pc + 1;
            break;
          }
        case (byte) 0x2a: // aload_0
          {
            operandStack.push(locals[0]);
            pc = pc + 1;
            break;
          }
        case (byte) 0x2b: // aload_1
          {
            operandStack.push(locals[1]);
            pc = pc + 1;
            break;
          }
        case (byte) 0x2c: // aload_2
          {
            operandStack.push(locals[2]);
            pc = pc + 1;
            break;
          }
        case (byte) 0x2d: // aload_3
          {
            operandStack.push(locals[3]);
            pc = pc + 1;
            break;
          }
        case (byte) 0x2e: // iaload
        case (byte) 0x2f: // laload
        case (byte) 0x30: // faload
        case (byte) 0x31: // faload
        case (byte) 0x32: // aaload
        case (byte) 0x33: // baload
        case (byte) 0x34: // caload
        case (byte) 0x35: // saload
          {
            var index = operandStack.pop().getValue();
            var arrayIndex = operandStack.pop().getValue();

            var arrayObject = heapManager.lookupObject(arrayIndex);
            var value = heapManager.getElement(arrayObject, index);

            value.forEach(operandStack::push);

            pc = pc + 1;
            break;
          }
        case (byte) 0x37: // lstore
        case (byte) 0x39: // dstore
          {
            var index = Byte.toUnsignedInt(methodAreaManager.lookupByte(pc + 1));
            locals[index] = operandStack.pop();
            locals[index + 1] = operandStack.pop();
            pc = pc + 2;
            break;
          }
        case (byte) 0x36: // istore
        case (byte) 0x38: // fstore
        case (byte) 0x3a: // astore
          {
            var index = Byte.toUnsignedInt(methodAreaManager.lookupByte(pc + 1));
            locals[index] = operandStack.pop();
            pc = pc + 2;
            break;
          }
        case (byte) 0x3b: // istore_0
          {
            locals[0] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case (byte) 0x3c: // istore_1
          {
            locals[1] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case (byte) 0x3d: // istore_2
          {
            locals[2] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case (byte) 0x3e: // istore_3
          {
            locals[3] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case (byte) 0x3f: // lstore_0
          {
            locals[0] = operandStack.pop();
            locals[1] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case (byte) 0x40: // lstore_1
          {
            locals[1] = operandStack.pop();
            locals[2] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case (byte) 0x41: // lstore_2
          {
            locals[2] = operandStack.pop();
            locals[3] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case (byte) 0x42: // lstore_3
          {
            locals[3] = operandStack.pop();
            locals[4] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case (byte) 0x43: // fstore_0
          {
            locals[0] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case (byte) 0x44: // fstore_1
          {
            locals[1] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case (byte) 0x45: // fstore_2
          {
            locals[2] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case (byte) 0x46: // fstore_3
          {
            locals[3] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case (byte) 0x47: // dstore_0
          {
            locals[0] = operandStack.pop();
            locals[1] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case (byte) 0x48: // dstore_1
          {
            locals[1] = operandStack.pop();
            locals[2] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case (byte) 0x49: // dstore_2
          {
            locals[2] = operandStack.pop();
            locals[3] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case (byte) 0x4a: // dstore_3
          {
            locals[3] = operandStack.pop();
            locals[4] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case (byte) 0x4b: // astore_0
          {
            locals[0] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case (byte) 0x4c: // astore_1
          {
            locals[1] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case (byte) 0x4d: // astore_2
          {
            locals[2] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case (byte) 0x4e: // astore_3
          {
            locals[3] = operandStack.pop();
            pc = pc + 1;
            break;
          }
        case (byte) 0x50: // lastore
        case (byte) 0x52: // dastore
          {
            var v2 = currentFrame().getOperandStack().pop();
            var v1 = currentFrame().getOperandStack().pop();
            List<Word> value = List.of(v1, v2);
            int position = currentFrame().getOperandStack().pop().getValue();
            int objectId = currentFrame().getOperandStack().pop().getValue();
            RawObject rawObject = heapManager.lookupObject(objectId);
            heapManager.setElement(rawObject, position, value);
            pc += 1;
            break;
          }
        case (byte) 0x4f: // iastore
        case (byte) 0x51: // fastore
        case (byte) 0x53: // aastore
        case (byte) 0x54: // bastore
        case (byte) 0x55: // castore
        case (byte) 0x56: // sastore
          {
            List<Word> value = List.of(currentFrame().getOperandStack().pop());
            int position = currentFrame().getOperandStack().pop().getValue();
            int objectId = currentFrame().getOperandStack().pop().getValue();
            RawObject rawObject = heapManager.lookupObject(objectId);
            heapManager.setElement(rawObject, position, value);
            pc += 1;
            break;
          }
        case (byte) 0x57: // pop
          {
            currentFrame().getOperandStack().pop();
            pc += 1;
            break;
          }
        case (byte) 0x58: // pop2
          {
            currentFrame().getOperandStack().pop();
            currentFrame().getOperandStack().pop();
            pc += 1;
            break;
          }
        case (byte) 0x59: // dup
          {
            // [v1] -> [copy(v1), v1]
            Word v1 = currentFrame().getOperandStack().pop();
            currentFrame().getOperandStack().push(Word.of(v1));
            currentFrame().getOperandStack().push(v1);
            pc += 1;
            break;
          }
        case (byte) 0x5a: // dup_x1
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
        case (byte) 0x5b: // dup_x2
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
        case (byte) 0x5c: // dup2
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
        case (byte) 0x5d: // dup2_x1
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
        case (byte) 0x5e: // dup2_x2
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
        case (byte) 0x5f: // swap
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
        case (byte) 0x60: // iadd
          {
            int a = operandStack.pop().getValue();
            int b = operandStack.pop().getValue();
            operandStack.push(Word.of(b + a));
            pc = pc + 1;
            break;
          }
        case (byte) 0x61: // ladd
          {
            // [v1, v2, v3, v4] -> a = Long(v1, v2), b = Long(v3, v4)
            int v4 = operandStack.pop().getValue();
            int v3 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            long a = ByteUtil.concatToLong(v1, v2);
            long b = ByteUtil.concatToLong(v3, v4);
            Word.of(b + a).forEach(operandStack::push);
            pc = pc + 1;
            break;
          }
        case (byte) 0x62: // fadd
          {
            float a = ByteUtil.convertToFloat(operandStack.pop().getValue());
            float b = ByteUtil.convertToFloat(operandStack.pop().getValue());
            operandStack.push(Word.of(b + a));
            pc = pc + 1;
            break;
          }
        case (byte) 0x63: // dadd
          {
            int v4 = operandStack.pop().getValue();
            int v3 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            double a = ByteUtil.concatToDouble(v1, v2);
            double b = ByteUtil.concatToDouble(v3, v4);
            Word.of(b + a).forEach(operandStack::push);
            pc = pc + 1;
            break;
          }
        case (byte) 0x64: // isub
          {
            int a = operandStack.pop().getValue();
            int b = operandStack.pop().getValue();
            operandStack.push(Word.of(b - a));
            pc = pc + 1;
            break;
          }
        case (byte) 0x65: // lsub
          {
            int v4 = operandStack.pop().getValue();
            int v3 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            long a = ByteUtil.concatToLong(v1, v2);
            long b = ByteUtil.concatToLong(v3, v4);
            Word.of(b - a).forEach(operandStack::push);
            pc = pc + 1;
            break;
          }
        case (byte) 0x66: // fsub
          {
            float a = ByteUtil.convertToFloat(operandStack.pop().getValue());
            float b = ByteUtil.convertToFloat(operandStack.pop().getValue());
            operandStack.push(Word.of(b - a));
            pc = pc + 1;
            break;
          }
        case (byte) 0x67: // dsub
          {
            int v4 = operandStack.pop().getValue();
            int v3 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            double a = ByteUtil.concatToDouble(v1, v2);
            double b = ByteUtil.concatToDouble(v3, v4);
            Word.of(b - a).forEach(operandStack::push);
            pc = pc + 1;
            break;
          }
        case (byte) 0x68: // imul
          {
            int a = operandStack.pop().getValue();
            int b = operandStack.pop().getValue();
            operandStack.push(Word.of(b * a));
            pc = pc + 1;
            break;
          }
        case (byte) 0x69: // lmul
          {
            // [v1, v2, v3, v4] -> a = Long(v1, v2), b = Long(v3, v4)
            int v4 = operandStack.pop().getValue();
            int v3 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            long a = ByteUtil.concatToLong(v1, v2);
            long b = ByteUtil.concatToLong(v3, v4);
            Word.of(b * a).forEach(operandStack::push);
            pc = pc + 1;
            break;
          }
        case (byte) 0x6a: // fmul
          {
            float a = ByteUtil.convertToFloat(operandStack.pop().getValue());
            float b = ByteUtil.convertToFloat(operandStack.pop().getValue());
            operandStack.push(Word.of(b * a));
            pc = pc + 1;
            break;
          }
        case (byte) 0x6b: // dmul
          {
            // [v1, v2, v3, v4] -> a = Double(v1, v2), b = Double(v3, v4)
            int v4 = operandStack.pop().getValue();
            int v3 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            double a = ByteUtil.concatToDouble(v1, v2);
            double b = ByteUtil.concatToDouble(v3, v4);
            long result = Double.doubleToLongBits(b * a);
            Word.of(result).forEach(operandStack::push);
            pc = pc + 1;
            break;
          }
        case (byte) 0x6c: // idiv
          {
            int a = operandStack.pop().getValue();
            int b = operandStack.pop().getValue();
            operandStack.push(Word.of(b / a));
            pc = pc + 1;
            break;
          }
        case (byte) 0x6d: // ldiv
          {
            int v4 = operandStack.pop().getValue();
            int v3 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            long a = ByteUtil.concatToLong(v1, v2);
            long b = ByteUtil.concatToLong(v3, v4);
            Word.of(b / a).forEach(operandStack::push);
            pc = pc + 1;
            break;
          }
        case (byte) 0x6e: // fdiv
          {
            float a = ByteUtil.convertToFloat(operandStack.pop().getValue());
            float b = ByteUtil.convertToFloat(operandStack.pop().getValue());
            operandStack.push(Word.of(b / a));
            pc = pc + 1;
            break;
          }
        case (byte) 0x6f: // ddiv
          {
            int v4 = operandStack.pop().getValue();
            int v3 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            double a = ByteUtil.concatToDouble(v1, v2);
            double b = ByteUtil.concatToDouble(v3, v4);
            long result = Double.doubleToLongBits(b / a);
            Word.of(result).forEach(operandStack::push);
            pc = pc + 1;
            break;
          }
        case (byte) 0x70: // irem
          {
            int a = operandStack.pop().getValue();
            int b = operandStack.pop().getValue();
            operandStack.push(Word.of(b % a));
            pc = pc + 1;
            break;
          }
        case (byte) 0x71: // lrem
          {
            int v4 = operandStack.pop().getValue();
            int v3 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            long a = ByteUtil.concatToLong(v1, v2);
            long b = ByteUtil.concatToLong(v3, v4);
            Word.of(b % a).forEach(operandStack::push);
            pc = pc + 1;
            break;
          }
        case (byte) 0x72: // frem
          {
            float a = ByteUtil.convertToFloat(operandStack.pop().getValue());
            float b = ByteUtil.convertToFloat(operandStack.pop().getValue());
            operandStack.push(Word.of(b % a));
            pc = pc + 1;
            break;
          }
        case (byte) 0x73: // drem
          {
            int v4 = operandStack.pop().getValue();
            int v3 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            double a = ByteUtil.concatToDouble(v1, v2);
            double b = ByteUtil.concatToDouble(v3, v4);
            Word.of(b % a).forEach(operandStack::push);
            pc = pc + 1;
            break;
          }
        case (byte) 0x74: // ineg
          {
            int a = operandStack.pop().getValue();
            operandStack.push(Word.of(-1 * a));
            pc = pc + 1;
            break;
          }
        case (byte) 0x75: // lneg
          {
            int v2 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            long a = ByteUtil.concatToLong(v1, v2);
            Word.of(-1 * a).forEach(operandStack::push);
            pc = pc + 1;
            break;
          }
        case (byte) 0x76: // fneg
          {
            float a = ByteUtil.convertToFloat(operandStack.pop().getValue());
            operandStack.push(Word.of(-1 * a));
            pc = pc + 1;
            break;
          }
        case (byte) 0x77: // dneg
          {
            int v2 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            double a = ByteUtil.concatToDouble(v1, v2);
            Word.of(-1 * a).forEach(operandStack::push);
            pc = pc + 1;
            break;
          }
        case (byte) 0x78: // ishl ... int shift left
          {
            int a = operandStack.pop().getValue();
            int b = operandStack.pop().getValue();
            operandStack.push(Word.of(b << a));
            pc = pc + 1;
            break;
          }
        case (byte) 0x79: // lshl ... long shift left
          {
            // [v1, v2, v3] -> a = Long(v1, v2), b = Int(v3)
            int a = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            long b = ByteUtil.concatToLong(v1, v2);
            Word.of(b << a).forEach(operandStack::push);
            pc = pc + 1;
            break;
          }
        case (byte) 0x7a: // ishr ... int shift right
          {
            int a = operandStack.pop().getValue();
            int b = operandStack.pop().getValue();
            operandStack.push(Word.of(b >> a));
            pc = pc + 1;
            break;
          }
        case (byte) 0x7b: // lshr ... long shift right
          {
            // [v1, v2, v3] -> a = Long(v1, v2), b = Int(v3)
            int b = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            long a = ByteUtil.concatToLong(v1, v2);
            operandStack.push(Word.of(b >> a));
            pc = pc + 1;
            break;
          }
        case (byte) 0x7c: // iushr ... int logical shift right
          {
            int a = operandStack.pop().getValue();
            int b = operandStack.pop().getValue();
            operandStack.push(Word.of(b >>> a));
            pc = pc + 1;
            break;
          }
        case (byte) 0x7d: // lushr ... long logical shift right
          {
            // [v1, v2, v3] -> a = Long(v1, v2), b = Int(v3)
            int b = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            long a = ByteUtil.concatToLong(v1, v2);
            operandStack.push(Word.of(b >>> a));
            pc = pc + 1;
            break;
          }
        case (byte) 0x7e: // iand
          {
            int a = operandStack.pop().getValue();
            int b = operandStack.pop().getValue();
            operandStack.push(Word.of(b & a));
            pc = pc + 1;
            break;
          }
        case (byte) 0x7f: // land
          {
            int v4 = operandStack.pop().getValue();
            int v3 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            long a = ByteUtil.concatToLong(v1, v2);
            long b = ByteUtil.concatToLong(v3, v4);
            Word.of(b & a).forEach(operandStack::push);
            pc = pc + 1;
            break;
          }
        case (byte) 0x80: // ior
          {
            int a = operandStack.pop().getValue();
            int b = operandStack.pop().getValue();
            operandStack.push(Word.of(b | a));
            pc = pc + 1;
            break;
          }
        case (byte) 0x81: // lor
          {
            int v4 = operandStack.pop().getValue();
            int v3 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            long a = ByteUtil.concatToLong(v1, v2);
            long b = ByteUtil.concatToLong(v3, v4);
            Word.of(b | a).forEach(operandStack::push);
            pc = pc + 1;
            break;
          }
        case (byte) 0x82: // ixor
          {
            int a = operandStack.pop().getValue();
            int b = operandStack.pop().getValue();
            operandStack.push(Word.of(b ^ a));
            pc = pc + 1;
            break;
          }
        case (byte) 0x83: // lxor
          {
            int v4 = operandStack.pop().getValue();
            int v3 = operandStack.pop().getValue();
            int v2 = operandStack.pop().getValue();
            int v1 = operandStack.pop().getValue();
            long a = ByteUtil.concatToLong(v1, v2);
            long b = ByteUtil.concatToLong(v3, v4);
            Word.of(b ^ a).forEach(operandStack::push);
            pc = pc + 1;
            break;
          }
        case (byte) 0x84: // iinc
          {
            int index = Byte.toUnsignedInt(methodAreaManager.lookupByte(pc + 1));
            byte incVal = methodAreaManager.lookupByte(pc + 2);
            locals[index] = Word.of(locals[index].getValue() + incVal);
            pc = pc + 3;
            break;
          }
        case (byte) 0x85: // i2l
          {
            // convert int to long
            var a = operandStack.pop().getValue();
            Word.of((long) a).forEach(operandStack::push);
            pc += 1;
            break;
          }
        case (byte) 0x86: // i2f
          {
            // 10 -> 10.0f
            var a = operandStack.pop().getValue();
            operandStack.push(Word.of((float) a));
            pc += 1;
            break;
          }
        case (byte) 0x87: // i2d
          {
            // 10 -> 10.0D
            var a = operandStack.pop().getValue();
            Word.of((double) a).forEach(operandStack::push);
            pc += 1;
            break;
          }
        case (byte) 0x88: // l2i
          {
            // truncate long to int
            var v2 = operandStack.pop().getValue();
            var _v1 = operandStack.pop().getValue();
            Word word = Word.of(v2);
            operandStack.push(word);
            pc += 1;
            break;
          }
        case (byte) 0x89: // l2f
          {
            // truncate long to int
            var v2 = operandStack.pop().getValue();
            var v1 = operandStack.pop().getValue();
            var a = ByteUtil.concatToLong(v1, v2);
            // TODO: now borrowing host language's functionality. use original method.
            Word word = Word.of((float) a);
            operandStack.push(word);
            pc += 1;
            break;
          }
        case (byte) 0x8a: // l2d
          {
            // truncate long to int
            var v2 = operandStack.pop().getValue();
            var v1 = operandStack.pop().getValue();
            var a = ByteUtil.concatToLong(v1, v2);
            // TODO: now borrowing host language's functionality. use original method.
            List<Word> words = Word.of((double) a);
            words.forEach(operandStack::push);
            pc += 1;
            break;
          }
        case (byte) 0x8b: // f2i
          {
            // 123.45 -> 123
            var a = ByteUtil.convertToFloat(operandStack.pop().getValue());
            operandStack.push(Word.of((int) a));
            pc += 1;
            break;
          }
        case (byte) 0x8c: // f2l
          {
            // 123.45 -> 123
            var a = ByteUtil.convertToFloat(operandStack.pop().getValue());
            Word.of((long) a).forEach(operandStack::push);
            pc += 1;
            break;
          }
        case (byte) 0x8d: // f2d
          {
            // 123.45 -> 123
            var a = ByteUtil.convertToFloat(operandStack.pop().getValue());
            Word.of((double) a).forEach(operandStack::push);
            pc += 1;
            break;
          }
        case (byte) 0x8e: // d2i
          {
            // 123.45 -> 123
            var v2 = operandStack.pop().getValue();
            var v1 = operandStack.pop().getValue();
            var a = ByteUtil.concatToDouble(v1, v2);
            operandStack.push(Word.of((int) a));
            pc += 1;
            break;
          }
        case (byte) 0x8f: // d2l
          {
            // 123.45 -> 123
            var v2 = operandStack.pop().getValue();
            var v1 = operandStack.pop().getValue();
            var a = ByteUtil.concatToDouble(v1, v2);
            Word.of((long) a).forEach(operandStack::push);
            pc += 1;
            break;
          }
        case (byte) 0x90: // d2f
          {
            var v2 = operandStack.pop().getValue();
            var v1 = operandStack.pop().getValue();
            var a = ByteUtil.concatToDouble(v1, v2);
            operandStack.push(Word.of((float) a));
            pc += 1;
            break;
          }
        case (byte) 0x91: // i2b
          {
            var a = operandStack.pop().getValue();
            operandStack.push(Word.of((byte) a));
            pc += 1;
            break;
          }
        case (byte) 0x92: // i2c
          {
            // truncate int into char
            Word word = Word.of(((byte) operandStack.pop().getValue()));
            operandStack.push(word);
            pc += 1;
            break;
          }
        case (byte) 0x93: // i2s
          {
            // truncate int into char
            Word word = Word.of(((short) operandStack.pop().getValue()));
            operandStack.push(word);
            pc += 1;
            break;
          }
        case (byte) 0x94: // lcmp
          {
            // if(left == right) push(0)
            // if(left > right>  push(1)
            // if(left < right>  push(-1)
            var v4 = operandStack.pop().getValue();
            var v3 = operandStack.pop().getValue();
            var v2 = operandStack.pop().getValue();
            var v1 = operandStack.pop().getValue();
            long a = ByteUtil.concatToLong(v1, v2);
            long b = ByteUtil.concatToLong(v3, v4);
            Word word = Word.of(Long.compare(a, b));
            operandStack.push(word);
            pc += 1;
            break;
          }
        case (byte) 0x99: // ifnq
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
        case (byte) 0x9a: // ifne
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
        case (byte) 0x9b: // iflt
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
        case (byte) 0x9c: // ifge
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
        case (byte) 0x9d: // ifgt
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
        case (byte) 0x9e: // ifle
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
        case (byte) 0xa0: // if_icmpne
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
        case (byte) 0xa1: // if_icmplt
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
        case (byte) 0xa2: // if_icmpge
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
        case (byte) 0xa3: // if_icmpgt
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
        case (byte) 0xa5: // if_acmpeq
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
        case (byte) 0xa6: // if_acmpne
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
        case (byte) 0xa7: // goto
          {
            int offset = peekTwoBytes();
            pc += offset;

            break;
          }
        case (byte) 0xac: // ireturn
        case (byte) 0xae: // freturn
        case (byte) 0xb0: // areturn
          {
            // ireturn/freturn/areturn/dreturn returns one word
            // these operand codes must be able to stackDown
            var word = currentFrame().getOperandStack().pop();
            previousFrame().getOperandStack().push(word);
            stackDown();
            break;
          }
        case (byte) 0xaf: // dreturn
          {
            // dreturn returns two words
            // these operand codes must be able to stackDown
            var v1 = currentFrame().getOperandStack().pop();
            var v2 = currentFrame().getOperandStack().pop();
            previousFrame().getOperandStack().push(v1);
            previousFrame().getOperandStack().push(v2);
            stackDown();
            break;
          }
        case (byte) 0xb1: // return
          {
            if (canStackDown()) {
              stackDown();
              break;
            }
            // this thread is finished
            return;
          }
        case (byte) 0xb2: // getstatic
          {
            var index = peekTwoBytes();
            ConstantFieldref fieldRef = (ConstantFieldref) constantPool.findByIndex(index);

            fieldRef.resolve(constantPool);
            RawClass rawClass = methodAreaManager.lookupClass(fieldRef.getConstantClassRef());
            RawSystem.classLinker.link(rawClass);
            RawSystem.classInitializer.initialize(rawClass);

            RawField rawField = getRawStaticField(fieldRef);

            List<Word> staticFieldValue = methodAreaManager.getStaticFieldValue(rawClass, rawField);
            staticFieldValue.forEach(operandStack::push);

            pc = pc + 3;
            break;
          }
        case (byte) 0xb3: // putstatic
          {
            var index = peekTwoBytes();
            ConstantFieldref fieldRef = (ConstantFieldref) constantPool.findByIndex(index);

            fieldRef.resolve(constantPool);
            RawClass rawClass = methodAreaManager.lookupClass(fieldRef.getConstantClassRef());
            RawSystem.classLinker.link(rawClass);
            RawSystem.classInitializer.initialize(rawClass);

            RawField rawField = getRawStaticField(fieldRef);

            JvmType jvmTypeName =
                JvmType.findByJvmSignature(fieldRef.getNameAndType().getDescriptor().getLabel());
            List<Word> words = retrieveData(jvmTypeName, operandStack);

            methodAreaManager.putStaticFieldValue(rawClass, rawField, words);

            pc = pc + 3;
            break;
          }
        case (byte) 0xb4: // getfield
          {
            /*
             * > objectref, value →
             * > get a field value of an object objectref,
             * */
            var index = peekTwoBytes();
            ConstantFieldref fieldRef = (ConstantFieldref) constantPool.findByIndex(index);

            int objectId = operandStack.pop().getValue();
            RawObject object = heapManager.lookupObject(objectId);
            RawField rawField = getRawMemberField(fieldRef);

            // REFACTOR: validate if the value is suitable for the field
            var words = heapManager.getValue(object, rawField);
            for (Word word : words) {
              operandStack.push(word);
            }

            pc += 3;
            break;
          }
        case (byte) 0xb5: // putfield
          {
            /*
             * > objectref, value →
             * > set field to value in an object objectref,
             * > where the field is identified by a field reference index in constant pool
             *
             * ref: JAVA virtual machine
             * it depends on the descriptor how many words to pop here
             * */
            var index = peekTwoBytes();
            ConstantFieldref fieldRef = (ConstantFieldref) constantPool.findByIndex(index);

            // retrieve value to set
            JvmType jvmTypeName =
                JvmType.findByJvmSignature(fieldRef.getNameAndType().getDescriptor().getLabel());
            List<Word> words = retrieveData(jvmTypeName, operandStack);

            // retrieve the object's address
            int objectId = operandStack.pop().getValue();
            var object = RawSystem.heapManager.lookupObject(objectId);

            RawField rawField = getRawMemberField(fieldRef);

            // set the value into field's address
            // REFACTOR: validate if the value is suitable for the field
            RawSystem.heapManager.setValue(object, rawField, words);

            pc += 3;
            break;
          }
        case (byte) 0xb6: // invokevirtual
        case (byte) 0xb7: // invokespecial
        case (byte) 0xb8: // invokestatic
          {
            // REFACTOR
            var index = peekTwoBytes();
            ConstantMethodref methodRef = (ConstantMethodref) constantPool.findByIndex(index);

            methodRef.resolve(constantPool);
            RawMethod rawMethod = getRawMethod(methodRef);

            boolean hasReceiver =
                (instruction == (byte) 0xb6
                    || instruction == (byte) 0xb7
                    || instruction == (byte) 0xb9);
            int numOfWordsToTransit = rawMethod.getTransitWordSize(hasReceiver);
            var nextPc = pc + 3;
            var hasAddedFrame = this.stackUp(rawMethod, nextPc, numOfWordsToTransit);

            if (hasAddedFrame) {
              // Since arguments are copied to the new frame in stackUp step,
              // arguments pushed into current frame should be removed here.
              for (int i = 0; i < numOfWordsToTransit; i++) {
                previousFrame().getOperandStack().pop();
              }
            }

            // no need to update pc. pc is modified in stackUp
            break;
          }

        case (byte) 0xb9: // invokeinterface
          {
            var index = peekTwoBytes();
            var count = methodAreaManager.lookupByte(pc + 3);
            var _no_used1 = methodAreaManager.lookupByte(pc + 4);

            ConstantInterfaceMethodref methodRef =
                (ConstantInterfaceMethodref) constantPool.findByIndex(index);

            methodRef.resolve(constantPool);
            RawMethod rawMethod = getRawMethod(methodRef);

            boolean hasReceiver = false;
            int numOfWordsToTransit = rawMethod.getTransitWordSize(hasReceiver);
            var nextPc = pc + 3;
            var hasAddedFrame = this.stackUp(rawMethod, nextPc, numOfWordsToTransit);

            if (hasAddedFrame) {
              // Since arguments are copied to the new frame in stackUp step,
              // arguments pushed into current frame should be removed here.
              for (int i = 0; i < numOfWordsToTransit; i++) {
                previousFrame().getOperandStack().pop();
              }
            }

            break;
          }

        case (byte) 0xba: // invokedynamic
          {
            // TODO: implementation
            var index = peekTwoBytes();
            var _no_used1 = methodAreaManager.lookupByte(pc + 3);
            var _no_used2 = methodAreaManager.lookupByte(pc + 4);

            //            ConstantInvokeDynamic invokeDynamic =
            //                (ConstantInvokeDynamic) constantPool.findByIndex(index);
            //
            //            invokeDynamic.resolve(constantPool);
            //
            //            var nextPc = pc + 5;
            //
            throw new RuntimeException("invokedynamic is not implemented yet");
            //            break;
          }
        case (byte) 0xbb: // new
          {
            /** new just allocates the necessary mem → objectref(=objectId) */
            var index = peekTwoBytes();
            ConstantClass classRef = (ConstantClass) constantPool.findByIndex(index);
            classRef.resolve(constantPool);

            RawClass rawClass = methodAreaManager.lookupClass(classRef);

            int objectId = RawSystem.heapManager.register(rawClass);

            operandStack.push(Word.of(objectId));

            pc += 3;

            break;
          }
        case (byte) 0xbc: // newarray
          {
            /**
             * https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-6.html#jvms-6.5.newarray
             * newarray just allocates the necessary mem → objectref(=objectId)
             */
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

            // TODO: no need to store element type info?
            //          var index =
            //              ByteUtil.concat(
            //                  methodAreaManager.lookupByte(pc + 1),

            // TODO: registerArray(ArrayRawClass)にする
            int arrSize = operandStack.pop().getValue();
            int objectId = RawSystem.heapManager.registerArray(arrType, arrSize);

            operandStack.push(Word.of(objectId));

            pc += 2;

            break;
          }
        case (byte) 0xbd: // anewarray
          {
            /** anewarray just allocates the necessary mem → objectref(=objectId) */

            // TODO: no need to store element type info?
            //          var index =
            //              ByteUtil.concat(
            //                  methodAreaManager.lookupByte(pc + 1),
            // methodAreaManager.lookupByte(pc + 2));
            //          ConstantClass classRef =
            //              (ConstantClass)
            //
            // currentFrame().getMethod().getKlass().getConstantPool().findByIndex(index);
            //          RawClass elementRawClass =
            // methodAreaManager.lookupOrLoadClass(classRef.getName().getLabel());
            int arrSize = operandStack.pop().getValue();
            int objectId = RawSystem.heapManager.registerArray(JvmType.OBJECT_REFERENCE, arrSize);

            operandStack.push(Word.of(objectId));

            pc += 3;

            break;
          }
        case (byte) 0xbe: // arraylength
          {
            int objectId = operandStack.pop().getValue();
            var word = Word.of(heapManager.lookupObject(objectId).getArrSize());

            operandStack.push(word);
            pc += 1;
            break;
          }
        case (byte) 0xbf: // athrow
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
        case (byte) 0xc0: // checkcast
          {
            int index = peekTwoBytes();
            ConstantClass classRef = (ConstantClass) constantPool.findByIndex(index);
            classRef.resolve(constantPool);
            RawObject rawObject = heapManager.lookupObject(classRef.getObjectId());
            // TODO: Class objectからrawClassを引けるようにする...

            int objectId = operandStack.pop().getValue();
            if (objectId == JvmType.NULL) {
              throw new RuntimeException("ClassCastException");
            }
            RawClass rawClass = heapManager.lookupObject(objectId).getRawClass();

            // TODO:
            boolean isInstanceOf =
                classRef
                    .getName()
                    .getLabel()
                    .equals(Objects.requireNonNull(rawClass).getBinaryName());
            if (!isInstanceOf) {
              throw new RuntimeException("ClassCastException");
            }

            operandStack.push(Word.of(objectId));

            pc += 3;
            break;
          }
        case (byte) 0xc1: // instanceof
          {
            int index = peekTwoBytes();
            ConstantClass classRef = (ConstantClass) constantPool.findByIndex(index);
            classRef.resolve(constantPool);
            RawObject rawObject = heapManager.lookupObject(classRef.getObjectId());
            // TODO: Class objectからrawClassを引けるようにする...

            int objectId = operandStack.pop().getValue();
            RawClass rawClass = heapManager.lookupObject(objectId).getRawClass();

            // TODO:
            boolean isInstanceOf =
                classRef
                    .getName()
                    .getLabel()
                    .equals(Objects.requireNonNull(rawClass).getBinaryName());

            operandStack.push(Word.of(isInstanceOf));

            pc += 3;
            break;
          }
        case (byte) 0xc2: // monitorenter
          {
            int objectId = operandStack.pop().getValue();
            // TODO: what to do with this objectId
            pc += 1;
            break;
          }
        case (byte) 0xc3: // monitorexit
          {
            int objectId = operandStack.pop().getValue();
            // TODO: what to do with this objectId
            pc += 1;
            break;
          }
        case (byte) 0xc6: // ifnull
          {
            // if value is not null, jump to jumpTo
            int right = operandStack.pop().getValue();
            int jumpTo = peekTwoBytes();

            if (right == JvmType.NULL) {
              pc = pc + jumpTo;
            } else {
              pc = pc + 3;
            }
            break;
          }
        case (byte) 0xc7: // ifnonnull
          {
            // if value is not null, jump to jumpTo
            int right = operandStack.pop().getValue();
            int jumpTo = peekTwoBytes();

            if (right != JvmType.NULL) {
              pc = pc + jumpTo;
            } else {
              pc = pc + 3;
            }
            break;
          }
        case (byte) 0xca: // breakpoint
        case (byte) 0xfe: // impdep1
        case (byte) 0xff: // impdep2
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
        default:
          throw new RuntimeException(String.format("unrecognized instruction %x", instruction));
      }
    }
  }

  private RawMethod getRawMethod(ConstantMethodref methodRef) {
    return methodAreaManager.lookupAllMethod(
        methodRef.getConstantClassRef().getName().getLabel(),
        methodRef.getNameAndType().getName().getLabel(),
        methodRef.getNameAndType().getDescriptor().getLabel());
  }

  private RawField getRawMemberField(ConstantFieldref fieldRef) {
    return methodAreaManager.lookupMemberField(
        fieldRef.getConstantClassRef().getName().getLabel(),
        fieldRef.getNameAndType().getName().getLabel());
  }

  private RawField getRawStaticField(ConstantFieldref fieldRef) {
    return methodAreaManager.lookupStaticField(
        fieldRef.getConstantClassRef().getName().getLabel(),
        fieldRef.getNameAndType().getName().getLabel());
  }

  private int peekTwoBytes() {
    return ByteUtil.concatToShort(
        methodAreaManager.lookupByte(pc + 1), methodAreaManager.lookupByte(pc + 2));
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
                      methodAreaManager.lookupCodeSectionRelativeAddress(
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

  private void dump(String name, int pc, byte inst, RawThread thread) {
    int stackSize = thread.frames.size() - 1;
    System.out.printf(
        "%s[%s] stack#=%d, pc = %2d, inst = %x(%s), frame=%s%n",
        "  ".repeat(stackSize),
        name,
        stackSize,
        pc,
        inst,
        Instruction.findBy(inst).name(),
        this.currentFrame());
  }
}
