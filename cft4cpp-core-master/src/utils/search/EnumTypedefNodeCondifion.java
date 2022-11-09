package utils.search;

import tree.object.EnumTypedefNode;
import tree.object.INode;

/**
 * Created by DucToan on 14/07/2017.
 */
public class EnumTypedefNodeCondifion extends SearchCondition {
    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof EnumTypedefNode)
            return true;
        return false;
    }
}
