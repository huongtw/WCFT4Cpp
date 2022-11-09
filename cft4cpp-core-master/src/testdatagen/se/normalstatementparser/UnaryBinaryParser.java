package testdatagen.se.normalstatementparser;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTArraySubscriptExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIdExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTUnaryExpression;

import testdatagen.se.ExpressionRewriterUtils;
import testdatagen.se.memory.ArraySymbolicVariable;
import testdatagen.se.memory.BasicSymbolicVariable;
import testdatagen.se.memory.ISymbolicVariable;
import testdatagen.se.memory.IVariableNodeTable;
import testdatagen.se.memory.PhysicalCell;
import testdatagen.se.memory.PointerSymbolicVariable;
import testdatagen.se.memory.VariableNodeTable;
import utils.ASTUtils;
import utils.Utils;

/**
 * Parse unary assignment statement. Ex: ++exp;
 *
 * @author ducanhnguyen
 */
public class UnaryBinaryParser extends AssignmentParser {
    @Override
    public void parse(IASTNode ast, VariableNodeTable table) throws Exception {
        ast = Utils.shortenAstNode(ast);

        if (ast instanceof IASTUnaryExpression) {

            IASTUnaryExpression unaryStm = (IASTUnaryExpression) ast;

			/*
             * In an unary expression, it exists only one IdExpression
			 */
            List<CPPASTIdExpression> ids = Utils.getIds(ast);
            CPPASTIdExpression firstId = ids.get(0);
            String nameVariable = firstId.getRawSignature();
            ISymbolicVariable var = table.findorCreateVariableByName(nameVariable);

            if (var instanceof BasicSymbolicVariable)
                updateValueforBasicVariable((BasicSymbolicVariable) var, unaryStm);

            else if (var instanceof ArraySymbolicVariable)
                updateValueforArrayVariable((ArraySymbolicVariable) var, table, unaryStm);

            else if (var instanceof PointerSymbolicVariable)
                updateValueforPointerVariable((PointerSymbolicVariable) var, unaryStm, table);
        } else
            throw new Exception("Dont match input");
    }

    /**
     * Update value of cell
     *
     * @param cell
     * @param operator
     * @throws Exception
     */
    private void updateValueofCell(PhysicalCell cell, int operator) throws Exception {
        switch (operator) {
            case IASTUnaryExpression.op_prefixIncr:
            case IASTUnaryExpression.op_postFixIncr: // ++exp
                final String INCREASE_ONE = "+ 1";
                cell.setValue("(" + cell.getValue() + ")" + INCREASE_ONE);
                break;

            case IASTUnaryExpression.op_prefixDecr:
            case IASTUnaryExpression.op_postFixDecr:// --exp
                final String DECREASE_ONE = "- 1";
                cell.setValue("(" + cell.getValue() + ")" + DECREASE_ONE);
                break;

            default:
                throw new Exception("Dont support");
        }
    }

    private void updateValueforBasicVariable(BasicSymbolicVariable var, IASTUnaryExpression unaryStm) throws Exception {
        PhysicalCell cell = var.getCell();
        updateValueofCell(cell, unaryStm.getOperator());
    }

    private void updateValueforArrayVariable(ArraySymbolicVariable var, IVariableNodeTable table,
                                             IASTUnaryExpression unaryStm) throws Exception {
        IASTExpression nameVar = unaryStm.getOperand();
        PhysicalCell cell = table.findPhysicalCellByName(nameVar.getRawSignature());
		/*
		 * In case the cell exists before
		 */
        if (cell != null)
            updateValueofCell(cell, unaryStm.getOperator());
		/*
		 * The cell does not exist, we create new cell
		 */
        {
            String value = ExpressionRewriterUtils.rewrite(table, nameVar.getRawSignature());
            PhysicalCell newCell = new PhysicalCell(value);
            updateValueofCell(newCell, unaryStm.getOperator());

            String newIndex = VariableNodeTable.getReducedIndex(nameVar.getRawSignature(), table);

            var.getBlock().addLogicalCell(newCell, newIndex);
        }
    }

    private void updateValueforPointerVariable(PointerSymbolicVariable var, IASTUnaryExpression unaryStm,
                                               VariableNodeTable table) throws Exception {
        IASTNode operand = Utils.shortenAstNode(unaryStm.getOperand());

		/*
		 * Ex1: (p1)++
		 * 
		 * Ex2: (*p1)++
		 */
        if (operand instanceof CPPASTUnaryExpression) {
            if (operand instanceof CPPASTIdExpression)
                // Ex1
                updateStartIndexofPointer(var, unaryStm.getOperator());
            else if (operand instanceof CPPASTUnaryExpression) {
				/*
				 * Transform the expression into binary expression
				 */
                String newExpression = operand.getRawSignature();

                switch (((CPPASTUnaryExpression) unaryStm).getOperator()) {
                    case IASTUnaryExpression.op_prefixIncr:// ++p1
                    case IASTUnaryExpression.op_postFixIncr: // p1++
                        newExpression = newExpression + "= " + newExpression + " + 1";
                        break;

                    case IASTUnaryExpression.op_prefixDecr:// --p1
                    case IASTUnaryExpression.op_postFixDecr:// p1--
                        newExpression = newExpression + "= " + newExpression + " - 1";
                        break;
                }

                IASTNode newAST = ASTUtils.convertToIAST(newExpression);

                new BinaryAssignmentParser().parse(Utils.shortenAstNode(newAST), table);
            }

        } else
		/*
		 * Ex: p1++
		 */
            if (operand instanceof CPPASTIdExpression)
                updateStartIndexofPointer(var, unaryStm.getOperator());
            else
		/*
		 * Ex: p1[0][0]++
		 */
                if (operand instanceof CPPASTArraySubscriptExpression) {

			/*
			 * Transform the expression into binary expression
			 */
                    String newExpression = operand.getRawSignature();

                    switch (((CPPASTUnaryExpression) unaryStm).getOperator()) {
                        case IASTUnaryExpression.op_prefixIncr:// ++p1
                        case IASTUnaryExpression.op_postFixIncr: // p1++
                            newExpression = newExpression + "= " + newExpression + " + 1";
                            break;

                        case IASTUnaryExpression.op_prefixDecr:// --p1
                        case IASTUnaryExpression.op_postFixDecr:// p1--
                            newExpression = newExpression + "= " + newExpression + " - 1";
                            break;
                    }

                    IASTNode newAST = ASTUtils.convertToIAST(newExpression);

                    new BinaryAssignmentParser().parse(Utils.shortenAstNode(newAST), table);

                } else
                    throw new Exception("Dont support " + unaryStm.getRawSignature());
    }

    private void updateStartIndexofPointer(PointerSymbolicVariable var, int operator) throws Exception {
        String startIndex = var.getReference().getStartIndex();
        switch (operator) {
            case IASTUnaryExpression.op_prefixIncr:// ++p1
            case IASTUnaryExpression.op_postFixIncr: // p1++
                final String INCREASE_ONE = "+ 1";
                var.getReference().setStartIndex(startIndex + INCREASE_ONE);
                break;

            case IASTUnaryExpression.op_prefixDecr:// --p1
            case IASTUnaryExpression.op_postFixDecr:// p1--
                final String DECREASE_ONE = "- 1";
                var.getReference().setStartIndex(startIndex + DECREASE_ONE);
                break;

            default:
                throw new Exception("Dont support " + var.getName());
        }
    }
}
