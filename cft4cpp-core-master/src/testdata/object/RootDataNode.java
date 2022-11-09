package testdata.object;

import tree.object.IFunctionNode;

/**
 * Represent the root of the variable tree
 *
 * @author ducanhnguyen
 */
public class RootDataNode extends AbstractDataNode {

	private IFunctionNode functionNode;

	public RootDataNode() {
	}

	public IFunctionNode getFunctionNode() {
		return this.functionNode;
	}

	public void setFunctionNode(IFunctionNode functionNode) {
		this.functionNode = functionNode;
	}

	@Override
	public String getSetterInStr(String nameVar) {
		return "(no setter)";
	}

	@Override
	public String getGetterInStr() {
		return "(no getter)";
	}

	@Override
	public String getVituralName() {
		return ""; // dont modify
	}

	@Override
	public String getName() {
		return "(no name)";
	}

	@Override
	public String getType() {
		return "(no type)";
	}

	@Override
	public String getDotGetterInStr() {
		return "(no getter)";
	}
}
