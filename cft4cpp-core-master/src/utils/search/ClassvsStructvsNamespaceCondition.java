package utils.search;

import tree.object.ClassNode;
import tree.object.INode;
import tree.object.NamespaceNode;
import tree.object.StructNode;

public class ClassvsStructvsNamespaceCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof ClassNode || n instanceof StructNode || n instanceof NamespaceNode)
            return true;
        return false;
    }
}
