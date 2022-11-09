package utils.search;

import tree.object.HeaderNode;
import tree.object.INode;
import tree.object.SourcecodeFileNode;

public class HeaderNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof HeaderNode || n instanceof SourcecodeFileNode)
            return true;
        return false;
    }
}
