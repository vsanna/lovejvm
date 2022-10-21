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

public final class Short {
  public static final short MIN_VALUE = -32768;
  public static final short MAX_VALUE = 32767;
  public static final Class<Short> TYPE = (Class<Short>) Class.getPrimitiveClass("short");
  public static final int SIZE = 16;
  public static final int BYTES = SIZE / Byte.SIZE;

  public static String toString(short s) {
    return Integer.toString((int) s, 10);
  }

  public static short parseShort(String s, int radix) {
    throw new UnsupportedOperationException("");
  }

  public static short parseShort(String s) throws NumberFormatException {
    return parseShort(s, 10);
  }

  public static Short valueOf(String s, int radix) throws NumberFormatException {
    return valueOf(parseShort(s, radix));
  }

  public static Short valueOf(String s) throws NumberFormatException {
    return valueOf(s, 10);
  }

  public static Short valueOf(short s) {
    throw new UnsupportedOperationException("");
  }

  public static Short decode(String nm) throws NumberFormatException {
    throw new UnsupportedOperationException("");
  }

  private final short value;

  public Short(short value) {
    this.value = value;
  }

  public Short(String s) throws NumberFormatException {
    this.value = parseShort(s, 10);
  }

  public byte byteValue() {
    return (byte) value;
  }

  public short shortValue() {
    return value;
  }

  public int intValue() {
    return (int) value;
  }

  public long longValue() {
    return (long) value;
  }

  public float floatValue() {
    return (float) value;
  }

  public double doubleValue() {
    return (double) value;
  }

  public String toString() {
    return Integer.toString((int) value);
  }

  @Override
  public int hashCode() {
    return Short.hashCode(value);
  }

  public static int hashCode(short value) {
    return (int) value;
  }

  public boolean equals(Object obj) {
    if (obj instanceof Short) {
      return value == ((Short) obj).shortValue();
    }
    return false;
  }
}
