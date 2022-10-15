package java.lang;

import java.io.PrintStream;
import java.lang.SecurityManager;
import java.util.Properties;

public final class System {
    private static Properties props;
    public static final InputStream in = null;
    public static final PrintStream out = null;
    public static final PrintStream err = null;

    private System() {}

    public static final PrintStream out = new PrintStream("dummy");

    public static SecurityManager getSecurityManager() {
        return null;
    }

    public static Properties getProperties() {
        return props;
    }

    public static void setIn(InputStream in) {
    }
    public static void setOut(PrintStream out) {
        checkIO();
        setOut0(out);
    }
    public static void setErr(PrintStream err) {
        checkIO();
        setErr0(err);
    }
    public static Console console() {
        Console c;
        if ((c = cons) == null) {
            synchronized (System.class) {
                if ((c = cons) == null) {
                    cons = c = SharedSecrets.getJavaIOAccess().console();
                }
            }
        }
        return c;
    }
    public static Channel inheritedChannel() throws IOException {
        return SelectorProvider.provider().inheritedChannel();
    }
    public static void setSecurityManager(SecurityManager sm) {
        if (allowSecurityManager()) {
            if (security == null) {
                // ensure image reader is initialized
                Object.class.getResource("java/lang/ANY");
                // ensure the default file system is initialized
                DefaultFileSystemProvider.theFileSystem();
            }
            if (sm != null) {
                try {
                    // pre-populates the SecurityManager.packageAccess cache
                    // to avoid recursive permission checking issues with custom
                    // SecurityManager implementations
                    sm.checkPackageAccess("java.lang");
                } catch (Exception e) {
                    // no-op
                }
            }
            setSecurityManager0(sm);
        } else {
            // security manager not allowed
            if (sm != null) {
                throw new UnsupportedOperationException(
                    "Runtime configured to disallow security manager");
            }
        }
    }
    public static SecurityManager getSecurityManager() {
        if (allowSecurityManager()) {
            return security;
        } else {
            return null;
        }
    }
    public static native long currentTimeMillis();
    public static native long nanoTime();
    public static native void arraycopy(Object src,  int  srcPos,
        Object dest, int destPos,
        int length);
    public static native int identityHashCode(Object x);
    public static Properties getProperties() {
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPropertiesAccess();
        }

        return props;
    }
    public static String lineSeparator() {
        return lineSeparator;
    }


    public static void setProperties(Properties props) {
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPropertiesAccess();
        }

        if (props == null) {
            Map<String, String> tempProps = SystemProps.initProperties();
            VersionProps.init(tempProps);
            props = createProperties(tempProps);
        }
        System.props = props;
    }

    public static String getProperty(String key) {
        checkKey(key);
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPropertyAccess(key);
        }

        return props.getProperty(key);
    }

    public static String getProperty(String key, String def) {
        checkKey(key);
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPropertyAccess(key);
        }

        return props.getProperty(key, def);
    }

    public static String setProperty(String key, String value) {
        checkKey(key);
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new PropertyPermission(key,
                SecurityConstants.PROPERTY_WRITE_ACTION));
        }

        return (String) props.setProperty(key, value);
    }

    public static String clearProperty(String key) {
        checkKey(key);
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new PropertyPermission(key, "write"));
        }

        return (String) props.remove(key);
    }

    public static String getenv(String name) {
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("getenv."+name));
        }

        return ProcessEnvironment.getenv(name);
    }

    public static java.util.Map<String,String> getenv() {
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("getenv.*"));
        }

        return ProcessEnvironment.getenv();
    }

}
