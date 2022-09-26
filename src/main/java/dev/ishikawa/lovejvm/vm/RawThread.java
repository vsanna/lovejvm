package dev.ishikawa.lovejvm.vm;

import static dev.ishikawa.lovejvm.vm.RawSystem.heapManager;
import static dev.ishikawa.lovejvm.vm.RawSystem.methodAreaManager;

import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantDouble;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantFieldref;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantFloat;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantInteger;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantLong;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodref;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantPoolEntry;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantString;
import dev.ishikawa.lovejvm.rawclass.field.RawField;
import dev.ishikawa.lovejvm.rawclass.method.RawMethod;
import dev.ishikawa.lovejvm.rawclass.type.JvmType;
import dev.ishikawa.lovejvm.rawobject.RawObject;
import dev.ishikawa.lovejvm.util.ByteUtil;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

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
    stackUp(entryPoint, 0);
    return this;
  }

  /**
   * add a new frame. copy necessary locals from current frame to the new frame
   *
   * @param {int} pcToReturn next pc in current frame. not in the next frame. the caller of stackUp
   *     has to calculate pcToReturn beforehand.
   */
  private void stackUp(RawMethod nextMethod, int pcToReturn) {
    pc = methodAreaManager.lookupCodeSectionAddress(nextMethod);
    Frame newFrame = new Frame(this, nextMethod);

    if (currentFrame() != null) {
      // NOTE: store previous PC on the bottom of operandStack
      // so that it can go back to previous PC when calling stackDown
      newFrame.getOperandStack().push(Word.of(pcToReturn));

      // REFACTOR: Ideally, Not copying the words,
      //  but use the unified global stack per one thread!!

      // load locals from currentFrame into the new frame
      var localsSize = nextMethod.getLocalsSize();
//      var receiverSize = hasReceiver ? 1 : 0;
//      var tmpQueueSize = localsSize + receiverSize;

      var tmpQueue = new ArrayDeque<Word>(localsSize);
      for (int i = 0; i < localsSize; i++) {
        tmpQueue.add(currentFrame().getOperandStack().pop());
      }
      for (int i = 0; i < localsSize; i++) {
        var word = tmpQueue.pop();
        currentFrame().getOperandStack().push(word);
        newFrame.getLocals()[localsSize - i - 1] = word; // put each word from the last position
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
  }

  private void stackDown() {
    // set next line of previousFrame's pc.
    pc = currentFrame().getOperandStack().getFirst().getValue();
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

      switch (instruction) {
        case (byte) 0x00: // nop
          {
            pc += 1;
            break;
          }
        case (byte) 0x01: // aconst_null
        {
          // TODO: To consider objectId/address == 0 as null
          //   heap/methodares should start with non zero address

          // push "null"
          operandStack.push(Word.of((byte) 0x00));
          pc = pc + 1;
          break;
        }
        case (byte) 0x02: // iconst_m1
        {
          operandStack.push(Word.of((byte) 0xff));
          pc = pc + 1;
          break;
        }
        case (byte) 0x03: // iconst_0
          {
            operandStack.push(Word.of((byte) 0x00));
            pc = pc + 1;
            break;
          }
        case (byte) 0x04: // iconst_1
          {
            operandStack.push(Word.of((byte) 0x01));
            pc = pc + 1;
            break;
          }
        case (byte) 0x05: // iconst_2
          {
            operandStack.push(Word.of((byte) 0x02));
            pc = pc + 1;
            break;
          }
        case (byte) 0x06: // iconst_2
          {
            operandStack.push(Word.of((byte) 0x03));
            pc = pc + 1;
            break;
          }
        case (byte) 0x07: // iconst_4
          {
            operandStack.push(Word.of((byte) 0x04));
            pc = pc + 1;
            break;
          }
        case (byte) 0x10: // bipush
          {
            operandStack.push(Word.of(methodAreaManager.lookupByte(pc + 1)));
            pc = pc + 2;
            break;
          }
        case (byte) 0x11: // sipush
          {
            // TODO
            operandStack.push(
                Word.of(
                    methodAreaManager.lookupByte(pc + 1), methodAreaManager.lookupByte(pc + 2)));
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
            var index = methodAreaManager.lookupByte(pc + 1);
            ConstantPoolEntry entry =
                currentFrame().getMethod().getKlass().getConstantPool().findByIndex(index);

            // integer, float literal or resolved String(= String objectのaddress)
            Word word;
            if (entry instanceof ConstantInteger) {
              word = Word.of(((ConstantInteger) entry).getIntValue());
            } else if (entry instanceof ConstantFloat) {
              word = Word.of(((ConstantFloat) entry).getIntBits());
            } else if (entry instanceof ConstantString) {
              int objectRef =
                  RawSystem.stringPool.getOrCreate(((ConstantString) entry).getLabel().getLabel());
              word = Word.of(objectRef);
            } else {
              throw new RuntimeException("invalid entry is specifeid in ldc operation");
            }

            operandStack.push(word);

            pc = pc + 2;
            break;
          }
        case (byte) 0x14: // ldc2_w
          {
            /**
             * push a constant #index from a constant pool ( double, long, or a dynamically-computed
             * constant ) onto the stack
             */
            var index =
                ByteUtil.concat(
                    methodAreaManager.lookupByte(pc + 1), methodAreaManager.lookupByte(pc + 2));
            ConstantPoolEntry entry =
                currentFrame().getMethod().getKlass().getConstantPool().findByIndex(index);

            // integer, float literal or resolved String(= String objectのaddress)
            List<Word> words;
            if (entry instanceof ConstantLong) {
              words = Word.of(((ConstantLong) entry).getLongValue());
            } else if (entry instanceof ConstantDouble) {
              words = Word.of(Double.doubleToLongBits(((ConstantDouble) entry).getDoubleValue()));
            } else {
              throw new RuntimeException("invalid entry is specifeid in ldc operation");
            }

            for (Word word : words) {
              operandStack.push(word);
            }

            pc = pc + 3;
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
            Word word = currentFrame().getOperandStack().pop();
            currentFrame().getOperandStack().push(word);
            currentFrame().getOperandStack().push(Word.of(word));
            pc += 1;
            break;
          }
        case (byte) 0x60: // iadd
          {
            int a = operandStack.pop().getValue();
            int b = operandStack.pop().getValue();
            int c = b + a;
            operandStack.push(Word.of(b + a));
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
        case (byte) 0x6c: // idiv
          {
            int a = operandStack.pop().getValue();
            int b = operandStack.pop().getValue();
            operandStack.push(Word.of(b / a));
            pc = pc + 1;
            break;
          }
        case (byte) 0x84: // iinc
          {
            int index = methodAreaManager.lookupByte(pc + 1);
            int incVal = methodAreaManager.lookupByte(pc + 2);
            locals[index] = Word.of(locals[index].getValue() + incVal);
            pc = pc + 3;
            break;
          }
        case (byte) 0xa2: // if_icmpge
          {
            // if (left >= right) then jump to jumpTo
            int right = operandStack.pop().getValue();
            int left = operandStack.pop().getValue();
            int jumpTo =
                ByteUtil.concat(
                    methodAreaManager.lookupByte(pc + 1), methodAreaManager.lookupByte(pc + 2));
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
            int jumpTo =
                ByteUtil.concat(
                    methodAreaManager.lookupByte(pc + 1), methodAreaManager.lookupByte(pc + 2));
            if (left > right) {
              pc = pc + jumpTo;
            } else {
              pc = pc + 3;
            }
            break;
          }
        case (byte) 0xa7: // goto
          {
            pc +=
                ByteUtil.concat(
                    methodAreaManager.lookupByte(pc + 1), methodAreaManager.lookupByte(pc + 2));
            break;
          }
        case (byte) 0xac: // ireturn
          {
            // ireturn returns one word
            var word = currentFrame().getOperandStack().pop();
            previousFrame().getOperandStack().push(word);
            if (canStackDown()) {
              stackDown();
              break;
            } else {
              return;
            }
          }
        case (byte) 0xb1: // return
          {
            // REFACTOR
            if (canStackDown()) {
              stackDown();
              break;
            } else {
              return;
            }
          }
        case (byte) 0xb2: // getstatic
          {
            var index =
                ByteUtil.concat(
                    methodAreaManager.lookupByte(pc + 1), methodAreaManager.lookupByte(pc + 2));
            ConstantFieldref fieldRef =
                (ConstantFieldref)
                    currentFrame().getMethod().getKlass().getConstantPool().findByIndex(index);

            RawClass rawClass = methodAreaManager.lookupClass(fieldRef.getConstantClassRef());
            RawField rawField = methodAreaManager.lookupField(fieldRef);

            List<Word> staticFieldValue = methodAreaManager.getStaticFieldValue(rawClass, rawField);
            staticFieldValue.forEach(operandStack::push);

            pc = pc + 3;
            break;
          }
        case (byte) 0xb3: // putstatic
        {
          var index =
              ByteUtil.concat(
                  methodAreaManager.lookupByte(pc + 1), methodAreaManager.lookupByte(pc + 2));
          ConstantFieldref fieldRef =
              (ConstantFieldref)
                  currentFrame().getMethod().getKlass().getConstantPool().findByIndex(index);

          RawClass rawClass = methodAreaManager.lookupClass(fieldRef.getConstantClassRef());
          RawField rawField = methodAreaManager.lookupField(fieldRef);

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
          var index =
              ByteUtil.concat(
                  methodAreaManager.lookupByte(pc + 1), methodAreaManager.lookupByte(pc + 2));
          ConstantFieldref fieldRef =
              (ConstantFieldref)
                  currentFrame().getMethod().getKlass().getConstantPool().findByIndex(index);

          // retrieve the object's address
          int objectId = operandStack.pop().getValue();
          var object = heapManager.lookupObject(objectId);

          var rawField = methodAreaManager.lookupField(fieldRef);

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
            var index =
                ByteUtil.concat(
                    methodAreaManager.lookupByte(pc + 1), methodAreaManager.lookupByte(pc + 2));
            ConstantFieldref fieldRef =
                (ConstantFieldref)
                    currentFrame().getMethod().getKlass().getConstantPool().findByIndex(index);

            // retrieve value to set
            JvmType jvmTypeName =
                JvmType.findByJvmSignature(fieldRef.getNameAndType().getDescriptor().getLabel());
            List<Word> words = retrieveData(jvmTypeName, operandStack);

            // retrieve the object's address
            int objectId = operandStack.pop().getValue();
            var object = RawSystem.heapManager.lookupObject(objectId);
            var rawField = methodAreaManager.lookupField(fieldRef);

            // set the value into field's address
            // REFACTOR: validate if the value is suitable for the field
            RawSystem.heapManager.setValue(object, rawField, words);

            pc += 3;
            break;
          }
        case (byte) 0xb6: // invokevirtual
//        {
//          // REFACTOR: cleanup codes
//          // REFACTOR: invokespecial and invokespecial has the identical logics.
//          var index = ByteUtil.concat(
//              methodAreaManager.lookupByte(pc + 1),
//              methodAreaManager.lookupByte(pc + 2)
//          );
//          ConstantMethodref methodRef =
//              (ConstantMethodref)
//                  currentFrame().getMethod().getKlass().getConstantPool().findByIndex(index);
//          RawMethod rawMethod = methodAreaManager.lookupMethod(methodRef);
//
//          var nextPc = pc + 3;
//          this.stackUp(rawMethod, nextPc);
//
//          // Since arguments are copied to the new frame in stackUp step,
//          // arguments pushed into current frame should be removed here.
//          for (int i = 0; i < rawMethod.getLocalsSize(); i++) {
//            previousFrame().getOperandStack().pop();
//          }
//
//          // no need to update pc. pc is modified in stackUp
//          break;
//        }
        case (byte) 0xb7: // invokespecial
          {
            // REFACTOR
            var index = ByteUtil.concat(
                methodAreaManager.lookupByte(pc + 1),
                methodAreaManager.lookupByte(pc + 2)
            );
            ConstantMethodref methodRef =
                (ConstantMethodref)
                    currentFrame().getMethod().getKlass().getConstantPool().findByIndex(index);
            RawMethod rawMethod = methodAreaManager.lookupMethod(methodRef);

            var nextPc = pc + 3;
            this.stackUp(rawMethod, nextPc);

            // Since arguments are copied to the new frame in stackUp step,
            // arguments pushed into current frame should be removed here.
            for (int i = 0; i < rawMethod.getLocalsSize(); i++) {
              previousFrame().getOperandStack().pop();
            }

            // no need to update pc. pc is modified in stackUp
            break;
          }
        case (byte) 0xb8: // invokestatic
          {
            // REFACTOR
            var index =
                ByteUtil.concat(
                    methodAreaManager.lookupByte(pc + 1), methodAreaManager.lookupByte(pc + 2));
            ConstantMethodref methodRef =
                (ConstantMethodref)
                    currentFrame().getMethod().getKlass().getConstantPool().findByIndex(index);
            RawMethod rawMethod = methodAreaManager.lookupMethod(methodRef);

            var nextPc = pc + 3; // invokestatic(1B) u2
            this.stackUp(rawMethod, nextPc);

            // Since arguments are copied to the new frame in stackUp step,
            // arguments pushed into current frame should be removed here.
            for (int i = 0; i < rawMethod.getLocalsSize(); i++) {
              previousFrame().getOperandStack().pop();
            }

            // no need to update pc. pc is modified in stackUp
            break;
          }
        case (byte) 0xbb: // new
          {
            /** new just allocates the necessary mem → objectref(=objectId) */
            var index =
                ByteUtil.concat(
                    methodAreaManager.lookupByte(pc + 1), methodAreaManager.lookupByte(pc + 2));
            ConstantClass classRef =
                (ConstantClass)
                    currentFrame().getMethod().getKlass().getConstantPool().findByIndex(index);

            RawClass rawClass = methodAreaManager.lookupOrLoadClass(classRef.getName().getLabel());
            int objectId = RawSystem.heapManager.register(rawClass);

            operandStack.push(Word.of(objectId));

            pc += 3;

            break;
          }
        default:
          throw new RuntimeException(String.format("unrecognized instruction %x", instruction));
      }
    }
  }

  /**
   * When retrieving data from operandStack,
   * it sometimes depends on what type is the data how many words to pop
   * */
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

  private void dump(String name, int pc, byte inst, RawThread thread) {
    System.out.printf(
        "[%s] stack#=%d, pc = %2d, inst = %x, frame=%s%n",
        name, thread.frames.size() - 1, pc, inst, this.currentFrame());
  }
}
