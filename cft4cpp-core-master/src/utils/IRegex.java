package utils;

/**
 * Contain regexes that make your regex clearly
 *
 * @author ducanh
 */
public interface IRegex {
    public static final String DENOMINATOR_SEPARATOR = "\\/";

    /**
     * Ex: 1.23 <br/>
     * Ex: -1.234 <br/>
     * Ex: +1.234
     */
    public static final String NUMBER_REGEX = "[\\+\\-\\d][.\\d]+";

    public static final String INTEGER_NUMBER_REGEX = "[\\+\\-\\d][\\d]*";

    /**
     * Ex: 123 <br/>
     * Ex: +1234
     */
    public static final String POSITIVE_INTEGER_REGEX = "[\\+]*[\\d]+";

    public static final String OPENING_PARETHENESS = "\\(";
    public static final String CLOSING_PARETHENESS = "\\)";

    /**
     * Ex1: (1+1) <br/>
     * Ex2: (1+1-1) <br/>
     * Ex3: (1/1) <br/>
     * Ex4:(1*1)
     */
    public static final String EXPRESSION_IN_PARETHENESS = IRegex.OPENING_PARETHENESS + "([0-9\\.\\+\\-\\*/]+)"
            + IRegex.CLOSING_PARETHENESS;

    public static final String OPENING_BRACKET = "\\[";
    public static final String CLOSING_BRACKET = "\\]";
    public static final String EXPRESSION_IN_BRACKET = IRegex.OPENING_BRACKET + "([\\(\\)\\s0-9\\.\\+\\-\\*/]+)"
            + IRegex.CLOSING_BRACKET;

    /**
     * Ex1: abc <br/>
     * Ex2: a1b2c3 <br/>
     * Ex3: a1_b2_c3
     */
    public static final String NAME_REGEX = "[a-zA-Z0-9_]+";

    public static final String SPACES = "\\s*";

    public static final String POINTER = "\\*";
    /**
     * Ex1: [1+1] <br/>
     * Ex2: [1+1-1] <br/>
     * Ex3: [1/1] <br/>
     * Ex4:[1*1]
     */
    public static final String ARRAY_INDEX = IRegex.OPENING_BRACKET + IRegex.SPACES + "([^\\]\\[]*)" + IRegex.SPACES
            + IRegex.CLOSING_BRACKET;

    public static final String ARRAY_ITEM = IRegex.NAME_REGEX + IRegex.ARRAY_INDEX;

    public static final String DOT_DELIMITER = "\\.";

    /**
     * Ex: 1.2
     */
    public static final String FLOAT_REGEX = IRegex.NUMBER_REGEX + IRegex.DOT_DELIMITER + IRegex.NUMBER_REGEX;

}
