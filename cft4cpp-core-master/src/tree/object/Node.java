package tree.object;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import tree.dependency.Dependency;

/**
 * Represent a node in the tree
 *
 * @author ducanhnguyen
 */
public abstract class Node implements INode {

    private List<INode> children = new ArrayList<>();

    private String name = "";

    private String absolutePath = "";

    private INode parent = null;

    private List<Dependency> dependencies = new ArrayList<>();

    private Icon icon = null;

    private int id;

    @Override
    public String getNewType() {
        return name;
    }

    @Override
    public INode getParent() {
        return parent;
    }

    @Override
    public void setParent(INode parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return getNewType();
    }

    @Override
    public INode clone() {
        try {
            return (INode) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getAbsolutePath() {
        return absolutePath;
    }

    @Override
    public void setAbsolutePath(String absolutePath) {
        final String DELIMITER_BETWEEN_COMPOPNENT_TYPE1 = "\\";
        final String DELIMITER_BETWEEN_COMPOPNENT_TYPE2 = File.separator;
        this.absolutePath = absolutePath.replace(
                DELIMITER_BETWEEN_COMPOPNENT_TYPE1,
                DELIMITER_BETWEEN_COMPOPNENT_TYPE2);
        this.absolutePath = this.absolutePath.replace(File.separator + "."
                + File.separator, File.separator);
        name = this.absolutePath.substring(this.absolutePath
                        .lastIndexOf(DELIMITER_BETWEEN_COMPOPNENT_TYPE2) + 1,
                this.absolutePath.length());
    }

    @Override
    public List<INode> getChildren() {
        return children;
    }

    @Override
    public void setChildren(List<INode> children) {
        this.children = children;
    }

    @Override
    public List<Dependency> getDependencies() {
        return dependencies;
    }

    @Override
    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    protected void setIcon(Icon icon) {
        this.icon = icon;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node objCast = (Node) obj;
            if (objCast.getAbsolutePath().equals(getAbsolutePath()))
                return true;
            else
                return false;
        } else
            return true;
    }
}
