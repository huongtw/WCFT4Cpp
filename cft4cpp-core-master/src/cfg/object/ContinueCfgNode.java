package cfg.object;

import org.eclipse.cdt.core.dom.ast.IASTNode;

public class ContinueCfgNode extends NormalCfgNode {

	public ContinueCfgNode(IASTNode node) {
		super(node);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		ContinueCfgNode cloneNode = (ContinueCfgNode) super.clone();
		return cloneNode;
	}
}
