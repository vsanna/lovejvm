package dev.ishikawa.lovejvm.vm;


import dev.ishikawa.lovejvm.LoveJVM;
import dev.ishikawa.lovejvm.classloader.BootstrapLoader;
import dev.ishikawa.lovejvm.classloader.BootstrapLoaderImpl;
import dev.ishikawa.lovejvm.memory.heap.HeapManager;
import dev.ishikawa.lovejvm.memory.heap.HeapManagerImpl;
import dev.ishikawa.lovejvm.memory.methodarea.MethodAreaManager;
import dev.ishikawa.lovejvm.memory.methodarea.MethodAreaManagerImpl;
import dev.ishikawa.lovejvm.memory.stringpool.StringPool;
import dev.ishikawa.lovejvm.memory.stringpool.StringPoolSimulator;
import dev.ishikawa.lovejvm.nativemethod.NativeMethodHandler;
import dev.ishikawa.lovejvm.nativemethod.NativeMethodHandlerSimulator;
import dev.ishikawa.lovejvm.rawclass.constantpool.resolver.ResolverService;
import dev.ishikawa.lovejvm.rawclass.initializer.ClassInitializer;
import dev.ishikawa.lovejvm.rawclass.linker.ClassLinker;
import dev.ishikawa.lovejvm.util.ByteUtil;

//jvmからみてsingleton.
public class RawSystem {
  private RawSystem(LoveJVM jvm) {
    this.jvm = jvm;
    this.methodAreaManager = new MethodAreaManagerImpl(this);
    this.heapManager = new HeapManagerImpl(this);
    this.stringPool = new StringPoolSimulator(this);
    this.bootstrapLoader = new BootstrapLoaderImpl(this);
    this.resolverService = new ResolverService(this);
    this.classLinker = new ClassLinker(this);
    this.classInitializer = new ClassInitializer(this);
    this.nativeMethodHandler = new NativeMethodHandlerSimulator(this);
  }

  private LoveJVM jvm;
  private RawThread mainThread;
  private final MethodAreaManager methodAreaManager;
  private final HeapManager heapManager;
  private final StringPool stringPool;
  private final BootstrapLoader bootstrapLoader;
  private final ResolverService resolverService;
  private final ClassLinker classLinker;
  private final ClassInitializer classInitializer;
  private final NativeMethodHandler nativeMethodHandler;

  static public RawSystem create(LoveJVM jvm) {
    return new RawSystem(jvm);
  }

  public RawThread createThread(String name) {
    return new RawThread(name, this.jvm);
  }

  public RawThread getMainThread() {
    return mainThread;
  }

  public void setMainThread(RawThread mainThread) {
    this.mainThread = mainThread;
  }

  public MethodAreaManager methodAreaManager() {
    return methodAreaManager;
  }

  public HeapManager heapManager() {
    return heapManager;
  }

  public StringPool stringPool() {
    return stringPool;
  }

  public BootstrapLoader bootstrapLoader() {
    return bootstrapLoader;
  }

  public ResolverService resolverService() {
    return resolverService;
  }

  public ClassLinker classLinker() {
    return classLinker;
  }

  public ClassInitializer classInitializer() {
    return classInitializer;
  }

  public NativeMethodHandler nativeMethodHandler() {
    return nativeMethodHandler;
  }
}
