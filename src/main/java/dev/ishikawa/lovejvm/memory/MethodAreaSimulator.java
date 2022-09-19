package dev.ishikawa.lovejvm.memory;

import dev.ishikawa.lovejvm.klass.LClass;

import java.util.Map;
import java.util.Optional;

/**
 * MethodAreaSimulator simulate MethodArea by utilizing host java's features.
 * This doesn't manage native memory at all.
 * */
public class MethodAreaSimulator implements MethodArea {
    private Map<String, LClass> classMap;
    private byte[] memory = new byte[100000];

    @Override
    public byte lookupInstruction(int pc) {
        return memory[pc];
    }

    // TODO: impl ClassNotFoundException
    @Override
    public LClass lookupClass(String className) {
        return Optional.ofNullable(classMap.get(className))
                .orElseThrow(() -> new RuntimeException(String.format("Class(%s) is not found", className)));
    }

    @Override
    public void register(LClass klass) {

    }
}
