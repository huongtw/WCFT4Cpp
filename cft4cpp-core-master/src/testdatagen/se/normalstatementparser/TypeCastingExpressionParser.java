package testdatagen.se.normalstatementparser;

import org.eclipse.cdt.core.dom.ast.IASTNode;

import testdatagen.se.memory.VariableNodeTable;

/**
 * Ex: c = (char) x
 *
 * @author ducanhnguyen
 */
public class TypeCastingExpressionParser extends NormalBinaryAssignmentParser {
    @Override
    public void parse(IASTNode ast, VariableNodeTable table) throws Exception {

        super.parse(ast, table);
    }
}
