package dev.ishikawa.lovejvm;

import dev.ishikawa.lovejvm.klass.parser.LClassParser;
import dev.ishikawa.lovejvm.option.Options;
import dev.ishikawa.lovejvm.option.OptionsParser;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.vm.LThread;

import java.io.*;

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
    private LThread mainThread;

    public LoveJVM(Options options) {
        this.options = options;
    }

    void run() {
        var classfileBytes = readEntryClass();

        // TODO: Use classloader instead of classparser.
        var klass = new LClassParser(classfileBytes).parse();
        var entryPoint = klass.findEntryPoint();

        // start the main thread with the entry point
        this.mainThread = entryPoint
                .map((ep) -> {
                    var thread = new LThread("main");
                    thread.init(ep);
                    thread.run();
                    return thread;
                })
                .orElseThrow(() -> {
                    throw new RuntimeException("no entrypoint");
                });
    }

    private byte[] readEntryClass() {
        // read classfile as bytes
        // TODO: correct way is not file path, but fullyQualifiedName should be passed here
        String entryClassPath = options.getEntryClass();
        byte[] classfileBytes;
        try {
            classfileBytes = ByteUtil.readClassfile(entryClassPath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("invalid entry class");
        }

        return classfileBytes;
    }
}
