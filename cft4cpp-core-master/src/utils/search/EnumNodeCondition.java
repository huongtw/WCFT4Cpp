package utils.search;

import tree.object.EnumNode;
import tree.object.INode;

/**
 * Demo a condition
 *
 * @author DucAnh
 */
public class EnumNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof EnumNode)
            return true;
        return false;
    }
}
