package testdatagen.se.normalstatementparser;

/**
 * Parse assignment statement. <br/>
 * Ex1: a = new int[2]<br/>
 * Ex2: p1 = p2 <br/>
 * Ex3: x = y + 1
 *
 * @author ducanhnguyen
 */
public abstract class AssignmentParser extends StatementParser {

    public static final String ADDRESS_OPERATOR = "&";
    public static final String ONE_LEVEL_OPERATOR = "*";
    /**
     * Ex: int* a = new int[2]
     */
    public static final int NEW = 0;
    public static final int MALLOC = 1;
    public static final int CALLOC = 2;
    public static final int NORMAL_ASSIGNMENT = 3;
    /**
     * Ex: t = z = y
     */
    public static final int MULTIPLE_ASSIGNMENTS = 4;
    public static final int UNSPECIFIED_ASSIGNMENT = 5;

    public AssignmentParser() {
    }
}
