package testdatagen;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import config.IFunctionConfig;
import config.Paths;

public class Tsdvr12_StorageKeyword_Test extends AbstractJUnitTest {
	@Rule
	public Timeout globalTimeout = Timeout.seconds(22);
	
	@Test
	public void test1() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_2, "RegisterKeywordTest(int[],int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test2() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_2, "AddTest(int,int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test3() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_2, "MathUtils::MinusTest(int,int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test4() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_2, "SimpleMarcoTest(int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test5() throws LineUnavailableException {

		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_2, "MarcoAsFunctionTest(int,int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test6() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_2, "IfDefTest(int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test7() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_2, "Abs(int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test8() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_2, "NestedTest(Outer)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test9() throws LineUnavailableException {

		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_2, "Divide(int,int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test10() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_2, "PointerTest(Outer*)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test11() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_2, "LinkedNodeTest(Node*)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test12() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_2, "mmin3_extern(int,int,int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test14() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_2, "SumRecursive(int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test15() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_2, "mmin3(int,int,int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test16() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_2, "mmin(int,int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test17() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_2, "ArrayOfStructTest(Simple1[])", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test18() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_2, "StructOfArrayTest(StructWArray)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test19() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_2, "PointerOfStructTest(Simple1*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test20() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_2, "StructOfPointerTest(StructWPointer)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test21() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_2, "setMyStruct(const Simple1&)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test22() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_2, "getMyStruct()", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test23() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_2, "ClassOfStructTest(ClassWithStruct)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test24() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_2, "setMyPointer(int*)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test25() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_2, "getMyPointer()", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test26() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_2, "ClassOfPointerTest(ClassWithPointer)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test27() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_2, "SimpleIntCharCast(int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test28() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_2, "StaticCast(int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}
}
