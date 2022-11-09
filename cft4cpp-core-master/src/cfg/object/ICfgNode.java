package cfg.object;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;

/**
 * Represent a node in CFG
 *
 * @author DucAnh
 */
public interface ICfgNode {
	/**
	 * Check whether a node is belonged to the current node or not
	 *
	 * @param child
	 *            the child
	 * @return
	 */
	boolean contains(ICfgNode child);

	/**
	 * Get location of the statement in the file
	 *
	 * @return
	 */
	IASTFileLocation getAstLocation();

	void setAstLocation(IASTFileLocation astLoc);

	/**
	 * Get the content of the statement
	 *
	 * @return
	 */
	String getContent();

	void setContent(String content);

	/**
	 * Get the node corresponding to the false branch of the current node
	 *
	 * @return
	 */
	ICfgNode getFalseNode();

	/**
	 * Get the id of the current node
	 *
	 * @return
	 */
	int getId();

	void setId(int id);

	/**
	 * Get the list target of the current node
	 *
	 * @return
	 */
	List<ICfgNode> getListTarget();

	void setListTarget(List<ICfgNode> listTarget);

	/**
	 * Get the parent of the current node
	 *
	 * @return
	 */
	ICfgNode getParent();

	void setParent(ICfgNode parent);

	/**
	 * Get the node corresponding to the true branch of the current node
	 *
	 * @return
	 */
	ICfgNode getTrueNode();

	/**
	 * @return true if the current node has multiple targets
	 */
	boolean isMultipleTarget();

	/**
	 * @return true if the current node is represent the statement of the given
	 *         source code (e.g., condition, assignment, declaration)
	 */
	boolean isNormalNode();

	/**
	 * Check whether the statement stored in the current node is flag statement
	 * (i.g., "}", "{"), or the begin node of the CFG, or the end node of the CFG
	 *
	 * @return
	 */
	boolean isSpecialCfgNode();

	/**
	 * Check whether the current node is visited or not
	 *
	 * @return true if the node is visited
	 */
	boolean isVisited();

	void setVisit(boolean visited);

	/**
	 * @return true if the node is displayed in CFG
	 */
	boolean shouldDisplayInCFG();

	/**
	 * @return true if the statement is displayed in the same line
	 */
	boolean shouldDisplayInSameLine();

	/**
	 * @return true if the statement is displayed in a block in GUI
	 */
	boolean shouldInBlock();

	void setBranch(ICfgNode next);

	void setFalse(ICfgNode falseNode);

	void setTrue(ICfgNode trueNode);

	Object clone() throws CloneNotSupportedException;
}
