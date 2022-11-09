package cfg.testpath;

import cfg.ICFG;

public interface IFullTestpaths extends ITestpaths {
    /**
     * Arrange the list of test paths in decreasing unvisited statement order
     *
     * @param cfg
     * @return
     */
    FullTestpaths arrangeByNumVisitedStatementsinDecreasingOrder(ICFG cfg);

    /**
     * Arrange the list of test paths in increasing unvisited statement order
     *
     * @param cfg
     * @return
     */
    FullTestpaths arrangeByNumVisitedStatementsinIncreasingOrder(ICFG cfg);

    /**
     * Remove test paths that its partial test path has no solution
     *
     * @param conditionCheckingId
     * @return
     */
    FullTestpaths removeNoSolutionTestpathsAt(int conditionCheckingId, boolean branch);

    FullTestpaths removeNoSolutionTestpathsAt(int conditionCheckingId);

    /**
     * Get a list of test paths containing unvisited statements
     *
     * @param cfg
     * @return
     */
    FullTestpaths getTestpathsContainingUncoveredStatements(ICFG cfg);

    FullTestpaths getTestpathsContainingUncoveredBranches(ICFG cfg);

    FullTestpath getTestpathAt(int i);

    @Override
    FullTestpaths cast();

    int getNumSymbolic();

    void setNumSymbolic(int numSymbolic);
}
