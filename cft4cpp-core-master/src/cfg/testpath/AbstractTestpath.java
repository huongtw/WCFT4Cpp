package cfg.testpath;

import java.util.ArrayList;
import java.util.List;

import cfg.object.AbstractConditionLoopCfgNode;
import cfg.object.ConditionCfgNode;
import cfg.object.ICfgNode;
import tree.object.IFunctionNode;

/**
 * Represent a test path of CFG
 *
 * @author ducanhnguyen
 */
public abstract class AbstractTestpath extends ArrayList<ICfgNode> implements ITestpathInCFG {
	/**
	 *
	 */
	private static final long serialVersionUID = -8189308138881545555L;
	private IFunctionNode functionNode;
	private String description = "";
	private IStaticSolutionGeneration staticSolutionGen;
	private boolean add;

	@Override
	public int count(ICfgNode stm) {
		int count = 0;
		for (ICfgNode item : this)
			if (item.equals(stm))
				count++;
		return count;
	}

	@Override
	public String toReducedString() {
		String output = "";
		for (int i = 0; i < size() - 1; i++)
			output += get(i).toString() + " -> ";
		output += get(size() - 1);
		return output;
	}

	@Override
	public boolean nextIsTrueBranch(ICfgNode currentNode, int indexofCurrentNode) {
		if (currentNode instanceof ConditionCfgNode) {
			if (indexofCurrentNode == size() - 1 || indexofCurrentNode == -1
					|| !(currentNode instanceof ConditionCfgNode))
				return false;
			else {
				int next = indexofCurrentNode + 1;
				if (currentNode.getTrueNode().getId() == get(next).getId())
					return true;
				else
					return false;
			}
		} else
			return true;
	}

	@Override
	public List<ICfgNode> getAllCfgNodes() {
		return this;
	}

	@Override
	public IStaticSolutionGeneration generateTestdata() {
		if (staticSolutionGen == null) {
			IStaticSolutionGeneration staticSolutionGeneration = new StaticSolutionGeneration();
			staticSolutionGeneration.setFunctionNode(getFunctionNode());
			staticSolutionGeneration.setTestpath(this);
			try {
				staticSolutionGeneration.generateStaticSolution();
				staticSolutionGen = staticSolutionGeneration;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return staticSolutionGen;
	}

	@Override
	public IFunctionNode getFunctionNode() {
		return functionNode;
	}

	@Override
	public void setFunctionNode(IFunctionNode functionNode) {
		this.functionNode = functionNode;
	}

	@Override
	public int getRealSize() {
		int size = 0;
		for (ICfgNode cfgNode : this)
			if (cfgNode.isNormalNode())
				size++;
		return size;
	}

	@Override
	public int getNumConditionsIncludingNegativeConditon() {
		int numCondition = 0;
		for (ICfgNode cfgNode : this)
			if (cfgNode instanceof ConditionCfgNode)
				numCondition++;
		return numCondition;
	}

	@Override
	public int getNumLoopConditions() {
		List<ICfgNode> loopConditions = new ArrayList<>();
		for (ICfgNode cfgNode : this)
			if (cfgNode instanceof AbstractConditionLoopCfgNode && !loopConditions.contains(cfgNode))
				loopConditions.add(cfgNode);
		return loopConditions.size();
	}

	@Override
	public ICfgNode getConditionAt(int idCondition) {
		ICfgNode conditionNode = null;

		int numCondition = 0;
		for (ICfgNode cfgNode : this)
			if (cfgNode instanceof ConditionCfgNode) {
				numCondition++;
				if (numCondition == idCondition) {
					conditionNode = cfgNode;
					break;
				}
			}
		return conditionNode;
	}

	@Override
	public boolean contains(Object o) {
		AbstractTestpath tp2 = (AbstractTestpath) o;
		return getFullPath().startsWith(tp2.getFullPath());
	}

	@Override
	public boolean equals(Object o) {
		AbstractTestpath tp2 = (AbstractTestpath) o;
		if (size() == tp2.size())
			return getFullPath().equals(tp2.getFullPath());
		else
			return false;
	}

	@Override
	public AbstractTestpath cast() {
		return this;
	}

	@Override
	public String getFullPath() {
		return getFullPath();
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}
}
