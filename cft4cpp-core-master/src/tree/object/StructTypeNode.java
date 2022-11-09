package tree.object;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;

public class StructTypeNode extends CustomASTNode<IASTDeclSpecifier> {
    @Override
    public String toString() {
        if (getAST() == null)
            return getNewType();
        else
            return super.toString();
    }
}
