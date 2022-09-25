package dev.ishikawa.lovejvm.classloader;

import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.field.RawField;
import dev.ishikawa.lovejvm.rawclass.parser.RawClassParser;
import dev.ishikawa.lovejvm.rawclass.type.JvmType;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.util.Pair;
import dev.ishikawa.lovejvm.vm.RawSystem;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BootstrapLoaderImpl implements BootstrapLoader {
  static public final BootstrapLoaderImpl INSTANCE = new BootstrapLoaderImpl();

  private BootstrapLoaderImpl() {}

  @Override
  public RawClass loadByPath(String filePath) {
    // load
    var classFileBytes = readClassFile(filePath);
    var rawClass = load(classFileBytes);

    // link = verify + prepare
    link(classFileBytes, rawClass);

    // resolve
    resolve(rawClass);

    // initialize
    initialize(rawClass);

    return rawClass;
  }

  private byte[] readClassFile(String filaPath) {
    return ByteUtil.readBytesFromFilePath(filaPath);
  }

  private RawClass load(byte[] classFileBytes) {
    // parse
    var rawClass = new RawClassParser(classFileBytes, null).parse();
    // register
    RawSystem.methodAreaManager.register(rawClass, classFileBytes);
    return rawClass;
  }

  private void link(byte[] classFileBytes, RawClass rawClass) {
    verify(classFileBytes);
    prepare(rawClass);
  }

  private void verify(byte[] classFileBytes) {
    /* TODO: impl */
  }

  /**
   * set default values to each field according to each field's type
   * ex: int -> 0, boolean -> false
   * */
  private void prepare(RawClass rawClass) {
    RawSystem.methodAreaManager.prepareStaticArea(rawClass);
  }

  private void resolve(RawClass rawClass) {
    /* TODO: impl OR do resolution lazily */
  }

  private void initialize(RawClass rawClass) {
    /* TODO: impl */
    // 1. run <clinit> to set static values
    // 2. make Class object, and store it in heap
  }


  /**
   * load a class from binaryName(ex: java/lang/String).
   * This methods checks all dirs to find libs. classpath + some default dirs.
   * */
  @Override
  public RawClass loadByBinaryName(String binaryName) {
    String binaryNamePlusExtension = binaryName + ".java";

    var dirsToLookForFiles = List.of(
        // default dirs to check
        Path.of(System.getenv("JAVA_HOME") + "/lib/java.base/")
        // TODO: add dirs in classpath here.
    );

    // traverse classpath. if not found, throw exception
    Pair<Path, Path> targetPathInfo =
        dirsToLookForFiles.stream()
            .map((libBath) -> Pair.of(
                libBath,
                Path.of(libBath.toString(), binaryNamePlusExtension)
            ))
            .filter((pair) -> {
              Path libPath = pair.getRight();
              return libPath.toFile().exists();
            })
            .findFirst()
            .orElseThrow(
                () -> {
                  throw new RuntimeException(
                      String.format(
                          "The specified class doesn't exist! given binaryName: %s", binaryName));
                });

    // (tentative) compile the .java file in the same location.
    var libDirPath = targetPathInfo.getLeft();
    var libPath = targetPathInfo.getRight();
    var libClassPath = libDirPath.toString() + binaryName + ".class";

    try {
      Process process = new ProcessBuilder(List.of("javac", libPath.toString(), "-d", libClassPath))
          .start();
      process.waitFor(5000, TimeUnit.MILLISECONDS);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("failed to compile the java file!");
    } catch (InterruptedException e) {
      e.printStackTrace();
      throw new RuntimeException("compile was interrupted!");
    }

    return loadByPath(libClassPath);
  }

}
