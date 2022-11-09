package cfg;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import cfg.object.BeginFlagCfgNode;
import cfg.object.ConditionCfgNode;
import cfg.object.EndFlagCfgNode;
import cfg.object.ICfgNode;
import config.Paths;
import parser.projectparser.ProjectParser;
import tree.object.IFunctionNode;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

/**
 * Test cfg generation for branch/statement coverage
 *
 * @author ducanhnguyen
 */
public class CFGGenerationforBranchvsStatementCoverageTest {
	@Test
	public void testSimple() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.CFG_GENERATION));
		IFunctionNode function = (IFunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
				"branchvsstatement.cpp\\testSimple(int)").get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(function);
		ICFG cfg = cfgGen.generateCFG();
		cfg.setIdforAllNodes();

		Assert.assertEquals(true, isExistEdge(BeginFlagCfgNode.BEGIN_FLAG, "{", cfg));
		Assert.assertEquals(true, isExistEdge("{", "a = 0;", cfg));
		Assert.assertEquals(true, isExistEdge("a = 0;", "}", cfg));
		Assert.assertEquals(true, isExistEdge("}", EndFlagCfgNode.END_FLAG, cfg));
	}

	@Test
	public void testIf() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.CFG_GENERATION));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "branchvsstatement.cpp\\testIf(int)")
				.get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(function);
		ICFG cfg = cfgGen.generateCFG();
		cfg.setIdforAllNodes();

		Assert.assertEquals(true, isExistEdge(BeginFlagCfgNode.BEGIN_FLAG, "{", cfg));
		Assert.assertEquals(true, isExistEdge("{", "a>0", cfg));
		Assert.assertEquals(true, isExistEdge("a>0", "a++;", true, cfg));
		Assert.assertEquals(true, isExistEdge("a>0", "}", false, cfg));
		Assert.assertEquals(true, isExistEdge("}", EndFlagCfgNode.END_FLAG, cfg));
	}

	@Test
	public void testIf_CompoundClause() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.CFG_GENERATION));
		IFunctionNode function = (IFunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
				"branchvsstatement.cpp\\testIf_CompoundBody(int)").get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(function);
		ICFG cfg = cfgGen.generateCFG();
		cfg.setIdforAllNodes();

		Assert.assertEquals(true, isExistEdge(BeginFlagCfgNode.BEGIN_FLAG, "{", cfg));
		Assert.assertEquals(true, isExistEdge("{", "a>0", cfg));
		Assert.assertEquals(true, isExistEdge("a>0", "{", true, cfg));
		Assert.assertEquals(true, isExistEdge("a>0", "}", false, cfg));
		Assert.assertEquals(true, isExistEdge("{", "a++;", cfg));
		Assert.assertEquals(true, isExistEdge("a++;", "}", cfg));
		Assert.assertEquals(true, isExistEdge("}", "}", cfg));
		Assert.assertEquals(true, isExistEdge("}", EndFlagCfgNode.END_FLAG, cfg));
	}

	@Test
	public void testIfElse() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.CFG_GENERATION));
		IFunctionNode function = (IFunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
				"branchvsstatement.cpp\\testIfElse(int)").get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(function);
		ICFG cfg = cfgGen.generateCFG();
		cfg.setIdforAllNodes();

		Assert.assertEquals(true, isExistEdge(BeginFlagCfgNode.BEGIN_FLAG, "{", cfg));
		Assert.assertEquals(true, isExistEdge("{", "a>0", cfg));
		Assert.assertEquals(true, isExistEdge("a>0", "a++;", true, cfg));
		Assert.assertEquals(true, isExistEdge("a>0", "a--;", false, cfg));
		Assert.assertEquals(true, isExistEdge("a++;", "}", false, cfg));
		Assert.assertEquals(true, isExistEdge("a--;", "}", false, cfg));
		Assert.assertEquals(true, isExistEdge("}", EndFlagCfgNode.END_FLAG, cfg));
	}

	@Test
	public void testIfElseIf() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.CFG_GENERATION));
		IFunctionNode function = (IFunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
				"branchvsstatement.cpp\\testIfElseIf(int)").get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(function);
		ICFG cfg = cfgGen.generateCFG();
		cfg.setIdforAllNodes();

		Assert.assertEquals(true, isExistEdge(BeginFlagCfgNode.BEGIN_FLAG, "{", cfg));
		Assert.assertEquals(true, isExistEdge("{", "a>0", cfg));
		Assert.assertEquals(true, isExistEdge("a>0", "a++;", true, cfg));
		Assert.assertEquals(true, isExistEdge("a>0", "a<0", false, cfg));
		Assert.assertEquals(true, isExistEdge("a++;", "}", false, cfg));
		Assert.assertEquals(true, isExistEdge("a<0", "a--;", true, cfg));
		Assert.assertEquals(true, isExistEdge("a<0", "}", false, cfg));
		Assert.assertEquals(true, isExistEdge("}", EndFlagCfgNode.END_FLAG, cfg));
	}

	@Test
	public void testDo() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.CFG_GENERATION));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "branchvsstatement.cpp\\testDo(int)")
				.get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(function);
		ICFG cfg = cfgGen.generateCFG();
		cfg.setIdforAllNodes();

		Assert.assertEquals(true, isExistEdge(BeginFlagCfgNode.BEGIN_FLAG, "{", cfg));
		Assert.assertEquals(true, isExistEdge("{", "{", cfg));
		Assert.assertEquals(true, isExistEdge("{", "a++;", cfg));
		Assert.assertEquals(true, isExistEdge("a++;", "}", cfg));
		Assert.assertEquals(true, isExistEdge("}", "a>0", cfg));
		Assert.assertEquals(true, isExistEdge("a>0", "{", true, cfg));
		Assert.assertEquals(true, isExistEdge("a>0", "}", false, cfg));
		Assert.assertEquals(true, isExistEdge("}", EndFlagCfgNode.END_FLAG, cfg));
	}

	@Test
	public void testWhile() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.CFG_GENERATION));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "branchvsstatement.cpp\\testWhile(int)")
				.get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(function);
		ICFG cfg = cfgGen.generateCFG();
		cfg.setIdforAllNodes();

		Assert.assertEquals(true, isExistEdge(BeginFlagCfgNode.BEGIN_FLAG, "{", cfg));
		Assert.assertEquals(true, isExistEdge("{", "a>0", cfg));
		Assert.assertEquals(true, isExistEdge("a>0", "a++;", true, cfg));
		Assert.assertEquals(true, isExistEdge("a>0", "}", false, cfg));
		Assert.assertEquals(true, isExistEdge("a++;", "a>0", cfg));
		Assert.assertEquals(true, isExistEdge("}", EndFlagCfgNode.END_FLAG, cfg));
	}

	@Test
	public void testWhile_CompoundBody() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.CFG_GENERATION));
		IFunctionNode function = (IFunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
				"branchvsstatement.cpp\\testWhile_CompoundBody(int)").get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(function);
		ICFG cfg = cfgGen.generateCFG();
		cfg.setIdforAllNodes();

		Assert.assertEquals(true, isExistEdge(BeginFlagCfgNode.BEGIN_FLAG, "{", cfg));
		Assert.assertEquals(true, isExistEdge("{", "a>0", cfg));
		Assert.assertEquals(true, isExistEdge("a>0", "{", true, cfg));
		Assert.assertEquals(true, isExistEdge("a>0", "}", false, cfg));
		Assert.assertEquals(true, isExistEdge("{", "a++;", cfg));
		Assert.assertEquals(true, isExistEdge("a++;", "}", cfg));
		Assert.assertEquals(true, isExistEdge("}", "a>0", cfg));
		Assert.assertEquals(true, isExistEdge("}", EndFlagCfgNode.END_FLAG, cfg));
	}

	@Test
	public void testFor() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.CFG_GENERATION));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "branchvsstatement.cpp\\testFor(int)")
				.get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(function);
		ICFG cfg = cfgGen.generateCFG();
		cfg.setIdforAllNodes();

		Assert.assertEquals(true, isExistEdge(BeginFlagCfgNode.BEGIN_FLAG, "{", cfg));
		Assert.assertEquals(true, isExistEdge("{", "int i = 0;", cfg));
		Assert.assertEquals(true, isExistEdge("int i = 0;", "i < 10", cfg));
		Assert.assertEquals(true, isExistEdge("i < 10", "a++;", true, cfg));
		Assert.assertEquals(true, isExistEdge("i < 10", "}", false, cfg));
		Assert.assertEquals(true, isExistEdge("a++;", "i++", cfg));
		Assert.assertEquals(true, isExistEdge("i++", "i < 10", cfg));
		Assert.assertEquals(true, isExistEdge("}", EndFlagCfgNode.END_FLAG, cfg));
	}

	@Test
	public void testFor_CompoundBody() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.CFG_GENERATION));
		IFunctionNode function = (IFunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
				"branchvsstatement.cpp\\testFor_CompoundBody(int)").get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(function);
		ICFG cfg = cfgGen.generateCFG();
		cfg.setIdforAllNodes();

		Assert.assertEquals(true, isExistEdge(BeginFlagCfgNode.BEGIN_FLAG, "{", cfg));
		Assert.assertEquals(true, isExistEdge("{", "int i = 0;", cfg));
		Assert.assertEquals(true, isExistEdge("int i = 0;", "i < 10", cfg));
		Assert.assertEquals(true, isExistEdge("i < 10", "{", true, cfg));
		Assert.assertEquals(true, isExistEdge("i < 10", "}", false, cfg));
		Assert.assertEquals(true, isExistEdge("{", "a++;", cfg));
		Assert.assertEquals(true, isExistEdge("a++;", "}", cfg));
		Assert.assertEquals(true, isExistEdge("}", "i++", cfg));
		Assert.assertEquals(true, isExistEdge("i++", "i < 10", cfg));
		Assert.assertEquals(true, isExistEdge("}", EndFlagCfgNode.END_FLAG, cfg));
	}

	@Test
	public void testBreak() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.CFG_GENERATION));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "branchvsstatement.cpp\\testBreak(int)")
				.get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(function);
		ICFG cfg = cfgGen.generateCFG();
		cfg.setIdforAllNodes();

		Assert.assertEquals(true, isExistEdge(BeginFlagCfgNode.BEGIN_FLAG, "{", cfg));
		Assert.assertEquals(true, isExistEdge("{", "{", cfg));
		Assert.assertEquals(true, isExistEdge("{", "a==2", cfg));
		Assert.assertEquals(true, isExistEdge("a==2", "break;", true, cfg));
		Assert.assertEquals(true, isExistEdge("a==2", "}", false, cfg));
		Assert.assertEquals(true, isExistEdge("break;", "a--;", cfg));
		Assert.assertEquals(true, isExistEdge("}", "a++>0", cfg));
		Assert.assertEquals(true, isExistEdge("a++>0", "{", true, cfg));
		Assert.assertEquals(true, isExistEdge("a++>0", "a--;", false, cfg));
		Assert.assertEquals(true, isExistEdge("a--;", "}", false, cfg));
		Assert.assertEquals(true, isExistEdge("}", EndFlagCfgNode.END_FLAG, cfg));
	}

	@Test
	public void testBreak_CompoundBody() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.CFG_GENERATION));
		IFunctionNode function = (IFunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
				"branchvsstatement.cpp\\testBreak_CompoundBody(int)").get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(function);
		ICFG cfg = cfgGen.generateCFG();
		cfg.setIdforAllNodes();

		Assert.assertEquals(true, isExistEdge(BeginFlagCfgNode.BEGIN_FLAG, "{", cfg));
		Assert.assertEquals(true, isExistEdge("{", "{", cfg));
		Assert.assertEquals(true, isExistEdge("{", "a==2", cfg));
		Assert.assertEquals(true, isExistEdge("a==2", "{", true, cfg));
		Assert.assertEquals(true, isExistEdge("a==2", "}", false, cfg));
		Assert.assertEquals(true, isExistEdge("{", "break;", cfg));
		Assert.assertEquals(true, isExistEdge("break;", "a--;", cfg));
		Assert.assertEquals(true, isExistEdge("}", "a++>0", cfg));
		Assert.assertEquals(true, isExistEdge("a++>0", "{", true, cfg));
		Assert.assertEquals(true, isExistEdge("a++>0", "a--;", false, cfg));
		Assert.assertEquals(true, isExistEdge("a--;", "}", false, cfg));
		Assert.assertEquals(true, isExistEdge("}", EndFlagCfgNode.END_FLAG, cfg));
	}

	@Test
	public void testContinue() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.CFG_GENERATION));
		IFunctionNode function = (IFunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
				"branchvsstatement.cpp\\testContinue(int)").get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(function);
		ICFG cfg = cfgGen.generateCFG();
		cfg.setIdforAllNodes();

		Assert.assertEquals(true, isExistEdge(BeginFlagCfgNode.BEGIN_FLAG, "{", cfg));
		Assert.assertEquals(true, isExistEdge("{", "{", cfg));
		Assert.assertEquals(true, isExistEdge("{", "a==2", cfg));
		Assert.assertEquals(true, isExistEdge("a==2", "continue;", true, cfg));
		Assert.assertEquals(true, isExistEdge("a==2", "}", false, cfg));
		Assert.assertEquals(true, isExistEdge("continue;", "a++>0", cfg));
		Assert.assertEquals(true, isExistEdge("}", "a++>0", cfg));
		Assert.assertEquals(true, isExistEdge("a++>0", "{", true, cfg));
		Assert.assertEquals(true, isExistEdge("a++>0", "a--;", false, cfg));
		Assert.assertEquals(true, isExistEdge("a--;", "}", false, cfg));
		Assert.assertEquals(true, isExistEdge("}", EndFlagCfgNode.END_FLAG, cfg));
	}
	@Test
	public void testContinue_CompoundBody() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.CFG_GENERATION));
		IFunctionNode function = (IFunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
				"branchvsstatement.cpp\\testContinue_CompoundBody(int)").get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(function);
		ICFG cfg = cfgGen.generateCFG();
		cfg.setIdforAllNodes();

		Assert.assertEquals(true, isExistEdge(BeginFlagCfgNode.BEGIN_FLAG, "{", cfg));
		Assert.assertEquals(true, isExistEdge("{", "{", cfg));
		Assert.assertEquals(true, isExistEdge("{", "a==2", cfg));
		Assert.assertEquals(true, isExistEdge("a==2", "{", true, cfg));
		Assert.assertEquals(true, isExistEdge("a==2", "}", false, cfg));
		Assert.assertEquals(true, isExistEdge("{", "continue;", true, cfg));;
		Assert.assertEquals(true, isExistEdge("continue;", "a++>0", cfg));
		Assert.assertEquals(true, isExistEdge("}", "a++>0", cfg));
		Assert.assertEquals(true, isExistEdge("a++>0", "{", true, cfg));
		Assert.assertEquals(true, isExistEdge("a++>0", "a--;", false, cfg));
		Assert.assertEquals(true, isExistEdge("a--;", "}", false, cfg));
		Assert.assertEquals(true, isExistEdge("}", EndFlagCfgNode.END_FLAG, cfg));
	}
	/**
	 * Check whether exist edge from two statements
	 *
	 * @param stm1
	 *            Content of statement 1
	 * @param stm2
	 *            Content of statement 2
	 * @param kindofEdge
	 *            true/false
	 * @param cfg
	 * @return
	 */
	private boolean isExistEdge(String stm1, String stm2, boolean kindofEdge, ICFG cfg) {
		boolean isExist = false;
		for (ICfgNode stm : cfg.getAllNodes())
			if (stm.getContent().equals(stm1))
				if (stm instanceof ConditionCfgNode) {
					if (kindofEdge && ((ConditionCfgNode) stm).getTrueNode().getContent().equals(stm2))
						isExist = true;
					else if (!kindofEdge && ((ConditionCfgNode) stm).getFalseNode().getContent().equals(stm2))
						isExist = true;
				} else {
					if (kindofEdge && stm.getTrueNode().getContent().equals(stm2))
						isExist = true;
					else if (!kindofEdge && (stm.getFalseNode().getContent().equals(stm2)))
						isExist = true;
				}
		return isExist;
	}

	private boolean isExistEdge(String stm1, String stm2, ICFG cfg) {
		return isExistEdge(stm1, stm2, true, cfg) && isExistEdge(stm1, stm2, false, cfg);
	}
}
