package cfg.object;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;

import utils.Utils;

public abstract class AbstractConditionLoopCfgNode extends ConditionCfgNode implements IConditionLoopCfgNode {

    public AbstractConditionLoopCfgNode(IASTNode node) {
        super(node);
    }

    @Override
    public boolean isBoundCondition() {
        return getUpperBound() != IConditionLoopCfgNode.CANNOT_DETECT_BOUND
                || getLowerBound() != IConditionLoopCfgNode.CANNOT_DETECT_BOUND;
    }

    @Override
    public double getUpperBound() {
        double upperBound = IConditionLoopCfgNode.CANNOT_DETECT_BOUND;

        if (getAst() instanceof IASTBinaryExpression) {
            IASTBinaryExpression cast = (IASTBinaryExpression) getAst();
            IASTNode left = Utils.shortenAstNode(cast.getOperand1());
            IASTNode right = Utils.shortenAstNode(cast.getOperand2());
            int operand = cast.getOperator();

            // Ex: a<=3, a<3,a==3
            if (left instanceof IASTFieldReference || left instanceof IASTIdExpression) {
                if (Utils.toDouble(right.getRawSignature()) != Utils.UNDEFINED_TO_DOUBLE)
                    switch (operand) {
                        case IASTBinaryExpression.op_lessEqual:
                            upperBound = Utils.toDouble(right.getRawSignature());
                            break;
                        case IASTBinaryExpression.op_lessThan:
                            upperBound = Utils.toDouble(right.getRawSignature()) - 1;
                            break;
                        case IASTBinaryExpression.op_equals:
                            upperBound = Utils.toDouble(right.getRawSignature());
                            break;
                        default:
                            break;
                    }
            } else
                // Ex: 3>=a, 3>a,3==a
                if (right instanceof IASTFieldReference || right instanceof IASTIdExpression) {
                    if (Utils.toDouble(left.getRawSignature()) != Utils.UNDEFINED_TO_DOUBLE)
                        switch (operand) {
                            case IASTBinaryExpression.op_greaterEqual:
                                upperBound = Utils.toDouble(left.getRawSignature());
                                break;
                            case IASTBinaryExpression.op_greaterThan:
                                upperBound = Utils.toDouble(left.getRawSignature()) - 1;
                                break;
                            case IASTBinaryExpression.op_equals:
                                upperBound = Utils.toDouble(left.getRawSignature());
                                break;
                            default:
                                break;
                        }
                }

        }
        return upperBound;
    }

    @Override
    public double getLowerBound() {
        double lowerBound = IConditionLoopCfgNode.CANNOT_DETECT_BOUND;

        if (getAst() instanceof IASTBinaryExpression) {
            IASTBinaryExpression cast = (IASTBinaryExpression) getAst();
            IASTNode left = Utils.shortenAstNode(cast.getOperand1());
            IASTNode right = Utils.shortenAstNode(cast.getOperand2());
            int operand = cast.getOperator();

            // Ex: a>=3, a>3, a==3
            if (left instanceof IASTFieldReference || left instanceof IASTIdExpression) {
                if (Utils.toDouble(right.getRawSignature()) != Utils.UNDEFINED_TO_DOUBLE)
                    switch (operand) {
                        case IASTBinaryExpression.op_greaterEqual:
                            lowerBound = Utils.toDouble(right.getRawSignature());
                            break;
                        case IASTBinaryExpression.op_greaterThan:
                            lowerBound = Utils.toDouble(right.getRawSignature()) + 1;
                            break;
                        case IASTBinaryExpression.op_equals:
                            lowerBound = Utils.toDouble(right.getRawSignature());
                            break;
                        default:
                            break;
                    }
            } else
                // Ex: 3<=a, 3<a, 3==a
                if (right instanceof IASTFieldReference || right instanceof IASTIdExpression)
                    if (Utils.toDouble(left.getRawSignature()) != Utils.UNDEFINED_TO_DOUBLE)
                        switch (operand) {
                            case IASTBinaryExpression.op_lessEqual:
                                lowerBound = Utils.toDouble(left.getRawSignature());
                                break;
                            case IASTBinaryExpression.op_lessThan:
                                lowerBound = Utils.toDouble(left.getRawSignature()) + 1;
                                break;
                            case IASTBinaryExpression.op_equals:
                                lowerBound = Utils.toDouble(left.getRawSignature());
                                break;
                            default:
                                break;
                        }
        }
        return lowerBound;
    }

    @Override
    public String getIterationVariable() {
        String iterationVariable = null;
        if (getAst() instanceof IASTBinaryExpression) {
            IASTBinaryExpression cast = (IASTBinaryExpression) getAst();
            IASTNode left = Utils.shortenAstNode(cast.getOperand1());
            IASTNode right = Utils.shortenAstNode(cast.getOperand2());
            int operand = cast.getOperator();
            switch (operand) {
                case IASTBinaryExpression.op_lessEqual:
                case IASTBinaryExpression.op_lessThan:
                case IASTBinaryExpression.op_greaterEqual:
                case IASTBinaryExpression.op_greaterThan:
                case IASTBinaryExpression.op_assign:

                    if ((left instanceof IASTFieldReference || left instanceof IASTIdExpression)
                            && Utils.toDouble(right.getRawSignature()) != Utils.UNDEFINED_TO_DOUBLE) {
                        iterationVariable = left.getRawSignature();
                    } else if ((right instanceof IASTFieldReference || right instanceof IASTIdExpression)
                            && Utils.toDouble(left.getRawSignature()) != Utils.UNDEFINED_TO_DOUBLE)
                        iterationVariable = right.getRawSignature();
                default:
                    break;
            }

        }
        return iterationVariable;
    }
}
