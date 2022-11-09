package cfg.testpath;

import cfg.object.ConditionCfgNode;
import cfg.object.ICfgNode;

/**
 * Represent partial test path
 *
 * @author ducanhnguyen
 */
public class PartialTestpath extends AbstractTestpath implements IPartialTestpath {
	/**
	 *
	 */
	private static final long serialVersionUID = 2276531353820115816L;
	private boolean finalConditionType;

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
		if (finalConditionType)
			output += get(size() - 1);
		else
			output += "!(" + get(size() - 1) + ")";
		return output;
	}

	@Override
	public boolean getFinalConditionType() {
		return finalConditionType;
	}

	@Override
	public void setFinalConditionType(boolean finalConditionType) {
		this.finalConditionType = finalConditionType;
	}

	@Override
	public PartialTestpath cast() {
		return this;
	}
}
