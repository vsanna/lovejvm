package dev.ishikawa.lovejvm.rawclass.constantpool.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ConstantPoolTag {
    CLASS((byte)0x07),
    FIELD_REF((byte)0x09),
    METHOD_REF((byte)0x0A),
    INTERFACE_METHOD_REF((byte)0x0B),
    STRING((byte)0x08),
    INTEGER((byte)0x03),
    FLOAT((byte)0x04),
    LONG((byte)0x05),
    DOUBLE((byte)0x06),
    NAME_AND_TYPE((byte)0x0C),
    UTF8((byte)0x01),
    METHOD_HANDLE((byte)0x0F),
    METHOD_TYPE((byte)0x10),
    DYNAMIC((byte)0x11),
    INVOKE_DYNAMIC((byte)0x12),
    MODULE((byte)0x13),
    PACKAGE((byte)0x14);

    private byte tag;

    ConstantPoolTag(byte tag) {
        this.tag = tag;
    }

    public byte getTag() {
        return tag;
    }

    private static Map<Byte, ConstantPoolTag> entryMap = Arrays.stream(ConstantPoolTag.values())
            .collect(Collectors.toMap(ConstantPoolTag::getTag, Function.identity()));

    public static ConstantPoolTag findBy(byte tag) {
        return Optional.ofNullable(entryMap.get(tag))
                .orElseThrow(() -> new RuntimeException(String.format("invalid tag: %x", tag)));
    }
}
