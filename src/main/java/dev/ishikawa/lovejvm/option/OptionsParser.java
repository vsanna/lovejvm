package dev.ishikawa.lovejvm.option;

import java.util.Arrays;
import java.util.List;

public class OptionsParser {
    /**
     * args format should follow below:
     * [entry class path]: must
     * [[option name] ([option value])] * n : optional
     * */
    public static Options parse(String[] args) {
        if(args.length == 0) {
            throw new RuntimeException("No entry point is given.");
        }

        Options.Builder builder = Options.builder();
        builder.setEntryClass(args[0]);

        if(args.length > 1) {
            List<String> remainingOptions = List.of(Arrays.copyOfRange(args, 1, args.length - 1));

            for (String arg : remainingOptions) {
                // TODO: impl option parser
            }
        }

        return builder.build();

//        return new Options(
////                "guest/out/Add.class",
////                "guest/out/ForLoop.class",
////                "guest/out/Recursive.class",
//                "guest/out/Recursive2.class",
//                LogLevel.INFO
//        );
    }
}
