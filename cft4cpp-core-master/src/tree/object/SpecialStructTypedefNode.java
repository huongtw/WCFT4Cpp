package tree.object;

import java.io.File;

import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;

/**
 * Ex:
 * <p>
 * <p>
 * <pre>
 * typedef struct XXXX {
 * int x;
 * } MyStruct1;
 * </pre>
 * <p>
 * Represent XXXX instead of MyStruct1
 *
 * @author ducanhnguyen
 */
public class SpecialStructTypedefNode extends StructNode {

    public SpecialStructTypedefNode() {
        super();
    }

    @Override
    public String getNewType() {
        return getAST().getChildren()[0].getChildren()[0].getRawSignature();
    }

    @Override
    public IASTFileLocation getNodeLocation() {
        return ((IASTCompositeTypeSpecifier) getAST().getDeclSpecifier()).getName().getFileLocation();
    }

    @Override
    public File getSourceFile() {
        return new File(getAST().getContainingFilename());
    }

    @Override
    public String toString() {
        return /* "struct " + */ super.toString();
    }

    @Override
    public String getAbsolutePath() {
        return getParent().getAbsolutePath() + File.separator + getNewType();
    }

}
