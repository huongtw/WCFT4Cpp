package normalizer;

import java.io.File;

import config.Paths;
import parser.projectparser.ProjectParser;
import tree.object.IFunctionNode;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

/**
 * Ex:"while (a[c] != '\0'){"--------------->"while (a[c] != 0){"
 *
 * @author DucAnh
 */
public class EndStringNormalizer extends AbstractFunctionNormalizer implements IFunctionNormalizer {
    public EndStringNormalizer() {

    }

    public EndStringNormalizer(IFunctionNode functionNode) {
        this.functionNode = functionNode;
    }

    public static void main(String[] args) {
        ProjectParser parser = new ProjectParser(new File(Paths.SYMBOLIC_EXECUTION_TEST));
        IFunctionNode function = (IFunctionNode) Search
                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "check_anagram(char[],char[])").get(0);

        System.out.println(function.getAST().getRawSignature());
        EndStringNormalizer normalizer = new EndStringNormalizer();
        normalizer.setFunctionNode(function);
        normalizer.normalize();

        System.out.println(normalizer.getTokens());
        System.out.println(normalizer.getNormalizedSourcecode());
    }

    @Override
    public void normalize() {
        String content = functionNode.getAST().getRawSignature();
        normalizeSourcecode = content.replace("'\\0'", "0");
    }
}
