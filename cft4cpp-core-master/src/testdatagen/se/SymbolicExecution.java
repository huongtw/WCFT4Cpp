package testdatagen.se;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUnaryExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTBinaryExpression;
import org.eclipse.cdt.internal.core.dom.rewrite.astwriter.ASTWriter;

import com.ibm.icu.util.Calendar;

import cfg.CFG;
import cfg.CFGGenerationforBranchvsStatementCoverage;
import cfg.ICFG;
import cfg.object.BeginFlagCfgNode;
import cfg.object.EndFlagCfgNode;
import cfg.object.FlagCfgNode;
import cfg.object.ICfgNode;
import cfg.object.NormalCfgNode;
import cfg.object.ScopeCfgNode;
import cfg.testpath.IFullTestpath;
import cfg.testpath.INormalizedTestpath;
import cfg.testpath.ITestpathInCFG;
import cfg.testpath.PossibleTestpathGeneration;
import config.FunctionConfig;
import config.IFunctionConfig;
import config.ISettingv2;
import config.ParameterBound;
import config.Paths;
import normalizer.FunctionNormalizer;
import normalizer.UnaryNormalizer;
import parser.projectparser.ProjectParser;
import testdatagen.AbstractTestdataGeneration;
import testdatagen.se.memory.ArraySymbolicVariable;
import testdatagen.se.memory.BasicSymbolicVariable;
import testdatagen.se.memory.CharacterSymbolicVariable;
import testdatagen.se.memory.ClassSymbolicVariable;
import testdatagen.se.memory.EnumSymbolicVariable;
import testdatagen.se.memory.ISymbolicVariable;
import testdatagen.se.memory.IVariableNodeTable;
import testdatagen.se.memory.LogicCell;
import testdatagen.se.memory.NumberSymbolicVariable;
import testdatagen.se.memory.OneDimensionCharacterSymbolicVariable;
import testdatagen.se.memory.OneDimensionClassSymbolicVariable;
import testdatagen.se.memory.OneDimensionEnumSymbolicVariable;
import testdatagen.se.memory.OneDimensionNumberSymbolicVariable;
import testdatagen.se.memory.OneDimensionStructSymbolicVariable;
import testdatagen.se.memory.OneDimensionSymbolicVariable;
import testdatagen.se.memory.OneDimensionUnionSymbolicVariable;
import testdatagen.se.memory.OneDimensionZ3ToIntSymbolicVariable;
import testdatagen.se.memory.OneLevelCharacterSymbolicVariable;
import testdatagen.se.memory.OneLevelClassSymbolicVariable;
import testdatagen.se.memory.OneLevelEnumSymbolicVariable;
import testdatagen.se.memory.OneLevelNumberSymbolicVariable;
import testdatagen.se.memory.OneLevelStructSymbolicVariable;
import testdatagen.se.memory.OneLevelSymbolicVariable;
import testdatagen.se.memory.OneLevelUnionSymbolicVariable;
import testdatagen.se.memory.PhysicalCell;
import testdatagen.se.memory.PointerSymbolicVariable;
import testdatagen.se.memory.StructSymbolicVariable;
import testdatagen.se.memory.SymbolicVariable;
import testdatagen.se.memory.TwoDimensionCharacterSymbolicVariable;
import testdatagen.se.memory.TwoDimensionClassSymbolicVariable;
import testdatagen.se.memory.TwoDimensionEnumSymbolicVariable;
import testdatagen.se.memory.TwoDimensionNumberSymbolicVariable;
import testdatagen.se.memory.TwoDimensionStructSymbolicVariable;
import testdatagen.se.memory.TwoDimensionSymbolicVariable;
import testdatagen.se.memory.TwoDimensionUnionSymbolicVariable;
import testdatagen.se.memory.TwoLevelCharacterSymbolicVariable;
import testdatagen.se.memory.TwoLevelClassSymbolicVariable;
import testdatagen.se.memory.TwoLevelEnumSymbolicVariable;
import testdatagen.se.memory.TwoLevelNumberSymbolicVariable;
import testdatagen.se.memory.TwoLevelStructSymbolicVariable;
import testdatagen.se.memory.TwoLevelSymbolicVariable;
import testdatagen.se.memory.TwoLevelUnionSymbolicVariable;
import testdatagen.se.memory.UnionSymbolicVariable;
import testdatagen.se.memory.VariableNodeTable;
import testdatagen.se.normalization.PointerAccessNormalizer;
import testdatagen.se.normalstatementparser.BinaryAssignmentParser;
import testdatagen.se.normalstatementparser.ConditionParser;
import testdatagen.se.normalstatementparser.DeclarationParser;
import testdatagen.se.normalstatementparser.ThrowParser;
import testdatagen.se.normalstatementparser.UnaryBinaryParser;
import testdatagen.se.normalstatementparser.UsingNamespaceParser;
import testdatagen.testdatainit.VariableTypes;
import tree.object.ClassNode;
import tree.object.EnumNode;
import tree.object.FunctionNode;
import tree.object.IFunctionNode;
import tree.object.INode;
import tree.object.StructNode;
import tree.object.UnionNode;
import tree.object.VariableNode;
import utils.ASTUtils;
import utils.IRegex;
import utils.Utils;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

public class SymbolicExecution implements ISymbolicExecution {
	final static Logger logger = Logger.getLogger(SymbolicExecution.class);

	protected IFunctionNode functionNode;

	/**
	 * Represent a test path generated from control flow graph
	 */
	protected ITestpathInCFG testpath = null;

