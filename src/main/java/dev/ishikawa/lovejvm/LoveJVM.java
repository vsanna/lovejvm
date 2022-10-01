package dev.ishikawa.lovejvm;


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
    try {
      Options options = OptionsParser.parse(args);
      LoveJVM jvm = new LoveJVM(options);
      jvm.run();
    } catch (Exception ex) {
      //      HeapManagerImpl.INSTANCE.dump();
      //      MethodAreaManagerImpl.INSTANCE.dump();
      throw ex;
    }
  }

  private final Options options;

  public LoveJVM(Options options) {
    this.options = options;
  }

  public void run() {
    var entryClass = prepareEntrypointClass(options);
    var entryPoint = entryClass.findEntryPoint();

    // start the main thread with the entry point
    entryPoint
        .map(
            (ep) -> {
              var thread = new RawThread("main");
              RawSystem.setMainThread(thread);
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
    var entryClass = RawSystem.bootstrapLoader.load(options.getEntryClass());
    RawSystem.classLinker.link(entryClass);
    RawSystem.classInitializer.initialize(entryClass);
    return entryClass;
  }

  public static final String ENTRY_METHOD_NAME = "main";
  public static final String ENTRY_METHOD_DESC = "()V";
}
