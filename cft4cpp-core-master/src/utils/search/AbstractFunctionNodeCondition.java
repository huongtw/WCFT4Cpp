package utils.search;

import tree.object.AbstractFunctionNode;
import tree.object.INode;

public class AbstractFunctionNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof AbstractFunctionNode)
            return true;
        return false;
    }
}
