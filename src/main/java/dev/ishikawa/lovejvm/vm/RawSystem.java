package dev.ishikawa.lovejvm.vm;

import dev.ishikawa.lovejvm.memory.MethodArea;
import dev.ishikawa.lovejvm.memory.MethodAreaSimulator;

public class RawSystem {
    static private RawThread mainThread;
    final static public MethodArea methodArea = new MethodAreaSimulator();

    public static void setMainThread(RawThread mainThread) {
        RawSystem.mainThread = mainThread;
    }
}
