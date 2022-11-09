package cfg;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTBreakStatement;
import org.eclipse.cdt.core.dom.ast.IASTCaseStatement;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTContinueStatement;
import org.eclipse.cdt.core.dom.ast.IASTDefaultStatement;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTGotoStatement;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTLabelStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTNullStatement;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTSwitchStatement;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.cdt.core.dom.ast.INodeFactory;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCatchHandler;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTryBlockStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionCallExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;

import cfg.object.AdditionalScopeCfgNode;
import cfg.object.BeginFlagCfgNode;
import cfg.object.BreakCfgNode;
import cfg.object.CfgNode;
import cfg.object.ConditionDoCfgNode;
import cfg.object.ConditionForCfgNode;
import cfg.object.ConditionIfCfgNode;
import cfg.object.ConditionWhileCfgNode;
import cfg.object.ContinueCfgNode;
import cfg.object.EndFlagCfgNode;
import cfg.object.ForwardCfgNode;
import cfg.object.ICfgNode;
import cfg.object.MarkFlagCfgNode;
import cfg.object.ReturnNode;
import cfg.object.ScopeCfgNode;
import cfg.object.SimpleCfgNode;
import cfg.object.ThrowCfgNode;
import config.Paths;
import normalizer.FunctionNormalizer;
import parser.projectparser.ProjectParser;
import tree.object.IFunctionNode;
import tree.object.INode;
import utils.ASTUtils;
import utils.Utils;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

/**
 * Generate control flow graph from source code for statement/branch coverage
 *
 * @author DucAnh
 */
public class CFGGenerationforBranchvsStatementCoverage implements ICFGGeneration {
	final static Logger logger = Logger.getLogger(CFGGenerationforBranchvsStatementCoverage.class);
	/**
	 * Represent the begin node of CFG
	 */
	private ICfgNode BEGIN;

	/**
	 * Represent the end node of CFG
	 */
	private ICfgNode END;

	private Map<String, MarkFlagCfgNode> labels;

	private IFunctionNode functionNode;

	public CFGGenerationforBranchvsStatementCoverage() {
	}

	public CFGGenerationforBranchvsStatementCoverage(IFunctionNode normalizedFunction) {
		functionNode = normalizedFunction;
	}

