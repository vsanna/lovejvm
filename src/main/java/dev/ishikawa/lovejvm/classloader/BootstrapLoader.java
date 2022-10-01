package dev.ishikawa.lovejvm.classloader;


import dev.ishikawa.lovejvm.rawclass.RawClass;

/**
 * BootstrapLoader is responsible for loading the entrypoint class.
 *
 * Class goes throw three steps to be fully usable
 * 1. load
 *   - locate the class ... BootstrapLoader accepts a path of the entrypoint class
 *   - read the classfile's bytes
 *   - convert them into this jvm's internal representation(RawClass)
 *   - put RawClass into MethodArea(=register)
 *   - ! We can make the target class's "raw object" from here.
 *     - "raw object" is just a mem allocated with the necessarry size for this class.
 *     - We CANNOT run <init> for the object at this point yet.
 * 2. link for itself/its direct superclass/its superinterfaces/its element type
 *   - @see ClassLinker
 *   - verify - check the binary structure
 *   - prepare - create static fields default values ...
 *       TODO: just allocating mem space?
 * 3. resolve
 *   - @see Resolver
 *   - ! resolve can be delayed to when some operand codes are executed(ex: new, putstatic, getstatic)
 *   - "replace" some constant entry in the constant pool from symbolic link to the actual *reference*
 *     - CONSTANT_String_info -> String object's objectId
 *     - CONSTANT_Class_info -> {some info that allows jvm to pick the corresponding class later. }
 *     - CONSTANT_Methodref_info -> {some info that allows jvm to pick the corresponding method later}
 *     - CONSTANT_Fieldref_info -> {some info that allows jvm to pick the corresponding field later}
 * 4. initialize
 *   - @see ClassInitializer
 *
 */
public interface BootstrapLoader {
  /**
   * @param filePath file path string from the project root to the specific class file
   * */
  RawClass load(String filePath);

  /**
   * @param binaryName binary name of the class to load. ex: java/lang/String, java/util/Map
   * */
  RawClass loadByBinaryName(String binaryName);
}
