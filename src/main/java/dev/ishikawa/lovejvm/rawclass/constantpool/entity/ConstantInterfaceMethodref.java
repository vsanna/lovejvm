package dev.ishikawa.lovejvm.rawclass.constantpool.entity;

/**
 * ConstantInterfaceMethodref is a tuple of {className, name, descriptor}
 * ConstantInterfaceMethodref itself is the key to identify what method to use.
 *
 * This is not loadable, so no extra value is put when resolving this entry.
 *
 * While resolving this entry, related other entries will be also resolved(ConstantClass)
 * */
public class ConstantInterfaceMethodref extends ConstantMethodref {
  public ConstantInterfaceMethodref(int classIndex, int nameAndTypeIndex) {
    super(classIndex, nameAndTypeIndex);
  }
}
