package testdatagen.coverage;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import cfg.ICFG;
import config.Paths;
import normalizer.FunctionNormalizer;
import parser.projectparser.ProjectParser;
import testdata.object.TestpathString_Marker;
import tree.object.IFunctionNode;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

public class CFGUpdater_MarkTest {
	@Test
	public void noBranchTestpath() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.IMSTRUMENT_TEST));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "simple()").get(0);

		FunctionNormalizer fnNorm = function.normalizedAST();
		function.setAST(fnNorm.getNormalizedAST());

		// Create a test path
		TestpathString_Marker testpath = new TestpathString_Marker();
		String[] nodes = new String[] { "statement={", "line-in-function=2###offset=18###statement=int a = 0;",
				"line-in-function=3###offset=31###statement=int b, c;", "statement=}" };
		testpath.setEncodedTestpath(nodes);

		// Mapping
		ICFG cfg = function.generateCFG();
		cfg.setFunctionNode(function);
		cfg.setIdforAllNodes();
		CFGUpdater_Mark updater = new CFGUpdater_Mark(testpath, cfg);
		cfg.resetVisitedStateOfNodes();
		updater.updateVisitedNodes();
		Assert.assertEquals(2, cfg.getVisitedStatements().size());
		Assert.assertEquals(true, updater.isCompleteTestpath());
	}

	@Test
	public void completenessTestpath() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.IMSTRUMENT_TEST));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "simple()").get(0);

		FunctionNormalizer fnNorm = function.normalizedAST();
		function.setAST(fnNorm.getNormalizedAST());

		// Create a test path
		TestpathString_Marker testpath = new TestpathString_Marker();
		String[] nodes = new String[] { "statement={", "line-in-function=2###offset=18###statement=int a = 0;",
				"line-in-function=3###offset=31###statement=int b, c;" };
		testpath.setEncodedTestpath(nodes);

		// Mapping
		ICFG cfg = function.generateCFG();
		cfg.setFunctionNode(function);
		cfg.setIdforAllNodes();
		CFGUpdater_Mark updater = new CFGUpdater_Mark(testpath, cfg);
		cfg.resetVisitedStateOfNodes();
		updater.updateVisitedNodes();
		Assert.assertEquals(2, cfg.getVisitedStatements().size());
		Assert.assertEquals(0, cfg.getVisitedBranches().size());
		Assert.assertEquals(false, updater.isCompleteTestpath());
	}

	@Test
	public void if_control_block_true_branch() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.IMSTRUMENT_TEST));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "if_control_block(int)").get(0);

		FunctionNormalizer fnNorm = function.normalizedAST();
		function.setAST(fnNorm.getNormalizedAST());

		// Create a test path
		TestpathString_Marker testpath = new TestpathString_Marker();
		String[] nodes = new String[] { "statement={",
				"line-in-function=2###offset=37###statement=a>1###control-block=if",
				"statement={###additional-code=true", "line-in-function=3###offset=45###statement=a++;",
				"statement=}###additional-code=true", "statement=}" };
		testpath.setEncodedTestpath(nodes);

		// Mapping
		ICFG cfg = function.generateCFG();
		cfg.setFunctionNode(function);
		cfg.setIdforAllNodes();
		CFGUpdater_Mark updater = new CFGUpdater_Mark(testpath, cfg);
		cfg.resetVisitedStateOfNodes();
		updater.updateVisitedNodes();
		Assert.assertEquals(2, cfg.getVisitedStatements().size());
		Assert.assertEquals(1, cfg.getVisitedBranches().size());
		Assert.assertEquals(true, updater.isCompleteTestpath());
	}

	@Test
	public void if_control_block_false_branch() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.IMSTRUMENT_TEST));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "if_control_block(int)").get(0);

		FunctionNormalizer fnNorm = function.normalizedAST();
		function.setAST(fnNorm.getNormalizedAST());

		// Create a test path
		TestpathString_Marker testpath = new TestpathString_Marker();
		String[] nodes = new String[] { "statement={",
				"line-in-function=2###offset=37###statement=a>1###control-block=if", "statement=}" };
		testpath.setEncodedTestpath(nodes);

		// Mapping
		ICFG cfg = function.generateCFG();
		cfg.setFunctionNode(function);
		cfg.setIdforAllNodes();
		CFGUpdater_Mark updater = new CFGUpdater_Mark(testpath, cfg);
		cfg.resetVisitedStateOfNodes();
		updater.updateVisitedNodes();
		Assert.assertEquals(1, cfg.getVisitedStatements().size());
		Assert.assertEquals(1, cfg.getVisitedBranches().size());
		Assert.assertEquals(true, updater.isCompleteTestpath());
	}

	@Test
	public void if_else_control_block() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.IMSTRUMENT_TEST));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "if_else_control_block(int)").get(0);

		FunctionNormalizer fnNorm = function.normalizedAST();
		function.setAST(fnNorm.getNormalizedAST());

		// Create a test path
		TestpathString_Marker testpath = new TestpathString_Marker();
		String[] nodes = new String[] { "statement={",
				"line-in-function=2###offset=42###statement=a>0###control-block=if",
				"statement={###additional-code=true", "statement={###additional-code=true",
				"line-in-function=5###offset=65###statement=a--;", "statement=}" };
		testpath.setEncodedTestpath(nodes);

		// Mapping
		ICFG cfg = function.generateCFG();
		cfg.setFunctionNode(function);
		cfg.setIdforAllNodes();
		CFGUpdater_Mark updater = new CFGUpdater_Mark(testpath, cfg);
		cfg.resetVisitedStateOfNodes();
		updater.updateVisitedNodes();
		Assert.assertEquals(2, cfg.getVisitedStatements().size());
		Assert.assertEquals(1, cfg.getVisitedBranches().size());
		Assert.assertEquals(true, updater.isCompleteTestpath());
	}

	@Test
	public void if_else_control_block_2() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.IMSTRUMENT_TEST));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "if_else_control_block_2(int)").get(0);

		FunctionNormalizer fnNorm = function.normalizedAST();
		function.setAST(fnNorm.getNormalizedAST());

		// Create a test path
		TestpathString_Marker testpath = new TestpathString_Marker();
		String[] nodes = new String[] { "statement={###line-of-blockin-function=1",
				"line-in-function=2###offset=44###statement=a>0###control-block=if",
				"statement={###line-of-blockin-function=2", "line-in-function=3###offset=53###statement=a++;",
				"statement=}###line-of-blockin-function=2", "statement=}###line-of-blockin-function=1" };
		testpath.setEncodedTestpath(nodes);

		// Mapping
		ICFG cfg = function.generateCFG();
		cfg.setFunctionNode(function);
		cfg.setIdforAllNodes();
		CFGUpdater_Mark updater = new CFGUpdater_Mark(testpath, cfg);
		cfg.resetVisitedStateOfNodes();
		updater.updateVisitedNodes();
		Assert.assertEquals(2, cfg.getVisitedStatements().size());
		Assert.assertEquals(1, cfg.getVisitedBranches().size());
		Assert.assertEquals(true, updater.isCompleteTestpath());
	}

	@Test
	public void if_else_control_block_3() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.IMSTRUMENT_TEST));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "if_else_control_block_2(int)").get(0);

		FunctionNormalizer fnNorm = function.normalizedAST();
		function.setAST(fnNorm.getNormalizedAST());

		// Create a test path
		TestpathString_Marker testpath = new TestpathString_Marker();
		String[] nodes = new String[] { "statement={###line-of-blockin-function=1",
				"line-in-function=2###offset=44###statement=a>0###control-block=if",
				"statement={###line-of-blockin-function=4", "line-in-function=5###offset=70###statement=a--;",
				"statement=}###line-of-blockin-function=4", "statement=}###line-of-blockin-function=1"

		};
		testpath.setEncodedTestpath(nodes);

		// Mapping
		ICFG cfg = function.generateCFG();
		cfg.setFunctionNode(function);
		cfg.setIdforAllNodes();
		CFGUpdater_Mark updater = new CFGUpdater_Mark(testpath, cfg);
		cfg.resetVisitedStateOfNodes();
		updater.updateVisitedNodes();
		Assert.assertEquals(2, cfg.getVisitedStatements().size());
		Assert.assertEquals(1, cfg.getVisitedBranches().size());
		Assert.assertEquals(true, updater.isCompleteTestpath());
	}

	@Test
	public void if_else_control_block_4() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.IMSTRUMENT_TEST));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "if_else_control_block_3(int)").get(0);

		FunctionNormalizer fnNorm = function.normalizedAST();
		function.setAST(fnNorm.getNormalizedAST());

		// Create a test path
		TestpathString_Marker testpath = new TestpathString_Marker();
		String[] nodes = new String[] { "statement={###line-of-blockin-function=1",
				"line-in-function=2###offset=44###statement=a>0###control-block=if",
				"statement={###line-of-blockin-function=4", "line-in-function=5###offset=70###statement=a++;",
				"statement=}###line-of-blockin-function=4", "statement=}###line-of-blockin-function=1"

		};
		testpath.setEncodedTestpath(nodes);

		// Mapping
		ICFG cfg = function.generateCFG();
		cfg.setFunctionNode(function);
		cfg.setIdforAllNodes();
		CFGUpdater_Mark updater = new CFGUpdater_Mark(testpath, cfg);
		cfg.resetVisitedStateOfNodes();
		updater.updateVisitedNodes();
		Assert.assertEquals(2, cfg.getVisitedStatements().size());
		Assert.assertEquals(1, cfg.getVisitedBranches().size());
		Assert.assertEquals(true, updater.isCompleteTestpath());
	}

	@Test
	public void if_else_control_block_5() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.IMSTRUMENT_TEST));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "if_else_control_block_3(int)").get(0);

		FunctionNormalizer fnNorm = function.normalizedAST();
		function.setAST(fnNorm.getNormalizedAST());

		// Create a test path
		TestpathString_Marker testpath = new TestpathString_Marker();
		String[] nodes = new String[] { "statement={###line-of-blockin-function=1",
				"line-in-function=2###offset=44###statement=a>0###control-block=if",
				"statement={###line-of-blockin-function=2", "line-in-function=3###offset=53###statement=a++;",
				"statement=}###line-of-blockin-function=2", "statement=}###line-of-blockin-function=1" };
		testpath.setEncodedTestpath(nodes);

		// Mapping
		ICFG cfg = function.generateCFG();
		cfg.setFunctionNode(function);
		cfg.setIdforAllNodes();
		CFGUpdater_Mark updater = new CFGUpdater_Mark(testpath, cfg);
		cfg.resetVisitedStateOfNodes();
		updater.updateVisitedNodes();
		Assert.assertEquals(2, cfg.getVisitedStatements().size());
		Assert.assertEquals(1, cfg.getVisitedBranches().size());
		Assert.assertEquals(true, updater.isCompleteTestpath());
	}

	@Test
	public void for_control_block() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.IMSTRUMENT_TEST));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "for_control_block(int)").get(0);

		FunctionNormalizer fnNorm = function.normalizedAST();
		function.setAST(fnNorm.getNormalizedAST());

		// Create a test path
		TestpathString_Marker testpath = new TestpathString_Marker();
		String[] nodes = new String[] { "statement={",
				"statement={###additional-code=true###surrounding-control-block=for",
				"line-in-function=2###offset=39###statement=int i=0;", "line-in-function=2###offset=48###statement=i<a",
				"statement={", "line-in-function=3###offset=63###statement=a++;", "statement=}",
				"line-in-function=2###offset=53###statement=i++", "line-in-function=2###offset=48###statement=i<a",
				"statement=}###additional-code=true###surrounding-control-block=for", "statement=}" };
		testpath.setEncodedTestpath(nodes);

		// Mapping
		ICFG cfg = function.generateCFG();
		cfg.setFunctionNode(function);
		cfg.setIdforAllNodes();
		CFGUpdater_Mark updater = new CFGUpdater_Mark(testpath, cfg);
		cfg.resetVisitedStateOfNodes();
		updater.updateVisitedNodes();

		Assert.assertEquals(4, cfg.getVisitedStatements().size());
		Assert.assertEquals(2, cfg.getVisitedBranches().size());
		Assert.assertEquals(true, updater.isCompleteTestpath());
	}

	@Test
	public void for_control_block_2() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.IMSTRUMENT_TEST));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "for_control_block_2(int)").get(0);

		FunctionNormalizer fnNorm = function.normalizedAST();
		function.setAST(fnNorm.getNormalizedAST());

		// Create a test path
		TestpathString_Marker testpath = new TestpathString_Marker();
		String[] nodes = new String[] { "statement={", "line-in-function=2###offset=36###statement=int i = 0;",
				"statement={###additional-code=true###surrounding-control-block=for",
				"line-in-function=3###offset=56###statement=i<a", "statement={",
				"line-in-function=4###offset=71###statement=a++;", "statement=}",
				"line-in-function=3###offset=61###statement=i++", "line-in-function=3###offset=56###statement=i<a",
				"statement=}###additional-code=true###surrounding-control-block=for", "statement=}" };
		testpath.setEncodedTestpath(nodes);

		// Mapping
		ICFG cfg = function.generateCFG();
		cfg.setFunctionNode(function);
		cfg.setIdforAllNodes();
		CFGUpdater_Mark updater = new CFGUpdater_Mark(testpath, cfg);
		cfg.resetVisitedStateOfNodes();
		updater.updateVisitedNodes();
		Assert.assertEquals(4, cfg.getVisitedStatements().size());
		Assert.assertEquals(true, updater.isCompleteTestpath());
	}

	@Test
	public void while_control_block() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.IMSTRUMENT_TEST));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "while_control_block(int)").get(0);

		FunctionNormalizer fnNorm = function.normalizedAST();
		function.setAST(fnNorm.getNormalizedAST());

		// Create a test path
		TestpathString_Marker testpath = new TestpathString_Marker();
		String[] nodes = new String[] { "statement={", "line-in-function=2###offset=43###statement=a<1", "statement={",
				"line-in-function=3###offset=53###statement=a--;", "statement=}",
				"line-in-function=2###offset=43###statement=a<1", "statement=}" };
		testpath.setEncodedTestpath(nodes);

		// Mapping
		ICFG cfg = function.generateCFG();
		cfg.setFunctionNode(function);
		cfg.setIdforAllNodes();
		CFGUpdater_Mark updater = new CFGUpdater_Mark(testpath, cfg);
		cfg.resetVisitedStateOfNodes();
		updater.updateVisitedNodes();
		Assert.assertEquals(2, cfg.getVisitedStatements().size());
		Assert.assertEquals(true, updater.isCompleteTestpath());
	}

	@Test
	public void while_control_block_2() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.IMSTRUMENT_TEST));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "while_control_block(int)").get(0);

		FunctionNormalizer fnNorm = function.normalizedAST();
		function.setAST(fnNorm.getNormalizedAST());

		// Create a test path
		TestpathString_Marker testpath = new TestpathString_Marker();
		String[] nodes = new String[] { "statement={", "line-in-function=2###offset=43###statement=a<1",
				"statement=}" };
		testpath.setEncodedTestpath(nodes);

		// Mapping
		ICFG cfg = function.generateCFG();
		cfg.setFunctionNode(function);
		cfg.setIdforAllNodes();
		CFGUpdater_Mark updater = new CFGUpdater_Mark(testpath, cfg);
		cfg.resetVisitedStateOfNodes();
		updater.updateVisitedNodes();
		Assert.assertEquals(1, cfg.getVisitedStatements().size());
		Assert.assertEquals(true, updater.isCompleteTestpath());
	}

	@Test
	public void do_control_block() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.IMSTRUMENT_TEST));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "do_control_block(int)").get(0);

		FunctionNormalizer fnNorm = function.normalizedAST();
		function.setAST(fnNorm.getNormalizedAST());

		// Create a test path
		TestpathString_Marker testpath = new TestpathString_Marker();
		String[] nodes = new String[] { "statement={", "statement={", "line-in-function=3###offset=41###statement=a--;",
				"statement=}", "line-in-function=4###offset=56###statement=a<1", "statement=}" };
		testpath.setEncodedTestpath(nodes);

		// Mapping
		ICFG cfg = function.generateCFG();
		cfg.setFunctionNode(function);
		cfg.setIdforAllNodes();
		CFGUpdater_Mark updater = new CFGUpdater_Mark(testpath, cfg);
		cfg.resetVisitedStateOfNodes();
		updater.updateVisitedNodes();
		Assert.assertEquals(2, cfg.getVisitedStatements().size());
		Assert.assertEquals(true, updater.isCompleteTestpath());
	}

	@Test
	public void try_catch() throws Exception {
		// Some failures
		Assert.assertEquals(true, false);
	}

	@Test
	public void recursive_Fibonaxi_1() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.IMSTRUMENT_TEST));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "Fibonacci(int)").get(0);

		FunctionNormalizer fnNorm = function.normalizedAST();
		function.setAST(fnNorm.getNormalizedAST());

		// Create a test path
		TestpathString_Marker testpath = new TestpathString_Marker();
		String[] nodes = new String[] { "statement={###line-of-blockin-function=1###openning-function=true",
				"line-in-function=2###offset=29###statement=n == 0###control-block=if",
				"line-in-function=4###offset=63###statement=n == 1###control-block=if",
				"line-in-function=7###offset=95###statement=return ( Fibonacci(n-1) + Fibonacci(n-2) );###is-recursive=true",
				"statement={###line-of-blockin-function=1###openning-function=true",
				"line-in-function=2###offset=29###statement=n == 0###control-block=if",
				"line-in-function=4###offset=63###statement=n == 1###control-block=if",
				"line-in-function=7###offset=95###statement=return ( Fibonacci(n-1) + Fibonacci(n-2) );###is-recursive=true",
				"statement={###line-of-blockin-function=1###openning-function=true",
				"line-in-function=2###offset=29###statement=n == 0###control-block=if",
				"line-in-function=4###offset=63###statement=n == 1###control-block=if",
				"line-in-function=5###offset=75###statement=return 1;",
				"statement={###line-of-blockin-function=1###openning-function=true",
				"line-in-function=2###offset=29###statement=n == 0###control-block=if",
				"line-in-function=3###offset=41###statement=return 0;",
				"statement={###line-of-blockin-function=1###openning-function=true",
				"line-in-function=2###offset=29###statement=n == 0###control-block=if",
				"line-in-function=4###offset=63###statement=n == 1###control-block=if",
				"line-in-function=5###offset=75###statement=return 1;" };
		testpath.setEncodedTestpath(nodes);

		// Mapping
		ICFG cfg = function.generateCFG();
		cfg.setFunctionNode(function);
		cfg.setIdforAllNodes();
		CFGUpdater_Mark updater = new CFGUpdater_Mark(testpath, cfg);
		cfg.resetVisitedStateOfNodes();
		updater.updateVisitedNodes();
		Assert.assertEquals(4, cfg.getVisitedBranches().size());
		Assert.assertEquals(true, updater.isCompleteTestpath());
	}

	@Test
	public void recursive_Fibonaxi_2() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.IMSTRUMENT_TEST));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "Fibonacci(int)").get(0);

		FunctionNormalizer fnNorm = function.normalizedAST();
		function.setAST(fnNorm.getNormalizedAST());

		// Create a test path
		TestpathString_Marker testpath = new TestpathString_Marker();
		String[] nodes = new String[] { "statement={###line-of-blockin-function=1###openning-function=true",
				"line-in-function=2###offset=29###statement=n == 0###control-block=if",
				"line-in-function=4###offset=63###statement=n == 1###control-block=if",
				"line-in-function=7###offset=95###statement=return ( Fibonacci(n-1) + Fibonacci(n-2) );###is-recursive=true",
				"statement={###line-of-blockin-function=1###openning-function=true",
				"line-in-function=2###offset=29###statement=n == 0###control-block=if",
				"line-in-function=4###offset=63###statement=n == 1###control-block=if",
				"line-in-function=7###offset=95###statement=return ( Fibonacci(n-1) + Fibonacci(n-2) );###is-recursive=true",
				"statement={###line-of-blockin-function=1###openning-function=true",
				"line-in-function=2###offset=29###statement=n == 0###control-block=if",
				"line-in-function=4###offset=63###statement=n == 1###control-block=if",
				"line-in-function=5###offset=75###statement=return 1;",
				"statement={###line-of-blockin-function=1###openning-function=true",
				"line-in-function=2###offset=29###statement=n == 0###control-block=if",
				"line-in-function=3###offset=41###statement=return 0;",
				"statement={###line-of-blockin-function=1###openning-function=true",
				"line-in-function=2###offset=29###statement=n == 0###control-block=if",
				"line-in-function=4###offset=63###statement=n == 1###control-block=if",
				"line-in-function=5###offset=75###statement=return 1;" };
		testpath.setEncodedTestpath(nodes);

		// Mapping
		ICFG cfg = function.generateCFG();
		cfg.setFunctionNode(function);
		cfg.setIdforAllNodes();
		CFGUpdater_Mark updater = new CFGUpdater_Mark(testpath, cfg);
		cfg.resetVisitedStateOfNodes();
		updater.updateVisitedNodes();
		Assert.assertEquals(5, cfg.getVisitedStatements().size());
		Assert.assertEquals(true, updater.isCompleteTestpath());
	}

	@Test
	public void recursive_add_digits_1() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.IMSTRUMENT_TEST));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "add_digits(int)").get(0);

		FunctionNormalizer fnNorm = function.normalizedAST();
		function.setAST(fnNorm.getNormalizedAST());

		// Create a test path
		TestpathString_Marker testpath = new TestpathString_Marker();
		String[] nodes = new String[] { "statement={###line-of-blockin-function=1###openning-function=true",
				"line-in-function=2###offset=26###statement=static int sum = 0;",
				"line-in-function=4###offset=55###statement=n == 0###control-block=if",
				"line-in-function=7###offset=85###statement=sum = n%10 + add_digits(n/10);###is-recursive=true",
				"statement={###line-of-blockin-function=1###openning-function=true",
				"line-in-function=2###offset=26###statement=static int sum = 0;",
				"line-in-function=4###offset=55###statement=n == 0###control-block=if",
				"line-in-function=7###offset=85###statement=sum = n%10 + add_digits(n/10);###is-recursive=true",
				"statement={###line-of-blockin-function=1###openning-function=true",
				"line-in-function=2###offset=26###statement=static int sum = 0;",
				"line-in-function=4###offset=55###statement=n == 0###control-block=if",
				"statement={###line-of-blockin-function=4", "line-in-function=5###offset=68###statement=return 0;",
				"line-in-function=9###offset=120###statement=return sum;",
				"line-in-function=9###offset=120###statement=return sum;" };
		testpath.setEncodedTestpath(nodes);

		// Mapping
		ICFG cfg = function.generateCFG();
		cfg.setFunctionNode(function);
		cfg.setIdforAllNodes();
		CFGUpdater_Mark updater = new CFGUpdater_Mark(testpath, cfg);
		cfg.resetVisitedStateOfNodes();
		updater.updateVisitedNodes();
		Assert.assertEquals(5, cfg.getVisitedStatements().size());
		Assert.assertEquals(true, updater.isCompleteTestpath());
	}

	@Test
	public void recursive_add_digits_2() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.IMSTRUMENT_TEST));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "add_digits(int)").get(0);

		FunctionNormalizer fnNorm = function.normalizedAST();
		function.setAST(fnNorm.getNormalizedAST());

		// Create a test path
		TestpathString_Marker testpath = new TestpathString_Marker();
		String[] nodes = new String[] { "statement={###line-of-blockin-function=1###openning-function=true",
				"line-in-function=2###offset=26###statement=static int sum = 0;",
				"line-in-function=4###offset=55###statement=n == 0###control-block=if",
				"line-in-function=7###offset=85###statement=sum = n%10 + add_digits(n/10);###is-recursive=true",
				"statement={###line-of-blockin-function=1###openning-function=true",
				"line-in-function=2###offset=26###statement=static int sum = 0;",
				"line-in-function=4###offset=55###statement=n == 0###control-block=if",
				"line-in-function=7###offset=85###statement=sum = n%10 + add_digits(n/10);###is-recursive=true",
				"statement={###line-of-blockin-function=1###openning-function=true",
				"line-in-function=2###offset=26###statement=static int sum = 0;",
				"line-in-function=4###offset=55###statement=n == 0###control-block=if",
				"statement={###line-of-blockin-function=4", "line-in-function=5###offset=68###statement=return 0;",
				"line-in-function=9###offset=120###statement=return sum;",
				"line-in-function=9###offset=120###statement=return sum;" };
		testpath.setEncodedTestpath(nodes);

		// Mapping
		ICFG cfg = function.generateCFG();
		cfg.setFunctionNode(function);
		cfg.setIdforAllNodes();
		CFGUpdater_Mark updater = new CFGUpdater_Mark(testpath, cfg);
		cfg.resetVisitedStateOfNodes();
		updater.updateVisitedNodes();
		Assert.assertEquals(2, cfg.getVisitedBranches().size());
		Assert.assertEquals(true, updater.isCompleteTestpath());
	}

	@Test
	public void break_Stm() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.IMSTRUMENT_TEST));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "compare_string(char*,char*)").get(0);

		FunctionNormalizer fnNorm = function.normalizedAST();
		function.setAST(fnNorm.getNormalizedAST());

		// Create a test path
		TestpathString_Marker testpath = new TestpathString_Marker();
		String[] nodes = new String[] { "statement={###line-of-blockin-function=1###openning-function=true",

				"line-in-function=2###offset=55###statement=*first==*second",
				"statement={###line-of-blockin-function=2",
				"line-in-function=3###offset=81###statement=*first == '\\0' || *second == '\\0'###control-block=if",
				"line-in-function=4###offset=121###statement=break;",
				"line-in-function=9###offset=167###statement=*first == '\\0' && *second == '\\0'###control-block=if",
				"line-in-function=10###offset=206###statement=return 0;" };
		testpath.setEncodedTestpath(nodes);

		// Mapping
		ICFG cfg = function.generateCFG();
		cfg.setFunctionNode(function);
		cfg.setIdforAllNodes();
		CFGUpdater_Mark updater = new CFGUpdater_Mark(testpath, cfg);
		cfg.resetVisitedStateOfNodes();
		updater.updateVisitedNodes();
		Assert.assertEquals(3, cfg.getVisitedBranches().size());
		Assert.assertEquals(true, updater.isCompleteTestpath());
	}

	@Test
	public void multiple_brackets() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.IMSTRUMENT_TEST));
		IFunctionNode function = (IFunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
				"mergeTwoArray(int[],int,int[],int,int[])").get(0);

		FunctionNormalizer fnNorm = function.normalizedAST();
		function.setAST(fnNorm.getNormalizedAST());

		// Create a test path
		TestpathString_Marker testpath = new TestpathString_Marker();
		String[] nodes = new String[] { "statement={###line-of-blockin-function=1###openning-function=true",
				"line-in-function=2###offset=69###statement=int i, j, k, t;",
				"line-in-function=4###offset=89###statement=t = j = k = 0;",
				"statement={###additional-code=true###surrounding-control-block=for",
				"line-in-function=6###offset=113###statement=i = 0;",
				"line-in-function=6###offset=120###statement=i < m + n", "statement={###line-of-blockin-function=6",
				"line-in-function=7###offset=145###statement=j < m && k < n###control-block=if",
				"line-in-function=16###offset=294###statement=j == m###control-block=if",
				"statement={###line-of-blockin-function=16",
				"statement={###additional-code=true###surrounding-control-block=for",
				"line-in-function=17###offset=313###statement=int t = 0;",
				"line-in-function=17###offset=324###statement=i < m + n", "statement={###line-of-blockin-function=17",
				"line-in-function=18###offset=346###statement=sorted[i] = b[k];" };
		testpath.setEncodedTestpath(nodes);

		// Mapping
		ICFG cfg = function.generateCFG();
		cfg.setFunctionNode(function);
		cfg.setIdforAllNodes();
		CFGUpdater_Mark updater = new CFGUpdater_Mark(testpath, cfg);
		cfg.resetVisitedStateOfNodes();
		updater.updateVisitedNodes();
		Assert.assertEquals(4, cfg.getVisitedBranches().size());
		Assert.assertEquals(false, updater.isCompleteTestpath());
	}
}
