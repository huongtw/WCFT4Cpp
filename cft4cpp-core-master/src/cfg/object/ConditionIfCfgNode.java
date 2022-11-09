package cfg.object;

import org.eclipse.cdt.core.dom.ast.IASTNode;

/**
 * Example: if (a>b) {...} <br/>
 * 
 * @author Duc Anh Nguyen
 *
 */
public class ConditionIfCfgNode extends ConditionCfgNode {

	public ConditionIfCfgNode(IASTNode node) {
		super(node);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		ConditionIfCfgNode cloneNode = (ConditionIfCfgNode) super.clone();
		cloneNode.setVisitedFalseBranch(isVisitedFalseBranch());
		cloneNode.setVisitedTrueBranch(isVisitedTrueBranch());
		return cloneNode;
	}
}
