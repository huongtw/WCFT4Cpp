package utils.search;

import tree.object.INode;
import tree.object.TypedefDeclaration;

public class TypedefNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof TypedefDeclaration)
            return true;
        return false;
    }
}
