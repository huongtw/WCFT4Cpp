package testdatagen.se.memory;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTNode;

import tree.object.IFunctionNode;

public interface IVariableNodeTable {
	IFunctionNode getFunctionNode();

	void setFunctionNode(IFunctionNode functionNode);

	/**
	 * Delete all variables at a specified scope in table of variable
	 *
	 * @param level
	 */
	void deleteScopeLevelAt(int level);

	/**
	 * Get the instance of variable by name
	 *
	 * @param name
	 *            VD: a, x, y, c[1], etc.
	 * @return
	 */
	ISymbolicVariable findorCreateVariableByName(String name);

	/**
	 * Find cell that stores variable by its name
	 *
	 * @param nameVariable
	 *            Ex1: x ; Ex2: x[2]
	 * @return
	 */
	PhysicalCell findPhysicalCellByName(String nameVariable);

	/**
	 * Find type of variable by name
	 *
	 * @param name
	 * @return
	 */
	String findTypeByName(String name);

	/**
	 * Get all variables containing a physical cell
	 *
	 * @param physicalCell
	 * @return
	 */
	List<ISymbolicVariable> findVariablesContainingCell(PhysicalCell physicalCell);

	/**
	 * Get all name of variables containing a physical cell
	 *
	 * @param physicalCell
	 * @return
	 */
	List<String> findNameVariablesContainingCell(PhysicalCell physicalCell);

	List<ISymbolicVariable> getVariables();

	String getCurrentNameSpace();

	void setCurrentNameSpace(String currentNameSpace);

	VariableNodeTable cast();

	List<ISymbolicVariable> findUsedVariablesAndCreate(IASTNode expression);
}