package normalizer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;

import config.Paths;
import parser.projectparser.ProjectParser;
import testdatagen.structuregen.ChangedToken;
import tree.object.IFunctionNode;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

public class ThrowNormalizer extends AbstractFunctionNormalizer implements IFunctionNormalizer {

    public static final String THROW_SIGNAL = "throw ";
    public static final String REPLACEMENT = "exit(0)";

    public ThrowNormalizer() {
    }

    public ThrowNormalizer(IFunctionNode functionNode) {
        this.functionNode = functionNode;
    }

    public static void main(String[] args) throws Exception {
        ProjectParser parser = new ProjectParser(new File(Paths.SYMBOLIC_EXECUTION_TEST));
        Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "switchCase0(int,int)").get(0);

        ThrowNormalizer nom = new ThrowNormalizer();

        nom.normalize();
        System.out.println(nom.getNormalizedSourcecode());
        System.out.println(nom.getTokens());
    }

    @Override
    public void normalize() {
        IASTFunctionDefinition astFunction = functionNode.getAST();
        normalizeSourcecode = astFunction.getRawSignature();
        List<IASTExpression> expressions = getThrow(astFunction);

        int count = 0;
        for (IASTExpression expression : expressions) {
            count++;
            String id = "/*" + count + "*/";

            int startOffsetInFunction = expression.getFileLocation().getNodeOffset()
                    - astFunction.getFileLocation().getNodeOffset();
            int length = expression.getFileLocation().getNodeLength();

			/*
             *
			 */
            String content = expression.getRawSignature();

            String pre = normalizeSourcecode.substring(0, startOffsetInFunction);

            String after = normalizeSourcecode.substring(startOffsetInFunction + length);

            int spacesLength = content.length() - ThrowNormalizer.REPLACEMENT.length() - id.length();

            if (spacesLength >= 0) {
                String spaces = generateSpaces(spacesLength);
                String replacement = expression.getRawSignature().replace(content, ThrowNormalizer.REPLACEMENT + id)
                        + spaces;
                normalizeSourcecode = pre + replacement + after;

                tokens.add(new ChangedToken(replacement, content));
            } else {
				/*
				 * It is hard to occur this situation
				 */
                String replacement = expression.getRawSignature().replace(content, ThrowNormalizer.REPLACEMENT + id);
                normalizeSourcecode = pre + replacement + after;

                tokens.add(new ChangedToken(replacement, content));
            }
        }
    }

    private String generateSpaces(int length) {
        String output = "";
        final String SPACE = " ";
        for (int i = 0; i < length; i++)
            output += SPACE;
        return output;
    }

    /**
     * Get all expressions of the function
     *
     * @param function
     * @return
     */
    private List<IASTExpression> getThrow(IASTFunctionDefinition function) {
        List<IASTExpression> throwsList = new ArrayList<>();

        ASTVisitor visitor = new ASTVisitor() {
            @Override
            public int visit(IASTExpression declaration) {
                if (declaration instanceof IASTExpression)
                    if (declaration.getRawSignature().startsWith(ThrowNormalizer.THROW_SIGNAL)) {
                        throwsList.add(declaration);
                        return ASTVisitor.PROCESS_SKIP;
                    }
                return ASTVisitor.PROCESS_CONTINUE;
            }
        };

        visitor.shouldVisitExpressions = true;
        function.accept(visitor);
        return throwsList;
    }

}
