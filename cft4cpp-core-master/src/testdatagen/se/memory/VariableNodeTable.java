package testdatagen.se.memory;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTArraySubscriptExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTArraySubscriptExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFieldReference;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIdExpression;

import config.IFunctionConfig;
import testdatagen.se.ExpressionRewriterUtils;
import testdatagen.testdatainit.VariableTypes;
import tree.object.AvailableTypeNode;
import tree.object.ClassNode;
import tree.object.EnumNode;
import tree.object.IFunctionNode;
import tree.object.INode;
import tree.object.IVariableNode;
import tree.object.StructNode;
import tree.object.StructureNode;
import tree.object.UnionNode;
import utils.ASTUtils;
import utils.Utils;

/**
 * Table of variables is used to store value of variable when we perform
 * symbolic execution for a test path
 *
 * @author ducanh
 */
public class VariableNodeTable extends ArrayList<ISymbolicVariable> implements IVariableNodeTable {
	final static Logger logger = Logger.getLogger(VariableNodeTable.class);
	/**
	 *
	 */
	private static final long serialVersionUID = -1720020482424054084L;
	private IFunctionNode functionNode;
	private String currentNameSpace = "";

	public VariableNodeTable() {
	}

	@Override
	public boolean add(ISymbolicVariable e) {
		if (this.contains(e))
			return false;
		else
			return super.add(e);
	}

	/**
	 * Get the reduce index of array item
	 * <p>
	 * Ex: a[1+2][3] --------> [3][3]
	 *
	 * @param arrayItem
	 * @param table
	 * @return
	 * @throws Exception
	 */
	public static String getReducedIndex(String arrayItem, IVariableNodeTable table) throws Exception {
		String index = "";
		List<String> indexes = Utils.getIndexOfArray(arrayItem);

		for (String indexItem : indexes) {
			indexItem = ExpressionRewriterUtils.rewrite(table, indexItem);
			index += Utils.asIndex(indexItem);
		}
		return index;
	}

	@Override
	public void deleteScopeLevelAt(int level) {
		for (int i = size() - 1; i >= 0; i--)
			if (get(i).getScopeLevel() == level)
				super.remove(i);
	}

	@Override
	public ISymbolicVariable findorCreateVariableByName(String name) {
		ISymbolicVariable symbolicVariable = null;
		for (ISymbolicVariable item : this)
			if (item.getName().equals(name)) {
				symbolicVariable = item;
				break;
			}

		if (symbolicVariable == null) {
			/**
			 * STEP 1. Find a searching name
			 */
			IASTNode astName = ASTUtils.convertToIAST(name);
			String searchingName = "";
			// Ex of name: "sv.other.name[1]"
			if (astName instanceof ICPPASTArraySubscriptExpression) {
				// Get "sv.other.name"
				searchingName = astName.getChildren()[0].getRawSignature();
			} else
			// Ex of name: "sv"
			if (astName instanceof IASTIdExpression) {
				searchingName = astName.getRawSignature();
			} else
			// Ex of name: "sv.other[0].name"
			if (astName instanceof IASTFieldReference) {
				IASTNode tmp = astName;
				while (!(tmp instanceof CPPASTIdExpression))
					tmp = tmp.getChildren()[0];
				searchingName = tmp.getRawSignature();
			} else {
				// nothing to do
			}

			/**
			 * STEP 2. Search the corresponding variable in the table
			 */
			for (ISymbolicVariable item : this)
				if (item.getName().equals(searchingName)) {
					symbolicVariable = item;
					break;
				}
			/**
			 * STEP 3. Create new variable and add it to the table
			 */
			if (symbolicVariable != null) {
				if (symbolicVariable.getNode() instanceof StructureNode) {
					String chain = "";
					if (astName.getRawSignature().startsWith(searchingName + "."))
						chain = astName.getRawSignature()
								.substring(astName.getRawSignature().indexOf(searchingName + ".")
										+ new String(searchingName + ".").length());
					INode node = ((StructureNode) symbolicVariable.getNode()).findAttributeByName(chain);

					if (node instanceof AvailableTypeNode)
						symbolicVariable = createNewVariable(node, ((AvailableTypeNode) node).getType(), name,
								getFunctionNode().getFunctionConfig());
					else if (node instanceof StructureNode) {
						symbolicVariable = createNewVariable(node, ((StructureNode) node).getName(), name,
								getFunctionNode().getFunctionConfig());
					} else if (node instanceof IVariableNode) {
						symbolicVariable = createNewVariable(node, ((IVariableNode) node).getRawType(), name,
								getFunctionNode().getFunctionConfig());
					}
				}
			}
			if (symbolicVariable != null)
				this.add(symbolicVariable);
		}

		return symbolicVariable;
	}

