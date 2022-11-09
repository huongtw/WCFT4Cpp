package tree.object;

import java.io.File;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

public interface ISourcecodeFileNode<N extends IASTTranslationUnit> extends INode {

    N getAST();

    void setAST(N aST);

    @Override
    String toString();

    File getFile();

}