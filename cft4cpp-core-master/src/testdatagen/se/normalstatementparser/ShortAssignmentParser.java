package testdatagen.se.normalstatementparser;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTArraySubscriptExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIdExpression;

import testdatagen.se.memory.VariableNodeTable;
import utils.ASTUtils;
import utils.Utils;

/**
 * Parse short assignment Ex1: a/=1; Ex2: b-=2; Ex3: c+=3; Ex4: e+=4; Ex5: f%=5;
 *
 * @author ducanhnguyen
 */
public class ShortAssignmentParser extends BinaryAssignmentParser {

    @Override
    public void parse(IASTNode ast, VariableNodeTable table) throws Exception {
        ast = Utils.shortenAstNode(ast);
        if (ast instanceof IASTBinaryExpression) {

            IASTExpression right = ((IASTBinaryExpression) ast).getOperand2();
            IASTExpression left = ((IASTBinaryExpression) ast).getOperand1();

            String reducedRight = right.getRawSignature();

            switch (((IASTBinaryExpression) ast).getOperator()) {
                case IASTBinaryExpression.op_multiplyAssign:
                    reducedRight = left + "*" + reducedRight;
                    break;

                case IASTBinaryExpression.op_divideAssign:
                    reducedRight = left + "/" + reducedRight;
                    break;

                case IASTBinaryExpression.op_moduloAssign:
                    reducedRight = left + "%" + reducedRight;
                    break;

                case IASTBinaryExpression.op_plusAssign:
                    reducedRight = left + "+" + reducedRight;
                    break;

                case IASTBinaryExpression.op_minusAssign:
                    reducedRight = left + "-" + reducedRight;
                    break;
            }

			/*
             * If the left expression is an array item. VD: p1[0]
			 */
            if (left instanceof ICPPASTArraySubscriptExpression) {
                IASTNode newAST = ASTUtils.convertToIAST(left.getRawSignature() + "=" + reducedRight);
                new ArrayItemToExpressionParser().parse(newAST, table);

            } else
			/*
			 * In case left expression represents name of variable
			 */
                if (left instanceof CPPASTIdExpression) {
                    IASTNode newAST = ASTUtils.convertToIAST(left.getRawSignature() + " = " + reducedRight);
                    new NormalVariableToExpressionParser().parse(newAST, table);

                } else
                    throw new Exception("Dont support " + ast.getRawSignature());
        }
    }

}
