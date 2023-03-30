package dev.ishikawa.lovejvm;


import dev.ishikawa.lovejvm.memory.heap.HeapManagerImpl;
import dev.ishikawa.lovejvm.memory.methodarea.MethodAreaManagerImpl;
import dev.ishikawa.lovejvm.option.Options;
import dev.ishikawa.lovejvm.option.OptionsParser;
import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.RawThread;

/**
 * LoveJVM is the starting point of this hand-made JVM!
 *
 * @see Options what options are available
 */
public class LoveJVM {
  public static void main(String[] args) {
    Options options = OptionsParser.parse(args);
    LoveJVM jvm = new LoveJVM(options);
    try {
      jvm.run();
      jvm.rawSystem.heapManager().dump();
      jvm.rawSystem.methodAreaManager().dump();
    } catch (Exception ex) {
      throw ex;
    }
  }

  private final Options options;
  private final RawSystem rawSystem;

  public LoveJVM(Options options) {
    this.options = options;
    this.rawSystem = RawSystem.create(this);
  }

  public RawSystem getRawSystem() {
    return rawSystem;
  }

  public void run() {
    var entryClass = prepareEntrypointClass(options);
    var entryPoint = entryClass.findEntryPoint();

    // start the main thread with the entry point
    entryPoint
        .map(
            (ep) -> {
              var thread = new RawThread("main", this);
              rawSystem.setMainThread(thread);
              thread.init(ep).run();
              return thread;
            })
        .orElseThrow(
            () -> {
              throw new RuntimeException("no entrypoint");
            });

    // cleanup operations below
  }

  /** entrypoint class is loaded/linked/initialized at the beginning of the JVM process. */
  private RawClass prepareEntrypointClass(Options options) {
    var entryClass = rawSystem.bootstrapLoader().loadByFilePath(options.getEntryClass());
    rawSystem.classLinker().link(entryClass);
    rawSystem.classInitializer().initialize(entryClass);
    return entryClass;
  }

  public static final String ENTRY_METHOD_NAME = "main";
  public static final String ENTRY_METHOD_DESC = "([Ljava/lang/String;)V";
}
