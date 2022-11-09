package testdatagen.se.normalstatementparser;

import org.eclipse.cdt.core.dom.ast.IASTNode;

import testdatagen.se.ExpressionRewriterUtils;
import testdatagen.se.memory.VariableNodeTable;
import utils.Utils;

public class ConditionParser extends StatementParser {

    private String newConstraint = "";

    @Override
    public void parse(IASTNode ast, VariableNodeTable table) throws Exception {
        ast = Utils.shortenAstNode(ast);
        newConstraint = ExpressionRewriterUtils.rewrite(table, ast.getRawSignature());
    }

    public String getNewConstraint() {
        return newConstraint;
    }

    public void setNewConstraint(String newConstraint) {
        this.newConstraint = newConstraint;
    }
}
