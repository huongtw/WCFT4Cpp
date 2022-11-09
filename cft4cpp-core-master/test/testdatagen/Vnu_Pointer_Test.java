package testdatagen;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import config.IFunctionConfig;
import config.Paths;

public class Vnu_Pointer_Test extends AbstractJUnitTest {
	@Test
	public void test1() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "pointerTest0(const int*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test2() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "pointerTest1(const int*,int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test3() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "pointerTest2(const int*,int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test4() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "pointerTest3(int*,int*,int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test5() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "pointerTest4(int*,const int&)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test6() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "pointerTest5(int*,int*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test7() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "pointerTest6(int*,int*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test8() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "pointerTest7(int*,int*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test9() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "pointerTest8(int*,int*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test10() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "pointerTest9(int*,int*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test11() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "pointerTest10(int*,int*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test12() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "pointerTest11(int*,int&)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test14() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "pointerTest13(int[5],int*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test16() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "pointerTest15(char*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test17() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "pointerTest16(char*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test18() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "pointerTest17(int*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test19() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "pointerTest18(int*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test20() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "pointerTest19(int*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test21() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "pointerTest20(int*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test22() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "pointerTest21(int*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test23() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "pointerTest22(int*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test24() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "pointerTest23(int**)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test25() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "pointerTest24(const bool*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test26() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "pointerTest25(char*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}
}
