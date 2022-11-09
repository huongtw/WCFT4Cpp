package testdatagen;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import config.IFunctionConfig;
import config.Paths;

public class Tsdvr1_Struct_Test extends AbstractJUnitTest {
	@Test
	public void test1() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1, "SimpleTest(Student)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test2() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "SimpleTestWithKeyword(struct Student)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test3() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "NestedStructTest(Class,Class)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}
}
