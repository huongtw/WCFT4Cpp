package cfg.object;

import org.eclipse.cdt.core.dom.ast.IASTNode;

public abstract class ConditionCfgNode extends NormalCfgNode {
	private boolean isVisitedTrueBranch = false;

	private boolean isVisitedFalseBranch = false;

	public ConditionCfgNode(IASTNode node) {
		super(node);
	}

	public boolean isVisitedTrueBranch() {
		return isVisitedTrueBranch;
	}

	public void setVisitedTrueBranch(boolean isVisitedTrueBranch) {
		this.isVisitedTrueBranch = isVisitedTrueBranch;
	}

	public boolean isVisitedFalseBranch() {
		return isVisitedFalseBranch;
	}

	public void setVisitedFalseBranch(boolean isVisitedFalseBranch) {
		this.isVisitedFalseBranch = isVisitedFalseBranch;
	}

}
