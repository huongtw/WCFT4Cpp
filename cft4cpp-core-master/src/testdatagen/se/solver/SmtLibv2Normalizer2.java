package testdatagen.se.solver;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUnaryExpression;

import normalizer.AbstractNormalizer;
import utils.ASTUtils;

/**
 *
 * @author anhanh
 */
public class SmtLibv2Normalizer2 extends SmtLibv2Normalizer {

	public SmtLibv2Normalizer2(String expression) {
		originalSourcecode = expression;
	}

	public SmtLibv2Normalizer2() {
	}

	public static void main(String[] args) {
		String[] samples = new String[] { "((x+1))!=1", "((((-((-(tvw_a)+(-1)*1+0))+0))+1+0))>0", "!(!(d->ngay==1))",
				"a!=b", "a==b", "(a)>b", "a>x[1][2]", "((tvwb_w)/((tvwhe)*(tvwhe)/10000))<19",
				"!((tvwkey)==tvwarray[(to_int*(((0)+(to_int*((tvwsize)+0)))/2+0))+0])",
				"tvwp[0+0+0][0+0+0]>=(-10)&&tvwp[0+0+0][0+0+0]<=20",
				"(to_int*(16807*((tvwseed)-(to_int*((tvwseed)/127773))*127773)-(to_int*((tvwseed)/127773))*2836))<0" };

		for (int i = 0; i < samples.length; i++) {
			System.out.println(samples[i]);
			AbstractNormalizer norm = new SmtLibv2Normalizer2();
			norm.setOriginalSourcecode(samples[i]);
			norm.normalize();
			System.out.println(norm.getNormalizedSourcecode() + "\n\n");
		}
	}

	@Override
	public void normalize() {
		IASTNode ast = ASTUtils.convertToIAST(originalSourcecode);
		normalizeSourcecode = createSmtLib(ast);

		// Ex: "(1.2)" ----------->"1.2"
		normalizeSourcecode = normalizeSourcecode.replaceAll("\\(([a-zA-Z0-9_\\.]+)\\)", "$1");
	}

	protected String createSmtLib(IASTNode ast) {
		String normalizeSc = "";
		if (ast.getRawSignature().equals(NEGATIVE_ONE)) {
			normalizeSc = "- 1";

		} else if (ast instanceof ICPPASTName || ast instanceof IASTIdExpression
				|| ast instanceof ICPPASTLiteralExpression || ast instanceof ICPPASTFieldReference) {
			normalizeSc = ast.getRawSignature();

		} else {
			// STEP 1. Shorten expression
			boolean isNegate = false;
			boolean isUnaryExpression = ast instanceof ICPPASTUnaryExpression;

			while (ast instanceof ICPPASTUnaryExpression) {
				ICPPASTUnaryExpression astUnary = (ICPPASTUnaryExpression) ast;
				switch (astUnary.getOperator()) {
				case IASTUnaryExpression.op_plus:
					ast = astUnary.getOperand();
					break;
				case IASTUnaryExpression.op_minus:
					ast = ASTUtils.convertToIAST(NEGATIVE_ONE + "*(" + astUnary.getOperand().getRawSignature() + ")");
					break;
				case IASTUnaryExpression.op_prefixIncr:
					ast = ASTUtils.convertToIAST("1+ " + astUnary.getOperand().getRawSignature());
					break;
				case IASTUnaryExpression.op_prefixDecr:
					ast = ASTUtils.convertToIAST(astUnary.getOperand().getRawSignature() + "-1");
					break;
				case IASTUnaryExpression.op_bracketedPrimary:
					ast = astUnary.getOperand();
					break;
				case IASTUnaryExpression.op_not:
					isNegate = !isNegate;
					ast = astUnary.getOperand();
					break;
				}
			}

			// STEP 2. Get operator
			String operator = "";
			if (isUnaryExpression) {
				if (isNegate) {
					operator = "not";
					normalizeSc = String.format("%s %s", operator, createSmtLib(ast));
				} else
					normalizeSc = String.format("%s", createSmtLib(ast));

			} else if (ast instanceof ICPPASTBinaryExpression) {
				ICPPASTBinaryExpression astBinary = (ICPPASTBinaryExpression) ast;
				switch (astBinary.getOperator()) {
				case IASTBinaryExpression.op_divide:
					operator = "div"; // integer division
					break;
				case IASTBinaryExpression.op_minus:
					operator = "-";
					break;
				case IASTBinaryExpression.op_plus:
					operator = "+";
					break;
				case IASTBinaryExpression.op_multiply:
					operator = "*";
					break;
				case IASTBinaryExpression.op_modulo:
					operator = "mod";
					break;

				case IASTBinaryExpression.op_greaterEqual:
					operator = ">=";
					break;
				case IASTBinaryExpression.op_greaterThan:
					operator = ">";
					break;
				case IASTBinaryExpression.op_lessEqual:
					operator = "<=";
					break;
				case IASTBinaryExpression.op_lessThan:
					operator = "<";
					break;
				case IASTBinaryExpression.op_equals:
					operator = "=";
					break;
				case IASTBinaryExpression.op_notequals:
					operator = "!=";
					break;

				case IASTBinaryExpression.op_logicalAnd:
					operator = "and";
					break;
				case IASTBinaryExpression.op_logicalOr:
					operator = "or";
					break;
				}

				if (operator.length() > 0)
					if (operator.equals("!="))
						normalizeSc = String.format("or (> %s %s) (< %s %s)",
								createSmtLib(((ICPPASTBinaryExpression) ast).getOperand1()),
								createSmtLib(((ICPPASTBinaryExpression) ast).getOperand2()),
								createSmtLib(((ICPPASTBinaryExpression) ast).getOperand1()),
								createSmtLib(((ICPPASTBinaryExpression) ast).getOperand2()));
					else
						normalizeSc = String.format("%s %s %s", operator,
								createSmtLib(((ICPPASTBinaryExpression) ast).getOperand1()),
								createSmtLib(((ICPPASTBinaryExpression) ast).getOperand2()));

			} else if (ast instanceof ICPPASTArraySubscriptExpression) {
				// Get all elements in array item
				List<IASTNode> elements = new ArrayList<>();
				while (ast.getChildren().length > 1) {
					elements.add(0, ast.getChildren()[1]);
					ast = ast.getChildren()[0];
				}
				elements.add(ast);
				//
				IASTNode astName = elements.get(elements.size() - 1);
				normalizeSc = astName.getRawSignature();

				for (int i = elements.size() - 2; i >= 0; i--)
					normalizeSc += createSmtLib(elements.get(i));
			}
		}

		normalizeSc = checkInBracket(normalizeSc) ? normalizeSc : " (" + normalizeSc + ") ";
		return normalizeSc;
	}

	private boolean checkInBracket(String stm) {
		stm = stm.trim();
		if (stm.startsWith("(")) {
			int count = 0;
			for (Character c : stm.toCharArray())
				if (c == '(')
					count++;
				else if (c == ')')
					count--;
			if (count == 0)
				return true;
			else
				return false;
		} else
			return false;

	}

	private final String NEGATIVE_ONE = "(-1)";
}
