package dev.ishikawa.lovejvm;


import dev.ishikawa.lovejvm.option.Options;
import dev.ishikawa.lovejvm.option.OptionsParser;
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
    jvm.run();
  }

  private final Options options;

  public LoveJVM(Options options) {
    this.options = options;
  }

  public void run() {
    var entryPoint = RawSystem.bootstrapLoader
        .loadByPath(options.getEntryClass())
        .findEntryPoint();

    // start the main thread with the entry point
    var mainThread =
        entryPoint
            .map(
                (ep) -> {
                  var thread = new RawThread("main");
                  thread.init(ep);
                  thread.run();
                  return thread;
                })
            .orElseThrow(
                () -> {
                  throw new RuntimeException("no entrypoint");
                });
    RawSystem.setMainThread(mainThread);
  }

  public static final String ENTRY_METHOD_NAME = "main";
  public static final String ENTRY_METHOD_DESC = "()V";
}
