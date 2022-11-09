package cfg.testpath;

import java.util.List;

import cfg.object.BeginFlagCfgNode;
import cfg.object.EndFlagCfgNode;
import cfg.object.ICfgNode;
import cfg.object.ScopeCfgNode;
import tree.object.IFunctionNode;

public interface ITestpathInCFG {

	String SEPARATE_BETWEEN_NODES = "=>";

	String[] SPECIAL_SATEMENTS = { BeginFlagCfgNode.BEGIN_FLAG, EndFlagCfgNode.END_FLAG, ScopeCfgNode.SCOPE_CLOSE,
			ScopeCfgNode.SCOPE_OPEN };

	/**
	 * Count the number of occurrences of a node in test path
	 *
	 * @param stm
	 * @return
	 */
	int count(ICfgNode stm);

	String toReducedString();

	@Override
	String toString();

	/**
	 * Return true if the next node is belonged to the true branch
	 *
	 * @param currentNode
	 * @param indexofCurrentNode
	 * @return
	 */
	boolean nextIsTrueBranch(ICfgNode currentNode, int indexofCurrentNode);

	List<ICfgNode> getAllCfgNodes();

	/**
	 * Generate test data using symbolic execution
	 *
	 * @return
	 */
	IStaticSolutionGeneration generateTestdata();

	/**
	 * Get the function node containing this test path
	 *
	 * @return
	 */
	IFunctionNode getFunctionNode();

	/**
	 * @param functionNode
	 *            the function contains the current test path
	 */
	void setFunctionNode(IFunctionNode functionNode);

	/**
	 * Get number of statements in the test path, not including "{","}", begin node,
	 * end node
	 *
	 * @return
	 */
	int getRealSize();

	/**
	 * Get number of conditions
	 * <p>
	 * Ex: Testpath: (a>b) => { => ... => } => (a>b)
	 * <p>
	 * where (a>b) is condition of loop. The test path includes the start node of
	 * loop, end the end node of the loop.
	 * <p>
	 * Therefore, the return value is 2
	 *
	 * @return
	 */
	int getNumConditionsIncludingNegativeConditon();

	/**
	 * Get number of loop conditions
	 * <p>
	 * Ex: Testpath: (a>b) => { => ... => } => (a>b)
	 * <p>
	 * where (a>b) is condition of loop. The test path includes the start node of
	 * loop, end the end node of the loop.
	 * <p>
	 * We only consider the start of loop. Therefore, the return value is 1
	 *
	 * @return
	 */
	int getNumLoopConditions();

	/**
	 * Get condition at specified location
	 *
	 * @param idCondition
	 * @return
	 */
	ICfgNode getConditionAt(int idCondition);

	AbstractTestpath cast();

	/**
	 * Return content of test path. This content reveals the boolean of condition.
	 * <br/>
	 * For example, A->B->C, B is condition, C is executed when B is false. Full
	 * path here: A->!(B)->C
	 */
	String getFullPath();

	String getDescription();

	void setDescription(String description);
}