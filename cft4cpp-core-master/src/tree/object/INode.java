package tree.object;

import java.util.List;

import javax.swing.Icon;

import interfaces.ITreeNode;
import tree.dependency.Dependency;

/**
 * Interface represents a node in a tree, e.g., structure tree
 *
 * @author DucAnh
 */
public interface INode extends ITreeNode {

    INode clone();

    String getAbsolutePath();

    void setAbsolutePath(String absolutePath);

    List<INode> getChildren();

    void setChildren(List<INode> children);

    List<Dependency> getDependencies();

    void setDependencies(List<Dependency> dependencies);

    Icon getIcon();

    String getNewType();

    INode getParent();

    void setParent(INode parent);

    String getName();

    void setName(String name);

    int getId();

    void setId(int id);
}
