package cfg.object;

public interface IConditionLoopCfgNode extends ICfgNode {
    final int CANNOT_DETECT_BOUND = -1653524;

    /**
     * Get the upper bound of the condition bound is specified. Ex:
     * "(i<100)"------------->"100"
     *
     * @return a specified value if the upper bound is existed. Otherwise, it
     * returns -1
     */
    double getUpperBound();

    /**
     * Get the lower bound of the condition bound is specified. Ex:
     * "(i>1)"------------->"1"
     *
     * @return a specified value if the lower bound is existed. Otherwise, it
     * returns -1
     */
    double getLowerBound();

    boolean isBoundCondition();

    /**
     * Get name of iteration variable. Ex: for (int i = 0 ;i <10;i++). The
     * function return "i";
     *
     * @return null if we cannot detect the iteration variable (Ex: in UCLN()
     * function: while (a>b){...})
     */
    String getIterationVariable();

}
