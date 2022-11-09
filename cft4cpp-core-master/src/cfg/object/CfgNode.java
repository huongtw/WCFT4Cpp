package cfg.object;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;

/**
 * Represent a node (or vertex) in CFG
 *
 * @author ducanh
 */
public class CfgNode implements ICfgNode, Cloneable {
	private String content;
	/**
	 * Represent the node corresponding to true condition
	 */
	private ICfgNode trueNode;

	/**
	 * Represent the node corresponding to false condition
	 */
	private ICfgNode falseNode;

	/**
	 * Represent the parent node (the previous node of the current node)
	 */
	private ICfgNode parentNode;

	/**
	 * Represent all target node of the current node
	 */
	private List<ICfgNode> targetNodesList = new ArrayList<>();

	/**
	 * State of a node (visited or unvisited)
	 */
	private boolean isVisit;

	/**
	 * Each node has an unique id
	 */
	private int id;

	/**
	 * Location of the abstract syntax tree corresponding to the current node
	 */
	private IASTFileLocation astLocation;

	protected CfgNode() {
	}

	/**
	 * Initialize a node in CFG
	 *
	 * @param content
	 *            the content of statement that new node stores
	 */
	public CfgNode(String content) {
		this.content = content;
	}

	@Override
	public boolean contains(ICfgNode child) {
		while (child != null) {
			if (child == this)
				return true;
			child = child.getParent();
		}
		return false;
	}

	@Override
	public IASTFileLocation getAstLocation() {
		return astLocation;
	}

	@Override
	public void setAstLocation(IASTFileLocation astLoc) {
		astLocation = astLoc;
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public ICfgNode getFalseNode() {
		return falseNode;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public List<ICfgNode> getListTarget() {
		return targetNodesList;
	}

	@Override
	public void setListTarget(List<ICfgNode> listTarget) {
		targetNodesList = listTarget;
	}

	@Override
	public ICfgNode getParent() {
		return parentNode;
	}

	@Override
	public void setParent(ICfgNode parent) {
		parentNode = parent;
	}

	@Override
	public ICfgNode getTrueNode() {
		return trueNode;
	}

	@Override
	public boolean isMultipleTarget() {
		return targetNodesList != null && targetNodesList.size() >= 1;
	}

	@Override
	public boolean isNormalNode() {
		return true;
	}

	@Override
	public boolean isVisited() {
		return isVisit;
	}

	@Override
	public void setBranch(ICfgNode next) {
		setTrue(next);
		setFalse(next);
	}

	@Override
	public void setFalse(ICfgNode falseNode) {
		this.falseNode = falseNode;
	}

	@Override
	public void setTrue(ICfgNode trueNode) {
		this.trueNode = trueNode;
	}

	@Override
	public void setVisit(boolean visited) {
		isVisit = visited;
	}

	@Override
	public boolean shouldDisplayInCFG() {
		return isNormalNode();
	}

	@Override
	public boolean shouldInBlock() {
		return !(this instanceof ConditionCfgNode);
	}

	@Override
	public boolean shouldDisplayInSameLine() {
		return shouldInBlock();
	}

	@Override
	public String toString() {
		return content + " [" + id + "]";
	}

	@Override
	public boolean isSpecialCfgNode() {
		return this instanceof BeginFlagCfgNode || this instanceof EndFlagCfgNode || this instanceof ScopeCfgNode;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ICfgNode)
			return getId() == ((ICfgNode) o).getId();
		else
			return false;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		CfgNode cloneNode = (CfgNode) super.clone();
		cloneNode.setContent(content);
		cloneNode.setTrue(trueNode);
		cloneNode.setFalse(falseNode);
		cloneNode.setParent(parentNode);
		cloneNode.setListTarget(targetNodesList);
		cloneNode.setVisit(isVisit);
		cloneNode.setId(id);
		cloneNode.setAstLocation(astLocation);
		return cloneNode;
	}
}
