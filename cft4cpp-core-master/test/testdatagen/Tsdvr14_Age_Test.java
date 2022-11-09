package testdatagen;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import config.IFunctionConfig;
import config.Paths;

public class Tsdvr14_Age_Test extends AbstractJUnitTest {
	@Test
	public void test1() throws LineUnavailableException {
		boolean reachCoverageObjective = generateTestdata(Paths.TSDV_R1_4, "bmi(float,float)", null,
				IFunctionConfig.BRANCH_COVERAGE);
		Assert.assertEquals(true, reachCoverageObjective);
	}

	@Test
	@Ignore
	public void test3() throws LineUnavailableException {
		boolean reachCoverageObjective = generateTestdata(Paths.TSDV_R1_4, "calculateAge(Date,Date)", null,
				IFunctionConfig.BRANCH_COVERAGE);
		Assert.assertEquals(true, reachCoverageObjective);
	}

	@Test
	@Ignore
	public void test4() throws LineUnavailableException {
		boolean reachCoverageObjective = generateTestdata(Paths.TSDV_R1_4, "calculateZodiac(Date)", null,
				IFunctionConfig.BRANCH_COVERAGE);
		Assert.assertEquals(true, reachCoverageObjective);
	}

}
