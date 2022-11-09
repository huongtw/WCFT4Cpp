package testdatagen.se.solver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;

import cfg.CFGGenerationforBranchvsStatementCoverage;
import cfg.testpath.IFullTestpath;
import cfg.testpath.PossibleTestpathGeneration;
import config.FunctionConfig;
import config.ISettingv2;
import config.ParameterBound;
import config.Paths;
import normalizer.FunctionNormalizer;
import parser.projectparser.ProjectParser;
import testdatagen.se.ISymbolicExecution;
import testdatagen.se.Parameter;
import testdatagen.se.PathConstraint;
import testdatagen.se.PathConstraints;
import testdatagen.se.SymbolicExecution;
import testdatagen.se.memory.ISymbolicVariable;
import testdatagen.testdatainit.VariableTypes;
import tree.object.FunctionNode;
import tree.object.IFunctionNode;
import tree.object.INode;
import tree.object.IVariableNode;
import tree.object.StructureNode;
import utils.SpecialCharacter;
import utils.Utils;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

/**
 * Generate SMT-Lib file
 *
 * @author anhanh
 */
public class SmtLibGeneration implements ISmtLibGeneration {
	final static Logger logger = Logger.getLogger(SmtLibGeneration.class);

	// List of test cases
	private List<IVariableNode> testcases = new ArrayList<>();
	// List of path constraints
	private List<PathConstraint> constraints = new ArrayList<>();
	// SMT-Lib content
	private String smtLib = "";

	public SmtLibGeneration(List<IVariableNode> testcases, List<PathConstraint> constraints) {
		this.testcases = testcases;
		this.constraints = constraints;
	}
	
	public SmtLibGeneration() {
		
	}

	public List<IVariableNode> getTestcases() {
		return testcases;
	}


	public void setTestcases(List<IVariableNode> testcases) {
		this.testcases = testcases;
	}


	public List<PathConstraint> getConstraints() {
		return constraints;
	}


	public void setConstraints(List<PathConstraint> constraints) {
		this.constraints = constraints;
	}


