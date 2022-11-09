package testdatagen.loop;

import java.io.File;

import org.apache.log4j.Logger;

import cfg.CFGGenerationforBranchvsStatementCoverage;
import cfg.ICFG;
import cfg.object.AbstractConditionLoopCfgNode;
import cfg.object.ConditionCfgNode;
import cfg.object.EndFlagCfgNode;
import cfg.object.ICfgNode;
import cfg.testpath.IStaticSolutionGeneration;
import cfg.testpath.PartialTestpath;
import cfg.testpath.PartialTestpaths;
import config.Paths;
import parser.projectparser.ProjectParser;
import tree.object.IFunctionNode;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

/**
 * Generate test paths to test a loop in the program.
 * <p>
 * <b>Idea</b>: Generate a set of test paths that each test path ends after
 * traversing through the testing loop a specified times. It means that, each
 * test path in this set is a partial test path
 * </p>
 * <p>
 * <p>
 * For example,
 * <p>
 * <p>
 * 
 * <pre>
 * void Merge1(int t1[3], int t2[3], int t3[6]) {
 * &nbsp&nbsp while (i < 3 && j < 3) {}
 * &nbsp&nbsp while (i < 3) {}
 * &nbsp&nbsp while (j < 3) {}
 * }
 * </pre>
 * <p>
 * If we want to test the second condition named "(i < 3)". All generated test
 * path do reveal information about the final loop named "j<3".
 * <p>
 * Ex (the testing loop is executed two times): <i>(i < 3 && j < 3) -> (i<3)->
 * (i<3)-> (i<3)->!(i<3)</i>
 * <p>
 * Created by ducanhnguyen on 7/05/2017.
 */
public class PossibleTestpathGenerationForLoop extends AbstractPossibleTestpathGenerationForLoop {
	final static Logger logger = Logger.getLogger(PossibleTestpathGenerationForLoop.class);

	public PossibleTestpathGenerationForLoop(ICFG cfg, AbstractConditionLoopCfgNode loopCondition) {
		this.cfg = cfg;
		this.cfg.resetVisitedStateOfNodes();
		this.loopCondition = loopCondition;
	}

	public static void main(String[] args) throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.SYMBOLIC_EXECUTION_TEST));

		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "Merge1(int[3],int[3],int[6])").get(0);
		logger.debug(function.getAST().getRawSignature());

		// Generate cfg
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(function);
		ICFG cfg = cfgGen.generateCFG();
		cfg.setFunctionNode(function);
		cfg.setIdforAllNodes();
		cfg.resetVisitedStateOfNodes();

		// Generate test path for loop
		AbstractConditionLoopCfgNode loopCondition = (AbstractConditionLoopCfgNode) cfg
				.findFirstCfgNodeByContent("i < 3");
		PossibleTestpathGenerationForLoop tpGen = new PossibleTestpathGenerationForLoop(cfg, loopCondition);
		tpGen.setMaximumIterationsForOtherLoops(4);
		tpGen.setIterationForUnboundedTestingLoop(10);
		tpGen.setAddTheEndTestingCondition(true);
		tpGen.generateTestpaths();

		logger.debug("num test path = " + tpGen.getPossibleTestpaths().size());
	}

	@Override
	protected void traverseCFG(ICfgNode stm, PartialTestpath tp, PartialTestpaths testpaths,
			boolean isJustOverTheTestingLoop) throws Exception {
		if (!isJustOverTheTestingLoop && !(stm instanceof EndFlagCfgNode)) {
			tp.add(stm);
			ICfgNode trueNode = stm.getTrueNode();
			ICfgNode falseNode = stm.getFalseNode();

			if (stm instanceof ConditionCfgNode) {
				int currentIterations = tp.count(trueNode);

				if (stm instanceof AbstractConditionLoopCfgNode) {
					// If the current condition is the testing loop condition
					if (stm.equals(loopCondition)) {
						// Find the maximum iteration for the testing loop
						if (currentIterations == 0) {
							maximumIterationForTestingLoop = getMaximumIterationsInTargetCondition(stm, tp);
						}

						if (currentIterations < maximumIterationForTestingLoop) {
							traverseCFG(trueNode, tp, testpaths, false);

						} else if (currentIterations == maximumIterationForTestingLoop) {
							PartialTestpath newTp = (PartialTestpath) tp.clone();

							if (!addTheEndTestingCondition)
								newTp.remove(newTp.size() - 1);
							else {
								newTp.setFinalConditionType(false);
							}

							newTp.setDescription("Loop condition: " + loopCondition.toString() + ", its loop num = "
									+ maximumIterationForTestingLoop + ", max other loops iteration = "
									+ maximumIterationsForOtherLoops + ", INCREASE = " + delta_);

							// solve
							String solution = solveTestpath(cfg.getFunctionNode(), newTp);
							if (!solution.equals(IStaticSolutionGeneration.NO_SOLUTION)) {
								logger.debug(newTp.getFullPath());
								logger.debug(newTp.getDescription());
								logger.debug(solution);
								logger.debug("\n\n");
							} else
								logger.debug("no solution");
							testpaths.add(newTp);
						}

					} else if (currentIterations <= maximumIterationsForOtherLoops) {
						traverseCFG(falseNode, tp, testpaths, false);
						traverseCFG(trueNode, tp, testpaths, false);
					}

				} else {
					traverseCFG(falseNode, tp, testpaths, false);
					traverseCFG(trueNode, tp, testpaths, false);
				}
			} else
				traverseCFG(trueNode, tp, testpaths, false);
			tp.remove(tp.size() - 1);
		}
	}

}
