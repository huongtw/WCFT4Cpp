package utils.search;

import tree.object.ConstructorNode;
import tree.object.INode;

/**
 * Demo a condition
 *
 * @author DucAnh
 */
public class ConstructorNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof ConstructorNode)
            return true;
        return false;
    }
}
