package utils.search;

import tree.object.INode;
import tree.object.StructTypedefNode;

public class StructTypedefNodeCondifion extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof StructTypedefNode)
            return true;
        return false;
    }
}
