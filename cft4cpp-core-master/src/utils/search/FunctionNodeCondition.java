package utils.search;

import tree.object.IFunctionNode;
import tree.object.INode;

/**
 * Demo a condition
 *
 * @author DucAnh
 */
public class FunctionNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof IFunctionNode)
            return true;
        return false;
    }
}
