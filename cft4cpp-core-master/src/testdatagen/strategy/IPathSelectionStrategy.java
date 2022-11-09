package testdatagen.strategy;

import java.util.List;
import java.util.Set;

import testdatagen.se.IPathConstraints;

/**
 * Interface for path selection strategy
 * 
 * @author Duc Anh Nguyen
 *
 */
public interface IPathSelectionStrategy {

	/**
	 * Negate path constraints
	 * 
	 * @param originalConstraints
	 *            the path constraints needed to be negated
	 * @return
	 */
	PathSelectionOutput negateTheOriginalPathConstraints();

	List<IPathConstraints> getSolvedPathConstraints();

	void setSolvedPathConstraints(List<IPathConstraints> solvedPathConstraints);

	List<String> getGeneratedTestdata();

	void setGeneratedTestdata(List<String> generatedTestdata);

	IPathConstraints getOriginalConstraints();

	void setOriginalConstraints(IPathConstraints originalConstraints);
}
