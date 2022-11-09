package utils.search;

import tree.object.ClassNode;
import tree.object.INode;
import tree.object.StructNode;

public class ClassvsStructCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof ClassNode || n instanceof StructNode)
            return true;
        return false;
    }
}
