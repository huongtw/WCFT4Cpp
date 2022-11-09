package utils.search;

import tree.object.FolderNode;
import tree.object.INode;

public class FolderNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof FolderNode)
            return true;
        return false;
    }
}
