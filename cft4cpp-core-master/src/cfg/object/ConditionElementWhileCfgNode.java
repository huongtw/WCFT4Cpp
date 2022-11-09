package cfg.object;

import org.eclipse.cdt.core.dom.ast.IASTNode;

public class ConditionElementWhileCfgNode extends ConditionWhileCfgNode {

	public ConditionElementWhileCfgNode(IASTNode node) {
		super(node);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		ConditionElementWhileCfgNode cloneNode = (ConditionElementWhileCfgNode) super.clone();
		return cloneNode;
	}

}
