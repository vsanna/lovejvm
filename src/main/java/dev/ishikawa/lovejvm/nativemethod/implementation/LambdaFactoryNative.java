package dev.ishikawa.lovejvm.nativemethod.implementation;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.type.JvmType;
import dev.ishikawa.lovejvm.rawobject.RawObject;
import dev.ishikawa.lovejvm.vm.Frame;
import dev.ishikawa.lovejvm.vm.InvokeHelper;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LambdaFactoryNative {
  private static final AtomicInteger atomicInteger = new AtomicInteger(0);

  /*
      MethodType invokedMethodType      // capture情報
      String invokedName,               // apply|run|accept, etc                 ex: apply, run, accept
      MethodHandle implMethod,          // ref to actual logic to use.           ex: REF_invokeStatic dev/ishikawa/sample/LambdaSample2.lambda$makerun$0:(I)V
      MethodType instantiatedMethodType // descriptor of this functional method  ex: ()V
  * */
  public static List<Word> createLambdaImplObject(Frame currentFrame) {
    int instantiatedMethodTypeObjectId = currentFrame.getOperandStack().pop().getValue();
    int implMethodObjectId = currentFrame.getOperandStack().pop().getValue();
    int invokedNameObjectId = currentFrame.getOperandStack().pop().getValue();
    int invokedMethodTypeObjectId = currentFrame.getOperandStack().pop().getValue();

    // このinner classが満たすべきI/F
    // 1. name
    String invokedName = RawSystem.stringPool.getLabelBy(invokedNameObjectId).orElseThrow();
    // 2. descriptor
    RawObject typeForCaller = RawSystem.heapManager.lookupObject(instantiatedMethodTypeObjectId);
    String descriptorForCaller =
        RawSystem.stringPool
            .getLabelBy(
                RawSystem.heapManager.getValue(typeForCaller, "descriptor").get(0).getValue())
            .orElseThrow();
    // reference -> Object, primitive -> primitive
    var modifiedPtypesDescForCaller =
        InvokeHelper.INSTANCE.parsePTypes(descriptorForCaller).stream()
            .map(
                it -> {
                  if (JvmType.primaryTypes.contains(JvmType.findByJvmSignature(it))) {
                    return it;
                  } else {
                    return "Ljava/lang/Object;";
                  }
                })
            .collect(Collectors.toList());
    String modifiedRtypeDescriptorForCaller = InvokeHelper.INSTANCE.parseRType(descriptorForCaller);
    if (!JvmType.primaryTypes.contains(
        JvmType.findByJvmSignature(modifiedRtypeDescriptorForCaller))) {
      modifiedRtypeDescriptorForCaller = "Ljava/lang/Object;";
    }

    String modifiedDescriptorForCaller =
        "(" + String.join("", modifiedPtypesDescForCaller) + ")" + modifiedRtypeDescriptorForCaller;

    // このinner classが実際に呼び出す処理
    // 1. name
    RawObject methodHandleRawObject = RawSystem.heapManager.lookupObject(implMethodObjectId);
    String methodNameForImpl =
        RawSystem.stringPool
            .getLabelBy(
                RawSystem.heapManager.getValue(methodHandleRawObject, "name").get(0).getValue())
            .orElseThrow();
    // 2. outer class
    List<Word> klassRawValue = RawSystem.heapManager.getValue(methodHandleRawObject, "klass");
    List<Word> outerClassNameRawValue =
        RawSystem.heapManager.getValue(
            RawSystem.heapManager.lookupObject(klassRawValue.get(0).getValue()), "name");
    String outerClassBinaryName =
        RawSystem.stringPool.getLabelBy(outerClassNameRawValue.get(0).getValue()).orElseThrow();
    // 3. descriptor
    RawObject typeForImpl =
        RawSystem.heapManager.lookupObject(
            RawSystem.heapManager.getValue(methodHandleRawObject, "type").get(0).getValue());

    String descriptorForImpl =
        RawSystem.stringPool
            .getLabelBy(RawSystem.heapManager.getValue(typeForImpl, "descriptor").get(0).getValue())
            .orElseThrow();

    String rtypeDescForImpl = InvokeHelper.INSTANCE.parseRType(descriptorForImpl);
    List<String> ptypeDescForImpl = InvokeHelper.INSTANCE.parsePTypes(descriptorForImpl);

    // captureのためのtype
    RawObject typeForCapture = RawSystem.heapManager.lookupObject(invokedMethodTypeObjectId);
    String descriptorForCapture =
        RawSystem.stringPool
            .getLabelBy(
                RawSystem.heapManager.getValue(typeForCapture, "descriptor").get(0).getValue())
            .orElseThrow();
    List<String> ptypesDescForCapture = InvokeHelper.INSTANCE.parsePTypes(descriptorForCapture);

    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    String innerClassName =
        String.format("%s$Lambda$%d", outerClassBinaryName, atomicInteger.incrementAndGet());
    // define class
    cw.visit(
        Opcodes.V1_8, Opcodes.ACC_PUBLIC, innerClassName, null, "java/lang/Object", new String[0]);

    // define fields
    // To store captured variables in fields
    for (int i = 0; i < ptypesDescForCapture.size(); i++) {
      cw.visitField(
              Opcodes.ACC_PUBLIC,
              String.format("var%d", i),
              ptypesDescForCapture.get(i),
              null,
              null)
          .visitEnd();
    }

    // define a instance method
    MethodVisitor methodVisitor =
        cw.visitMethod(Opcodes.ACC_PUBLIC, invokedName, modifiedDescriptorForCaller, null, null);

    // receiverとしてselfを必ず持つので+1
    int nLocal = modifiedPtypesDescForCaller.size() + 1;
    var localLabels = new Object[nLocal];
    localLabels[0] = "java/lang/Object";
    for (int i = 0; i < nLocal - 1; i++) {
      localLabels[i + 1] = modifiedPtypesDescForCaller.get(i);
    }
    methodVisitor.visitCode();
    //    methodVisitor.visitFrame(Opcodes.F_NEW, nLocal, localLabels, 0, new Object[0]);

    // var1...varNをload
    // captureした値を先にpush
    for (int i = 0; i < ptypesDescForCapture.size(); i++) {
      String fieldDescriptor = ptypeDescForImpl.get(i);
      methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
      methodVisitor.visitFieldInsn(
          Opcodes.GETFIELD, innerClassName, String.format("var%d", i), fieldDescriptor);
    }
    // lambdaに渡された値が続く.
    for (int i = 0; i < modifiedPtypesDescForCaller.size(); i++) {
      String ptypeDescriptor = modifiedPtypesDescForCaller.get(i);
      int localPos = 1 + i; // thisを除く
      // 引数によってops変わる
      switch (JvmType.findByJvmSignature(ptypeDescriptor)) {
        case BYTE:
        case SHORT:
        case CHAR:
        case BOOLEAN:
        case INT:
          methodVisitor.visitVarInsn(Opcodes.ILOAD, localPos);
          break;
        case LONG:
          methodVisitor.visitVarInsn(Opcodes.LLOAD, localPos);
          break;
        case FLOAT:
          methodVisitor.visitVarInsn(Opcodes.FLOAD, localPos);
          break;
        case DOUBLE:
          methodVisitor.visitVarInsn(Opcodes.DLOAD, localPos);
          break;
        case VOID:
        case ARRAY:
        case OBJECT_REFERENCE:
          methodVisitor.visitVarInsn(Opcodes.ALOAD, localPos);
      }
    }

    // call
    methodVisitor.visitMethodInsn(
        Opcodes.INVOKESTATIC, outerClassBinaryName, methodNameForImpl, descriptorForImpl, false);
    if (rtypeDescForImpl.equals(JvmType.VOID.getJvmSignature())
        || rtypeDescForImpl.equals(JvmType.VOID.getBoxTypeBinaryName())) {
      methodVisitor.visitInsn(Opcodes.RETURN);
    } else {
      methodVisitor.visitInsn(Opcodes.ARETURN);
    }
    methodVisitor.visitEnd();

    // cwからinner classを登録
    // cwをcache
    // inner classからnew

    cw.visitEnd();
    byte[] classbytes = cw.toByteArray();

    RawClass rawClass = RawSystem.bootstrapLoader.loadFromBytes(classbytes);
    int objectId = RawSystem.heapManager.newObject(rawClass);
    return List.of(Word.of(objectId));
  }
}
