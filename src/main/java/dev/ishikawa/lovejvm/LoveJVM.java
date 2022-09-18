package dev.ishikawa.lovejvm;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class LoveJVM {
    public static void main(String[] args) {
        /*
          public int test();
            Code:
               0: iconst_0
               1: istore_1
               2: iconst_0
               3: istore_2
               4: iload_2
               5: sipush        10000
               8: if_icmpge     21
              11: iload_1
              12: iload_2
              13: iadd
              14: istore_1
              15: iinc          2, 1
              18: goto          4
              21: iload_1
              22: ireturn
        * */

        int[] input = {
                0x00, 0x02, 0x00, 0x03, 0x00, 0x00, 0x00, 0x17,
                0x03, 0x3C, 0x03, 0x3D, 0x1C, 0x11, 0x27, 0x10,
                0xA2, 0x00, 0x0D, 0x1B, 0x1C, 0x60, 0x3C, 0x84,
                0x02, 0x01, 0xA7, 0xFF, 0xF2, 0x1B, 0xAC
        };

        byte[] bytecode = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
           bytecode[i] = (byte)input[i];
        }

        LoveJVM jvm = new LoveJVM();

        jvm.run(bytecode);
    }

    void run(byte[] bytecode) {
        int codeMetaSize = 8;
        int pc = codeMetaSize; /// bytecode[0] ~ bytecode[7] = metadata of the code section

        int localSize = concat(bytecode[2], bytecode[3]);
        Word[] locals = new Word[localSize];

        // NOTE: an element's size in locals = 1word(=32bit=4bytes)
        //       an element's size in opStck = 1word
        Deque<Word> operandStack = new ArrayDeque<>();

        while (true) {
            dump(pc, bytecode, operandStack, locals);

            byte instruction = bytecode[pc];
            switch (instruction) {
                case (byte)0x10: // bipush
                    operandStack.push(Word.of(bytecode[pc+1]));
                    pc = pc + 2;
                    break;
                case (byte)0x11: // sipush
                    operandStack.push(Word.of(bytecode[pc+1], bytecode[pc+2]));
                    pc = pc + 3;
                    break;
                case (byte)0x3b: // istore_0
                    locals[0] = operandStack.pop();
                    pc = pc + 1;
                    break;
                case (byte)0x3c: // istore_1
                    locals[1] = operandStack.pop();
                    pc = pc + 1;
                    break;
                case (byte)0x3d: // istore_2
                    locals[2] = operandStack.pop();
                    pc = pc + 1;
                    break;
                case (byte)0x03: // iconst_0
                    operandStack.push(Word.of((byte)0x00));
                    pc = pc + 1;
                    break;
                case (byte)0x05: // iconst_2
                    operandStack.push(Word.of((byte)0x02));
                    pc = pc + 1;
                    break;
                case (byte)0x07: // iconst_4
                    operandStack.push(Word.of((byte)0x04));
                    pc = pc + 1;
                    break;
                case (byte)0x1a: // iload_0
                    operandStack.push(locals[0]);
                    pc = pc + 1;
                    break;
                case (byte)0x1b: // iload_1
                    operandStack.push(locals[1]);
                    pc = pc + 1;
                    break;
                case (byte)0x1c: // iload_2
                    operandStack.push(locals[2]);
                    pc = pc + 1;
                    break;
                case (byte)0x60: // iadd
                    int iaddResult = operandStack.pop().getValue() + operandStack.pop().getValue();
                    operandStack.push(Word.of(iaddResult));
                    pc = pc + 1;
                    break;
                case (byte)0x6c: // idiv
                    int idiv1 = operandStack.pop().getValue();
                    int idiv2 = operandStack.pop().getValue();
                    operandStack.push(Word.of(idiv2/idiv1));
                    pc = pc + 1;
                    break;
                case (byte)0xa2: // if_icmpge
                    // if (left >= right) then jump to jumpTo
                    int right = operandStack.pop().getValue();
                    int left = operandStack.pop().getValue();
                    int jumpTo = concat(bytecode[pc+1], bytecode[pc+2]);
                    if(left >= right) {
                        pc = pc + jumpTo;
                    } else {
                        pc = pc + 3;
                    }
                    break;
                case (byte)0x84: // iinc
                    int index = bytecode[pc+1];
                    int incVal = bytecode[pc+2];
                    locals[index] = Word.of(locals[index].getValue() + incVal);
                    pc = pc + 3;
                    break;
                case (byte)0xa7: // goto
                    pc += concat(bytecode[pc+1], bytecode[pc+2]);
                    break;
                case (byte)0xac: // ireturn
                    // TODO: push top item of current operandStack into caller's operandStack
                    return;
                case (byte)0xb1: // return
                    return;
                default:
                    throw new RuntimeException(String.format("unrecognized instruction %x", instruction));
            }
        }
    }

    private void dump(int pc, byte[] bytecode, Deque<Word> operandStack, Word[] locals) {
        System.out.printf("pc = %2d, inst = %x, locals = %-10s, operandStack = %s%n", pc, bytecode[pc], Arrays.toString(locals), operandStack);
    }

    private byte concat(byte a, byte b) {
        return (byte)((a << 4 << 4) + b);
    }

    static class Word {
        public Word(byte[] bytes) {
            this.bytes = bytes;
        }

        // 4bytes.
        private byte[] bytes;

        public int getValue() {
            return (0b11111111000000000000000000000000 & (bytes[0] << 8 << 8 << 8))
                    | (0b00000000111111110000000000000000 & (bytes[1] << 8 << 8))
                    | (0b00000000000000001111111100000000 & (bytes[2] << 8))
                    | (0b00000000000000000000000011111111 & bytes[3]);
        }

        static Word of(byte b) {
            return new Word(new byte[]{0x00, 0x00, 0x00, b});
        }

        static Word of(byte a, byte b) {
            return new Word(new byte[]{0x00, 0x00, a, b});
        }

        static Word of(byte a, byte b, byte c) {
            return new Word(new byte[]{0x00, a, b, c});
        }

        static Word of(byte a, byte b, byte c, byte d) {
            return new Word(new byte[]{a, b, c, d});
        }

        static Word of(int a) {
            return new Word(new byte[]{
                    (byte)((a >> 8 >> 8 >> 8) & 0b11111111),
                    (byte)((a >> 8 >> 8) & 0b11111111),
                    (byte)((a >> 8) & 0b11111111),
                    (byte)(a & 0b11111111)
            });
        }

        @Override
        public String toString() {
            return String.format("Word(%x,%x,%x,%x: %d)", bytes[0], bytes[1], bytes[2], bytes[3], this.getValue());
        }
    }
}
