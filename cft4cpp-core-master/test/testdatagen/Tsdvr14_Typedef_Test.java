package testdatagen;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import config.IFunctionConfig;
import config.Paths;

public class Tsdvr14_Typedef_Test extends AbstractJUnitTest {
	@Test
	public void test1() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_4, "SimpleTypeDefTest(MyInt)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test2() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_4, "PointerTypeDefTest(MyIntPtr)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test3() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_4, "CombineTypeDefTest(IntType,IntTypePtr,IntType2*)",
				null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test4() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.TSDV_R1_4, "ArrayTypeDefTest(Color)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test5() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_4, "AnnonymousStructTypeDefTest(MyStruct1)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test6() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_4, "RedeclareStructTypeDefTest(MyStruct3)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test7() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_4, "StructCombineTypeDefTest(MyStruct4,MyStruct5)",
				null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test8() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_4, "ChainingTypeDefTest(MyStruct33)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

}
