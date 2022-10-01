package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;

/*
 * CONSTANT_Dynamic_info は動的にcall siteを指し示す
 * call siteは java.lang.invoke.CallSiteのinstanceで、bootstrapmethodを実行することで得られる
 * invokedynamicでtriggerされる
 * nameAndTypeはそのcall site(=何かしらのmethod)の引数/返り値を表現する
 * @SEE <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.4.10">jvm spec</a>
 */
public class ConstantInvokeDynamic extends ConstantPoolResolvableEntry
    implements ConstantPoolEntry {
  private final int bootstrapMethodAttrIndex; // 2bytes

  private final int nameAndTypeIndex; // 2byte
  private ConstantNameAndType nameAndType;

  private int objectId; // to java.lang.invoke.CallStack

  public ConstantInvokeDynamic(int bootstrapMethodAttrIndex, int nameAndTypeIndex) {
    this.bootstrapMethodAttrIndex = bootstrapMethodAttrIndex;
    this.nameAndTypeIndex = nameAndTypeIndex;
  }

  @Override
  public void shakeOut(ConstantPool constantPool) {
    nameAndType = (ConstantNameAndType) constantPool.findByIndex(nameAndTypeIndex);
  }

  public ConstantNameAndType getNameAndType() {
    return nameAndType;
  }

  public int getBootstrapMethodAttrIndex() {
    return bootstrapMethodAttrIndex;
  }

  public int getNameAndTypeIndex() {
    return nameAndTypeIndex;
  }

  public void setNameAndType(ConstantNameAndType nameAndType) {
    this.nameAndType = nameAndType;
  }

  public int getObjectId() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return objectId;
  }

  public void setObjectId(int objectId) {
    this.objectId = objectId;
  }

  @Override
  public int size() {
    return 5;
  }
}
