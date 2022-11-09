package normalizer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUnaryExpression;

import utils.ASTUtils;
import utils.Utils;

/**
 * Split the given statement (contain ++, --) into some corresponding
 * statements.
 * <p>
 * <p>
 * Ex: "int a = ++b;" -----------> "++b; int a= b;"
 *
 * @author ducanhnguyen
 */
public class ShortenIncreaseDecreaseNormalizer extends AbstractStatementNormalizer implements IStatementNormalizer {
    /**
     * The statements that execute before the given statement
     */
    private List<String> pre = new ArrayList<>();

    /**
     * The statements that execute after the given statement
     */
    private List<String> after = new ArrayList<>();

    public ShortenIncreaseDecreaseNormalizer() {
    }

    public static void main(String[] args) throws Exception {
        String[] tests = new String[]{"int a = ++b - c--", "p1[0][0]++"};

        for (String test : tests) {
            ShortenIncreaseDecreaseNormalizer norm = new ShortenIncreaseDecreaseNormalizer();
            norm.setOriginalSourcecode(test);
            norm.normalize();
            System.out.println("normalized source code:" + norm.getNormalizedSourcecode());
            System.out.println("pre:" + norm.getPre());
            System.out.println("after:" + norm.getAfter());
        }
    }

    @Override
    public void normalize() {
        if (originalSourcecode != null && pre != null && after != null)
            normalizeSourcecode = parseNormalNode(ASTUtils.convertToIAST(originalSourcecode), pre, after);
    }

    private String parseNormalNode(IASTNode newAst, List<String> pre, List<String> after) {
        if (newAst instanceof ICPPASTUnaryExpression)
            normalizeSourcecode = newAst.getRawSignature();
        else
        /*
		 * Get all unary expressions of the current expression. Then based on
		 * the type of each unary expression, this expression may be run prior
		 * to the current expression, or after the current expression
		 * 
		 * Ex: "x=(a++) + 1 + (--b)" -------> unary expression: {"a++", "--b}
		 */ {
            List<ICPPASTUnaryExpression> unaryExpressions = Utils.getUnaryExpressions(newAst);

            if (unaryExpressions.size() > 0)
                for (ICPPASTUnaryExpression unaryExpression : unaryExpressions) {

                    int operator = unaryExpression.getOperator();
                    switch (operator) {
                        case IASTUnaryExpression.op_prefixIncr:// ++exp
                        case IASTUnaryExpression.op_prefixDecr:// --exp
                            pre.add(unaryExpression.getRawSignature());
                            break;

                        case IASTUnaryExpression.op_postFixIncr: // exp++
                        case IASTUnaryExpression.op_postFixDecr:// exp--
                            after.add(unaryExpression.getRawSignature());
                            break;
                    }
                }
            normalizeSourcecode = normalizeAssignment(newAst.getRawSignature());
        }
        return normalizeSourcecode;
    }

    private String normalizeAssignment(String stm) {
        return stm.replace("++", "").replace("--", "");
    }

    public List<String> getPre() {
        return pre;
    }

    public void setPre(List<String> pre) {
        this.pre = pre;
    }

    public List<String> getAfter() {
        return after;
    }

    public void setAfter(List<String> after) {
        this.after = after;
    }
}
