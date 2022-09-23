package dev.ishikawa.lovejvm.vm;


import dev.ishikawa.lovejvm.memory.MethodArea;
import dev.ishikawa.lovejvm.memory.MethodAreaSimulator;

public class RawSystem {
  private static RawThread mainThread;
  public static final MethodArea methodArea = new MethodAreaSimulator();

  public static void setMainThread(RawThread mainThread) {
    RawSystem.mainThread = mainThread;
  }
}
