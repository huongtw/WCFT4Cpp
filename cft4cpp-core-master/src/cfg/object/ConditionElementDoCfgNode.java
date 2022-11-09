package cfg.object;

import org.eclipse.cdt.core.dom.ast.IASTNode;

public class ConditionElementDoCfgNode extends ConditionDoCfgNode {

	public ConditionElementDoCfgNode(IASTNode node) {
		super(node);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		ConditionElementDoCfgNode cloneNode = (ConditionElementDoCfgNode) super.clone();
		return cloneNode;
	}
}
