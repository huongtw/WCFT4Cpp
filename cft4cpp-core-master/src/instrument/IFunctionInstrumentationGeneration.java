package instrument;

import interfaces.IGeneration;
import tree.object.IFunctionNode;

/**
 * Instrument function
 *
 * @author DucAnh
 */
public interface IFunctionInstrumentationGeneration extends IGeneration {
	/**
	 * Generate instrumented source code of a function
	 *
	 * @return
	 */
	String generateInstrumentedFunction();

	/**
	 * Get the function node
	 *
	 * @return
	 */
	IFunctionNode getFunctionNode();

	/**
	 * Set the function node
	 *
	 * @param functionNode
	 */
	void setFunctionNode(IFunctionNode functionNode);

}
