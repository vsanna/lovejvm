package dev.ishikawa.lovejvm.classloader;


import dev.ishikawa.lovejvm.rawclass.RawClass;

/**
 * BootstrapLoader is responsible for loading the entrypoint class.
 *
 * Classes go through three steps in order to be fully usable
 * 1. load
 *   - BootstrapLoader accepts class identifier(ex: binaryName)
 *   - BL locates the class
 *   - BL reads the class's classfile as byte array
 *   - BL pass the bytearray to JVM, and JVM converts them into an internal representation(RawClass in LoveJVM case)
 *   - BL puts the RawClass into MethodArea
 *   - NOTE: We can make the target class's "raw object" from this point.
 *     - "raw object" is just an allocated mem space with the necessarry size for this class.
 *     - We CANNOT run <init> for the object at this point yet. we have to wait the class to be initialized completely.
 * 2. link
 *   - @see ClassLinker
 * 3. resolve
 *   - @see Resolver
 * 4. initialize
 *   - @see ClassInitializer
 *
 */
public interface BootstrapLoader {
  /**
   * @param binaryName binary name of the class to load. ex: java/lang/String, java/util/Map
   * */
  RawClass load(String binaryName);

  /**
   * @param filePath file path string from the project root to the specific class file
   * */
  RawClass loadByFilePath(String filePath);
}
