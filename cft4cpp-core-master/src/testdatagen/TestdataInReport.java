package testdatagen;

import testdata.object.TestpathString_Marker;

/**
 * Represent a test data stored in test report
 * 
 * @author Duc Anh Nguyen
 *
 */
public class TestdataInReport {
	private String value;
	private TestpathString_Marker testpath;
	private boolean completeTestpath;
	// The code coverage achieved until executing this test data
	private float currentStatementCodeCoverage;
	private float currentBranchCodeCoverage;

	private boolean highlight = false;

	private boolean generateRandomly = false;

	boolean improvedTestdata = false;

	public TestdataInReport(String value, TestpathString_Marker testpath, boolean completeTestpath,
			float currentCodeCoverage, float currentBranchCodeCoverage, boolean highlight, boolean generateRandomly,
			boolean improvedTestdata) {
		super();
		this.value = value;
		this.testpath = testpath;
		this.completeTestpath = completeTestpath;
		this.currentStatementCodeCoverage = currentCodeCoverage;
		this.currentBranchCodeCoverage = currentBranchCodeCoverage;
		this.highlight = highlight;
		this.generateRandomly = generateRandomly;
		this.improvedTestdata = improvedTestdata;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public TestpathString_Marker getTestpath() {
		return testpath;
	}

	public void setTestpath(TestpathString_Marker testpath) {
		this.testpath = testpath;
	}

	public boolean outputCompleteTestpath() {
		return completeTestpath;
	}

	public void setCompleteTestpath(boolean completeTestpath) {
		this.completeTestpath = completeTestpath;
	}

	public float getCurrentBranchCodeCoverage() {
		return currentBranchCodeCoverage;
	}

	public void setCurrentBranchCodeCoverage(float currentBranchCodeCoverage) {
		this.currentBranchCodeCoverage = currentBranchCodeCoverage;
	}

	public float getCurrentStatementCodeCoverage() {
		return currentStatementCodeCoverage;
	}

	public void setCurrentStatementCodeCoverage(float currentStatementCodeCoverage) {
		this.currentStatementCodeCoverage = currentStatementCodeCoverage;
	}

	public boolean isHighlight() {
		return highlight;
	}

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}

	public boolean isGenerateRandomly() {
		return generateRandomly;
	}

	public void setGenerateRandomly(boolean generateRandomly) {
		this.generateRandomly = generateRandomly;
	}

	public boolean isGeneratedBySDART() {
		return improvedTestdata;
	}

	public void setImprovedTestdata(boolean improvedTestdata) {
		this.improvedTestdata = improvedTestdata;
	}
}
