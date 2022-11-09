package testdata.object;

import utils.SpecialCharacter;

/**
 * Represent variable as one dimension array.
 *
 * @author ducanhnguyen
 */
public class OneDimensionDataNode extends ArrayDataNode {
	/**
	 * The size of array
	 */
	private int size;

	public boolean canConvertToString() {
		return false;
	}

	public int getSize() {
		return this.size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public String generateInputToSavedInFile() throws Exception {
		String output = "";
		for (AbstractDataNode child : getChildren())
			if (child instanceof NormalDataNode)
				output += ((NormalDataNode) child).getVituralName() + "=" + ((NormalDataNode) child).getValue()
						+ SpecialCharacter.LINE_BREAK;
		output += "sizeof(" + getVituralName() + ")=" + getSize() + SpecialCharacter.LINE_BREAK;
		return output;
	}
}
