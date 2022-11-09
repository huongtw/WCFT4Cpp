package normalizer;

import java.io.File;

import config.Paths;
import parser.projectparser.ProjectParser;
import tree.object.IFunctionNode;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

/**
 * Convert nullptr to NULL <br/>
 * Ex:
 * <p>
 * <p>
 * <pre>
 * int NullPtrTest(int *x){
 * if (x == nullptr)
 * return -1;
 * else
 * return *x;
 * }
 * </pre>
 *
 * @author ducanhnguyen
 */
public class NullPtrNormalizer extends AbstractFunctionNormalizer implements IFunctionNormalizer {
    public NullPtrNormalizer() {

    }

    public NullPtrNormalizer(IFunctionNode functionNode) {
        this.functionNode = functionNode;
    }

    public static void main(String[] args) {
        ProjectParser parser = new ProjectParser(new File(Paths.TSDV_R1_2));
        IFunctionNode function = (IFunctionNode) Search
                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "ExternKeywordTest(int)").get(0);

        System.out.println(function.getAST().getRawSignature());
        NullPtrNormalizer normalizer = new NullPtrNormalizer();
        normalizer.normalize();

        System.out.println(normalizer.getTokens());
        System.out.println(normalizer.getNormalizedSourcecode());
    }

    @Override
    public void normalize() {
        String content = functionNode.getAST().getRawSignature();
        normalizeSourcecode = content.replaceAll("\\bnullptr\\b", "NULL");
    }
}
