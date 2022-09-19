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
}
