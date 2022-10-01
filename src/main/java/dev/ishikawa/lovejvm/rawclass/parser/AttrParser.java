package dev.ishikawa.lovejvm.rawclass.parser;


import dev.ishikawa.lovejvm.rawclass.attr.*;
import dev.ishikawa.lovejvm.rawclass.attr.AttrBootstrapMethods.LAttrBootstrapMethodsBody;
import dev.ishikawa.lovejvm.rawclass.attr.AttrBootstrapMethods.LAttrBootstrapMethodsBody.LAttrBootstrapMethod;
import dev.ishikawa.lovejvm.rawclass.attr.AttrExceptions.LAttrExceptionsBody;
import dev.ishikawa.lovejvm.rawclass.attr.AttrInnerClass.AttrInnerClassBody.LAttrInnerClassEntry;
import dev.ishikawa.lovejvm.rawclass.attr.AttrNestMembers.LAttrNestMembersBody;
import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.*;
import dev.ishikawa.lovejvm.rawclass.method.exceptionhandler.ExceptionInfoTable;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.util.Pair;
import java.util.ArrayList;

public class AttrParser {
  public static Pair<Integer, Attr> parseAttrData(
      int pointer, byte[] bytecode, ConstantPool constantPool) {
    // attrName
    var index = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
    var attrName = (ConstantUtf8) constantPool.findByIndex(index);
    pointer += 2;

    // attrSize
    var dataLength =
        ByteUtil.concatToInt(
            bytecode[pointer], bytecode[pointer + 1], bytecode[pointer + 2], bytecode[pointer + 3]);
    pointer += 4;

    // parse attrBody according to attrName
    var attr = parseAttrData(attrName, dataLength, pointer, bytecode, constantPool);

    return Pair.of(pointer + dataLength, attr);
  }

