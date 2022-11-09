package cfg.object;

import org.eclipse.cdt.core.dom.ast.IASTNode;

public class ConditionElementIfCfgNode extends ConditionIfCfgNode {

	public ConditionElementIfCfgNode(IASTNode node) {
		super(node);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		ConditionElementIfCfgNode cloneNode = (ConditionElementIfCfgNode) super.clone();
		return cloneNode;
	}
}
