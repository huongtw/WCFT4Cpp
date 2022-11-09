package testdatagen.se.expander;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTArraySubscriptExpression;

import testdatagen.se.CustomJeval;
import testdatagen.se.ISymbolicExecution;
import utils.ASTUtils;
import utils.Utils;

/**
 * All indexes must be greater than 0. We must add condition into path
 * constraints.
 * <p>
 * Ex: a[i+1] ---> new constraint: "i+1>0"
 *
 * @author DucAnh
 */
public class ArrayIndexExpander extends AbstractPathConstraintExpander implements IPathConstraintExpander {

    public static void main(String[] args) {
        AbstractPathConstraintExpander expander = new ArrayIndexExpander();
        expander.setInputConstraint("a[b]==a  [   b-1]");
        expander.generateNewConstraints();
        System.out.println(expander.getNewConstraints());
    }

    @Override
    public void generateNewConstraints() {
        List<String> indexConstraints = getIndexofArrayItemConstraints(inputConstraint);
        for (String indexConstraint : indexConstraints)
            newConstraints.add(indexConstraint);
    }

    /**
     * All indexes must be greater than 0. We must add condition into path
     * constraints.
     * <p>
     * Ex: a[i+1] ---> new constraint: "i+1>0"
     */
    private List<String> getIndexofArrayItemConstraints(String constraint) {
        List<String> newConstraints = new ArrayList<>();

        List<ICPPASTArraySubscriptExpression> arrayItems = Utils
                .getArraySubscriptExpression(ASTUtils.convertToIAST(constraint));

        for (ICPPASTArraySubscriptExpression arrayItem : arrayItems)
            if (arrayItem.getRawSignature().startsWith("[")) {
                // if
                // (arrayItem.getRawSignature().startsWith(ISymbolicExecution.TO_INT_Z3
                // + "[")) {
                // nothing to do
            } else {
                IASTNode index = arrayItem.getChildren()[1];
                try {
                    Integer.parseInt(index.getRawSignature());
                } catch (Exception e1) {
                    /*
					 * If index of array item is not an integer.
					 */
                    String newConstraint = index.getRawSignature() + ">=0";
                    if (new CustomJeval().evaluate(newConstraint).equals(CustomJeval.TRUE)) {
                        // no thing to do
                    } else if (new CustomJeval().evaluate(newConstraint).equals(CustomJeval.FALSE))
                        newConstraints.add(ISymbolicExecution.NO_SOLUTION_CONSTRAINT);
                    else
                        newConstraints.add(newConstraint);
                }
            }
        return newConstraints;
    }
}
