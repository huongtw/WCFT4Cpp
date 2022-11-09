package testdatagen.module;

import java.util.Map;

import interfaces.IGeneration;
import testdata.object.RootDataNode;
import tree.object.IFunctionNode;

/**
 * A tree represent value of variables
 *
 * @author DucAnh
 */
public interface IDataTreeGeneration extends IGeneration {

	final String NEGATIVE = "!";

	/**
	 * Generate tree of variables
	 *
	 * @throws Exception
	 */
	void generateTree() throws Exception;

	/**
	 * Get corresponding function call
	 *
	 * @return
	 * @throws Exception
	 */
	String getFunctionCall() throws Exception;

	/**
	 * Get the corresponding function
	 *
	 * @return
	 */
	IFunctionNode getFunctionNode();

	/**
	 * Set function node
	 *
	 * @param functionNode
	 */
	void setFunctionNode(IFunctionNode functionNode);

	/**
	 * Get input for display
	 *
	 * @return
	 */
	String getInputforDisplay();

	/**
	 * Get input for google test
	 *
	 * @return
	 */
	String getInputforGoogleTest();

	/**
	 * Get input from file
	 *
	 * @return
	 */
	String getInputformFile();

	String getInputSavedInFile();

	/**
	 * Get static solution
	 *
	 * @return
	 */
	Map<String, Object> getStaticSolution();

	void setStaticSolution(Map<String, Object> staticSolution);

	void setRoot(RootDataNode root);

}