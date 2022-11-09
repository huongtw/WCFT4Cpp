package cfg.object;

import org.eclipse.cdt.core.dom.ast.IASTNode;

public class ConditionForCfgNode extends AbstractConditionLoopCfgNode {

	public ConditionForCfgNode(IASTNode node) {
		super(node);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		ConditionForCfgNode cloneNode = (ConditionForCfgNode) super.clone();
		cloneNode.setVisitedFalseBranch(isVisitedFalseBranch());
		cloneNode.setVisitedTrueBranch(isVisitedTrueBranch());
		return cloneNode;
	}
}
