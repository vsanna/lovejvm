package dev.ishikawa.lovejvm.memory;

import dev.ishikawa.lovejvm.klass.LClass;
import dev.ishikawa.lovejvm.klass.LMethod;
import dev.ishikawa.lovejvm.klass.constantpool.AttrName;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * MethodAreaSimulator simulate MethodArea by utilizing host java's features.
 * This doesn't manage native memory at all.
 * */
public class MethodAreaSimulator implements MethodArea {
    private Map<String, ClassEntry> classMap = new HashMap<>();
    private byte[] memory = new byte[100000];
    private int size = 0;

    @Override
    public byte lookupByte(int pc) {
        return memory[pc];
    }

    @Override
    public int lookupCodeSectionAddress(LMethod method) {
        LClass klass = method.getMethods().getKlass();
        ClassEntry classEntry = Optional.ofNullable(classMap.get(method.getMethods().getKlass().getName()))
                        .orElseThrow(() -> new RuntimeException("Non existing class is tried to load"));

        var classAddr = classEntry.address;
        var offsetToMethods = classEntry.klass.offsetToMethods();
        var offsetToMethod = klass.getMethods().offsetToMethod(method);
        var offsetToAttrs = method.offsetToAttrs();
        var offsetToAttr = method.getAttrs().offsetToAttr(AttrName.CODE);
        var offsetToInstructions = 14; // attrName ~ instruction length
        var result = classAddr + offsetToMethods + offsetToMethod + offsetToAttrs + offsetToAttr + offsetToInstructions;
        return result;
    }

    @Override
    public void register(LClass klass) {
        // TODO: According to the reference, The same name class can be loaded under different classloaders.
        // Take that into consideration.
        classMap.put(klass.getName(), new ClassEntry(size, klass));
        System.arraycopy(klass.getRaw(), 0, memory, size, klass.getRaw().length);
        size = klass.getRaw().length;
    }

    private static class ClassEntry {
        private int address;
        private LClass klass;

        public ClassEntry(int address, LClass klass) {
            this.address = address;
            this.klass = klass;
        }

        public int getAddress() {
            return address;
        }

        public LClass getKlass() {
            return klass;
        }
    }
}
