package testdatagen.se.normalstatementparser;

import org.eclipse.cdt.core.dom.ast.IASTNode;

import testdatagen.se.memory.VariableNodeTable;

/**
 * The top abstract class used to parse statement
 *
 * @author ducanhnguyen
 */
public abstract class StatementParser {
    /**
     * Parse the statement
     *
     * @param ast   the AST of the statement
     * @param table table of variables
     * @throws Exception
     */
    public abstract void parse(IASTNode ast, VariableNodeTable table) throws Exception;
}
