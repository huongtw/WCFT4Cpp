package testdatagen;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import config.IFunctionConfig;
import config.Paths;

public class Tsdvr12_ViewSource_Test extends AbstractJUnitTest {
	@Test
	public void test1() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_2, "foo()", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test2() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_2, "bubbleSort(int[],int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test3() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_2, "CFGIfElse(int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test4() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_2, "CFGSwitch(int,int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test6() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_2, "CFGException(int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}
}
