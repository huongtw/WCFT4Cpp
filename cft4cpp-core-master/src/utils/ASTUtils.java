package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTSwitchStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.DefaultLogService;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.ScannerInfo;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTExpressionStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIdExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTProblemDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclSpecifier;

public class ASTUtils {

	public static IASTTranslationUnit getIASTTranslationUnit(char[] source, String filePath,
			Map<String, String> macroList, ILanguage lang) {
		FileContent reader = FileContent.create(filePath, source);
		String[] includeSearchPaths = new String[0];
		IScannerInfo scanInfo = new ScannerInfo(macroList, includeSearchPaths);
		IncludeFileContentProvider fileCreator = IncludeFileContentProvider.getSavedFilesProvider();
		int options = ILanguage.OPTION_IS_SOURCE_UNIT;
		IParserLogService log = new DefaultLogService();

		try {
			return lang.getASTTranslationUnit(reader, scanInfo, fileCreator, null, options, log);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		String path = "data-test/samvu/1_Load_Tree.cpp";
		ASTUtils.printTree(path);
	}

	/**
	 * In cÃ¢y cáº¥u trÃºc ra mÃ n hÃ¬nh
	 */
	public static void printTree(IASTNode n, String s) {
		String content = n.getRawSignature().replaceAll("[\r\n]", "");
		IASTNode[] child = n.getChildren();
		System.out.println(s + content + ": " + n.getClass().getSimpleName());
		for (IASTNode c : child)
			ASTUtils.printTree(c, s + "   ");

	}

	public static void printTree(String path) {
		try {
			File file = new File(path);
			String content = UtilsVu.getContentFile(file);
			ILanguage lang = file.getName().toLowerCase().endsWith(".c") ? GCCLanguage.getDefault()
					: GPPLanguage.getDefault();
			IASTTranslationUnit u = ASTUtils.getIASTTranslationUnit(content.toCharArray(), path, null, lang);

			ASTUtils.printTree(u, " | ");
		} catch (Exception e) {

		}
	}

	
	/**
	 * Print an ASTNode out the screen <br/>
	 * Example: From the ASTNode of this function: <br/>
	 * 
	 * <pre>
	 * int class_test1(SinhVien sv) {
	 * 	if ((1.0 + 2) > x)
	 * 		return true;
	 * 	if (1 + 2 > x + 1 + 3)
	 * 		return true;
	 * }
	 * </pre>
	 * 
	 * We have the content of ASTNode:
	 * 
	 * <pre>
	 | int class_test1(SinhVien sv){	if ((1.0+2)>x)		return true;	if (1+2>x+1+3)		return true;}: CPPASTTranslationUnit
	 |    int class_test1(SinhVien sv){	if ((1.0+2)>x)		return true;	if (1+2>x+1+3)		return true;}: CPPASTFunctionDefinition
	 |       int: CPPASTSimpleDeclSpecifier
	 |       class_test1(SinhVien sv): CPPASTFunctionDeclarator
	 |          class_test1: CPPASTName
	 |          SinhVien sv: CPPASTParameterDeclaration
	 |             SinhVien: CPPASTNamedTypeSpecifier
	 |                SinhVien: CPPASTName
	 |             sv: CPPASTDeclarator
	 |                sv: CPPASTName
	 |       {	if ((1.0+2)>x)		return true;	if (1+2>x+1+3)		return true;}: CPPASTCompoundStatement
	 |          if ((1.0+2)>x)		return true;: CPPASTIfStatement
	 |             (1.0+2)>x: CPPASTBinaryExpression
	 |                (1.0+2): CPPASTUnaryExpression
	 |                   1.0+2: CPPASTBinaryExpression
	 |                      1.0: CPPASTLiteralExpression
	 |                      2: CPPASTLiteralExpression
	 |                x: CPPASTIdExpression
	 |                   x: CPPASTName
	 |             return true;: CPPASTReturnStatement
	 |                true: CPPASTLiteralExpression
	 |          if (1+2>x+1+3)		return true;: CPPASTIfStatement
	 |             1+2>x+1+3: CPPASTBinaryExpression
	 |                1+2: CPPASTBinaryExpression
	 |                   1: CPPASTLiteralExpression
	 |                   2: CPPASTLiteralExpression
	 |                x+1+3: CPPASTBinaryExpression
	 |                   x+1: CPPASTBinaryExpression
	 |                      x: CPPASTIdExpression
	 |                         x: CPPASTName
	 |                      1: CPPASTLiteralExpression
	 |                   3: CPPASTLiteralExpression
	 |             return true;: CPPASTReturnStatement
	 |                true: CPPASTLiteralExpression
	 * 
	 * </pre>
	 */
	public static void printTreeFromAstNode(IASTNode n, String tab) {
		String content = n.getRawSignature().replaceAll("[\r\n]", "");
		IASTNode[] child = n.getChildren();
		System.out.println(tab + content + ": " + n.getClass().getSimpleName());
		for (IASTNode c : child)
			ASTUtils.printTreeFromAstNode(c, tab + "   ");

	}

	/**
	 * Print content of abstract tree from source code <br/>
	 * Example: From the ASTNode of this function: <br/>
	 * 
	 * <pre>
	 * int class_test1(SinhVien sv) {
	 * 	if ((1.0 + 2) > x)
	 * 		return true;
	 * 	if (1 + 2 > x + 1 + 3)
	 * 		return true;
	 * }
	 * </pre>
	 * 
	 * We have the content of ASTNode:
	 * 
	 * <pre>
	 | int class_test1(SinhVien sv){	if ((1.0+2)>x)		return true;	if (1+2>x+1+3)		return true;}: CPPASTTranslationUnit
	 |    int class_test1(SinhVien sv){	if ((1.0+2)>x)		return true;	if (1+2>x+1+3)		return true;}: CPPASTFunctionDefinition
	 |       int: CPPASTSimpleDeclSpecifier
	 |       class_test1(SinhVien sv): CPPASTFunctionDeclarator
	 |          class_test1: CPPASTName
	 |          SinhVien sv: CPPASTParameterDeclaration
	 |             SinhVien: CPPASTNamedTypeSpecifier
	 |                SinhVien: CPPASTName
	 |             sv: CPPASTDeclarator
	 |                sv: CPPASTName
	 |       {	if ((1.0+2)>x)		return true;	if (1+2>x+1+3)		return true;}: CPPASTCompoundStatement
	 |          if ((1.0+2)>x)		return true;: CPPASTIfStatement
	 |             (1.0+2)>x: CPPASTBinaryExpression
	 |                (1.0+2): CPPASTUnaryExpression
	 |                   1.0+2: CPPASTBinaryExpression
	 |                      1.0: CPPASTLiteralExpression
	 |                      2: CPPASTLiteralExpression
	 |                x: CPPASTIdExpression
	 |                   x: CPPASTName
	 |             return true;: CPPASTReturnStatement
	 |                true: CPPASTLiteralExpression
	 |          if (1+2>x+1+3)		return true;: CPPASTIfStatement
	 |             1+2>x+1+3: CPPASTBinaryExpression
	 |                1+2: CPPASTBinaryExpression
	 |                   1: CPPASTLiteralExpression
	 |                   2: CPPASTLiteralExpression
	 |                x+1+3: CPPASTBinaryExpression
	 |                   x+1: CPPASTBinaryExpression
	 |                      x: CPPASTIdExpression
	 |                         x: CPPASTName
	 |                      1: CPPASTLiteralExpression
	 |                   3: CPPASTLiteralExpression
	 |             return true;: CPPASTReturnStatement
	 |                true: CPPASTLiteralExpression
	 * 
	 * </pre>
	 */
	public static void printTreeFromSourcecodeFile(String path) {
		try {
			File file = new File(path);

			if (file.exists()) {
				String content = UtilsVu.getContentFile(file);
				ILanguage lang = file.getName().toLowerCase().endsWith(".c") ? GCCLanguage.getDefault()
						: GPPLanguage.getDefault();
				IASTTranslationUnit u = ASTUtils.getIASTTranslationUnit(content.toCharArray(), path, null, lang);

				ASTUtils.printTreeFromAstNode(u, " | ");
			} else
				throw new Exception("File " + path + " does not exist");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Find ast by the content of condition
	 *
	 * @param name
	 *            the content of condition
	 * @param ast
	 *            the ast of source code containing the condition
	 * @return
	 */
	public static IASTNode findFirstConditionByName(String name, IASTNode ast) {
		ASTVisitor visitor = new ASTVisitor() {
			@Override
			public int visit(IASTStatement statement) {
				if (statement instanceof IASTIfStatement) {
					IASTNode con = ((IASTIfStatement) statement).getConditionExpression();

					if (con.getRawSignature().equals(name)) {
						Utils.output = con;
						return ASTVisitor.PROCESS_ABORT;
					}
				} else if (statement instanceof IASTWhileStatement) {
					IASTNode con = ((IASTWhileStatement) statement).getCondition();
					if (con.getRawSignature().equals(name)) {
						Utils.output = con;
						return ASTVisitor.PROCESS_ABORT;
					}
				} else if (statement instanceof IASTDoStatement) {
					IASTNode con = ((IASTDoStatement) statement).getCondition();
					if (con.getRawSignature().equals(name)) {
						Utils.output = con;
						return ASTVisitor.PROCESS_ABORT;
					}
				} else if (statement instanceof IASTSwitchStatement) {
					// TODO: xu ly
				}
				return ASTVisitor.PROCESS_CONTINUE;
			}
		};
		visitor.shouldVisitStatements = true;
		visitor.shouldVisitExpressions = true;
		ast.accept(visitor);

		return Utils.output;
	}

	/**
	 * Get all binary expressions
	 *
	 * @param ast
	 * @return
	 */
	public static List<ICPPASTBinaryExpression> getBinaryExpressions(IASTNode ast) {
		List<ICPPASTBinaryExpression> binaryASTs = new ArrayList<>();

		ASTVisitor visitor = new ASTVisitor() {

			@Override
			public int visit(IASTExpression name) {
				if (name instanceof ICPPASTBinaryExpression)
					binaryASTs.add((ICPPASTBinaryExpression) name);
				return ASTVisitor.PROCESS_CONTINUE;
			}
		};

		visitor.shouldVisitExpressions = true;

		ast.accept(visitor);
		return binaryASTs;
	}

	/**
	 * Get all declarations in the given ast
	 *
	 * @param ast
	 * @return
	 */
	public static List<IASTSimpleDeclaration> getSimpleDeclarations(IASTNode ast) {
		List<IASTSimpleDeclaration> declarationASTs = new ArrayList<>();

		ASTVisitor visitor = new ASTVisitor() {

			@Override
			public int visit(IASTDeclaration name) {
				if (name instanceof IASTSimpleDeclaration)
					declarationASTs.add((IASTSimpleDeclaration) name);
				return ASTVisitor.PROCESS_CONTINUE;
			}
		};

		visitor.shouldVisitDeclarations = true;

		ast.accept(visitor);
		return declarationASTs;
	}

	public static List<ICPPASTLiteralExpression> getLiteralExpressions(IASTNode ast) {
		List<ICPPASTLiteralExpression> literalASTs = new ArrayList<>();

		ASTVisitor visitor = new ASTVisitor() {

			@Override
			public int visit(IASTExpression name) {
				if (name instanceof ICPPASTLiteralExpression)
					literalASTs.add((ICPPASTLiteralExpression) name);
				return ASTVisitor.PROCESS_CONTINUE;
			}
		};

		visitor.shouldVisitExpressions = true;

		ast.accept(visitor);
		return literalASTs;
	}

	/**
	 * Get all functions in the given source code
	 *
	 * @param sourcecode
	 *            Source code contains several functions
	 * @return
	 */
	public static List<ICPPASTFunctionDefinition> getFunctionsinAST(char[] sourcecode) {
		List<ICPPASTFunctionDefinition> output = new ArrayList<>();

		try {
			IASTTranslationUnit unit = ASTUtils.getIASTTranslationUnitforCpp(sourcecode);

			if (unit.getChildren()[0] instanceof CPPASTProblemDeclaration)
				unit = ASTUtils.getIASTTranslationUnitforC(sourcecode);

			ASTVisitor visitor = new ASTVisitor() {
				@Override
				public int visit(IASTDeclaration declaration) {
					if (declaration instanceof ICPPASTFunctionDefinition) {
						output.add((ICPPASTFunctionDefinition) declaration);
						return ASTVisitor.PROCESS_SKIP;
					}
					return ASTVisitor.PROCESS_CONTINUE;
				}
			};

			visitor.shouldVisitDeclarations = true;

			unit.accept(visitor);
		} catch (Exception e) {

		}
		return output;
	}

	public static List<ICPPASTFieldReference> getFieldReferences(IASTNode ast) {
		List<ICPPASTFieldReference> binaryASTs = new ArrayList<>();

		ASTVisitor visitor = new ASTVisitor() {

			@Override
			public int visit(IASTExpression name) {
				if (name instanceof ICPPASTFieldReference)
					binaryASTs.add((ICPPASTFieldReference) name);
				return ASTVisitor.PROCESS_CONTINUE;
			}
		};

		visitor.shouldVisitExpressions = true;

		ast.accept(visitor);
		return binaryASTs;
	}

	public static List<ICPPASTArraySubscriptExpression> getArraySubscriptExpression(IASTNode ast) {
		List<ICPPASTArraySubscriptExpression> ids = new ArrayList<>();

		ASTVisitor visitor = new ASTVisitor() {
			@Override
			public int visit(IASTExpression expression) {
				if (expression instanceof ICPPASTArraySubscriptExpression) {
					ids.add((ICPPASTArraySubscriptExpression) expression);

					if (expression.getChildren()[0] instanceof ICPPASTArraySubscriptExpression)
						return ASTVisitor.PROCESS_SKIP;
					else
						return ASTVisitor.PROCESS_CONTINUE;
				} else
					return ASTVisitor.PROCESS_CONTINUE;
			}
		};

		visitor.shouldVisitExpressions = true;

		ast.accept(visitor);
		return ids;
	}

	public static IASTTranslationUnit getIASTTranslationUnitforC(char[] code) throws Exception {
		File filePath = new File("");
		FileContent fc = FileContent.create(filePath.getAbsolutePath(), code);
		Map<String, String> macroDefinitions = new HashMap<>();
		String[] includeSearchPaths = new String[0];
		IScannerInfo si = new ScannerInfo(macroDefinitions, includeSearchPaths);
		IncludeFileContentProvider ifcp = IncludeFileContentProvider.getEmptyFilesProvider();
		IIndex idx = null;
		int options = ILanguage.OPTION_IS_SOURCE_UNIT;
		IParserLogService log = new DefaultLogService();
		return GCCLanguage.getDefault().getASTTranslationUnit(fc, si, ifcp, idx, options, log);
	}

	/**
	 * Get all CPPASTIdExpression objects in ASTNode
	 *
	 * @param ast
	 * @return
	 */
	public static List<CPPASTIdExpression> getIds(IASTNode ast) {
		List<CPPASTIdExpression> ids = new ArrayList<>();

		ASTVisitor visitor = new ASTVisitor() {
			@Override
			public int visit(IASTExpression expression) {
				if (expression instanceof CPPASTIdExpression)
					ids.add((CPPASTIdExpression) expression);
				return ASTVisitor.PROCESS_CONTINUE;
			}
		};

		visitor.shouldVisitExpressions = true;

		ast.accept(visitor);
		return ids;
	}

	public static IASTTranslationUnit getIASTTranslationUnitforCpp(char[] code) throws Exception {
		File filePath = new File("");
		FileContent fc = FileContent.create(filePath.getAbsolutePath(), code);
		Map<String, String> macroDefinitions = new HashMap<>();
		String[] includeSearchPaths = new String[0];
		IScannerInfo si = new ScannerInfo(macroDefinitions, includeSearchPaths);
		IncludeFileContentProvider ifcp = IncludeFileContentProvider.getEmptyFilesProvider();
		IIndex idx = null;
		int options = ILanguage.OPTION_IS_SOURCE_UNIT;
		IParserLogService log = new DefaultLogService();
		return GPPLanguage.getDefault().getASTTranslationUnit(fc, si, ifcp, idx, options, log);
	}

	/**
	 * Láº¥y danh sÃ¡ch CPPASTSimpleDeclSpecifier trong má»™t node AST
	 *
	 * @param ast
	 * @return
	 */
	public static List<CPPASTSimpleDeclSpecifier> getSimpleDeclSpecifiers(IASTNode ast) {
		List<CPPASTSimpleDeclSpecifier> cppSimpleDeclSpecifiers = new ArrayList<>();

		ASTVisitor visitor = new ASTVisitor() {
			@Override
			public int visit(IASTDeclSpecifier declSpec) {
				if (declSpec instanceof CPPASTSimpleDeclSpecifier)
					cppSimpleDeclSpecifiers.add((CPPASTSimpleDeclSpecifier) declSpec);
				return super.visit(declSpec);
			}
		};

		visitor.shouldVisitDeclSpecifiers = true;

		ast.accept(visitor);
		return cppSimpleDeclSpecifiers;
	}

	public static IASTNode findFirstASTByName(String name, IASTNode ast) {

		ASTVisitor visitor = new ASTVisitor() {
			@Override
			public int visit(IASTStatement statement) {
				if (statement.getRawSignature().equals(name)) {
					Utils.output = statement;
					return ASTVisitor.PROCESS_ABORT;
				} else
					return ASTVisitor.PROCESS_CONTINUE;
			}
		};
		visitor.shouldVisitStatements = true;
		visitor.shouldVisitExpressions = true;
		ast.accept(visitor);

		return Utils.output;
	}

	/**
	 * Kiá»ƒm tra trong biá»ƒu thá»©c cÃ³ lá»�i gá»�i hÃ m khÃ´ng
	 *
	 * @param ast
	 * @return
	 */
	public static boolean containFunctionCall(IASTNode ast) {

		ASTVisitor visitor = new ASTVisitor() {

			@Override
			public int visit(IASTExpression expression) {
				if (expression instanceof IASTFunctionCallExpression) {
					Utils.containFunction = true;
					return ASTVisitor.PROCESS_ABORT;
				} else
					return ASTVisitor.PROCESS_CONTINUE;
			}
		};
		visitor.shouldVisitStatements = true;
		visitor.shouldVisitExpressions = true;
		ast.accept(visitor);
		return Utils.containFunction;
	}

	/**
	 * Get ast corresponding to statement, e.g., x=y+2
	 *
	 * @param content
	 * @return
	 */
	public static IASTNode convertToIAST(String content) {
		IASTNode ast = null;

		/*
		 * Get type of the statement
		 */
		boolean isCondition = Utils.isCondition(content);
		/*
		 * The statement is assignment
		 */
		if (!isCondition) {
			content += content.endsWith(SpecialCharacter.END_OF_STATEMENT) ? "" : SpecialCharacter.END_OF_STATEMENT;

			ICPPASTFunctionDefinition fn = getFunctionsinAST(new String("void test(){" + content + "}").toCharArray())
					.get(0);
			ast = fn.getBody().getChildren()[0];
		} else
		/*
		 * The statement is condition
		 */ {
			ICPPASTFunctionDefinition fn = getFunctionsinAST(
					new String("void test(){if (" + content + "){}}").toCharArray()).get(0);
			ast = fn.getBody().getChildren()[0].getChildren()[0];
		}
		return ASTUtils.shortenAstNode(ast);
	}

	/**
	 * Get all unary expression
	 * <p>
	 * Ex: "x=(a++) +1+ (--b)" -------> unary expression: {"a++", "--b}
	 *
	 * @param ast
	 * @return
	 */
	public static List<ICPPASTUnaryExpression> getUnaryExpressions(IASTNode ast) {
		List<ICPPASTUnaryExpression> unaryExpressions = new ArrayList<>();

		ASTVisitor visitor = new ASTVisitor() {

			@Override
			public int visit(IASTExpression name) {
				if (name instanceof ICPPASTUnaryExpression) {
					unaryExpressions.add((ICPPASTUnaryExpression) name);
					return ASTVisitor.PROCESS_CONTINUE;
				} else
					return ASTVisitor.PROCESS_CONTINUE;
			}
		};

		visitor.shouldVisitExpressions = true;

		ast.accept(visitor);
		return unaryExpressions;
	}

	/**
	 * Shorten ast node. <br/>
	 * Ex:"(a)" -----> "a" <br/>
	 * Ex: "(!a)" --------> "!a"
	 *
	 * @param ast
	 * @return
	 */
	public static IASTNode shortenAstNode(IASTNode ast) {
		IASTNode tmp = ast;
		/*
		 * Ex:"(a)" -----> "a"
		 *
		 * Ex: "(!a)" --------> !a
		 */
		while ((tmp instanceof CPPASTExpressionStatement || tmp instanceof ICPPASTUnaryExpression
				&& tmp.getRawSignature().startsWith("(") && tmp.getRawSignature().endsWith(")"))
				&& tmp.getChildren().length == 1 && !tmp.getRawSignature().startsWith("!"))
			tmp = tmp.getChildren()[0];

		return tmp;
	}
}
