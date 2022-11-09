package testdatagen;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import config.IFunctionConfig;
import config.Paths;

public class Tsdvr1_Namespace_Test extends AbstractJUnitTest {
	@Test
	@Ignore
	public void test1() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1, "Level2SimpleTest(X)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test2() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "Level2MultipleNsTest(::X,::ns1::X,X)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test3() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1, "Level1SimpleTest(X)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test4() throws LineUnavailableException {

		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "Level1MultipleNsTest(::X,X,ns2::X)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test5() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "Level0MultipleNsTest(X,ns1::X,ns1::ns2::X)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test6() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "FunctionInAnnonymousNsTest(int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test7() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1, "Level0UsingNsTest(int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test8() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1, "Level0UsingAutoNsTest(Y)", null, IFunctionConfig.BRANCH_COVERAGE));
	}
}