	public static void main(String[] args) throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.SYMBOLIC_EXECUTION_TEST));

		INode function = Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "compare_string(char*,char*)").get(0);

		System.out.println(((IFunctionNode) function).getAST().getRawSignature());
		FunctionNormalizer fnNorm = ((IFunctionNode) function).normalizedAST();
		String normalizedCoverage = fnNorm.getNormalizedSourcecode();
		IFunctionNode clone = (IFunctionNode) function.clone();
		clone.setAST(Utils.getFunctionsinAST(normalizedCoverage.toCharArray()).get(0));

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(clone);
		ICFG cfg = cfgGen.generateCFG();
		cfg.setIdforAllNodes();
		System.out.println(cfg.toString());
	}

	@Override
	public ICFG generateCFG() throws Exception {
		IFunctionNode normalizedFunction = (IFunctionNode) functionNode.clone();
		normalizedFunction.setAST(functionNode.normalizedAST().getNormalizedAST());

		ICFG cfg = parse(normalizedFunction);
		cfg = setParentAgain(cfg);
		return cfg;
	}

	/**
	 * For fix bugs. Reason: Some scope nodes ("{", "}") do not have its
	 * parents.
	 * 
	 * @param cfg
	 * @return
	 */
	private ICFG setParentAgain(ICFG cfg) {
		for (ICfgNode cfgNode : cfg.getAllNodes()) {
			if (cfgNode instanceof ReturnNode) {
				// prevent
			} else {
				if (cfgNode.getTrueNode() != null)
					cfgNode.getTrueNode().setParent(cfgNode);

				if (cfgNode.getFalseNode() != null)
					cfgNode.getFalseNode().setParent(cfgNode);
			}
		}
		return cfg;
	}

	private String ast(IASTNode node) {
		return node == null ? "" : node.getRawSignature();
	}

	private boolean isThrowStatement(IASTStatement stm) {
		if (stm instanceof IASTExpressionStatement) {
			IASTExpression ex = ((IASTExpressionStatement) stm).getExpression();

			if (ex instanceof IASTUnaryExpression)
				return ((IASTUnaryExpression) ex).getOperator() == IASTUnaryExpression.op_throw;
			else
				return false;
		} else
			return false;
	}

	private IASTExpression joinCaseSwitch(IASTExpression cond, ArrayList<IASTCaseStatement> cases) {
		INodeFactory fac = cond.getTranslationUnit().getASTNodeFactory();
		IASTExpression build = fac.newBinaryExpression(IASTBinaryExpression.op_equals,
				cond.copy(CopyStyle.withLocations), cases.get(0).getExpression().copy(CopyStyle.withLocations));

		for (int i = 1; i < cases.size(); i++) {
			IASTExpression build2 = fac.newBinaryExpression(IASTBinaryExpression.op_equals,
					cond.copy(CopyStyle.withLocations), cases.get(i).getExpression().copy(CopyStyle.withLocations));
			build = fac.newBinaryExpression(IASTBinaryExpression.op_logicalOr, build, build2);
		}
		return build;
	}

	/**
	 * Get next statement that is not temporary statement
	 */
	private ICfgNode nextConcrete(ICfgNode stm) {
		while (stm instanceof ForwardCfgNode)
			stm = stm.getTrueNode();
		return stm;
	}

	/**
	 * Shorten expression by removing unnecessary "(" and ")"
	 */
	private IASTExpression normalize(IASTExpression ex) {
		return (IASTExpression) Utils.shortenAstNode(ex);
	}

	private boolean notNull(IASTNode node) {
		return node != null && !(node instanceof IASTNullStatement);
	}

	private void linkStatement(ICfgNode root, List<ICfgNode> stmList) {
		if (root == null || root.isVisited())
			return;
		root.setVisit(true);
		stmList.add(root);

		if (root.isMultipleTarget()) {
			for (ICfgNode target : root.getListTarget())
				linkStatement(target, stmList);
			return;
		}

		ICfgNode stmTrue = nextConcrete(root.getTrueNode());
		root.setTrue(stmTrue);

		ICfgNode stmFalse = nextConcrete(root.getFalseNode());
		root.setFalse(stmFalse);

		linkStatement(stmTrue, stmList);
		linkStatement(stmFalse, stmList);
	}

	private ICFG parse(IFunctionNode fn) throws Exception {
		BEGIN = new BeginFlagCfgNode();
		END = new EndFlagCfgNode();
		((EndFlagCfgNode) END).setBeginNode((BeginFlagCfgNode) BEGIN);
		labels = new HashMap<>();

		visitBlock((IASTCompoundStatement) fn.getAST().getBody(), BEGIN, END, null, null, END, BEGIN);

		ArrayList<ICfgNode> stmList = new ArrayList<>();
		linkStatement(BEGIN, stmList);

		for (ICfgNode stm : stmList)
			stm.setParent(nextConcrete(stm.getParent()));

		return new CFG(stmList);
	}

	private void visitBlock(IASTCompoundStatement block, ICfgNode begin, ICfgNode end, ICfgNode _break,
			ICfgNode _continue, ICfgNode _throw, ICfgNode parent) {
		IASTStatement[] childs = block.getStatements();

		if (childs.length == 0) {
			begin.setBranch(end);
			return;
		}

		ICfgNode scopeIn = ScopeCfgNode.newOpenScope();
		ICfgNode scopeOut = ScopeCfgNode.newCloseScope(end);
		ICfgNode[] points = new CfgNode[childs.length + 1];
		begin.setBranch(scopeIn);

		// Táº¡o cÃ¡c Ä‘iá»ƒm ná»‘i trung gian
		points[0] = scopeIn;
		for (int i = 1; i < childs.length; i++)
			points[i] = new ForwardCfgNode();
		points[childs.length] = scopeOut;

		for (int i = 0; i < childs.length; i++)
			visitStatement(childs[i], points[i], points[i + 1], _break, _continue, _throw, parent);
	}

	private void visitCondition(IASTExpression cond, ICfgNode begin, ICfgNode endTrue, ICfgNode endFalse,
			ICfgNode parent, int flag) {
		cond = normalize(cond);

		ICfgNode stmCond = null;
		switch (flag) {
		case DO_FLAG:
			stmCond = new ConditionDoCfgNode(normalize(cond));
			break;
		case FOR_FLAG:
			stmCond = new ConditionForCfgNode(normalize(cond));
			break;
		case WHILE_FLAG:
			stmCond = new ConditionWhileCfgNode(normalize(cond));
			break;
		case IF_FLAG:
			stmCond = new ConditionIfCfgNode(normalize(cond));
			break;
		}

		begin.setBranch(stmCond);
		stmCond.setTrue(endTrue);
		stmCond.setFalse(endFalse);
		stmCond.setParent(parent);
	}

	private void visitSimpleStatement(IASTStatement stm, ICfgNode begin, ICfgNode end, ICfgNode _throw,
			ICfgNode parent) {

		ICfgNode normal = null;

		if (isThrowStatement(stm)) {
			normal = new ThrowCfgNode(stm);
			normal.setBranch(_throw);

		} else if (stm instanceof IASTReturnStatement) {
			normal = new ReturnNode(stm);
			parseReturnStatement(normal, stm, END, BEGIN);

		} else if (stm.getRawSignature().matches("exit\\s*\\(.*") || stm.getRawSignature().matches("abort\\s*\\(.*")) {
			normal = new SimpleCfgNode(stm);
			normal.setBranch(END);

		} else {
			normal = new SimpleCfgNode(stm);
			parseSimpleStatement(normal, stm, end, BEGIN);
			// normal.setBranch(end);
		}

		begin.setBranch(normal);
		normal.setParent(parent);
	}

	private void parseReturnStatement(ICfgNode returnNode, IASTStatement stm, ICfgNode endOfCFG,
			ICfgNode beginningOfCfg) {
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
			if (functionNode.getName().startsWith(name)) {
				returnNode.setBranch(beginningOfCfg.getTrueNode());
				foundRecursive = true;
				break;
			}
		}

		if (!foundRecursive) {
			returnNode.setBranch(endOfCFG);
		}
	}

	private void parseSimpleStatement(ICfgNode simpleNode, IASTStatement stm, ICfgNode nextStatement,
			ICfgNode beginningOfCfg) {
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
			if (functionNode.getName().startsWith(name)) {
				simpleNode.setTrue(beginningOfCfg.getTrueNode());
				simpleNode.setFalse(nextStatement);
				foundRecursive = true;
				break;
			}
		}
		if (!foundRecursive) {
			simpleNode.setBranch(nextStatement);
		}
	}

	private void visitStatement(IASTStatement stm, ICfgNode begin, ICfgNode end, ICfgNode _break, ICfgNode _continue,
			ICfgNode _throw, ICfgNode parent) {
		if (stm instanceof IASTIfStatement) {
			IASTIfStatement stmIf = (IASTIfStatement) stm;
			IASTExpression astCond = null;
			astCond = stmIf.getConditionExpression();
			// Sometimes, statement "if(A){...}" can not detect that A is a
			// condition. In this case,
			// A is
			// CPPASTSimpleDeclaration
			if (astCond == null) {
				CPPASTSimpleDeclaration decl = (CPPASTSimpleDeclaration) stmIf.getChildren()[0];
				String newStm = decl.getRawSignature() + "==true";
				astCond = (IASTExpression) ASTUtils.convertToIAST(newStm);
			}
			//
			IASTStatement astThen = stmIf.getThenClause();
			IASTStatement astElse = stmIf.getElseClause();

			ICfgNode afterTrue = new ForwardCfgNode();
			ICfgNode afterFalse = new ForwardCfgNode();

			visitCondition(astCond, begin, afterTrue, afterFalse, parent, ICFGGeneration.IF_FLAG);

			visitStatement(astThen, afterTrue, end, _break, _continue, _throw, begin);

			visitStatement(astElse, afterFalse, end, _break, _continue, _throw, begin);
		} else if (!notNull(stm))
			begin.setBranch(end);

		else if (stm instanceof IASTForStatement) {

			IASTForStatement stmFor = (IASTForStatement) stm;
			IASTStatement astInit = stmFor.getInitializerStatement();
			IASTExpression astCond = stmFor.getConditionExpression();
			IASTExpression astIter = stmFor.getIterationExpression();
			IASTStatement astBody = stmFor.getBody();

			ICfgNode bfInit = new ForwardCfgNode(), bfCond = new ForwardCfgNode(), bfBody = new ForwardCfgNode();

			// Táº¡o scope áº£o cho trÆ°á»�ng há»£p cÃ³ khai bÃ¡o thÃªm biáº¿n
			// cháº¡y
			ICfgNode scopeIn = new AdditionalScopeCfgNode(ScopeCfgNode.SCOPE_OPEN);
			begin.setBranch(scopeIn);
			scopeIn.setParent(begin);
			scopeIn.setBranch(bfInit);
			bfInit.setParent(scopeIn);

			ICfgNode scopeOut = new AdditionalScopeCfgNode(ScopeCfgNode.SCOPE_CLOSE);
			scopeOut.setBranch(end);
			end.setParent(scopeOut);

			_continue = new ForwardCfgNode();// sau khi háº¿t pháº§n thÃ¢n
												// hoáº·c

			ForwardCfgNode bodyParent = new ForwardCfgNode();
			bodyParent.setBranch(bfCond);

			visitStatement(astInit, bfInit, bfCond, scopeOut, _continue, _throw, bodyParent);

			if (notNull(astCond))
				visitCondition(astCond, bfCond, bfBody, scopeOut, parent, ICFGGeneration.FOR_FLAG);
			else {
				bfCond.setBranch(bfBody);
				bodyParent.setBranch(parent);
			}

			visitStatement(astBody, bfBody, _continue, scopeOut, _continue, _throw, bodyParent);

			//
			if (bfCond.getTrueNode().getTrueNode() == _continue)
				throw new RuntimeException(
						"This FOR statement is infinity." + "No condition or body found: " + stmFor.getRawSignature());

			if (notNull(astIter)) {
				ICfgNode stmIter = new SimpleCfgNode(astIter);
				_continue.setBranch(stmIter);
				stmIter.setBranch(bfCond);
				stmIter.setParent(bodyParent);
			} else
				_continue.setBranch(bfCond);

		} else if (stm instanceof IASTWhileStatement) {
			IASTWhileStatement astWhile = (IASTWhileStatement) stm;
			IASTStatement astBody = astWhile.getBody();

			IASTExpression astCond = null;
			astCond = astWhile.getCondition();

			// Sometimes, statement "if(A){...}" can not detect that A is a
			// condition. In this case,
			// A is
			// CPPASTSimpleDeclaration
			if (astCond == null) {
				CPPASTSimpleDeclaration decl = (CPPASTSimpleDeclaration) astWhile.getChildren()[0];
				String newStm = decl.getRawSignature() + "==true";
				astCond = (IASTExpression) ASTUtils.convertToIAST(newStm);
			}

			//
			ICfgNode beforeCond = new ForwardCfgNode();
			ICfgNode afterCond = new ForwardCfgNode();

			begin.setBranch(beforeCond);
			visitCondition(astCond, beforeCond, afterCond, end, parent, ICFGGeneration.WHILE_FLAG);

			if (notNull(astBody))
				visitStatement(astBody, afterCond, beforeCond, end, beforeCond, _throw, beforeCond);
			else
				afterCond.setBranch(beforeCond);

		} else if (stm instanceof IASTDoStatement) {
			IASTDoStatement astDo = (IASTDoStatement) stm;
			IASTStatement astBody = astDo.getBody();

			ICfgNode beforeDo = new ForwardCfgNode();
			ICfgNode beforeCond = new ForwardCfgNode();

			begin.setBranch(beforeDo);
			_break = end;
			_continue = beforeCond;

			if (notNull(astBody))
				visitStatement(astBody, beforeDo, beforeCond, _break, _continue, _throw, beforeCond);
			else
				beforeDo.setBranch(beforeCond);

			visitCondition(astDo.getCondition(), beforeCond, beforeDo, _break, parent, ICFGGeneration.DO_FLAG);

		} else if (stm instanceof IASTSwitchStatement) {
			IASTSwitchStatement astSw = (IASTSwitchStatement) stm;
			ICfgNode scopeIn = ScopeCfgNode.newOpenScope();
			ICfgNode scopeOut = ScopeCfgNode.newCloseScope(end);
			begin.setBranch(scopeIn);
			visitSwitch(astSw.getControllerExpression(), (IASTCompoundStatement) astSw.getBody(), scopeIn, scopeOut,
					_continue, _throw, parent);

		} else if (stm instanceof IASTCompoundStatement)
			visitBlock((IASTCompoundStatement) stm, begin, end, _break, _continue, _throw, parent);

		else if (stm instanceof IASTBreakStatement) {
			ICfgNode breakNode = new BreakCfgNode(stm);
			begin.setBranch(breakNode);
			breakNode.setBranch(_break);
		}

		else if (stm instanceof IASTContinueStatement) {
			ICfgNode continueNode = new ContinueCfgNode(stm);
			begin.setBranch(continueNode);
			continueNode.setBranch(_continue);
		}

		else if (stm instanceof ICPPASTTryBlockStatement)
			visitTryCatch((ICPPASTTryBlockStatement) stm, begin, end, _break, _continue, _throw, parent);

		else if (stm instanceof IASTLabelStatement) {
			IASTLabelStatement stmLabel = (IASTLabelStatement) stm;
			String name = stmLabel.getName().toString();
			MarkFlagCfgNode ref = labels.get(name);

			if (ref == null) {
				ref = new MarkFlagCfgNode(name + ":");
				labels.put(name, ref);
			}
			ref.setAstLocation(stmLabel.getName().getFileLocation());
			ref.setParent(parent);

			begin.setBranch(ref);
			visitStatement(stmLabel.getNestedStatement(), ref, end, _break, _continue, _throw, ref);

		} else if (stm instanceof IASTGotoStatement) {
			String name = ((IASTGotoStatement) stm).getName().toString();
			MarkFlagCfgNode ref = labels.get(name);

			if (ref == null) {
				ref = new MarkFlagCfgNode(name + ":");
				labels.put(name, ref);
			}
			begin.setBranch(ref);

		} else
			visitSimpleStatement(stm, begin, end, _throw, parent);
	}

	private void visitSwitch(IASTExpression cond, IASTCompoundStatement body, ICfgNode begin, ICfgNode end,
			ICfgNode _continue, ICfgNode _throw, ICfgNode parent) {
		ArrayList<Pair> caseLink = new ArrayList<>();
		ArrayList<IASTCaseStatement> cases = new ArrayList<>();

		IASTStatement[] childs = body.getStatements();
		ICfgNode defaultPoint = null;
		ICfgNode before = new ForwardCfgNode(), after;
		int i = 0;

		while (i < childs.length) {
			IASTStatement stm = childs[i];

			if (stm instanceof IASTCaseStatement)
				cases.add((IASTCaseStatement) stm);
			else if (stm instanceof IASTDefaultStatement) {
				cases.clear();
				while (i + 1 < childs.length && childs[i + 1] instanceof IASTCaseStatement)
					i++;
				defaultPoint = before;
			} else {
				// Vá»«a má»›i ra khá»�i má»™t dÃ£y case
				if (cases.size() > 0) {
					caseLink.add(new Pair(cases, before));
					cases = new ArrayList<>();
				}

				after = new ForwardCfgNode();
				visitStatement(stm, before, after, end, _continue, _throw, parent);
				before = after;
			}
			i++;
		}
		before.setBranch(end);

		// NÃºt trÆ°á»›c khi báº¯t Ä‘áº§u default
		ICfgNode beforeDefault = new ForwardCfgNode();

		beforeDefault.setBranch(defaultPoint == null ? end : defaultPoint);
		if (caseLink.size() == 0) {
			begin.setBranch(beforeDefault);
			return;
		}

		ICfgNode[] mid = new CfgNode[caseLink.size() + 1];
		mid[0] = begin;
		for (i = 1; i < mid.length - 1; i++)
			mid[i] = new ForwardCfgNode();
		mid[i] = beforeDefault;

		for (i = 0; i < caseLink.size(); i++) {
			Pair p = caseLink.get(i);
			IASTExpression join = joinCaseSwitch(cond, p.getCases());
			visitCondition(join, mid[i], p.getStm(), mid[i + 1], parent, ICFGGeneration.IF_FLAG);
		}
	}

	private void visitTryCatch(ICPPASTTryBlockStatement stm, ICfgNode begin, ICfgNode end, ICfgNode _break,
			ICfgNode _continue, ICfgNode _throw, ICfgNode parent) {

		MarkFlagCfgNode startTry = new MarkFlagCfgNode("start try");
		MarkFlagCfgNode endCatch = new MarkFlagCfgNode("end catch");

		begin.setBranch(startTry);
		startTry.setParent(parent);
		endCatch.setParent(startTry);
		endCatch.setBranch(end);
		ICfgNode catchEntry = new ForwardCfgNode();
		visitStatement(stm.getTryBody(), startTry, catchEntry, _break, _continue, catchEntry, startTry);
		catchEntry.setBranch(_throw);

		for (ICPPASTCatchHandler catcher : stm.getCatchHandlers()) {
			ICfgNode label;

			if (catcher.isCatchAll()) {
				label = new MarkFlagCfgNode("catch (...)");
				label.setAstLocation(catcher.getFileLocation());
			} else {
				label = new MarkFlagCfgNode(String.format("catch (%s)", catcher.getDeclaration().getRawSignature()));
				label.setAstLocation(catcher.getDeclaration().getFileLocation());
			}

			label.setParent(startTry);
			catchEntry.setBranch(label);

			if (catcher.isCatchAll()) {
				visitStatement(catcher.getCatchBody(), label, endCatch, _break, _continue, _throw, label);
				break;
			} else {
				ICfgNode labelTrue = new ForwardCfgNode();
				label.setTrue(labelTrue);
				visitStatement(catcher.getCatchBody(), labelTrue, endCatch, _break, _continue, _throw, label);

				catchEntry = new ForwardCfgNode();
				catchEntry.setBranch(_throw);
				label.setFalse(catchEntry);
			}
		}
	}

	@Override
	public IFunctionNode getFunctionNode() {
		return functionNode;

	}

	@Override
	public void setFunctionNode(IFunctionNode functionNode) {
		this.functionNode = functionNode;
	}

	class Pair {

		ArrayList<IASTCaseStatement> cases;
		ICfgNode stm;

		public Pair(ArrayList<IASTCaseStatement> cases, ICfgNode stm) {
			this.cases = cases;
			this.stm = stm;
		}

		public Pair() {
		}

		public ArrayList<IASTCaseStatement> getCases() {
			return cases;
		}

		public void setCases(ArrayList<IASTCaseStatement> cases) {
			this.cases = cases;
		}

		public ICfgNode getStm() {
			return stm;
		}

		public void setStm(ICfgNode stm) {
			this.stm = stm;
		}

	}

}
