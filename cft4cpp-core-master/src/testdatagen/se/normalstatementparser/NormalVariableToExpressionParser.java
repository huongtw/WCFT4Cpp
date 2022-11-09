package testdatagen.se.normalstatementparser;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUnaryExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTBinaryExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCastExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIdExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTLiteralExpression;

import testdatagen.se.ExpressionRewriterUtils;
import testdatagen.se.memory.BasicSymbolicVariable;
import testdatagen.se.memory.ISymbolicVariable;
import testdatagen.se.memory.IVariableNodeTable;
import testdatagen.se.memory.LogicBlock;
import testdatagen.se.memory.OneDimensionSymbolicVariable;
import testdatagen.se.memory.OneLevelSymbolicVariable;
import testdatagen.se.memory.Reference;
import testdatagen.se.memory.TwoLevelSymbolicVariable;
import testdatagen.se.memory.VariableNodeTable;
import utils.ASTUtils;
import utils.Utils;

/**
 * Assign a variable that its name is normal (e.g., x, y, z) to an expression.
 *
 * @author ducanhnguyen
 */
public class NormalVariableToExpressionParser extends NormalBinaryAssignmentParser {

	@Override
	public void parse(IASTNode ast, VariableNodeTable table) throws Exception {
		ast = Utils.shortenAstNode(ast);

		if (ast instanceof ICPPASTBinaryExpression) {
			IASTNode leftAST = ((ICPPASTBinaryExpression) ast).getOperand1();
			ISymbolicVariable left = table.findorCreateVariableByName(leftAST.getRawSignature());

			IASTNode rightAST = ((ICPPASTBinaryExpression) ast).getOperand2();
			if (left instanceof BasicSymbolicVariable) {
				/*
				 * In case right is type casting, e.g., (char) x
				 */
				if (rightAST instanceof CPPASTCastExpression)
					new NormalVariableToTypeCastingParser().parse(ast, table);
				else
				/*
				 * The right expression is a condition. Ex: x = a>b
				 */
				if (rightAST instanceof CPPASTBinaryExpression && Utils.isCondition(rightAST.getRawSignature()))
					throw new Exception("Dont support " + ast.getRawSignature());
				else {
					rightAST = Utils.shortenAstNode(rightAST);

					String reducedRight = ExpressionRewriterUtils.rewrite(table, rightAST.getRawSignature());
					BasicSymbolicVariable leftVar = (BasicSymbolicVariable) left;
					leftVar.setValue(reducedRight);
				}

			} else if (left instanceof OneLevelSymbolicVariable) {

				rightAST = Utils.shortenAstNode(rightAST);

				assignOneLevelSymbolicVariableToExpression(leftAST, rightAST, (OneLevelSymbolicVariable) left, table);

			} else if (left instanceof TwoLevelSymbolicVariable)
				throw new Exception("Dont support " + ast.getRawSignature());
		} else
			throw new Exception("Dont support " + ast.getRawSignature());

	}

	/**
	 * Consider expression below (p1 is a pointer): <br/>
	 * Ex1: p1 = &numbers[2]<br/>
	 * Ex2: p1 = &a<br/>
	 * Ex3: p1 = NULL<br/>
	 *
	 * @param astLeft
	 * @param astRight
	 * @param leftVar
	 * @param table
	 * @throws Exception
	 */
	private void assignOneLevelSymbolicVariableToExpression(IASTNode astLeft, IASTNode astRight,
			OneLevelSymbolicVariable leftVar, IVariableNodeTable table) throws Exception {
		String reducedRight = astRight.getRawSignature();
		/*
		 * Ex1: p1 = &numbers[2]
		 * 
		 * Ex2: p1 = &a
		 * 
		 * Ex3: p1 = NULL
		 */
		if (astRight instanceof ICPPASTUnaryExpression && reducedRight.startsWith(AssignmentParser.ADDRESS_OPERATOR)) {
			IASTNode shortenRight = Utils.shortenAstNode(astRight.getChildren()[0]);

			/*
			 * Ex: numbers[2]
			 */
			if (shortenRight instanceof ICPPASTArraySubscriptExpression) {
				CPPASTIdExpression nameVar = (CPPASTIdExpression) shortenRight.getChildren()[0];
				CPPASTLiteralExpression index = (CPPASTLiteralExpression) shortenRight.getChildren()[1];

				String newRight = nameVar + "+" + index;
				assignPointerToPointer(ASTUtils.convertToIAST(newRight), leftVar, table);

			} else
			/*
			 * Ex: a
			 */
			if (shortenRight instanceof CPPASTIdExpression) {
				String shortenRightInStr = shortenRight.getRawSignature();

				ISymbolicVariable ref = table.findorCreateVariableByName(shortenRightInStr);

				if (ref instanceof BasicSymbolicVariable) {
					LogicBlock vituralLeftBlock = new LogicBlock(
							ISymbolicVariable.PREFIX_SYMBOLIC_VALUE + ref.getName());
					vituralLeftBlock.addLogicalCell(((BasicSymbolicVariable) ref).getCell(), LogicBlock.FIRST_CELL);
					leftVar.setReference(new Reference(vituralLeftBlock, Reference.FIRST_START_INDEX));

				} else
					throw new Exception(
							"Dont support " + astLeft.getRawSignature() + " = " + astRight.getRawSignature());
			}
			/*
			 * Ex: NULL
			 */
			else if (reducedRight.equals("NULL"))
				assignPointerToNull(leftVar);
			else
				throw new Exception("Dont support " + astLeft.getRawSignature() + " = " + astRight.getRawSignature());
		} else
			/*
			 * In this case, the right expression may be an expression of pointer. Ex1:
			 * p2+1. Ex2: p2-1
			 */
			assignPointerToPointer(astRight, leftVar, table);
	}

