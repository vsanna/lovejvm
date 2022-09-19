package dev.ishikawa.lovejvm.memory;

import dev.ishikawa.lovejvm.klass.LClass;
import dev.ishikawa.lovejvm.klass.LMethod;

public interface MethodArea {
    byte lookupByte(int pc);
    int lookupCodeSectionAddress(LMethod method);
    void register(LClass klass);
}
