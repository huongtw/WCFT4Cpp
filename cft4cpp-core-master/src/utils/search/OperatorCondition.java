package utils.search;

import testdatagen.se.expression.OperatorNode;
import tree.object.INode;

public class OperatorCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof OperatorNode)
            return true;
        return false;
    }
}
