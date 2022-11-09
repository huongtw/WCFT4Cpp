package utils.search;

import tree.object.CppFileNode;
import tree.object.INode;

public class CppFileNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof CppFileNode)
            return true;
        return false;
    }
}
