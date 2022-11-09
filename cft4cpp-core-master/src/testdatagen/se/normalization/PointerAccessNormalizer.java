package testdatagen.se.normalization;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUnaryExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTBinaryExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIdExpression;

import normalizer.AbstractFunctionNormalizer;
import normalizer.IFunctionNormalizer;
import utils.ASTUtils;
import utils.Utils;

/**
 * Convert pointer access to array index
 *
 * @author DucAnh
 */
public class PointerAccessNormalizer extends AbstractFunctionNormalizer implements IFunctionNormalizer {

    public static void main(String[] args) {
        String[] tests = new String[]{"a == *(p1+4)", "a==*p1", "a==*(p1)", "a==(*p1)", "a==*(p1+0)", "a==*(p1-1)",
                "a== *(p2+1 + (1+1))", "a == **(p1+4)", "a==**p1", "a==**(p1)", "a==(**p1)", "a==**(p1+0)",
                "a==**(p1-1)", "a== **(p2+1 + (1+1))", "**(p+1)"};

        for (String test : tests) {
            PointerAccessNormalizer norm = new PointerAccessNormalizer();
            norm.setOriginalSourcecode(test);
            norm.normalize();
            System.out.println(norm.getNormalizedSourcecode());
        }
    }

    @Override
    public void normalize() {
        if (originalSourcecode != null && originalSourcecode.length() > 0)
            try {
                normalizeSourcecode = convertOneLevelPointerAccessToArrayItem(originalSourcecode);
                normalizeSourcecode = convertOneLevelPointerAccessToArrayItem(normalizeSourcecode);
            } catch (Exception e) {
                e.printStackTrace();
                normalizeSourcecode = originalSourcecode;
            }
        else
            normalizeSourcecode = originalSourcecode;
    }

    private String convertOneLevelPointerAccessToArrayItem(String statement) throws Exception {
        String newStatement = statement;
        List<ICPPASTUnaryExpression> unaryExpressions = Utils.getUnaryExpressions(ASTUtils.convertToIAST(statement));

        for (ICPPASTUnaryExpression unaryExpression : unaryExpressions)
            switch (unaryExpression.getOperator()) {
                case IASTUnaryExpression.op_star:
                    String oldExp = unaryExpression.getRawSignature();

                    IASTNode firstChild = Utils.shortenAstNode(unaryExpression.getChildren()[0]);
                    if (firstChild instanceof CPPASTIdExpression || firstChild instanceof IASTArraySubscriptExpression) {
                        // Ex: *p1
                        String pointerName = firstChild.getRawSignature();
                        String newExp = pointerName + "[0]";
                        newStatement = newStatement.replace(oldExp, newExp);

                    } else if (firstChild instanceof CPPASTBinaryExpression) {
                        // Ex: *(p2+1 + (1+1))
                        IASTNode pointerAst = Utils.getIds(firstChild).get(0);
                        String pointerName = pointerAst.getRawSignature();

                        String index = firstChild.getRawSignature().replaceFirst(Utils.toRegex(pointerName), "");

                        String newExp = pointerName + "[0" + index + "]";
                        newStatement = newStatement.replace(oldExp, newExp);
                    }
            }
        return newStatement;
    }

}
