package tree.object;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;

public class ClassNode extends StructureNode implements ISourceNavigable {

	public ClassNode() {
		try {
			Icon ICON_CLASS = new ImageIcon(Node.class.getResource("/image/node/ClassNode.png"));
			setIcon(ICON_CLASS);
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

	public IASTCompositeTypeSpecifier getSpecifiedAST() {
		return (IASTCompositeTypeSpecifier) AST;
	}

	@Override
	public String toString() {
		return getAST().getRawSignature();
	}



}
