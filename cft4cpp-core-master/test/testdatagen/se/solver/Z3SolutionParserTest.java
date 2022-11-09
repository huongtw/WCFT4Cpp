package testdatagen.se.solver;

import org.junit.Assert;
import org.junit.Test;

public class Z3SolutionParserTest {

	@Test
	public void test0() {
		String Z3Solution = "sat\r\n" + "(model \r\n" + "  (define-fun tvw_l2 () Int\r\n" + "    1)\r\n"
				+ "  (define-fun tvw_l1 () Int\r\n" + "    1)\r\n" + "  (define-fun tvw_t1 ((x!1 Int)) Int\r\n"
				+ "    (ite (= x!1 0) 1\r\n" + "      2))\r\n" + "  (define-fun tvw_t2 ((x!1 Int)) Int\r\n"
				+ "    (ite (= x!1 0) 1\r\n" + "      1))\r\n" + ")\r\n" + "";
		String output = new Z3SolutionParser().getSolution(Z3Solution);
		Assert.assertEquals("l2=1;l1=1;t1[0]=1;t2[0]=1;", output);
	}

	@Test
	public void test1() {
		String Z3Solution = "sat\r\n" + "(model \r\n" + " (define-fun tvw_x ()		 Int\r\n" + " 0)\r\n"
				+ " (define-fun tvw_A ((x!1 Int)) Int\r\n" + " 0)\r\n" + ")";
		String output = new Z3SolutionParser().getSolution(Z3Solution);
		Assert.assertEquals("x=0;A[1]=0;", output);
	}

	@Test
	public void test2() {
		String Z3Solution = "(model\n(define-fun tvwhe () Real\n1.0)\n(define-fun		 tvwb_w () Real\n(- (/ 9981.0 10000.0))))";
		String output = new Z3SolutionParser().getSolution(Z3Solution);
		Assert.assertEquals("tvwhe=1.0;tvwb_w=-0.9981;", output);
	}

	@Test
	public void test3() {
		String Z3Solution = "(model\n(define-fun tvwx () Real\n(/ 1.0 10000.0))\n)";
		String output = new Z3SolutionParser().getSolution(Z3Solution);
		Assert.assertEquals("tvwx=0.0001;", output);
	}

	@Test
	public void test4() {
		String Z3Solution = "(error \"line 6 column 30: invalid function application, wrong number of arguments\")\nsat\n(model\n\n (define-fun tvwp ((x!1 Int) (x!2 Int)) Int\n (ite (and (= x!1 0) (= x!2 0)) 10\n 10))\n)";
		String output = new Z3SolutionParser().getSolution(Z3Solution);
		Assert.assertEquals("tvwp[0][0]=10;", output);
	}

	@Test
	public void test5() {
		String Z3Solution = "unknown\n(error \"line 27 column 10: model is not available\")";
		String output = new Z3SolutionParser().getSolution(Z3Solution);
		Assert.assertEquals("", output);
	}

	/**
	 * <pre>
	sat
	(model 
	(define-fun tvw_n () Int
	2)
	(define-fun tvw_a ((x!1 Int)) Int
	(ite (= x!1 1) 0
	(ite (= x!1 0) (- 1)
	  0)))
	)
	 * </pre>
	 */
	@Test
	public void test6() {
		String Z3Solution = "sat\r\n" + "(model \r\n" + "  (define-fun tvw_n () Int\r\n" + "    2)\r\n"
				+ "  (define-fun tvw_a ((x!1 Int)) Int\r\n" + "    (ite (= x!1 1) 0\r\n"
				+ "    (ite (= x!1 0) (- 1)\r\n" + "      0)))\r\n" + ")";
		String output = new Z3SolutionParser().getSolution(Z3Solution);
		Assert.assertEquals("n=2;a[1]=0;a[0]=(-1);", output);
	}
}
