package tree.object;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTPointer;

import cfg.CFGGenerationforBranchvsStatementCoverage;
import cfg.CFGGenerationforSubConditionCoverage;
import cfg.ICFG;
import config.AbstractSetting;
import config.FunctionConfig;
import config.IFunctionConfig;
import config.ISettingv2;
import externalvariable.ExternalVariableDetecter;
import externalvariable.ReducedExternalVariableDetecter;
import normalizer.ArgumentTypeNormalizer;
import normalizer.ConditionCovertNormalizer;
import normalizer.FunctionNameNormalizer;
import normalizer.FunctionNormalizer;
import normalizer.MacroNormalizer2;
import normalizer.SetterandGetterFunctionNormalizer;
import normalizer.SwitchCaseNormalizer;
import normalizer.TernaryConvertNormalizer;
import testdatagen.AbstractJUnitTest;
import testdatagen.module.DataTreeGeneration;
import testdatagen.module.IDataTreeGeneration;
import testdatagen.testdatainit.VariableTypes;
import tree.dependency.Dependency;
import tree.dependency.GetterDependency;
import tree.dependency.SetterDependency;
import utils.SpecialCharacter;
import utils.Utils;

public abstract class AbstractFunctionNode extends CustomASTNode<IASTFunctionDefinition> implements IFunctionNode {
	final static Logger logger = Logger.getLogger(AbstractFunctionNode.class);

	private FunctionConfig functionConfig = new FunctionConfig();

	private MacroNormalizer2 fnMacroNormalizer = null;

	private FunctionNormalizer generalNormalizationFunction = null;

	/**
	 * Represent the real parent of function. Ex: if function in class is
	 * defined outside it, then its real parent is this class
	 */
	private INode realParent;

	public AbstractFunctionNode() {
		try {
			Icon ICON_FUNCTION = new ImageIcon(Node.class.getResource("/image/node/FunctionNode.png"));
			setIcon(ICON_FUNCTION);
		} catch (Exception e) {
		}
	}

	@Override
	public List<IVariableNode> getArguments() {
		List<IVariableNode> arguments = new ArrayList<>();

		for (IASTNode child : getAST().getDeclarator().getChildren())
			if (child instanceof IASTParameterDeclaration) {
				IASTParameterDeclaration astArgument = (IASTParameterDeclaration) child;

				VariableNode argumentNode = new InternalVariableNode();
				argumentNode.setAST(astArgument);
				argumentNode.setParent(this);
				argumentNode.setAbsolutePath(getAbsolutePath() + File.separator + argumentNode.getNewType());
				getChildren().add(argumentNode);

				if (!arguments.contains(argumentNode))
					arguments.add(argumentNode);
			}
		return arguments;
	}

	@Override
	public String getDeclaration() {
		String simpleName = getSimpleName();
		String declator = getAST().getDeclarator().getRawSignature();
		int index = declator.indexOf(simpleName);

		return declator.substring(index);
	}

	@Override
	public List<IVariableNode> getReducedExternalVariables() {
		return new ReducedExternalVariableDetecter(this).findExternalVariables();
	}

	@Override
	public List<IVariableNode> getExternalVariables() {
		return new ExternalVariableDetecter(this).findExternalVariables();
	}

	@Override
	public String getFullName() {
		String fullName = getSingleSimpleName() + "(";
		for (INode var : getArguments())
			fullName += ((VariableNode) var).getNewType() + ",";
		fullName += ")";
		fullName = fullName.replace(",)", ")");

		/*
		 * Add prefix of the current name
		 */
		String logicPath = getLogicPathFromTopLevel();
		if (logicPath.length() > 0)
			return logicPath + SpecialCharacter.STRUCTURE_OR_NAMESPACE_ACCESS + fullName;
		else
			return fullName;
	}

	@Override
	public FunctionConfig getFunctionConfig() {
		return functionConfig;
	}

	@Override
	public void setFunctionConfig(FunctionConfig functionConfig) {
		this.functionConfig = functionConfig;
	}

	/**
	 * Get the name of the function and the types of variables pass into it Ex:
	 * "test(int,int)"
	 */
	@Override
	public String getNewType() {
		String output = getAST().getDeclarator().getName().getRawSignature();
		output += "(";
		for (IVariableNode paramater : getArguments())
			output += paramater.getRawType() + ",";
		output += ")";
		output = output.replace(",)", ")").replaceAll("\\s*\\)", "\\)");
		return output;
	}

