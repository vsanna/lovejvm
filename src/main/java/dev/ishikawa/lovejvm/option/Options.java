package dev.ishikawa.lovejvm.option;


import dev.ishikawa.lovejvm.logging.LogLevel;
import org.jetbrains.annotations.NotNull;

public class Options {
  private final String ENTRY_CLASS;
  private final LogLevel LOG_LEVEL;

  public Options(String entryClass, LogLevel logLevel) {
    this.ENTRY_CLASS = entryClass;
    this.LOG_LEVEL = logLevel;
  }

  public String getEntryClass() {
    return ENTRY_CLASS;
  }

  public LogLevel getLogLevel() {
    return LOG_LEVEL;
  }

  static Builder builder() {
    return new Builder();
  }

  static class Builder {
    private String entryClass;
    private LogLevel logLevel;

    public Builder() {
      setEntryClass(
          //                     "guest/out/main/dev/ishikawa/sample/Add.class"
          //                     "guest/out/main/dev/ishikawa/sample/ForLoop.class"
          //                     "guest/out/main/dev/ishikawa/sample/Recursive.class"
          //                     "guest/out/main/dev/ishikawa/sample/Recursive2.class"
          //                     "guest/out/main/dev/ishikawa/sample/Math.class"
          //                     "guest/out/main/dev/ishikawa/sample/TypeCast.class"
          //                     "guest/out/main/dev/ishikawa/sample/BasicClass.class"
          //                     "guest/out/main/dev/ishikawa/sample/InstanceNew.class"
          //                     "guest/out/main/dev/ishikawa/sample/InstanceNew2.class"
          //                     "guest/out/main/dev/ishikawa/sample/ArraySample.class"
          //                     "guest/out/main/dev/ishikawa/sample/SystemOut.class"
          //                     "guest/out/main/dev/ishikawa/sample/Boxing.class" // FIX
          //                     "guest/out/main/dev/ishikawa/sample/ListSample.class"
          //                     "guest/out/main/dev/ishikawa/sample/MapSample1.class"
          //                     "guest/out/main/dev/ishikawa/sample/MapSample2.class"
                               "guest/out/main/dev/ishikawa/sample/BasicClass2.class"
//          "guest/out/main/dev/ishikawa/sample/LambdaSample.class"
          //                     "guest/out/test/dev/ishikawa/sample/PrintStreamTest.class"
          );
      setLogLevel(LogLevel.DEBUG);
    }

    public void setEntryClass(@NotNull String entryClass) {
      this.entryClass = entryClass;
    }

    public void setLogLevel(@NotNull LogLevel logLevel) {
      this.logLevel = logLevel;
    }

    public Options build() {
      return new Options(builder().entryClass, builder().logLevel);
    }
  }
}
