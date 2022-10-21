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

package java.lang.reflect;

public class Modifier {
  public static final int PUBLIC = 0x00000001;
  public static final int PRIVATE = 0x00000002;
  public static final int PROTECTED = 0x00000004;
  public static final int STATIC = 0x00000008;
  public static final int FINAL = 0x00000010;
  public static final int SYNCHRONIZED = 0x00000020;
  public static final int VOLATILE = 0x00000040;
  public static final int TRANSIENT = 0x00000080;
  public static final int NATIVE = 0x00000100;
  public static final int INTERFACE = 0x00000200;
  public static final int ABSTRACT = 0x00000400;
  public static final int STRICT = 0x00000800;

  static final int BRIDGE = 0x00000040;
  static final int VARARGS = 0x00000080;
  static final int SYNTHETIC = 0x00001000;
  static final int ANNOTATION = 0x00002000;
  static final int ENUM = 0x00004000;
  static final int MANDATED = 0x00008000;

  private static final int CLASS_MODIFIERS =
      Modifier.PUBLIC
          | Modifier.PROTECTED
          | Modifier.PRIVATE
          | Modifier.ABSTRACT
          | Modifier.STATIC
          | Modifier.FINAL
          | Modifier.STRICT;

  private static final int INTERFACE_MODIFIERS =
      Modifier.PUBLIC
          | Modifier.PROTECTED
          | Modifier.PRIVATE
          | Modifier.ABSTRACT
          | Modifier.STATIC
          | Modifier.STRICT;

  private static final int CONSTRUCTOR_MODIFIERS =
      Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE;

  private static final int METHOD_MODIFIERS =
      Modifier.PUBLIC
          | Modifier.PROTECTED
          | Modifier.PRIVATE
          | Modifier.ABSTRACT
          | Modifier.STATIC
          | Modifier.FINAL
          | Modifier.SYNCHRONIZED
          | Modifier.NATIVE
          | Modifier.STRICT;

  private static final int FIELD_MODIFIERS =
      Modifier.PUBLIC
          | Modifier.PROTECTED
          | Modifier.PRIVATE
          | Modifier.STATIC
          | Modifier.FINAL
          | Modifier.TRANSIENT
          | Modifier.VOLATILE;

  private static final int PARAMETER_MODIFIERS = Modifier.FINAL;

  static final int ACCESS_MODIFIERS = Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE;
}
