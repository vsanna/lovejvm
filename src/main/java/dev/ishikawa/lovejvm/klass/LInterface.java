package dev.ishikawa.lovejvm.klass;

import dev.ishikawa.lovejvm.klass.constantpool.entity.ConstantClass;

public class LInterface {
    private int accessFlag;
    private ConstantClass klass;

    public LInterface(
            int accessFlag,
            ConstantClass klass) {
        this.accessFlag = accessFlag;
        this.klass = klass;
    }

    public int size() {
        return 2;
    }
}
