package utils.search;

import tree.object.FunctionNode;
import tree.object.INode;
import tree.object.VariableNode;

/**
 * Đại diện thuộc tính trong class, struct
 *
 * @author phibao37
 */
public class FieldMemberCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof VariableNode && !(n.getParent() instanceof FunctionNode))
            return true;
        return false;
    }
}
