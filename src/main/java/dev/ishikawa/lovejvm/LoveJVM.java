package dev.ishikawa.lovejvm;

import dev.ishikawa.lovejvm.klass.parser.LClassParser;
import dev.ishikawa.lovejvm.option.Options;
import dev.ishikawa.lovejvm.option.OptionsParser;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.vm.LSystem;
import dev.ishikawa.lovejvm.vm.LThread;

/**
 * LoveJVM is the starting point of this hand-made JVM!
 * */
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
        var klass = new LClassParser(classfileBytes).parse();

        // debug
        System.out.printf("size = %d, length=%d\n", klass.size(), klass.getRaw().length);


        LSystem.methodArea.register(klass);
        var entryPoint = klass.findEntryPoint();

        // start the main thread with the entry point
        var mainThread = entryPoint
                .map((ep) -> {
                    var thread = new LThread("main");
                    thread.stackUp(ep);
                    thread.run();
                    return thread;
                })
                .orElseThrow(() -> {
                    throw new RuntimeException("no entrypoint");
                });
        LSystem.setMainThread(mainThread);
    }
}
