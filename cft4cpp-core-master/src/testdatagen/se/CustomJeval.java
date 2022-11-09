package testdatagen.se;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.jeval.Evaluator;
import utils.IRegex;
import utils.Utils;

public class CustomJeval extends Evaluator {
	public static final String TRUE = "1";
	public static final String FALSE = "0";

	@Override
	public String evaluate(String expression) {
		String output = "";
		/*
		 * Prevent 0.00001 -------> 1E-4
		 */
		if (expression.matches(IRegex.FLOAT_REGEX) || expression.matches(IRegex.NUMBER_REGEX))
			output = expression;
		else
			try {
				output = this.simplifyFloatNumber(super.evaluate(expression));
			} catch (Exception e) {
				output = this.simplifyFloatNumber(expression);
			}
		
		output = Utils.transformFloatNegativeE(output);
		output = Utils.transformFloatPositiveE(output);
		return output;
	}

	/**
	 * Delete .0 in expression
	 *
	 * @param expression
	 * @return
	 */
	private String simplifyFloatNumber(String expression) {
		expression = expression + "@";// to simplify regex
		Matcher m = Pattern.compile("(\\d)\\.0([^\\d])").matcher(expression);
		StringBuffer sb = new StringBuffer();
		while (m.find())
			m.appendReplacement(sb, m.group(1) + m.group(2));
		m.appendTail(sb);
		return sb.deleteCharAt(sb.length() - 1).toString();
	}
}
