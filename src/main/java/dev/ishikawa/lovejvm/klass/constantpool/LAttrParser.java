package dev.ishikawa.lovejvm.klass.constantpool;

import dev.ishikawa.lovejvm.klass.*;
import dev.ishikawa.lovejvm.klass.constantpool.entity.*;
import dev.ishikawa.lovejvm.klass.parser.ExceptionHandlersParser;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.util.Pair;

import java.util.ArrayList;

public class LAttrParser {
    public static Pair<Integer, LAttr> parse(int pointer, byte[] bytecode, ConstantPool constantPool) {
        // attrName
        var index = ByteUtil.concat(bytecode[pointer], bytecode[pointer+1]);
        var attrName = (ConstantUtf8) constantPool.findByIndex(index);
        pointer += 2;

        // attrSize
        var dataLength = ByteUtil.concat(
                bytecode[pointer],
                bytecode[pointer+1],
                bytecode[pointer+2],
                bytecode[pointer+3]
        );
        pointer += 4;

        // parse attrBody according to attrName
        var attr = parse(attrName, dataLength, pointer, bytecode, constantPool);

        return Pair.of(pointer + dataLength, attr);
    }

    private static LAttr parse(ConstantUtf8 attrName, int dataLength, int pointer, byte[] bytecode, ConstantPool constantPool) {
        var attrNameEnum = AttrName.findBy(attrName.getLabel());
        switch (attrNameEnum) {
            case CONSTANT_VALUE: {
                var index = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]);
                return new LAttrConstantValue(attrName, dataLength, constantPool.findByIndex(index));
            }
            case CODE: {
                // TODO create LCodeParser and move these logics to it
                var operandStackSize = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]);
                pointer += 2;

                var localsSize = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]);
                pointer += 2;

                var instructionLength = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1], bytecode[pointer + 2], bytecode[pointer + 3]);
                pointer += 4;

                byte[] instructionSection = new byte[instructionLength];
                System.arraycopy(bytecode, pointer, instructionSection, 0, instructionLength);
                pointer += instructionLength;

                var parseExceptionHandlersResult = ExceptionHandlersParser.parse(pointer, bytecode, constantPool);
                pointer = parseExceptionHandlersResult.getLeft();
                ExceptionHandlers exceptionHandlers = parseExceptionHandlersResult.getRight();

                var parseAttrsResult = AttrsParser.parse(pointer, bytecode, constantPool);
                pointer = parseAttrsResult.getLeft();
                var attrs = parseAttrsResult.getRight();

                return LAttrCode.of(attrName, dataLength, operandStackSize, localsSize, instructionLength, instructionSection, exceptionHandlers, attrs);
            }
            case STACK_MAP_TABLE:
                return new LAttrDummy(attrName, dataLength, "STACK_MAP_TABLE");
//            case BOOTSTRAP_METHODS:
//                break;
//            case NEST_HOST:
//                break;
//            case NEST_MEMBERS:
//                break;
//            case PERMITTED_SUBCLASSES:
//                break;
//            case EXCEPTIONS:
//                break;
//            case INNER_CLASSES:
//                break;
//            case ENCLOSING_METHOD:
//                break;
//            case SYNTHETIC:
//                break;
//            case SIGNATURE:
//                break;
//            case RECORD:
//                break;
            case SOURCE_FILE: {
                var index = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]);
                return new LAttrSourceFile(attrName, dataLength, constantPool.findByIndex(index));
            }
            case LINE_NUMBER_TABLE: {
                var entrySize = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]);
                pointer += 2;
                var entries = new ArrayList<LAttrLineNumberTable.LAttrLineNumberTableEntry>(entrySize);
                for (int i = 0; i < entrySize; i++) {
                    var instructionLine = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]);
                    pointer += 2;
                    var originalLine = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]);
                    pointer += 2;
                    entries.add(new LAttrLineNumberTable.LAttrLineNumberTableEntry(instructionLine, originalLine));
                }

                return new LAttrLineNumberTable(attrName, dataLength, entries);
            }
//            case LOCAL_VARIABLE_TABLE:
//                break;
//            case LOCAL_VARIABLE_TYPE_TABLE:
//                break;
//            case SOURCE_DEBUG_EXTENSION:
//                break;
//            case RUNTIME_VISIBLE_ANNOTATIONS:
//                break;
//            case RUNTIME_INVISIBLE_ANNOTATIONS:
//                break;
//            case RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS:
//                break;
//            case RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS:
//                break;
//            case RUNTIME_VISIBLE_TYPE_ANNOTATIONS:
//                break;
//            case RUNTIME_INVISIBLE_TYPE_ANNOTATIONS:
//                break;
//            case ANNOTATION_DEFAULT:
//                break;
//            case METHOD_PARAMETERS:
//                break;
//            case MODULE:
//                break;
//            case MODULE_PACKAGES:
//                break;
//            case MODULE_MAIN_CLASS:
//                break;
            default:
                throw new RuntimeException(String.format("unsupported attr is passed: %s", attrNameEnum));
        }
    }
}
