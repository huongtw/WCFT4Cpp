package testdatagen.se.normalstatementparser;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerClause;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFieldReference;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIdExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTUnaryExpression;

import testdatagen.se.memory.BasicSymbolicVariable;
import testdatagen.se.memory.ISymbolicVariable;
import testdatagen.se.memory.SimpleStructureSymbolicVariable;
import testdatagen.se.memory.VariableNodeTable;
import utils.ASTUtils;
import utils.Utils;

/**
 * Parse normal binary assignment
 *
 * @author ducanhnguyen
 */
public class NormalBinaryAssignmentParser extends BinaryAssignmentParser {
	@Override
	public void parse(IASTNode ast, VariableNodeTable table) throws Exception {
		ast = Utils.shortenAstNode(ast);

		if (ast instanceof IASTBinaryExpression) {

			IASTNode left = ((IASTBinaryExpression) ast).getOperand1();
			left = Utils.shortenAstNode(left);

			/*
			 * If the left expression is an array item. VD: p1[0]
			 */
			if (left instanceof ICPPASTArraySubscriptExpression)
				new ArrayItemToExpressionParser().parse(ast, table);
			else
			/*
			 * If the left expression is name of variable
			 */
			if (left instanceof CPPASTIdExpression) {
				IASTNode right = ((IASTBinaryExpression) ast).getOperand2();

				if (right != null) {
					/*
					 * Ex: a = 2
					 */
					right = Utils.shortenAstNode(right);
					IASTNode newAst = ASTUtils.convertToIAST(left.getRawSignature() + " = " + right.getRawSignature());

					new NormalVariableToExpressionParser().parse(newAst, table);
				} else {
					/*
					 * Ex: fx_vec[N_MAX] = { -0.5772156649015329E+00, -0.4237549404110768E+00,
					 * -0.2890398965921883E+00, -0.1691908888667997E+00, -0.6138454458511615E-01,
					 * 0.3648997397857652E-01, 0.1260474527734763E+00, 0.2085478748734940E+00,
					 * 0.2849914332938615E+00, 0.3561841611640597E+00, 0.4227843350984671E+00 };
					 */
					IASTInitializerClause initialization = ((IASTBinaryExpression) ast).getInitOperand2();
					if (initialization != null)
						for (int i = 0; i < initialization.getChildren().length; i++) {
							IASTNode child = initialization.getChildren()[i];
							if (child instanceof ICPPASTInitializerClause) {
								String newAssignment = left.getRawSignature() + "[" + i + "]" + "="
										+ child.getRawSignature() + ";";

								new ArrayItemToExpressionParser().parse(ASTUtils.convertToIAST(newAssignment), table);
							}
						}
				}
			} else
			/*
			 * If the left expression is pointer
			 */
			if (left instanceof CPPASTUnaryExpression)
				new PointerItemToExpressionParser().parse(ast, table);
			else
			/*
			 * If the left expression is field reference, e.g., sv.age
			 */
			if (left instanceof CPPASTFieldReference) {
				IASTNode right = ((IASTBinaryExpression) ast).getOperand2();

				CPPASTFieldReference cast = (CPPASTFieldReference) left;
				IASTNode[] children = cast.getChildren();
				if (children.length == 2) {
					// Ex: a.b
					ISymbolicVariable var = table.findorCreateVariableByName(children[0].getRawSignature());
					if (var != null && var instanceof SimpleStructureSymbolicVariable) {
						SimpleStructureSymbolicVariable structureVar = (SimpleStructureSymbolicVariable) var;

						for (ISymbolicVariable attribute : structureVar.getAttributes())
							if (attribute.getName().equals(children[1].getRawSignature())) {
								if (attribute instanceof BasicSymbolicVariable) {
									((BasicSymbolicVariable) attribute).setValue(right.getRawSignature());
								} else {
									// TODO: support later
								}
								break;
							}
					}
				} else {
					// Ex: a.b.c
					// TODO: support later
				}
			} else
				throw new Exception("Dont support " + ast.getRawSignature());
		}
	}

}
