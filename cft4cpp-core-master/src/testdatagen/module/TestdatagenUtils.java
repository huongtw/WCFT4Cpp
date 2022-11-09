package testdatagen.module;

import testdata.object.IAbstractDataNode;
import testdata.object.RootDataNode;

public class TestdatagenUtils {

    public static IAbstractDataNode getRoot(IAbstractDataNode n) {
        if (n instanceof RootDataNode)
            return n;
        else
            return TestdatagenUtils.getRoot(n.getParent());
    }
}
