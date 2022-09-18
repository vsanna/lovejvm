package dev.ishikawa.lovejvm;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class LoveJVM {
    public static void main(String[] args) {
        /*
          static void add();
            Code:
               0: bipush        10
               2: istore_0
               3: iconst_4
               4: istore_1
               5: iload_0
               6: iload_1
               7: iadd
               8: istore_2
               9: iload_2
              10: iconst_2
              11: idiv
              12: istore_2
              13: return
        * */
        int[] input = {0x00, 0x02, 0x00, 0x03, 0x00, 0x00, 0x00, 0x0E,
                       0x10, 0x0A, 0x3B, 0x07, 0x3C, 0x1A, 0x1B,
                       0x60, 0x3D, 0x1C, 0x05, 0x6C, 0x3D, 0xB1};
        byte[] bytecode = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
           bytecode[i] = (byte)input[i];
        }

        LoveJVM jvm = new LoveJVM();

        jvm.run(bytecode);
    }

    void run(byte[] bytecode) {
        int pc = 8; /// bytecode[0] ~ bytecode[7] = metadata of the code section

        int localSize = (bytecode[2] << 4 << 4) + bytecode[3];
        byte[] locals = new byte[localSize];

        Deque<Byte> operandStack = new ArrayDeque<>();

        while (true) {
            dump(pc, bytecode, operandStack, locals);

            byte instruction = bytecode[pc];
            switch (instruction) {
                case (byte)0x10: // bipush
                    operandStack.push(bytecode[pc+1]);
                    pc = pc + 2;
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
                case (byte)0x05: // iconst_2
                    operandStack.push((byte)0x02);
                    pc = pc + 1;
                    break;
                case (byte)0x07: // iconst_4
                    operandStack.push((byte)0x04);
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
                    int iaddResult = (int)operandStack.pop() + (int)operandStack.pop();
                    operandStack.push((byte)iaddResult);
                    pc = pc + 1;
                    break;
                case (byte)0x6c: // idiv
                    int idiv1 = (int)operandStack.pop();
                    int idiv2 = (int)operandStack.pop();
                    operandStack.push((byte)(idiv2 / idiv1));
                    pc = pc + 1;
                    break;
                case (byte)0xb1: // return
                    return;
                default:
                    throw new RuntimeException(String.format("unrecognized instruction %x", instruction));
            }
        }
    }

    private void dump(int pc, byte[] bytecode, Deque<Byte> operandStack, byte[] locals) {
        System.out.printf("pc = %2d, inst = %x, locals = %-10s, operandStack = %s%n", pc, bytecode[pc], Arrays.toString(locals), operandStack);
    }
}
