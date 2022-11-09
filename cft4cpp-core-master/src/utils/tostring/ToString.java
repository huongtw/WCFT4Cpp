package utils.tostring;

import tree.object.INode;

public abstract class ToString {

    protected String treeInString = new String();

    public ToString(INode root) {
        treeInString = this.toString(root);
    }

    protected String genTab(int level) {
        String tab = "";
        for (int i = 0; i < level; i++)
            tab += "     ";
        return tab;
    }

    public String getTreeInString() {
        return treeInString;
    }

    abstract public String toString(INode n);
}
