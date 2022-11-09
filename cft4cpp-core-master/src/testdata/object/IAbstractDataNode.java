package testdata.object;

import java.util.List;

import interfaces.ITreeNode;
import tree.object.IVariableNode;
import tree.object.VariableNode;

public interface IAbstractDataNode extends ITreeNode {
	/**
	 * The access operator to access element in class/struct
	 */
	String DOT_ACCESS = ".";
	String GETTER_METHOD = IAbstractDataNode.DOT_ACCESS + "get";
	String NULL = "NULL";
	String ONE_LEVEL_POINTER_OPERATOR = "*";
	String REFERENCE_OPERATOR = "&";
	String SETTER_METHOD = IAbstractDataNode.DOT_ACCESS + "set";

	void addChild(AbstractDataNode newChild);

	/**
	 * Check whether the corresponding variable has getter or not
	 *
	 * @return
	 */
	boolean containGetterNode();

	/**
	 * Check whether the corresponding variable has setter or not
	 *
	 * @return
	 */
	boolean containSetterNode();

	List<AbstractDataNode> getChildren();

	void setChildren(List<AbstractDataNode> children);

	IVariableNode getCorrespondingVar();

	void setCorrespondingVar(VariableNode correspondingVar);

	/**
	 * Get the getter of element in string. <br/>
	 * Ex1: "front[0].N" <br/>
	 * Ex2: "front[0]"
	 *
	 * @return
	 */
	String getDotGetterInStr();

	/**
	 * Get the setter of element in string. <br/>
	 * Ex1: "front[0].N=NULL" <br/>
	 * Ex2: "x=2"
	 *
	 * @param value
	 *            the value of variable.
	 * @return
	 */
	String getDotSetterInStr(String value);

	/**
	 * Ex: "front[0].N"
	 *
	 * @return
	 */
	String getGetterInStr();

	/**
	 * Get the string used to display in GUI
	 *
	 * @return
	 * @throws Exception
	 */
	String getInputForDisplay() throws Exception;

	/**
	 * Get the string used to put in google test file
	 *
	 * @return
	 * @throws Exception
	 */
	String getInputForGoogleTest() throws Exception;

	String generareSourcecodetoReadInputFromFile() throws Exception;

	String generateInputToSavedInFile() throws Exception;

	String getName();

	void setName(String name);

	/**
	 * Get all node from the root node to the current node
	 *
	 * @param n
	 *            the current node
	 * @return a list of nodes from the root node
	 */
	List<AbstractDataNode> getNodesChainFromRoot(AbstractDataNode n);

	IAbstractDataNode getParent();

	void setParent(AbstractDataNode parent);

	/**
	 * Ex: front.setName(age);
	 *
	 * @param nameVar
	 *            Ex: age
	 * @return the string contains the setter of the current node
	 */
	String getSetterInStr(String nameVar);

	String getType();

	void setType(String type);

	String getVituralName();

	void setVituralName(String vituralName);

	/**
	 * Check whether the node is array item or not
	 *
	 * @return true if the current node is array item
	 */
	boolean isArrayElement();

	/**
	 * Check whether the node is attribute or not (element of
	 * class/struct/enum/union/etc.)
	 *
	 * @return
	 */
	boolean isAttribute();

	/**
	 * Check whether the node has value in static solution or not. For example, the
	 * static solution is "a=1; b=2;". In the variable tree, we have a node named
	 * "a". Therefore, this node return true.
	 *
	 * @return
	 */
	boolean isInStaticSolution();

	void setInStaticSolution(boolean isInStaticSolution);

	/**
	 * Check whether the node is passing variable or not
	 *
	 * @return
	 */
	boolean isPassingVariable();

	@Override
	String toString();

	boolean getExternelVariable();
}