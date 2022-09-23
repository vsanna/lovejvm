package dev.ishikawa.lovejvm.memory;

import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodref;
import dev.ishikawa.lovejvm.rawclass.method.RawMethod;

public interface MethodArea {
    void register(RawClass rawClass);
    byte lookupByte(int pc);

    int lookupCodeSectionAddress(RawMethod method);

    RawMethod lookupMethod(ConstantMethodref constantMethodref);
}
