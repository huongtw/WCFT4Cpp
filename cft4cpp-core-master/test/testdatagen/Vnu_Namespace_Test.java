package testdatagen;

import java.io.File;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import config.IFunctionConfig;
import config.Paths;

@Ignore
public class Vnu_Namespace_Test extends AbstractJUnitTest {
	@Test
	public void test1() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST,
				"nsTest0" + File.separator + "Student::isAvailable(int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test2() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "Student::isAvailable2(int)",
				null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test4() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST,
						"nsTest1" + File.separator + "Student" + File.separator + "isAvailable(int)", null,
						IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test5() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST,
				"nsTest2" + File.separator + "isAvailable(int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test6() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST,
						"nsTest3" + File.separator + "second_space" + File.separator + "isAvailable(int)", null,
						IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test7() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "nsTest4::func1(int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test8() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST,
				"nsTest4" + File.separator + "func2(int,int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test9() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST,
				"nsTest5" + File.separator + "test1(int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test10() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST,
				"nsTest5" + File.separator + "test2(int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}
}
