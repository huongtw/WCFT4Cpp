package testdatagen.se.normalstatementparser;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;

import testdatagen.se.ExpressionRewriterUtils;
import testdatagen.se.memory.ArraySymbolicVariable;
import testdatagen.se.memory.ISymbolicVariable;
import testdatagen.se.memory.LogicBlock;
import testdatagen.se.memory.PhysicalCell;
import testdatagen.se.memory.PointerSymbolicVariable;
import testdatagen.se.memory.VariableNodeTable;
import utils.Utils;

/**
 * Assign an array element to an expression <br/>
 * Ex: a[1]=2+x
 *
 * @author ducanhnguyen
 */
public class ArrayItemToExpressionParser extends NormalBinaryAssignmentParser {
    @Override
    public void parse(IASTNode ast, VariableNodeTable table) throws Exception {
        ast = Utils.shortenAstNode(ast);
        if (ast instanceof IASTBinaryExpression) {
            IASTNode leftAST = ((IASTBinaryExpression) ast).getOperand1();
            IASTNode rightAST = ((IASTBinaryExpression) ast).getOperand2();

            if (rightAST instanceof IASTExpression && leftAST instanceof IASTExpression) {
                String reducedRightExpression = ExpressionRewriterUtils.rewrite(table, rightAST.getRawSignature());
                String name = Utils.getNameVariable(leftAST.getRawSignature());
                ISymbolicVariable ref = table.findorCreateVariableByName(name);

				/*
                 * Get the block that contains data of variable
				 */
                LogicBlock block = null;
                if (ref instanceof ArraySymbolicVariable)
                    block = ((ArraySymbolicVariable) ref).getBlock();

                else if (ref instanceof PointerSymbolicVariable)
                    block = ((PointerSymbolicVariable) ref).getReference().getBlock();

                if (block != null) {
					/*
					 * Get the reduce index of array item
					 */
                    String index = Utils.getReducedIndex(leftAST.getRawSignature(), table);

					/*
					 * If the cell exists in the block
					 */
                    if (block.findCellByIndex(index) != null)
                        block.updateCellByIndex(index, reducedRightExpression);
                    else
					/*
					 * If the cell does not in the block, a new cell is added to
					 * the block
					 */ {
                        PhysicalCell newCell = new PhysicalCell(reducedRightExpression);
                        block.addLogicalCell(newCell, index);
                    }
                }
            } else
                throw new Exception("Dont support " + ast.getRawSignature());
        } else
            throw new Exception("Dont support " + ast.getRawSignature());
    }
}
