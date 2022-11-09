package tree.object;

import java.io.File;

import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;

/**
 * For example, <br/>
 * <p>
 * <p>
 * 
 * <pre>
 * union RGBA{
 * int color;
 * int aliasColor;
 * };
 * </pre>
 *
 * @author ducanhnguyen
 */
public class UnionNode extends StructureNode implements ISourceNavigable {

	public UnionNode() {
		// try {
		// Icon ICON_UNION = new
		// ImageIcon(Node.class.getResource("/image/node/UnionNode.png"));
		// this.setIcon(ICON_UNION);
		// } catch (Exception e) {
		// }
	}

	@Override
	public String getNewType() {
		String name = ((IASTCompositeTypeSpecifier) getAST().getDeclSpecifier()).getName().toString();
		/*
		 * Ex: union RGB
		 * 
		 * 
		 * Delete union keywork in name
		 */
		name = name.replaceAll("^union\\s*", "");
		return name;
	}

	@Override
	public IASTFileLocation getNodeLocation() {
		return ((IASTCompositeTypeSpecifier) getAST().getDeclSpecifier()).getName().getFileLocation();
	}

	@Override
	public File getSourceFile() {
		return new File(getAST().getContainingFilename());
	}

	public IASTCompositeTypeSpecifier getSpecifiedAST() {
		return (IASTCompositeTypeSpecifier) AST;
	}

	@Override
	public String toString() {
		return getAST().getRawSignature();
	}

	@Override
	@Deprecated
	public INode findAttributeByName(String name) {
		return null;
	}

}
