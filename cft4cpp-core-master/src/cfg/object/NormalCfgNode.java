package cfg.object;

import org.eclipse.cdt.core.dom.ast.IASTNode;

/**
 * Represent normal statements (not flag statement, scope statement, etc.)
 *
 * @author ducanh
 */
public class NormalCfgNode extends CfgNode {

	private IASTNode ast;

	public NormalCfgNode(IASTNode node) {
		ast = node;
		setContent(ast.getRawSignature());
		setAstLocation(node.getFileLocation());
	}

	public IASTNode getAst() {
		return ast;
	}

	public void setAst(IASTNode ast) {
		if (ast != null) {
			this.ast = ast;
			setContent(ast.getRawSignature());
		}
	}

	@Override
	public String toString() {
		if (ast != null) {
			return ast.getRawSignature();
		} else
			return getContent();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		NormalCfgNode cloneNode = (NormalCfgNode) super.clone();
		cloneNode.setAst(ast);
		return cloneNode;
	}

	public static void main(String[] args) throws CloneNotSupportedException {
		NormalCfgNode c = new NormalCfgNode(null);
		c.setContent("xx");
		NormalCfgNode clone = (NormalCfgNode) c.clone();
		System.out.println(clone.getContent());
	}
}
