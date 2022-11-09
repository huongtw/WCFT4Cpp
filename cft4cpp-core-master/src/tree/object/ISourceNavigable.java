package tree.object;

import java.io.File;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;

public interface ISourceNavigable {

    IASTFileLocation getNodeLocation();

    File getSourceFile();
}
