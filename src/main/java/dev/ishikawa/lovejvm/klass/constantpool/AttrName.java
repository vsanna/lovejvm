package dev.ishikawa.lovejvm.klass.constantpool;


import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ref: https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.7
 * - critical
 *   - ConstantValue, Code, StackMapTable, BootstrapMethods, NestHost, NestMembers, PermittedSubclasses
 * - not critical, but used by libs
 *   - Exceptions, InnerClasses, EnclosingMethod, Synthetic, Signature, Record, SourceFile, LineNumberTable, LocalVariableTable, LocalVariableTypeTable
 * - optional
 *   - SourceDebugExtension, Deprecated, RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations, RuntimeVisibleParameterAnnotations, RuntimeInvisibleParameterAnnotations, RuntimeVisibleTypeAnnotations, RuntimeInvisibleTypeAnnotations, AnnotationDefault, MethodParameters, Module, ModulePackages, ModuleMainClass
 * */
public enum AttrName {
    CONSTANT_VALUE("ConstantValue"),
    CODE("Code"),
    STACK_MAP_TABLE("StackMapTable"),
    BOOTSTRAP_METHODS("BootstrapMethods"),
    NEST_HOST("NestHost"),
    NEST_MEMBERS("NestMembers"),
    PERMITTED_SUBCLASSES("PermittedSubclasses"),
    EXCEPTIONS("Exceptions"),
    INNER_CLASSES("InnerClasses"),
    ENCLOSING_METHOD("EnclosingMethod"),
    SYNTHETIC("Synthetic"),
    SIGNATURE("Signature"),
    RECORD("Record"),
    SOURCE_FILE("SourceFile"),
    LINE_NUMBER_TABLE("LineNumberTable"),
    LOCAL_VARIABLE_TABLE("LocalVariableTable"),
    LOCAL_VARIABLE_TYPE_TABLE("LocalVariableTypeTable"),
    SOURCE_DEBUG_EXTENSION("SourceDebugExtension"),
    RUNTIME_VISIBLE_ANNOTATIONS("RuntimeVisibleAnnotations"),
    RUNTIME_INVISIBLE_ANNOTATIONS("RuntimeInvisibleAnnotations"),
    RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS("RuntimeVisibleParameterAnnotations"),
    RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS("RuntimeInvisibleParameterAnnotations"),
    RUNTIME_VISIBLE_TYPE_ANNOTATIONS("RuntimeVisibleTypeAnnotations"),
    RUNTIME_INVISIBLE_TYPE_ANNOTATIONS("RuntimeInvisibleTypeAnnotations"),
    ANNOTATION_DEFAULT("AnnotationDefault"),
    METHOD_PARAMETERS("MethodParameters"),
    MODULE("Module"),
    MODULE_PACKAGES("ModulePackages"),
    MODULE_MAIN_CLASS("ModuleMainClass");

    private String label;

    AttrName(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    private static Map<String, AttrName> entryMap = Arrays.stream(AttrName.values())
            .collect(Collectors.toMap(AttrName::getLabel, Function.identity()));

    public static AttrName findBy(String label) {
        return Optional.ofNullable(entryMap.get(label))
                .orElseThrow(() -> new RuntimeException(String.format("invalid label: %s", label)));
    }
}
