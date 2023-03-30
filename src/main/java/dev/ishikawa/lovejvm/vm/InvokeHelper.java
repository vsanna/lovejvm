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
  private final RawSystem rawSystem;

  private final MethodTypeFactory methodTypeFactory;

  public InvokeHelper(RawSystem rawSystem) {
    this.rawSystem = rawSystem;
    this.methodTypeFactory = new MethodTypeFactory(rawSystem, this);
  }

  public int createMethodTypeObject(String descriptor) {
    return methodTypeFactory.createMethodTypeObject(descriptor);
  }

  // These logics should be in MethodType in SDK
  static class MethodTypeFactory {
    private static final String METHOD_TYPE_BINARY_NAME = "java/lang/invoke/MethodType";
    private final RawSystem rawSystem;
    private final InvokeHelper invokeHelper;
    private final RawClass methodTypeRawClass;

    public MethodTypeFactory(RawSystem rawSystem, InvokeHelper invokeHelper) {
      this.rawSystem = rawSystem;
      this.invokeHelper = invokeHelper;
      this.methodTypeRawClass = rawSystem.methodAreaManager().lookupOrLoadClass(METHOD_TYPE_BINARY_NAME);
    }

    public int createMethodTypeObject(String descriptor) {
      return createMethodTypeObject(
          descriptor, getRTypeClassObjectId(descriptor), getPTypeClassObjectIds(descriptor));
    }

    private int getRTypeClassObjectId(String descriptor) {
      String rTypeBinaryName = invokeHelper.parseRType(descriptor);
      RawClass rawClass = invokeHelper.mapType(rTypeBinaryName);
      return rawClass.getClassObjectId();
    }

    private List<Integer> getPTypeClassObjectIds(String descriptor) {
      List<String> pTypeBinaryNames = invokeHelper.parsePTypes(descriptor);
      return pTypeBinaryNames.stream()
          .map(it -> invokeHelper.mapType(it).getClassObjectId())
          .collect(Collectors.toList());
    }

    private int createMethodTypeObject(
        String descriptor, int rTypeClassObjectId, List<Integer> pTypeClassObjectIds) {

      int objectId = rawSystem.heapManager().newObject(methodTypeRawClass);
      RawObject methodTypeObject = rawSystem.heapManager().lookupObject(objectId);

      // set descriptor
      rawSystem.heapManager().setValue(
          methodTypeObject,
          "descriptor",
          List.of(Word.of(rawSystem.stringPool().getOrCreate(descriptor))));

      // set rType
      rawSystem.heapManager().setValue(
          methodTypeObject, "rtype", List.of(Word.of(rTypeClassObjectId)));

      // set pTypes
      RawClass classArrayClass =
          rawSystem.methodAreaManager().lookupOrLoadClass("[Ljava/lang/Class;");
      int classArrayRawObjectId =
          rawSystem.heapManager().newArrayObject(
              ((RawArrayClass) classArrayClass), pTypeClassObjectIds.size());
      RawObject classArrayRawObject = rawSystem.heapManager().lookupObject(classArrayRawObjectId);
      for (int i = 0; i < pTypeClassObjectIds.size(); i++) {
        rawSystem.heapManager().setElement(
            classArrayRawObject, i, List.of(Word.of(pTypeClassObjectIds.get(i))));
      }
      rawSystem.heapManager().setValue(
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

    return rawSystem.methodAreaManager().lookupOrLoadClass(binaryNameToLookup);
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
