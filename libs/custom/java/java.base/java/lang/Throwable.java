package java.lang;

import java.io.PrintStream;
import java.util.List;
import java.util.ArrayList;

public class Throwable {
    private static final StackTraceElement[] UNASSIGNED_STACK = new StackTraceElement[0];
    private static final List<Throwable> SUPPRESSED_SENTINEL = new ArrayList<>();

    private String detailMessage;
    private Throwable cause = this;
    private StackTraceElement[] stackTrace = UNASSIGNED_STACK;
    private transient int depth;
    private List<Throwable> suppressedExceptions = SUPPRESSED_SENTINEL;

    public Throwable() {}

    public Throwable(String message) {
        detailMessage = message;
    }

    public Throwable(String message, Throwable cause) {
        detailMessage = message;
        this.cause = cause;
    }

    public Throwable(Throwable cause) {
        detailMessage = (cause == null ? null : cause.toString());
        this.cause = cause;
    }

    protected Throwable(
        String message,
        Throwable cause,
        boolean enableSuppression,
        boolean writableStackTrace
    ) {
        stackTrace = null;
        detailMessage = message;
        this.cause = cause;
    }

    public void printStackTrace() {
        printStackTrace(System.err);
    }

    public void printStackTrace(PrintStream s) {
        // TODO:
    }

    public synchronized Throwable fillInStackTrace() {
        throw new UnsupportedOperationException("");
    }

    public String getMessage() {
        return detailMessage;
    }

    public String toString() {
        throw new UnsupportedOperationException("");
//        String s = getClass().getName();
//        String message = getLocalizedMessage();
//        return (message != null) ? (s + ": " + message) : s;
    }

    public String getLocalizedMessage() {
        return getMessage();
    }
}
