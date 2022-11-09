package tree.object;

import java.io.File;

import config.Paths;
import parser.projectparser.ProjectParser;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

public class FunctionNode extends AbstractFunctionNode {

    public static void main(String[] args) {
        ProjectParser parser = new ProjectParser(new File(Paths.TSDV_R1), null);
        INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "SimpleTest(RGBA)")
                .get(0);

        System.out.println(((FunctionNode) function).getPassingVariables());

    }

}
