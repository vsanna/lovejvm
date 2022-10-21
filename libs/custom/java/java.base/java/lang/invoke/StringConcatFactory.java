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


import java.lang.invoke.MethodHandles.Lookup;
import java.util.List;
import java.util.Objects;

public final class StringConcatFactory {
  private static final int MAX_INDY_CONCAT_ARG_SLOTS = 200;

  public static CallSite makeConcat(Lookup lookup, String name, MethodType concatType)
      throws StringConcatException {
    // This bootstrap method is unlikely to be used in practice,
    // avoid optimizing it at the expense of makeConcatWithConstants

    // Mock the recipe to reuse the concat generator code
    String recipe = "\u0001".repeat(concatType.parameterCount());
    return makeConcatWithConstants(lookup, name, concatType, recipe);
  }

  public static CallSite makeConcatWithConstants(
      Lookup lookup, String name, MethodType concatType, String recipe, Object... constants)
      throws StringConcatException {
    Objects.requireNonNull(lookup, "Lookup is null");
    Objects.requireNonNull(name, "Name is null");
    Objects.requireNonNull(concatType, "Concat type is null");
    Objects.requireNonNull(constants, "Constants are null");

    for (Object o : constants) {
      Objects.requireNonNull(o, "Cannot accept null constants");
    }

    if ((lookup.lookupModes() & Lookup.PRIVATE) == 0) {
      throw new StringConcatException("Invalid caller: " + lookup.lookupClass().getName());
    }

    List<String> elements = parseRecipe(concatType, recipe, constants);

    if (!concatType.returnType().isAssignableFrom(String.class)) {
      throw new StringConcatException(
          "The return type should be compatible with String, but it is " + concatType.returnType());
    }

    if (concatType.parameterSlotCount() > MAX_INDY_CONCAT_ARG_SLOTS) {
      throw new StringConcatException(
          "Too many concat argument slots: "
              + concatType.parameterSlotCount()
              + ", can only accept "
              + MAX_INDY_CONCAT_ARG_SLOTS);
    }

    try {
      // TODO
      return new ConstantCallSite(null);
    } catch (Error e) {
      // Pass through any error
      throw e;
    } catch (Throwable t) {
      throw new StringConcatException("Generator failed", t);
    }
  }

  private static MethodHandle generateMHInlineCopy(MethodType concatType, List<String> elements) {
    return null;
  }

  private static List<String> parseRecipe(
      MethodType concatType, String recipe, Object[] constants) {
    return null;
  }

  private StringConcatFactory() {}
}
