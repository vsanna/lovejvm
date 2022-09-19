package dev.ishikawa.lovejvm.klass.parser;

import dev.ishikawa.lovejvm.klass.*;
import dev.ishikawa.lovejvm.klass.constantpool.*;
import dev.ishikawa.lovejvm.klass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.klass.constantpool.entity.ConstantPoolEntry;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class LClassParser {
    private byte[] bytecode;
    private int pointer = 0;
    private ConstantPool constantPool;


    public LClassParser(byte[] bytecode) {
        this.bytecode = bytecode;
        this.pointer = 0;
    }

    public LClass parse() {
        if(!hasValidMagicWord()) throw new RuntimeException("invalid magic word");

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
        var fullyQualifiedName = parseFullyQualifiedName();
        var fileName = retrieveFileName(classAttrs);

        return new LClass(
                bytecode,
                fullyQualifiedName,
                className,
                fileName,
                "dummyClassLoader",
                minorVersion,
                majorVersion,
                constantPool,
                accessFlag,
                thisClass,
                superClass,
                interfaces,
                fields,
                methods,
                classAttrs
        );
    }

    private boolean hasValidMagicWord() {
        boolean hasValidMagicWord = bytecode[0] == (byte)0xCA
                && bytecode[1] == (byte)0xFE
                && bytecode[2] == (byte)0xBA
                && bytecode[3] == (byte)0xBE;
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
            // parse constant pool entry
            var result = ConstantPoolEntryParser.parse(pointer, bytecode);
            pointer = result.getLeft();
            entries.add(result.getRight());
        }

        return new ConstantPool(entrySize, entries);
    }

    private int parseAccessFlag() {
        var accessFlag = ByteUtil.concat(bytecode[pointer], bytecode[pointer+1]);
        pointer += 2;
        return accessFlag;
    }

    private ConstantClass parseClassField() {
        var index = ByteUtil.concat(bytecode[pointer], bytecode[pointer+1]);
        pointer += 2;
        var entry = constantPool.findByIndex(index);

        if(!(entry instanceof ConstantClass)) {
            throw new RuntimeException("unexpected entry is returned. ");
        }

        return (ConstantClass) entry;
    }

    private Interfaces parseInterface() {
        var entrySize = ByteUtil.concat(bytecode[pointer], bytecode[pointer+1]);
        pointer += 2;
        List<LInterface> entries = new ArrayList<>(entrySize);

        for (int i = 0; i < entrySize; i++) {
            // TODO: impl
        }

        return new Interfaces(entrySize, entries);
    }

    private Fields parseFields() {
        var entrySize = ByteUtil.concat(bytecode[pointer], bytecode[pointer+1]);
        pointer += 2;
        List<LField> entries = new ArrayList<>(entrySize);

        for (int i = 0; i < entrySize; i++) {
            // TODO: verify
            var result = LFieldParser.parse(pointer, bytecode, constantPool);
            pointer = result.getLeft();
            entries.add(result.getRight());
        }

        return new Fields(entrySize, entries);
    }

    private Methods parseMethods() {
        var entrySize = ByteUtil.concat(bytecode[pointer], bytecode[pointer+1]);
        pointer += 2;
        List<LMethod> entries = new ArrayList<>(entrySize);

        for (int i = 0; i < entrySize; i++) {
            // TODO: verify
            var result = LMethodParser.parse(pointer, bytecode, constantPool);
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

    private String retrieveClassName(ConstantClass thisClass) {
        return thisClass.getName().getLabel();
    }

    /**
     * > There may be at most one SourceFile attribute in the attributes table of a ClassFile structure.
     * */
    private String retrieveFileName(Attrs classAttrs) {
        return classAttrs.findAllBy(AttrName.SOURCE_FILE).stream()
                .findFirst()
                .map((it) -> ((LAttrSourceFile)it).getSourceFileName())
                .orElse("N/A");
    }

    private String parseClassName() {
        return "hoge"; // TODO
    }

    private String parseFullyQualifiedName() {
        return "hello"; // TODO. pack name should be given somehow
    }
}
