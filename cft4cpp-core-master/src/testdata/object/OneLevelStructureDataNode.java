package testdata.object;

import utils.SpecialCharacter;

public class OneLevelStructureDataNode extends OneLevelDataNode {
	@Override
	public String generateInputToSavedInFile() throws Exception {
		String output = "";
		for (AbstractDataNode child : getChildren())
			output += child.generateInputToSavedInFile() + SpecialCharacter.LINE_BREAK;
		output += "sizeof(" + getVituralName() + ")=" + getAllocatedSize() + SpecialCharacter.LINE_BREAK;
		return output;
	}
}
