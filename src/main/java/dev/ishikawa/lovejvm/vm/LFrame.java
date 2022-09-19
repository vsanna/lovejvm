package dev.ishikawa.lovejvm.vm;

import dev.ishikawa.lovejvm.klass.LMethod;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class LFrame {
    private LThread thread;
    // TODO: keep the info of what method this frame is for for a while
    private LMethod method;
    private Word[] locals;
    private Deque<Word> operandStack;

    public LFrame(LThread thread, LMethod method) {
        this.thread = thread;
        this.method = method;
        this.locals =  new Word[method.getLocalsSize()];
        this.operandStack = new ArrayDeque<>(method.getOperandStackSize());
    }

    public static LFrame init(LThread thread, LMethod method) {
        return new LFrame(thread, method);
    }

    public Deque<Word> getOperandStack() {
        return operandStack;
    }

    // NOTE: no need to consider thread safe here because this is the thread itself, so these tasks here won't be splitted.
    public Word[] getLocals() {
        return locals;
    }

    @Override
    public String toString() {
        return String.format("LFrame{locals=%s, operandStack=%s}", Arrays.toString(locals), operandStack);
    }

    public LMethod getMethod() {
        return method;
    }
}
