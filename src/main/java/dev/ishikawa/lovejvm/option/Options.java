package dev.ishikawa.lovejvm.option;


import dev.ishikawa.lovejvm.logging.LogLevel;
import java.util.Optional;

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

    public void setEntryClass(String entryClass) {
      this.entryClass = entryClass;
    }

    public void setLogLevel(LogLevel logLevel) {
      this.logLevel = logLevel;
    }

    public Options build() {

      return new Options(
          // REFACTOR: this should throw exception when entry class is not given
          Optional.ofNullable(entryClass)
              .orElse(
//                  "guest/out/Add.class"
//                                     "guest/out/ForLoop.class"
//                                     "guest/out/dev/ishikawa/test/Recursive.class"
//                                     "guest/out/dev/ishikawa/test/Recursive2.class"
//                                     "guest/out/dev/ishikawa/test/ArraySample.class"
//                  "guest/out/BasicClass.class"
//                                    "guest/out/dev/ishikawa/test/InstanceNew.class"
//                                    "guest/out/dev/ishikawa/test/InstanceNew2.class"
                                    "guest/out/dev/ishikawa/test/SystemOut.class"
                  ),
          Optional.ofNullable(logLevel).orElse(LogLevel.INFO));
    }
  }
}
