package tree.dependency;

import java.io.File;
import java.util.List;

import config.Paths;
import parser.projectparser.ProjectParser;
import tree.object.AbstractFunctionNode;
import tree.object.DefinitionFunctionNode;
import tree.object.FunctionNode;
import tree.object.IFunctionNode;
import tree.object.INode;
import utils.search.AbstractFunctionNodeCondition;
import utils.search.DefinitionFunctionNodeCondition;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

/**
 * Find method
 *
 * @author DucAnh
 */
public class MethodFinder {

    /**
     * Node in the structure that contains the searched function
     */
    private IFunctionNode context;

    public MethodFinder(IFunctionNode context) {
        this.context = context;
    }

    public static void main(String[] args) throws Exception {
        ProjectParser parser = new ProjectParser(new File(Paths.SEPARATE_FUNCTION_TEST), null);
        FunctionNode context = (FunctionNode) Search
                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), File.separator + "test5()").get(0);

        MethodFinder finder = new MethodFinder(context);
        finder.find("sub", 1);
    }

    public INode find(String simpleFunctionName, int paramater) throws Exception {
        List<Level> spaces = new VariableSearchingSpace(context).getSpaces();
        for (Level l : spaces)
            for (INode n : l) {

                List<INode> completeFunctions = Search.searchNodes(n, new AbstractFunctionNodeCondition());
                for (INode function : completeFunctions)
                    if (((AbstractFunctionNode) function).getSimpleName().equals(simpleFunctionName))
                        if (((AbstractFunctionNode) function).getArguments().size() == paramater)
                            return function;

                List<INode> onlyDefinedFunction = Search.searchNodes(n, new DefinitionFunctionNodeCondition());
                for (INode function : onlyDefinedFunction)
                    if (((DefinitionFunctionNode) function).getSimpleName().equals(simpleFunctionName))
                        if (((DefinitionFunctionNode) function).getArguments().size() == paramater)
                            return function;
            }
        return null;
    }
}
