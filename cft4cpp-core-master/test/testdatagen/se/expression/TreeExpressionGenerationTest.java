package testdatagen.se.expression;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import config.Paths;
import parser.projectparser.ProjectParser;
import tree.object.FunctionNode;
import tree.object.IFunctionNode;
import tree.object.Node;
import utils.Utils;
import utils.search.ExpressionCondition;
import utils.search.FunctionNodeCondition;
import utils.search.OperatorCondition;
import utils.search.Search;

public class TreeExpressionGenerationTest {

	@Test
	public void test1() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.TREE_EXPRESSION_GENERATION_TEST));
		IFunctionNode function = (FunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "test(int,int,int)").get(0);
		TreeExpressionGeneration treeGen = new TreeExpressionGeneration(
				Utils.findFirstConditionByName("1", function.getAST()));
		Node root = treeGen.getRoot();
		Assert.assertEquals(0, Search.searchNodes(root, new OperatorCondition()).size());
		Assert.assertEquals(1, Search.searchNodes(root, new ExpressionCondition()).size());
	}

	@Test
	public void test2() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.TREE_EXPRESSION_GENERATION_TEST));
		IFunctionNode function = (FunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "test(int,int,int)").get(0);
		TreeExpressionGeneration treeGen = new TreeExpressionGeneration(
				Utils.findFirstConditionByName("(1)", function.getAST()));
		Node root = treeGen.getRoot();
		Assert.assertEquals(0, Search.searchNodes(root, new OperatorCondition()).size());
		Assert.assertEquals(1, Search.searchNodes(root, new ExpressionCondition()).size());
	}

	@Test
	public void test3() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.TREE_EXPRESSION_GENERATION_TEST));
		IFunctionNode function = (FunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "test(int,int,int)").get(0);
		TreeExpressionGeneration treeGen = new TreeExpressionGeneration(
				Utils.findFirstConditionByName("1>0", function.getAST()));
		Node root = treeGen.getRoot();
		Assert.assertEquals(0, Search.searchNodes(root, new OperatorCondition()).size());
		Assert.assertEquals(1, Search.searchNodes(root, new ExpressionCondition()).size());
	}

	@Test
	public void test4() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.TREE_EXPRESSION_GENERATION_TEST));
		IFunctionNode function = (FunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "test(int,int,int)").get(0);
		TreeExpressionGeneration treeGen = new TreeExpressionGeneration(
				Utils.findFirstConditionByName("a>0 && b > 0", function.getAST()));
		Node root = treeGen.getRoot();
		Assert.assertEquals(1, Search.searchNodes(root, new OperatorCondition()).size());
		Assert.assertEquals(2, Search.searchNodes(root, new ExpressionCondition()).size());
	}

	@Test
	public void test5() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.TREE_EXPRESSION_GENERATION_TEST));
		IFunctionNode function = (FunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "test(int,int,int)").get(0);
		TreeExpressionGeneration treeGen = new TreeExpressionGeneration(
				Utils.findFirstConditionByName("a>0 && b > 0 && c>0", function.getAST()));
		Node root = treeGen.getRoot();
		Assert.assertEquals(2, Search.searchNodes(root, new OperatorCondition()).size());
		Assert.assertEquals(3, Search.searchNodes(root, new ExpressionCondition()).size());
	}

	@Test
	public void test6() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.TREE_EXPRESSION_GENERATION_TEST));
		IFunctionNode function = (FunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "test(int,int,int)").get(0);
		TreeExpressionGeneration treeGen = new TreeExpressionGeneration(
				Utils.findFirstConditionByName("a>0 && b > 0 && ((c>0))", function.getAST()));
		Node root = treeGen.getRoot();
		Assert.assertEquals(2, Search.searchNodes(root, new OperatorCondition()).size());
		Assert.assertEquals(3, Search.searchNodes(root, new ExpressionCondition()).size());
	}

	@Test
	public void test7() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.TREE_EXPRESSION_GENERATION_TEST));
		IFunctionNode function = (FunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "test(int,int,int)").get(0);
		TreeExpressionGeneration treeGen = new TreeExpressionGeneration(
				Utils.findFirstConditionByName("a>0 || (b > 0 && b<9)", function.getAST()));
		Node root = treeGen.getRoot();
		Assert.assertEquals(2, Search.searchNodes(root, new OperatorCondition()).size());
		Assert.assertEquals(3, Search.searchNodes(root, new ExpressionCondition()).size());
	}

	@Test
	public void test8() throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.TREE_EXPRESSION_GENERATION_TEST));
		IFunctionNode function = (FunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "test(int,int,int)").get(0);
		TreeExpressionGeneration treeGen = new TreeExpressionGeneration(
				Utils.findFirstConditionByName("a>0 || (b > 0 && (b<9 || b>1))", function.getAST()));
		Node root = treeGen.getRoot();
		Assert.assertEquals(3, Search.searchNodes(root, new OperatorCondition()).size());
		Assert.assertEquals(4, Search.searchNodes(root, new ExpressionCondition()).size());
	}
}
