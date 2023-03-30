/*
 * Copyright (c) 2002, 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package java.lang.invoke;


import java.lang.reflect.Modifier;

public class MethodHandles {
  private MethodHandles() {} // do not instantiate

  public static final class Lookup {
    public static final int PUBLIC = Modifier.PUBLIC;
    public static final int PRIVATE = Modifier.PRIVATE;
    public static final int PROTECTED = Modifier.PROTECTED;
    public static final int PACKAGE = Modifier.STATIC;
    public static final int MODULE = PACKAGE << 1;
    public static final int UNCONDITIONAL = PACKAGE << 2;
    private static final int ALL_MODES =
        (PUBLIC | PRIVATE | PROTECTED | PACKAGE | MODULE | UNCONDITIONAL);
    private static final int FULL_POWER_MODES = (ALL_MODES & ~UNCONDITIONAL);
    private static final int TRUSTED = -1;

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
