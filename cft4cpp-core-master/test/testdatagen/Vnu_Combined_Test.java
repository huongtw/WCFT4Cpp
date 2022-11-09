package testdatagen;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import config.IFunctionConfig;
import config.Paths;

@Ignore
public class Vnu_Combined_Test extends AbstractJUnitTest {
	@Test
	public void test1() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.COMBINED_STATIC_AND_DYNAMIC_GENERATION, "simpleTest0(int)",
				null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test2() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.COMBINED_STATIC_AND_DYNAMIC_GENERATION, "simpleTest1(int)",
				null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test3() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.COMBINED_STATIC_AND_DYNAMIC_GENERATION, "simpleTest2(int,int)",
				null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test4() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.COMBINED_STATIC_AND_DYNAMIC_GENERATION, "simpleTest3(int,int)",
				null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test5() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.COMBINED_STATIC_AND_DYNAMIC_GENERATION, "simpleTest4(int,int)",
				null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test6() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.COMBINED_STATIC_AND_DYNAMIC_GENERATION, "simpleTest5(int,int)",
				null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test7() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.COMBINED_STATIC_AND_DYNAMIC_GENERATION,
				"simpleTest6(int,int,int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test8() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.COMBINED_STATIC_AND_DYNAMIC_GENERATION,
				"simpleTest7(int,int,int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test9() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.COMBINED_STATIC_AND_DYNAMIC_GENERATION,
				"struct_test0(SinhVien)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test10() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.COMBINED_STATIC_AND_DYNAMIC_GENERATION,
				"struct_test1(SinhVien)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test11() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.COMBINED_STATIC_AND_DYNAMIC_GENERATION,
						"class_test0(int,int*,int[],int[2],char,char*,char[],char[10],SinhVien*,SinhVien,SinhVien[])",
						null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test12() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.COMBINED_STATIC_AND_DYNAMIC_GENERATION,
				"class_test1(SinhVien)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test13() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.COMBINED_STATIC_AND_DYNAMIC_GENERATION,
				"class_test2(SinhVien)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test14() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.COMBINED_STATIC_AND_DYNAMIC_GENERATION,
				"class_test3(SinhVien)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test15() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.COMBINED_STATIC_AND_DYNAMIC_GENERATION,
				"class_test4(SinhVien)", null, IFunctionConfig.BRANCH_COVERAGE));
	}
}
