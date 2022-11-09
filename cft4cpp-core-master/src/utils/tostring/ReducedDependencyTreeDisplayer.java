package utils.tostring;

import java.io.File;

import config.Paths;
import parser.projectparser.ProjectLoader;
import tree.dependency.Dependency;
import tree.object.FolderNode;
import tree.object.FunctionNode;
import tree.object.INode;
import tree.object.IProjectNode;
import tree.object.Node;
import tree.object.SourcecodeFileNode;
import tree.object.TypedefDeclaration;
import tree.object.VariableNode;
import utils.search.CppFileNodeCondition;
import utils.search.Search;

public class ReducedDependencyTreeDisplayer extends ToString {

    public ReducedDependencyTreeDisplayer(INode root) {
        super(root);
    }

    public static void main(String[] args) {
        // Project tree generation
        IProjectNode projectRootNode = new ProjectLoader().load(new File(Paths.TSDV_LOG4CPP));

        new ReducedDependencyTreeDisplayer(projectRootNode);

    }

    private void displayTree(INode n, int level) {
        if (n == null || n instanceof FolderNode && Search.searchNodes(n, new CppFileNodeCondition()).size() == 0)
            return;
        else {
            if (n instanceof VariableNode || n instanceof FunctionNode || n instanceof TypedefDeclaration)
                treeInString += genTab(level) + "[" + n.getClass().getSimpleName() + "] " + n.toString() + "\n";
            else
                treeInString += genTab(level) + "[" + n.getClass().getSimpleName() + "] " + n.getNewType() + "\n";

            if (n instanceof SourcecodeFileNode)
                treeInString += genTab(level + 1) + "path = " + n.getAbsolutePath() + "\n";
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
