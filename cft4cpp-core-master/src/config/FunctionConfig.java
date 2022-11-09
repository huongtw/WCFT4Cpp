package config;

/**
 * Reresent configuration of a function
 *
 * @author ducanh
 */
public class FunctionConfig implements IFunctionConfig {

	protected IBound integerBound;

	protected long time;
	// by default
	private String solvingStrategy = Settingv2.SUPPORT_SOLVING_STRATEGIES[0];
	private int sizeOfArray;
	// by default
	private int typeofCoverage = IFunctionConfig.STATEMENT_COVERAGE;
	// by default
	private int maximumInterationsForEachLoop = IFunctionConfig.DEFAULT_MAXIMUM_ITERATIONS_FOR_EACH_LOOP;
	private IBound characterBound;

	public FunctionConfig() {
	}

	@Override
	public IBound getCharacterBound() {
		return characterBound;
	}

	@Override
	public void setCharacterBound(IBound characterBound) {
		this.characterBound = characterBound;
	}

	@Override
	public int getTypeofCoverage() {
		return typeofCoverage;
	}

	@Override
	public void setTypeofCoverage(int typeofCoverage) {
		this.typeofCoverage = typeofCoverage;
	}

	@Override
	public IBound getIntegerBound() {
		return integerBound;
	}

	@Override
	public void setIntegerBound(IBound integerBound) {
		this.integerBound = integerBound;
	}

	@Override
	public int getSizeOfArray() {
		return sizeOfArray;
	}

	@Override
	public void setSizeOfArray(int sizeOfArray) {
		this.sizeOfArray = sizeOfArray;
	}

	@Override
	public int getMaximumInterationsForEachLoop() {
		return maximumInterationsForEachLoop;
	}

	@Override
	public void setMaximumInterationsForEachLoop(int maximumInterationsForEachLoop) {
		this.maximumInterationsForEachLoop = maximumInterationsForEachLoop;
	}

	@Override
	public String getSolvingStrategy() {
		return solvingStrategy;
	}

	@Override
	public void setSolvingStrategy(String solvingStrategy) {
		this.solvingStrategy = solvingStrategy;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		FunctionConfig clone = (FunctionConfig) super.clone();
		clone.setCharacterBound((IBound) characterBound.clone());
		clone.setIntegerBound((IBound) integerBound.clone());
		return clone();
	}
}
