package utils.search;

import tree.object.ExeNode;
import tree.object.INode;

public class ExeNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof ExeNode)
            return true;
        return false;
    }
}
