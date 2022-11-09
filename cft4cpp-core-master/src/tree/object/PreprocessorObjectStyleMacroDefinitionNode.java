package tree.object;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorObjectStyleMacroDefinition;

/**
 * Represent constant-macro definitions. The macro may define in single line or
 * multiple lines. <br/>
 * <p>
 * <p>
 * Ex1: <b>#define MIN 100</b>
 *
 * @author ducanhnguyen
 */
public class PreprocessorObjectStyleMacroDefinitionNode
        extends PreprocessorMacroDefinitionNode<IASTPreprocessorObjectStyleMacroDefinition> {
    public String getValue() {
        return getAST().getExpansion();
    }

    @Override
    public String getNewType() {
        return getAST().getName().getRawSignature();
    }

    @Override
    public IASTPreprocessorObjectStyleMacroDefinition getAST() {
        return AST;
    }

    @Override
    public String toString() {
        return "(" + getNewType() + ", " + getValue() + ")";
    }
}
