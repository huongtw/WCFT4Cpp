package testdatagen.se;

import org.junit.Assert;
import org.junit.Test;

/**
 * See #{ExpressionRewriterUtils.class}
 *
 * @author ducanhnguyen
 */
public class ExpressionRewriterUtilsTest {
	@Test
	public void shortenExpressionByCalculatingValueTest() throws Exception {
		// Type 1.
		Assert.assertEquals("sv.age > 0", ExpressionRewriterUtils.shortenExpressionByCalculatingValue("sv.age > 0"));
		Assert.assertEquals("3+x+1>0", ExpressionRewriterUtils.shortenExpressionByCalculatingValue("3+x+(1)>0"));
		Assert.assertEquals("3+x>0", ExpressionRewriterUtils.shortenExpressionByCalculatingValue("(1+2)+x>0"));
		Assert.assertEquals("10+x+3>0",
				ExpressionRewriterUtils.shortenExpressionByCalculatingValue("(1+2+3+4)+x+(1+2)>0"));
		Assert.assertEquals("10+x+5>0",
				ExpressionRewriterUtils.shortenExpressionByCalculatingValue("(1+2+3+4)+x+(1+2+(1+1))>0"));
		Assert.assertEquals("10", ExpressionRewriterUtils.shortenExpressionByCalculatingValue("1+2+3+4"));

		// Type 2. Array index
		Assert.assertEquals("a[10]", ExpressionRewriterUtils.shortenExpressionByCalculatingValue("a[10]"));
		Assert.assertEquals("a[10]", ExpressionRewriterUtils.shortenExpressionByCalculatingValue("a[(10)]"));
		Assert.assertEquals("a[11]", ExpressionRewriterUtils.shortenExpressionByCalculatingValue("a[(10)+1]"));
		Assert.assertEquals("a[14]", ExpressionRewriterUtils.shortenExpressionByCalculatingValue("a[(10)+1+(1+2)]"));
	}

	@Test
	public void convertOneLevelPointerItemToArrayItemTest() throws Exception {
		// Type 1. One level pointer
		Assert.assertEquals("a == p1[4]", ExpressionRewriterUtils.convertPointerItemToArrayItem("a == *(p1+4)"));
		Assert.assertEquals("a==p1[0]", ExpressionRewriterUtils.convertPointerItemToArrayItem("a==*p1"));
		Assert.assertEquals("a==p1[0]", ExpressionRewriterUtils.convertPointerItemToArrayItem("a==*(p1)"));
		Assert.assertEquals("a==(p1[0])", ExpressionRewriterUtils.convertPointerItemToArrayItem("a==(*p1)"));
		Assert.assertEquals("a==p1[0]", ExpressionRewriterUtils.convertPointerItemToArrayItem("a==*(p1+0)"));
		Assert.assertEquals("a==p1[-1]", ExpressionRewriterUtils.convertPointerItemToArrayItem("a==*(p1-1)"));
		Assert.assertEquals("a== p2[3]", ExpressionRewriterUtils.convertPointerItemToArrayItem("a== *(p2+1 + (1+1))"));

		// Type 2. Two level pointer
		Assert.assertEquals("a == p1[4][0]", ExpressionRewriterUtils.convertPointerItemToArrayItem("a == **(p1+4)"));
		Assert.assertEquals("a==p1[0][0]", ExpressionRewriterUtils.convertPointerItemToArrayItem("a==**p1"));
		Assert.assertEquals("a==p1[0][0]", ExpressionRewriterUtils.convertPointerItemToArrayItem("a==**(p1)"));
		Assert.assertEquals("a==(p1[0][0])", ExpressionRewriterUtils.convertPointerItemToArrayItem("a==(**p1)"));
		Assert.assertEquals("a==p1[0][0]", ExpressionRewriterUtils.convertPointerItemToArrayItem("a==**(p1+0)"));
		Assert.assertEquals("a==p1[-1][0]", ExpressionRewriterUtils.convertPointerItemToArrayItem("a==**(p1-1)"));
		Assert.assertEquals("a== p2[3][0]",
				ExpressionRewriterUtils.convertPointerItemToArrayItem("a== **(p2+1 + (1+1))"));
		Assert.assertEquals("p[1][0]", ExpressionRewriterUtils.convertPointerItemToArrayItem("**(p+1)"));
	}

	@Test
	public void convertconvertCharToNumberTest() throws Exception {
		Assert.assertEquals("1+97", ExpressionRewriterUtils.convertCharToNumber("1+'a'"));
		Assert.assertEquals("x=97", ExpressionRewriterUtils.convertCharToNumber("x='a'"));
	}

	@Test
	public void transformFloatNegativeETest() {
		Assert.assertEquals("1", ExpressionRewriterUtils.transformFloatNegativeE("1E-0"));
		Assert.assertEquals("0.1", ExpressionRewriterUtils.transformFloatNegativeE("1E-1"));
		Assert.assertEquals("0.01", ExpressionRewriterUtils.transformFloatNegativeE("1E-2"));
		Assert.assertEquals("12.3", ExpressionRewriterUtils.transformFloatNegativeE("123E-1"));
		Assert.assertEquals("123", ExpressionRewriterUtils.transformFloatNegativeE("123E-0"));
		Assert.assertEquals("12.3", ExpressionRewriterUtils.transformFloatNegativeE("123E-1"));
		Assert.assertEquals("0.0123", ExpressionRewriterUtils.transformFloatNegativeE("123E-4"));
	}

	@Test
	public void transformFloatPositiveETest() {
		Assert.assertEquals("1", ExpressionRewriterUtils.transformFloatPositiveE("1E+0"));
		Assert.assertEquals("10", ExpressionRewriterUtils.transformFloatPositiveE("1E+1"));
		Assert.assertEquals("100", ExpressionRewriterUtils.transformFloatPositiveE("1E+2"));
		Assert.assertEquals("1230", ExpressionRewriterUtils.transformFloatPositiveE("123E+1"));
		Assert.assertEquals("123", ExpressionRewriterUtils.transformFloatPositiveE("123E+0"));
		Assert.assertEquals("123000", ExpressionRewriterUtils.transformFloatPositiveE("123E+3"));
	}

	@Test
	public void simplifyFloatNumberTest() {
		Assert.assertEquals("1", ExpressionRewriterUtils.simplifyFloatNumber("1.0"));
		Assert.assertEquals("1+1", ExpressionRewriterUtils.simplifyFloatNumber("1.0+1.0"));
		Assert.assertEquals("1+1", ExpressionRewriterUtils.simplifyFloatNumber("1+1.0"));
	}

	@Test
	public void tranformNegativeTest() {
		Assert.assertEquals("(-1)*1.0", ExpressionRewriterUtils.tranformNegative("-1.0"));
		Assert.assertEquals("(-1)*1", ExpressionRewriterUtils.tranformNegative("-1"));
		Assert.assertEquals("(-1)*1+(-1)*2", ExpressionRewriterUtils.tranformNegative("-1+-2"));
		Assert.assertEquals("(-1)*1+((-1)*2)", ExpressionRewriterUtils.tranformNegative("-1+(-2)"));
	}
}
