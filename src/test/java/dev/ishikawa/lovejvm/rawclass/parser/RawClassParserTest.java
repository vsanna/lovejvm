package dev.ishikawa.lovejvm.rawclass.parser;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;

import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.field.RawField;
import dev.ishikawa.lovejvm.rawclass.field.RawField.AccessFlag;
import dev.ishikawa.lovejvm.rawclass.method.RawMethod;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class RawClassParserTest {

  @Test
  void parse_Add() {
    var testClass = getClass().getClassLoader().getResource("testClassFiles/Add.class");
    byte[] classFiles = ByteUtil.readBytesFromFilePath(testClass.getPath());
    RawClass actual = new RawClassParser(classFiles, null).parse();

    assertThat(actual.getRaw().length).isEqualTo(actual.getClassfileBytes()); // 255
    assertThat(actual.getMajorVersion()).isEqualTo(59);
    assertThat(actual.getMinorVersion()).isEqualTo(0);
    assertThat(actual.getConstantPool().getEntrySize()).isEqualTo(14);
    assertThat(actual.getAccessFlag()).isEqualTo(32);
    assertThat(actual.getThisClass().getName().getLabel()).isEqualTo("Add");
    assertThat(actual.getSuperClass().getName().getLabel()).isEqualTo("java/lang/Object");
    assertThat(actual.getInterfaces().getEntrySize()).isEqualTo(0);
    assertThat(actual.getFields().getEntrySize()).isEqualTo(0);
    assertThat(actual.getMethods().getEntrySize()).isEqualTo(2);
    assertThat(actual.getAttrs().getEntrySize()).isEqualTo(1);

    assertThat(actual.getClassLoaderName()).isNull();
    assertThat(actual.getName()).isEqualTo("Add");
    assertThat(actual.getFullyQualifiedName()).isEqualTo("Add");
    assertThat(actual.getFilename()).isEqualTo("Add.java");
  }

  @Test
  void parse_Recursive2() {
    var testClass = getClass().getClassLoader().getResource("testClassFiles/Recursive2.class");
    byte[] classFiles = ByteUtil.readBytesFromFilePath(testClass.getPath());
    RawClass actual = new RawClassParser(classFiles, null).parse();

    assertThat(actual.getRaw().length).isEqualTo(actual.getClassfileBytes()); // 368
    assertThat(actual.getMajorVersion()).isEqualTo(59);
    assertThat(actual.getMinorVersion()).isEqualTo(0);
    assertThat(actual.getConstantPool().getEntrySize()).isEqualTo(19);
    assertThat(actual.getAccessFlag()).isEqualTo(32);
    assertThat(actual.getThisClass().getName().getLabel()).isEqualTo("Recursive2");
    assertThat(actual.getSuperClass().getName().getLabel()).isEqualTo("java/lang/Object");
    assertThat(actual.getInterfaces().getEntrySize()).isEqualTo(0);
    assertThat(actual.getFields().getEntrySize()).isEqualTo(0);
    assertThat(actual.getMethods().getEntrySize()).isEqualTo(3);
    assertThat(actual.getAttrs().getEntrySize()).isEqualTo(1);

    assertThat(actual.getClassLoaderName()).isNull();
    assertThat(actual.getName()).isEqualTo("Recursive2");
    assertThat(actual.getFullyQualifiedName()).isEqualTo("Recursive2");
    assertThat(actual.getFilename()).isEqualTo("Recursive2.java");
  }

  @Test
  void parse_RichClass() {
    var testClass =
        getClass().getClassLoader().getResource("testClassFiles/dev/ishikawa/test/RichClass.class");
    byte[] classFiles = ByteUtil.readBytesFromFilePath(testClass.getPath());
    RawClass actual = new RawClassParser(classFiles, null).parse();

    assertThat(actual.getRaw().length).isEqualTo(actual.getClassfileBytes()); // 368
    assertThat(actual.getMajorVersion()).isEqualTo(59);
    assertThat(actual.getMinorVersion()).isEqualTo(0);
    assertThat(actual.getConstantPool().getEntrySize()).isEqualTo(54);
    assertThat(actual.getAccessFlag()).isEqualTo(32);
    assertThat(actual.getThisClass().getName().getLabel()).isEqualTo("dev/ishikawa/test/RichClass");
    assertThat(actual.getSuperClass().getName().getLabel()).isEqualTo("java/lang/Object");
    assertThat(actual.getInterfaces().getEntrySize()).isEqualTo(1);
    assertThat(actual.getFields().getEntrySize()).isEqualTo(12);
    var fieldAccessFlagTestCases =
        Map.ofEntries(
            entry("s1", List.of(AccessFlag.PRIVATE)),
            entry("s2", List.of(AccessFlag.PRIVATE, AccessFlag.FINAL)),
            entry("s3", List.of(AccessFlag.PUBLIC)),
            entry("s4", List.of(AccessFlag.PUBLIC, AccessFlag.FINAL)),
            entry("s5", List.of(AccessFlag.PROTECTED)),
            entry("s6", List.of(AccessFlag.PROTECTED, AccessFlag.FINAL)));
    fieldAccessFlagTestCases.forEach(
        (fieldName, accessFlagList) -> {
          Optional<RawField> field = actual.getFields().findMemberBy(fieldName);
          assertThat(field).isNotEmpty();
          assertThat(field.get().getAccessFlag())
              .containsExactlyInAnyOrderElementsOf(accessFlagList);
        });
    var staticFieldAccessFlagTestCases =
        Map.ofEntries(
            entry("ss1", List.of(AccessFlag.STATIC, AccessFlag.PRIVATE)),
            entry("ss2", List.of(AccessFlag.STATIC, AccessFlag.PRIVATE, AccessFlag.FINAL)),
            entry("ss3", List.of(AccessFlag.STATIC, AccessFlag.PUBLIC)),
            entry("ss4", List.of(AccessFlag.STATIC, AccessFlag.PUBLIC, AccessFlag.FINAL)),
            entry("ss5", List.of(AccessFlag.STATIC, AccessFlag.PROTECTED)),
            entry("ss6", List.of(AccessFlag.STATIC, AccessFlag.PROTECTED, AccessFlag.FINAL)));
    staticFieldAccessFlagTestCases.forEach(
        (fieldName, accessFlagList) -> {
          Optional<RawField> field = actual.getFields().findStaticBy(fieldName);
          assertThat(field).isNotEmpty();
          assertThat(field.get().getAccessFlag())
              .containsExactlyInAnyOrderElementsOf(accessFlagList);
        });

    assertThat(actual.getMethods().getEntrySize()).isEqualTo(3);
    var methodAccessFlagTestCases =
        Map.ofEntries(
            entry(
                Pair.of("<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"),
                List.of(RawMethod.AccessFlag.PUBLIC)));
    methodAccessFlagTestCases.forEach(
        (methodInfo, accessFlagList) -> {
          Optional<RawMethod> method =
              actual.getMethods().findMemberBy(methodInfo.getLeft(), methodInfo.getRight());
          assertThat(method).isNotEmpty();
          assertThat(method.get().getAccessFlag())
              .containsExactlyInAnyOrderElementsOf(accessFlagList);
        });
    var staticMethodAccessFlagTestCases =
        Map.ofEntries(
            entry(
                Pair.of("main", "()V"),
                List.of(RawMethod.AccessFlag.STATIC, RawMethod.AccessFlag.PUBLIC)),
            entry(Pair.of("fibonacchi", "(I)I"), List.of(RawMethod.AccessFlag.STATIC)));
    staticMethodAccessFlagTestCases.forEach(
        (methodInfo, accessFlagList) -> {
          Optional<RawMethod> method =
              actual.getMethods().findStaticBy(methodInfo.getLeft(), methodInfo.getRight());
          assertThat(method).isNotEmpty();
          assertThat(method.get().getAccessFlag())
              .containsExactlyInAnyOrderElementsOf(accessFlagList);
        });

    assertThat(actual.getAttrs().getEntrySize()).isEqualTo(1);

    assertThat(actual.getClassLoaderName()).isNull();
    assertThat(actual.getName()).isEqualTo("RichClass");
    assertThat(actual.getFullyQualifiedName()).isEqualTo("dev.ishikawa.test.RichClass");
    assertThat(actual.getFilename()).isEqualTo("RichClass.java");
  }
}
