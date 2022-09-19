package dev.ishikawa.lovejvm.klass;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Methods {
    private final int size;
    private List<LMethod> methods;

    public Methods(int size, List<LMethod> methods) {
        if(size != methods.size()) throw new RuntimeException("invalid Methods. the size doesn't match with num of entries");
        this.size = size;
        this.methods = methods;
    }

    public Optional<LMethod> findBy(String methodName, String methodDescriptor) {
        return methods.stream()
                .filter((method) -> Objects.equals(method.getName().getLabel(), methodName) && Objects.equals(method.getDescriptor().getLabel(), methodDescriptor))
                .findFirst();
    }

    public Optional<LMethod> findMemberBy(String methodName, String methodDescriptor) {
        return methods.stream()
                .filter((method) -> !method.isStatic())
                .filter((method) -> Objects.equals(method.getName().getLabel(), methodName) && Objects.equals(method.getDescriptor().getLabel(), methodDescriptor))
                .findFirst();
    }

    public Optional<LMethod> findStaticBy(String methodName, String methodDescriptor) {
        return methods.stream()
                .filter(LMethod::isStatic)
                .filter((method) -> Objects.equals(method.getName().getLabel(), methodName) && Objects.equals(method.getDescriptor().getLabel(), methodDescriptor))
                .findFirst();
    }

    public int getSize() {
        return size;
    }
}
