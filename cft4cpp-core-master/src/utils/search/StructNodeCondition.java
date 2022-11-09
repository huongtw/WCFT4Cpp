package utils.search;

import tree.object.INode;
import tree.object.SpecialStructTypedefNode;
import tree.object.StructNode;
import tree.object.StructTypedefNode;

/**
 * Demo a condition
 *
 * @author DucAnh
 */
public class StructNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof StructNode || n instanceof SpecialStructTypedefNode || n instanceof StructTypedefNode)
            return true;
        return false;
    }
}
