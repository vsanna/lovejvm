package dev.ishikawa.lovejvm.rawclass.parser;


import dev.ishikawa.lovejvm.rawclass.*;
import dev.ishikawa.lovejvm.rawclass.attr.AttrName;
import dev.ishikawa.lovejvm.rawclass.attr.AttrSourceFile;
import dev.ishikawa.lovejvm.rawclass.attr.Attrs;
import dev.ishikawa.lovejvm.rawclass.constantpool.*;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantPoolEntry;
import dev.ishikawa.lovejvm.rawclass.field.Fields;
import dev.ishikawa.lovejvm.rawclass.field.RawField;
import dev.ishikawa.lovejvm.rawclass.linterface.Interfaces;
import dev.ishikawa.lovejvm.rawclass.linterface.RawInterface;
import dev.ishikawa.lovejvm.rawclass.method.Methods;
import dev.ishikawa.lovejvm.rawclass.method.RawMethod;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class RawClassParser {
  private final byte[] bytecode;
  private int pointer = 0;
  private ConstantPool constantPool;
  private final @Nullable String loaderName;

  public RawClassParser(byte[] bytecode, String loaderName) {
    this.bytecode = bytecode;
    this.loaderName = loaderName;
    this.pointer = 0;
  }

  /** parse does parsing the given binary and make a RawClass object */
  public RawClass parse() {
    if (!hasValidMagicWord()) throw new RuntimeException("invalid magic word");

    int minorVersion = parseMinorVersion();
    int majorVersion = parseMajorVersion();

    this.constantPool = parseConstantPool();

    int accessFlag = parseAccessFlag();
    var thisClass = parseClassField();
    var superClass = parseClassField();

    var interfaces = parseInterface();
    var fields = parseFields();
    var methods = parseMethods();

    var classAttrs = parseClassAttrs();

    var className = retrieveClassName(thisClass);
    var fullyQualifiedName = parseFullyQualifiedName(thisClass);
    var fileName = retrieveFileName(classAttrs);
    var classLabel = thisClass.getName().getLabel();

    return new RawClass(
        bytecode,
        fullyQualifiedName,
        className,
        fileName,
        classLabel,
        this.getLoaderName(),
        minorVersion,
        majorVersion,
        constantPool,
        accessFlag,
        thisClass,
        superClass,
        interfaces,
        fields,
        methods,
        classAttrs);
  }

  private boolean hasValidMagicWord() {
    boolean hasValidMagicWord =
        bytecode[0] == (byte) 0xCA
            && bytecode[1] == (byte) 0xFE
            && bytecode[2] == (byte) 0xBA
            && bytecode[3] == (byte) 0xBE;
    pointer = 4;
    return hasValidMagicWord;
  }

  private int parseMinorVersion() {
    var minorVersion = ByteUtil.concat(bytecode[4], bytecode[5]);
    pointer = 6;
    return minorVersion;
  }

  private int parseMajorVersion() {
    var majorVersion = ByteUtil.concat(bytecode[6], bytecode[7]);
    pointer = 8;
    return majorVersion;
  }

  private ConstantPool parseConstantPool() {
    var entrySize = ByteUtil.concat(bytecode[8], bytecode[9]);
    pointer = 10;
    List<ConstantPoolEntry> entries = new ArrayList<>(entrySize);

    // entrySize = actual num of entries - 1
    // 1-index
    // TODO: research why
    for (int i = 0; i < entrySize - 1; i++) {
      var result = ConstantPoolEntryParser.parse(pointer, bytecode);
      pointer = result.getLeft();
      entries.add(result.getRight());
    }

    return new ConstantPool(entrySize, entries);
  }

  private int parseAccessFlag() {
    var accessFlag = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]);
    pointer += 2;
    return accessFlag;
  }

  private ConstantClass parseClassField() {
    var index = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]);
    pointer += 2;
    var entry = constantPool.findByIndex(index);

    if (!(entry instanceof ConstantClass)) {
      throw new RuntimeException("unexpected entry is returned. ");
    }

    return (ConstantClass) entry;
  }

  private Interfaces parseInterface() {
    var entrySize = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]);
    pointer += 2;
    List<RawInterface> entries = new ArrayList<>(entrySize);

    for (int i = 0; i < entrySize; i++) {
      var result = new RawInterface(parseClassField());
      entries.add(result);
    }

    return new Interfaces(entrySize, entries);
  }

  private Fields parseFields() {
    var entrySize = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]);
    pointer += 2;
    List<RawField> entries = new ArrayList<>(entrySize);

    for (int i = 0; i < entrySize; i++) {
      var result = RawFieldParser.parse(pointer, bytecode, constantPool);
      pointer = result.getLeft();
      entries.add(result.getRight());
    }

    return new Fields(entrySize, entries);
  }

  private Methods parseMethods() {
    var entrySize = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]);
    pointer += 2;
    List<RawMethod> entries = new ArrayList<>(entrySize);

    for (int i = 0; i < entrySize; i++) {
      var result = RawMethodParser.parse(pointer, bytecode, constantPool);
      pointer = result.getLeft();
      entries.add(result.getRight());
    }

    return new Methods(entrySize, entries);
  }

  private Attrs parseClassAttrs() {
    Pair<Integer, Attrs> parseAttrsResult = AttrsParser.parse(pointer, bytecode, constantPool);
    pointer = parseAttrsResult.getLeft();
    return parseAttrsResult.getRight();
  }

  // ex: Object, App,
  private String retrieveClassName(ConstantClass thisClass) {
    String[] words = thisClass.getName().getLabel().split("/");
    return words[words.length - 1];
  }

  /**
   * > There may be at most one SourceFile attribute in the attributes table of a ClassFile >
   * structure.
   */
  private String retrieveFileName(Attrs classAttrs) {
    return classAttrs.findAllBy(AttrName.SOURCE_FILE).stream()
        .findFirst()
        .map((it) -> ((AttrSourceFile) it).getSourceFileName())
        .orElse("N/A");
  }

  // ex. dev.ishikawa.app.MyApp
  private String parseFullyQualifiedName(ConstantClass thisClass) {
    String[] words = thisClass.getName().getLabel().split("/");
    return String.join(".", Arrays.asList(words));
  }

  public @Nullable String getLoaderName() {
    return loaderName;
  }
}
