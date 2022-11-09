package testdatagen;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Assert;
import org.junit.Test;

import config.IFunctionConfig;
import config.Paths;

public class Tsdvr14_Cities_Test extends AbstractJUnitTest {
	@Test
	public void test1() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_4, "ch_cap(char)", null,
				IFunctionConfig.BRANCH_COVERAGE ));
	}

	@Test
	public void test2() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_4, "ch_eqi(char,char)", null,
				IFunctionConfig.BRANCH_COVERAGE ));
	}

	@Test
	public void test3() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_4, "ch_to_digit(char)", null,
				IFunctionConfig.BRANCH_COVERAGE ));
	}

	@Test
	public void test4() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_4, "degrees_to_radians(double)", null,
				IFunctionConfig.BRANCH_COVERAGE ));
	}

	@Test
	public void test5() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_4, "dms_to_radians(int[])", null,
				IFunctionConfig.BRANCH_COVERAGE ));
	}

	@Test
	public void test6() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_4, "i4_sign(int)", null,
				IFunctionConfig.BRANCH_COVERAGE ));
	}

	@Test
	public void test7() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_4, "lat_char(int)", null,
				IFunctionConfig.BRANCH_COVERAGE ));
	}

	@Test
	public void test8() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_4, "r8_abs(double)", null,
				IFunctionConfig.BRANCH_COVERAGE ));
	}
}
