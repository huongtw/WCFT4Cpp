package cfg.testpath;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import cfg.CFGGenerationforBranchvsStatementCoverage;
import config.Paths;
import parser.projectparser.ProjectParser;
import tree.object.IFunctionNode;
import tree.object.INode;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

public class PossibleTestpathGenerationTest {

	@Test
	public void test1() throws Exception {
		File p = new File(Paths.TEST_PATH_GENERATION_TEST);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "test1()").get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function);
		PossibleTestpathGeneration tpGen = new PossibleTestpathGeneration(cfgGen.generateCFG());
		tpGen.generateTestpaths();
		Assert.assertEquals(1, tpGen.getPossibleTestpaths().size());
	}

	@Test
	public void test2() throws Exception {
		File p = new File(Paths.TEST_PATH_GENERATION_TEST);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "test2()").get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function);
		PossibleTestpathGeneration tpGen = new PossibleTestpathGeneration(cfgGen.generateCFG());
		tpGen.generateTestpaths();
		Assert.assertEquals(1, tpGen.getPossibleTestpaths().size());
	}

	@Test
	public void test3() throws Exception {
		File p = new File(Paths.TEST_PATH_GENERATION_TEST);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "test3(int,int)").get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function);
		PossibleTestpathGeneration tpGen = new PossibleTestpathGeneration(cfgGen.generateCFG());
		tpGen.generateTestpaths();
		Assert.assertEquals(2, tpGen.getPossibleTestpaths().size());
	}

	@Test
	public void test4() throws Exception {
		File p = new File(Paths.TEST_PATH_GENERATION_TEST);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "switch_case2(char)")
				.get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function);
		PossibleTestpathGeneration tpGen = new PossibleTestpathGeneration(cfgGen.generateCFG());
		tpGen.generateTestpaths();
		Assert.assertEquals(2, tpGen.getPossibleTestpaths().size());
	}

	@Test
	public void test5() throws Exception {
		File p = new File(Paths.TEST_PATH_GENERATION_TEST);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "switch_case3(char)")
				.get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function);
		PossibleTestpathGeneration tpGen = new PossibleTestpathGeneration(cfgGen.generateCFG());
		tpGen.generateTestpaths();
		Assert.assertEquals(4, tpGen.getPossibleTestpaths().size());
	}

	@Test
	public void test6() throws Exception {
		File p = new File(Paths.TEST_PATH_GENERATION_TEST);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "switch_case1(char)")
				.get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function);
		PossibleTestpathGeneration tpGen = new PossibleTestpathGeneration(cfgGen.generateCFG());
		tpGen.generateTestpaths();
		Assert.assertEquals(2, tpGen.getPossibleTestpaths().size());
	}

	@Test
	public void test7() throws Exception {
		File p = new File(Paths.TEST_PATH_GENERATION_TEST);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "try_catch1(int,int)")
				.get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function);
		PossibleTestpathGeneration tpGen = new PossibleTestpathGeneration(cfgGen.generateCFG());
		tpGen.generateTestpaths();
		Assert.assertEquals(2, tpGen.getPossibleTestpaths().size());
	}

	@Test
	public void test9() throws Exception {
		File p = new File(Paths.TEST_PATH_GENERATION_TEST);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "control_block1(int,int)").get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function);
		PossibleTestpathGeneration tpGen = new PossibleTestpathGeneration(cfgGen.generateCFG(), 1);

		tpGen.generateTestpaths();
		Assert.assertEquals(3, tpGen.getPossibleTestpaths().size());
	}

	@Test
	public void test10() throws Exception {
		File p = new File(Paths.TEST_PATH_GENERATION_TEST);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "control_block1(int,int)").get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function);
		PossibleTestpathGeneration tpGen = new PossibleTestpathGeneration(cfgGen.generateCFG(), 2);

		tpGen.generateTestpaths();
		Assert.assertEquals(7, tpGen.getPossibleTestpaths().size());
	}

	@Test
	public void test11() throws Exception {
		File p = new File(Paths.TEST_PATH_GENERATION_TEST);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "control_block1(int,int)").get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function);
		PossibleTestpathGeneration tpGen = new PossibleTestpathGeneration(cfgGen.generateCFG(), 0);

		tpGen.generateTestpaths();
		Assert.assertEquals(1, tpGen.getPossibleTestpaths().size());
	}

	@Test
	public void test12() throws Exception {
		File p = new File(Paths.TEST_PATH_GENERATION_TEST);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "control_block2()")
				.get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function);
		PossibleTestpathGeneration tpGen = new PossibleTestpathGeneration(cfgGen.generateCFG(), 1);

		tpGen.generateTestpaths();
		Assert.assertEquals(2, tpGen.getPossibleTestpaths().size());
	}

	@Test
	public void test14() throws Exception {
		File p = new File(Paths.TEST_PATH_GENERATION_TEST);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "control_block4()")
				.get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function);
		PossibleTestpathGeneration tpGen = new PossibleTestpathGeneration(cfgGen.generateCFG(), 1);

		tpGen.generateTestpaths();
		Assert.assertEquals(1, tpGen.getPossibleTestpaths().size());
	}

	@Test
	public void test15() throws Exception {
		File p = new File(Paths.TEST_PATH_GENERATION_TEST);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "control_block4()")
				.get(0);

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function);
		PossibleTestpathGeneration tpGen = new PossibleTestpathGeneration(cfgGen.generateCFG(), 2);

		tpGen.generateTestpaths();
		Assert.assertEquals(2, tpGen.getPossibleTestpaths().size());
	}
}
