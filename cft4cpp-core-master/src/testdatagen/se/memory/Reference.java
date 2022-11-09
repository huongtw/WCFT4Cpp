package testdatagen.se.memory;

public class Reference {

	protected LogicBlock block = null;

	protected String startIndex = "0";

	public Reference(LogicBlock block) {
		this.block = block;
	}

	public Reference(LogicBlock block, String startIndex) {
		this.block = block;
		this.startIndex = startIndex;
	}

	public LogicBlock getBlock() {
		return block;
	}

	public void setBlock(LogicBlock block) {
		this.block = block;
	}

	public String getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(String index) {
		startIndex = index;
	}

	@Override
	public String toString() {
		if (block == null)
			return "null";
		else
			return block.toString() + "; start index = " + startIndex;
	}

	public static final String FIRST_START_INDEX = "0";
	public static final String UNDEFINED_INDEX = "-1";
}
