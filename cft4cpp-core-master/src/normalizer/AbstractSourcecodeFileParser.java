package normalizer;

import java.io.File;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import tree.object.ISourcecodeFileNode;
import tree.object.SourcecodeFileNode;

/**
 * Abstract class for parsing source code file, e.g., *.c, *.cpp, *.h
 *
 * @author ducanhnguyen
 */
public abstract class AbstractSourcecodeFileParser extends AbstractParser {
    protected SourcecodeFileNode sourcecodeNode;

    protected IASTTranslationUnit translationUnit;

    public IASTTranslationUnit getTranslationUnit() {
        return translationUnit;
    }

    public void setTranslationUnit(IASTTranslationUnit translationUnit) {
        this.translationUnit = translationUnit;
    }

    public File getSourcecodeFile() {
        return new File(sourcecodeNode.getAbsolutePath());
    }

    public void setSourcecodeFile(File sourcecodeFile) {
        // nothing to do
    }

    public ISourcecodeFileNode getSourcecodeNode() {
        return sourcecodeNode;
    }

    public void setSourcecodeNode(SourcecodeFileNode sourcecodeNode) {
        this.sourcecodeNode = sourcecodeNode;
    }
}
