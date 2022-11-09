package testdatagen.se.expression;

import org.eclipse.cdt.core.dom.ast.IASTNode;

public class ExpressionNode extends AbstractExpressionNode {

    protected IASTNode AST;
    protected boolean value = false;

    public ExpressionNode(IASTNode condition) {
        setName(condition.getRawSignature());
        AST = condition;
    }

    public IASTNode getAST() {
        return AST;
    }

    public void setAST(IASTNode aST) {
        AST = aST;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
