package utils.tostring;

import tree.dependency.Dependency;
import tree.object.FunctionNode;
import tree.object.INode;
import tree.object.Node;
import tree.object.TypedefDeclaration;
import tree.object.VariableNode;

public class DependencyTreeDisplayer extends ToString {

    // public static void main(String[] args) {
    // // Project tree generation
    // IProjectNode projectRootNode = ProjectLoader.load(new
    // File(ConfigOfAnh.TSDV_LOG4CPP));
    //
    // // display tree of project
    // ToString treeDisplayer = new DependencyTreeDisplayer((Node)
    // projectRootNode);
    //
    // }
    public DependencyTreeDisplayer(INode root) {
        super(root);
    }

    private void displayTree(INode n, int level) {
        if (n == null)
            return;
        else {
            if (n instanceof VariableNode || n instanceof FunctionNode || n instanceof TypedefDeclaration)
                treeInString += genTab(level) + "[" + n.getClass().getSimpleName() + "] " + n.toString() + "\n";
            else
                treeInString += genTab(level) + "[" + n.getClass().getSimpleName() + "] " + n.getNewType() + "\n";

            treeInString += genTab(level) + n.getAbsolutePath() + "\n";
            for (Dependency d : n.getDependencies())
                if (d.getStartArrow().equals(n))
                    treeInString += genTab(level + 1) + "[" + d.getClass().getSimpleName() + "]"
                            + d.getEndArrow().getAbsolutePath() + "\n";

        }
        for (Object child : n.getChildren()) {
            displayTree((Node) child, ++level);
            level--;
        }

    }

    @Override
    public String toString(INode n) {
        displayTree(n, 0);
        return treeInString;
    }
}
