package instrument;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTBreakStatement;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTContinueStatement;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNullStatement;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCatchHandler;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTryBlockStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIdExpression;

import config.Paths;
import parser.projectparser.ProjectParser;
import testdatagen.fastcompilation.AvoidingErrorFunctionInstrumentation;
import tree.object.IFunctionNode;
import tree.object.INode;
import utils.SpecialCharacter;
import utils.Utils;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

/**
 * Instrument function with sub-condition
 *
 * @author nartoan
 */

public class FunctionInstrumentationForSubCondition implements IFunctionInstrumentationGeneration {
	final static Logger logger = Logger.getLogger(AvoidingErrorFunctionInstrumentation.class);

	private IFunctionNode functionNode;
	private boolean normalizedMode = false;

	public FunctionInstrumentationForSubCondition(IFunctionNode fn) {
		functionNode = fn;
		normalizedMode = false;
	}

	public FunctionInstrumentationForSubCondition(IFunctionNode fn, boolean normalizedMode) {
		functionNode = fn;
		this.normalizedMode = normalizedMode;
	}

	public static void main(String[] args) {
		ProjectParser parser = new ProjectParser(new File(Paths.SYMBOLIC_EXECUTION_TEST));
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "basicTest18(bool,int)")
				.get(0);
		System.out.println(new FunctionInstrumentationForSubCondition((IFunctionNode) function, true)
				.generateInstrumentedFunction());
	}

	@Override
	public String generateInstrumentedFunction() {
		StringBuilder tempStr = new StringBuilder();
		IASTFunctionDefinition fnDef = null;

		if (!normalizedMode) {
			fnDef = functionNode.getAST();
		} else {
			try {
				fnDef = functionNode.normalizedAST().getNormalizedAST();
			} catch (Exception e) {
				e.printStackTrace();
				fnDef = functionNode.getAST();
			}
		}
		if (fnDef != null) {
			tempStr.append(ast(fnDef.getDeclSpecifier())).append(SpecialCharacter.SPACE)
					.append(ast(fnDef.getDeclarator()))
					.append(parseBlock((IASTCompoundStatement) fnDef.getBody(), null, ""));
		}
		return tempStr.toString();
	}

	private String parseBlock(IASTCompoundStatement block, String extra, String margin) {
		StringBuilder tempStr = new StringBuilder();
		String inside = margin + SpecialCharacter.TAB;

		tempStr.append(SpecialCharacter.OPEN_BRACE).append("mark(\"{\");").append(SpecialCharacter.LINE_BREAK);

		if (extra != null) {
			tempStr.append(inside);
		}

		for (IASTStatement stm : block.getStatements())
			tempStr.append(inside).append(parseStatement(stm, inside)).append(SpecialCharacter.LINE_BREAK)
					.append(SpecialCharacter.LINE_BREAK);

		tempStr.append(margin).append("mark(\"}\");").append(SpecialCharacter.CLOSE_BRACE);

		return tempStr.toString();
	}

	private String parseStatement(IASTStatement stm, String margin) {
		StringBuilder tempStr = new StringBuilder();
		if (stm instanceof IASTCompoundStatement) {
			tempStr.append(parseBlock((IASTCompoundStatement) stm, null, margin));
		} else if (stm instanceof IASTIfStatement) {
			IASTIfStatement astIf = (IASTIfStatement) stm;
			IASTStatement astElse = astIf.getElseClause();
			IASTExpression astCond = astIf.getConditionExpression();

			tempStr.append("if (").append(createMarkForSubCondition(astCond)).append(")");

			tempStr.append(addExtraCall(astIf.getThenClause(), "", margin));

			if (astElse != null) {
				tempStr.append(SpecialCharacter.LINE_BREAK).append(margin).append("else ");
				tempStr.append(addExtraCall(astElse, "", margin));
			}
		} else if (stm instanceof IASTForStatement) {
			IASTForStatement astFor = (IASTForStatement) stm;
			IASTStatement astInit = astFor.getInitializerStatement();
			IASTExpression astCond = (IASTExpression) Utils.shortenAstNode(astFor.getConditionExpression());
			IASTExpression astIter = astFor.getIterationExpression();

			if (!(astInit instanceof IASTNullStatement))
				tempStr.append(mark(esc(ast(astInit)), true, true)).append(SpecialCharacter.LINE_BREAK).append(margin);
			tempStr.append("for (").append(ast(astInit));

			if (astCond != null)
				tempStr.append(createMarkForSubCondition(astCond));
			tempStr.append("; ");

			if (astIter != null)
				tempStr.append(mark(esc(ast(astIter)), false, true)).append(',');
			tempStr.append(ast(astIter)).append(") ");

			// Block for does not have condition, e.g., for (int i=0;;)
			if (astCond == null)
				tempStr.append(parseStatement(astFor.getBody(), margin));
			else
				tempStr.append(addExtraCall(astFor.getBody(), "", margin));

		} else if (stm instanceof IASTWhileStatement) {
			IASTWhileStatement astWhile = (IASTWhileStatement) stm;
			IASTExpression astCond = (IASTExpression) Utils.shortenAstNode(astWhile.getCondition());

			tempStr.append("while (").append(createMarkForSubCondition(astCond)).append(")");

			tempStr.append(addExtraCall(astWhile.getBody(), "", margin));

		} else if (stm instanceof IASTDoStatement) {
			IASTDoStatement astDo = (IASTDoStatement) stm;
			IASTExpression astCond = (IASTExpression) Utils.shortenAstNode(astDo.getCondition());

			tempStr.append("do ").append(addExtraCall(astDo.getBody(), "", margin)).append(SpecialCharacter.LINE_BREAK)
					.append(margin).append("while (").append(createMarkForSubCondition(astCond)).append(");");

		} else if (stm instanceof ICPPASTTryBlockStatement) {
			ICPPASTTryBlockStatement astTry = (ICPPASTTryBlockStatement) stm;

			String extra = "start try";
			tempStr.append(mark(extra, true, true));

			tempStr.append(SpecialCharacter.LINE_BREAK).append(margin).append("try ");
			tempStr.append(addExtraCall(astTry.getTryBody(), null, margin));

			for (ICPPASTCatchHandler catcher : astTry.getCatchHandlers()) {
				tempStr.append(SpecialCharacter.LINE_BREAK).append(margin).append("catch (");

				String exception = catcher.isCatchAll() ? "..." : ast(catcher.getDeclaration());
				tempStr.append(exception).append(") ");

				extra = SpecialCharacter.MARK + exception + SpecialCharacter.MARK;
				tempStr.append(addExtraCall(catcher.getCatchBody(), extra, margin));
			}

			extra = "end catch";
			tempStr.append(SpecialCharacter.LINE_BREAK).append(margin).append(mark(extra, true, true));

		} else if (stm instanceof IASTBreakStatement || stm instanceof IASTContinueStatement)
			tempStr.append(ast(stm));
		else {
			String raw = ast(stm);
			tempStr.append(mark(esc(raw), true, true)).append(SpecialCharacter.SPACE).append(raw);
		}

		return tempStr.toString();
	}

	private String createMarkForSubCondition(IASTNode astCon) {
		StringBuilder tempStr = new StringBuilder();
		astCon = Utils.shortenAstNode(astCon);
		if (isCondition(astCon)) {
			if (astCon instanceof IASTBinaryExpression) {
				int operator = ((IASTBinaryExpression) astCon).getOperator();

				switch (operator) {
				case IASTBinaryExpression.op_greaterEqual:
				case IASTBinaryExpression.op_greaterThan:
				case IASTBinaryExpression.op_lessEqual:
				case IASTBinaryExpression.op_lessThan:
					String markAstCon = "mark(\"" + Utils.shortenAstNode(astCon).getRawSignature() + "\")";
					tempStr.append("	(" + astCon + "&&" + Utils.shortenAstNode(astCon).getRawSignature() + ")");
					break;

				case IASTBinaryExpression.op_logicalAnd:
				case IASTBinaryExpression.op_logicalOr:
					IASTExpression operand1 = ((IASTBinaryExpression) astCon).getOperand1();
					IASTExpression operand2 = ((IASTBinaryExpression) astCon).getOperand2();

					tempStr.append("(" + createMarkForSubCondition(operand1) + ")")
							.append(operator == IASTBinaryExpression.op_logicalAnd ? "	&&" : "	||")
							.append("(" + createMarkForSubCondition(operand2) + ")");

					break;
				}
			} else if (astCon instanceof CPPASTIdExpression) {
				tempStr.append("mark(\"" + astCon.getRawSignature() + "\")&&" + astCon.getRawSignature());
			} else {
				// unknown cases
				tempStr.append("mark(\"" + astCon.getRawSignature() + "\")&&" + astCon.getRawSignature());
			}
		} else {
			tempStr.append("mark(\"" + Utils.shortenAstNode(astCon).getRawSignature() + "\")").append("&&")
					.append("(" + Utils.shortenAstNode(astCon).getRawSignature() + ")");
		}

		return tempStr.toString();
	}

	private boolean isCondition(IASTNode condition) {
		boolean isCondition = false;
		// Ex1: abc
		// Ex2: 123
		// Ex3: sv.name
		condition = Utils.shortenAstNode(condition);
		if (condition instanceof IASTIdExpression || condition instanceof IASTFieldReference) {
			isCondition = true;

		} else if (condition instanceof IASTBinaryExpression) {
			IASTBinaryExpression binaryCon = (IASTBinaryExpression) condition;
			int operator = binaryCon.getOperator();

			switch (operator) {
			case IASTBinaryExpression.op_logicalAnd:
			case IASTBinaryExpression.op_logicalOr:
				isCondition = true;
				break;
			}
		}
		return isCondition;
	}

	private String addExtraCall(IASTStatement stm, String extra, String margin) {
		if (extra != null)
			extra = mark(extra, true, false);

		if (stm instanceof IASTCompoundStatement)
			return parseBlock((IASTCompoundStatement) stm, extra, margin);
		else {
			StringBuilder b = new StringBuilder();
			String inside = margin + SpecialCharacter.TAB;
			b.append(SpecialCharacter.OPEN_BRACE).append("mark(\"{\");").append(SpecialCharacter.LINE_BREAK)
					.append(inside).append(inside).append(parseStatement(stm, inside))
					.append(SpecialCharacter.LINE_BREAK).append(margin).append("mark(\"}\");")
					.append(SpecialCharacter.CLOSE_BRACE);
			return b.toString();
		}
	}

	private String mark(String arg, boolean end, boolean count) {
		String b = "mark(\"" + arg + "\")";

		if (end)
			b += ';';

		return b;
	}

	private String esc(String str) {
		str = str.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\"", "\\\\\"");
		str = str.replaceAll("(\\n)|(\\r\\n)", " ");
		return str;
	}

	/**
	 * Ex: "( x ==1 )"------> "x==1". We normalize condition
	 */
	private String ast(IASTNode node) {
		if (node != null) {
			if (!node.getRawSignature().endsWith(SpecialCharacter.END_OF_STATEMENT)) {
				node = Utils.shortenAstNode(node);
			}
			return node.getRawSignature();
		} else {
			return "";
		}
	}

	@Override
	public IFunctionNode getFunctionNode() {
		return null;
	}

	@Override
	public void setFunctionNode(IFunctionNode functionNode) {

	}
}