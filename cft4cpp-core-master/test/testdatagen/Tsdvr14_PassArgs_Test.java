package testdatagen;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import config.IFunctionConfig;
import config.Paths;

public class Tsdvr14_PassArgs_Test extends AbstractJUnitTest {
	@Test
	@Ignore
	public void test1() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_4, "PassAsConstRefTest(const BigData&)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test2() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_4, "PassAsConstValueTest(const BigData)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test3() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_4, "PassAsRefTest(BigData&)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test4() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_4, "PassAsRefTest(BigData&)", null, IFunctionConfig.BRANCH_COVERAGE));
	}
}
