package instrument;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTBreakStatement;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTContinueStatement;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNullStatement;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCatchHandler;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTryBlockStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionCallExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDefinition;

import config.Paths;
import normalizer.FunctionNormalizer;
import parser.projectparser.ProjectParser;
import tree.object.IFunctionNode;
import utils.SpecialCharacter;
import utils.Utils;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

/**
 * Extend the previous instrumentation function by adding extra information
 * (e.g., the line of statements) to markers. <br/>
 * Ex: int a = 0; ----instrument-----> mark("line 12:int a = 0"); int a = 0;
 * 
 * <br/>
 * See {@link FunctionInstrumentUtils.java} to analyze the test path of a
 * function under test
 * 
 * @author DucAnh
 */
public class FunctionInstrumentationForStatementvsBranch_Marker implements IFunctionInstrumentationGeneration {

	private IFunctionNode functionNode;

	public FunctionInstrumentationForStatementvsBranch_Marker() {
	}

	public FunctionInstrumentationForStatementvsBranch_Marker(IFunctionNode fn) {
		functionNode = fn;
	}

	public FunctionInstrumentationForStatementvsBranch_Marker(IFunctionNode fn, boolean normalizedMode) {
		functionNode = fn;
	}

	public static void main(String[] args) throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.JOURNAL_TEST));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "quickSort(int[],int,int)").get(0);
		System.out.println("Original function:\n" + function.getAST().getRawSignature());

		FunctionNormalizer fnNorm = function.normalizedAST();
		function.setAST(fnNorm.getNormalizedAST());
		System.out.println("Normalized function:\n" + function.getAST().getRawSignature());

		System.out.println(
				new FunctionInstrumentationForStatementvsBranch_Marker(function, true).generateInstrumentedFunction());
	}

	@Override
	public String generateInstrumentedFunction() {
		return instrument(functionNode);
	}

	protected String addExtraCall(IASTStatement stm, String extra, String margin) {
		if (extra != null)
			extra = putInMark(extra, true);

		if (stm instanceof IASTCompoundStatement)
			return parseBlock((IASTCompoundStatement) stm, extra, margin);
		else {
			StringBuilder b = new StringBuilder();
			String inside = margin + SpecialCharacter.TAB;

			b.append(SpecialCharacter.OPEN_BRACE).append(SpecialCharacter.LINE_BREAK).append(inside).append(inside)
					.append(parseStatement(stm, inside)).append(SpecialCharacter.LINE_BREAK).append(margin)
					.append(SpecialCharacter.CLOSE_BRACE);
			return b.toString();
		}
	}

	protected String instrument(IFunctionNode fn) {
		StringBuilder b = new StringBuilder();

		b.append(getShortenContent(fn.getAST().getDeclSpecifier())).append(SpecialCharacter.SPACE)
				.append(getShortenContent(fn.getAST().getDeclarator()))
				.append(parseBlock((IASTCompoundStatement) fn.getAST().getBody(), null, ""));

		return b.toString();
	}

	protected String parseBlock(IASTCompoundStatement block, String extra, String margin) {
		StringBuilder b = new StringBuilder();

		if (block.getParent() instanceof CPPASTFunctionDefinition) {
			b = new StringBuilder("{" + putInMark(addMarkerByProperty(STATEMENT, "{") + DELIMITER_BETWEEN_PROPERTIES
					+ addMarkerByProperty(LINE_NUMBER_OF_BLOCK_IN_FUNCTION,
							block.getFileLocation().getStartingLineNumber() + "")
					+ DELIMITER_BETWEEN_PROPERTIES + addMarkerByProperty(OPENNING_FUNCTION_SCOPE, "true"), true)
					+ "\n");
		} else
			b = new StringBuilder("{" + putInMark(
					addMarkerByProperty(STATEMENT, "{") + DELIMITER_BETWEEN_PROPERTIES + addMarkerByProperty(
							LINE_NUMBER_OF_BLOCK_IN_FUNCTION, block.getFileLocation().getStartingLineNumber() + ""),
					true) + "\n");

		String inside = margin + SpecialCharacter.TAB;
		if (extra != null)
			b.append(inside);

		for (IASTStatement stm : block.getStatements())
			b.append(inside).append(parseStatement(stm, inside)).append(SpecialCharacter.LINE_BREAK)
					.append(SpecialCharacter.LINE_BREAK);

		b.append(margin)
				.append(putInMark(addMarkerByProperty(STATEMENT, "}") + DELIMITER_BETWEEN_PROPERTIES
						+ addMarkerByProperty(LINE_NUMBER_OF_BLOCK_IN_FUNCTION,
								block.getFileLocation().getStartingLineNumber() + ""),
						true))
				.append(SpecialCharacter.CLOSE_BRACE);
		return b.toString();
	}

	protected String parseStatement(IASTStatement stm, String margin) {
		StringBuilder b = new StringBuilder();

		if (stm instanceof IASTCompoundStatement)
			b.append(parseBlock((IASTCompoundStatement) stm, null, margin));

		else if (stm instanceof IASTIfStatement) {
			IASTIfStatement astIf = (IASTIfStatement) stm;
			IASTStatement astElse = astIf.getElseClause();
			String cond = getShortenContent(astIf.getConditionExpression());
			b.append("if (").append(putInMark(addDefaultMarkerContentForIf(astIf.getConditionExpression()), false))
					.append(" && (").append(cond).append(")) ");

			b.append(addExtraCall(astIf.getThenClause(), "", margin));

			if (astElse != null) {
				b.append(SpecialCharacter.LINE_BREAK).append(margin).append("else ");
				b.append(addExtraCall(astElse, "", margin));
			}

		} else if (stm instanceof IASTForStatement) {
			IASTForStatement astFor = (IASTForStatement) stm;
			b.append(putInMark(addBeginningScopeMarkerForForLoop(), true));

			// Add marker for initialization
			IASTStatement astInit = astFor.getInitializerStatement();
			if (!(astInit instanceof IASTNullStatement)) {
				b.append(putInMark(addDefaultMarkerContentforNormalStatement(astInit), true));
			}

			b.append("for (").append(getShortenContent(astInit));
			// Add marker for condition
			IASTExpression astCond = (IASTExpression) Utils.shortenAstNode(astFor.getConditionExpression());
			if (astCond != null) {
				b.append(SpecialCharacter.LINE_BREAK).append("\t\t\t");
				b.append(putInMark(addDefaultMarkerContentforNormalStatement(astCond), false) + " && "
						+ getShortenContent(astCond) + ";");
			}

			// Add marker for increment
			IASTExpression astIter = astFor.getIterationExpression();
			if (astIter != null) {
				b.append(SpecialCharacter.LINE_BREAK).append("\t\t\t");
				b.append(putInMark(addDefaultMarkerContentforNormalStatement(astIter), false)).append(" && ").append("(")
						.append(getShortenContent(astIter)).append(")");
			}
			b.append(") ");

			// For loop: no condition
			if (astCond == null)
				b.append(parseStatement(astFor.getBody(), margin));
			else
				b.append(addExtraCall(astFor.getBody(), "", margin));

			b.append(putInMark(addEndScopeMarkerForForLoop(), true));

		} else if (stm instanceof IASTWhileStatement) {
			IASTWhileStatement astWhile = (IASTWhileStatement) stm;
			String cond = getShortenContent(astWhile.getCondition());

			b.append("while (")
					.append(putInMark(addDefaultMarkerContentforNormalStatement(astWhile.getCondition()), false))
					.append(" && (").append(cond).append(")) ");

			b.append(addExtraCall(astWhile.getBody(), "", margin));

		} else if (stm instanceof IASTDoStatement) {
			IASTDoStatement astDo = (IASTDoStatement) stm;
			String cond = getShortenContent(astDo.getCondition());

			b.append("do ").append(addExtraCall(astDo.getBody(), "", margin)).append(SpecialCharacter.LINE_BREAK)
					.append(margin).append("while (")
					.append(putInMark(addDefaultMarkerContentforNormalStatement(astDo.getCondition()), false))
					.append(" && (").append(cond).append("));");

		} else if (stm instanceof ICPPASTTryBlockStatement) {
			ICPPASTTryBlockStatement astTry = (ICPPASTTryBlockStatement) stm;

			b.append("mark(\"start try;\")");

			b.append(SpecialCharacter.LINE_BREAK).append(margin).append("try ");
			b.append(addExtraCall(astTry.getTryBody(), null, margin));

			for (ICPPASTCatchHandler catcher : astTry.getCatchHandlers()) {
				b.append(SpecialCharacter.LINE_BREAK).append(margin).append("catch (");

				String exception = catcher.isCatchAll() ? "..." : getShortenContent(catcher.getDeclaration());
				b.append(exception).append(") ");

				b.append(addExtraCall(catcher.getCatchBody(), exception, margin));
			}

			b.append(SpecialCharacter.LINE_BREAK).append(margin).append("mark(\"end catch;\")");

		} else if (stm instanceof IASTBreakStatement || stm instanceof IASTContinueStatement) {
			b.append(putInMark(addDefaultMarkerContentforNormalStatement(stm), true));
			b.append(getShortenContent(stm));

		} else if (stm instanceof IASTReturnStatement) {
			b.append(putInMark(addDefaultMarkerContentforNormalStatement(stm), true));
			b.append(getShortenContent(stm));

		} else {
			String raw = getShortenContent(stm);
			b.append(putInMark(addDefaultMarkerContentforNormalStatement(stm), true));// add markers
			b.append(raw);
		}

		return b.toString();
	}

	@Override
	public IFunctionNode getFunctionNode() {
		return functionNode;
	}

	@Override
	public void setFunctionNode(IFunctionNode functionNode) {
		this.functionNode = functionNode;
	}

	protected String getShortenContent(IASTNode node) {
		if (node != null) {
			if (node.getRawSignature().endsWith(SpecialCharacter.END_OF_STATEMENT)) {

			} else
				/*
				 * Ex: "( x ==1   )"------> "x=1". We normalize condition
				 */
				node = Utils.shortenAstNode(node);

			return node.getRawSignature();
		} else
			return "";
	}

	/**
	 * Shorten a statement
	 * 
	 * @param str
	 * @return A shortened statement
	 */
	protected String esc(String str) {
		str = str.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\"", "\\\\\"");

		/**
		 * The statement that is divided in multiple line is convert into a single line,
		 * e.g.,
		 * 
		 * <pre>
		 * int a =
		 *
		 * 		0;
		 * </pre>
		 * 
		 * ----------------> "int a = 0"
		 *
		 */
		str = str.replaceAll("(\\n)|(\\r\\n)", " ");
		return str;
	}

	/**
	 * Add information which we want to print out in instrumented code
	 * 
	 * @param node
	 *            The AST of node needed to be instrumented
	 * @return a string which store the extra information
	 */
	protected String addDefaultMarkerContentforNormalStatement(IASTNode node) {
		String lineProperty = LINE_NUMBER_IN_FUNCTION + DELIMITER_BETWEEN_PROPERTY_AND_VALUE
				+ node.getFileLocation().getStartingLineNumber();
		String colProperty = OFFSET_IN_FUNCTION + DELIMITER_BETWEEN_PROPERTY_AND_VALUE
				+ node.getFileLocation().getNodeOffset();
		String statementProperty = STATEMENT + DELIMITER_BETWEEN_PROPERTY_AND_VALUE + esc(getShortenContent(node));
		String recursive = IS_RECURSIVE + DELIMITER_BETWEEN_PROPERTY_AND_VALUE + "true";

		if (containRecursiveCall(node)) {
			return lineProperty + DELIMITER_BETWEEN_PROPERTIES + colProperty + DELIMITER_BETWEEN_PROPERTIES
					+ statementProperty + (DELIMITER_BETWEEN_PROPERTIES + recursive);
		} else
			return lineProperty + DELIMITER_BETWEEN_PROPERTIES + colProperty + DELIMITER_BETWEEN_PROPERTIES
					+ statementProperty;
	}

	protected String addDefaultMarkerContentForIf(IASTNode node) {
		String condition = IN_CONTROL_BLOCK + DELIMITER_BETWEEN_PROPERTY_AND_VALUE + IF_BLOCK;
		return addDefaultMarkerContentforNormalStatement(node) + DELIMITER_BETWEEN_PROPERTIES + condition;
	}

	protected String addMarkerByProperty(String nameProperty, String value) {
		return nameProperty + DELIMITER_BETWEEN_PROPERTY_AND_VALUE + value;
	}

	protected String addBeginningScopeMarker() {
		return STATEMENT + DELIMITER_BETWEEN_PROPERTY_AND_VALUE + "{" + DELIMITER_BETWEEN_PROPERTIES
				+ ADDITIONAL_BODY_CONTROL_MARKER + DELIMITER_BETWEEN_PROPERTY_AND_VALUE + "true";
	}

	protected String addEndScopeMarker() {
		return STATEMENT + DELIMITER_BETWEEN_PROPERTY_AND_VALUE + "}" + DELIMITER_BETWEEN_PROPERTIES
				+ ADDITIONAL_BODY_CONTROL_MARKER + DELIMITER_BETWEEN_PROPERTY_AND_VALUE + "true";
	}

	protected String addBeginningScopeMarkerForForLoop() {
		return STATEMENT + DELIMITER_BETWEEN_PROPERTY_AND_VALUE + "{" + DELIMITER_BETWEEN_PROPERTIES
				+ ADDITIONAL_BODY_CONTROL_MARKER + DELIMITER_BETWEEN_PROPERTY_AND_VALUE + "true"
				+ DELIMITER_BETWEEN_PROPERTIES + SOURROUNDING_MARKER + DELIMITER_BETWEEN_PROPERTY_AND_VALUE + "for";
	}

	protected String addEndScopeMarkerForForLoop() {
		return STATEMENT + DELIMITER_BETWEEN_PROPERTY_AND_VALUE + "}" + DELIMITER_BETWEEN_PROPERTIES
				+ ADDITIONAL_BODY_CONTROL_MARKER + DELIMITER_BETWEEN_PROPERTY_AND_VALUE + "true"
				+ DELIMITER_BETWEEN_PROPERTIES + SOURROUNDING_MARKER + DELIMITER_BETWEEN_PROPERTY_AND_VALUE + "for";
	}

	/**
	 * Put a string in a marker
	 * 
	 * @param str
	 * @return
	 */
	protected String putInMark(String str, boolean isAStatement) {
		return "mark(\"" + str + "\")" + (isAStatement ? ";" : "");
	}

	private boolean containRecursiveCall(IASTNode stm) {
		// Get all function calls
		List<CPPASTFunctionCallExpression> functionCalls = new ArrayList<>();
		ASTVisitor visitor = new ASTVisitor() {
			@Override
			public int visit(IASTExpression statement) {
				if (statement instanceof CPPASTFunctionCallExpression) {
					functionCalls.add((CPPASTFunctionCallExpression) statement);
					return ASTVisitor.PROCESS_SKIP;
				} else
					return ASTVisitor.PROCESS_CONTINUE;
			}
		};
		visitor.shouldVisitExpressions = true;
		stm.accept(visitor);

		// Create link from statement to the called function
		boolean foundRecursive = false;
		for (CPPASTFunctionCallExpression functionCall : functionCalls) {
			String name = functionCall.getChildren()[0].getRawSignature();
			if (functionNode.getDeclaration().startsWith(name + "(")) {

				foundRecursive = true;
				break;
			}
		}
		return foundRecursive;
	}

	public static final String TYPE_STATEMENT = "type";

	public static enum TYPE_STATEMENT_ENUM {
		DO_BEGINNING("do_beginning"), DO_ENDING("do_ending"), WHILE_BEGINING("while_beginning"), WHILE_ENDING(
				"while_ending"), IF_BEGINNING("if_beginning"), IF_ENDING("if_ending"),;

		private String url;

		TYPE_STATEMENT_ENUM(String url) {
			this.url = url;
		}

		public String url() {
			return url;
		}
	}

	public static final String IN_CONTROL_BLOCK = "control-block";
	public static final String IF_BLOCK = "if";
	public static final String FOR_BLOCK = "for";

	public static final String LINE_NUMBER_IN_FUNCTION = "line-in-function";
	public static final String LINE_NUMBER_OF_BLOCK_IN_FUNCTION = "line-of-blockin-function";
	public static final String OFFSET_IN_FUNCTION = "offset";
	public static final String STATEMENT = "statement";
	public static final String DELIMITER_BETWEEN_PROPERTIES = "###";
	public static final String DELIMITER_BETWEEN_PROPERTY_AND_VALUE = "=";
	public static final String OPENNING_FUNCTION_SCOPE = "openning-function"; // {true, false}
	public static final String IS_RECURSIVE = "is-recursive"; // {true, false}
	// Beside the executed statements, we also add several additional codes to print
	// out further information
	public static final String ADDITIONAL_BODY_CONTROL_MARKER = "additional-code";
	public static final String SOURROUNDING_MARKER = "surrounding-control-block";
}
