package testdatagen.module;

import testdata.object.AbstractDataNode;
import testdata.object.IAbstractDataNode;
import testdata.object.RootDataNode;

public class Search2 {

    /**
     * @param names Danh sách tên có thứ tự
     * @param n     Tên node cha bắt đầu tìm kiếm (Bắt đầu tìm kiếm từ con node
     *              cha)
     * @return
     */
    public static IAbstractDataNode findNodeByChainName(String[] names, IAbstractDataNode n) {
        IAbstractDataNode output = n;
        for (String name : names) {
            IAbstractDataNode nName = Search2.findNodeByName(name, output);
            if (nName == null)
                return null;
            else
                output = nName;
        }
        return output;
    }

    /**
     * Tìm con một node có tên xác định
     *
     * @param name tên node cần tìm
     * @param n    node cha
     * @return
     */
    public static AbstractDataNode findNodeByName(String name, IAbstractDataNode n) {
        for (AbstractDataNode child : n.getChildren())
            if (child.getName().equals(name))
                return child;
        return null;
    }

    public static RootDataNode getRoot(IAbstractDataNode n) {
        do
            if (n instanceof RootDataNode)
                return (RootDataNode) n;
            else if (n.getParent() != null)
                n = n.getParent();
            else
                return null;
        while (n != null);
        return null;
    }
}
