package dev.ishikawa.lovejvm.vm;

import dev.ishikawa.lovejvm.klass.LMethod;

import java.util.ArrayDeque;
import java.util.Deque;

public class LThread {
    public LThread(String name) {
        this.name = name;
    }

    private final String name;
    private final Deque<LFrame> frames = new ArrayDeque<>();
    private int pc = 0;
    // TODO: have a stack in thread level.

    public void init(LMethod entryPoint) {
        pc = 1; // TODO MethodArea.lookupCodeSection(entryPoint)
        LFrame frame = LFrame.init(this, entryPoint);
        frames.push(frame);
    }

    private LFrame currentFrame() {
        return this.frames.peek();
    }

    public void run() {
        while(true) {
            // ここでswitch
            // pc, frame(=locals, operandStack)を更新
            break;
        }
    }
}
