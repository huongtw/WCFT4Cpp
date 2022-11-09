package projectparser;

import java.io.File;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import config.Paths;
import parser.projectparser.ProjectParser;
import tree.object.FunctionNode;
import tree.object.INode;
import tree.object.IProcessNotify;
import tree.object.Node;
import tree.object.TypedefDeclaration;
import utils.search.FunctionNodeCondition;
import utils.search.Search;
import utils.search.TypedefNodeCondition;

public class ProjectParserTest {
	/**
	 * Test function name
	 */
	@Test
	public void testFunctionName1() {
		File p = new File(Paths.FUNCTION_NODE_NAME_TEST);

		ProjectParser parser = new ProjectParser(p);
		INode n = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
				File.separator + "getInforOfApple(IndividualStore::hoaqua)").get(0);
		Assert.assertEquals(true, n != null);
	}

	@Test
	@Ignore
	public void testFunctionName10() {
		File p = new File(Paths.FUNCTION_NODE_NAME_TEST);

		ProjectParser parser = new ProjectParser(p);
		INode n = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
				File.separator + "testFunctionNameMethod(A1::customStruct)").get(0);
		Assert.assertEquals(true, n != null);
	}

	@Test
	public void testFunctionName11() {
		File p = new File(Paths.FUNCTION_NODE_NAME_TEST);

		ProjectParser parser = new ProjectParser(p);
		INode n = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
				File.separator + "displayAddress(IndividualStore::House::Accommodation::information)").get(0);
		Assert.assertEquals(true, n != null);
	}

	@Test
	public void testFunctionName12() {
		File p = new File(Paths.BTL);

		ProjectParser parser = new ProjectParser(p);
		FunctionNode sampleNode = (FunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
				File.separator + "TS::inFile(ofstream&,TS*)").get(0);
		Assert.assertEquals(true, sampleNode != null);
	}

	@Test
	public void testFunctionName2() {
		File p = new File(Paths.FUNCTION_NODE_NAME_TEST);

		ProjectParser parser = new ProjectParser(p);
		INode n = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
				File.separator + "listAllApples(std::vector<IndividualStore::hoaqua>)").get(0);
		Assert.assertEquals(true, n != null);
	}

	@Test
	public void testFunctionName3() {
		File p = new File(Paths.FUNCTION_NODE_NAME_TEST);

		ProjectParser parser = new ProjectParser(p);
		INode n = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
				File.separator + "getInforOfApples(IndividualStore::hoaqua*)").get(0);
		Assert.assertEquals(true, n != null);
	}

	@Test
	public void testFunctionName4() {
		File p = new File(Paths.FUNCTION_NODE_NAME_TEST);

		ProjectParser parser = new ProjectParser(p);
		INode n = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
				File.separator + "getInforOfApples(IndividualStore::hoaqua**)").get(0);
		Assert.assertEquals(true, n != null);
	}

	@Test
	public void testFunctionName5() {
		File p = new File(Paths.FUNCTION_NODE_NAME_TEST);

		ProjectParser parser = new ProjectParser(p);
		INode n = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
				File.separator + "getInforOfApples(IndividualStore::hoaqua&)").get(0);
		Assert.assertEquals(true, n != null);
	}

	@Test
	public void testFunctionName6() {
		File p = new File(Paths.FUNCTION_NODE_NAME_TEST);

		ProjectParser parser = new ProjectParser(p);
		INode n = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
				File.separator + "getInforOfApples2(IndividualStore::hoaqua[])").get(0);
		Assert.assertEquals(true, n != null);
	}

	@Test
	public void testFunctionName7() {
		File p = new File(Paths.FUNCTION_NODE_NAME_TEST);

		ProjectParser parser = new ProjectParser(p);
		INode n = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
				File.separator + "getInforOfApples2(IndividualStore::hoaqua[][5])").get(0);
		Assert.assertEquals(true, n != null);
	}

	@Test
	public void testFunctionName8() {
		File p = new File(Paths.FUNCTION_NODE_NAME_TEST);

		ProjectParser parser = new ProjectParser(p);
		INode n = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
				File.separator + "getInforOfChainStore(ns1::C1)").get(0);
		Assert.assertEquals(true, n != null);
	}

	@Test
	public void testFunctionName9() {
		File p = new File(Paths.FUNCTION_NODE_NAME_TEST);

		ProjectParser parser = new ProjectParser(p);
		INode n = Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), File.separator + "getSQRT(float_type)")
				.get(0);
		Assert.assertEquals(true, n != null);
	}

	/**
	 * Test Typedef
	 */
	@Test
	public void testTypedefDeclaration_simpleTypedef() {
		File p = new File(Paths.MULTIPLE_TYPEDEF_DECLARE);
		Node root = (Node) new ProjectParser(p, IProcessNotify.DEFAULT).getRootTree();

		TypedefDeclaration td = (TypedefDeclaration) Search
				.searchNodes(root, new TypedefNodeCondition(), File.separator + "int_type").get(0);
		Assert.assertEquals("int", td.getOldType());
		Assert.assertEquals("int_type", td.getNewType());
	}

	@Test
	public void testTypedefDeclaration_secondTypedef() {
		File p = new File(Paths.MULTIPLE_TYPEDEF_DECLARE);
		Node root = (Node) new ProjectParser(p, IProcessNotify.DEFAULT).getRootTree();

		TypedefDeclaration td = (TypedefDeclaration) Search
				.searchNodes(root, new TypedefNodeCondition(), File.separator + "int_type_2").get(0);
		Assert.assertEquals("int", td.getOldType());
		Assert.assertEquals("int_type_2", td.getNewType());
	}

	@Test
	public void testTypedefDeclaration_pointerTypdef() {
		File p = new File(Paths.MULTIPLE_TYPEDEF_DECLARE);
		Node root = (Node) new ProjectParser(p, IProcessNotify.DEFAULT).getRootTree();

		TypedefDeclaration td = (TypedefDeclaration) Search
				.searchNodes(root, new TypedefNodeCondition(), File.separator + "int_pointer").get(0);
		Assert.assertEquals("int*", td.getOldType());
		Assert.assertEquals("int_pointer", td.getNewType());
	}

}
