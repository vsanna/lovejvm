package dev.ishikawa.lovejvm.classloader;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.parser.RawClassParser;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.RawThread;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BootstrapLoaderImpl implements BootstrapLoader {
  public static final BootstrapLoaderImpl INSTANCE = new BootstrapLoaderImpl();

  private BootstrapLoaderImpl() {}

  @Override
  public RawClass loadByPath(String filePath) {
    var classFileBytes = readClassFile(filePath);
    var rawClass = load(classFileBytes);

    link(classFileBytes, rawClass);
    resolve(rawClass);
    initialize(rawClass);

    return rawClass;
  }

  private byte[] readClassFile(String filaPath) {
    return ByteUtil.readBytesFromFilePath(filaPath);
  }

  private RawClass load(byte[] classFileBytes) {
    var rawClass = new RawClassParser(classFileBytes, null).parse();
    RawSystem.methodAreaManager.register(rawClass, classFileBytes);
    return rawClass;
  }

  private void link(byte[] classFileBytes, RawClass rawClass) {
    loadRelatedClasses(rawClass);
    verify(classFileBytes);
    prepare(rawClass);
  }

  /** load its direct superclass, its direct super interfaces, and its element's class if it has. */
  private void loadRelatedClasses(RawClass rawClass) {
    // Only Object.java doesn't have its superclass
    Optional.ofNullable(rawClass.getSuperClass())
        .ifPresent(
            (superClass) -> {
              String superClassBinaryName = superClass.getName().getLabel();
              RawSystem.methodAreaManager.lookupOrLoadClass(superClassBinaryName);
            });

    List<String> superInterfaceBinaryNames =
        rawClass.getInterfaces().getInterfaces().stream()
            .map((it) -> it.getConstantClassRef().getName().getLabel())
            .collect(Collectors.toList());
    superInterfaceBinaryNames.forEach(RawSystem.methodAreaManager::lookupOrLoadClass);

    // TODO: if rawClass has an element class, load the class too
  }

  private void verify(byte[] classFileBytes) {
    /* TODO: impl */
  }

  /**
   * set default values to each field according to each field's type ex: int -> 0, boolean -> false
   */
  private void prepare(RawClass rawClass) {
    RawSystem.methodAreaManager.prepareStaticArea(rawClass);
  }

  private void resolve(RawClass rawClass) {
    /* TODO: impl OR do resolution lazily */
  }

  private void initialize(RawClass rawClass) {
    // 1. run <clinit> to set static values
    rawClass
        .findClinit()
        .ifPresent(
            (clinit) -> {
              // TODO: stop all the other thread.
              new RawThread("system:" + rawClass.getBinaryName()).init(clinit).run();
            });

    // 2. make Class object, and store it in heap
  }

  /**
   * load a class from binaryName(ex: java/lang/String). This methods checks all dirs to find libs.
   * classpath + some default dirs.
   */
  @Override
  public RawClass loadByBinaryName(String binaryName) {
    String binaryNamePlusExtension = binaryName + ".class";

    var dirsToLookForFiles =
        List.of(
            // default dirs to check
            // 1. path/to/this/project/standardlibs. check README.md to see how to compile standard
            // libraries
            Path.of("standardlibs/java.base/")
            // TODO: add dirs in classpath here.
            );

    // traverse classpath. if not found, throw exception
    Path libClassPath =
        dirsToLookForFiles.stream()
            .map((libBath) -> Path.of(libBath.toString(), binaryNamePlusExtension))
            .filter((libPath) -> libPath.toFile().exists())
            .findFirst()
            .orElseThrow(
                () -> {
                  throw new RuntimeException(
                      String.format(
                          "The specified class doesn't exist! given binaryName: %s", binaryName));
                });

    return loadByPath(libClassPath.toString());
  }
}
