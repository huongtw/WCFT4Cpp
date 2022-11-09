package testdatagen.se.expression;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;

public class OperatorNode extends AbstractExpressionNode {

    public OperatorNode(int operator) {
        switch (operator) {
            case IASTBinaryExpression.op_binaryAnd:
            case IASTBinaryExpression.op_logicalAnd:
                setName("&&");
                break;
            case IASTBinaryExpression.op_binaryOr:
            case IASTBinaryExpression.op_logicalOr:
                setName("||");
                break;
        }
    }

    public OperatorNode(String operator) {
        setName(operator);
    }
}