	public static void main(String[] args) throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.JOURNAL_TEST));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "find_maximum(int[],int)").get(0);
		logger.debug(function.getAST().getRawSignature());

		FunctionConfig functionConfig = new FunctionConfig();
		functionConfig.setCharacterBound(new ParameterBound(30, 120));
		functionConfig.setIntegerBound(new ParameterBound(10, 200));
		functionConfig.setSizeOfArray(5);
		functionConfig.setMaximumInterationsForEachLoop(3);
		functionConfig.setSolvingStrategy(ISettingv2.SUPPORT_SOLVING_STRATEGIES[0]);
		function.setFunctionConfig(functionConfig);

		// Normalize function
		FunctionNormalizer fnNormalizer = function.normalizedAST();

		String newFunctionInStr = fnNormalizer.getNormalizedSourcecode();
		ICPPASTFunctionDefinition newAST = Utils.getFunctionsinAST(newFunctionInStr.toCharArray()).get(0);
		((FunctionNode) function).setAST(newAST);

		// Choose a random test path to test
		PossibleTestpathGeneration tpGen = new PossibleTestpathGeneration(
				new CFGGenerationforBranchvsStatementCoverage(function).generateCFG(),
				function.getFunctionConfig().getMaximumInterationsForEachLoop());
		tpGen.generateTestpaths();
		logger.debug("num tp = " + tpGen.getPossibleTestpaths().size());
		IFullTestpath randomTestpath = tpGen.getPossibleTestpaths().get(1);
		logger.debug(randomTestpath);

		// Get the passing variables of the given function
		Parameter paramaters = new Parameter();
		for (INode n : ((FunctionNode) function).getArguments())
			paramaters.add(n);
		for (INode n : ((FunctionNode) function).getReducedExternalVariables())
			paramaters.add(n);

		// Get the corresponding path constraints of the test path
		ISymbolicExecution se = new SymbolicExecution(randomTestpath, paramaters, function);
		System.out.println("constraints=\n" + se.getConstraints());

		//
		List<PathConstraint> constraints = new ArrayList<>();
		for (PathConstraint c : (PathConstraints) se.getConstraints())
			constraints.add(c);
		SmtLibGeneration smt = new SmtLibGeneration(function.getArguments(), constraints);
		smt.generate();
		System.out.println(smt.getSmtLibContent());
		BufferedWriter writer = new BufferedWriter(new FileWriter("myConstraint.smt2", true));
		writer.append(smt.getSmtLibContent());
		writer.close();
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
						// If we can not normalize the constraint, we ignore it
						// :)
					}
					break;
				}

			smtLib += ISmtLibGeneration.SOLVE_COMMAND;
		}
	}

	/**
	 * Generate "(declare-fun...)"
	 *
	 * @return
	 * @throws Exception
	 */
	private String getDeclarationFun(List<IVariableNode> variables) throws Exception {
		String output = "";
		if (variables.size() > 0) {
			INode function = variables.get(0).getParent();
			for (IVariableNode var : variables) {
				String type = Utils.getRealType(var.getRawType(), function);

				if (VariableTypes.isBasic(type))
					switch (VariableTypes.getType(type)) {
					/**
					 * float type
					 */
					case VariableTypes.BASIC.NUMBER.FLOAT.FLOAT:
					case VariableTypes.BASIC.NUMBER.FLOAT.DOUBLE:

					case VariableTypes.BASIC.NUMBER.FLOAT.FLOAT + VariableTypes.REFERENCE:
					case VariableTypes.BASIC.NUMBER.FLOAT.DOUBLE + VariableTypes.REFERENCE:
						output += "(declare-fun " + ISymbolicVariable.PREFIX_SYMBOLIC_VALUE + var.getNewType()
								+ " () Real)" + SpecialCharacter.LINE_BREAK;
						break;
					default:
						/**
						 * integer type
						 */
						output += "(declare-fun " + ISymbolicVariable.PREFIX_SYMBOLIC_VALUE + var.getNewType()
								+ " () Int)" + SpecialCharacter.LINE_BREAK;
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

					output += "(declare-fun " + ISymbolicVariable.PREFIX_SYMBOLIC_VALUE + var.getNewType() + " (Int) Real)" + SpecialCharacter.LINE_BREAK;
					break;

					default:
					/**
					 * integer type
					 */
					output += "(declare-fun " + ISymbolicVariable.PREFIX_SYMBOLIC_VALUE + var.getNewType() + " (Int) Int)" + SpecialCharacter.LINE_BREAK;
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

					output += "(declare-fun " + ISymbolicVariable.PREFIX_SYMBOLIC_VALUE + var.getNewType() + " (Int Int) Real)" + SpecialCharacter.LINE_BREAK;
					break;

					default:
					/**
					 * integer type
					 */
					output += "(declare-fun " + ISymbolicVariable.PREFIX_SYMBOLIC_VALUE + var.getNewType() + " (Int Int) Int)" + SpecialCharacter.LINE_BREAK;
					break;
					}
				else if (var.resolveCoreType() instanceof StructureNode) {
					StructureNode cast = (StructureNode) var.resolveCoreType();

					for (IVariableNode attribute : cast.getAttributes()) {
						if (VariableTypes.isBasic(attribute.getRawType())) {

							switch (VariableTypes.getType(attribute.getRawType())) {
							/**
							 * float type
							 */
							case VariableTypes.BASIC.NUMBER.FLOAT.FLOAT:
							case VariableTypes.BASIC.NUMBER.FLOAT.DOUBLE:

							case VariableTypes.BASIC.NUMBER.FLOAT.FLOAT + VariableTypes.REFERENCE:
							case VariableTypes.BASIC.NUMBER.FLOAT.DOUBLE + VariableTypes.REFERENCE:
								output += "(declare-fun " + ISymbolicVariable.PREFIX_SYMBOLIC_VALUE + var.getName()
										+ ISymbolicVariable.SEPARATOR_BETWEEN_STRUCTURE_NAME_AND_ITS_ATTRIBUTES
										+ attribute.getName() + " () Real)" + SpecialCharacter.LINE_BREAK;
								break;
							default:
								/**
								 * integer type
								 */
								output += "(declare-fun " + ISymbolicVariable.PREFIX_SYMBOLIC_VALUE + var.getName()
										+ ISymbolicVariable.SEPARATOR_BETWEEN_STRUCTURE_NAME_AND_ITS_ATTRIBUTES
										+ attribute.getName() + " () Int)" + SpecialCharacter.LINE_BREAK;
								break;
							}

						} else if (VariableTypes.isOneDimensionBasic(attribute.getRawType())
								|| VariableTypes.isOneLevelBasic(attribute.getRawType()))
							/**
							 * float type
							 */
							switch (VariableTypes.getType(attribute.getRawType())) {

							case VariableTypes.BASIC.NUMBER.FLOAT.FLOAT + VariableTypes.ONE_DIMENSION:
							case VariableTypes.BASIC.NUMBER.FLOAT.DOUBLE + VariableTypes.ONE_DIMENSION:

							case VariableTypes.BASIC.NUMBER.FLOAT.FLOAT + VariableTypes.ONE_LEVEL:
							case VariableTypes.BASIC.NUMBER.FLOAT.DOUBLE + VariableTypes.ONE_LEVEL:

							output += "(declare-fun " + ISymbolicVariable.PREFIX_SYMBOLIC_VALUE + var.getName() + ISymbolicVariable.SEPARATOR_BETWEEN_STRUCTURE_NAME_AND_ITS_ATTRIBUTES + attribute.getName() + " (Int) Real)" + SpecialCharacter.LINE_BREAK;
							break;

							default:
							/**
							 * integer type
							 */
							output += "(declare-fun " + ISymbolicVariable.PREFIX_SYMBOLIC_VALUE + var.getName() + ISymbolicVariable.SEPARATOR_BETWEEN_STRUCTURE_NAME_AND_ITS_ATTRIBUTES + attribute.getName() + " (Int) Int)" + SpecialCharacter.LINE_BREAK;
							break;
							}
						else
							output += "; dont support " + attribute.getRawType() + " " + attribute.getName()
									+ SpecialCharacter.LINE_BREAK;

					}
					output += "(declare-fun " + ISymbolicVariable.PREFIX_SYMBOLIC_VALUE + var.getNewType()
							+ " (Int Int) Int)" + SpecialCharacter.LINE_BREAK;
				} else
					output += "; dont support " + var.getNewType() + " " + var.getRawType()
							+ SpecialCharacter.LINE_BREAK;
			}
		}
		return output;

	}

	@Override
	public String getSmtLibContent() {
		return smtLib;
	}
}
