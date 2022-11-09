package testdatagen.se.expression;

import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;

public class NegativeNode extends AbstractExpressionNode {

    public NegativeNode(int operator) {
        switch (operator) {
            case IASTUnaryExpression.op_not:
                setName("!");
                break;
        }
    }

    public NegativeNode(String operator) {
        setName(operator);
    }
}
