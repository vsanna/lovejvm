package dev.ishikawa.lovejvm.memory;

import dev.ishikawa.lovejvm.klass.LClass;
import dev.ishikawa.lovejvm.klass.LMethod;
import dev.ishikawa.lovejvm.klass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.klass.constantpool.entity.ConstantMethodref;

public interface MethodArea {
    void register(LClass klass);
    byte lookupByte(int pc);

    int lookupCodeSectionAddress(LMethod method);

    LMethod lookupMethod(ConstantMethodref constantMethodref);
}
