package dev.ishikawa.lovejvm.classloader;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.parser.RawClassParser;
import dev.ishikawa.lovejvm.rawobject.RawObject;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.vm.RawSystem;
import java.nio.file.Path;
import java.util.List;

/**
 * 目的 - readClassData - loadClassData: RawClass - create internal representation of class = RawClass
 * - この過程でsuperclass, superinterfacesを"resolve" = link. 当然未loadのclassならまずはloadしてから -
 * classにはどのloaderを使ったかを記録. - > For every loaded .class file, JVM immediately creates an object on
 * the heap memory of type java.lang.class - ref:
 * https://www.javacodegeeks.com/2018/04/jvm-architecture-jvm-class-loader-and-runtime-data-areas.html
 * - and return Class object -
 * https://stackoverflow.com/questions/32819927/is-the-class-object-a-created-when-the-jvm-loads-class-a-or-when-i-call-a-class
 * - Target classをloadしたタイミングでClass objectは作れないといけない. Class clsasのconstructorはJVMが無理やり実行 -
 * そのためには、Class classがloadされている必要がある? ... これは本当か? - newのためにはinitializeも必要なはず...つまりClass
 * classは最初にload/link/initializeまで言っていないといけない?? - しかしClass classはいくつかのinterfaceを実装している = Class
 * classのcreateの段階でそれらのresolve=linkが発生してしまう - clinitもある - よく考えよう. Class.classをRawClassにした段階でclass
 * objectは作れる(=sizeが分かる) - class objectのlockができる必要がある / classでlockできる必要がある trigger -
 *
 * @see ClassLoader ClassLoader#defineClass methodを読びbytes of a classfileを(CLがJVMに)渡したときにclass
 *     objectが作られる 全てのClass objectは使用されたClassLoaderへの"reference"をもつ
 *     ただしBootstrapClassLoaderがloadした場合はnull ArrayのClass objectはCL使われない.
 *     よって、それらのCLは要素型をloadしたCLが使われる. 要素型がprimitiveの場合はit has no CL
 *     <p>## Run-time Built-in Class loaders JVMがもつbuilt-in CLがある = Bootstrap class loader = written
 *     in native code(ref: https://www.baeldung.com/java-classloaders) - 全てのCLのparent - JDK internal
 *     classes(rt.jar, other jars located under jre/lib dir) - > This bootstrap class loader is part
 *     of the core JVM and is written in native code, as pointed out in the above example. Different
 *     platforms might have different implementations of this particular class loader. Platform
 *     class loader: ??? System class loader: a.k.k ApplicationClassLoader. loaderのtree構造 -
 *     CL.loadClass(binaryName)したとき、まず先祖がすでにloadしていないかrecursiveに問い合わせる - 先祖が持っていないときだけloadできる -
 *     よって、兄弟が持っているときは同じクラスを再びloadできる - なお、自分がloadに失敗した場合はchildrenにdelegateする
 *     <p>?? イマイチどうやってjvm <-> java file間で連携するかわからない
 *     <p>JVMの責務はbyte arrayをparseしてmemに置くところ
 */
public class BootstrapLoaderImpl implements BootstrapLoader {
  public static final BootstrapLoaderImpl INSTANCE = new BootstrapLoaderImpl();

  private BootstrapLoaderImpl() {}

  @Override
  public RawClass load(String filePath) {
    var targetClassFileBytes = readClassFile(filePath);
    // TODO make and use a builder to create RawClsas. RawClass should be built after
    // `setClassObjectId` is called
    var targetClass = new RawClassParser(targetClassFileBytes, null).parse();

    // check if the class has been already loaded or not
    return RawSystem.methodAreaManager
        .lookupClass(targetClass.getBinaryName())
        .orElseGet(
            () -> {
              // load on mem
              allocateClassData(targetClass, targetClassFileBytes);

              // make raw class object in advance.
              RawObject classObject = RawSystem.heapManager.createClassObject(targetClass);
              targetClass.setClassObjectId(classObject.getObjectId());

              return targetClass;
            });
  }

  private byte[] readClassFile(String filaPath) {
    return ByteUtil.readBytesFromFilePath(filaPath);
  }

  private void allocateClassData(RawClass targetClass, byte[] classFileBytes) {
    RawSystem.methodAreaManager.register(targetClass, classFileBytes);
  }

  /**
   * load a class from binaryName(ex: java/lang/String). This methods checks all dirs to find libs.
   * classpath + some default dirs.
   *
   * <p>TODO: 本来こっちがloadであるべき
   */
  @Override
  public RawClass loadByBinaryName(String binaryName) {
    return load(getPathFrom(binaryName).toString());
  }

  private Path getPathFrom(String binaryName) {
    String binaryNamePlusExtension = binaryName + ".class";

    var dirsToLookForFiles =
        List.of(
            /*
             * default dirs to check
             *   1. path/to/this/project/standardlibs.
             *     check README.md to see how to compile standard libraries
             *   2. TBD
             * */
            Path.of("standardlibs/java.base/"));

    // traverse classpath. if not found, throw exception
    return dirsToLookForFiles.stream()
        .map((dirPath) -> Path.of(dirPath.toString(), binaryNamePlusExtension))
        .filter((libPath) -> libPath.toFile().exists())
        .findFirst()
        .orElseThrow(
            () -> {
              throw new RuntimeException(
                  String.format(
                      "The specified class doesn't exist! given binaryName: %s", binaryName));
            });
  }
}
