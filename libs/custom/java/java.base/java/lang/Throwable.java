package java.lang;

import java.io.PrintStream;

public class Throwable {
    private static final StackTraceElement[] UNASSIGNED_STACK = new StackTraceElement[0];

    private String detailMessage;
    private Throwable cause = this;
    private StackTraceElement[] stackTrace = UNASSIGNED_STACK;

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
        for (int i = 0; i < stackTrace.length; i++) {
            s.println(stackTrace[i].formatted());
        }
    }

    public synchronized Throwable fillInStackTrace() {
        throw new UnsupportedOperationException("");
    }

    public String getMessage() {
        return detailMessage;
    }

    public String toString() {
        throw new UnsupportedOperationException("");
    }

    public String getLocalizedMessage() {
        return getMessage();
    }
}
