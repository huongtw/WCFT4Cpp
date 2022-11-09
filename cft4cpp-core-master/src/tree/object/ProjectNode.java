package tree.object;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class ProjectNode extends Node implements IProjectNode, IHasFileNode {

    public ProjectNode() {
        try {
            Icon ICON_PROJECT = new ImageIcon(Node.class.getResource("/image/node/FolderNode.png"));

            if (ICON_PROJECT != null)
                setIcon(ICON_PROJECT);
        } catch (Exception e) {

        }
    }

    @Override
    public File getFile() {
        return new File(getAbsolutePath());
    }
}
