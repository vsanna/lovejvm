package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantFieldref;
import dev.ishikawa.lovejvm.rawclass.field.RawField;
import dev.ishikawa.lovejvm.rawclass.method.RawMethod;
import dev.ishikawa.lovejvm.rawclass.type.JvmType;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.RawThread;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.ArrayList;
import java.util.List;

public class ConstantFieldrefResolver implements Resolver<ConstantFieldref> {

  /**
   * To resolve ConstantFieldref,
   * 1. resolve the class this field belongs to = create/load the class
   * 2. create a java.lang.reflect.Field object
   * 3. set the objectId to this entry
   */
  @Override
  public void resolve(ConstantPool constantPool, ConstantFieldref entry) {
    if (entry.isResolved()) {
      return;
    }

    int classObjectId =
        RawSystem.methodAreaManager
            .lookupOrLoadClass(entry.getConstantClassRef().getName().getLabel())
            .getClassObjectId();
    entry.setClassObjectId(classObjectId);

    int fieldObjectId = createFieldObject(entry);
    //    int fieldObjectId = -1;
    entry.setFieldObjectId(fieldObjectId);

    entry.setResolved(true);
  }

  private int createFieldObject(ConstantFieldref entry) {
    var rawClass =
        RawSystem.methodAreaManager.lookupOrLoadClass(
            entry.getConstantClassRef().getName().getLabel());
    var classObjectId = rawClass.getClassObjectId();

    RawField rawField = getRawField(entry);
    RawClass fieldRawClass = getFieldRawClass();
    var fieldObjectId = RawSystem.heapManager.register(fieldRawClass);

    String fieldClassInitDescriptor =
        "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;IZILjava/lang/String;[B)V";
    RawMethod fieldInitMethod = fieldRawClass.findInit(fieldClassInitDescriptor).get();

    int wordSize = fieldInitMethod.getTransitWordSize(true);
    List<Word> locals = new ArrayList<>(wordSize);

    // REFACTOR/TODO: how does HotSpotVM create Class/Field/Method?

    /*
    *     Field(
    *       Class<?> declaringClass,
            String name,
            Class<?> type,
            int modifiers,
            boolean trustedFinal,
            int slot,
            String signature,
            byte[] annotations
          )
    * */
    locals.add(Word.of(fieldObjectId)); // a reference to this field object
    locals.add(Word.of(classObjectId));
    locals.add(
        Word.of(RawSystem.stringPool.getOrCreate(entry.getNameAndType().getName().getLabel())));
    locals.add(Word.of(0)); // TODO
    locals.add(Word.of(rawField.getRawAccessFlag()));
    locals.add(Word.of(0)); // TODO
    locals.add(Word.of(rawClass.getMemberFields().indexOf(rawField)));
    locals.add(
        Word.of(
            RawSystem.stringPool.getOrCreate(entry.getNameAndType().getDescriptor().getLabel())));
    locals.add(Word.of(RawSystem.heapManager.registerArray(JvmType.BYTE, 0))); // TODO

    new RawThread("system:initfield:" + entry.getNameAndType().getName().getLabel())
        .invoke(fieldInitMethod, locals);

    return fieldObjectId;
  }

  private RawField getRawField(ConstantFieldref entry) {
    String binaryName = entry.getConstantClassRef().getName().getLabel();
    String fieldName = entry.getNameAndType().getName().getLabel();
    return RawSystem.methodAreaManager.lookupAllField(binaryName, fieldName);
  }

  private RawClass getFieldRawClass() {
    String fieldBinaryClassName = "java/lang/reflect/Field";
    return RawSystem.methodAreaManager.lookupOrLoadClass(fieldBinaryClassName);
  }
}
