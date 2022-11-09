package testdatagen;

import java.util.ArrayList;

import instrument.FunctionInstrumentationForStatementvsBranch_Marker;
import testdata.object.StatementInTestpath_Mark;
import testdata.object.TestpathString_Marker;
import tree.object.IFunctionNode;

/**
 * Represent a bug
 * 
 * @author Duc Anh Nguyen
 *
 */
public class Bug {
	String testdata;
	TestpathString_Marker testpath;
	IFunctionNode functionNode;

	public Bug(String testdata, TestpathString_Marker testpath, IFunctionNode functionNode) {
		super();
		this.testdata = testdata;
		this.testpath = testpath;
		this.functionNode = functionNode;
	}

	public void setTestdata(String testdata) {
		this.testdata = testdata;
	}

	public void setTestpath(TestpathString_Marker testpath) {
		this.testpath = testpath;
	}

	public void setFunctionNode(IFunctionNode functionNode) {
		this.functionNode = functionNode;
	}

	public String getTestdata() {
		return testdata;
	}

	public TestpathString_Marker getTestpath() {
		return testpath;
	}

	public IFunctionNode getFunctionNode() {
		return functionNode;
	}

	@Override
	public String toString() {
		ArrayList<StatementInTestpath_Mark> properties = testpath.getStandardTestpathByAllProperties();
		int sizeOfTestpath = properties.size();
		String failStatement = properties.get(sizeOfTestpath - 1)
				.getPropertyByName(FunctionInstrumentationForStatementvsBranch_Marker.STATEMENT).getValue();
		return "testdata=" + (testdata.length() > 20 ? testdata.substring(0, 20) + "..." : testdata)
				+ "; failed statement=" + failStatement + "; length testpath=" + properties.size() + "\n";
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof Bug) {
			Bug cast = (Bug) arg0;
			// Two bugs are the same iff two execution test paths are identical regardless
			// of
			// different test data
			if (cast.getTestpath().equals(this.getTestpath()))
				return true;
			else
				return false;
		} else
			return false;
	}
}