	/**
	 * The variable passing to the function
	 */
	protected Parameter paramaters = null;

	/**
	 * Table of variables
	 */
	protected VariableNodeTable tableMapping = new VariableNodeTable();

	/**
	 * Store path constraints generated by performing symbolic execution the
	 * given test path
	 */
	protected PathConstraints constraints = new PathConstraints();

	/**
	 * The return value of a test path, specified by "return ..."
	 */
	protected String returnValue = "";

	/**
	 * Contains all used variables in the test path
	 * 
	 */
	private Set<String> usedVariables = new HashSet<>();

	public SymbolicExecution(ITestpathInCFG testpath, Parameter paramaters, IFunctionNode functionNode)
			throws Exception {
		if (functionNode != null && paramaters != null && paramaters.size() > 0 && testpath != null) {
			Date startTime = Calendar.getInstance().getTime();

			this.testpath = testpath;
			this.paramaters = paramaters;
			this.functionNode = functionNode;

			tableMapping.setFunctionNode(functionNode);
			createMappingTable(paramaters);
			// this.addZ3Casting();
			symbolicTestpath(testpath, this.tableMapping, functionNode);

			Date end = Calendar.getInstance().getTime();
			AbstractTestdataGeneration.symbolicExecutionTime += end.getTime() - startTime.getTime();
		}
	}

