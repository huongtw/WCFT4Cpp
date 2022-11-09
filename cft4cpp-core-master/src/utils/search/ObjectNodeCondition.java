package utils.search;

import tree.object.INode;
import tree.object.ObjectNode;

public class ObjectNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof ObjectNode)
            return true;
        return false;
    }
}
