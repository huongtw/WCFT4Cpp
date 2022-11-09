package cfg.object;

import org.eclipse.cdt.core.dom.ast.IASTNode;

public class SimpleCfgNode extends NormalCfgNode {

	public SimpleCfgNode(IASTNode node) {
		super(node);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		SimpleCfgNode cloneNode = (SimpleCfgNode) super.clone();
		return cloneNode;
	}
}
