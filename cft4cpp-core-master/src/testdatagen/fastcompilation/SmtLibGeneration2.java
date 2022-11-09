package testdatagen.fastcompilation;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import testdatagen.se.ISymbolicExecution;
import testdatagen.se.PathConstraint;
import testdatagen.se.memory.ISymbolicVariable;
import testdatagen.se.solver.ISmtLibGeneration;
import testdatagen.se.solver.SmtLibv2Normalizer2;
import testdatagen.testdatainit.VariableTypes;
import utils.SpecialCharacter;

/**
 * Generate SMT-Lib file
 *
 * @author anhanh
 */
public class SmtLibGeneration2 implements ISmtLibGeneration {
	final static Logger logger = Logger.getLogger(SmtLibGeneration2.class);

	// List of test cases
	private List<ISymbolicVariable> testcases = new ArrayList<>();
	// List of path constraints
	private List<PathConstraint> constraints = new ArrayList<>();
	// SMT-Lib content
	private String smtLib = "";

	public SmtLibGeneration2(List<ISymbolicVariable> testcases, List<PathConstraint> constraints) {
		this.testcases = testcases;
		this.constraints = constraints;
	}

	public static void main(String[] args) throws Exception {
	}

	@Override
	public void generate() throws Exception {
		smtLib = ISmtLibGeneration.OPTION_TIMEOUT + SpecialCharacter.LINE_BREAK + getDeclarationFun(testcases);

		// Generate body of the smt-lib file
		if (constraints.size() == 0)
			smtLib = EMPTY_SMT_LIB_FILE;
		else {
			for (PathConstraint constraint : constraints)
				switch (constraint.getConstraint()) {
				case ISymbolicExecution.NO_SOLUTION_CONSTRAINT:
					smtLib = EMPTY_SMT_LIB_FILE;
					return;
				case ISymbolicExecution.ALWAYS_TRUE_CONSTRAINT:
					// nothing to do
					break;
				default:
					SmtLibv2Normalizer2 normalizer = new SmtLibv2Normalizer2(constraint.getConstraint());
					normalizer.normalize();

					if (normalizer.getNormalizedSourcecode() != null
							&& normalizer.getNormalizedSourcecode().length() > 0) {
						smtLib += "(assert" + normalizer.getNormalizedSourcecode() + ")" + SpecialCharacter.LINE_BREAK;
					} else {
						// If we can not normalize the constraint, we ignore it :)
					}
					break;
				}

			smtLib += ISmtLibGeneration.SOLVE_COMMAND;
		}
		//		logger.debug(smtLib);
	}

	/**
	 * Generate "(declare-fun...)"
	 *
	 * @return
	 * @throws Exception
	 */
	private String getDeclarationFun(List<ISymbolicVariable> variables) throws Exception {
		String output = "";
		for (ISymbolicVariable var : variables) {
			String type = var.getType();
			if (VariableTypes.isBasic(type))
				switch (VariableTypes.getType(type)) {
				/**
				 * float type
				 */
				case VariableTypes.BASIC.NUMBER.FLOAT.FLOAT:
				case VariableTypes.BASIC.NUMBER.FLOAT.DOUBLE:

				case VariableTypes.BASIC.NUMBER.FLOAT.FLOAT + VariableTypes.REFERENCE:
				case VariableTypes.BASIC.NUMBER.FLOAT.DOUBLE + VariableTypes.REFERENCE:
					output += "(declare-fun " + convertToVariableInSmt(var.getName()) + " () Real)"
							+ SpecialCharacter.LINE_BREAK;
					break;
				default:
					/**
					 * integer type
					 */
					output += "(declare-fun " + convertToVariableInSmt(var.getName()) + " () Int)"
							+ SpecialCharacter.LINE_BREAK;
					break;
				}
			else if (VariableTypes.isOneDimension(type) || VariableTypes.isOneLevel(type))
				/**
				 * float type
				 */
				switch (VariableTypes.getType(type)) {

				case VariableTypes.BASIC.NUMBER.FLOAT.FLOAT + VariableTypes.ONE_DIMENSION:
				case VariableTypes.BASIC.NUMBER.FLOAT.DOUBLE + VariableTypes.ONE_DIMENSION:

				case VariableTypes.BASIC.NUMBER.FLOAT.FLOAT + VariableTypes.ONE_LEVEL:
				case VariableTypes.BASIC.NUMBER.FLOAT.DOUBLE + VariableTypes.ONE_LEVEL:

					output += "(declare-fun " + convertToVariableInSmt(var.getName()) + " (Int) Real)"
							+ SpecialCharacter.LINE_BREAK;
					break;

				default:
					/**
					 * integer type
					 */
					output += "(declare-fun " + convertToVariableInSmt(var.getName()) + " (Int) Int)"
							+ SpecialCharacter.LINE_BREAK;
					break;
				}
			else if (VariableTypes.isTwoDimension(type) || VariableTypes.isTwoLevel(type))
				/**
				 * float type
				 */
				switch (VariableTypes.getType(type)) {

				case VariableTypes.BASIC.NUMBER.FLOAT.FLOAT + VariableTypes.TWO_DIMENSION:
				case VariableTypes.BASIC.NUMBER.FLOAT.DOUBLE + VariableTypes.TWO_DIMENSION:
				case VariableTypes.BASIC.NUMBER.FLOAT.FLOAT + VariableTypes.TWO_LEVEL:
				case VariableTypes.BASIC.NUMBER.FLOAT.DOUBLE + VariableTypes.TWO_LEVEL:

					output += "(declare-fun " + convertToVariableInSmt(var.getName()) + " (Int Int) Real)"
							+ SpecialCharacter.LINE_BREAK;
					break;

				default:
					/**
					 * integer type
					 */
					output += "(declare-fun " + convertToVariableInSmt(var.getName()) + " (Int Int) Int)"
							+ SpecialCharacter.LINE_BREAK;
					break;
				}
			else
				output += "; dont support " + var.getType() + SpecialCharacter.LINE_BREAK;
		}
		return output;

	}

	@Override
	public String getSmtLibContent() {
		return smtLib;
	}

	protected String convertToVariableInSmt(String name) {
		return ISymbolicVariable.PREFIX_SYMBOLIC_VALUE
				+ name.replace("[", ISymbolicVariable.ARRAY_OPENING).replace("]", ISymbolicVariable.ARRAY_CLOSING)
						.replace(".", ISymbolicVariable.SEPARATOR_BETWEEN_STRUCTURE_NAME_AND_ITS_ATTRIBUTES)
						.replaceAll("->", ISymbolicVariable.SEPARATOR_BETWEEN_STRUCTURE_NAME_AND_ITS_ATTRIBUTES);
	}
}