	@Override
	public IASTFileLocation getNodeLocation() {
		return getAST().getDeclarator().getFileLocation();
	}

	@Override
	public INode getRealParent() {
		return realParent;
	}

	@Override
	public void setRealParent(INode realParent) {
		this.realParent = realParent;
	}

	@Override
	public String getSimpleName() {
		CPPASTFunctionDeclarator declarator = (CPPASTFunctionDeclarator) getAST().getDeclarator();
		IASTNode[] children = declarator.getChildren();
		IASTNode selectedChild = children[0];
		if (selectedChild instanceof CPPASTPointer)
			selectedChild = children[1];
		return selectedChild.getRawSignature();
	}

	@Override
	public File getSourceFile() {
		INode sourceCodeFileNode = Utils.getSourcecodeFile(this);
		if (sourceCodeFileNode != null)
			return new File(sourceCodeFileNode.getAbsolutePath());
		else
			return null;
	}

	@Override
	public INode isGetter() {
		for (Dependency d : getDependencies())
			if (d instanceof GetterDependency)
				if (d.getStartArrow() instanceof VariableNode)
					return d.getStartArrow();
				else if (d.getEndArrow() instanceof VariableNode)
					return d.getEndArrow();
		return null;
	}

	@Override
	public boolean isNoType() {
		return getAST().getDeclSpecifier().getRawSignature().isEmpty();
	}

	@Override
	public INode isSetter() {
		for (Dependency d : getDependencies())
			if (d instanceof SetterDependency)
				if (d.getStartArrow() instanceof VariableNode)
					return d.getStartArrow();
				else if (d.getEndArrow() instanceof VariableNode)
					return d.getEndArrow();
		return null;
	}

	@Override
	public void setAST(IASTFunctionDefinition aST) {
		super.setAST(aST);
	}

	@Override
	public void setParent(INode parent) {
		super.setParent(parent);
		realParent = parent;
	}

	@Override
	public String toString() {
		return getAST().getDeclSpecifier().toString() + " " + getAST().getDeclarator().getRawSignature();
	}

	@Override
	public boolean isTemplate() {
		return AST instanceof ICPPASTTemplateDeclaration;
	}

	@Override
	public String getSingleSimpleName() {
		String singleSimpleName = getSimpleName();
		if (!singleSimpleName.contains(SpecialCharacter.STRUCTURE_OR_NAMESPACE_ACCESS))
			return singleSimpleName;
		else
			return singleSimpleName
					.substring(singleSimpleName.lastIndexOf(SpecialCharacter.STRUCTURE_OR_NAMESPACE_ACCESS) + 2);
	}

	@Override
	public IVariableNode getCorrespondingVariable() {
		IVariableNode output = null;
		INode structureParent = Utils.getStructureParent(this);

		if (structureParent != null && structureParent instanceof StructureNode) {
			StructureNode p = (StructureNode) structureParent;

			for (IVariableNode child : p.getAttributes())
				if (child.getSetterNode() != null && child.getSetterNode().equals(this)
						|| child.getGetterNode() != null && child.getGetterNode().equals(this)) {
					output = child;
					break;
				}
		}
		return output;
	}

	@Override
	public List<IVariableNode> getExpectedNodeTypes() {
		List<IVariableNode> expectedNodeTypes = getReducedExternalVariables();
		String returnType = getReturnType();

		if (isNoType()) {
			// nothing to do
		} else {
			// add a variable representing return value
			VariableNode returnVar = new ReturnVariableNode();
			returnVar.setName(INameRule.RETURN_VARIABLE_NAME_PREFIX);
			returnVar.setRawType(returnType);
			returnVar.setCoreType(returnType.replace(SpecialCharacter.POINTER, ""));
			returnVar.setReducedRawType(returnType);
			returnVar.setParent(this);

			expectedNodeTypes.add(returnVar);
		}
		/*
		 * Add throw variable
		 */
		VariableNode returnVar = new ThrowVariableNode();
		returnVar.setName(INameRule.THROW_VARIABLE_NAME_PREFIX);
		returnVar.setRawType(VariableTypes.THROW);
		returnVar.setCoreType(VariableTypes.THROW);
		returnVar.setReducedRawType(VariableTypes.THROW);
		returnVar.setParent(this);

		expectedNodeTypes.add(returnVar);

		/*
		 * Add all variables as passing variable
		 */
		for (IVariableNode argument : getPassingVariables())
			if (!expectedNodeTypes.contains(argument))
				expectedNodeTypes.add(argument);

		return expectedNodeTypes;
	}

