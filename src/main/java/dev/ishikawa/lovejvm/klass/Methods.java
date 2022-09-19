package dev.ishikawa.lovejvm.klass;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Methods {
    private LClass klass;
    private final int entrySize;
    private List<LMethod> methods;

    public Methods(int entrySize, List<LMethod> methods) {
        if(entrySize != methods.size()) throw new RuntimeException("invalid Methods. the entrySize doesn't match with num of entries");
        this.entrySize = entrySize;
        this.methods = methods;
        this.methods.forEach(it -> it.setMethods(this));
    }

    // TODO: if we can use the same signature for static/member methods in a class, this is broken.
    // What is the identifical information of one method?
    public Optional<LMethod> findBy(String methodName, String methodDescriptor) {
        return methods.stream()
                .filter((method) -> method.hasSignature(methodName, methodDescriptor))
                .findFirst();
    }

    public Optional<LMethod> findMemberBy(String methodName, String methodDescriptor) {
        return methods.stream()
                .filter((method) -> !method.isStatic())
                .filter((method) -> method.hasSignature(methodName, methodDescriptor))
                .findFirst();
    }

    public Optional<LMethod> findStaticBy(String methodName, String methodDescriptor) {
        return methods.stream()
                .filter(LMethod::isStatic)
                .filter((method) -> method.hasSignature(methodName, methodDescriptor))
                .findFirst();
    }

    public int size() {
        return 2 + methods.stream().map(LMethod::size).reduce(0, Integer::sum);
    }

    public int offsetToMethod(LMethod method) {
        if(!methods.contains(method)) return 0;

        int result = 2;

        for (LMethod lMethod : methods) {
            if(lMethod != method) {
                result += lMethod.size();
            } else {
                break;
            }
        }

        return result;
    }

    public LClass getKlass() {
        return klass;
    }

    public void setKlass(LClass klass) {
        this.klass = klass;
    }
}
