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

package java.lang;

public final class Boolean {
  public static final Boolean TRUE = new Boolean(true);
  public static final Boolean FALSE = new Boolean(false);
  public static final Class<Boolean> TYPE = (Class<Boolean>) Class.getPrimitiveClass("boolean");
  private final boolean value;

  public Boolean(boolean value) {
    this.value = value;
  }

  public Boolean(String s) {
    this(parseBoolean(s));
  }

  public static boolean parseBoolean(String s) {
    return "true".equalsIgnoreCase(s);
  }

  public boolean booleanValue() {
    return value;
  }

  public static Boolean valueOf(boolean b) {
    return (b ? TRUE : FALSE);
  }

  public static Boolean valueOf(String s) {
    return parseBoolean(s) ? TRUE : FALSE;
  }

  public static String toString(boolean b) {
    return b ? "true" : "false";
  }

  public String toString() {
    return value ? "true" : "false";
  }

  @Override
  public int hashCode() {
    return Boolean.hashCode(value);
  }

  public static int hashCode(boolean value) {
    return value ? 1231 : 1237;
  }

  public boolean equals(Object obj) {
    if (obj instanceof Boolean) {
      return value == ((Boolean) obj).booleanValue();
    }
    return false;
  }

  public static boolean getBoolean(String name) {
    boolean result = false;
    try {
      result = parseBoolean(System.getProperty(name));
    } catch (IllegalArgumentException | NullPointerException e) {
    }
    return result;
  }

  public int compareTo(Boolean b) {
    return compare(this.value, b.value);
  }

  public static int compare(boolean x, boolean y) {
    return (x == y) ? 0 : (x ? 1 : -1);
  }

  public static boolean logicalAnd(boolean a, boolean b) {
    return a && b;
  }

  public static boolean logicalOr(boolean a, boolean b) {
    return a || b;
  }

  public static boolean logicalXor(boolean a, boolean b) {
    return a ^ b;
  }
}
