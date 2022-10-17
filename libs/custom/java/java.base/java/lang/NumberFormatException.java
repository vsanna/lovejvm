package java.lang;

/**
 * Thrown to indicate that the application has attempted to convert
 * a string to one of the numeric types, but that the string does not
 * have the appropriate format.
 *
 * @author  unascribed
 * @see     Integer#parseInt(String)
 * @since   1.0
 */
public class NumberFormatException extends IllegalArgumentException {
    /**
     * Constructs a {@code NumberFormatException} with no detail message.
     */
    public NumberFormatException () {
        super();
    }

    /**
     * Constructs a {@code NumberFormatException} with the
     * specified detail message.
     *
     * @param   s   the detail message.
     */
    public NumberFormatException (String s) {
        super (s);
    }

    /**
     * Factory method for making a {@code NumberFormatException}
     * given the specified input which caused the error.
     *
     * @param   s   the input causing the error
     * @param   beginIndex   the beginning index, inclusive.
     * @param   endIndex     the ending index, exclusive.
     * @param   errorIndex   the index of the first error in s
     */
//    static NumberFormatException forCharSequence(CharSequence s,
//            int beginIndex, int endIndex, int errorIndex) {
//        return new NumberFormatException("Error at index "
//                + (errorIndex - beginIndex) + " in: \""
//                + s.subSequence(beginIndex, endIndex) + "\"");
//    }
}
