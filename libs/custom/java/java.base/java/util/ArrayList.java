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

package java.util;

public class ArrayList<E> implements List<E> {
  private Object[] elementData; // non-private to simplify nested class access
  private int size = 0;

  public ArrayList() {
    this.elementData = new Object[0];
  }

  public ArrayList(int capacity) {
    this.elementData = new Object[capacity];
  }

  public int size() {
    return size;
  }

  public boolean isEmpty() {
    return elementData.length == 0;
  }

  public boolean contains(Object o) {
    return indexOf(o) >= 0;
  }

  public int indexOf(Object o) {
    return indexOfRange(o, 0, size);
  }

  int indexOfRange(Object o, int start, int end) {
    Object[] es = elementData;
    if (o == null) {
      for (int i = start; i < end; i++) {
        if (es[i] == null) {
          return i;
        }
      }
    } else {
      for (int i = start; i < end; i++) {
        if (o.equals(es[i])) {
          return i;
        }
      }
    }
    return -1;
  }

  public boolean add(E e) {
    if (size == elementData.length) {
      elementData = grow();
    }
    elementData[size] = e;
    size = size + 1;

    return true;
  }

  public E get(int index) {
    return (E) elementData[index];
  }

  public E set(int index, E element) {
    elementData[index] = element;
    return element;
  }

  private Object[] grow() {
    return grow(size + 1);
  }

  private Object[] grow(int minCapacity) {
    int oldCapacity = elementData.length;
    int newCapacity = oldCapacity * 2;

    Object[] newElementData = new Object[newCapacity];
    for (int i = 0; i < elementData.length; i++) {
      newElementData[i] = elementData[i];
    }

    return newElementData;
  }
}
