package testdatagen.se.solver;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import testdatagen.se.CustomJeval;
import testdatagen.se.ISymbolicExecution;
import testdatagen.se.memory.ISymbolicVariable;

/**
 * Convert solution in SMT-Solver z3 to the readable human format
 * <p>
 * <p>
 * Z3 link:https://github.com/Z3Prover/z3/releases
 *
 * @author anhanh
 */
public class Z3SolutionParser implements IZ3SolutionParser {
	boolean beforeLineIsArray = false;

	public Z3SolutionParser() {
	}

	public static void main(String[] args) {
		String Z3Solution = "(error \"line 6 column 30: invalid function application, wrong number of arguments\")\nsat\n(model\n\n (define-fun tvwp ((x!1 Int) (x!2 Int)) Int\n (ite (and (= x!1 0) (= x!2 0)) 10\n 10))\n)";
		String sol= "sat\r\n" + 
				"(model \r\n" + 
				"  (define-fun tvw_n () Int\r\n" + 
				"    2)\r\n" + 
				"  (define-fun tvw_a ((x!1 Int)) Int\r\n" + 
				"    (ite (= x!1 1) 0\r\n" + 
				"    (ite (= x!1 0) 0\r\n" + 
				"      0)))\r\n" + 
				")";
				
		System.out.println(new Z3SolutionParser().getSolution(sol));
	}

	@Override
	public String getSolution(String Z3Solution) {
		String solution = "";
		if (Z3Solution.equals(ISymbolicExecution.UNSAT_IN_Z3)) {

		} else {
			String[] lineList = Z3Solution.split("\n");
			String name = "";
			String value = "";
			boolean ignoreEndLine = false;

			for (String line : lineList)
				switch (getTypeOfLine(line)) {
				case KHAI_BAO_ID:
					ignoreEndLine = false;
					name = getName(line);
					break;

				case IF_THEN_ELSE_ID:
					ArrayList<String> indexList = getIndex(line);
					ArrayList<String> indexVariableList = getIndexVariable(line);
					value = getValueOfIte(line);

					String tmp = name;
					for (int i = 0; i < indexList.size(); i++)
						tmp = tmp.replace("[" + indexVariableList.get(i) + "]", "[" + indexList.get(i) + "]");

					solution += tmp + "=" + value + ",";
					ignoreEndLine = true;
					break;

				case VALUE_ID:
					if (!ignoreEndLine) {
						value = getValueOfVariable(line);
						value = new CustomJeval().evaluate(value);

						// ! is the signal of array
						if (name.contains("!"))
							solution += name.replace("x!", "") + "=" + value + ",";

						else
							solution += name + "=" + value + ",";
					}
					break;

				case ERROR:
				case UNKNOWN_ID:
					break;
				}
			if (solution.lastIndexOf(",") > 0)
				solution = solution.substring(0, solution.lastIndexOf(","));

			// Restore solution to its original format
			solution = solution.replace(ISymbolicVariable.PREFIX_SYMBOLIC_VALUE, "")
					.replace(ISymbolicVariable.SEPARATOR_BETWEEN_STRUCTURE_NAME_AND_ITS_ATTRIBUTES, ".")
					.replace(ISymbolicVariable.ARRAY_CLOSING, "]").replace(ISymbolicVariable.ARRAY_OPENING, "[")
					.replace(",", ";");

			if (solution.length() > 0 && !solution.endsWith(";"))
				solution += ";";
		}

		if (solution != null && solution.startsWith("=;"))
			solution = solution.substring(2);
		return solution;
	}

	/**
	 * " (ite (= x!1 0) 1\r" ---------> 0
	 * 
	 * @param ifThenElse
	 * @return
	 */
	private ArrayList<String> getIndex(String ifThenElse) {
		ArrayList<String> indexList = new ArrayList<>();
		Matcher m = Pattern.compile("=\\s(\\w+)!(\\d+)\\s([^\\)]+)").matcher(ifThenElse);
		while (m.find())
			indexList.add(m.group(3));
		return indexList;
	}

