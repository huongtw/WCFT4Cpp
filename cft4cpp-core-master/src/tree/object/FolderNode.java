package tree.object;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class FolderNode extends Node implements IHasFileNode {

    public FolderNode() {
        try {
            Icon ICON_FOLDER = new ImageIcon(Node.class.getResource("/image/node/FolderNode.png"));
            setIcon(ICON_FOLDER);
        } catch (Exception e) {

        }
    }

    @Override
    public File getFile() {
        return new File(getAbsolutePath());
    }
}
