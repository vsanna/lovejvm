package dev.ishikawa.lovejvm.vm;


import dev.ishikawa.lovejvm.classloader.BootstrapLoader;
import dev.ishikawa.lovejvm.classloader.BootstrapLoaderImpl;
import dev.ishikawa.lovejvm.memory.heap.HeapManager;
import dev.ishikawa.lovejvm.memory.heap.HeapManagerImpl;
import dev.ishikawa.lovejvm.memory.methodarea.MethodAreaManager;
import dev.ishikawa.lovejvm.memory.methodarea.MethodAreaManagerImpl;
import dev.ishikawa.lovejvm.memory.stringpool.StringPool;
import dev.ishikawa.lovejvm.memory.stringpool.StringPoolSimulator;

public class RawSystem {
  private static RawThread mainThread;
  public static final MethodAreaManager methodAreaManager = MethodAreaManagerImpl.INSTANCE;
  public static final HeapManager heapManager = HeapManagerImpl.INSTANCE;
  public static final StringPool stringPool = StringPoolSimulator.INSTANCE;
  public static final BootstrapLoader bootstrapLoader = BootstrapLoaderImpl.INSTANCE;
  public static void setMainThread(RawThread mainThread) {
    RawSystem.mainThread = mainThread;
  }
}
