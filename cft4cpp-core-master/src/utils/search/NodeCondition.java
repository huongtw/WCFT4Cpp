package utils.search;

import tree.object.INode;
import tree.object.Node;

public class NodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof Node)
            return true;
        return false;
    }
}
