package tree.object;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;

import tree.dependency.Dependency;
import tree.dependency.ExtendDependency;

public class NamespaceNode extends CustomASTNode<ICPPASTNamespaceDefinition> implements ISourceNavigable {
    protected ArrayList<ArrayList<INode>> extendPaths = new ArrayList<>();

    public NamespaceNode() {
        try {
            Icon ICON_FOLDER = new ImageIcon(Node.class.getResource("/image/node/NamespaceNode.png"));
            setIcon(ICON_FOLDER);
        } catch (Exception e) {

        }
    }

    @Override
    public String getNewType() {
        String name = getAST().getName().getRawSignature();
        // do something here
        return name;
    }

    @Override
    public IASTFileLocation getNodeLocation() {
        return getAST().getName().getFileLocation();
    }

    @Override
    public File getSourceFile() {
        return new File(getAST().getContainingFilename());
    }

    public ArrayList<ArrayList<INode>> getExtendPaths() {
        ArrayList<INode> path = new ArrayList<>();
        this.getExtendPaths(this, path);
        return extendPaths;
    }

    public List<INode> getExtendNodes() {
        List<INode> extendedNode = new ArrayList<>();
        for (Dependency d : getDependencies())
            if (d instanceof ExtendDependency && d.getStartArrow().equals(this))
                extendedNode.add(d.getEndArrow());
        return extendedNode;
    }

    private void getExtendPaths(INode n, ArrayList<INode> path) {
        path.add(n);
        List<INode> extendedNodes = new ArrayList<>();
        if (n instanceof NamespaceNode)
            extendedNodes = ((NamespaceNode) n).getExtendNodes();
        else if (n instanceof StructureNode)
            extendedNodes = ((StructureNode) n).getExtendNodes();

        if (extendedNodes.size() > 0)
            for (INode child : extendedNodes)
                this.getExtendPaths(child, path);
        else
            extendPaths.add((ArrayList<INode>) path.clone());
        path.remove(path.size() - 1);
    }
}
