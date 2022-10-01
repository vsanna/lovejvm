package dev.ishikawa.lovejvm.vm;


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

public class RawSystem {
  public static RawThread mainThread;
  public static final MethodAreaManager methodAreaManager = MethodAreaManagerImpl.INSTANCE;
  public static final HeapManager heapManager = HeapManagerImpl.INSTANCE;
  public static final StringPool stringPool = StringPoolSimulator.INSTANCE;
  public static final BootstrapLoader bootstrapLoader = BootstrapLoaderImpl.INSTANCE;
  public static final ResolverService resolverService = ResolverService.INSTANCE;
  public static final ClassLinker classLinker = ClassLinker.INSTANCE;
  public static final ClassInitializer classInitializer = ClassInitializer.INSTANCE;
  public static final NativeMethodHandler nativeMethodHandler =
      NativeMethodHandlerSimulator.INSTANCE;

  public static void setMainThread(RawThread mainThread) {
    RawSystem.mainThread = mainThread;
  }
}
