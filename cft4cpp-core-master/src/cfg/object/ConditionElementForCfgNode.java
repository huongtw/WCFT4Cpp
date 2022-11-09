package cfg.object;

import org.eclipse.cdt.core.dom.ast.IASTNode;

public class ConditionElementForCfgNode extends ConditionForCfgNode {

	public ConditionElementForCfgNode(IASTNode node) {
		super(node);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		ConditionElementForCfgNode cloneNode = (ConditionElementForCfgNode) super.clone();
		return cloneNode;
	}
}