	private void assignPointerToNull(Object left) {
		((Reference) left).setBlock(null);
		((Reference) left).setStartIndex(Reference.UNDEFINED_INDEX);
	}

	/**
	 * Parse statement that change the reference of a pointer <br/>
	 * Ex1: p1 = p2<br/>
	 * Ex2: p1 = p2 + 1<br/>
	 * Ex3: p1 = p2 - 1<br/>
	 *
	 * @param right
	 * @param left
	 * @param table
	 * @throws Exception
	 */
	private void assignPointerToPointer(IASTNode right, OneLevelSymbolicVariable left, IVariableNodeTable table)
			throws Exception {

		String index = Reference.FIRST_START_INDEX;
		String nameRightPointer = "";

		// Assign pointer to pointer directly
		// Ex1: p1=student.age
		// Ex2: p1 = student[0]
		// Ex3: p1 = p2
		if (right instanceof ICPPASTFieldReference // Ex1
				|| right instanceof ICPPASTArraySubscriptExpression // Ex2
				|| right instanceof CPPASTIdExpression// Ex3
		) {
			ISymbolicVariable rightVar = table.findorCreateVariableByName(right.getRawSignature());

			if (rightVar != null)
				if (rightVar instanceof OneLevelSymbolicVariable) {
					/*
					 * p = numbers+3; (p: pointer, numbers: one-dimension array)
					 */
					Reference r = ((OneLevelSymbolicVariable) rightVar).getReference();

					if (r != null) {
						left.getReference().setBlock(r.getBlock());
						left.getReference().setStartIndex(r.getStartIndex());
					}

				} else if (rightVar instanceof OneDimensionSymbolicVariable) {
					LogicBlock b = ((OneDimensionSymbolicVariable) rightVar).getBlock();

					if (b != null) {
						left.getReference().setBlock(b);
						left.getReference().setStartIndex(index);
					}
				} else
					throw new Exception("Dont support " + left + " = " + right);

		} else if (right instanceof ICPPASTExpression) {
			int location = -1;
			if (right.getRawSignature().contains("+"))
				location = right.getRawSignature().indexOf("+");
			else if (right.getRawSignature().contains("-"))
				location = right.getRawSignature().indexOf("-");

			if (location >= 0) {
				index = right.getRawSignature().substring(location + 1);
				nameRightPointer = right.getRawSignature().substring(0, location);

				/*
				 * If name of pointer put in pair of brackets
				 */
				nameRightPointer = nameRightPointer.replace("(", "").replace(")", "");
				ISymbolicVariable rightVar = table.findorCreateVariableByName(nameRightPointer);

				if (rightVar != null)
					if (rightVar instanceof OneLevelSymbolicVariable) {
						/*
						 * p = numbers+3; (p: pointer, numbers: one-dimension array)
						 */
						Reference r = ((OneLevelSymbolicVariable) rightVar).getReference();

						if (r != null) {
							left.getReference().setBlock(r.getBlock());
							left.getReference().setStartIndex(r.getStartIndex() + "+ (" + index + ")");
						}

					} else if (rightVar instanceof OneDimensionSymbolicVariable) {
						LogicBlock b = ((OneDimensionSymbolicVariable) rightVar).getBlock();

						if (b != null) {
							left.getReference().setBlock(b);
							left.getReference().setStartIndex(index);
						}
					} else
						throw new Exception("Dont support " + left + " = " + right);
			}
		}
	}
}
