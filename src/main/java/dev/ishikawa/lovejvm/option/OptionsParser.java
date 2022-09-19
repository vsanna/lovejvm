package dev.ishikawa.lovejvm.option;

import dev.ishikawa.lovejvm.logging.LogLevel;

public class OptionsParser {
    public static Options parse(String[] args) {
        // TODO: this is mock.
        return new Options(
//                "guest/out/ForLoop.class", // "test", "()I");
                "guest/out/Add.class", // "add", "()V"
                LogLevel.INFO
        );
    }
}
