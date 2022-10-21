/*
 * Copyright (c) 1994, 2019, Oracle and/or its affiliates. All rights reserved.
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

public final class Integer {
  // static field
  public static final int MIN_VALUE = 0x80000000;
  public static final int MAX_VALUE = 0x7fffffff;
  public static final Class<Integer> TYPE = (Class<Integer>) Class.getPrimitiveClass("int");
  public static final int SIZE = 32;
  public static final int BYTES = SIZE / Byte.SIZE;
  public static final char[] NumChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

  // instance field
  private final int value;

  // constructure & factory method
  public Integer(int value) {
    this.value = value;
  }

  public static Integer valueOf(int i) {
    // TODO: cache
    return new Integer(i);
  }

  // instance method
  public byte byteValue() {
    return (byte) value;
  }

  public short shortValue() {
    return (short) value;
  }

  public int intValue() {
    return value;
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
    return toString(value);
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(value);
  }

  public boolean equals(Object obj) {
    if (obj instanceof Integer) {
      return value == ((Integer) obj).intValue();
    }
    return false;
  }

  // static method
  public static int hashCode(int value) {
    return value;
  }

  public static String toString(int i, int radix) {
    throw new UnsupportedOperationException("");
  }

  public static String toString(int i) {
    if (i == 0) {
      return "0";
    }
    if (i == Integer.MAX_VALUE) {
      return "2147483647";
    }
    if (i == Integer.MIN_VALUE) {
      return "-2147483648";
    }

    boolean isNegative = i < 0;
    int digit = 0;
    char[] nums = new char[10];

    i = isNegative ? (-1 * i) : i;

    while (i >= 10) {
      int a = i % 10; // 3, 2
      nums[digit] = NumChars[a];
      digit++; // (0), 1, 2
      i = (i - a) / 10;
    }
    nums[digit] = NumChars[i]; // 1

    char[] reversedNums = new char[isNegative ? digit + 2 : digit + 1];

    if (isNegative) {
      reversedNums[0] = '-';
    }

    for (int i1 = 0; i1 <= digit; i1++) {
      reversedNums[isNegative ? i1 + 1 : i1] = nums[digit - i1];
    }

    return new String(reversedNums);
  }

  public static int sum(int a, int b) {
    return a + b;
  }

  public static int max(int a, int b) {
    throw new UnsupportedOperationException("");
  }

  public static int min(int a, int b) {
    throw new UnsupportedOperationException("");
  }
}
