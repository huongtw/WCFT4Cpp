package testdatagen.loop;

import java.util.List;

import org.apache.log4j.Logger;

import cfg.ICFG;
import cfg.object.AbstractConditionLoopCfgNode;
import cfg.object.ICfgNode;
import cfg.object.IConditionLoopCfgNode;
import cfg.testpath.IStaticSolutionGeneration;
import cfg.testpath.ITestpathInCFG;
import cfg.testpath.PartialTestpath;
import cfg.testpath.PartialTestpaths;
import config.ISettingv2;
import config.Settingv2;
import testdatagen.se.ISymbolicExecution;
import testdatagen.se.Parameter;
import testdatagen.se.PathConstraint;
import testdatagen.se.SymbolicExecution;
import testdatagen.se.memory.BasicSymbolicVariable;
import testdatagen.se.memory.ISymbolicVariable;
import testdatagen.se.solver.ISmtLibGeneration;
import testdatagen.se.solver.RunZ3OnCMD;
import testdatagen.se.solver.SmtLibGeneration;
import testdatagen.se.solver.Z3SolutionParser;
import tree.object.IFunctionNode;
import tree.object.IVariableNode;
import utils.SpecialCharacter;
import utils.Utils;

/**
 * Created by ducanhnguyen on 5/8/2017.
 */
public abstract class AbstractPossibleTestpathGenerationForLoop {
    public static final String CONSTRAINTS_FILE = Settingv2.getValue(ISettingv2.SMT_LIB_FILE_PATH);
    public static final String Z3 = Settingv2.getValue(ISettingv2.SOLVER_Z3_PATH);
    final static Logger logger = Logger.getLogger(AbstractPossibleTestpathGenerationForLoop.class);
    /**
     * Represent control flow graph
     */
    protected ICFG cfg;
    protected PartialTestpaths possibleTestpaths = new PartialTestpaths();
    /**
     * The maximum iteration for the testing loop
     */
    protected int maximumIterationForTestingLoop = -1;
    /**
     * Maximum iterations for all loops not being tested
     */
    protected int maximumIterationsForOtherLoops = -1;
    /**
     * The number of iteration in the unbound loop
     */
    protected int iterationForUnboundedTestingLoop = -1;
    protected boolean addTheEndTestingCondition = false;
    protected boolean isUnboundedTestingLoop = true;
    protected int delta_ = 0;
    protected int realMaximumIterationForTestingLoop;
    AbstractConditionLoopCfgNode loopCondition = null;

    protected abstract void traverseCFG(ICfgNode stm, PartialTestpath tp, PartialTestpaths testpaths,
                                        boolean isJustOverTheTestingLoop) throws Exception;

