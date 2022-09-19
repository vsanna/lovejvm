package dev.ishikawa.lovejvm.memory;

import dev.ishikawa.lovejvm.klass.LClass;

public interface MethodArea {
    byte lookupInstruction(int pc);
    LClass lookupClass(String className);
//    LMethod lookupMethod(String className, String methodName, String methodDesc);
//    LField lookupField(String className, String fieldName);

    void register(LClass klass);
}
