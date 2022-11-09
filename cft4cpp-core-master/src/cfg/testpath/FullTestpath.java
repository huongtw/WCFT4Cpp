package cfg.testpath;

import java.util.ArrayList;
import java.util.List;

import cfg.ICFG;
import cfg.object.ConditionCfgNode;
import cfg.object.ICfgNode;

/**
 * Represent full test path from the beginning node to the end node
 *
 * @author ducanhnguyen
 */
public class FullTestpath extends AbstractTestpath implements IFullTestpath {

	/**
	 *
	 */
	private static final long serialVersionUID = 3205932220413141035L;

	@Override
	public IPartialTestpath getPartialTestpathAt(int endConditionId, boolean finalConditionType) {
		IPartialTestpath tp = new PartialTestpath();
		tp.setFunctionNode(getFunctionNode());

		if (endConditionId < getNumConditionsIncludingNegativeConditon()) {
			int numVisitedCondition = 0;

			for (ICfgNode node : this) {
				tp.cast().add(node);
				if (node instanceof ConditionCfgNode) {
					numVisitedCondition++;
					if (numVisitedCondition >= endConditionId + 1)
						break;
				}
			}
			tp.setFinalConditionType(finalConditionType);
			return tp;
		} else
			return tp;
	}

	@Override
	public int getNumUnvisitedStatements(ICFG cfg) {
		int numUnvisitedStatements = 0;

		List<ICfgNode> unvisitedNodes = cfg.getUnvisitedStatements();

		List<Integer> unvisitedIds = new ArrayList<>();
		for (ICfgNode unvisitedNode : unvisitedNodes)
			unvisitedIds.add(unvisitedNode.getId());

		for (ICfgNode cfgNode : getAllCfgNodes())
			if (cfgNode.isNormalNode())
				if (unvisitedIds.contains(cfgNode.getId()))
					numUnvisitedStatements++;
		return numUnvisitedStatements;
	}

	@Override
	public String getFullPath() {
		String output = "";
		for (int i = 0; i < size() - 1; i++) {
			ICfgNode n = get(i);
			if (n instanceof ConditionCfgNode)
				if (nextIsTrueBranch(n, i))
					output += "(" + n.getContent() + ") " + ITestpathInCFG.SEPARATE_BETWEEN_NODES + " ";
				else
					output += "!(" + n.getContent() + ") " + ITestpathInCFG.SEPARATE_BETWEEN_NODES + " ";
			else
				output += n.getContent() + ITestpathInCFG.SEPARATE_BETWEEN_NODES + " ";
		}
		output += get(size() - 1);
		return output;
	}

	@Override
	public FullTestpath cast() {
		return this;
	}

}