    public void generateTestpaths() throws Exception {
        if (loopCondition != null && maximumIterationsForOtherLoops != -1 && iterationForUnboundedTestingLoop != -1) {
            ICfgNode beginNode = cfg.getBeginNode();
            PartialTestpath initialTestpath = new PartialTestpath();
            initialTestpath.setFunctionNode(cfg.getFunctionNode());
            try {
                traverseCFG(beginNode, initialTestpath, possibleTestpaths, false);
                realMaximumIterationForTestingLoop = maximumIterationForTestingLoop;

                if (!isUnboundedTestingLoop) {
                    // maxLoop-1
                    logger.debug("test (maxLoop-1) = " + (realMaximumIterationForTestingLoop - 1) + " times");
                    cfg.resetVisitedStateOfNodes();
                    delta_ = -1;
                    traverseCFG(beginNode, initialTestpath, possibleTestpaths, false);

                    // maxLoop+1
                    logger.debug("test (maxLoop+1) = " + (realMaximumIterationForTestingLoop + 1) + " times");
                    cfg.resetVisitedStateOfNodes();
                    delta_ = +1;
                    traverseCFG(beginNode, initialTestpath, possibleTestpaths, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            throw new Exception("Dont configure full");
    }

    public int getMaximumIterationsInTargetCondition(ICfgNode stm, PartialTestpath tp) throws Exception {
        int iteration = iterationForUnboundedTestingLoop;
        isUnboundedTestingLoop = true;

        if (stm instanceof AbstractConditionLoopCfgNode) {
            if (stm.equals(loopCondition)) {
                AbstractConditionLoopCfgNode stmCast = (AbstractConditionLoopCfgNode) stm;

                String iterationVariable = stmCast.getIterationVariable();
                if (iterationVariable != null) {
                    // Create table of variables to the current condition
                    Parameter paramaters = new Parameter();
                    for (IVariableNode n : cfg.getFunctionNode().getArguments())
                        paramaters.add(n);
                    for (IVariableNode n : cfg.getFunctionNode().getReducedExternalVariables())
                        paramaters.add(n);

                    ISymbolicExecution se = new SymbolicExecution(tp, paramaters, cfg.getFunctionNode());
                    List<ISymbolicVariable> vars = se.getTableMapping().getVariables();

                    for (ISymbolicVariable var : vars)
                        if (var.getName().equals(iterationVariable) && var instanceof BasicSymbolicVariable) {

                            double initialIteration = Utils.toDouble(var.getAllPhysicalCells().get(0).getValue());
                            if (initialIteration != Utils.UNDEFINED_TO_DOUBLE) {
                                // Compute delta
                                double bound = -1;
                                if (stmCast.getLowerBound() != IConditionLoopCfgNode.CANNOT_DETECT_BOUND) {
                                    bound = stmCast.getLowerBound();
                                } else if (stmCast.getUpperBound() != IConditionLoopCfgNode.CANNOT_DETECT_BOUND) {
                                    bound = stmCast.getUpperBound();
                                }

                                double delta = -1;
                                if (bound >= 0 && initialIteration >= 0)
                                    delta = Math.abs(bound - initialIteration) + 1;
                                else if (bound >= 0 && initialIteration <= 0)
                                    delta = bound + Math.abs(initialIteration) + 1;
                                else if (bound <= 0 && initialIteration >= 0)
                                    delta = Math.abs(bound) + initialIteration + 1;
                                else if (bound <= 0 && initialIteration <= 0)
                                    delta = Math.abs(Math.abs(bound) - Math.abs(initialIteration)) + 1;

                                iteration = Utils.toInt(Math.round(delta) + "") + delta_;
                                isUnboundedTestingLoop = false;
                            } else
                                iteration = iterationForUnboundedTestingLoop;
                            break;
                        }
                } else {
                    iteration = iterationForUnboundedTestingLoop;
                }
            }
        }
        return iteration;
    }

    protected String solveTestpath(IFunctionNode function, ITestpathInCFG testpath) throws Exception {
        /*
		 * Get the passing variables of the given function
		 */
        Parameter paramaters = new Parameter();
        for (IVariableNode n : function.getArguments())
            paramaters.add(n);
        for (IVariableNode n : function.getReducedExternalVariables())
            paramaters.add(n);

		/*
		 * Get the corresponding path constraints of the test path
		 */
        ISymbolicExecution se = new SymbolicExecution(testpath, paramaters, function);
        if (se.getConstraints().getNormalConstraints().size() + se.getConstraints().getNullorNotNullConstraints().size() > 0) {
			/*
			 * Solve the path constraints
			 */
            ISmtLibGeneration smtLibGen = new SmtLibGeneration(function.getPassingVariables(),
                    se.getConstraints().getNormalConstraints());
            smtLibGen.generate();

            Utils.writeContentToFile(smtLibGen.getSmtLibContent(), CONSTRAINTS_FILE);

            RunZ3OnCMD z3 = new RunZ3OnCMD(Z3, CONSTRAINTS_FILE);
            z3.execute();
            String staticSolution = new Z3SolutionParser().getSolution(z3.getSolution());

            if (staticSolution.equals(IStaticSolutionGeneration.NO_SOLUTION)) {
                return IStaticSolutionGeneration.NO_SOLUTION;
            } else {
                if (se.getConstraints().getNullorNotNullConstraints().size() > 0)
                    for (PathConstraint nullConstraint : se.getConstraints().getNullorNotNullConstraints())
                        staticSolution += nullConstraint.getConstraint() + SpecialCharacter.END_OF_STATEMENT;

                if (se.getConstraints().getNullorNotNullConstraints().size() > 0)
                    return staticSolution + "; " + se.getConstraints().getNullorNotNullConstraints();
                else
                    return staticSolution + ";";
            }
        } else
            return IStaticSolutionGeneration.NO_SOLUTION;
    }

    public PartialTestpaths getPossibleTestpaths() {
        return possibleTestpaths;
    }

    public int getMaximumIterationForTestingLoop() {
        return maximumIterationForTestingLoop;
    }

    public int getMaximumIterationsForOtherLoops() {
        return maximumIterationsForOtherLoops;
    }

    public void setMaximumIterationsForOtherLoops(int maximumIterationsForOtherLoops) {
        this.maximumIterationsForOtherLoops = maximumIterationsForOtherLoops;
    }

    public int getIterationForUnboundedTestingLoop() {
        return iterationForUnboundedTestingLoop;
    }

    public void setIterationForUnboundedTestingLoop(int iterationForUnboundedTestingLoop) {
        this.iterationForUnboundedTestingLoop = iterationForUnboundedTestingLoop;
    }

    public int getRealMaximumIterationForTestingLoop() {
        return realMaximumIterationForTestingLoop;
    }

    public void setAddTheEndTestingCondition(boolean addTheEndTestingCondition) {
        this.addTheEndTestingCondition = addTheEndTestingCondition;
    }

}
