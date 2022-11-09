package testdata.object;

import java.util.ArrayList;
import java.util.List;

import tree.object.VariableNode;
import utils.Utils;

/**
 * Represent a variable node in the <b>variable tree</b>, example: class,
 * struct, array item, etc.
 *
 * @author DucAnh
 */
public abstract class AbstractDataNode implements IAbstractDataNode {

	/**
	 * Represent the fixed name of variable
	 * <p>
	 * 1. Case 1: The node corresponding to array item. Ex: [0], [1],[0][1], v.v.
	 * <p>
	 * 2. Case 2: The node represents pointer/array/class/struct/structure/etc.: Ex:
	 * sv, x, y, v.v.
	 */
	private String name = "";

	/**
	 * The type of variable. Ex: const int&
	 */
	private String type = "";

	/**
	 * The node contains the definition of type's variable. For example, the type of
	 * variable is "Student". This instance returns the node that defines "Student"
	 * (class Student{char* name; ...}).
	 */
	private VariableNode correspondingVar = null;

	/**
	 * The virtual name of node. The name can be changed :D
	 */
	private String virtualName = "";

	/**
	 * The parent node of the current node
	 */
	private AbstractDataNode parent = null;

	/**
	 *
	 */
	private boolean externelVariable = false;

	/**
	 * The children of the current node
	 */
	private List<AbstractDataNode> children = new ArrayList<>();

	private boolean isInStaticSolution = false;

	@Override
	public void addChild(AbstractDataNode newChild) {
		this.children.add(newChild);
	}

	@Override
	public boolean containGetterNode() {
		return this.getCorrespondingVar() != null && this.getCorrespondingVar().getGetterNode() != null;
	}

	@Override
	public boolean containSetterNode() {
		return this.getCorrespondingVar() != null && this.getCorrespondingVar().getSetterNode() != null;
	}

	@Override
	public List<AbstractDataNode> getChildren() {
		return this.children;
	}

	@Override
	public void setChildren(List<AbstractDataNode> children) {
		this.children = children;
	}

	@Override
	public VariableNode getCorrespondingVar() {
		return this.correspondingVar;
	}

	@Override
	public void setCorrespondingVar(VariableNode correspondingVar) {
		this.correspondingVar = correspondingVar;
	}

	@Override
	public String getDotGetterInStr() {
		String dotAccess = "";
		List<AbstractDataNode> chain = this.getNodesChainFromRoot(this);

		for (IAbstractDataNode item : chain)
			if (item instanceof RootDataNode) {
				// nothing to do

			} else if (item.isArrayElement() || item.isPassingVariable())
				dotAccess += item.getName();
			else
				dotAccess += IAbstractDataNode.DOT_ACCESS + item.getName();
		return dotAccess;

	}

	@Override
	public String getDotSetterInStr(String value) {
		return this.getDotGetterInStr() + "=" + value;
	}

	@Override
	public String getGetterInStr() {
		final String METHOD = "()";

		String getter = "";
		List<AbstractDataNode> chain = this.getNodesChainFromRoot(this);

		for (IAbstractDataNode item : chain)
			if (item instanceof RootDataNode) {
				// nothing to do
			} else if (item.isArrayElement() || item.isPassingVariable())
				getter += item.getName();
			else if (item.containGetterNode())
				getter += IAbstractDataNode.DOT_ACCESS
						+ item.getCorrespondingVar().getGetterNode().getSingleSimpleName() + METHOD;
			else if (!item.containGetterNode())
				if (item.getCorrespondingVar().isPrivate())
					getter += IAbstractDataNode.GETTER_METHOD + Utils.toUpperFirstCharacter(item.getName() + METHOD);
				else
					getter += IAbstractDataNode.DOT_ACCESS + item.getName();
		return getter;

	}

	@Override
	public String getInputForDisplay() throws Exception {
		String output = "";
		for (IAbstractDataNode child : this.getChildren())
			output += child.getInputForDisplay();
		return output;
	}

