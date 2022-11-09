package testdatagen;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import config.IFunctionConfig;
import config.Paths;

public class Tsdvr14_Asaxxx_Test extends AbstractJUnitTest {
	@Test
	@Ignore
	public void test1() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_4, "alnorm(double,bool)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test2() throws LineUnavailableException {
		AbstractJUnitTest.ENABLE_MACRO_NORMALIZATION = true;
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_4, "student_noncentral_cdf_values(int*,int*,double*,double*,double*)",
						null, IFunctionConfig.BRANCH_COVERAGE));
		AbstractJUnitTest.ENABLE_MACRO_NORMALIZATION = false;
	}

	@Test
	@Ignore
	public void test3() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_4, "digamma(double,int*)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test4() throws LineUnavailableException {
		AbstractJUnitTest.ENABLE_MACRO_NORMALIZATION = true;
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_4, "psi_values(int*,double*,double*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
		AbstractJUnitTest.ENABLE_MACRO_NORMALIZATION = false;
	}
}
