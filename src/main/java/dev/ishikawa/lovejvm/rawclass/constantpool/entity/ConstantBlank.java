package dev.ishikawa.lovejvm.rawclass.constantpool.entity;

/**
 * ConstantBlank is used to fill blank slots - the initial slot - slot right after ConstantDouble /
 * ConstantLong
 *
 * <p>This is not specified by the spec
 */
public class ConstantBlank extends ConstantPoolUnresolvableEntry implements ConstantPoolEntry {
  public ConstantBlank() {}

  @Override
  public int size() {
    return 0;
  }
}
