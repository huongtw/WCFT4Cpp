package tree.object;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;

/**
 * Represent normal struct node
 * <p>
 * Ex:
 * <p>
 * <p>
 * <pre>
 * struct SinhVien{
 * 			int age;
 * }
 * </pre>
 *
 * @author ducanhnguyen
 */
public class StructNode extends StructureNode implements ISourceNavigable {

    public StructNode() {
        try {
            Icon ICON_STRUCT = new ImageIcon(Node.class.getResource("/image/node/StructNode.png"));
            setIcon(ICON_STRUCT);
        } catch (Exception e) {
        }

    }

    @Override
    public String getNewType() {
        return ((IASTCompositeTypeSpecifier) getAST().getDeclSpecifier()).getName().toString();
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
}
