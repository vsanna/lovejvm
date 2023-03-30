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

package java.util.locale;

public final class BaseLocale {

  public static BaseLocale[] constantBaseLocales;
  public static final byte ENGLISH = 0,
      FRENCH = 1,
      GERMAN = 2,
      ITALIAN = 3,
      JAPANESE = 4,
      KOREAN = 5,
      CHINESE = 6,
      SIMPLIFIED_CHINESE = 7,
      TRADITIONAL_CHINESE = 8,
      FRANCE = 9,
      GERMANY = 10,
      ITALY = 11,
      JAPAN = 12,
      KOREA = 13,
      UK = 14,
      US = 15,
      CANADA = 16,
      CANADA_FRENCH = 17,
      ROOT = 18,
      NUM_CONSTANTS = 19;

  static {
    BaseLocale[] baseLocales = constantBaseLocales;
    if (baseLocales == null) {
      baseLocales = new BaseLocale[NUM_CONSTANTS];
      baseLocales[ENGLISH] = createInstance("en", "");
      baseLocales[FRENCH] = createInstance("fr", "");
      baseLocales[GERMAN] = createInstance("de", "");
      baseLocales[ITALIAN] = createInstance("it", "");
      baseLocales[JAPANESE] = createInstance("ja", "");
      baseLocales[KOREAN] = createInstance("ko", "");
      baseLocales[CHINESE] = createInstance("zh", "");
      baseLocales[SIMPLIFIED_CHINESE] = createInstance("zh", "CN");
      baseLocales[TRADITIONAL_CHINESE] = createInstance("zh", "TW");
      baseLocales[FRANCE] = createInstance("fr", "FR");
      baseLocales[GERMANY] = createInstance("de", "DE");
      baseLocales[ITALY] = createInstance("it", "IT");
      baseLocales[JAPAN] = createInstance("ja", "JP");
      baseLocales[KOREA] = createInstance("ko", "KR");
      baseLocales[UK] = createInstance("en", "GB");
      baseLocales[US] = createInstance("en", "US");
      baseLocales[CANADA] = createInstance("en", "CA");
      baseLocales[CANADA_FRENCH] = createInstance("fr", "CA");
      baseLocales[ROOT] = createInstance("", "");
      constantBaseLocales = baseLocales;
    }
  }

  public static final String SEP = "_";

  private final String language;
  private final String script;
  private final String region;
  private final String variant;

  private volatile int hash;

  // This method must be called with normalize = false only when creating the
  // Locale.* constants and non-normalized BaseLocale$Keys used for lookup.
  private BaseLocale(
      String language, String script, String region, String variant, boolean normalize) {
    this.language = language;
    this.script = script;
    this.region = region;
    this.variant = variant;
  }

  // Called for creating the Locale.* constants. No argument
  // validation is performed.
  private static BaseLocale createInstance(String language, String region) {
    return new BaseLocale(language, "", region, "", false);
  }

  public static BaseLocale getInstance(
      String language, String script, String region, String variant) {
    throw new UnsupportedOperationException("");
  }

  public String getLanguage() {
    return language;
  }

  public String getScript() {
    return script;
  }

  public String getRegion() {
    return region;
  }

  public String getVariant() {
    return variant;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof BaseLocale)) {
      return false;
    }
    BaseLocale other = (BaseLocale) obj;
    return language == other.language
        && script == other.script
        && region == other.region
        && variant == other.variant;
  }

  @Override
  public String toString() {
    throw new UnsupportedOperationException("");
  }

  @Override
  public int hashCode() {
    int h = hash;
    if (h == 0) {
      // Generating a hash value from language, script, region and variant
      h = language.hashCode();
      h = 31 * h + script.hashCode();
      h = 31 * h + region.hashCode();
      h = 31 * h + variant.hashCode();
      if (h != 0) {
        hash = h;
      }
    }
    return h;
  }
}
