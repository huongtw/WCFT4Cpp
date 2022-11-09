package utils.search;

import tree.object.INode;
import tree.object.VariableNode;

public class VariableNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof VariableNode)
            return true;
        return false;
    }
}
