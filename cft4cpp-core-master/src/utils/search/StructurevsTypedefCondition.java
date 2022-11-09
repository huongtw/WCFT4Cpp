package utils.search;

import tree.object.FunctionNode;
import tree.object.INode;
import tree.object.StructureNode;
import tree.object.TypedefDeclaration;
import tree.object.VariableNode;

public class StructurevsTypedefCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof StructureNode || n instanceof TypedefDeclaration)
            return true;
        else if (n instanceof VariableNode && !(n.getParent() instanceof FunctionNode))
            return true;
        return false;
    }
}
