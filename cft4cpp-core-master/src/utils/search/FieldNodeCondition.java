package utils.search;

import tree.object.FunctionNode;
import tree.object.INode;
import tree.object.VariableNode;

public class FieldNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof VariableNode && !(n.getParent() instanceof FunctionNode))
            return true;
        return false;
    }
}
