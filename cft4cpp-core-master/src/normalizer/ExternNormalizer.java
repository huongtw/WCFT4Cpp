package normalizer;

import java.io.File;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTInitializer;

import config.Paths;
import interfaces.INormalizer;
import parser.projectparser.ProjectParser;
import testdatagen.structuregen.ChangedToken;
import tree.dependency.IVariableSearchingSpace;
import tree.dependency.Level;
import tree.dependency.VariableSearchingSpace;
import tree.object.IFunctionNode;
import tree.object.INode;
import tree.object.VariableNode;
import utils.Utils;
import utils.search.ExternVariableNodeCondition;
import utils.search.FunctionNodeCondition;
import utils.search.Search;
import utils.search.VariableNodeCondition;

/**
 * Replace extern variables with its value
 *
 * @author ducanhnguyen
 */
public class ExternNormalizer extends AbstractFunctionNormalizer implements IFunctionNormalizer {

    public ExternNormalizer() {
    }

    public ExternNormalizer(IFunctionNode functionNode) {
        this.functionNode = functionNode;
    }

    public static void main(String[] args) {
        ProjectParser parser = new ProjectParser(new File(Paths.TSDV_R1_2));
        IFunctionNode function = (IFunctionNode) Search
                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "ExternKeywordTest(int)").get(0);

        System.out.println(function.getAST().getRawSignature());
        ExternNormalizer normalizer = new ExternNormalizer();
        normalizer.setFunctionNode(function);
        normalizer.normalize();

        System.out.println(normalizer.getTokens());
        System.out.println(normalizer.getNormalizedSourcecode());
    }

    @Override
    public void normalize() {
        if (functionNode != null) {
            normalizeSourcecode = functionNode.getAST().getRawSignature();

            IVariableSearchingSpace space = new VariableSearchingSpace(functionNode);

            for (Level level : space.getSpaces())
                for (INode n : level) {

                    List<INode> externsVariables = Search.searchNodes(n, new ExternVariableNodeCondition());

                    for (INode externsVariable : externsVariables)
                        if (externsVariable instanceof VariableNode) {
                            /**
                             * Convert value of extern variable into
                             * corresponding value
                             */
                            String oldName = externsVariable.getNewType();
                            String newName = getValueOfExternalVariable(oldName, Utils.getRoot(n));

                            normalizeSourcecode = normalizeSourcecode.replaceAll("\\b" + oldName + "\\b", newName);

                            tokens.add(new ChangedToken(newName, oldName));
                        }
                }
        } else
            normalizeSourcecode = INormalizer.ERROR;
    }

    /**
     * Get initializer of an extern variable in the current project
     *
     * @param nameExternalVariable
     * @param root
     * @return
     */
    private String getValueOfExternalVariable(String nameExternalVariable, INode root) {
        String value = "";

        List<INode> nodes = Search.searchNodes(root, new VariableNodeCondition());

        for (INode node : nodes)
            if (node instanceof VariableNode) {

                VariableNode n = (VariableNode) node;
                if (!n.isExtern() && n.getNewType().equals(nameExternalVariable)) {
                    IASTInitializer initializer = n.getInitializer();

                    if (initializer != null)
                        return initializer.getChildren()[0].getRawSignature();
                }
            }
        return value;
    }
}
