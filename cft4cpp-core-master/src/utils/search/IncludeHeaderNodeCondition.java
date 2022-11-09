package utils.search;

import tree.object.INode;
import tree.object.IncludeHeaderNode;

public class IncludeHeaderNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof IncludeHeaderNode)
            return true;
        return false;
    }
}
