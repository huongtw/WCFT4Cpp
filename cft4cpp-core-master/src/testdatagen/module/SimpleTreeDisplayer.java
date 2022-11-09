package testdatagen.module;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import config.FunctionConfig;
import config.ParameterBound;
import config.Paths;
import parser.projectparser.ProjectParser;
import testdata.object.IAbstractDataNode;
import testdata.object.NormalDataNode;
import testdata.object.OneDimensionDataNode;
import testdata.object.OneLevelDataNode;
import tree.object.IFunctionNode;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

public class SimpleTreeDisplayer {

    String treeInString = "";

    public static void main(String[] args) throws Exception {
        ProjectParser parser = new ProjectParser(new File(Paths.DATA_GEN_TEST));

        String name = "test(int,int*,int[],int[2],char,char*,char[],char[10],SinhVien*,SinhVien,SinhVien[])";
        IFunctionNode function = (IFunctionNode) Search
                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), name).get(0);
        FunctionConfig functionConfig = new FunctionConfig();
        functionConfig.setCharacterBound(new ParameterBound(32, 36));
        functionConfig.setIntegerBound(new ParameterBound(45, 50));
        functionConfig.setSizeOfArray(3);
        function.setFunctionConfig(functionConfig);
        /**
         *
         */
        Map<String, Object> staticSolution = new HashMap<>();
        staticSolution.put("sv.age", 1);
        staticSolution.put("sv.name[2]", 97);

        IDataTreeGeneration dataTreeGen = new DataTreeGeneration();
        dataTreeGen.setFunctionNode(function);
        dataTreeGen.setStaticSolution(staticSolution);
        dataTreeGen.getInputforGoogleTest();

    }

    private void displayTree(IAbstractDataNode n, int level) {
        if (n == null)
            return;
        else {
            treeInString += genTab(level) + "[" + n.getClass().getSimpleName() + "] real name: " + n.getName() + "\n";
            treeInString += genTab(level + 1) + "virtual name: " + n.getVituralName() + "\n";
            treeInString += genTab(level + 1) + "type: " + n.getType() + "\n";
            treeInString += genTab(level + 1) + "is external variable : " + n.getExternelVariable() + "\n";

            if (n instanceof NormalDataNode)
                treeInString += genTab(level + 1) + "value: " + ((NormalDataNode) n).getValue() + "\n";
            else if (n instanceof OneLevelDataNode)
                treeInString += genTab(level + 1) + "size (-1 = NULL): " + ((OneLevelDataNode) n).getAllocatedSize()
                        + "\n";
            else if (n instanceof OneDimensionDataNode)
                treeInString += genTab(level + 1) + "size: " + ((OneDimensionDataNode) n).getSize() + "\n";

            if (n.getParent() != null)
                treeInString += genTab(level + 1) + "virtual name of parent: " + n.getParent().getVituralName() + "\n";

            if (n.getCorrespondingVar() != null)
                treeInString += genTab(level + 1) + "corresponding variable node: "
                        + n.getCorrespondingVar().getAbsolutePath() + "(" + n.getCorrespondingVar().getClass().getSimpleName() + ")" + "\n";
            // treeInString += genTab(level + 1) + "dot getter:" + n.getDotGetterInStr() +
            // "\n";
            // treeInString += genTab(level + 1) + "dot setter:" + n.getDotSetterInStr("aa")
            // + "\n";

            // if (n.getGetterInStr().length() > 0)
            // treeInString += genTab(level + 1) + "getter:" + n.getGetterInStr() + "\n";
            // if (n.getSetterInStr("aa").length() > 0)
            // treeInString += genTab(level + 1) + "setter:" + n.getSetterInStr("aa") +
            // "\n";

            if (n.getChildren() != null)
                for (IAbstractDataNode child : n.getChildren()) {
                    displayTree(child, ++level);
                    level--;
                }
        }
    }

    protected String genTab(int level) {
        String tab = "";
        for (int i = 0; i < level; i++)
            tab += "     ";
        return tab;
    }

    public String toString(IAbstractDataNode n) {
        displayTree(n, 0);
        return treeInString;
    }
}
