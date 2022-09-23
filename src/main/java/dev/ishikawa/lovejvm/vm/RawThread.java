package dev.ishikawa.lovejvm.vm;

import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodref;
import dev.ishikawa.lovejvm.rawclass.method.RawMethod;
import dev.ishikawa.lovejvm.util.ByteUtil;

import java.util.ArrayDeque;
import java.util.Deque;

public class RawThread {
    private final String name;
    private final Deque<Frame> frames = new ArrayDeque<>(); // TODO: have a stack in thread level.
    private int pc = 0;

    public RawThread(String name) {
        this.name = name;
    }

    public void init(RawMethod entryPoint) {
        stackUp(entryPoint, 0);
    }

    /**
     * add a new frame. copy necessary locals from current frame to the new frame
     * @param {int} pcToReturn next pc in current frame. not in the next frame.
     *              the caller of stackUp has to calculate pcToReturn beforehand.
     * */
    private void stackUp(RawMethod nextMethod, int pcToReturn) {
        pc = RawSystem.methodArea.lookupCodeSectionAddress(nextMethod);
        Frame newFrame = new Frame(this, nextMethod);

        if(currentFrame() != null) {
            // NOTE: store previous PC on the bottom of operandStack
            // so that it can go back to previous PC when calling stackDown
            newFrame.getOperandStack().push(Word.of(pcToReturn));

            // TODO: Ideally, Not copying the words, but use the unified global stack per one thread!!
            // load locals from currentFrame into the new frame
            var localsSize = nextMethod.getLocalsSize();
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
            // this pcToReturn won't be used because pcToReturn is for returning to the previous frame
            newFrame.getOperandStack().push(Word.of(pcToReturn));

            // this is the entry point method.
            // TODO: need to pass initial args passed from command line to this method.
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

    // TODO: refactor
    private Frame previousFrame() {
        var tmp = this.frames.pop();
        var ret = this.frames.peek();
        this.frames.push(tmp);
        return ret;
    }

    public void run() {
        var methodArea = RawSystem.methodArea;

        // TODO: refactor. most of these logics should be in a separate place or in LFrame
        while(true) {
            byte instruction = methodArea.lookupByte(pc);
            dump(pc, instruction, this);

            Deque<Word> operandStack = currentFrame().getOperandStack();
            Word[] locals = currentFrame().getLocals();

            switch (instruction) {
                case (byte)0x10: { // bipush
                    operandStack.push(Word.of(methodArea.lookupByte(pc+1)));
                    pc = pc + 2;
                    break;
                }
                case (byte)0x11: { // sipush
                    operandStack.push(Word.of(methodArea.lookupByte(pc+1), methodArea.lookupByte(pc+2)));
                    pc = pc + 3;
                    break;
                }
                case (byte)0x3b: { // istore_0
                    locals[0] = operandStack.pop();
                    pc = pc + 1;
                    break;
                }
                case (byte)0x3c: { // istore_1
                    locals[1] = operandStack.pop();
                    pc = pc + 1;
                    break;
                }
                case (byte)0x3d: { // istore_2
                    locals[2] = operandStack.pop();
                    pc = pc + 1;
                    break;
                }
                case (byte)0x03: { // iconst_0
                    operandStack.push(Word.of((byte)0x00));
                    pc = pc + 1;
                    break;
                }
                case (byte)0x04: { // iconst_1
                    operandStack.push(Word.of((byte)0x01));
                    pc = pc + 1;
                    break;
                }
                case (byte)0x05: { // iconst_2
                    operandStack.push(Word.of((byte) 0x02));
                    pc = pc + 1;
                    break;
                }
                case (byte)0x07: { // iconst_4
                    operandStack.push(Word.of((byte)0x04));
                    pc = pc + 1;
                    break;
                }
                case (byte)0x1a: { // iload_0
                    operandStack.push(locals[0]);
                    pc = pc + 1;
                    break;
                }
                case (byte)0x1b: { // iload_1
                    operandStack.push(locals[1]);
                    pc = pc + 1;
                    break;
                }
                case (byte)0x1c: // iload_2
                    operandStack.push(locals[2]);
                    pc = pc + 1;
                    break;
                case (byte)0x60: { // iadd
                    int a = operandStack.pop().getValue();
                    int b = operandStack.pop().getValue();
                    operandStack.push(Word.of(b + a));
                    pc = pc + 1;
                    break;
                }
                case (byte)0x64: { // isub
                    int a = operandStack.pop().getValue();
                    int b = operandStack.pop().getValue();
                    operandStack.push(Word.of(b - a));
                    pc = pc + 1;
                    break;
                }
                case (byte)0x6c: { // idiv
                    int a = operandStack.pop().getValue();
                    int b = operandStack.pop().getValue();
                    operandStack.push(Word.of(b / a));
                    pc = pc + 1;
                    break;
                }
                case (byte)0xa2: { // if_icmpge
                    // if (left >= right) then jump to jumpTo
                    int right = operandStack.pop().getValue();
                    int left = operandStack.pop().getValue();
                    int jumpTo = ByteUtil.concat(methodArea.lookupByte(pc + 1), methodArea.lookupByte(pc + 2));
                    if (left >= right) {
                        pc = pc + jumpTo;
                    } else {
                        pc = pc + 3;
                    }
                    break;
                }
                case (byte)0xa3: { // if_icmpgt
                    // if (left > right) then jump to jumpTo
                    int right = operandStack.pop().getValue();
                    int left = operandStack.pop().getValue();
                    int jumpTo = ByteUtil.concat(methodArea.lookupByte(pc + 1), methodArea.lookupByte(pc + 2));
                    if (left > right) {
                        pc = pc + jumpTo;
                    } else {
                        pc = pc + 3;
                    }
                    break;
                }
                case (byte)0x84: { // iinc
                    int index = methodArea.lookupByte(pc + 1);
                    int incVal = methodArea.lookupByte(pc + 2);
                    locals[index] = Word.of(locals[index].getValue() + incVal);
                    pc = pc + 3;
                    break;
                }
                case (byte)0xa7: { // goto
                    pc += ByteUtil.concat(methodArea.lookupByte(pc + 1), methodArea.lookupByte(pc + 2));
                    break;
                }
                case (byte)0xac: { // ireturn
                    // ireturn returns one word
                    var word = currentFrame().getOperandStack().pop();
                    previousFrame().getOperandStack().push(word);
                    if(canStackDown()) {
                        stackDown();
                        break;
                    } else {
                        return;
                    }
                }
                case (byte)0xb1: { // return
                    // TODO: refactor
                    if(canStackDown()) {
                        stackDown();
                        break;
                    } else {
                        return;
                    }
                }
                case (byte)0xb8: { // invokestatic
                    // TODO refactor...
                    var index = ByteUtil.concat(methodArea.lookupByte(pc+1), methodArea.lookupByte(pc+2));
                    ConstantMethodref methodRef = (ConstantMethodref) currentFrame().getMethod().getKlass().getConstantPool().findByIndex(index);
                    RawMethod rawMethod = methodArea.lookupMethod(methodRef);

                    var nextPc = pc + 3; // invokestatic(1B) u2
                    this.stackUp(rawMethod, nextPc);

                    // Since arguments are copied to the new frame in stackUp,
                    // arguments pushed into current frame should be removed here.
                    // argumentの内容 = localsのこと
                    for (int i = 0; i < rawMethod.getLocalsSize(); i++) {
                        previousFrame().getOperandStack().pop();
                    }

                    break;
                }
                case (byte)0x00: { // nop
                    pc += 1;
                    break;
                }
                case (byte)0x57: { // pop
                    currentFrame().getOperandStack().pop();
                    pc += 1;
                    break;
                }
                case (byte)0x58: { // pop2
                    currentFrame().getOperandStack().pop();
                    currentFrame().getOperandStack().pop();
                    pc += 1;
                    break;
                }
                default:
                    throw new RuntimeException(String.format("unrecognized instruction %x", instruction));
            }
        }
    }

    private void dump(int pc, byte inst, RawThread thread) {
        System.out.printf("stack#=%d, pc = %2d, inst = %x, frame=%s%n", thread.frames.size()-1, pc, inst, this.currentFrame());
    }
}