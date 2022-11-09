package normalizer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUnaryExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTBinaryExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIdExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTUnaryExpression;

import cfg.CFGGenerationforBranchvsStatementCoverage;
import cfg.object.ConditionCfgNode;
import cfg.object.ICfgNode;
import cfg.object.NormalCfgNode;
import cfg.object.SimpleCfgNode;
import cfg.testpath.IFullTestpath;
import cfg.testpath.INormalizedTestpath;
import cfg.testpath.IPartialTestpath;
import cfg.testpath.ITestpathInCFG;
import cfg.testpath.NormalizedTestpath;
import cfg.testpath.PossibleTestpathGeneration;
import config.Paths;
import interfaces.INormalizer;
import parser.projectparser.ProjectParser;
import tree.object.FunctionNode;
import tree.object.IFunctionNode;
import utils.ASTUtils;
import utils.IRegex;
import utils.Utils;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

/**
 * Rewrite the test path into a corresponding test path
 * <p>
 * <p>
 * Ex: "int a = ++b;" -----------> "++b; int a= b;"
 *
 * @author ducanhnguyen
 */
public class UnaryNormalizer extends AbstractNormalizer implements ITestpathNormalizer {

	private ITestpathInCFG originalTestpath;

	private INormalizedTestpath normalizedTestpath;

	public UnaryNormalizer() {
	}

