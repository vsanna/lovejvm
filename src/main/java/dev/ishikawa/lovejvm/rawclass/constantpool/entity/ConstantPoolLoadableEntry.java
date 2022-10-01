package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

/**
 * some constant pool entry can be loaded onto stack. integer, float, long, double, class, string,
 * method_handle, method_type, dynamic
 */
public interface ConstantPoolLoadableEntry extends ConstantPoolEntry {
  List<Word> loadableValue();
}
