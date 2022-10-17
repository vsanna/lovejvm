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

/**
 * A {@code Locale} object represents a specific geographical, political,
 * or cultural region. An operation that requires a {@code Locale} to perform
 * its task is called <em>locale-sensitive</em> and uses the {@code Locale}
 * to tailor information for the user. For example, displaying a number
 * is a locale-sensitive operation&mdash; the number should be formatted
 * according to the customs and conventions of the user's native country,
 * region, or culture.
 *
 * <p> The {@code Locale} class implements IETF BCP 47 which is composed of
 * <a href="http://tools.ietf.org/html/rfc4647">RFC 4647 "Matching of Language
 * Tags"</a> and <a href="http://tools.ietf.org/html/rfc5646">RFC 5646 "Tags
 * for Identifying Languages"</a> with support for the LDML (UTS#35, "Unicode
 * Locale Data Markup Language") BCP 47-compatible extensions for locale data
 * exchange.
 *
 * <p> A {@code Locale} object logically consists of the fields
 * described below.
 *
 * <dl>
 *   <dt><a id="def_language"><b>language</b></a></dt>
 *
 *   <dd>ISO 639 alpha-2 or alpha-3 language code, or registered
 *   language subtags up to 8 alpha letters (for future enhancements).
 *   When a language has both an alpha-2 code and an alpha-3 code, the
 *   alpha-2 code must be used.  You can find a full list of valid
 *   language codes in the IANA Language Subtag Registry (search for
 *   "Type: language").  The language field is case insensitive, but
 *   {@code Locale} always canonicalizes to lower case.</dd>
 *
 *   <dd>Well-formed language values have the form
 *   <code>[a-zA-Z]{2,8}</code>.  Note that this is not the full
 *   BCP47 language production, since it excludes extlang.  They are
 *   not needed since modern three-letter language codes replace
 *   them.</dd>
 *
 *   <dd>Example: "en" (English), "ja" (Japanese), "kok" (Konkani)</dd>
 *
 *   <dt><a id="def_script"><b>script</b></a></dt>
 *
 *   <dd>ISO 15924 alpha-4 script code.  You can find a full list of
 *   valid script codes in the IANA Language Subtag Registry (search
 *   for "Type: script").  The script field is case insensitive, but
 *   {@code Locale} always canonicalizes to title case (the first
 *   letter is upper case and the rest of the letters are lower
 *   case).</dd>
 *
 *   <dd>Well-formed script values have the form
 *   <code>[a-zA-Z]{4}</code></dd>
 *
 *   <dd>Example: "Latn" (Latin), "Cyrl" (Cyrillic)</dd>
 *
 *   <dt><a id="def_region"><b>country (region)</b></a></dt>
 *
 *   <dd>ISO 3166 alpha-2 country code or UN M.49 numeric-3 area code.
 *   You can find a full list of valid country and region codes in the
 *   IANA Language Subtag Registry (search for "Type: region").  The
 *   country (region) field is case insensitive, but
 *   {@code Locale} always canonicalizes to upper case.</dd>
 *
 *   <dd>Well-formed country/region values have
 *   the form <code>[a-zA-Z]{2} | [0-9]{3}</code></dd>
 *
 *   <dd>Example: "US" (United States), "FR" (France), "029"
 *   (Caribbean)</dd>
 *
 *   <dt><a id="def_variant"><b>variant</b></a></dt>
 *
 *   <dd>Any arbitrary value used to indicate a variation of a
 *   {@code Locale}.  Where there are two or more variant values
 *   each indicating its own semantics, these values should be ordered
 *   by importance, with most important first, separated by
 *   underscore('_').  The variant field is case sensitive.</dd>
 *
 *   <dd>Note: IETF BCP 47 places syntactic restrictions on variant
 *   subtags.  Also BCP 47 subtags are strictly used to indicate
 *   additional variations that define a language or its dialects that
 *   are not covered by any combinations of language, script and
 *   region subtags.  You can find a full list of valid variant codes
 *   in the IANA Language Subtag Registry (search for "Type: variant").
 *
 *   <p>However, the variant field in {@code Locale} has
 *   historically been used for any kind of variation, not just
 *   language variations.  For example, some supported variants
 *   available in Java SE Runtime Environments indicate alternative
 *   cultural behaviors such as calendar type or number script.  In
 *   BCP 47 this kind of information, which does not identify the
 *   language, is supported by extension subtags or private use
 *   subtags.</dd>
 *
 *   <dd>Well-formed variant values have the form <code>SUBTAG
 *   (('_'|'-') SUBTAG)*</code> where <code>SUBTAG =
 *   [0-9][0-9a-zA-Z]{3} | [0-9a-zA-Z]{5,8}</code>. (Note: BCP 47 only
 *   uses hyphen ('-') as a delimiter, this is more lenient).</dd>
 *
 *   <dd>Example: "polyton" (Polytonic Greek), "POSIX"</dd>
 *
 *   <dt><a id="def_extensions"><b>extensions</b></a></dt>
 *
 *   <dd>A map from single character keys to string values, indicating
 *   extensions apart from language identification.  The extensions in
 *   {@code Locale} implement the semantics and syntax of BCP 47
 *   extension subtags and private use subtags. The extensions are
 *   case insensitive, but {@code Locale} canonicalizes all
 *   extension keys and values to lower case. Note that extensions
 *   cannot have empty values.</dd>
 *
 *   <dd>Well-formed keys are single characters from the set
 *   {@code [0-9a-zA-Z]}.  Well-formed values have the form
 *   {@code SUBTAG ('-' SUBTAG)*} where for the key 'x'
 *   <code>SUBTAG = [0-9a-zA-Z]{1,8}</code> and for other keys
 *   <code>SUBTAG = [0-9a-zA-Z]{2,8}</code> (that is, 'x' allows
 *   single-character subtags).</dd>
 *
 *   <dd>Example: key="u"/value="ca-japanese" (Japanese Calendar),
 *   key="x"/value="java-1-7"</dd>
 * </dl>
 *
 * <b>Note:</b> Although BCP 47 requires field values to be registered
 * in the IANA Language Subtag Registry, the {@code Locale} class
 * does not provide any validation features.  The {@code Builder}
 * only checks if an individual field satisfies the syntactic
 * requirement (is well-formed), but does not validate the value
 * itself.  See {@link Builder} for details.
 *
 * <h2><a id="def_locale_extension">Unicode locale/language extension</a></h2>
 *
 * <p>UTS#35, "Unicode Locale Data Markup Language" defines optional
 * attributes and keywords to override or refine the default behavior
 * associated with a locale.  A keyword is represented by a pair of
 * key and type.  For example, "nu-thai" indicates that Thai local
 * digits (value:"thai") should be used for formatting numbers
 * (key:"nu").
 *
 * <p>The keywords are mapped to a BCP 47 extension value using the
 * extension key 'u' ({@link #UNICODE_LOCALE_EXTENSION}).  The above
 * example, "nu-thai", becomes the extension "u-nu-thai".
 *
 * <p>Thus, when a {@code Locale} object contains Unicode locale
 * attributes and keywords,
 * {@code getExtension(UNICODE_LOCALE_EXTENSION)} will return a
 * String representing this information, for example, "nu-thai".  The
 * {@code Locale} class also provides {@link
 * #getUnicodeLocaleAttributes}, {@link #getUnicodeLocaleKeys}, and
 * {@link #getUnicodeLocaleType} which allow you to access Unicode
 * locale attributes and key/type pairs directly.  When represented as
 * a string, the Unicode Locale Extension lists attributes
 * alphabetically, followed by key/type sequences with keys listed
 * alphabetically (the order of subtags comprising a key's type is
 * fixed when the type is defined)
 *
 * <p>A well-formed locale key has the form
 * <code>[0-9a-zA-Z]{2}</code>.  A well-formed locale type has the
 * form <code>"" | [0-9a-zA-Z]{3,8} ('-' [0-9a-zA-Z]{3,8})*</code> (it
 * can be empty, or a series of subtags 3-8 alphanums in length).  A
 * well-formed locale attribute has the form
 * <code>[0-9a-zA-Z]{3,8}</code> (it is a single subtag with the same
 * form as a locale type subtag).
 *
 * <p>The Unicode locale extension specifies optional behavior in
 * locale-sensitive services.  Although the LDML specification defines
 * various keys and values, actual locale-sensitive service
 * implementations in a Java Runtime Environment might not support any
 * particular Unicode locale attributes or key/type pairs.
 *
 * <h3>Creating a Locale</h3>
 *
 * <p>There are several different ways to create a {@code Locale}
 * object.
 *
 * <h4>Builder</h4>
 *
 * <p>Using {@link Builder} you can construct a {@code Locale} object
 * that conforms to BCP 47 syntax.
 *
 * <h4>Constructors</h4>
 *
 * <p>The {@code Locale} class provides three constructors:
 * <blockquote>
 * <pre>
 *     {@link #Locale(String language)}
 *     {@link #Locale(String language, String country)}
 *     {@link #Locale(String language, String country, String variant)}
 * </pre>
 * </blockquote>
 * These constructors allow you to create a {@code Locale} object
 * with language, country and variant, but you cannot specify
 * script or extensions.
 *
 * <h4>Factory Methods</h4>
 *
 * <p>The method {@link #forLanguageTag} creates a {@code Locale}
 * object for a well-formed BCP 47 language tag.
 *
 * <h4>Locale Constants</h4>
 *
 * <p>The {@code Locale} class provides a number of convenient constants
 * that you can use to create {@code Locale} objects for commonly used
 * locales. For example, the following creates a {@code Locale} object
 * for the United States:
 * <blockquote>
 * <pre>
 *     Locale.US
 * </pre>
 * </blockquote>
 *
 * <h3><a id="LocaleMatching">Locale Matching</a></h3>
 *
 * <p>If an application or a system is internationalized and provides localized
 * resources for multiple locales, it sometimes needs to find one or more
 * locales (or language tags) which meet each user's specific preferences. Note
 * that a term "language tag" is used interchangeably with "locale" in this
 * locale matching documentation.
 *
 * <p>In order to do matching a user's preferred locales to a set of language
 * tags, <a href="http://tools.ietf.org/html/rfc4647">RFC 4647 Matching of
 * Language Tags</a> defines two mechanisms: filtering and lookup.
 * <em>Filtering</em> is used to get all matching locales, whereas
 * <em>lookup</em> is to choose the best matching locale.
 * Matching is done case-insensitively. These matching mechanisms are described
 * in the following sections.
 *
 * <p>A user's preference is called a <em>Language Priority List</em> and is
 * expressed as a list of language ranges. There are syntactically two types of
 * language ranges: basic and extended. See
 * {@link LanguageRange Locale.LanguageRange} for details.
 *
 * <h4>Filtering</h4>
 *
 * <p>The filtering operation returns all matching language tags. It is defined
 * in RFC 4647 as follows:
 * "In filtering, each language range represents the least specific language
 * tag (that is, the language tag with fewest number of subtags) that is an
 * acceptable match. All of the language tags in the matching set of tags will
 * have an equal or greater number of subtags than the language range. Every
 * non-wildcard subtag in the language range will appear in every one of the
 * matching language tags."
 *
 * <p>There are two types of filtering: filtering for basic language ranges
 * (called "basic filtering") and filtering for extended language ranges
 * (called "extended filtering"). They may return different results by what
 * kind of language ranges are included in the given Language Priority List.
 * {@link FilteringMode} is a parameter to specify how filtering should
 * be done.
 *
 * <h4>Lookup</h4>
 *
 * <p>The lookup operation returns the best matching language tags. It is
 * defined in RFC 4647 as follows:
 * "By contrast with filtering, each language range represents the most
 * specific tag that is an acceptable match.  The first matching tag found,
 * according to the user's priority, is considered the closest match and is the
 * item returned."
 *
 * <p>For example, if a Language Priority List consists of two language ranges,
 * {@code "zh-Hant-TW"} and {@code "en-US"}, in prioritized order, lookup
 * method progressively searches the language tags below in order to find the
 * best matching language tag.
 * <blockquote>
 * <pre>
 *    1. zh-Hant-TW
 *    2. zh-Hant
 *    3. zh
 *    4. en-US
 *    5. en
 * </pre>
 * </blockquote>
 * If there is a language tag which matches completely to a language range
 * above, the language tag is returned.
 *
 * <p>{@code "*"} is the special language range, and it is ignored in lookup.
 *
 * <p>If multiple language tags match as a result of the subtag {@code '*'}
 * included in a language range, the first matching language tag returned by
 * an {@link Iterator} over a {@link Collection} of language tags is treated as
 * the best matching one.
 *
 * <h3>Use of Locale</h3>
 *
 * <p>Once you've created a {@code Locale} you can query it for information
 * about itself. Use {@code getCountry} to get the country (or region)
 * code and {@code getLanguage} to get the language code.
 * You can use {@code getDisplayCountry} to get the
 * name of the country suitable for displaying to the user. Similarly,
 * you can use {@code getDisplayLanguage} to get the name of
 * the language suitable for displaying to the user. Interestingly,
 * the {@code getDisplayXXX} methods are themselves locale-sensitive
 * and have two versions: one that uses the default
 * {@link Category#DISPLAY DISPLAY} locale and one
 * that uses the locale specified as an argument.
 *
 * <p>The Java Platform provides a number of classes that perform locale-sensitive
 * operations. For example, the {@code NumberFormat} class formats
 * numbers, currency, and percentages in a locale-sensitive manner. Classes
 * such as {@code NumberFormat} have several convenience methods
 * for creating a default object of that type. For example, the
 * {@code NumberFormat} class provides these three convenience methods
 * for creating a default {@code NumberFormat} object:
 * <blockquote>
 * <pre>
 *     NumberFormat.getInstance()
 *     NumberFormat.getCurrencyInstance()
 *     NumberFormat.getPercentInstance()
 * </pre>
 * </blockquote>
 * Each of these methods has two variants; one with an explicit locale
 * and one without; the latter uses the default
 * {@link Category#FORMAT FORMAT} locale:
 * <blockquote>
 * <pre>
 *     NumberFormat.getInstance(myLocale)
 *     NumberFormat.getCurrencyInstance(myLocale)
 *     NumberFormat.getPercentInstance(myLocale)
 * </pre>
 * </blockquote>
 * A {@code Locale} is the mechanism for identifying the kind of object
 * ({@code NumberFormat}) that you would like to get. The locale is
 * <STRONG>just</STRONG> a mechanism for identifying objects,
 * <STRONG>not</STRONG> a container for the objects themselves.
 *
 * <h3>Compatibility</h3>
 *
 * <p>In order to maintain compatibility with existing usage, Locale's
 * constructors retain their behavior prior to the Java Runtime
 * Environment version 1.7.  The same is largely true for the
 * {@code toString} method. Thus Locale objects can continue to
 * be used as they were. In particular, clients who parse the output
 * of toString into language, country, and variant fields can continue
 * to do so (although this is strongly discouraged), although the
 * variant field will have additional information in it if script or
 * extensions are present.
 *
 * <p>In addition, BCP 47 imposes syntax restrictions that are not
 * imposed by Locale's constructors. This means that conversions
 * between some Locales and BCP 47 language tags cannot be made without
 * losing information. Thus {@code toLanguageTag} cannot
 * represent the state of locales whose language, country, or variant
 * do not conform to BCP 47.
 *
 * <p>Because of these issues, it is recommended that clients migrate
 * away from constructing non-conforming locales and use the
 * {@code forLanguageTag} and {@code Locale.Builder} APIs instead.
 * Clients desiring a string representation of the complete locale can
 * then always rely on {@code toLanguageTag} for this purpose.
 *
 * <h4><a id="special_cases_constructor">Special cases</a></h4>
 *
 * <p>For compatibility reasons, two
 * non-conforming locales are treated as special cases.  These are
 * <b>{@code ja_JP_JP}</b> and <b>{@code th_TH_TH}</b>. These are ill-formed
 * in BCP 47 since the variants are too short. To ease migration to BCP 47,
 * these are treated specially during construction.  These two cases (and only
 * these) cause a constructor to generate an extension, all other values behave
 * exactly as they did prior to Java 7.
 *
 * <p>Java has used {@code ja_JP_JP} to represent Japanese as used in
 * Japan together with the Japanese Imperial calendar. This is now
 * representable using a Unicode locale extension, by specifying the
 * Unicode locale key {@code ca} (for "calendar") and type
 * {@code japanese}. When the Locale constructor is called with the
 * arguments "ja", "JP", "JP", the extension "u-ca-japanese" is
 * automatically added.
 *
 * <p>Java has used {@code th_TH_TH} to represent Thai as used in
 * Thailand together with Thai digits. This is also now representable using
 * a Unicode locale extension, by specifying the Unicode locale key
 * {@code nu} (for "number") and value {@code thai}. When the Locale
 * constructor is called with the arguments "th", "TH", "TH", the
 * extension "u-nu-thai" is automatically added.
 *
 * <h4>Serialization</h4>
 *
 * <p>During serialization, writeObject writes all fields to the output
 * stream, including extensions.
 *
 * <p>During deserialization, readResolve adds extensions as described
 * in <a href="#special_cases_constructor">Special Cases</a>, only
 * for the two cases th_TH_TH and ja_JP_JP.
 *
 * <h4>Legacy language codes</h4>
 *
 * <p>Locale's constructor has always converted three language codes to
 * their earlier, obsoleted forms: {@code he} maps to {@code iw},
 * {@code yi} maps to {@code ji}, and {@code id} maps to
 * {@code in}.  This continues to be the case, in order to not break
 * backwards compatibility.
 *
 * <p>The APIs added in 1.7 map between the old and new language codes,
 * maintaining the old codes internal to Locale (so that
 * {@code getLanguage} and {@code toString} reflect the old
 * code), but using the new codes in the BCP 47 language tag APIs (so
 * that {@code toLanguageTag} reflects the new one). This
 * preserves the equivalence between Locales no matter which code or
 * API is used to construct them. Java's default resource bundle
 * lookup mechanism also implements this mapping, so that resources
 * can be named using either convention, see {@link ResourceBundle.Control}.
 *
 * <h4>Three-letter language/country(region) codes</h4>
 *
 * <p>The Locale constructors have always specified that the language
 * and the country param be two characters in length, although in
 * practice they have accepted any length.  The specification has now
 * been relaxed to allow language codes of two to eight characters and
 * country (region) codes of two to three characters, and in
 * particular, three-letter language codes and three-digit region
 * codes as specified in the IANA Language Subtag Registry.  For
 * compatibility, the implementation still does not impose a length
 * constraint.
 *
 * @see Builder
 * @see ResourceBundle
 * @see java.text.Format
 * @see java.text.NumberFormat
 * @see java.text.Collator
 * @author Mark Davis
 * @since 1.1
 */
