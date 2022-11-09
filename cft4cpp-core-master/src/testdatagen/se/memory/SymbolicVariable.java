package testdatagen.se.memory;

import java.util.List;

import testdatagen.testdatainit.VariableTypes;
import tree.object.IFunctionNode;
import tree.object.INode;

/**
 * Represent a symbolic variable
 *
 * @author ducanh
 */
public abstract class SymbolicVariable implements ISymbolicVariable {
	protected IFunctionNode function;
	/**
	 * The AST node corresponding to variable
	 */
	protected INode node;

	/**
	 * Name of variable
	 */
	protected String name = "";

	/**
	 * The type of variable
	 */
	protected String type = "";

	/**
	 * The scope of variable
	 */
	protected int scopeLevel = ISymbolicVariable.UNSPECIFIED_SCOPE;

	public SymbolicVariable(String name, String type, int scopeLevel) {
		this.name = name;
		this.type = type;
		this.scopeLevel = scopeLevel;
	}

	@Override
	public boolean isBasicType() {
		return VariableTypes.isBasic(type);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public int getScopeLevel() {
		return scopeLevel;
	}

	@Override
	public void setScopeLevel(int scopeLevel) {
		this.scopeLevel = scopeLevel;
	}

	@Override
	public INode getNode() {
		return node;
	}

	@Override
	public void setNode(INode node) {
		this.node = node;
	}

	@Override
	public IFunctionNode getFunction() {
		return function;
	}

	@Override
	public void setFunction(IFunctionNode function) {
		this.function = function;
	}

	@Override
	public abstract List<PhysicalCell> getAllPhysicalCells();

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ISymbolicVariable) {
			if (((ISymbolicVariable) obj).getName().equals(this.getName()))
				return true;
			else
				return false;
		} else
			return false;
	}
}
