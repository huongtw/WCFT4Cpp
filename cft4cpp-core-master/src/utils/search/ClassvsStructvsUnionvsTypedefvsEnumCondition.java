package utils.search;

import tree.object.ClassNode;
import tree.object.EnumNode;
import tree.object.FunctionNode;
import tree.object.INode;
import tree.object.SingleTypedefDeclaration;
import tree.object.StructNode;
import tree.object.UnionNode;
import tree.object.VariableNode;

public class ClassvsStructvsUnionvsTypedefvsEnumCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof ClassNode || n instanceof StructNode || n instanceof SingleTypedefDeclaration
                || n instanceof UnionNode || n instanceof EnumNode)
            return true;
        else if (n instanceof VariableNode && !(n.getParent() instanceof FunctionNode))
            return true;
        return false;
    }
}
