package utils.search;

import tree.object.INode;
import tree.object.NamespaceNode;

public class NamespaceNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof NamespaceNode)
            return true;
        return false;
    }
}
