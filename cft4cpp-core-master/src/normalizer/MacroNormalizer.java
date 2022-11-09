package normalizer;

import java.io.File;
import java.util.List;

import config.Paths;
import parser.projectparser.PreprocessorParser;
import parser.projectparser.ProjectParser;
import testdatagen.structuregen.ChangedToken;
import testdatagen.structuregen.ChangedTokens;
import tree.object.FunctionStyleMacroDefinitionNode;
import tree.object.IFunctionNode;
import tree.object.MacroDefinitionNode;
import tree.object.PreprocessorMacroDefinitionNode;
import utils.Utils;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

/**
 * Convert macro-code in the function into its corresponding values. <br/>
 * Ex:
 * <p>
 * <p>
 * <pre>
 * #define MY_DEFAULT_VALUE 10
 *
 * int SimpleMarcoTest(int x){
 * if (x == MY_DEFAULT_VALUE)
 * return 1;
 * else
 * return 0;
 * }
 * </pre>
 * <p>
 * is converted into
 * <p>
 * <p>
 * <pre>
 * #define MY_DEFAULT_VALUE 10
 *
 * int SimpleMarcoTest(int x){
 * if (x == <b>10</b>)
 * return 1;
 * else
 * return 0;
 * }
 * </pre>
 *
 * @author ducanhnguyen
 */
@Deprecated
public class MacroNormalizer extends AbstractFunctionNormalizer implements IFunctionNormalizer {

    public MacroNormalizer() {
    }

    public MacroNormalizer(IFunctionNode functionNode) {
        this.functionNode = functionNode;
    }

    public static void main(String[] args) throws Exception {
        ProjectParser parser = new ProjectParser(new File(Paths.TSDV_R1_4));
        IFunctionNode function = (IFunctionNode) Search
                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "psi_values(int*,double*,double*)")
                .get(0);

        System.out.println("before:" + function.getAST().getRawSignature());
        MacroNormalizer normalizer = new MacroNormalizer();
        normalizer.setFunctionNode(function);
        normalizer.normalize();

        System.out.println("changed tokens:" + normalizer.getTokens());
        System.out.println("normalized:" + normalizer.getNormalizedSourcecode());
    }

    @Override
    public void normalize() {
        PreprocessorParser preprocessor = new PreprocessorParser(functionNode);

        try {
            List<PreprocessorMacroDefinitionNode> macros = preprocessor.getAllPreprocessors();
            String content = functionNode.getAST().getRawSignature();
            for (PreprocessorMacroDefinitionNode macro : macros)
                if (macro instanceof MacroDefinitionNode)
                    content = parseMacroDefinition(tokens, macro, content);
                else if (macro instanceof FunctionStyleMacroDefinitionNode)
                    content = parseFunctionStyleMacroDefinition(tokens, macro, content);
            normalizeSourcecode = content;
        } catch (Exception e) {
            e.printStackTrace();
            normalizeSourcecode = "";
        }
    }

    /**
     * Ex: #define ADD(a, b) ((a) + (b))
     *
     * @param changedTokens
     * @param macro
     * @param content
     * @return
     */
    private String parseFunctionStyleMacroDefinition(ChangedTokens changedTokens, PreprocessorMacroDefinitionNode macro,
                                                     String content) {
        String normalizedContent = content;
        if (macro instanceof FunctionStyleMacroDefinitionNode) {
            FunctionStyleMacroDefinitionNode functionMacro = (FunctionStyleMacroDefinitionNode) macro;
            functionMacro.getAST();

        }
        return normalizedContent;
    }

    /**
     * Ex: #define MY_DEFAULT_VALUE 10
     *
     * @param changedTokens
     * @param macro
     * @param content
     * @return
     */
    private String parseMacroDefinition(ChangedTokens changedTokens, PreprocessorMacroDefinitionNode macro,
                                        String content) {
        String normalizedContent = content;
        if (macro instanceof MacroDefinitionNode) {
            MacroDefinitionNode castMacro = (MacroDefinitionNode) macro;

            String oldValueRegex = "\\b" + castMacro.getNewType() + "\\b";
            if (Utils.containRegex(normalizedContent, oldValueRegex)) {
                String newValue = castMacro.getValue();
                String oldValue = castMacro.getNewType();
                changedTokens.add(new ChangedToken(newValue, oldValue));

                normalizedContent = normalizedContent.replaceAll(oldValueRegex, castMacro.getValue());
            }
        }
        return normalizedContent;
    }
}
