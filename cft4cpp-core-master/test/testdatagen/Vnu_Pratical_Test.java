package testdatagen;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import config.IFunctionConfig;
import config.Paths;

public class Vnu_Pratical_Test extends AbstractJUnitTest {

	@Test
	public void test1() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "Tritype(int,int,int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test2() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "Merge1(int[3],int[3],int[6])", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test3() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "Merge2(int[],int[],int[],int,int)",
				null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test4() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "uninit_var(int[3],int[3])", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test5() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "Bsearch(int[10],int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test6() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST,
				"ArrayCmp(int,unsigned char*,unsigned char*)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test7() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "f(int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test8() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "spec_f(int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test9() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "bsort(int*,int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test10() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "Fibonacci(int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test11() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "find_maximum(int[],int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test12() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "add_digits(int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test14() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "check_prime(int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test16() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "power(int,int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test17() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "print_floyd(int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test18() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "find_minimum(int[],int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test19() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "linear_search1(long[],long,long)",
				null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test20() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "linear_search2(long*,long,long)",
				null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test21() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "bubble_sort(long[],long)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test22() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "concatenate(char[],char[])", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test23() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "concatenate_string(char*,char*)",
				null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test24() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "reverse2(char*,int,int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test25() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "copy_string(char*,char*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test26() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "string_length(char*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test27() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "compare_string(char*,char*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test28() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "check_vowel(char)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test29() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "check_subsequence(char[],char[])",
				null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test30() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "find_frequency(char[],int[])", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test31() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "bsort(int*,int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test32() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "reverse_array(int*,int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test33() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "string_length1(char*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test34() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "merge(int[],int,int[],int,int[])",
				null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test36() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "check_anagram(char[],char[])", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

}