	/**
	 * " (ite (= x!1 0) 1\r" ----> "x!1"
	 * 
	 * @param ifThenElse
	 * @return
	 */
	private ArrayList<String> getIndexVariable(String ifThenElse) {
		ArrayList<String> indexList = new ArrayList<>();
		Matcher m = Pattern.compile("=\\s(\\w+!\\d+)\\s([^\\)]+)").matcher(ifThenElse);
		while (m.find())
			indexList.add(m.group(1));
		return indexList;
	}

	private String getName(String defineFun) {
		String nameFunction = "";
		Matcher m = Pattern.compile("\\(define-fun\\s+(\\w+)").matcher(defineFun);
		while (m.find()) {
			nameFunction = m.group(1);
			break;
		}

		// case "define-fun tvw_A ((x!1 Int)) Int"
		m = Pattern.compile("(\\w+!\\d+)").matcher(defineFun);
		while (m.find()) {
			nameFunction += "[" + m.group(0) + "]";
		}

		return nameFunction;
	}

	private int getTypeOfLine(String line) {
		if (line.contains(IZ3SolutionParser.KHAI_BAO))
			return IZ3SolutionParser.KHAI_BAO_ID;

		else if (line.contains(IZ3SolutionParser.IF_THEN_ELSE))
			return IZ3SolutionParser.IF_THEN_ELSE_ID;

		else if (line.contains(IZ3SolutionParser.MODEL))
			return IZ3SolutionParser.MODEL_ID;

		else if (line.equals(IZ3SolutionParser.END))
			return IZ3SolutionParser.END_ID;

		else if (line.equals(IZ3SolutionParser.SAT))
			return IZ3SolutionParser.SAT_ID;

		else if (line.equals(IZ3SolutionParser.UNSAT))
			return IZ3SolutionParser.UNSAT_ID;

		else if (line.equals(IZ3SolutionParser.UNKNOWN))
			return IZ3SolutionParser.UNKNOWN_ID;

		else if (line.startsWith("(error") || line.length() == 0)
			return IZ3SolutionParser.ERROR;

		return IZ3SolutionParser.VALUE_ID;
	}

	private String getValueOfIte(String ifThenElse) {
		String value = "";
		Matcher m = Pattern.compile("(\\(=\\s\\w+!\\d+\\s\\d+\\)\\s*)+(.*)").matcher(ifThenElse);
		while (m.find()) {
			value = m.group(2).replace(") ", "").replace(" ", "");
			break;
		}
		return value;
	}

	private String getValueOfVariable(String line) {
		String value = "";
		final String DEVIDE = "/";
		final String NEGATIVE = "-";
		/*
		 * Ex: (/ 1.0 10000.0))
		 */
		if (line.contains(DEVIDE) && !line.contains(NEGATIVE)) {
			int start = line.indexOf("(") + 1;
			int end = line.indexOf(")");
			String reducedLine = line.substring(start, end);

			String[] elements = reducedLine.split(" ");
			if (elements.length >= 3)
				value = elements[1] + elements[0] + elements[2];

		} else
		/*
		 * Ex: (- (/ 9981.0 10000.0))
		 */
		if (line.contains(DEVIDE) && line.contains(NEGATIVE)) {
			int start = line.lastIndexOf("(") + 1;
			int end = line.indexOf(")");
			String reducedLine = line.substring(start, end);

			String[] elements = reducedLine.split(" ");
			if (elements.length >= 3)
				value = NEGATIVE + "(" + elements[1] + elements[0] + elements[2] + ")";

		} else {
			Matcher m = Pattern.compile("(.*)\\)").matcher(line);
			while (m.find()) {
				value = m.group(1).replace(" ", "");
				break;
			}
		}
		return value;
	}
}
