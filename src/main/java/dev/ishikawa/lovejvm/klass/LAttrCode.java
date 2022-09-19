package dev.ishikawa.lovejvm.klass;

import dev.ishikawa.lovejvm.klass.constantpool.Attrs;
import dev.ishikawa.lovejvm.klass.constantpool.entity.ConstantPoolEntry;
import dev.ishikawa.lovejvm.klass.constantpool.entity.ConstantUtf8;

/**
 * only method can have this attr
 * */

public class LAttrCode extends LAttr<LAttrCode.LAttrCodeBody> {
    public LAttrCode(
            ConstantUtf8 attrName,
            int dataLength,
            LAttrCodeBody body
    ) {
        super(attrName, dataLength, body);
    }

    public static LAttrCode of(
            ConstantUtf8 attrName,
            int dataLength,
            int operandStackSize,
            int localsSize,
            int instructionLength,
            byte[] instructionSection,
            ExceptionHandlers exceptionHandlers,
            Attrs attrs
    ) {
       var body = new LAttrCodeBody(operandStackSize, localsSize, instructionLength, instructionSection, exceptionHandlers, attrs);
       return new LAttrCode(attrName, dataLength, body);
    }

    public int getOperandStackSize(){
        return this.getAttrBody().operandStackSize;
    }

    public int getLocalsSize(){
        return this.getAttrBody().localsSize;
    }


    static class LAttrCodeBody {
        // TODO: consider how to hold this part.
        // just as an array of byte OR something like List<Instruction>.

        private int operandStackSize;
        private int localsSize;
        private int instructionLength;
        private byte[] instructionSection;
        private ExceptionHandlers exceptionHandlers;
        private Attrs attrs;

        public LAttrCodeBody(
                int operandStackSize,
                int localsSize,
                int instructionLength,
                byte[] instructionSection,
                ExceptionHandlers exceptionHandlers,
                Attrs attrs
        ) {
            this.operandStackSize = operandStackSize;
            this.localsSize = localsSize;
            this.instructionLength = instructionLength;
            this.instructionSection = instructionSection;
            this.exceptionHandlers = exceptionHandlers;
            this.attrs = attrs;
        }

        public byte[] getInstructionSection() {
            return instructionSection;
        }
    }
}
