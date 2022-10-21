package dev.ishikawa.lovejvm.vm;

import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawobject.RawObject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InvokeHelper {
  static public int createMethodTypeObject(String descriptor) {
    return createMethodTypeObject(
        getRTypeClassObjectId(descriptor),
        getPTypeClassObjectIds(descriptor)
    );
  }

  static private int getRTypeClassObjectId(String descriptor) {
    return 1; // TODO
  }

  static private int[] getPTypeClassObjectIds(String descriptor) {
    return new int[]{1,2,3}; // TODO
  }

  static private int createMethodTypeObject(int rTypeClassObjectId, int[] pTypeClassObjectIds) {
    if (methodTypeRawClass == null) {
      methodTypeRawClass = RawSystem.methodAreaManager.lookupOrLoadClass(METHOD_TYPE_BINARY_NAME);
    }

    int objectId = RawSystem.heapManager.newObject(methodTypeRawClass);
    RawObject methodTypeObject = RawSystem.heapManager.lookupObject(objectId);

    // set rType
    var rTypeField =
        methodTypeRawClass
            .findMemberFieldBy("pType")
            .orElseThrow(() -> new RuntimeException("Class<?> pType field is not found"));
    RawSystem.heapManager.setValue(methodTypeObject, rTypeField, List.of(Word.of(rTypeClassObjectId)));

    // set pTypes
    List<Word> pTypeFieldValue = Arrays.stream(pTypeClassObjectIds)
        .mapToObj(Word::of).collect(Collectors.toList());

    var pTypeField =
        methodTypeRawClass
            .findMemberFieldBy("pType")
            .orElseThrow(() -> new RuntimeException("Class<?> pType field is not found"));
    RawSystem.heapManager.setValue(methodTypeObject, pTypeField, pTypeFieldValue);

    return objectId;
  }

  private static final String METHOD_TYPE_BINARY_NAME = "java/lang/invoke/MethodType";
  private static RawClass methodTypeRawClass;
}
