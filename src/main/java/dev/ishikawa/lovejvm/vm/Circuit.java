package dev.ishikawa.lovejvm.vm;


import dev.ishikawa.lovejvm.util.ByteUtil;

import java.util.Deque;

public class Circuit {
    /**
     * @return int next pc
     * */
    public int trip(int pc, LFrame frame) {
        byte instruction = 0x10; // TODO: MethodArea
        dump(pc, instruction, frame);

        Deque<Word> operandStack = frame.getOperandStack();
        Word[] locals = frame.getLocals();

//        switch (instruction) {
//            case (byte)0x10: // bipush
//                operandStack.push(Word.of(bytecode[pc+1]));
//                pc = pc + 2;
//                break;
//            case (byte)0x11: // sipush
//                operandStack.push(Word.of(bytecode[pc+1], bytecode[pc+2]));
//                pc = pc + 3;
//                break;
//            case (byte)0x3b: // istore_0
//                locals[0] = operandStack.pop();
//                pc = pc + 1;
//                break;
//            case (byte)0x3c: // istore_1
//                locals[1] = operandStack.pop();
//                pc = pc + 1;
//                break;
//            case (byte)0x3d: // istore_2
//                locals[2] = operandStack.pop();
//                pc = pc + 1;
//                break;
//            case (byte)0x03: // iconst_0
//                operandStack.push(Word.of((byte)0x00));
//                pc = pc + 1;
//                break;
//            case (byte)0x05: // iconst_2
//                operandStack.push(Word.of((byte)0x02));
//                pc = pc + 1;
//                break;
//            case (byte)0x07: // iconst_4
//                operandStack.push(Word.of((byte)0x04));
//                pc = pc + 1;
//                break;
//            case (byte)0x1a: // iload_0
//                operandStack.push(locals[0]);
//                pc = pc + 1;
//                break;
//            case (byte)0x1b: // iload_1
//                operandStack.push(locals[1]);
//                pc = pc + 1;
//                break;
//            case (byte)0x1c: // iload_2
//                operandStack.push(locals[2]);
//                pc = pc + 1;
//                break;
//            case (byte)0x60: // iadd
//                int iaddResult = operandStack.pop().getValue() + operandStack.pop().getValue();
//                operandStack.push(Word.of(iaddResult));
//                pc = pc + 1;
//                break;
//            case (byte)0x6c: // idiv
//                int idiv1 = operandStack.pop().getValue();
//                int idiv2 = operandStack.pop().getValue();
//                operandStack.push(Word.of(idiv2/idiv1));
//                pc = pc + 1;
//                break;
//            case (byte)0xa2: // if_icmpge
//                // if (left >= right) then jump to jumpTo
//                int right = operandStack.pop().getValue();
//                int left = operandStack.pop().getValue();
//                int jumpTo = ByteUtil.concat(bytecode[pc+1], bytecode[pc+2]);
//                if(left >= right) {
//                    pc = pc + jumpTo;
//                } else {
//                    pc = pc + 3;
//                }
//                break;
//            case (byte)0x84: // iinc
//                int index = bytecode[pc+1];
//                int incVal = bytecode[pc+2];
//                locals[index] = Word.of(locals[index].getValue() + incVal);
//                pc = pc + 3;
//                break;
//            case (byte)0xa7: // goto
//                pc += ByteUtil.concat(bytecode[pc+1], bytecode[pc+2]);
//                break;
//            case (byte)0xac: // ireturn
//                // TODO: push top item of current operandStack into caller's operandStack
//                return;
//            case (byte)0xb1: // return
//                return;
//            default:
//                throw new RuntimeException(String.format("unrecognized instruction %x", instruction));
//        }
//

        return 0;
    }

    private void dump(int pc, byte inst, LFrame frame) {
        System.out.printf("pc = %2d, inst = %x, frame=%s%n", pc, inst, frame);
    }
}