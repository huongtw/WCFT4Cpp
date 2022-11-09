package testdatagen;

import interfaces.IGeneration;

/**
 * Generate test data for a function
 *
 * @author ducanhnguyen
 */
public interface ITestdataGeneration extends IGeneration {
	/**
	 * Generate test data satisfying criterion
	 *
	 * @throws Exception
	 */
	void generateTestdata() throws Exception;

	public static class TESTDATA_GENERATION_STRATEGIES {
		/**
		 * @see #{MarsTestdataGeneration2.java}
		 */
		public static final int MARS2 = 1;

		/**
		 * @see #{FastTestdataGeneration.java}
		 */
		public static final int FAST_MARS = 2;
	}
}