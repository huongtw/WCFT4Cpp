package utils.search;

import tree.object.INode;
import tree.object.UnknowObjectNode;

public class UnknownFileNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof UnknowObjectNode)
            return true;
        return false;
    }
}