	@Override
	public List<IVariableNode> getPassingVariables() {
		List<IVariableNode> passingVariables = new ArrayList<>();

		for (IVariableNode n : getArguments())
			passingVariables.add(n);

		for (IVariableNode n : getReducedExternalVariables())
			passingVariables.add(n);

		return passingVariables;
	}

	@Override
	public String getReturnType() {
		IASTFunctionDefinition funDef = getAST();

		IASTNode firstChildren = funDef.getChildren()[0];
		String returnType = firstChildren.getRawSignature();

		/*
		 * Name of function may contain * character. Ex: SinhVien* StrDel2(char
		 * s[],int k,int h){...} ==> * StrDel2(char s[],int k,int h)
		 */
		boolean isReturnReference = false;
		CPPASTFunctionDeclarator functionDeclarator = (CPPASTFunctionDeclarator) funDef.getChildren()[1];
		IASTNode firstChild = functionDeclarator.getChildren()[0];
		if (firstChild instanceof CPPASTPointer)
			isReturnReference = true;
		/*
		 *
		 */
		returnType += isReturnReference == true ? "*" : "";
		return returnType;
	}

	@Override
	public String getInstrumentedofNormalizedSource() {
		return null;
	}

	@Override
	public String getLogicPathFromTopLevel() {
		INode namespace = Utils.getTopLevelClassvsStructvsNamesapceNodeParent(this);

		if (namespace == null)
			return "";
		else {
			String path = "";
			INode parent = this;
			while (!parent.equals(namespace)) {
				if (parent instanceof IFunctionNode)
					parent = ((IFunctionNode) parent).getRealParent();
				else
					parent = parent.getParent();

				if (parent instanceof NamespaceNode || parent instanceof StructureNode)
					path = parent.getNewType() + "::" + path;
				else
					break;
			}
			if (path.length() >= 2)
				path = path.substring(0, path.length() - 2);
			return path;
		}
	}

	@Override
	public Boolean isChildrenOfUnnameNamespace() {
		INode namespace = Utils.getTopLevelClassvsStructvsNamesapceNodeParent(this);

		if (namespace == null)
			return false;
		else {
			if (namespace.getNewType().equals(""))
				return true;
		}

		return false;
	}

	@Override
	public FunctionNormalizer normalizedAST() throws Exception {
		if (generalNormalizationFunction == null) {
			FunctionNormalizer fnNormalizer = new FunctionNormalizer();
			fnNormalizer.setFunctionNode(this);

			// only for test
			if (AbstractSetting.getValue(ISettingv2.IN_TEST_MODE).equals("false")
					|| (AbstractSetting.getValue(ISettingv2.IN_TEST_MODE).equals("true")
							&& AbstractJUnitTest.ENABLE_MACRO_NORMALIZATION)) {
				fnNormalizer.addNormalizer(new MacroNormalizer2());
			}
			// end test
			fnNormalizer.addNormalizer(new FunctionNameNormalizer());
			fnNormalizer.addNormalizer(new ArgumentTypeNormalizer());
			fnNormalizer.addNormalizer(new TernaryConvertNormalizer());
			fnNormalizer.addNormalizer(new ConditionCovertNormalizer());
			fnNormalizer.addNormalizer(new SwitchCaseNormalizer());

			fnNormalizer.normalize();
			generalNormalizationFunction = fnNormalizer;
			return generalNormalizationFunction;

		} else
			return generalNormalizationFunction;
	}

	@Override
	public SetterandGetterFunctionNormalizer performSettervsGetterTransformer() {
		SetterandGetterFunctionNormalizer performer = new SetterandGetterFunctionNormalizer();
		performer.setFunctionNode(this);
		performer.normalize();
		return performer;
	}

	@Override
	public IDataTreeGeneration generateDataTree(Map<String, Object> solution) {
		IDataTreeGeneration dataTreeGen = new DataTreeGeneration();
		dataTreeGen.setFunctionNode(this);
		dataTreeGen.setStaticSolution(solution);
		return dataTreeGen;
	}

