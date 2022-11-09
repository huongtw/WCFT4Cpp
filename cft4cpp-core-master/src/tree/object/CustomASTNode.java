package tree.object;

import org.eclipse.cdt.core.dom.ast.IASTNode;

public class CustomASTNode<N extends IASTNode> extends Node {

    protected N AST;

    public N getAST() {
        return this.AST;
    }

    public void setAST(N aST) {
        this.AST = aST;
    }

    @Override
    public String toString() {
        return this.AST.getRawSignature();
    }

    @Override
    public INode clone() {
        return super.clone();
    }
}