	@Override
	public String getInputForGoogleTest() throws Exception {
		String output = "";
		for (IAbstractDataNode child : this.getChildren())
			output += child.getInputForGoogleTest();
		return output;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public List<AbstractDataNode> getNodesChainFromRoot(AbstractDataNode n) {
		List<AbstractDataNode> chain = new ArrayList<>();
		final int FIRST_LOCATION = 0;

		chain.add(FIRST_LOCATION, n);

		if (n.getParent() != null && !(n.getParent() instanceof RootDataNode))
			chain.addAll(FIRST_LOCATION, this.getNodesChainFromRoot(n.getParent()));

		return chain;
	}

	@Override
	public AbstractDataNode getParent() {
		return this.parent;
	}

	@Override
	public void setParent(AbstractDataNode parent) {
		this.parent = parent;
	}

	@Override
	public String getSetterInStr(String nameVar) {
		String setter = "";
		List<AbstractDataNode> chain = this.getNodesChainFromRoot(this);

		/*
		 * Get the getter of the previous variable.For example, we have "front[0]" and
		 * we need to get the setter of this variable.
		 *
		 * The first step, we get the getter of variable "front".
		 */
		String getterOfPreviousNode = "";

		final int MIN_ELEMENTS_IN_CHAIN = 2;

		if (chain.size() >= MIN_ELEMENTS_IN_CHAIN) {
			/*
			 * If the variable belongs is array item, belongs to class/struct/namespace,
			 * etc., the the size of chain is greater than 2
			 */
			IAbstractDataNode previousNode = chain.get(chain.size() - 2);
			getterOfPreviousNode = previousNode.getGetterInStr();
		} else {
			// nothing to do
		}
		/*
		 *
		 */
		IAbstractDataNode currentNode = chain.get(chain.size() - 1);
		if (currentNode instanceof RootDataNode) {
			// nothing to do

		} else if (currentNode.isArrayElement())
			return this.getParent().getVituralName() + currentNode.getName() + "=" + nameVar;
		else if (currentNode.isPassingVariable())
			setter = getterOfPreviousNode + currentNode.getName() + "=" + nameVar;
		else if (currentNode.containSetterNode())
			setter = getterOfPreviousNode + IAbstractDataNode.DOT_ACCESS
					+ currentNode.getCorrespondingVar().getSetterNode().getSingleSimpleName() + "(" + nameVar + ")";
		else if (!currentNode.containSetterNode())
			if (currentNode.getCorrespondingVar().isPrivate())
				setter = getterOfPreviousNode + IAbstractDataNode.SETTER_METHOD
						+ Utils.toUpperFirstCharacter(currentNode.getName() + "(" + nameVar + ")");
			else if (currentNode instanceof OneDimensionCharacterDataNode) {
				String name = getterOfPreviousNode + IAbstractDataNode.DOT_ACCESS + currentNode.getName();
				setter = "strcpy(" + name + "," + nameVar + ")";

			} else
				setter = getterOfPreviousNode + IAbstractDataNode.DOT_ACCESS + currentNode.getName() + "=" + nameVar;
		return setter;
	}

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getVituralName() {
		return this.virtualName;
	}

	@Override
	public void setVituralName(String vituralName) {
		this.virtualName = vituralName;
	}

	@Override
	public boolean getExternelVariable() {
		return externelVariable;
	}

	public void setExternelVariable(boolean _externelVariable) {
		externelVariable = _externelVariable;
	}

	@Override
	public boolean isArrayElement() {
		if (this.getParent() != null && (this.getParent() instanceof OneLevelDataNode
				|| this.getParent() instanceof OneDimensionDataNode || this.getParent() instanceof TwoDimensionDataNode
				|| this.getParent() instanceof TwoLevelDataNode))
			return true;
		else
			return false;
	}

	@Override
	public boolean isAttribute() {
		if (this.getParent() instanceof StructureDataNode)
			return true;
		else
			return false;
	}

	@Override
	public boolean isInStaticSolution() {
		return this.isInStaticSolution;
	}

	@Override
	public void setInStaticSolution(boolean isInStaticSolution) {
		this.isInStaticSolution = isInStaticSolution;
	}

	@Override
	public boolean isPassingVariable() {
		if (this.getParent() != null && this.getParent() instanceof RootDataNode)
			return true;
		else
			return false;
	}

	@Override
	public String toString() {
		return this.getName() + "\n";
	}

	@Override
	public String generareSourcecodetoReadInputFromFile() throws Exception {
		String output = "";
		for (IAbstractDataNode child : this.getChildren())
			output += child.generareSourcecodetoReadInputFromFile();
		return output;

	}

	@Override
	public String generateInputToSavedInFile() throws Exception {
		String output = "";
		for (IAbstractDataNode child : this.getChildren())
			output += child.generateInputToSavedInFile();
		return output;

	}
}