	@Override
	public ICFG generateCFG() {
		logger.debug("Generate CFG to find static solution");
		ICFG cfg = null;
		try {
			if (getFunctionConfig().getTypeofCoverage() == IFunctionConfig.BRANCH_COVERAGE
					|| getFunctionConfig().getTypeofCoverage() == IFunctionConfig.STATEMENT_COVERAGE) {
				cfg = new CFGGenerationforBranchvsStatementCoverage(this).generateCFG();
				cfg.setFunctionNode(this);

			} else if (getFunctionConfig().getTypeofCoverage() == IFunctionConfig.SUBCONDITION) {
				cfg = new CFGGenerationforSubConditionCoverage(this).generateCFG();
				cfg.setFunctionNode(this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cfg;
	}

	@Override
	public ICFG generateCFGofExecutionSourcecode() {
		logger.debug("Generate CFG of execution source code");
		ICFG cfg = null;
		try {
			if (getFunctionConfig().getTypeofCoverage() == IFunctionConfig.BRANCH_COVERAGE
					|| getFunctionConfig().getTypeofCoverage() == IFunctionConfig.STATEMENT_COVERAGE) {
				cfg = new CFGGenerationforBranchvsStatementCoverage(
						this.getGeneralNormalizationFunction().getFunctionNode()).generateCFG();
				cfg.setFunctionNode(this);

			} else if (getFunctionConfig().getTypeofCoverage() == IFunctionConfig.SUBCONDITION) {
				cfg = new CFGGenerationforSubConditionCoverage(this.getGeneralNormalizationFunction().getFunctionNode())
						.generateCFG();
				cfg.setFunctionNode(this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cfg;
	}

	@Override
	public MacroNormalizer2 getFnMacroNormalizer() {
		return fnMacroNormalizer;
	}

	@Override
	public void setFnMacroNormalizer(MacroNormalizer2 fnMacroNormalizer) {
		this.fnMacroNormalizer = fnMacroNormalizer;
	}

	@Override
	public String generateCompleteFunction() {
		String completeFunction = "";

		// STEP 1: normalize the arguments of the current function
		ArgumentTypeNormalizer varialeTypeNormalizer = new ArgumentTypeNormalizer();
		varialeTypeNormalizer.setFunctionNode(this);
		varialeTypeNormalizer.normalize();
		completeFunction = varialeTypeNormalizer.getNormalizedSourcecode();

		// STEP 2: normalize the name of function
		String prefixPathofFunction = getLogicPathFromTopLevel();
		if (prefixPathofFunction.length() > 0)
			if (prefixPathofFunction.startsWith(SpecialCharacter.STRUCTURE_OR_NAMESPACE_ACCESS))
				completeFunction = completeFunction.replace(getSimpleName(),
						getLogicPathFromTopLevel() + getSingleSimpleName());
			else
				completeFunction = completeFunction.replace(getSimpleName(),
						getLogicPathFromTopLevel() + SpecialCharacter.FILE_SCOPE_ACCESS + getSingleSimpleName());

		return completeFunction;
	}

	@Override
	public INode clone() {
		IFunctionNode clone = new FunctionNode();
		clone.setAbsolutePath(getAbsolutePath());
		clone.setChildren(getChildren());
		clone.setDependencies(getDependencies());
		clone.setId(getId());
		clone.setName(getNewType());
		clone.setParent(getParent());
		clone.setFunctionConfig(getFunctionConfig());
		clone.setRealParent(getRealParent());
		clone.setAST(getAST());

		clone.setFnMacroNormalizer(getFnMacroNormalizer());
		clone.setGeneralNormalizationFunction(getGeneralNormalizationFunction());
		return clone;
	}

	@Override
	public boolean isStaticFunction() {
		if (getRealParent() == null) {
			return getReturnType().contains("static");
		} else {
			for (INode node : getRealParent().getChildren()) {
				if (node instanceof DefinitionFunctionNode) {
					if (((DefinitionFunctionNode) node).getReturnType().contains("static")
							&& ((DefinitionFunctionNode) node).getSimpleName().equals(getSingleSimpleName())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public FunctionNormalizer getGeneralNormalizationFunction() {
		return generalNormalizationFunction;
	}

	@Override
	public void setGeneralNormalizationFunction(FunctionNormalizer generalNormalizationFunction) {
		this.generalNormalizationFunction = generalNormalizationFunction;
	}

	interface INameRule {

		public static final String ACTUAL_OUTPUT_PREFIX = "AO";
		public static final String EXPECTED_OUTPUT_PREFIX = "EO_";
		public static final String RETURN_VARIABLE_NAME_PREFIX = "returnVar";
		public static final String THROW_VARIABLE_NAME_PREFIX = "throw";
	}
}
