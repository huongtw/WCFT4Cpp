package testdatagen.se.memory;

import java.util.List;

import interfaces.ITreeNode;
import tree.object.IFunctionNode;
import tree.object.INode;

/**
 * This interface represents a node in the symbolic variables tree
 * 
 * @author ducanhnguyen
 *
 */
public interface ISymbolicVariable extends ITreeNode {

	// Each default value of a symbolic variable should be started with a unique
	// string
	// For example, we have "int a", the default value of "a" is "tvwa", or
	// something else.
	String PREFIX_SYMBOLIC_VALUE = "tvw_";// default

	// This is separator between structure name and its attributes.
	// For example, we have "a.age", its default value may be "tvwa_______age"
	String SEPARATOR_BETWEEN_STRUCTURE_NAME_AND_ITS_ATTRIBUTES = "egt_______fes";// default

	String ARRAY_OPENING = "_dsgs_";// default
	String ARRAY_CLOSING = "_fdq_";// default

	// Unspecified scopr
	int UNSPECIFIED_SCOPE = -1;
	int GLOBAL_SCOPE = 0;

	/**
	 * Check whether the variable is basic type (not pointer, not array, not
	 * structure, not enum, etc.), only number or char.
	 *
	 * @return
	 */
	boolean isBasicType();

	/**
	 * Get the name of symbolic variable
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Set the name for the symbolic variable
	 * 
	 * @param name
	 */
	void setName(String name);

	/**
	 * Get type of symbolic variable, e.g., int, int*, float
	 * 
	 * @return
	 */
	String getType();

	/**
	 * Set type for the symbolic variable
	 * 
	 * @param type
	 */
	void setType(String type);

	/**
	 * Get scope level of the symbolic variable. If the symbolic variable scope is
	 * global, its value is equivalent to GLOBAL_SCOPE
	 * 
	 * @return
	 */
	int getScopeLevel();

	/**
	 * Set the scope level for the symbolic variable
	 * 
	 * @param scopeLevel
	 */
	void setScopeLevel(int scopeLevel);

	/**
	 * Get the corresponding node. For example, if the symbolic variable is "Student
	 * x", its node is the definition of "Student". That is: <b>class Student{ ...
	 * }</b>
	 * 
	 * @return
	 */
	INode getNode();

	/**
	 * Set the corresponding node
	 * 
	 * @param node
	 */
	void setNode(INode node);

	/**
	 * Get all physical cells stored by symbolic variable. For example, consider
	 * symbolic variable x <br/>
	 * If x is basic type, it has one physical cell. <br/>
	 * If x is one dimension array, it has a list of physical cells. Each physical
	 * cell represents an array item
	 * 
	 * @return
	 */
	List<PhysicalCell> getAllPhysicalCells();

	IFunctionNode getFunction();

	void setFunction(IFunctionNode function);

}