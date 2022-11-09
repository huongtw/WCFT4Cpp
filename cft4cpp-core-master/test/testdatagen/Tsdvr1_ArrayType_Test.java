package testdatagen;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import config.IFunctionConfig;
import config.Paths;

public class Tsdvr1_ArrayType_Test extends AbstractJUnitTest {
	@Test
	public void test1() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1, "Dim1InputSimple(int[])", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test2() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1, "Dim1InputAdvance(int[])", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test3() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1, "Dim2InputSimple(int[][1])", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test4() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1, "Dim2InputAdvance(int[][1])", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test5() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1, "Dim1OutputSimple(int[])", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test6() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1, "Dim1OutputAdvance(int[])", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test7() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1, "Dim2OutputSimple(int[][1])", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test8() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1, "Dim2OutputAdvance(int[][1])", null, IFunctionConfig.BRANCH_COVERAGE));
	}
}