public final class Locale {

    /** Useful constant for language.
     */
    public static final Locale ENGLISH;

    /** Useful constant for language.
     */
    public static final Locale FRENCH;

    /** Useful constant for language.
     */
    public static final Locale GERMAN;

    /** Useful constant for language.
     */
    public static final Locale ITALIAN;

    /** Useful constant for language.
     */
    public static final Locale JAPANESE;

    /** Useful constant for language.
     */
    public static final Locale KOREAN;

    /** Useful constant for language.
     */
    public static final Locale CHINESE;

    /** Useful constant for language.
     */
    public static final Locale SIMPLIFIED_CHINESE;

    /** Useful constant for language.
     */
    public static final Locale TRADITIONAL_CHINESE;

    /** Useful constant for country.
     */
    public static final Locale FRANCE;

    /** Useful constant for country.
     */
    public static final Locale GERMANY;

    /** Useful constant for country.
     */
    public static final Locale ITALY;

    /** Useful constant for country.
     */
    public static final Locale JAPAN;

    /** Useful constant for country.
     */
    public static final Locale KOREA;

    /** Useful constant for country.
     */
    public static final Locale UK;

    /** Useful constant for country.
     */
    public static final Locale US;

    /** Useful constant for country.
     */
    public static final Locale CANADA;

    /** Useful constant for country.
     */
    public static final Locale CANADA_FRENCH;

    /**
     * Useful constant for the root locale.  The root locale is the locale whose
     * language, country, and variant are empty ("") strings.  This is regarded
     * as the base locale of all locales, and is used as the language/country
     * neutral locale for the locale sensitive operations.
     *
     * @since 1.6
     */
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

    /** Useful constant for country.
     */
    public static final Locale CHINA = SIMPLIFIED_CHINESE;

    /** Useful constant for country.
     */
    public static final Locale PRC = SIMPLIFIED_CHINESE;

    /** Useful constant for country.
     */
    public static final Locale TAIWAN = TRADITIONAL_CHINESE;

    private transient BaseLocale baseLocale;
    private transient Object localeExtensions;

    private Locale(BaseLocale baseLocale, Object extensions) {
        this.baseLocale = baseLocale;
        this.localeExtensions = extensions;
    }

    /**
     * This method must be called only for creating the Locale.*
     * constants due to making shortcuts.
     */
    private static Locale createConstant(byte baseType) {
        BaseLocale base = BaseLocale.constantBaseLocales[baseType];
        Locale locale = new Locale(base, null);
        CONSTANT_LOCALES.put(base, locale);
        return locale;
    }
}
