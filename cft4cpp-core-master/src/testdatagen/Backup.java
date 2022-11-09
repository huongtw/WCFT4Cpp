package testdatagen;

import tree.object.INode;
import utils.Utils;

/**
 * Represent backup of a project
 * 
 * @author ducanhnguyen
 *
 */
public class Backup {

	private INode mainParent;
	private String contentOfMainParent;
	private INode fnParent;
	private String contentOfTestFunctionParent;

	public void restore() {
		if (mainParent != null)
			Utils.writeContentToFile(contentOfMainParent, mainParent);
		Utils.writeContentToFile(contentOfTestFunctionParent, fnParent);
	}

	public INode getMainParent() {
		return mainParent;
	}

	public void setMainParent(INode mainParent) {
		this.mainParent = mainParent;
	}

	public String getContentOfMainParent() {
		return contentOfMainParent;
	}

	public void setContentOfMainParent(String contentOfMainParent) {
		this.contentOfMainParent = contentOfMainParent;
	}

	public INode getFnParent() {
		return fnParent;
	}

	public void setFnParent(INode fnParent) {
		this.fnParent = fnParent;
	}

	public String getContentOfTestFunctionParent() {
		return contentOfTestFunctionParent;
	}

	public void setContentOfTestFunctionParent(String contentOfTestFunctionParent) {
		this.contentOfTestFunctionParent = contentOfTestFunctionParent;
	}

}
