/*
 * Copyright (c) 1996, 2019, Oracle and/or its affiliates. All rights reserved.
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

/*
 * (C) Copyright Taligent, Inc. 1996, 1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1998 - All Rights Reserved
 *
 * The original version of this source code and documentation
 * is copyrighted and owned by Taligent, Inc., a wholly-owned
 * subsidiary of IBM. These materials are provided under terms
 * of a License Agreement between Taligent and Sun. This technology
 * is protected by multiple US and International patents.
 *
 * This notice and attribution to Taligent may not be removed.
 * Taligent is a registered trademark of Taligent, Inc.
 *
 */

package java.util;


import java.util.locale.BaseLocale;

public final class Locale {
  public static final Locale ENGLISH;
  public static final Locale FRENCH;
  public static final Locale GERMAN;
  public static final Locale ITALIAN;
  public static final Locale JAPANESE;
  public static final Locale KOREAN;
  public static final Locale CHINESE;
  public static final Locale SIMPLIFIED_CHINESE;
  public static final Locale TRADITIONAL_CHINESE;
  public static final Locale FRANCE;
  public static final Locale GERMANY;
  public static final Locale ITALY;
  public static final Locale JAPAN;
  public static final Locale KOREA;
  public static final Locale UK;
  public static final Locale US;
  public static final Locale CANADA;
  public static final Locale CANADA_FRENCH;
  public static final Locale ROOT;

  private static final Map<BaseLocale, Locale> CONSTANT_LOCALES = new HashMap<>();

  static {
    ENGLISH = createConstant(BaseLocale.ENGLISH);
    FRENCH = createConstant(BaseLocale.FRENCH);
    GERMAN = createConstant(BaseLocale.GERMAN);
    ITALIAN = createConstant(BaseLocale.ITALIAN);
    JAPANESE = createConstant(BaseLocale.JAPANESE);
    KOREAN = createConstant(BaseLocale.KOREAN);
    CHINESE = createConstant(BaseLocale.CHINESE);
    SIMPLIFIED_CHINESE = createConstant(BaseLocale.SIMPLIFIED_CHINESE);
    TRADITIONAL_CHINESE = createConstant(BaseLocale.TRADITIONAL_CHINESE);
    FRANCE = createConstant(BaseLocale.FRANCE);
    GERMANY = createConstant(BaseLocale.GERMANY);
    ITALY = createConstant(BaseLocale.ITALY);
    JAPAN = createConstant(BaseLocale.JAPAN);
    KOREA = createConstant(BaseLocale.KOREA);
    UK = createConstant(BaseLocale.UK);
    US = createConstant(BaseLocale.US);
    CANADA = createConstant(BaseLocale.CANADA);
    CANADA_FRENCH = createConstant(BaseLocale.CANADA_FRENCH);
    ROOT = createConstant(BaseLocale.ROOT);
  }

  public static final Locale CHINA = SIMPLIFIED_CHINESE;
  public static final Locale PRC = SIMPLIFIED_CHINESE;
  public static final Locale TAIWAN = TRADITIONAL_CHINESE;

  private transient BaseLocale baseLocale;
  private transient Object localeExtensions;

  private Locale(BaseLocale baseLocale, Object extensions) {
    this.baseLocale = baseLocale;
    this.localeExtensions = extensions;
  }

  private static Locale createConstant(byte baseType) {
    BaseLocale base = BaseLocale.constantBaseLocales[baseType];
    Locale locale = new Locale(base, null);
    CONSTANT_LOCALES.put(base, locale);
    return locale;
  }
}
