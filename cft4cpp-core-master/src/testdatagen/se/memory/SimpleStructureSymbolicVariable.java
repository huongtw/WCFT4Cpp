package testdatagen.se.memory;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import testdatagen.testdatainit.VariableTypes;
import tree.object.ClassNode;
import tree.object.EnumNode;
import tree.object.INode;
import tree.object.IVariableNode;
import tree.object.StructNode;
import tree.object.StructureNode;
import tree.object.UnionNode;
import tree.object.VariableNode;
import utils.Utils;

public abstract class SimpleStructureSymbolicVariable extends SymbolicVariable {
	final static Logger logger = Logger.getLogger(SimpleStructureSymbolicVariable.class);

	// Represent attributes in the structure variable
	private List<ISymbolicVariable> attributes = new ArrayList<>();

	public SimpleStructureSymbolicVariable(String name, String type, int scopeLevel) {
		super(name, type, scopeLevel);
	}

	public List<ISymbolicVariable> getAttributes() {
		return attributes;
	}

	@Override
	public void setNode(INode node) {
		super.setNode(node);
		if (node instanceof StructureNode) {
			StructureNode cast = (StructureNode) node;
			for (IVariableNode attribute : cast.getAttributes()) {
				ISymbolicVariable symbolicAttribute = createSymbolicVariableFromAttribute(attribute);
				this.getAttributes().add(symbolicAttribute);
			}
		}
	}

	public void setAttributes(List<ISymbolicVariable> attributes) {
		this.attributes = attributes;
	}

	protected ISymbolicVariable createSymbolicVariableFromAttribute(IVariableNode attribute) {
		SymbolicVariable v = null;

		// All passing variables have global access
		VariableNode par = (VariableNode) attribute;
		INode nodeType = par.resolveCoreType();
		String name = par.getName();
		String defaultValue = PREFIX_SYMBOLIC_VALUE + this.getName() + SEPARATOR_BETWEEN_STRUCTURE_NAME_AND_ITS_ATTRIBUTES
				+ name;

		String realType = Utils.getRealType(par.getReducedRawType(), par.getParent());
		if (VariableTypes.isAuto(realType))
			logger.debug("Does not support type of the passing variable is auto");
		else
		/*
		 * ----------------NUMBER----------------------
		 */
		if (VariableTypes.isNumBasic(realType))
			v = new NumberSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE, defaultValue);
		else if (VariableTypes.isNumOneDimension(realType)) {
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
					.setSize(this.getFunction().getFunctionConfig().getSizeOfArray() + "");

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

		}
		return v;
	}
}
