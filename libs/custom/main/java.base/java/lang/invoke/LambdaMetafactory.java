/*
 * Copyright (c) 2012, 2017, Oracle and/or its affiliates. All rights reserved.
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


import jdk.internal.lambda.LambdaFactory;

public final class LambdaMetafactory {

  private LambdaMetafactory() {}

  public static final int FLAG_SERIALIZABLE = 1 << 0;
  public static final int FLAG_MARKERS = 1 << 1;
  public static final int FLAG_BRIDGES = 1 << 2;

  private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];
  private static final MethodType[] EMPTY_MT_ARRAY = new MethodType[0];

  public static CallSite metafactory(
      MethodHandles.Lookup caller, // not used now. always null.
      String invokedName, // run, accept, apply ... など
      MethodType invokedType, // lambdaを作る型. (captureするargs) -> lambda
      MethodType samMethodType, // not used now.
      MethodHandle implMethod, // 実装本体への参照 REF_invokeStatic
      // book/LambdaSample.lambda$test3$2:(Ljava/lang/Integer;)Ljava/lang/String;
      MethodType
          instantiatedMethodType // lambdaが満たすsignature (Ljava/lang/Integer;)Ljava/lang/String;
      ) throws LambdaConversionException {
    return LambdaFactory.createCallSite(
        invokedType, invokedName, implMethod, instantiatedMethodType);
  }
}
