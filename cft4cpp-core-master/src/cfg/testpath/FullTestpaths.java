package cfg.testpath;

import org.apache.log4j.Logger;

import cfg.ICFG;
import testdatagen.IVenusTestdataGenerationStrategy;

public class FullTestpaths extends Testpaths<IFullTestpath> implements IFullTestpaths {
    final static Logger logger = Logger.getLogger(FullTestpaths.class);
    /**
     *
     */
    private static final long serialVersionUID = -5462154103460415384L;
    /**
     * Only for testing
     */
    int numSymbolic = 0;

    @Override
    public FullTestpaths removeNoSolutionTestpathsAt(int conditionCheckingId, boolean conditionType) {
        FullTestpaths output = this;
        int symbolicCount = 0;
        /*
		 * 
		 */
        if (conditionCheckingId != IVenusTestdataGenerationStrategy.CONDITION_ORDER.NO_CHECK) {
            ITestpaths removedTestpaths = new Testpaths();
            ITestpaths mayHaveSolutionTestpaths = new Testpaths();

            for (IFullTestpath tp : output)
                if (tp.getNumConditionsIncludingNegativeConditon() >= conditionCheckingId + 1) {

                    IPartialTestpath partialTestpath = tp.getPartialTestpathAt(conditionCheckingId, conditionType);

                    if (!removedTestpaths.cast().contains(partialTestpath)
                            && !mayHaveSolutionTestpaths.cast().contains(partialTestpath)) {

                        IStaticSolutionGeneration staticSolutionGen = partialTestpath.generateTestdata();
                        String staticSolution = staticSolutionGen.getStaticSolution();
                        symbolicCount++;

                        if (staticSolution == IStaticSolutionGeneration.NO_SOLUTION) {
                            for (IFullTestpath item : output)
                                if (item.cast().contains(partialTestpath) && !removedTestpaths.cast().contains(item))
                                    removedTestpaths.cast().add(item);
                            removedTestpaths.cast().add(partialTestpath);
                            // System.out.println("Num of removed test paths = "
                            // + removedTestpaths.cast().size());
                        } else {
                            for (IFullTestpath item : output)
                                if (item.cast().contains(partialTestpath)
                                        && !mayHaveSolutionTestpaths.cast().contains(item))
                                    mayHaveSolutionTestpaths.cast().add(item);
                            mayHaveSolutionTestpaths.cast().add(partialTestpath);
                            // System.out.println("Num of may-have solution test
                            // paths = " +
                            // mayHaveSolutionTestpaths.cast().size());
                        }
                    }
                }

            for (Object removedTestpath : removedTestpaths.cast())
                if (removedTestpath instanceof IFullTestpath)
                    output.remove(removedTestpath);
        }

        output.setNumSymbolic(symbolicCount);
        return output;
    }

    @Override
    public FullTestpaths arrangeByNumVisitedStatementsinDecreasingOrder(ICFG cfg) {
        FullTestpaths output = this;
        for (int i = 0; i < output.size() - 1; i++)
            for (int j = 0; j < output.size(); j++)
                if (output.getTestpathAt(i).getNumUnvisitedStatements(cfg) > output.getTestpathAt(j)
                        .getNumUnvisitedStatements(cfg)) {
                    IFullTestpath tp = output.get(i);
                    output.set(i, output.get(j));
                    output.set(j, tp);
                }
        return output;
    }

    @Override
    public FullTestpaths arrangeByNumVisitedStatementsinIncreasingOrder(ICFG cfg) {
        FullTestpaths output = this;
        for (int i = 0; i < output.size() - 1; i++)
            for (int j = 0; j < output.size(); j++)
                if (output.getTestpathAt(i).getNumUnvisitedStatements(cfg) < output.getTestpathAt(j)
                        .getNumUnvisitedStatements(cfg)) {
                    IFullTestpath tp = output.get(i);
                    output.set(i, output.get(j));
                    output.set(j, tp);
                }
        return output;
    }

    @Override
    public FullTestpaths getTestpathsContainingUncoveredStatements(ICFG cfg) {
        return cfg.getTestpathsContainingUncoveredStatements(this);
    }

    @Override
    public FullTestpaths getTestpathsContainingUncoveredBranches(ICFG cfg) {
        return cfg.getTestpathsContainingUncoveredBranches(this);
    }

    @Override
    public FullTestpath getTestpathAt(int i) {
        return (FullTestpath) get(i);
    }

    @Override
    public FullTestpaths cast() {
        return this;
    }

    @Override
    public FullTestpaths removeNoSolutionTestpathsAt(int conditionCheckingId) {
        return this.removeNoSolutionTestpathsAt(conditionCheckingId, true)
                .removeNoSolutionTestpathsAt(conditionCheckingId, false);
    }

    @Override
    public int getNumSymbolic() {
        return numSymbolic;
    }

    @Override
    public void setNumSymbolic(int numSymbolic) {
        this.numSymbolic = numSymbolic;
    }
   
}
