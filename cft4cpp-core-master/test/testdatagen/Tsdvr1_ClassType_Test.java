package testdatagen;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Assert;
import org.junit.Test;

import config.IFunctionConfig;
import config.Paths;

public class Tsdvr1_ClassType_Test extends AbstractJUnitTest {
	@Test
	public void test1() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1, "SimpleTest(Class1)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test2() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "getX()", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test3() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "getXEx()", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test4() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1, "SimpleMethodTest()", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test5() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1, "SimpleTest2(Class2)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test6() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1, "SimpleTest3(Class2)", null, IFunctionConfig.BRANCH_COVERAGE));
	}
}
