package utils.search;

import tree.object.ClassNode;
import tree.object.INode;

public class ClassNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof ClassNode)
            return true;
        return false;
    }
}
