package testdatagen.fastcompilation;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Assert;
import org.junit.Test;

import config.IFunctionConfig;
import config.Paths;
import testdatagen.AbstractJUnitTest;

/**
 * Vnu_Basic_Test
 */
public class FastTestdataGenerationTest_basic extends AbstractJUnitTest {

	@Test
	public void Vnu_Basic_Test1() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest1(int,int)", null,
				IFunctionConfig.SUBCONDITION));
	}

	@Test
	public void Vnu_Basic_Test2() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest2(int,int)", null,
				IFunctionConfig.SUBCONDITION));
	}

	@Test
	public void Vnu_Basic_Test3() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest3(int,int)", null,
				IFunctionConfig.SUBCONDITION));
	}

	@Test
	public void Vnu_Basic_Test4() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest4(int,int)", null,
				IFunctionConfig.SUBCONDITION));
	}

	@Test
	public void Vnu_Basic_Test5() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest5(int,int)", null,
				IFunctionConfig.SUBCONDITION));
	}

	@Test
	public void Vnu_Basic_Test6() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest6(int)", null, IFunctionConfig.SUBCONDITION));
	}

	@Test
	public void Vnu_Basic_Test7() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest7(int)", null, IFunctionConfig.SUBCONDITION));
	}

	@Test
	public void Vnu_Basic_Test8() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest8(int)", null, IFunctionConfig.SUBCONDITION));
	}

	@Test
	public void Vnu_Basic_Test9() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest9(int)", null, IFunctionConfig.SUBCONDITION));
	}

	@Test
	public void Vnu_Basic_Test10() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest10(int)", null,
				IFunctionConfig.SUBCONDITION));
	}

	@Test
	public void Vnu_Basic_Test11() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest11(int)", null,
				IFunctionConfig.SUBCONDITION));
	}

	@Test // HALF-PASS: handle cast, e.g., int a = 1/2
	public void Vnu_Basic_Test13() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest12(int)", null,
				IFunctionConfig.SUBCONDITION));
	}

	@Test // HALF-PASS: handle cast, e.g., int a = 1/2
	public void Vnu_Basic_Test15() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest13(int)", null,
				IFunctionConfig.SUBCONDITION));
	}

	@Test
	public void Vnu_Basic_Test16() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest14(char,int)", null,
				IFunctionConfig.SUBCONDITION));
	}

	@Test
	public void Vnu_Basic_Test17() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest15(bool)", null,
				IFunctionConfig.SUBCONDITION));
	}

	@Test
	public void Vnu_Basic_Test18() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest16(bool)", null,
				IFunctionConfig.SUBCONDITION));
	}

	@Test
	public void Vnu_Basic_Test19() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest17(int,int,int)", null,
				IFunctionConfig.SUBCONDITION));
	}

	@Test
	public void Vnu_Basic_Test20() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest18(bool,int)", null,
				IFunctionConfig.SUBCONDITION));
	}

	@Test
	public void Vnu_Basic_Test22() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest20(bool,bool)", null,
				IFunctionConfig.SUBCONDITION));
	}

	@Test
	public void Vnu_Basic_Test23() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest21(bool,bool)", null,
				IFunctionConfig.SUBCONDITION));
	}

}
