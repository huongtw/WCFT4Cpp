package testdatagen.se.normalstatementparser;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTDeclarationStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTUsingDirective;

import testdatagen.se.memory.VariableNodeTable;

/**
 * Parse "using namespace xxx"
 *
 * @author ducanhnguyen
 */
public class UsingNamespaceParser extends StatementParser {

    @Override
    public void parse(IASTNode ast, VariableNodeTable table) throws Exception {
        if (ast instanceof CPPASTDeclarationStatement) {
            IASTNode firstChild = ast.getChildren()[0];
            if (firstChild instanceof CPPASTUsingDirective) {
                IASTNode nameSpace = firstChild.getChildren()[0];
                table.setCurrentNameSpace(nameSpace.getRawSignature());
            }
        }
    }

}
