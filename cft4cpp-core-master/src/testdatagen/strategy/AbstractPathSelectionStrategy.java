package testdatagen.strategy;

import java.util.ArrayList;
import java.util.List;

import testdatagen.se.IPathConstraints;

public abstract class AbstractPathSelectionStrategy implements IPathSelectionStrategy {
	private List<IPathConstraints> solvedPathConstraints = new ArrayList<>();
	private List<String> generatedTestdata = new ArrayList<>();
	private IPathConstraints originalConstraints;

	public List<IPathConstraints> getSolvedPathConstraints() {
		return solvedPathConstraints;
	}

	public void setSolvedPathConstraints(List<IPathConstraints> solvedPathConstraints) {
		this.solvedPathConstraints = solvedPathConstraints;
	}

	public List<String> getGeneratedTestdata() {
		return generatedTestdata;
	}

	public void setGeneratedTestdata(List<String> generatedTestdata) {
		this.generatedTestdata = generatedTestdata;
	}

	public IPathConstraints getOriginalConstraints() {
		return originalConstraints;
	}

	public void setOriginalConstraints(IPathConstraints originalConstraints) {
		this.originalConstraints = originalConstraints;
	}
}
