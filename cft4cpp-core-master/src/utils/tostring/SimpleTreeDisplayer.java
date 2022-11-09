package utils.tostring;

import java.io.File;

import config.Paths;
import parser.projectparser.ProjectLoader;
import tree.object.INode;
import tree.object.IProjectNode;
import tree.object.Node;

public class SimpleTreeDisplayer extends ToString {

    public SimpleTreeDisplayer(INode root) {
        super(root);
    }

    public static void main(String[] args) {
        // Project tree generation
        IProjectNode projectRootNode = new ProjectLoader().load(new File(Paths.TSDV_LOG4CPP));

        new SimpleTreeDisplayer(projectRootNode);

    }

    @Override
    public String toString(INode n) {
        displayTree(n, 0);
        return treeInString;
    }

    private void displayTree(INode n, int level) {
        if (n == null)
            return;
        else
            treeInString += genTab(level) + "[" + n.getClass().getSimpleName() + "] " + n.toString() + "\n";
        for (Object child : n.getChildren()) {
            displayTree((Node) child, ++level);
            level--;
        }

    }
}
