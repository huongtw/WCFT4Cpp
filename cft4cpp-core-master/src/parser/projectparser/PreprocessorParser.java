package parser.projectparser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorObjectStyleMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import config.Paths;
import normalizer.AbstractPreprocessorParser;
import normalizer.IPreprocessorParser;
import tree.dependency.Dependency;
import tree.dependency.IncludeHeaderDependency;
import tree.object.FunctionStyleMacroDefinitionNode;
import tree.object.IFunctionNode;
import tree.object.INode;
import tree.object.IProjectNode;
import tree.object.ISourcecodeFileNode;
import tree.object.MacroDefinitionNode;
import tree.object.PreprocessorMacroDefinitionNode;
import tree.object.SourcecodeFileNode;
import utils.Utils;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

/**
 * Get all preprocessors in the current files and the included files
 *
 * @author ducanhnguyen
 */
public class PreprocessorParser extends AbstractPreprocessorParser implements IPreprocessorParser {

    public PreprocessorParser(INode ast) {
        if (ast instanceof IFunctionNode)
            functionNode = (IFunctionNode) ast;
    }

    public static void main(String[] args) throws Exception {
        ProjectParser projectParser = new ProjectParser(new File(Paths.TSDV_R1_4), null);
        IProjectNode projectRoot = projectParser.getRootTree();

        INode functionNode = Search
                .searchNodes(projectRoot, new FunctionNodeCondition(), "psi_values(int*,double*,double*)").get(0);
        PreprocessorParser p = new PreprocessorParser(functionNode);
        System.out.println(p.getAllPreprocessors());
    }

    /**
     * Get all preprocessor macro definitions nodes of the given unit including
     * the included headers
     *
     * @return
     * @throws Exception
     */
    public List<PreprocessorMacroDefinitionNode> getAllPreprocessors() throws Exception {
        List<PreprocessorMacroDefinitionNode> macros = new ArrayList<>();
        macros.addAll(getMacrosInIncludedFiles(functionNode));
        macros.addAll(getMacrosInCurrentFile(functionNode));
        return macros;
    }

    private List<PreprocessorMacroDefinitionNode> getMacrosInIncludedFiles(INode functionNode) throws Exception {
        List<PreprocessorMacroDefinitionNode> macros = new ArrayList<>();
        List<Dependency> dependencies = Utils.getSourcecodeFile(functionNode).getDependencies();

        for (Dependency d : dependencies)
            if (d instanceof IncludeHeaderDependency) {
                INode included = d.getEndArrow();
                if (included instanceof SourcecodeFileNode) {

                    IASTTranslationUnit unit = ((ISourcecodeFileNode) included).getAST();
                    int functionLocation = ((IFunctionNode) functionNode).getAST().getFileLocation().getNodeOffset();
                    macros.addAll(getPreprocessors(unit, functionLocation));
                }
            }

        return macros;
    }

    private List<PreprocessorMacroDefinitionNode> getMacrosInCurrentFile(INode functionNode) throws Exception {
        INode currentFile = Utils.getSourcecodeFile(functionNode);
        IASTTranslationUnit unit = ((ISourcecodeFileNode) currentFile).getAST();
        return getPreprocessors(unit, ((IFunctionNode) functionNode).getAST().getFileLocation().getNodeOffset()
                + ((IFunctionNode) functionNode).getAST().getFileLocation().getNodeLength());
    }

    /**
     * Get all preprocessor macro definitions nodes of the given unit that
     * defined in this unit
     *
     * @param unit
     * @return
     * @throws Exception
     */
    private List<PreprocessorMacroDefinitionNode> getPreprocessors(IASTTranslationUnit unit, int functionLocation)
            throws Exception {
        List<PreprocessorMacroDefinitionNode> macros = new ArrayList<>();
        if (unit != null) {
            IASTPreprocessorMacroDefinition[] press = unit.getMacroDefinitions();
            for (IASTPreprocessorMacroDefinition pres : press)
                if (pres instanceof IASTPreprocessorObjectStyleMacroDefinition) {
                    IASTFileLocation location = pres.getExpansionLocation();

                    if (location.getNodeOffset() < functionLocation) {
                        PreprocessorMacroDefinitionNode macroNode = null;

                        if (pres.getClass().getSimpleName().equals("ASTMacroDefinition"))
                            macroNode = new MacroDefinitionNode();
                        else if (pres.getClass().getSimpleName().equals("ASTFunctionStyleMacroDefinition"))
                            macroNode = new FunctionStyleMacroDefinitionNode();

                        if (macroNode != null) {
                            macroNode.setAST(pres);
                            macros.add(macroNode);
                        }
                    }

                } else
                    throw new Exception("Dont support " + pres.getRawSignature());
        }

        return macros;
    }
}
