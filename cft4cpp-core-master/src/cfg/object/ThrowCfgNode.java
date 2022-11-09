package cfg.object;

import org.eclipse.cdt.core.dom.ast.IASTNode;

public class ThrowCfgNode extends NormalCfgNode {

	public ThrowCfgNode(IASTNode node) {
		super(node);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		ThrowCfgNode cloneNode = (ThrowCfgNode) super.clone();
		return cloneNode;
	}
}
