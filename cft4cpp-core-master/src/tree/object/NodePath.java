package tree.object;

import java.io.File;

public class NodePath implements INodePath {

    private File absolutePath;

    public NodePath(File path) {
        setPath(path);
    }

    @Override
    public File getPath() {
        return absolutePath;
    }

    @Override
    public void setPath(File absolutePath) {
        this.absolutePath = absolutePath;
    }
}
