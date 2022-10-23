package dev.ishikawa.lovejvm.rawclass.parser;


import dev.ishikawa.lovejvm.rawclass.constantpool.entity.*;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.util.Pair;

public class ConstantPoolEntryParser {
  public static Pair<Integer, ConstantPoolEntry> parse(int pointer, byte[] bytecode) {
    var tag = ConstantPoolTag.findBy(bytecode[pointer]);

    switch (tag) {
      case CLASS:
        {
          var nameIndex = ByteUtil.concatToShort(bytecode[pointer + 1], bytecode[pointer + 2]);
          return Pair.of(pointer + 3, new ConstantClass(nameIndex));
        }
      case FIELD_REF:
        {
          var classIndex = ByteUtil.concatToShort(bytecode[pointer + 1], bytecode[pointer + 2]);
          var nameAndTypeIndex =
              ByteUtil.concatToShort(bytecode[pointer + 3], bytecode[pointer + 4]);
          return Pair.of(pointer + 5, new ConstantFieldref(classIndex, nameAndTypeIndex));
        }
      case METHOD_REF:
        {
          var classIndex = ByteUtil.concatToShort(bytecode[pointer + 1], bytecode[pointer + 2]);
          var nameAndTypeIndex =
              ByteUtil.concatToShort(bytecode[pointer + 3], bytecode[pointer + 4]);
          return Pair.of(pointer + 5, new ConstantMethodref(classIndex, nameAndTypeIndex));
        }
      case INTERFACE_METHOD_REF:
        {
          var classIndex = ByteUtil.concatToShort(bytecode[pointer + 1], bytecode[pointer + 2]);
          var nameAndTypeIndex =
              ByteUtil.concatToShort(bytecode[pointer + 3], bytecode[pointer + 4]);
          return Pair.of(pointer + 5, new ConstantInterfaceMethodref(classIndex, nameAndTypeIndex));
        }
      case STRING:
        {
          var stringIndex = ByteUtil.concatToShort(bytecode[pointer + 1], bytecode[pointer + 2]);
          return Pair.of(pointer + 3, new ConstantString(stringIndex));
        }
      case INTEGER:
        {
          int intValue =
              ByteUtil.concatToInt(
                  bytecode[pointer + 1],
                  bytecode[pointer + 2],
                  bytecode[pointer + 3],
                  bytecode[pointer + 4]);
          return Pair.of(pointer + 5, new ConstantInteger(intValue));
        }
      case FLOAT:
        {
          int bits =
              ByteUtil.concatToInt(
                  bytecode[pointer + 1],
                  bytecode[pointer + 2],
                  bytecode[pointer + 3],
                  bytecode[pointer + 4]);
          float floatValue = Float.intBitsToFloat(bits);
          return Pair.of(pointer + 5, new ConstantFloat(floatValue));
        }
      case LONG:
        {
          long longValue =
              ByteUtil.concatToLong(
                  bytecode[pointer + 1],
                  bytecode[pointer + 2],
                  bytecode[pointer + 3],
                  bytecode[pointer + 4],
                  bytecode[pointer + 5],
                  bytecode[pointer + 6],
                  bytecode[pointer + 7],
                  bytecode[pointer + 8]);
          return Pair.of(pointer + 9, new ConstantLong(longValue));
        }
      case DOUBLE:
        {
          long bits =
              ByteUtil.concatToLong(
                  bytecode[pointer + 1],
                  bytecode[pointer + 2],
                  bytecode[pointer + 3],
                  bytecode[pointer + 4],
                  bytecode[pointer + 5],
                  bytecode[pointer + 6],
                  bytecode[pointer + 7],
                  bytecode[pointer + 8]);
          double doubleValue = Double.longBitsToDouble(bits);
          return Pair.of(pointer + 9, new ConstantDouble(doubleValue));
        }
      case NAME_AND_TYPE:
        {
          var nameIndex = ByteUtil.concatToShort(bytecode[pointer + 1], bytecode[pointer + 2]);
          var descriptorIndex =
              ByteUtil.concatToShort(bytecode[pointer + 3], bytecode[pointer + 4]);
          return Pair.of(pointer + 5, new ConstantNameAndType(nameIndex, descriptorIndex));
        }
      case UTF8:
        {
          var labelLength = ByteUtil.concatToShort(bytecode[pointer + 1], bytecode[pointer + 2]);
          pointer += 3;
          byte[] bytes = new byte[labelLength];
          System.arraycopy(bytecode, pointer, bytes, 0, labelLength);

          return Pair.of(pointer + labelLength, new ConstantUtf8(new String(bytes)));
        }
      case METHOD_HANDLE:
        {
          /* u1: tag, u1: reference_kind, u2: reference_index */
          var referenceKind = bytecode[pointer + 1];
          var referenceIndex = ByteUtil.concatToShort(bytecode[pointer + 2], bytecode[pointer + 3]);
          return Pair.of(pointer + 4, new ConstantMethodHandle(referenceKind, referenceIndex));
        }
      case METHOD_TYPE:
        {
          /* u1: tag, u2: descriptor_index */
          var describtorIndex =
              ByteUtil.concatToShort(bytecode[pointer + 1], bytecode[pointer + 2]);
          return Pair.of(pointer + 3, new ConstantMethodType(describtorIndex));
        }
      case DYNAMIC:
        {
          /* u1: tag, u2: bootstrap_method_attr_index, u2: name_and_type_index */
          var bootstrapMethodIndex =
              ByteUtil.concatToShort(bytecode[pointer + 1], bytecode[pointer + 2]);
          var nameAndTypeIndex =
              ByteUtil.concatToShort(bytecode[pointer + 3], bytecode[pointer + 4]);
          return Pair.of(pointer + 5, new ConstantDynamic(bootstrapMethodIndex, nameAndTypeIndex));
        }
      case INVOKE_DYNAMIC:
        {
          /* u1: tag, u2: bootstrap_method_attr_index, u2: name_and_type_index */
          var bootstrapMethodIndex =
              ByteUtil.concatToShort(bytecode[pointer + 1], bytecode[pointer + 2]);
          var nameAndTypeIndex =
              ByteUtil.concatToShort(bytecode[pointer + 3], bytecode[pointer + 4]);
          return Pair.of(
              pointer + 5, new ConstantInvokeDynamic(bootstrapMethodIndex, nameAndTypeIndex));
        }
      case MODULE:
        {
          /* u1: tag, u1: name_index */
          var nameIndex = ByteUtil.concatToShort(bytecode[pointer + 1], bytecode[pointer + 2]);
          return Pair.of(pointer + 3, new ConstantModule(nameIndex));
        }
      case PACKAGE:
      default:
        throw new RuntimeException(
            String.format("invalid Constant Pool Entity tag: 0x%x", tag.getTag()));
    }
  }
}
