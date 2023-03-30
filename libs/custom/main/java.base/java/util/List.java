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

public interface List<E> {
  int size();

  boolean isEmpty();

  boolean contains(Object o);

  boolean add(E e);

  E get(int index);

  E set(int index, E element);

  //    static <E> List<E> of() {}
  //
  //    static <E> List<E> of(E e1) {
  //        return new ImmutableCollections.List12<>(e1);
  //    }
  //
  //    /**
//     * Returns an unmodifiable list containing two elements.
//     *
//     * See <a href="#unmodifiable">Unmodifiable Lists</a> for details.
//     *
//     * @param <E> the {@code List}'s element type
//     * @param e1 the first element
//     * @param e2 the second element
//     * @return a {@code List} containing the specified elements
//     * @throws NullPointerException if an element is {@code null}
//     *
//     * @since 9
//     */
  //    static <E> List<E> of(E e1, E e2) {
  //        return new ImmutableCollections.List12<>(e1, e2);
  //    }
  //
  //    /**
//     * Returns an unmodifiable list containing three elements.
//     *
//     * See <a href="#unmodifiable">Unmodifiable Lists</a> for details.
//     *
//     * @param <E> the {@code List}'s element type
//     * @param e1 the first element
//     * @param e2 the second element
//     * @param e3 the third element
//     * @return a {@code List} containing the specified elements
//     * @throws NullPointerException if an element is {@code null}
//     *
//     * @since 9
//     */
  //    static <E> List<E> of(E e1, E e2, E e3) {
  //        return new ImmutableCollections.ListN<>(e1, e2, e3);
  //    }
  //
  //    /**
//     * Returns an unmodifiable list containing four elements.
//     *
//     * See <a href="#unmodifiable">Unmodifiable Lists</a> for details.
//     *
//     * @param <E> the {@code List}'s element type
//     * @param e1 the first element
//     * @param e2 the second element
//     * @param e3 the third element
//     * @param e4 the fourth element
//     * @return a {@code List} containing the specified elements
//     * @throws NullPointerException if an element is {@code null}
//     *
//     * @since 9
//     */
  //    static <E> List<E> of(E e1, E e2, E e3, E e4) {
  //        return new ImmutableCollections.ListN<>(e1, e2, e3, e4);
  //    }
  //
  //    /**
//     * Returns an unmodifiable list containing five elements.
//     *
//     * See <a href="#unmodifiable">Unmodifiable Lists</a> for details.
//     *
//     * @param <E> the {@code List}'s element type
//     * @param e1 the first element
//     * @param e2 the second element
//     * @param e3 the third element
//     * @param e4 the fourth element
//     * @param e5 the fifth element
//     * @return a {@code List} containing the specified elements
//     * @throws NullPointerException if an element is {@code null}
//     *
//     * @since 9
//     */
  //    static <E> List<E> of(E e1, E e2, E e3, E e4, E e5) {
  //        return new ImmutableCollections.ListN<>(e1, e2, e3, e4, e5);
  //    }
  //
  //    /**
//     * Returns an unmodifiable list containing six elements.
//     *
//     * See <a href="#unmodifiable">Unmodifiable Lists</a> for details.
//     *
//     * @param <E> the {@code List}'s element type
//     * @param e1 the first element
//     * @param e2 the second element
//     * @param e3 the third element
//     * @param e4 the fourth element
//     * @param e5 the fifth element
//     * @param e6 the sixth element
//     * @return a {@code List} containing the specified elements
//     * @throws NullPointerException if an element is {@code null}
//     *
//     * @since 9
//     */
  //    static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6) {
  //        return new ImmutableCollections.ListN<>(e1, e2, e3, e4, e5,
  //                                                e6);
  //    }
  //
  //    /**
//     * Returns an unmodifiable list containing seven elements.
//     *
//     * See <a href="#unmodifiable">Unmodifiable Lists</a> for details.
//     *
//     * @param <E> the {@code List}'s element type
//     * @param e1 the first element
//     * @param e2 the second element
//     * @param e3 the third element
//     * @param e4 the fourth element
//     * @param e5 the fifth element
//     * @param e6 the sixth element
//     * @param e7 the seventh element
//     * @return a {@code List} containing the specified elements
//     * @throws NullPointerException if an element is {@code null}
//     *
//     * @since 9
//     */
  //    static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7) {
  //        return new ImmutableCollections.ListN<>(e1, e2, e3, e4, e5,
  //                                                e6, e7);
  //    }
  //
  //    /**
//     * Returns an unmodifiable list containing eight elements.
//     *
//     * See <a href="#unmodifiable">Unmodifiable Lists</a> for details.
//     *
//     * @param <E> the {@code List}'s element type
//     * @param e1 the first element
//     * @param e2 the second element
//     * @param e3 the third element
//     * @param e4 the fourth element
//     * @param e5 the fifth element
//     * @param e6 the sixth element
//     * @param e7 the seventh element
//     * @param e8 the eighth element
//     * @return a {@code List} containing the specified elements
//     * @throws NullPointerException if an element is {@code null}
//     *
//     * @since 9
//     */
  //    static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8) {
  //        return new ImmutableCollections.ListN<>(e1, e2, e3, e4, e5,
  //                                                e6, e7, e8);
  //    }
  //
  //    /**
//     * Returns an unmodifiable list containing nine elements.
//     *
//     * See <a href="#unmodifiable">Unmodifiable Lists</a> for details.
//     *
//     * @param <E> the {@code List}'s element type
//     * @param e1 the first element
//     * @param e2 the second element
//     * @param e3 the third element
//     * @param e4 the fourth element
//     * @param e5 the fifth element
//     * @param e6 the sixth element
//     * @param e7 the seventh element
//     * @param e8 the eighth element
//     * @param e9 the ninth element
//     * @return a {@code List} containing the specified elements
//     * @throws NullPointerException if an element is {@code null}
//     *
//     * @since 9
//     */
  //    static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9) {
  //        return new ImmutableCollections.ListN<>(e1, e2, e3, e4, e5,
  //                                                e6, e7, e8, e9);
  //    }
  //
  //    /**
//     * Returns an unmodifiable list containing ten elements.
//     *
//     * See <a href="#unmodifiable">Unmodifiable Lists</a> for details.
//     *
//     * @param <E> the {@code List}'s element type
//     * @param e1 the first element
//     * @param e2 the second element
//     * @param e3 the third element
//     * @param e4 the fourth element
//     * @param e5 the fifth element
//     * @param e6 the sixth element
//     * @param e7 the seventh element
//     * @param e8 the eighth element
//     * @param e9 the ninth element
//     * @param e10 the tenth element
//     * @return a {@code List} containing the specified elements
//     * @throws NullPointerException if an element is {@code null}
//     *
//     * @since 9
//     */
  //    static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10) {
  //        return new ImmutableCollections.ListN<>(e1, e2, e3, e4, e5,
  //                                                e6, e7, e8, e9, e10);
  //    }
}
