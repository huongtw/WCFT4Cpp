package cfg.object;

import org.eclipse.cdt.core.dom.ast.IASTNode;

public class BreakCfgNode extends NormalCfgNode {

	public BreakCfgNode(IASTNode node) {
		super(node);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		BreakCfgNode cloneNode = (BreakCfgNode) super.clone();
		return cloneNode;
	}
}
