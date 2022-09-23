package dev.ishikawa.lovejvm;


import dev.ishikawa.lovejvm.option.Options;
import dev.ishikawa.lovejvm.option.OptionsParser;
import dev.ishikawa.lovejvm.rawclass.parser.RawClassParser;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.RawThread;

/** LoveJVM is the starting point of this hand-made JVM! */
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
    var classfileBytes = ByteUtil.readBytesFromFilePath(options.getEntryClass());

    // TODO: Use classloader instead of classparser. init class object, put it in heap, etc
    // var entryPoint = SystemClassLoader.load(classfileBytes)
    // SystemClassLoader.loadClasspath(classpath) ... register multiple classes into methodArea
    // ?? when to do dynamic load?
    var rawClass = new RawClassParser(classfileBytes).parse();
    RawSystem.methodArea.register(rawClass);
    var entryPoint = rawClass.findEntryPoint();

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