	public static void main(String[] args) throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.SYMBOLIC_EXECUTION_TEST));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "concatenate_string(char*,char*)")
				.get(0);
		System.out.println(function.getAST().getRawSignature());
		/*
		 * Normalize function
		 */
		FunctionNormalizer fnNormalizer = function.getGeneralNormalizationFunction();
		String newFunctionInStr = fnNormalizer.getNormalizedSourcecode();
		ICPPASTFunctionDefinition newAST = Utils.getFunctionsinAST(newFunctionInStr.toCharArray()).get(0);
		((FunctionNode) function).setAST(newAST);

		/*
		 * Generate CFG of the normalized functionF
		 */
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(function);

		/*
		 * Choose a random test path to test
		 */

		PossibleTestpathGeneration tpGen = new PossibleTestpathGeneration(cfgGen.generateCFG(), 1);
		tpGen.generateTestpaths();
		ITestpathInCFG specialTestpath = tpGen.getPossibleTestpaths().get(0);
		System.out.println(specialTestpath);

		System.out.println(new UnaryNormalizer().rewrite(specialTestpath).toReducedString());
	}

	@Override
	public void normalize() {
		if (originalTestpath != null)
			normalizedTestpath = rewrite(originalTestpath);
	}

	/**
	 * Rewrite the test path into a corresponding test path
	 * <p>
	 * The content of condition is <b>negated automatically </b>its the
	 * statements after it belongs to the false branch
	 *
	 * @param tp
	 *            The input test path
	 * @return
	 */
	private INormalizedTestpath rewrite(ITestpathInCFG tp) {
		INormalizedTestpath newTestpath = new NormalizedTestpath();
		newTestpath.setFunctionNode(tp.getFunctionNode());

		int last = tp.cast().size() - 1;
		for (int currentPosition = 0; currentPosition < last; currentPosition++) {
			ICfgNode currentCfgNode = tp.cast().get(currentPosition);

			if (currentCfgNode instanceof NormalCfgNode) {
				/*
				 * Get the state of the next expression
				 */
				boolean nextIsTrue = false;
				if (currentCfgNode instanceof ConditionCfgNode)
					nextIsTrue = tp.nextIsTrueBranch(currentCfgNode, currentPosition);

				parseNormalNode((NormalCfgNode) currentCfgNode, newTestpath, nextIsTrue);
			} else
				newTestpath.cast().add(currentCfgNode);
		}

		// for the final node
		if (last >= 0) {
			ICfgNode lastCfgNode = tp.cast().get(last);

			if (lastCfgNode instanceof NormalCfgNode) {
				/*
				 * Get the state of the next expression
				 */
				boolean nextIsTrue = false;
				if (tp instanceof IFullTestpath) {
					if (lastCfgNode instanceof ConditionCfgNode)
						nextIsTrue = tp.nextIsTrueBranch(lastCfgNode, last);

				} else if (tp instanceof IPartialTestpath)
					nextIsTrue = ((IPartialTestpath) tp).getFinalConditionType();

				parseNormalNode((NormalCfgNode) lastCfgNode, newTestpath, nextIsTrue);
			} else
				newTestpath.cast().add(lastCfgNode);
		}
		return newTestpath;
	}

	private void parseNormalNode(NormalCfgNode currentCfgNode, ITestpathInCFG newTestpath, boolean nextIsTrue) {
		List<NormalCfgNode> runFirst = new ArrayList<>();
		List<NormalCfgNode> runLast = new ArrayList<>();

		ShortenIncreaseDecreaseNormalizer shortenNorm = new ShortenIncreaseDecreaseNormalizer();
		shortenNorm.setOriginalSourcecode(currentCfgNode.getContent());
		shortenNorm.normalize();

		// Create before statement
		List<String> pre = shortenNorm.getPre();
		for (String newStm : pre)
			runFirst.add(new SimpleCfgNode(ASTUtils.convertToIAST(newStm)));

		// Create after statement
		List<String> after = shortenNorm.getAfter();
		for (String newStm : after)
			runLast.add(new SimpleCfgNode(ASTUtils.convertToIAST(newStm)));

		// Normalize the current statement
		NormalCfgNode newNormalNode;
		try {
			newNormalNode = (NormalCfgNode) currentCfgNode.clone();

			if (currentCfgNode instanceof ConditionCfgNode) {
				normalizeCondition(newNormalNode, nextIsTrue);
			} else if (!(Utils.shortenAstNode(newNormalNode.getAst()) instanceof ICPPASTUnaryExpression))
				// dont handle statement "p1[0][0]++"
				normalizeAssignment(newNormalNode);

			// Finally, add new expressions in specified order
			for (NormalCfgNode n : runFirst)
				newTestpath.cast().add(n);

			newTestpath.cast().add(newNormalNode);

			for (NormalCfgNode n : runLast)
				newTestpath.cast().add(n);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	private void normalizeAssignment(NormalCfgNode newConditionNode) {
		String newContent = newConditionNode.getAst().getRawSignature();
		newContent = newContent.replace("++", "").replace("--", "");
		newConditionNode.setAst(ASTUtils.convertToIAST(newContent));
	}

	/**
	 * Rewrite the current expression. If the next statement belong to false
	 * branch, the condition is negated!
	 *
	 * @param newConditionNode
	 * @param nextIsTrue
	 */
	private void normalizeCondition(NormalCfgNode newConditionNode, boolean nextIsTrue) {
		normalizeSingleBoolVariable(newConditionNode);
		String newContent = newConditionNode.getAst().getRawSignature();

		newContent = newContent.replace("++", "").replace("--", "");

		/*
		 * If the condition is bool variable
		 * 
		 * Ex: "a" ----> "a==1"/"a==0"
		 * 
		 * Ex: "!a" ----> "a==0"/"a==1"
		 */
		if (!nextIsTrue) {
			if (newContent.matches(IRegex.NAME_REGEX))
				newContent = newContent + " == 0";

			else if (newContent.matches("!\\s*" + IRegex.NAME_REGEX))
				newContent = newContent.substring(1) + " == 1";

			else if (newContent.matches(IRegex.ARRAY_ITEM))
				newContent = newContent + " == 0";

			else if (newContent.matches("!\\s*" + IRegex.ARRAY_ITEM))
				newContent = newContent.substring(1) + " == 1";

			else
			// case pointer, ex: while (*ptr)
			if (newContent.matches("\\*" + IRegex.NAME_REGEX))
				newContent = newContent.substring(1) + " == 0";

			else
				newContent = "!(" + newContent + ")";

		} else if (newContent.matches(IRegex.NAME_REGEX))
			newContent = newContent + " == 1";

		else if (newContent.matches("!\\s*" + IRegex.NAME_REGEX))
			newContent = newContent.substring(1) + " == 0";

		else if (newContent.matches(IRegex.ARRAY_ITEM))
			newContent = newContent + " == 1";

		else if (newContent.matches("!\\s*" + IRegex.ARRAY_ITEM))
			newContent = newContent.substring(1) + " == 0";

		else
		// case pointer, ex: while (*ptr)
		if (newContent.matches("\\*" + IRegex.NAME_REGEX))
			newContent = newContent.substring(1) + " != 0";
		NullStatementNormalizer nullNorm = new NullStatementNormalizer();
		nullNorm.setOriginalSourcecode(newContent);
		nullNorm.normalize();
		newContent = nullNorm.getNormalizedSourcecode();

		newConditionNode.setAst(ASTUtils.convertToIAST(newContent));
	}

	/**
	 * Normlize single bool variable with fully condition. <br/>
	 * Ex:"(less && x > 0)"-------------->"(less==1 && x > 0)"
	 *
	 * @param newConditionNode
	 */
	private void normalizeSingleBoolVariable(NormalCfgNode newConditionNode) {
		String condition = newConditionNode.getAst().getRawSignature();
		/*
		 * Get all tokens as id and array item
		 */
		List<CPPASTIdExpression> ids = Utils.getIds(newConditionNode.getAst());
		List<ICPPASTArraySubscriptExpression> arrayItems = Utils.getArraySubscriptExpression(newConditionNode.getAst());

		/*
		 * Add tokens to the list. Some redudant tokens are removed, i.g., token
		 * "a" in array item "a[1]"
		 */
		List<IASTNode> var = new ArrayList<>();
		for (CPPASTIdExpression n : ids)
			if (!(n.getParent() instanceof ICPPASTArraySubscriptExpression))
				var.add(n);
		for (ICPPASTArraySubscriptExpression n : arrayItems)
			var.add(n);

		/*
		 * Build the rule to detect a token is bool variable or not
		 */
		for (IASTNode id : var)
			if (id.getParent() != null)
				if (id.getParent() instanceof CPPASTBinaryExpression) {
					// Ex:"(less && x > 0)"-------------->"(less==1 && x > 0)"
					CPPASTBinaryExpression parent = (CPPASTBinaryExpression) id.getParent();
					switch (parent.getOperator()) {
					case IASTBinaryExpression.op_logicalAnd:
					case IASTBinaryExpression.op_logicalOr:
						condition = condition.replaceAll(Utils.toRegex(id.getRawSignature()),
								id.getRawSignature() + "==1");
						break;

					default:
						break;
					}
				} else if (id.getParent() instanceof CPPASTUnaryExpression) {
					CPPASTUnaryExpression parent = (CPPASTUnaryExpression) id.getParent();
					switch (parent.getOperator()) {
					case IASTUnaryExpression.op_not:
						// Ex:"(!less && x > 0)"-------------->"(less==0 && x >
						// 0)"
						condition = condition.replaceAll(Utils.toRegex(parent.getRawSignature()),
								id.getRawSignature() + "==0");
						break;
					case IASTUnaryExpression.op_bracketedPrimary:
						condition = condition.replaceAll(Utils.toRegex(id.getRawSignature()),
								id.getRawSignature() + "==1");
						break;
					default:

						break;
					}
				}
		newConditionNode.setAst(ASTUtils.convertToIAST(condition));
	}

	public ITestpathInCFG getOriginalTestpath() {
		return originalTestpath;
	}

	public void setOriginalTestpath(ITestpathInCFG tp) {
		originalTestpath = tp;
	}

	public INormalizedTestpath getNormalizedTestpath() {
		return normalizedTestpath;
	}

	@Override
	@Deprecated
	public String getNormalizedSourcecode() {
		if (normalizedTestpath != null)
			return normalizedTestpath.toString();
		else
			return INormalizer.ERROR;
	}

	@Override
	@Deprecated
	public void setNormalizedSourcecode(String normalizeSourcecode) {
		super.setNormalizedSourcecode(normalizeSourcecode);
	}

	@Override
	@Deprecated
	public String getOriginalSourcecode() {
		if (originalTestpath != null)
			return originalTestpath.toString();
		else
			return INormalizer.ERROR;
	}

	@Override
	@Deprecated
	public void setOriginalSourcecode(String originalSourcecode) {
		super.setOriginalSourcecode(originalSourcecode);
	}
}
