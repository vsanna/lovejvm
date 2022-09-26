package dev.ishikawa.lovejvm.classloader;


import dev.ishikawa.lovejvm.rawclass.RawClass;

/**
 * BootstrapLoader is responisble for loading the entrypoint class.
 *
 * <p>it takes these steps 1. load - find the class ... BootstrapLoader accepts a path of the
 * entrypoint class - read the classfile's bytes - convert them into this jvm's internal
 * representation(RawClass) - put RawClass into MethodArea(=register) 2. link for itself/its direct
 * superclass/its superinterfaces/its element type - verify - check the binary structure - prepare -
 * create static fields default values ... TODO: just allocating mem space? 3. resolve - ! resolve
 * can be delayed to when some operand codes are executed(ex: new, putstatic, getstatic) - "replace"
 * some constant entry in the constant pool from symbolic link to the actual *reference* -
 * CONSTANT_String_info -> String object's objectId - CONSTANT_Class_info -> {some info that allows
 * jvm to pick the corresponding class later. } - CONSTANT_Methodref_info -> {some info that allows
 * jvm to pick the corresponding method later} - CONSTANT_Fieldref_info -> {some info that allows
 * jvm to pick the corresponding field later} 4. initialize - execute <clinit>
 */
public interface BootstrapLoader {
  RawClass loadByPath(String filePath);

  RawClass loadByBinaryName(String binaryName);
}
