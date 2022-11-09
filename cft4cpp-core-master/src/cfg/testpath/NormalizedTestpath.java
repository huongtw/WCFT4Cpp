package cfg.testpath;

import cfg.object.ConditionCfgNode;
import cfg.object.ICfgNode;

public class NormalizedTestpath extends AbstractTestpath implements INormalizedTestpath {
	/**
	 *
	 */
	private static final long serialVersionUID = -7984311819565059228L;

	@Override
	public String getFullPath() {
		String output = "";
		for (int i = 0; i < size() - 1; i++) {
			ICfgNode n = get(i);
			if (n instanceof ConditionCfgNode)
				output += "(" + n.getContent() + ") " + ITestpathInCFG.SEPARATE_BETWEEN_NODES + " ";
			else
				output += n.getContent() + ITestpathInCFG.SEPARATE_BETWEEN_NODES + " ";
		}

		output += get(size() - 1).getContent();
		return output;
	}

	@Override
	public NormalizedTestpath cast() {
		return this;
	}
}
