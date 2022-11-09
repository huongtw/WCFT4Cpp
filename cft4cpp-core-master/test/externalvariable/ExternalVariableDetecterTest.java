package externalvariable;

import java.io.File;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import config.Paths;
import parser.projectparser.ProjectParser;
import tree.object.FunctionNode;
import utils.Utils;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

@Ignore
public class ExternalVariableDetecterTest {

	@Test
	public void test1() {
		File p = new File(Utils.normalizePath(Paths.EXTERNAL_VARIABLE_DETECTER_TEST));
		ProjectParser parser = new ProjectParser(p);

		FunctionNode function = (FunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "C::test(A*,int)").get(0);
		Assert.assertEquals(function.getExternalVariables().size(), 10);
	}

	/**
	 * static variable int class, struct
	 */
	@Test
	public void test2() {
		File p = new File(Utils.normalizePath(Paths.EXTERNAL_VARIABLE_DETECTER_TEST));
		ProjectParser parser = new ProjectParser(p);

		FunctionNode function = (FunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "C\\testStatic()").get(0);
		Assert.assertEquals(function.getExternalVariables().size(), 3);
	}

	/**
	 * 2 namespaces declared in 2 files
	 */
	@Test
	public void test3() {
		File p = new File(Utils.normalizePath(Paths.EXTERNAL_VARIABLE_DETECTER_TEST));
		ProjectParser parser = new ProjectParser(p);

		FunctionNode function = (FunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "B\\C1::test()").get(0);
		Assert.assertEquals(function.getExternalVariables().size(), 2);
	}

	/**
	 * static in namespace
	 */
	@Test
	public void test4() {
		File p = new File(Utils.normalizePath(Paths.EXTERNAL_VARIABLE_DETECTER_TEST));
		ProjectParser parser = new ProjectParser(p);

		FunctionNode function = (FunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "A\\C2\\test()").get(0);
		Assert.assertEquals(function.getExternalVariables().size(), 1);
	}

	/**
	 * namespace in namespace
	 */
	@Test
	public void test5() {
		File p = new File(Utils.normalizePath(Paths.EXTERNAL_VARIABLE_DETECTER_TEST));
		ProjectParser parser = new ProjectParser(p);

		FunctionNode function = (FunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "test5()").get(0);
		Assert.assertEquals(function.getExternalVariables().size(), 4);
	}
}
