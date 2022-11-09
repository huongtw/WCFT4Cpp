package utils.search;

import tree.object.INode;
import tree.object.SourcecodeFileNode;

public class SourcecodeFileNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof SourcecodeFileNode)
            return true;
        return false;
    }
}
