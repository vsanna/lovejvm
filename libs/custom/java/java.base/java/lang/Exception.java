package java.lang;

public class Exception extends Throwable {

    public Exception() {
        super();
    }

    public Exception(String message) {
        super(message);
    }

    public Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public Exception(Throwable cause) {
        super(cause);
    }

    protected Exception(
        String message, Throwable cause,
        boolean enableSuppression,
        boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
