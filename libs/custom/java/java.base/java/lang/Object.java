package java.lang;

public class Object {
    public Object() {}

    public final native Class<?> getClass();

    public native int hashCode();

    public boolean equals(Object obj) {
        return (this == obj);
    }

    protected Object clone() {
        throw new UnsupportedOperationException("");
    }

    public String toString() {
        throw new UnsupportedOperationException("");
//        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }

    public final native void notify();

    public final native void notifyAll();

//    public final void wait() throws InterruptedException {
//        wait(0L);
//    }

//    public final native void wait(long timeoutMillis) throws InterruptedException;
//
//    public final void wait(long timeoutMillis, int nanos) throws InterruptedException {
//        if (timeoutMillis < 0) {
//            throw new IllegalArgumentException("timeoutMillis value is negative");
//        }
//
//        if (nanos < 0 || nanos > 999999) {
//            throw new IllegalArgumentException(
//                                "nanosecond timeout value out of range");
//        }
//
//        if (nanos > 0 && timeoutMillis < Long.MAX_VALUE) {
//            timeoutMillis++;
//        }
//
//        wait(timeoutMillis);
//    }

    protected void finalize() throws Throwable { }
}
