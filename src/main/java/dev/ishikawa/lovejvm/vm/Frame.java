package dev.ishikawa.lovejvm.vm;


import dev.ishikawa.lovejvm.rawclass.method.RawMethod;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class Frame {
  private RawThread thread;
  // REFACTOR: keep the info of what method this frame is for for a while
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

  public void setLocals(Word[] words) {
    this.locals = words;
  }

  public RawMethod getMethod() {
    return method;
  }

  @Override
  public String toString() {
    String className = getMethod().getClassBinaryName();
    String methodName = getMethod().getName().getLabel();
    return String.format(
        "[%s#%s]{locals=%s, operandStack=%s}",
        className, methodName, Arrays.toString(locals), operandStack);
  }
}
