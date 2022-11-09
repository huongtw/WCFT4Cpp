package testdatagen;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import config.IFunctionConfig;
import config.Paths;

public class Tsdvr1_PointerType_Test extends AbstractJUnitTest {
	@Test
	public void test1() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1, "Level1InputSimple(int*)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test2() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1, "Level1InputAdvance(int*)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test3() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1, "Level2InputSimple(int**)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test4() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1, "Level2InputAdvance(int**)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test5() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1, "Level1OutputSimple(int*)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test6() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1, "Level1OutputAdvance(int*)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test7() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1, "Level2OutputSimple(int**)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test8() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1, "Level2OutputAdvance(int**)", null, IFunctionConfig.BRANCH_COVERAGE));
	}
}
