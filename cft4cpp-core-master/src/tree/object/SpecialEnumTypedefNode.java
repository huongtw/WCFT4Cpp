package tree.object;

import java.io.File;

/**
 * Created by DucToan on 14/07/2017.
 */

public class SpecialEnumTypedefNode extends EnumNode {
    public SpecialEnumTypedefNode() {
        super();
    }

    @Override
    public String getNewType(){
        return getAST().getDeclarators()[0].getName().getRawSignature();
    }

    @Override
    public String getAbsolutePath() {
        return getParent().getAbsolutePath() + File.separator + getNewType();
    }
}
