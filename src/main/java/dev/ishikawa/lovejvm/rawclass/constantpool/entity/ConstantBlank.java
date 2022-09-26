package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;

/**
 * ConstantBlank is used to fill blank slots - the initial slot - slot right after ConstantDouble /
 * ConstantLong
 *
 * <p>This is not specified by the spec
 */
public class ConstantBlank implements ConstantPoolEntry {

  public ConstantBlank() {}

  @Override
  public void resolve(ConstantPool constantPool) {
    // noop
  }

  @Override
  public boolean isResolved() {
    return true;
  }

  @Override
  public int size() {
    return 0;
  }
}
