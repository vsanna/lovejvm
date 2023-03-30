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

public abstract class Enum<E extends Enum<E>> {
  private final String name;

  public final String name() {
    return name;
  }

  private final int ordinal;

  public final int ordinal() {
    return ordinal;
  }

  protected Enum(String name, int ordinal) {
    this.name = name;
    this.ordinal = ordinal;
  }

  public String toString() {
    return name;
  }

  public final boolean equals(Object other) {
    return this == other;
  }

  public final int hashCode() {
    return super.hashCode();
  }

  public final int compareTo(E o) {
    Enum<?> other = (Enum<?>) o;
    Enum<E> self = this;
    if (self.getClass() != other.getClass()
        && // optimization
        self.getDeclaringClass() != other.getDeclaringClass()) throw new ClassCastException();
    return self.ordinal - other.ordinal;
  }

  public final Class<E> getDeclaringClass() {
    Class<?> clazz = getClass();
    Class<?> zuper = clazz.getSuperclass();
    return (zuper == Enum.class) ? (Class<E>) clazz : (Class<E>) zuper;
  }

  public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name) {
    throw new UnsupportedOperationException("");
  }

  protected final void finalize() {}
}
