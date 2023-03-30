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

public final class Float {
  public static final float POSITIVE_INFINITY = 1.0f / 0.0f;
  public static final float NEGATIVE_INFINITY = -1.0f / 0.0f;
  public static final float NaN = 0.0f / 0.0f;
  public static final float MAX_VALUE = 0x1.fffffeP+127f; // 3.4028235e+38f
  public static final float MIN_NORMAL = 0x1.0p-126f; // 1.17549435E-38f
  public static final float MIN_VALUE = 0x0.000002P-126f; // 1.4e-45f
  public static final int MAX_EXPONENT = 127;
  public static final int MIN_EXPONENT = -126;
  public static final int SIZE = 32;
  public static final int BYTES = SIZE / Byte.SIZE;
  public static final Class<Float> TYPE = (Class<Float>) Class.getPrimitiveClass("float");

  public static String toString(float f) {
    throw new UnsupportedOperationException("");
  }

  public static Float valueOf(String s) throws NumberFormatException {
    return new Float(parseFloat(s));
  }

  public static Float valueOf(float f) {
    return new Float(f);
  }

  public static float parseFloat(String s) throws NumberFormatException {
    throw new UnsupportedOperationException("");
  }

  public static boolean isNaN(float v) {
    return (v != v);
  }

  private final float value;

  public Float(float value) {
    this.value = value;
  }

  public Float(double value) {
    this.value = (float) value;
  }

  public Float(String s) throws NumberFormatException {
    value = parseFloat(s);
  }

  public String toString() {
    return Float.toString(value);
  }

  public byte byteValue() {
    return (byte) value;
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
    return value;
  }

  public double doubleValue() {
    return (double) value;
  }

  @Override
  public int hashCode() {
    return Float.hashCode(value);
  }

  public static int hashCode(float value) {
    return floatToIntBits(value);
  }

  public boolean equals(Object obj) {
    return (obj instanceof Float) && (floatToIntBits(((Float) obj).value) == floatToIntBits(value));
  }

  public static int floatToIntBits(float value) {
    if (!isNaN(value)) {
      return floatToRawIntBits(value);
    }
    return 0x7fc00000;
  }

  public static native int floatToRawIntBits(float value);

  public static native float intBitsToFloat(int bits);

  public int compareTo(Float anotherFloat) {
    return Float.compare(value, anotherFloat.value);
  }

  public static int compare(float f1, float f2) {
    if (f1 < f2) return -1; // Neither val is NaN, thisVal is smaller
    if (f1 > f2) return 1; // Neither val is NaN, thisVal is larger

    // Cannot use floatToRawIntBits because of possibility of NaNs.
    int thisBits = Float.floatToIntBits(f1);
    int anotherBits = Float.floatToIntBits(f2);

    return (thisBits == anotherBits
        ? 0
        : // Values are equal
        (thisBits < anotherBits
            ? -1
            : // (-0.0, 0.0) or (!NaN, NaN)
            1)); // (0.0, -0.0) or (NaN, !NaN)
  }

  public static float sum(float a, float b) {
    return a + b;
  }

  public static float max(float a, float b) {
    return Math.max(a, b);
  }

  public static float min(float a, float b) {
    return Math.min(a, b);
  }
}
