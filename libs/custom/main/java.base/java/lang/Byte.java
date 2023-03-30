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

public final class Byte {
  public static final byte MIN_VALUE = -128;
  public static final byte MAX_VALUE = 127;
  public static final Class<Byte> TYPE = (Class<Byte>) Class.getPrimitiveClass("byte");
  public static final int SIZE = 8;
  public static final int BYTES = SIZE / Byte.SIZE;
  private final byte value;

  public Byte(byte value) {
    this.value = value;
  }

  public static Byte valueOf(byte b) {
    // TODO: cache
    throw new UnsupportedOperationException("");
  }

  public byte byteValue() {
    return value;
  }

  public short shortValue() {
    return (short) value;
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
    return Byte.hashCode(value);
  }

  public boolean equals(Object obj) {
    if (obj instanceof Byte) {
      return value == ((Byte) obj).byteValue();
    }
    return false;
  }

  public static int hashCode(byte value) {
    return (int) value;
  }

  public static String toString(byte b) {
    return Integer.toString((int) b, 10);
  }
}
