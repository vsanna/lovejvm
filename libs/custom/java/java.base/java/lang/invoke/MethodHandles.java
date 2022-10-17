package java.lang.invoke;

import java.lang.reflect.Modifier;

public class MethodHandles {
    private MethodHandles() { }  // do not instantiate

    public static final class Lookup {
        public static final int PUBLIC = Modifier.PUBLIC;
        public static final int PRIVATE = Modifier.PRIVATE;
        public static final int PROTECTED = Modifier.PROTECTED;
        public static final int PACKAGE = Modifier.STATIC;
        public static final int MODULE = PACKAGE << 1;
        public static final int UNCONDITIONAL = PACKAGE << 2;
        private static final int ALL_MODES = (PUBLIC | PRIVATE | PROTECTED | PACKAGE | MODULE | UNCONDITIONAL);
        private static final int FULL_POWER_MODES = (ALL_MODES & ~UNCONDITIONAL);
        private static final int TRUSTED   = -1;

        private final Class<?> lookupClass;
        private final int allowedModes;

        Lookup(Class<?> lookupClass) {
            this(lookupClass, FULL_POWER_MODES);
        }

        private Lookup(Class<?> lookupClass, int allowedModes) {
            this.lookupClass = lookupClass;
            this.allowedModes = allowedModes;
        }

        public int lookupModes() {
            return allowedModes & ALL_MODES;
        }

        public Class<?> lookupClass() {
            return lookupClass;
        }
    }
}
