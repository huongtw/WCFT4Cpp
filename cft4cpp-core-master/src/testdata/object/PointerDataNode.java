package testdata.object;

/**
 * Represent variable as pointer (one level, two level, etc.)
 *
 * @author ducanhnguyen
 */
public class PointerDataNode extends AbstractDataNode {
	public static int NULL_VALUE = -1;

	@Override
	public String generareSourcecodetoReadInputFromFile() throws Exception {
		String output = "";
		for (IAbstractDataNode child : this.getChildren())
			output += child.generareSourcecodetoReadInputFromFile();
		return output;

	}
}
