package config;

/**
 * Represent configuration of a function
 *
 * @author DucAnh
 */
public interface IFunctionConfig {
	final int DEFAULT_MAXIMUM_ITERATIONS_FOR_EACH_LOOP = 1;

	@Deprecated
	final static int STATEMENT_COVERAGE = 0;

	final static int BRANCH_COVERAGE = 1;

	@Deprecated
	final static int SUBCONDITION = 2;

	/**
	 * Get character bound
	 *
	 * @return
	 */
	IBound getCharacterBound();

	/**
	 * Set character bound
	 *
	 * @param characterBound
	 */
	void setCharacterBound(IBound characterBound);

	/**
	 * Get type of coverage
	 *
	 * @return
	 */
	int getTypeofCoverage();

	/**
	 * Set type of coverage
	 *
	 * @param coverage
	 */
	void setTypeofCoverage(int coverage);

	/**
	 * Get integer bound
	 *
	 * @return
	 */
	IBound getIntegerBound();

	/**
	 * Set bound of integer
	 *
	 * @param integerBound
	 */
	void setIntegerBound(IBound integerBound);

	/**
	 * Get maximum iterations for each loop
	 *
	 * @return
	 */
	int getMaximumInterationsForEachLoop();

	/**
	 * Set maximum iterations for each loop
	 *
	 * @param maximumInterationsForEachLoop
	 */
	void setMaximumInterationsForEachLoop(int maximumInterationsForEachLoop);

	/**
	 * Get default size of array
	 *
	 * @return
	 */
	int getSizeOfArray();

	/**
	 * Set default size of array
	 *
	 * @param defaultSizeofArray
	 */
	void setSizeOfArray(int defaultSizeofArray);

	String getSolvingStrategy();

	void setSolvingStrategy(String solvingStrategy);
}
