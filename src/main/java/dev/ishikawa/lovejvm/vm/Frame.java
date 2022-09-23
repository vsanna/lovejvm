package dev.ishikawa.lovejvm.vm;


import dev.ishikawa.lovejvm.rawclass.method.RawMethod;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class Frame {
  private RawThread thread;
  // TODO: keep the info of what method this frame is for for a while
  private RawMethod method;
  private Word[] locals;
  private Deque<Word> operandStack;

  public Frame(RawThread thread, RawMethod method) {
    this.thread = thread;
    this.method = method;
    this.locals = new Word[method.getLocalsSize()];
    this.operandStack = new ArrayDeque<>(method.getOperandStackSize());
  }

  public Deque<Word> getOperandStack() {
    return operandStack;
  }

  public Word[] getLocals() {
    return locals;
  }

  public RawMethod getMethod() {
    return method;
  }

  @Override
  public String toString() {
    return String.format(
        "LFrame{locals=%s, operandStack=%s}", Arrays.toString(locals), operandStack);
  }
}
