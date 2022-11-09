package cfg;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import cfg.object.ConditionCfgNode;
import cfg.object.ICfgNode;
import cfg.object.SimpleCfgNode;
import config.Paths;
import parser.projectparser.ProjectParser;
import tree.object.IFunctionNode;
import utils.Utils;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

/**
 * Test cfg generation for sub-condition
 *
 * @author ducanhnguyen
 */
public class CFGGenerationforSubConditionCoverageTest {
	@Test
	public void test00() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Utils.normalizePath(Paths.CFG_GENERATION)));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "testSubcondition0(int,int)").get(0);

		CFGGenerationforSubConditionCoverage cfgGen = new CFGGenerationforSubConditionCoverage(function);
		ICFG cfg = cfgGen.generateCFG();
		Assert.assertEquals(true, isExistEdge("a>1", "b>1", true, cfg));
		Assert.assertEquals(true, isExistEdge("a>1", "return 1;", false, cfg));
		Assert.assertEquals(true, isExistEdge("b>1", "return 0;", true, cfg));
		Assert.assertEquals(true, isExistEdge("b>1", "return 1;", false, cfg));
	}

	@Test
	public void test01() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Utils.normalizePath(Paths.CFG_GENERATION)));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "testSubcondition1(int,int)").get(0);

		CFGGenerationforSubConditionCoverage cfgGen = new CFGGenerationforSubConditionCoverage(function);
		ICFG cfg = cfgGen.generateCFG();
		Assert.assertEquals(true, isExistEdge("a>1", "return 0;", true, cfg));
		Assert.assertEquals(true, isExistEdge("a>1", "b>1", false, cfg));
		Assert.assertEquals(true, isExistEdge("b>1", "return 0;", true, cfg));
		Assert.assertEquals(true, isExistEdge("b>1", "return 1;", false, cfg));
	}

	@Test
	public void test02() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Utils.normalizePath(Paths.CFG_GENERATION)));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "testSubcondition2(int,int,int)")
				.get(0);

		CFGGenerationforSubConditionCoverage cfgGen = new CFGGenerationforSubConditionCoverage(function);
		ICFG cfg = cfgGen.generateCFG();
		Assert.assertEquals(true, isExistEdge("a>1", "return 0;", true, cfg));
		Assert.assertEquals(true, isExistEdge("a>1", "b>1", false, cfg));
		Assert.assertEquals(true, isExistEdge("b>1", "return 0;", true, cfg));
		Assert.assertEquals(true, isExistEdge("b>1", "c>1", false, cfg));
		Assert.assertEquals(true, isExistEdge("c>1", "return 0;", true, cfg));
		Assert.assertEquals(true, isExistEdge("c>1", "return 1;", false, cfg));
	}

	@Test
	public void test03() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Utils.normalizePath(Paths.CFG_GENERATION)));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "testSubcondition3(int,int,int)")
				.get(0);

		CFGGenerationforSubConditionCoverage cfgGen = new CFGGenerationforSubConditionCoverage(function);
		ICFG cfg = cfgGen.generateCFG();
		Assert.assertEquals(true, isExistEdge("a>1", "b>1", true, cfg));
		Assert.assertEquals(true, isExistEdge("a>1", "return 1;", false, cfg));
		Assert.assertEquals(true, isExistEdge("b>1", "c>1", true, cfg));
		Assert.assertEquals(true, isExistEdge("b>1", "return 1;", false, cfg));
		Assert.assertEquals(true, isExistEdge("c>1", "return 0;", true, cfg));
		Assert.assertEquals(true, isExistEdge("c>1", "return 1;", false, cfg));
	}

	@Test
	public void test04() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Utils.normalizePath(Paths.CFG_GENERATION)));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "testSubcondition4(int,int,int)")
				.get(0);

		CFGGenerationforSubConditionCoverage cfgGen = new CFGGenerationforSubConditionCoverage(function);
		ICFG cfg = cfgGen.generateCFG();
		Assert.assertEquals(true, isExistEdge("a>1", "b>1", true, cfg));
		Assert.assertEquals(true, isExistEdge("a>1", "return 1;", false, cfg));
		Assert.assertEquals(true, isExistEdge("b>1", "return 0;", true, cfg));
		Assert.assertEquals(true, isExistEdge("b>1", "c>1", false, cfg));
		Assert.assertEquals(true, isExistEdge("c>1", "return 0;", true, cfg));
		Assert.assertEquals(true, isExistEdge("c>1", "return 1;", false, cfg));
	}

	@Test
	public void test05() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Utils.normalizePath(Paths.CFG_GENERATION)));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "testSubcondition5(int,int,int)")
				.get(0);

		CFGGenerationforSubConditionCoverage cfgGen = new CFGGenerationforSubConditionCoverage(function);
		ICFG cfg = cfgGen.generateCFG();
		Assert.assertEquals(true, isExistEdge("a>1", "return 0;", true, cfg));
		Assert.assertEquals(true, isExistEdge("a>1", "b>1", false, cfg));
		Assert.assertEquals(true, isExistEdge("b>1", "c>1", true, cfg));
		Assert.assertEquals(true, isExistEdge("b>1", "return 1;", false, cfg));
		Assert.assertEquals(true, isExistEdge("c>1", "return 0;", true, cfg));
		Assert.assertEquals(true, isExistEdge("c>1", "return 1;", false, cfg));
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
				} else if (stm instanceof SimpleCfgNode) {
					if (kindofEdge && ((SimpleCfgNode) stm).getTrueNode().getContent().equals(stm2))
						isExist = true;
					else if (!kindofEdge && ((SimpleCfgNode) stm).getFalseNode().getContent().equals(stm2))
						isExist = true;
				}
		return isExist;
	}
}
