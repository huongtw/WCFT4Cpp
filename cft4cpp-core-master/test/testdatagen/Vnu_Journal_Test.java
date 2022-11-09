package testdatagen;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import config.IFunctionConfig;
import config.Paths;

/**
 * Contain all of tests used in the experiment
 * 
 * @author Duc Anh Nguyen
 *
 */
public class Vnu_Journal_Test extends AbstractJUnitTest {
	public static final int BFS = 0;
	public static final int UNVISITED_VS_RANDOM_COMBINATION = 1;
	public static final int UNVISITED = 2;
	public static final int RANDOM = 3;
	public static final int DFS = 4;
	public static final int ANYTHING = -1;
	public static final int FOLLOW_CONFIGURATION_IN_CODE = -2;

	public static int TEST_DATA_SELECTION_STRATEGY = BFS;

	@Test
	public void test1() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "mergeTwoArray(int[],int,int[],int,int[])", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test2() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "linear_search1(long[],long,long)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test3() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "linear_search2(long*,long,long)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test4() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "binarySearch(int[],int,int,int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test5() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "NaivePatternSearch(char*,char*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test6() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "computeLPSArray(char*,int,int*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test7() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.JOURNAL_TEST, "KMPSearch(char*,char*)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test8() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "selectionSort(int[],int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test9() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.JOURNAL_TEST, "bubbleSort1(int[],int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test10() throws LineUnavailableException {

		Assert.assertEquals(true,
				generateTestdata(Paths.JOURNAL_TEST, "bubbleSort2(int[],int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test11() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "insertionSort(int[],int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test12() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "quickSort(int[],int,int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test13() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "quickSortIterative(int[],int,int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	@Ignore
	public void test14() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "quickSortRecur(struct Node*,struct Node*)",
				null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test15() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "merge(int[],int,int,int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test16() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "mergeSort(int[],int,int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test17() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.JOURNAL_TEST, "Tritype(int,int,int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test18() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "uninit_var(int[3],int[3])", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test19() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "ArrayCmp(int,unsigned char*,unsigned char*)",
				null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test22() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.JOURNAL_TEST, "Fibonacci(int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test23() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.JOURNAL_TEST, "find_maximum(int[],int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test24() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.JOURNAL_TEST, "add_digits(int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test25() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.JOURNAL_TEST, "reverse(long)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test26() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.JOURNAL_TEST, "check_prime(int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test27() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "check_armstrong(long long)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test28() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.JOURNAL_TEST, "power(int,int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test29() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.JOURNAL_TEST, "print_floyd(int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test30() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.JOURNAL_TEST, "find_minimum(int[],int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test31() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "concatenate(char[],char[])", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test32() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "concatenate_string(char*,char*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test33() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.JOURNAL_TEST, "reverse2(char*,int,int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test34() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "copy_string(char*,char*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test35() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.JOURNAL_TEST, "string_length(char*)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test36() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "compare_string(char*,char*)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test37() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.JOURNAL_TEST, "check_vowel(char)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test38() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "check_subsequence(char[],char[])", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test39() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "find_frequency(char[],int[])", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test40() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.JOURNAL_TEST, "reverse_array(int*,int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test41() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.JOURNAL_TEST, "string_length1(char*)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test42() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "check_anagram(char[],char[])", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test43() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "printClosest(int[],int,int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test44() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.JOURNAL_TEST, "largestNum(int,int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test45() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "frequencyDigits(int,int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test46() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.JOURNAL_TEST, "lds(int[],int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test47() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.JOURNAL_TEST, "getPriority(char)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test48() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.JOURNAL_TEST, "printDistinctPFs(int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test49() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.JOURNAL_TEST, "steps(int,int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test50() throws LineUnavailableException {

		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "minimumNumberOfDigits(int,int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test51() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "printSmallestNumber(int,int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test52() throws LineUnavailableException {
		Assert.assertEquals(true, generateTestdata(Paths.JOURNAL_TEST, "binomialCoeff(int[],int)", null,
				IFunctionConfig.BRANCH_COVERAGE));
	}

	@Test
	public void test53() throws LineUnavailableException {
		Assert.assertEquals(true,
				generateTestdata(Paths.JOURNAL_TEST, "sumOfproduct(int)", null, IFunctionConfig.BRANCH_COVERAGE));
	}

}
