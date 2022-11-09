package tree.object;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

public class HeaderNode extends SourcecodeFileNode<IASTTranslationUnit> {
    public static final String HEADER_SIGNALS = ".h";

    public HeaderNode() {
        try {
            Icon ICON_HEADER = new ImageIcon(Node.class.getResource("/image/node/HeaderNode.png"));
            setIcon(ICON_HEADER);
        } catch (Exception e) {
        }
    }

    @Override
    public File getFile() {
        return new File(getAbsolutePath());
    }
}