	public static void main(String[] args) throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.SYMBOLIC_EXECUTION_TEST));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "basicTest20(bool,bool)").get(0);
		logger.debug(function.getAST().getRawSignature());

		FunctionConfig functionConfig = new FunctionConfig();
		functionConfig.setCharacterBound(new ParameterBound(30, 120));
		functionConfig.setIntegerBound(new ParameterBound(10, 200));
		functionConfig.setSizeOfArray(5);
		functionConfig.setMaximumInterationsForEachLoop(1);
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
		ICFG cfg = function.generateCFG();
		logger.debug("num tp = " + tpGen.getPossibleTestpaths().size());
		
		IFullTestpath randomTestpath = (IFullTestpath) cfg.getUnvisitedStatements().get(0);
		
		
		// Get the passing variables of the given function
		Parameter paramaters = new Parameter();
		for (INode n : ((FunctionNode) function).getArguments())
			paramaters.add(n);
		for (INode n : ((FunctionNode) function).getReducedExternalVariables())
			paramaters.add(n);

		// Get the corresponding path constraints of the test path
		ISymbolicExecution se = new SymbolicExecution(randomTestpath, paramaters, function);
		System.out.println("constraints=\n" + se.getConstraints());
		// System.out.println("table var=\n" + se.getTableMapping());
	}

	private void symbolicTestpath(ITestpathInCFG testpath, VariableNodeTable table, IFunctionNode function)
			throws Exception {
		AbstractTestdataGeneration.numOfSymbolicExecutions++;
		int scopeLevel = 1;
		int count = 0;

		// STEP 1.
		// If the test path is not normalized before, we normalize it.
		// Visit @see{INormalizedTestpath.class} for more information.
		// Note that, after normalizing, the content of each decision in this
		// test path
		// reveals it boolean value.
		INormalizedTestpath normalizedTestpath = null;
		if (testpath instanceof INormalizedTestpath)
			normalizedTestpath = (INormalizedTestpath) testpath;
		else {
			UnaryNormalizer tpNormalizer = new UnaryNormalizer();
			tpNormalizer.setOriginalTestpath(testpath);
			tpNormalizer.normalize();
			normalizedTestpath = tpNormalizer.getNormalizedTestpath();
		}

		this.addBoolBasicConstraints(constraints, table);

		this.addCharacterBasicConstraints(constraints, table);

		// STEP 2.
		// We perform the symbolic execution on all of the statements in the
		// normalized
		// test path until catch an supported statement.
		int i = 0;
		for (ICfgNode cfgNode : normalizedTestpath.getAllCfgNodes())
			// If the test path is always false, we stop the symbolic execution.
			if (!this.constraints.isAlwaysFalse())
				if (cfgNode instanceof BeginFlagCfgNode || cfgNode instanceof EndFlagCfgNode) {
					// nothing to do
				} else
					try {
						i++;
						// System.out.println("[" + i + "/" +
						// normalizedTestpath.getAllCfgNodes().size() + "] Parse
						// "
						// + cfgNode.getContent());
						AbstractTestdataGeneration.numOfSymbolicStatements++;
						boolean isContinue = true;

						if (cfgNode instanceof NormalCfgNode) {
							IASTNode ast = ((NormalCfgNode) cfgNode).getAst();

							switch (this.getTypeOfStatement(ast)) {
							case NAMESPACE:
								new UsingNamespaceParser().parse(ast, table);
								break;

							case UNARY_ASSIGNMENT: {
								List<ISymbolicVariable> newUsedVariable = tableMapping.findUsedVariablesAndCreate(ast);
								constraints.createNewConstraint(newUsedVariable);

								new UnaryBinaryParser().parse(ast, table);

								this.usedVariables.addAll(this.getUsedVariable(this.tableMapping));
								break;
							}
							case BINARY_ASSIGNMENT: {
								List<ISymbolicVariable> newUsedVariable = tableMapping.findUsedVariablesAndCreate(ast);
								constraints.createNewConstraint(newUsedVariable);

								new BinaryAssignmentParser().parse(ast, table);

								this.usedVariables.addAll(this.getUsedVariable(this.tableMapping));
								break;
							}

							case DECLARATION: {
								List<ISymbolicVariable> newUsedVariable = tableMapping.findUsedVariablesAndCreate(ast);
								constraints.createNewConstraint(newUsedVariable);

								isContinue = this.parseDeclaration(cfgNode, ast, scopeLevel, table, function);

								this.usedVariables.addAll(this.getUsedVariable(this.tableMapping));
								break;
							}

							case CONDITION: {
								List<ISymbolicVariable> newUsedVariable = tableMapping.findUsedVariablesAndCreate(ast);
								constraints.createNewConstraint(newUsedVariable);

								isContinue = this.parseCondition(cfgNode, ast, count, table);

								for (PathConstraint constraint : this.constraints)
									this.usedVariables
											.addAll(this.getUsedArrayvsPointerItem(constraint.getConstraint()));
								break;
							}

							case RETURN: {
								IASTReturnStatement returnAst = (IASTReturnStatement) ast;
								IASTExpression returnValue = returnAst.getReturnValue();
								if (returnValue != null) {
									String value = ExpressionRewriterUtils.rewrite(table,
											returnValue.getRawSignature());
									this.usedVariables.addAll(this.getUsedArrayvsPointerItem(value));
								}
								break;
							}

							case THROW: {
								ThrowParser throwParser = new ThrowParser();
								throwParser.parse(ast, table);
								this.returnValue = throwParser.getExceptionName();
								break;
							}

							case IGNORE:
								break;

							case UNSPECIFIED_STATEMENT:
								break;
							}
						} else if (cfgNode instanceof ScopeCfgNode)
							scopeLevel = this.parseScope(cfgNode, scopeLevel, table);

						else if (cfgNode instanceof FlagCfgNode) {
							// nothing to do
						} else
							break;

						Utils.containFunction = false;
						count++;

						if (!isContinue)
							break;
					} catch (Exception e) {
						e.printStackTrace();
						break;
					}

		this.constraints.setVariablesTableNode(this.tableMapping);
	}

	private Set<String> getUsedArrayvsPointerItem(String normalizedExpression) {
		// normalize pointer
		PointerAccessNormalizer pointerNorm = new PointerAccessNormalizer();
		pointerNorm.setOriginalSourcecode(normalizedExpression);
		pointerNorm.normalize();
		normalizedExpression = pointerNorm.getNormalizedSourcecode();

		Set<String> usedVariables = new HashSet<>();
		List<ICPPASTArraySubscriptExpression> arrayItems = Utils
				.getArraySubscriptExpression(ASTUtils.convertToIAST(normalizedExpression));

		for (ICPPASTArraySubscriptExpression arrayItem : arrayItems) {
			// shorten index of array item before add its into the list of used
			// variables
			String arrayItemInStr = ExpressionRewriterUtils.shortenExpressionInBracket(arrayItem.getRawSignature());
			usedVariables.add(arrayItemInStr.replaceFirst(ISymbolicVariable.PREFIX_SYMBOLIC_VALUE, ""));
		}

		return usedVariables;
	}

	private Set<String> getUsedVariable(VariableNodeTable tableMapping) {
		Set<String> usedVariables = new HashSet<>();

		for (ISymbolicVariable var : tableMapping)
			if (var instanceof BasicSymbolicVariable)
				usedVariables.add(var.getName());
			else {
				List<LogicCell> logicCells = new ArrayList<>();

				if (var instanceof ArraySymbolicVariable)
					logicCells = ((ArraySymbolicVariable) var).getAllLogicCells();

				else if (var instanceof PointerSymbolicVariable)
					logicCells = ((PointerSymbolicVariable) var).getAllLogicCells();

				for (LogicCell logicCell : logicCells) {
					List<ICPPASTArraySubscriptExpression> arrayItems = Utils.getArraySubscriptExpression(
							ASTUtils.convertToIAST(logicCell.getPhysicalCell().getValue()));

					String fullNameVar = var.getName() + logicCell.getFullIndex();
					arrayItems.addAll(Utils.getArraySubscriptExpression(ASTUtils.convertToIAST(fullNameVar)));

					for (ICPPASTArraySubscriptExpression arrayItem : arrayItems) {
						// shorten index of array item before add its into the
						// list of used variables
						String arrayItemInStr = ExpressionRewriterUtils
								.shortenExpressionInBracket(arrayItem.getRawSignature());
						usedVariables.add(arrayItemInStr.replaceFirst(ISymbolicVariable.PREFIX_SYMBOLIC_VALUE, ""));
					}
				}
			}
		return usedVariables;
	}

	private void addAdditionalConstraints(Set<String> usedVariables, VariableNodeTable table, IFunctionNode fn,
			PathConstraints constraints) throws Exception {
		String solvingStrategy = fn.getFunctionConfig().getSolvingStrategy();
		/*
		 * Add some constraints
		 */
		if (solvingStrategy.equals(ISettingv2.SUPPORT_SOLVING_STRATEGIES[1])) {
			for (String usedArrayvsPointerItem : usedVariables) {
				ISymbolicVariable var = table.findorCreateVariableByName(usedArrayvsPointerItem);
				if (usedArrayvsPointerItem != null && var != null
						&& var.getScopeLevel() == ISymbolicVariable.GLOBAL_SCOPE)
					this.addNewConstraint(var, fn, usedArrayvsPointerItem, constraints);
			}
			for (ISymbolicVariable var : table)
				if (var instanceof BasicSymbolicVariable && var.getScopeLevel() == ISymbolicVariable.GLOBAL_SCOPE)
					this.addNewConstraint(var, fn, var.getName(), constraints);

		} else if (solvingStrategy.equals(ISettingv2.SUPPORT_SOLVING_STRATEGIES[0])) {
			/*
			 * "Z3 strategy"
			 */
			// Get all variables used in constraints
			Set<String> usedVariablesInConstraints = new HashSet<>();
			for (PathConstraint constraint : constraints)
				usedVariablesInConstraints.addAll(this.getUsedArrayvsPointerItem(constraint.getConstraint()));

			// Get all variables in table var
			for (ISymbolicVariable var : table)
				if (var instanceof BasicSymbolicVariable && var.getScopeLevel() == ISymbolicVariable.GLOBAL_SCOPE)
					usedVariablesInConstraints.add(var.getName());

			// Add constraints for variables that dont exist in constraints
			for (String usedVariable : usedVariables)
				if (!usedVariablesInConstraints.contains(usedVariable)) {
					ISymbolicVariable var = table.findorCreateVariableByName(usedVariable);

					if (usedVariable != null && var != null && var.getScopeLevel() == ISymbolicVariable.GLOBAL_SCOPE)
						if (var instanceof OneLevelCharacterSymbolicVariable
								|| var instanceof TwoLevelCharacterSymbolicVariable
								|| var instanceof CharacterSymbolicVariable) {
							// nothing to do
						} else
							// is number
							this.addNewConstraint(var, fn, usedVariable, constraints);
				}

			// Add constraints for character variables
			for (String usedVariable : usedVariables) {
				ISymbolicVariable var = table.findorCreateVariableByName(usedVariable);

				if (usedVariable != null && var != null && var.getScopeLevel() == ISymbolicVariable.GLOBAL_SCOPE
						&& VariableTypes.isCh(var.getType()))
					// ASCII of character [0..126]
					this.addNewConstraintForCharacter(var, 32, 126, usedVariable, constraints);
			}

			this.addBoolBasicConstraints(constraints, table);

			this.addCharacterBasicConstraints(constraints, table);
		}
	}

	private void addCharacterBasicConstraints(PathConstraints constraints, VariableNodeTable tableMapping) {
		for (ISymbolicVariable var : tableMapping)
			if (var.getScopeLevel() == ISymbolicVariable.GLOBAL_SCOPE && VariableTypes.isChBasic(var.getType())) {
				String nameVarInConstraint = ISymbolicVariable.PREFIX_SYMBOLIC_VALUE + var.getName();
				String newConstraint = "(" + nameVarInConstraint + ">=32&&" + nameVarInConstraint + "<=126)" + "||"
						+ nameVarInConstraint + "==0";
				constraints.add(new PathConstraint(newConstraint, null, PathConstraint.ADDITIONAL_TYPE));
			}
	}

	private void addBoolBasicConstraints(PathConstraints constraints, VariableNodeTable tableMapping) {
		for (ISymbolicVariable var : tableMapping)
			if (var.getScopeLevel() == ISymbolicVariable.GLOBAL_SCOPE && VariableTypes.isBool(var.getType())
					&& VariableTypes.isBasic(var.getType())) {
				String nameVarInConstraint = ISymbolicVariable.PREFIX_SYMBOLIC_VALUE + var.getName();
				String newConstraint = nameVarInConstraint + "==" + 0 + "||" + nameVarInConstraint + "==1";
				constraints.add(new PathConstraint(newConstraint, null, PathConstraint.ADDITIONAL_TYPE));
			}
	}

	private void addNewConstraint(ISymbolicVariable var, IFunctionNode fn, String usedVariable,
			PathConstraints constraints) {
		int lower = this.getBound(var, fn)[0];
		int upper = this.getBound(var, fn)[1];

		// if the value of it is null, we dont consider its item
		boolean isIgnored = false;
		for (PathConstraint nullConstraint : constraints.getNullorNotNullConstraints())
			if (nullConstraint.getConstraint().equals(var.getName() + "==NULL"))
				isIgnored = true;
		if (!isIgnored) {
			String newConstraint = usedVariable + ">=(" + lower + ")&&" + usedVariable + "<=(" + upper + ")";

			for (ISymbolicVariable item : tableMapping)
				newConstraint = newConstraint.replaceAll(Utils.toRegex(item.getName()),
						ISymbolicVariable.PREFIX_SYMBOLIC_VALUE + item.getName());

			constraints.add(new PathConstraint(newConstraint, null, PathConstraint.ADDITIONAL_TYPE));
		}
	}

	private void addNewConstraintForCharacter(ISymbolicVariable var, int lower, int upper, String usedVariable,
			PathConstraints constraints) {
		// if the value of it is null, we dont consider its item
		boolean isIgnored = false;
		for (PathConstraint nullConstraint : constraints.getNullorNotNullConstraints())
			if (nullConstraint.getConstraint().equals(var.getName() + "==NULL"))
				isIgnored = true;
		if (!isIgnored) {
			String nameVarInConstraint = ISymbolicVariable.PREFIX_SYMBOLIC_VALUE + usedVariable;
			String newConstraint = "(" + nameVarInConstraint + ">=(" + lower + ")&&" + nameVarInConstraint + "<=("
					+ upper + "))||" + nameVarInConstraint + "==0";
			constraints.add(new PathConstraint(newConstraint, null, PathConstraint.ADDITIONAL_TYPE));
		}
	}

	/**
	 * In order to trace the changes of variables, we create a table of
	 * variables that stores its values.
	 * <p>
	 * For example, we have a table: (x, int, 2). The value of x is equivalent
	 * to 2. After an increasing statement (i.g., x++), the table becomes (x,
	 * int, 3)
	 *
	 * @param paramaters
	 * @throws Exception
	 */
	private void createMappingTable(Parameter paramaters) throws Exception {
		for (INode paramater : paramaters)
			if (paramater instanceof VariableNode) {
				SymbolicVariable v = null;

				// All passing variables have global access
				VariableNode par = (VariableNode) paramater;
				INode nodeType = par.resolveCoreType();
				String name = par.getName();
				String defaultValue = ISymbolicVariable.PREFIX_SYMBOLIC_VALUE + name;

				String realType = Utils.getRealType(par.getReducedRawType(), par.getParent());
				if (VariableTypes.isAuto(realType))
					logger.debug("Does not support type of the passing variable is auto");
				else
				/*
				 * ----------------NUMBER----------------------
				 */
				if (VariableTypes.isNumBasic(realType)) {
					v = new NumberSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE, defaultValue);
				} else if (VariableTypes.isNumOneDimension(realType)) {
					v = new OneDimensionNumberSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
					((OneDimensionSymbolicVariable) v).getBlock().setName(defaultValue);

				} else if (VariableTypes.isNumTwoDimension(realType)) {
					v = new TwoDimensionNumberSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
					((TwoDimensionSymbolicVariable) v).getBlock().setName(defaultValue);

				} else if (VariableTypes.isNumOneLevel(realType)) {
					v = new OneLevelNumberSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
					((OneLevelSymbolicVariable) v).getReference().getBlock().setName(defaultValue);

				} else if (VariableTypes.isNumTwoLevel(realType)) {
					v = new TwoLevelNumberSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
					((TwoLevelNumberSymbolicVariable) v).getReference().getBlock().setName(defaultValue);

				} else
				/*
				 * ----------------CHARACTER----------------------
				 */
				if (VariableTypes.isChBasic(realType))
					v = new CharacterSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE, defaultValue);
				else if (VariableTypes.isChOneDimension(realType)) {
					v = new OneDimensionCharacterSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
					((OneDimensionSymbolicVariable) v).getBlock().setName(defaultValue);

				} else if (VariableTypes.isChTwoDimension(realType)) {
					v = new TwoDimensionCharacterSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
					((TwoDimensionSymbolicVariable) v).getBlock().setName(defaultValue);

				} else if (VariableTypes.isChOneLevel(realType)) {
					v = new OneLevelCharacterSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
					((OneLevelCharacterSymbolicVariable) v).getReference().getBlock().setName(defaultValue);
					((OneLevelCharacterSymbolicVariable) v)
							.setSize(this.functionNode.getFunctionConfig().getSizeOfArray() + "");

				} else if (VariableTypes.isChTwoLevel(realType)) {
					v = new TwoLevelCharacterSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
					((TwoLevelCharacterSymbolicVariable) v).getReference().getBlock().setName(defaultValue);

				} else
				/*
				 * ----------------STRUCTURE----------------------
				 */
				if (VariableTypes.isStructureSimple(realType)) {

					if (nodeType instanceof UnionNode)
						v = new UnionSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
					else if (nodeType instanceof StructNode)
						v = new StructSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
					else if (nodeType instanceof ClassNode)
						v = new ClassSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
					else if (nodeType instanceof EnumNode)
						v = new EnumSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);

				} else if (VariableTypes.isStructureOneDimension(realType)) {
					if (nodeType instanceof UnionNode)
						v = new OneDimensionUnionSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
					else if (nodeType instanceof StructNode)
						v = new OneDimensionStructSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
					else if (nodeType instanceof ClassNode)
						v = new OneDimensionClassSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
					else if (nodeType instanceof EnumNode)
						v = new OneDimensionEnumSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);

					if (v != null)
						((OneDimensionSymbolicVariable) v).getBlock().setName(defaultValue);

				} else if (VariableTypes.isStructureTwoDimension(realType)) {
					if (nodeType instanceof UnionNode)
						v = new TwoDimensionUnionSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
					else if (nodeType instanceof StructNode)
						v = new TwoDimensionStructSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
					else if (nodeType instanceof ClassNode)
						v = new TwoDimensionClassSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
					else if (nodeType instanceof EnumNode)
						v = new TwoDimensionEnumSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);

					if (v != null)
						((OneDimensionSymbolicVariable) v).getBlock().setName(defaultValue);

				} else if (VariableTypes.isStructureOneLevel(realType)) {
					if (nodeType instanceof UnionNode)
						v = new OneLevelUnionSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
					else if (nodeType instanceof StructNode)
						v = new OneLevelStructSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
					else if (nodeType instanceof ClassNode)
						v = new OneLevelClassSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
					else if (nodeType instanceof EnumNode)
						v = new OneLevelEnumSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);

					if (v != null)
						((PointerSymbolicVariable) v).getReference().getBlock().setName(defaultValue);

				} else if (VariableTypes.isStructureTwoLevel(realType)) {

					if (nodeType instanceof UnionNode)
						v = new TwoLevelUnionSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
					else if (nodeType instanceof StructNode)
						v = new TwoLevelStructSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
					else if (nodeType instanceof ClassNode)
						v = new TwoLevelClassSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
					else if (nodeType instanceof EnumNode)
						v = new TwoLevelEnumSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
					if (v != null)
						((PointerSymbolicVariable) v).getReference().getBlock().setName(defaultValue);

				} else
					logger.debug("The variable " + paramater.toString() + " is not supported now");

				if (v != null) {
					v.setFunction(functionNode);
					this.tableMapping.add(v);
					v.setNode(nodeType);
				}
			}
	}

	/**
	 * Note: Using this function increase the solving time. By removing it, the
	 * time will be improved significantly.
	 */
	private void addZ3Casting() {
		this.tableMapping.add(new OneDimensionZ3ToIntSymbolicVariable(ISymbolicExecution.TO_INT_Z3, "int[]",
				+ISymbolicVariable.GLOBAL_SCOPE));
	}

	/**
	 * Get lower bound and upper bound of variable
	 *
	 * @param var
	 * @param fn
	 * @return
	 */
	private int[] getBound(ISymbolicVariable var, IFunctionNode fn) {
		IFunctionConfig fnConfig = fn.getFunctionConfig();
		int lower = 0, upper = 10;
		if (var instanceof NumberSymbolicVariable || var instanceof OneDimensionNumberSymbolicVariable
				|| var instanceof TwoDimensionNumberSymbolicVariable || var instanceof OneLevelNumberSymbolicVariable
				|| var instanceof TwoLevelSymbolicVariable) {
			lower = fnConfig.getIntegerBound().getLower();
			upper = fnConfig.getIntegerBound().getUpper();

			if (VariableTypes.isBool(var.getType())) {
				lower = 0;
				upper = 1;
			}

		} else if (var instanceof CharacterSymbolicVariable || var instanceof OneDimensionCharacterSymbolicVariable
				|| var instanceof TwoDimensionCharacterSymbolicVariable
				|| var instanceof OneLevelCharacterSymbolicVariable
				|| var instanceof TwoLevelCharacterSymbolicVariable) {
			lower = fnConfig.getCharacterBound().getLower();
			upper = fnConfig.getCharacterBound().getUpper();
		}
		return new int[] { lower, upper };
	}

	/**
	 * Get the type of the statement.
	 * <p>
	 * In this function, we define some rules to detect the type of the
	 * statements exactly.
	 *
	 * @param stm
	 * @return
	 */
	private int getTypeOfStatement(IASTNode stm) {
		stm = Utils.shortenAstNode(stm);

		if (stm.getRawSignature().startsWith(ISymbolicExecution.NAMESPACE_SIGNAL))
			return ISymbolicExecution.NAMESPACE;

		else if (stm instanceof ICPPASTBinaryExpression && this.isCondition((ICPPASTBinaryExpression) stm)
				|| stm.getRawSignature().startsWith("!") /* Ex: !(a>0) */
				|| stm.getRawSignature().matches(IRegex.NAME_REGEX)/* Ex: a */)
			return ISymbolicExecution.CONDITION;

		else if (stm instanceof ICPPASTUnaryExpression)
			return ISymbolicExecution.UNARY_ASSIGNMENT;

		else if (stm instanceof IASTExpressionStatement) {
			/*
			 * In case the statement is expression (e.g., binary expression,
			 * unary expression)
			 */
			IASTExpression _stm = ((IASTExpressionStatement) stm).getExpression();

			if (_stm instanceof IASTBinaryExpression)
				if (_stm.getRawSignature().contains("="))
					/*
					 * If the statement is binary expression and it contains
					 * "=", it means that the statement is binary assignment.
					 *
					 * For example: x=2*x
					 */
					return ISymbolicExecution.BINARY_ASSIGNMENT;
				else
					/*
					 * If the statement is binary expression and it does not
					 * contains "=", in this case it is hard to detect the type
					 * of the statement. We ignore this circumstance!
					 *
					 */
					return ISymbolicExecution.IGNORE;

			else if (_stm instanceof IASTFunctionCallExpression)
				/*
				 * Ignore the statement corresponding to function call.
				 */
				return ISymbolicExecution.UNSPECIFIED_STATEMENT;

			else if (_stm instanceof IASTUnaryExpression) {
				IASTUnaryExpression unaryStm = (IASTUnaryExpression) _stm;

				switch (unaryStm.getOperator()) {
				case IASTUnaryExpression.op_prefixIncr:
				case IASTUnaryExpression.op_postFixIncr:
				case IASTUnaryExpression.op_prefixDecr:
				case IASTUnaryExpression.op_postFixDecr:
				case IASTUnaryExpression.op_star:
				case IASTUnaryExpression.op_amper:
					/*
					 * Ex1: a++; Ex2: a--; Ex3: --a; Ex4:++a; Ex5: *a; Ex6: &a
					 */
					return ISymbolicExecution.UNARY_ASSIGNMENT;

				case IASTUnaryExpression.op_throw:
					/*
					 * Ex: throw 1
					 */
					return ISymbolicExecution.THROW;

				default:
					return ISymbolicExecution.UNSPECIFIED_STATEMENT;
				}
			} else
				/*
				 * Does not handle the remaining circumstances.
				 */
				return ISymbolicExecution.UNSPECIFIED_STATEMENT;

		} else if (stm instanceof IASTDeclarationStatement)
			return ISymbolicExecution.DECLARATION;

		else if (stm instanceof IASTReturnStatement)
			return ISymbolicExecution.RETURN;

		else if (stm instanceof ICPPASTBinaryExpression && this.isBinaryAssignment((IASTBinaryExpression) stm))
			return ISymbolicExecution.BINARY_ASSIGNMENT;

		else if (Utils.containFunctionCall(stm))
			return ISymbolicExecution.UNSPECIFIED_STATEMENT;

		return ISymbolicExecution.UNSPECIFIED_STATEMENT;
	}

	/**
	 * Check whether the statement is condition or not
	 *
	 * @param ast
	 * @return
	 */
	private boolean isCondition(ICPPASTBinaryExpression ast) {
		switch (ast.getOperator()) {
		case IASTBinaryExpression.op_greaterEqual:
		case IASTBinaryExpression.op_greaterThan:
		case IASTBinaryExpression.op_lessEqual:
		case IASTBinaryExpression.op_lessThan:
		case IASTBinaryExpression.op_equals:
		case IASTBinaryExpression.op_notequals:

		case IASTBinaryExpression.op_logicalAnd:
		case IASTBinaryExpression.op_logicalOr:
			return true;
		default:
			return false;
		}

	}

	/**
	 * Check whether the statement is assignment or not
	 *
	 * @param binaryExpression
	 * @return
	 */
	private boolean isBinaryAssignment(IASTBinaryExpression binaryExpression) {
		switch (binaryExpression.getOperator()) {
		case IASTBinaryExpression.op_assign:
		case IASTBinaryExpression.op_multiplyAssign:
		case IASTBinaryExpression.op_divideAssign:
		case IASTBinaryExpression.op_moduloAssign:
		case IASTBinaryExpression.op_plusAssign:
		case IASTBinaryExpression.op_minusAssign:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Ex: (a>0 && a==1)
	 *
	 * @param ast
	 * @return
	 */
	private boolean isMultipleCodition(IASTNode ast) {
		if (ast instanceof ICPPASTUnaryExpression)
			return true;
		else {
			int operator = ((CPPASTBinaryExpression) ast).getOperator();
			switch (operator) {
			case IASTBinaryExpression.op_logicalAnd:
			case IASTBinaryExpression.op_logicalOr:
			case IASTBinaryExpression.op_binaryAnd:
				return true;
			default:
				return false;
			}
		}
	}

	/**
	 * Ex: a==0
	 */
	private boolean isSingleCodition(IASTNode ast) {
		String content = ast.getRawSignature();
		String[] SINGLE_CONDITIONS = new String[] { "&&", "||" };
		for (String singleCondition : SINGLE_CONDITIONS)
			if (content.contains(singleCondition))
				return false;

		return true;
	}

	/**
	 * Parse the scope statement
	 *
	 * @param stm
	 * @param scopeLevel
	 *            the new scope level
	 * @param table
	 */
	private int parseScope(ICfgNode stm, int scopeLevel, IVariableNodeTable table) {
		if (stm.getContent().equals("{"))
			scopeLevel++;
		else {
			table.deleteScopeLevelAt(scopeLevel);
			scopeLevel--;
		}
		return scopeLevel;
	}

	/**
	 * Parse the declaration
	 *
	 * @param stm
	 * @param ast
	 * @param scopeLevel
	 * @param table
	 * @return true if the declaration of variable is supported
	 * @throws Exception
	 */
	private boolean parseDeclaration(ICfgNode stm, IASTNode ast, int scopeLevel, VariableNodeTable table,
			INode function) throws Exception {
		if (ast instanceof IASTDeclarationStatement && function instanceof IFunctionNode) {
			IASTDeclarationStatement declarationStm = (IASTDeclarationStatement) ast;

			IASTDeclaration declaration = declarationStm.getDeclaration();
			if (declaration instanceof IASTSimpleDeclaration) {
				IASTSimpleDeclaration stm3 = (IASTSimpleDeclaration) declaration;

				DeclarationParser declarationParser = new DeclarationParser();
				declarationParser.setScopeLevel(scopeLevel);
				declarationParser.setFunction((IFunctionNode) function);
				declarationParser.parse(stm3, table);

				return true;
			}
		}
		return false;
	}

	/**
	 * Parse the condition
	 *
	 * @param stm
	 * @param ast
	 * @param count
	 * @param table
	 * @return true if the condition is supported. Otherwise, it returns false
	 * @throws Exception
	 */
	private boolean parseCondition(ICfgNode stm, IASTNode ast, int count, VariableNodeTable table) throws Exception {
		ast = Utils.shortenAstNode(ast);

		if (this.isSingleCodition(ast))
			return this.parseSingleCondition(stm, ast, count, table);

		else if (this.isMultipleCodition(ast))
			this.parseMultipleCondition(stm, table);
		else
			throw new Exception("Dont support " + stm.toString());

		return true;

	}

	private void parseMultipleCondition(ICfgNode stm, VariableNodeTable table) throws Exception {
		String str = stm.getContent();

		ConditionParser conditionParser = new ConditionParser();
		conditionParser.parse(ASTUtils.convertToIAST(str), table);
		this.constraints
				.add(new PathConstraint(conditionParser.getNewConstraint(), stm, PathConstraint.CREATE_FROM_DECISION));
	}

	private boolean parseSingleCondition(ICfgNode stm, IASTNode ast, int count, VariableNodeTable table)
			throws Exception {
		if (ast instanceof ICPPASTLiteralExpression || ast instanceof IASTIdExpression) {
			/*
			 * Ex: (a)
			 */
			String var = ast.getRawSignature();
			PhysicalCell r = table.findPhysicalCellByName(var);

			if (r != null) {

				List<ISymbolicVariable> vars = table.findVariablesContainingCell(r);
				if (vars.size() == 1) {
					ISymbolicVariable firstVar = vars.get(0);

					if (firstVar instanceof BasicSymbolicVariable)
						this.constraints
								.add(new PathConstraint(((BasicSymbolicVariable) firstVar).getSymbolicValue() + "==1",
										stm, PathConstraint.CREATE_FROM_DECISION));
				} else
					throw new Exception("Dont support!");
			}
		} else if (ast instanceof ICPPASTBinaryExpression) {
			ICPPASTBinaryExpression binaryAst = (ICPPASTBinaryExpression) ast;
			/*
			 * Ex1:a+x>0
			 * 
			 * Ex2: a == NULL
			 */
			String left = binaryAst.getOperand1().getRawSignature();
			String right = binaryAst.getOperand2().getRawSignature();
			/*
			 * Assign pointer to NULL/not-NULL
			 */
			if (right.equals("NULL")) {

				ISymbolicVariable r = table.findorCreateVariableByName(left);

				if (r != null && r instanceof OneLevelSymbolicVariable) {
					String sizeInStr = ((OneLevelSymbolicVariable) r).getSize();

					switch (binaryAst.getOperator()) {
					// Ex: "x==NULL"
					case IASTBinaryExpression.op_equals:
						// Ex: size of pointer x is specified
						if (Utils.toInt(sizeInStr) != Utils.UNDEFINED_TO_INT) {
							/*
							 * If the pointer points to a temporary block of
							 * memory, it means that the pointer is not
							 * allocated before.
							 */
							if (Utils.toInt(sizeInStr) == 0)
								// Add an true constraints :))
								this.constraints.add(new PathConstraint(sizeInStr + "==0", stm,
										PathConstraint.CREATE_FROM_DECISION));
							else
								// no solution
								return false;
						} else {
							this.constraints.add(
									new PathConstraint(sizeInStr + "==0", stm, PathConstraint.CREATE_FROM_DECISION));
						}
						break;
					// Ex: "x!=NULL"
					case IASTBinaryExpression.op_notequals:

						// Ex: size of pointer x is specified
						if (Utils.toInt(sizeInStr) != Utils.UNDEFINED_TO_INT) {
							/*
							 * If the pointer points to a temporary block of
							 * memory, it means that the pointer is not
							 * allocated before.
							 */
							if (Utils.toInt(sizeInStr) > 0) {
								/*
								 * In this case, the path constraint is always
								 * true. We dont add to the path constraints
								 */
							} else
								this.constraints.add(new PathConstraint(binaryAst.getRawSignature(), stm,
										PathConstraint.CREATE_FROM_DECISION));
						}
						// Ex: size of pointer x = "4*n" (n is variable)
						else {
							this.constraints.add(
									new PathConstraint(sizeInStr + "!=0", stm, PathConstraint.CREATE_FROM_DECISION));
						}

						break;
					}

				} else
					throw new Exception("Dont support " + ast.getRawSignature());
			} else {
				String exp = "";
				ISymbolicVariable leftVar = this.tableMapping.findorCreateVariableByName(left);
				if (leftVar instanceof PointerSymbolicVariable) {
					if (leftVar instanceof OneLevelSymbolicVariable) {
						/*
						 * Ex: while (*ptr){...}
						 */
						List<String> indexes = Utils.getIndexOfArray(left);
						String newIndex = "";
						if (indexes.size() == 1)
							newIndex = indexes.get(0);
						else
							newIndex = "0";
						String newVar = leftVar.getName() + "[" + newIndex + "]";
						exp = ast.getRawSignature().replace(left, newVar);
					} else
						exp = ast.getRawSignature();
				} else
					exp = ast.getRawSignature();

				ConditionParser conditionParser = new ConditionParser();
				conditionParser.parse(ASTUtils.convertToIAST(exp), table);
				/*
				 * If the condition reveals it true/false
				 */
				if (conditionParser.getNewConstraint().equals(CustomJeval.FALSE))
					this.constraints.add(new PathConstraint(ISymbolicExecution.NO_SOLUTION_CONSTRAINT, stm,
							PathConstraint.CREATE_FROM_DECISION));

				else if (conditionParser.getNewConstraint().equals(CustomJeval.TRUE))
					this.constraints.add(new PathConstraint(ISymbolicExecution.ALWAYS_TRUE_CONSTRAINT, stm,
							PathConstraint.CREATE_FROM_DECISION));

				else
					this.constraints.add(new PathConstraint(conditionParser.getNewConstraint(), stm,
							PathConstraint.CREATE_FROM_DECISION));

			}
		} else {
			String exp = new ASTWriter().write(((NormalCfgNode) stm).getAst());

			ConditionParser conditionParser = new ConditionParser();
			conditionParser.parse(ASTUtils.convertToIAST(exp), table);
			this.constraints.add(
					new PathConstraint(conditionParser.getNewConstraint(), stm, PathConstraint.CREATE_FROM_DECISION));
		}
		return true;
	}

	@Override
	public IPathConstraints getConstraints() {
		return this.constraints;
	}

	@Override
	public void setConstraints(PathConstraints constraints) {
		this.constraints = constraints;
	}

	@Override
	public Parameter getParamaters() {
		return this.paramaters;
	}

	@Override
	public void setParamaters(Parameter paramaters) {
		this.paramaters = paramaters;
	}

	@Override
	public String getReturnValue() {
		return this.returnValue;
	}

	@Override
	public void setReturnValue(String returnValue) {
		this.returnValue = returnValue;
	}

	@Override
	public IVariableNodeTable getTableMapping() {
		return this.tableMapping;
	}

	@Override
	public void setTableMapping(VariableNodeTable tableMapping) {
		this.tableMapping = tableMapping;
	}

	@Override
	public ITestpathInCFG getTestpath() {
		return this.testpath;
	}

	@Override
	public void setTestpath(ITestpathInCFG testpath) {
		this.testpath = testpath;
	}
}
