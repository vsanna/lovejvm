package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantFieldref;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantInterfaceMethodref;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodHandle;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodref;
import dev.ishikawa.lovejvm.rawobject.RawObject;
import dev.ishikawa.lovejvm.vm.InvokeHelper;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

/**
 * // void hello(int var1, String var2) MethodType methodType = MethodType.methodType(void.class,
 * int.class, String.class); MethodHandle methodHandle = lookup.findVirtual(MyClass.class, "hello", methodType);
 *
 * // 第一引数 = receiver
 * new ConstantCallSite(methodHandle).getTarget().invoke(new CallSiteSample(), 123, "hello");
 *
 * ConstantMethodHandle = referenceKind, referenceIndexをもつ
 * kind次第でconstantPool(index)がさししめすものが変わる
 *
 * 候補になるのはFieldref, Methodref, InterfaceMethodref, init/clinitへのMethodref
 * なおMethodHandleはfield情報もふくみうる.
 *
 *
 // TODO: follow this rule
 // when 1 (REF_getField), 2 (REF_getStatic), 3 (REF_putField), or 4 (REF_putStatic)
 //   then CONSTANT_Fieldref_info
 // when 5 (REF_invokeVirtual), 6 (REF_invokeStatic), 7 (REF_invokeSpecial), or 8
 // (REF_newInvokeSpecial),
 //   then CONSTANT_Methodref_info
 // when 9 (REF_invokeInterface)
 //   then CONSTANT_InterfaceMethodref_info
 // AND
 // when 5 (REF_invokeVirtual), 6 (REF_invokeStatic), 7 (REF_invokeSpecial), or 9
 // (REF_invokeInterface),
 //   then method name must not be <init> or <clinit>.
 // when 8 (REF_newInvokeSpecial)
 //   then method name must be <init>
 */
public class ConstantMethodHandleResolver implements Resolver<ConstantMethodHandle> {
  private final RawSystem rawSystem;
  private final InvokeHelper invokeHelper;

  public ConstantMethodHandleResolver(RawSystem rawSystem) {
    this.rawSystem = rawSystem;
    this.invokeHelper = new InvokeHelper(rawSystem);
  }

  @Override
  public void resolve(ConstantPool constantPool, ConstantMethodHandle entry) {
    // Fieldref or Methodrefがreferenceにsetされる
    entry.getReference().resolve(constantPool, rawSystem.resolverService());

    int methodTypeObjectId = -1;
    String name = null;
    int classObjectId = -1;

    switch (entry.getDescriptionKind()) {
      case REF_getField:
      case REF_getStatic:
      case REF_putField:
      case REF_putStatic:
        {
          var fieldref = (ConstantFieldref) entry.getReference();
          var descriptor = fieldref.getNameAndType().getDescriptor().getLabel();
          name = fieldref.getNameAndType().getName().getLabel();
          classObjectId = fieldref.getClassObjectId();
          methodTypeObjectId = invokeHelper.createMethodTypeObject(descriptor);
          break;
        }
      case REF_invokeVirtual:
      case REF_invokeStatic:
      case REF_invokeSpecial:
      case REF_newInvokeSpecial:
        {
          var methodref = (ConstantMethodref) entry.getReference();
          var descriptor = methodref.getNameAndType().getDescriptor().getLabel();
          name = methodref.getNameAndType().getName().getLabel();
          classObjectId = methodref.getClassObjectId();
          methodTypeObjectId = invokeHelper.createMethodTypeObject(descriptor);
          break;
        }
      case REF_invokeInterface:
        {
          var interfaceMethodref = (ConstantInterfaceMethodref) entry.getReference();
          var descriptor = interfaceMethodref.getNameAndType().getDescriptor().getLabel();
          name = interfaceMethodref.getNameAndType().getName().getLabel();
          classObjectId = interfaceMethodref.getClassObjectId();
          methodTypeObjectId = invokeHelper.createMethodTypeObject(descriptor);
          break;
        }
    }

    String METHOD_HANDLE_BINARY_NAME = "java/lang/invoke/MethodHandle";

    var methodHandleRawClass =
        rawSystem.methodAreaManager().lookupOrLoadClass(METHOD_HANDLE_BINARY_NAME);
    int objectId = rawSystem.heapManager().newObject(methodHandleRawClass);
    RawObject methodHandleObject = rawSystem.heapManager().lookupObject(objectId);

    // set typeField
    var typeField =
        methodHandleRawClass
            .findMemberFieldBy("type")
            .orElseThrow(() -> new RuntimeException("MethodHandle type field is not found"));
    rawSystem.heapManager().setValue(
        methodHandleObject, typeField, List.of(Word.of(methodTypeObjectId)));

    // set name
    var nameStringObjectId = rawSystem.stringPool().getOrCreate(name);
    var nameField =
        methodHandleRawClass
            .findMemberFieldBy("name")
            .orElseThrow(() -> new RuntimeException("MethodHandle name field is not found"));
    rawSystem.heapManager().setValue(
        methodHandleObject, nameField, List.of(Word.of(nameStringObjectId)));

    // set klass
    var klassField =
        methodHandleRawClass
            .findMemberFieldBy("klass")
            .orElseThrow(() -> new RuntimeException("MethodHandle klass field is not found"));
    rawSystem.heapManager().setValue(methodHandleObject, klassField, List.of(Word.of(classObjectId)));

    entry.setObjectId(objectId);
    entry.setResolved(true);
  }
}
