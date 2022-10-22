package dev.ishikawa.lovejvm.vm;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.type.JvmType;
import dev.ishikawa.lovejvm.rawclass.type.RawArrayClass;
import dev.ishikawa.lovejvm.rawobject.RawObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InvokeHelper {
  public static final InvokeHelper INSTANCE = new InvokeHelper();

  private final MethodTypeFactory METHOD_TYPE_HANDLER = new MethodTypeFactory();

  public int createMethodTypeObject(String descriptor) {
    return METHOD_TYPE_HANDLER.createMethodTypeObject(descriptor);
  }

  private final MethodHandleFactory METHOD_HANDLE_FACTORY = new MethodHandleFactory();

  public int createMethodHandleObject() {
    return METHOD_HANDLE_FACTORY.createMethodHandle();
  }

  static class MethodHandleFactory {
    private static final String METHOD_HANDLE_BINARY_NAME = "java/lang/invoke/MethodHandle";

    private final InvokeHelper invokeHelper = InvokeHelper.INSTANCE;

    public int createMethodHandle() {
      return 0;
    }
  }

  // These logics should be in MethodType in SDK
  static class MethodTypeFactory {
    private static final String METHOD_TYPE_BINARY_NAME = "java/lang/invoke/MethodType";

    private RawClass methodTypeRawClass;

    public int createMethodTypeObject(String descriptor) {
      return createMethodTypeObject(
          descriptor, getRTypeClassObjectId(descriptor), getPTypeClassObjectIds(descriptor));
    }

    private int getRTypeClassObjectId(String descriptor) {
      String rTypeBinaryName = InvokeHelper.INSTANCE.parseRType(descriptor);
      RawClass rawClass = InvokeHelper.INSTANCE.mapType(rTypeBinaryName);
      return rawClass.getClassObjectId();
    }

    private List<Integer> getPTypeClassObjectIds(String descriptor) {
      List<String> pTypeBinaryNames = InvokeHelper.INSTANCE.parsePTypes(descriptor);
      return pTypeBinaryNames.stream()
          .map(it -> InvokeHelper.INSTANCE.mapType(it).getClassObjectId())
          .collect(Collectors.toList());
    }

    private int createMethodTypeObject(
        String descriptor, int rTypeClassObjectId, List<Integer> pTypeClassObjectIds) {
      if (methodTypeRawClass == null) {
        methodTypeRawClass = RawSystem.methodAreaManager.lookupOrLoadClass(METHOD_TYPE_BINARY_NAME);
      }

      int objectId = RawSystem.heapManager.newObject(methodTypeRawClass);
      RawObject methodTypeObject = RawSystem.heapManager.lookupObject(objectId);

      // set descriptor
      RawSystem.heapManager.setValue(
          methodTypeObject,
          "descriptor",
          List.of(Word.of(RawSystem.stringPool.getOrCreate(descriptor))));

      // set rType
      RawSystem.heapManager.setValue(
          methodTypeObject, "rtype", List.of(Word.of(rTypeClassObjectId)));

      // set pTypes
      RawClass classArrayClass =
          RawSystem.methodAreaManager.lookupOrLoadClass("[Ljava/lang/Class;");
      int classArrayRawObjectId =
          RawSystem.heapManager.newArrayObject(
              ((RawArrayClass) classArrayClass), pTypeClassObjectIds.size());
      RawObject classArrayRawObject = RawSystem.heapManager.lookupObject(classArrayRawObjectId);
      for (int i = 0; i < pTypeClassObjectIds.size(); i++) {
        RawSystem.heapManager.setElement(
            classArrayRawObject, i, List.of(Word.of(pTypeClassObjectIds.get(i))));
      }
      RawSystem.heapManager.setValue(
          methodTypeObject, "ptypes", List.of(Word.of(classArrayRawObjectId)));

      return objectId;
    }
  }

  /**
   * @return Parameter Descriptor (ex. Ljava/lang/String;)
   * */
  public String parseRType(String descriptor) {
    // ex: ([Ljava/lang/String;[[IDLjava/lang/String;II)V -> [, [, D, L, I, I
    return descriptor.split("\\(")[1].split("\\)")[1];
  }

  /**
   * @return a List of Parameter Descriptor (ex. Ljava/lang/String;)
   * */
  public List<String> parsePTypes(String descriptor) {
    // ex: ([Ljava/lang/String;[[IDLjava/lang/String;II)V -> [, [, D, L, I, I
    var argumentString = descriptor.split("\\(")[1].split("\\)")[0];
    var argumentCharArray = argumentString.toCharArray();
    List<String> argumentTypes = new ArrayList<>();

    for (int i = 0; i < argumentCharArray.length; i++) {
      int start = i;
      int end = -1;

      WHILE:
      while (true) {
        char c = argumentCharArray[i];
        switch (c) {
          case '[':
            {
              i++;
              break;
            }
          case 'L':
            {
              int j = 1;
              while (argumentCharArray[i + j] != ';') {
                j++;
              }
              i = i + j;
              end = i;
              break WHILE;
            }
          case 'I':
          case 'J':
          case 'F':
          case 'D':
          case 'B':
          case 'S':
          case 'Z':
          case 'C':
          case 'V':
            {
              // argumentTypesにpushして次へ
              end = i;
              break WHILE;
            }
          default:
            throw new RuntimeException("invalid parsing");
        }
      }

      argumentTypes.add(argumentString.substring(start, end + 1));
    }

    return argumentTypes;
  }

  private RawClass mapType(String paramDescriptor) {
    String binaryName = paramDescriptorToBinaryName(paramDescriptor);
    String binaryNameToLookup =
        Optional.ofNullable(JvmType.findByJvmSignature(binaryName))
            .map(JvmType::getBoxTypeBinaryName)
            .orElse(binaryName);

    return RawSystem.methodAreaManager.lookupOrLoadClass(binaryNameToLookup);
  }

  /**
   * Ljava/lang/String; -> java/langString
   * [[T -> [[T
   * T -> t
   * */
  private String paramDescriptorToBinaryName(String paramDescriptor) {
    if (paramDescriptor.length() == 0) return paramDescriptor;

    if (paramDescriptor.charAt(0) == 'L') {
      return paramDescriptor.substring(1, paramDescriptor.length() - 1);
    }

    return paramDescriptor;
  }
}
