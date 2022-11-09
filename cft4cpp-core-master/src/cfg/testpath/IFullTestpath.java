package cfg.testpath;

import cfg.ICFG;

public interface IFullTestpath extends ITestpathInCFG {
    /**
     * Get a partial test path from the start node to the given condition.
     *
     * @param endIdCondition in range of [0..number of condition-1]
     * @return
     */
    IPartialTestpath getPartialTestpathAt(int endIdCondition, boolean finalConditionType);

    /**
     * Get number of unvisited statements in the current cfg
     *
     * @param cfg
     * @return
     */
    int getNumUnvisitedStatements(ICFG cfg);

}
