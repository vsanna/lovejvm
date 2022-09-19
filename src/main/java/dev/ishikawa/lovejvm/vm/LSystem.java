package dev.ishikawa.lovejvm.vm;

import dev.ishikawa.lovejvm.memory.MethodArea;
import dev.ishikawa.lovejvm.memory.MethodAreaSimulator;

public class LSystem {
    static private LThread mainThread;
    final static public MethodArea methodArea = new MethodAreaSimulator();

    public static void setMainThread(LThread mainThread) {
        LSystem.mainThread = mainThread;
    }
}
