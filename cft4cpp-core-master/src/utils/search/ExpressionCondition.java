package utils.search;

import testdatagen.se.expression.ExpressionNode;
import tree.object.INode;

public class ExpressionCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof ExpressionNode)
            return true;
        return false;
    }
}