  private static Attr parseAttrData(
      ConstantUtf8 attrName,
      int dataLength,
      int pointer,
      byte[] bytecode,
      ConstantPool constantPool) {
    var attrNameEnum = AttrName.findBy(attrName.getLabel());
    switch (attrNameEnum) {
      case CONSTANT_VALUE:
        {
          var index = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
          return new AttrConstantValue(attrName, dataLength, constantPool.findByIndex(index));
        }
      case CODE:
        {
          var operandStackSize = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
          pointer += 2;

          var localsSize = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
          pointer += 2;

          var instructionLength =
              ByteUtil.concatToInt(
                  bytecode[pointer],
                  bytecode[pointer + 1],
                  bytecode[pointer + 2],
                  bytecode[pointer + 3]);
          pointer += 4;

          byte[] instructionSection = new byte[instructionLength];
          System.arraycopy(bytecode, pointer, instructionSection, 0, instructionLength);
          pointer += instructionLength;

          var parseExceptionHandlersResult =
              ExceptionHandlersParser.parse(pointer, bytecode, constantPool);
          pointer = parseExceptionHandlersResult.getLeft();
          ExceptionInfoTable exceptionInfoTable = parseExceptionHandlersResult.getRight();

          var parseAttrsResult = AttrsParser.parse(pointer, bytecode, constantPool);
          pointer = parseAttrsResult.getLeft();
          var attrs = parseAttrsResult.getRight();

          return AttrCode.of(
              attrName,
              dataLength,
              operandStackSize,
              localsSize,
              instructionLength,
              exceptionInfoTable,
              attrs);
        }
      case EXCEPTIONS:
        {
          var numberOfExceptions = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
          pointer += 2;
          var exceptionIndexTable = new ArrayList<ConstantClass>(numberOfExceptions);

          for (int i = 0; i < numberOfExceptions; ++i) {
            var idx = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
            pointer += 2;
            exceptionIndexTable.add((ConstantClass) constantPool.findByIndex(idx));
          }

          return new AttrExceptions(
              attrName,
              dataLength,
              new LAttrExceptionsBody(numberOfExceptions, exceptionIndexTable));
        }
      case SOURCE_FILE:
        {
          var index = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
          return new AttrSourceFile(attrName, dataLength, constantPool.findByIndex(index));
        }
      case LINE_NUMBER_TABLE:
        {
          var entrySize = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
          pointer += 2;
          var entries = new ArrayList<AttrLineNumberTable.LAttrLineNumberTableEntry>(entrySize);
          for (int i = 0; i < entrySize; i++) {
            var instructionLine = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
            pointer += 2;
            var originalLine = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
            pointer += 2;
            entries.add(
                new AttrLineNumberTable.LAttrLineNumberTableEntry(instructionLine, originalLine));
          }

          return new AttrLineNumberTable(attrName, dataLength, entries);
        }
        //      case LOCAL_VARIABLE_TABLE:
        //        break;
      case INNER_CLASSES:
        {
          var numberOfClasses = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
          pointer += 2;

          var classes = new ArrayList<LAttrInnerClassEntry>(numberOfClasses);
          for (int i = 0; i < numberOfClasses; i++) {
            var innerClassInfoIdx =
                ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
            ConstantClass innerClassInfo =
                (ConstantClass) constantPool.findByIndex(innerClassInfoIdx);
            pointer += 2;

            var outerClassInfoIdx =
                ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
            ConstantClass outerClassInfo = null;
            if (outerClassInfoIdx > 0) {
              outerClassInfo = (ConstantClass) constantPool.findByIndex(outerClassInfoIdx);
            }
            pointer += 2;

            var innerNameIdx = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
            ConstantUtf8 innerName = null;
            if (innerNameIdx > 0) {
              innerName = (ConstantUtf8) constantPool.findByIndex(innerNameIdx);
            }
            pointer += 2;

            var accessFlags = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
            pointer += 2;

            classes.add(
                new LAttrInnerClassEntry(innerClassInfo, outerClassInfo, innerName, accessFlags));
          }

          return new AttrInnerClass(
              attrName,
              dataLength,
              new AttrInnerClass.AttrInnerClassBody(numberOfClasses, classes));
        }
        //      case SYNTHETIC:
        //        break;
      case DEPRECATED:
        {
          return new AttrDeprecated(attrName, dataLength);
        }
        //      case ENCLOSING_METHOD:
        //        break;
      case SIGNATURE:
        {
          var signatureIndex = bytecode[pointer];
          return new AttrSignature(attrName, dataLength, signatureIndex);
        }
        //      case SOURCE_DEBUG_EXTENSION:
        //        break;
        //      case LOCAL_VARIABLE_TYPE_TABLE:
        //        break;
      case RUNTIME_VISIBLE_ANNOTATIONS:
        {
          var result = AnnotationParser.parseList(pointer, bytecode, constantPool);
          var entries = result.getRight();
          return new AttrRuntimeVisibleAnnotations(attrName, dataLength, entries);
        }
      case RUNTIME_INVISIBLE_ANNOTATIONS:
        {
          var result = AnnotationParser.parseList(pointer, bytecode, constantPool);
          var entries = result.getRight();
          return new AttrRuntimeInvisibleAnnotations(attrName, dataLength, entries);
        }
        //      case RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS:
        //        break;
        //      case RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS:
        //        break;
        //      case ANNOTATION_DEFAULT:
        //        break;
      case STACK_MAP_TABLE:
        {
          return new AttrDummy(attrName, dataLength, "STACK_MAP_TABLE");
        }
      case BOOTSTRAP_METHODS:
        {
          var numBootstrapMethods =
              ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
          pointer += 2;

          var bootstrapMethods = new ArrayList<LAttrBootstrapMethod>(numBootstrapMethods);
          for (int i = 0; i < numBootstrapMethods; i++) {
            var idx = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
            ConstantMethodHandle bootstrapMethodRef =
                (ConstantMethodHandle) constantPool.findByIndex(idx);
            pointer += 2;

            var numBootstrapArguments =
                ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
            pointer += 2;

            var bootstrapArguments = new ArrayList<ConstantPoolEntry>(numBootstrapArguments);
            for (int j = 0; j < numBootstrapArguments; j++) {
              var argumentIdx = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
              pointer += 2;
              var bootstrapArgument = constantPool.findByIndex(argumentIdx);
              bootstrapArguments.add(bootstrapArgument);
            }

            bootstrapMethods.add(
                new LAttrBootstrapMethod(
                    bootstrapMethodRef, numBootstrapArguments, bootstrapArguments));
          }

          return new AttrBootstrapMethods(
              attrName,
              dataLength,
              new LAttrBootstrapMethodsBody(numBootstrapMethods, bootstrapMethods));
        }
        //      case RUNTIME_VISIBLE_TYPE_ANNOTATIONS:
        //        break;
        //      case RUNTIME_INVISIBLE_TYPE_ANNOTATIONS:
        //        break;
        //      case METHOD_PARAMETERS:
        //        break;
        //      case MODULE:
        //        break;
        //      case MODULE_PACKAGES:
        //        break;
        //      case MODULE_MAIN_CLASS:
        //        break;
      case NEST_HOST:
        {
          var idx = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
          var constantClass = (ConstantClass) constantPool.findByIndex(idx);
          return new AttrNestHost(attrName, dataLength, constantClass);
        }
      case NEST_MEMBERS:
        {
          var numberOfClasses = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
          pointer += 2;

          var classes = new ArrayList<ConstantClass>(numberOfClasses);
          for (int i = 0; i < numberOfClasses; i++) {
            var classIdx = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
            pointer += 2;
            var classInfo = (ConstantClass) constantPool.findByIndex(classIdx);
            classes.add(classInfo);
          }

          return new AttrNestMembers(
              attrName, dataLength, new LAttrNestMembersBody(numberOfClasses, classes));
        }
        //      case RECORD:
        //        break;
        //      case PERMITTED_SUBCLASSES:
        //        break;
      default:
        throw new RuntimeException(String.format("unsupported attr is passed: %s", attrNameEnum));
    }
  }
}
