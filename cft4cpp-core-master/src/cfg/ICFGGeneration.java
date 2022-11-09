package cfg;

import interfaces.IGeneration;
import tree.object.IFunctionNode;

/**
 * This interface is used to generate CFG
 *
 * @author ducanh
 */
public interface ICFGGeneration extends IGeneration {
	final static int IF_FLAG = 0;

	final static int DO_FLAG = 1;

	final static int WHILE_FLAG = 2;

	final static int FOR_FLAG = 3;

	/**
	 * Generate the control flow graph corresponding to the given function
	 *
	 * @return
	 */
	ICFG generateCFG() throws Exception;

	IFunctionNode getFunctionNode();

	void setFunctionNode(IFunctionNode functionNode);

}