	private ISymbolicVariable createNewVariable(INode nodeType, String realType, String name,
			IFunctionConfig functionConfig) {
		SymbolicVariable v = null;

		String defaultValue = ISymbolicVariable.PREFIX_SYMBOLIC_VALUE
				+ name.replace("[", ISymbolicVariable.ARRAY_OPENING).replace("]", ISymbolicVariable.ARRAY_CLOSING)
						.replace(".", ISymbolicVariable.SEPARATOR_BETWEEN_STRUCTURE_NAME_AND_ITS_ATTRIBUTES)
						.replaceAll("->", ISymbolicVariable.SEPARATOR_BETWEEN_STRUCTURE_NAME_AND_ITS_ATTRIBUTES);

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
			((OneLevelCharacterSymbolicVariable) v).setSize(functionConfig.getSizeOfArray() + "");

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
			logger.debug("The variable " + realType + " is not supported now");

		if (v != null) {
			v.setFunction(getFunctionNode());
			this.add(v);
			v.setNode(nodeType);
		}
		return v;
	}

	@Override
	public PhysicalCell findPhysicalCellByName(String nameVariable) {
		String newNameVariable = Utils.shortenAstNode(ASTUtils.convertToIAST(nameVariable)).getRawSignature();
		ISymbolicVariable var = findorCreateVariableByName(newNameVariable);

		if (var != null)
			if (var instanceof OneDimensionZ3ToIntSymbolicVariable) {
				// nothing to do

			} else if (var instanceof BasicSymbolicVariable)
				return ((BasicSymbolicVariable) var).getCell();

			else if (var instanceof PointerSymbolicVariable)
				return this.findPhysicalCell((PointerSymbolicVariable) var, nameVariable);

			else if (var instanceof ArraySymbolicVariable)
				return this.findPhysicalCell((ArraySymbolicVariable) var, nameVariable);
		return null;
	}

	private PhysicalCell findPhysicalCell(ArraySymbolicVariable variable, String nameCell) {
		PhysicalCell physicalCell = null;

		List<String> indexes = Utils.getIndexOfArray(nameCell);
		if (indexes.size() == 1) {
			String index = Utils.asIndex(indexes.get(0));
			LogicCell logicCell = variable.getBlock().findCellByIndex(index);

			if (logicCell != null)
				physicalCell = logicCell.getPhysicalCell();
		}
		return physicalCell;
	}

	private PhysicalCell findPhysicalCell(PointerSymbolicVariable variable, String nameVariable) {
		PhysicalCell physicalCell = null;

		if (variable.getReference() != null) {
			nameVariable = ExpressionRewriterUtils.convertOneLevelPointerItemToArrayItem(nameVariable);

			List<String> indexes = Utils.getIndexOfArray(nameVariable);
			if (indexes.size() == 1) {
				String index = Utils.asIndex(indexes.get(0));
				LogicCell logicCell = variable.getReference().getBlock().findCellByIndex(index);

				if (logicCell != null)
					physicalCell = logicCell.getPhysicalCell();
			}
		}
		return physicalCell;
	}

	@Override
	public String findTypeByName(String name) {
		final String UNSPECIFIED_NAME = "";
		name = Utils.getNameVariable(name);

		for (ISymbolicVariable item : this)
			if (item instanceof OneDimensionZ3ToIntSymbolicVariable) {
				// nothing to do
			} else if (item.getName().equals(name))
				return item.getType();

		return UNSPECIFIED_NAME;
	}

	@Override
	public String toString() {
		String output = "";
		for (ISymbolicVariable v : this)
			output += v.toString() + "\n";
		return output;
	}

	@Override
	public List<ISymbolicVariable> findVariablesContainingCell(PhysicalCell physicalCell) {
		List<ISymbolicVariable> vars = new ArrayList<>();

		for (ISymbolicVariable var : this)
			if (var instanceof OneDimensionZ3ToIntSymbolicVariable) {
				// nothing to do
			} else
				for (PhysicalCell physicalCellItem : var.getAllPhysicalCells())
					if (physicalCellItem.equals(physicalCell))
						vars.add(var);
		return vars;
	}

	@Override
	public List<String> findNameVariablesContainingCell(PhysicalCell physicalCell) {
		List<String> output = new ArrayList<>();
		List<ISymbolicVariable> vars = findVariablesContainingCell(physicalCell);

		for (ISymbolicVariable var : vars)
			if (var instanceof OneDimensionZ3ToIntSymbolicVariable) {
				// nothing to do
			} else
				output.add(var.getName());
		return output;
	}

	@Override
	public List<ISymbolicVariable> getVariables() {
		return this;
	}

	@Override
	public String getCurrentNameSpace() {
		return currentNameSpace;
	}

	@Override
	public void setCurrentNameSpace(String currentNameSpace) {
		this.currentNameSpace = currentNameSpace;
	}

	@Override
	public VariableNodeTable cast() {
		return this;
	}

	@Override
	public IFunctionNode getFunctionNode() {
		return this.functionNode;
	}

	@Override
	public void setFunctionNode(IFunctionNode functionNode) {
		this.functionNode = functionNode;
	}

	/**
	 * Find all used variables in the expression. If the variables have not been
	 * added to the table of variables, we add it.
	 * 
	 * Ex: Consider this expression: "char* s = sv.other[0].eeee;". Actually, object
	 * sv is a class, and there are one used variable known as "sv.other[0].eeee".
	 * We add this variable to the table of variables if it does not exist before.
	 */
	@Override
	public List<ISymbolicVariable> findUsedVariablesAndCreate(IASTNode expression) {
		List<ISymbolicVariable> newUsedVariable = new ArrayList<>();
		// STEP 1. Get all expressions
		List<IASTNode> expressions = new ArrayList<>();
		ASTVisitor visitor = new ASTVisitor() {
			@Override
			public int visit(IASTExpression expression) {
				if (expression instanceof CPPASTArraySubscriptExpression || expression instanceof CPPASTFieldReference
						|| expression instanceof CPPASTIdExpression) {
					expressions.add(expression);
					return PROCESS_SKIP;
				}
				return PROCESS_CONTINUE;
			};
		};
		visitor.shouldVisitExpressions = true;
		expression.accept(visitor);

		// STEP 2. Create new variables
		for (IASTNode item : expressions) {

			// Check whether exists
			boolean isExist = false;
			for (ISymbolicVariable var : this)
				if (var.getName().equals(item.getRawSignature())) {
					isExist = true;
					break;
				}
			//
			if (!isExist)
				// Ex: "sv.name"
				if (item instanceof CPPASTFieldReference) {
					ISymbolicVariable newSymbolicVar = findorCreateVariableByName(item.getRawSignature());
					if (newSymbolicVar != null)
						newUsedVariable.add(newSymbolicVar);
				} else if (item instanceof CPPASTIdExpression) {
					// nothing to do
				} else
				// Ex: "sv.name[2]"
				if (item instanceof CPPASTArraySubscriptExpression) {
					// Parse "sv.name"
					ISymbolicVariable newSymbolicVar = findorCreateVariableByName(
							item.getChildren()[0].getRawSignature());
					if (newSymbolicVar != null)
						newUsedVariable.add(newSymbolicVar);
				}
		}
		return newUsedVariable;
	}
}
