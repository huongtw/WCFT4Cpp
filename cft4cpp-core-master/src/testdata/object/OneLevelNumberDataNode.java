package testdata.object;

import testdatagen.testdatainit.VariableTypes;
import utils.SpecialCharacter;

public class OneLevelNumberDataNode extends OneLevelDataNode {
	@Override
	public String generareSourcecodetoReadInputFromFile() throws Exception {
		String typeVar = VariableTypes.deleteStorageClasses(this.getType())
				.replace(IAbstractDataNode.REFERENCE_OPERATOR, "")
				.replace(IAbstractDataNode.ONE_LEVEL_POINTER_OPERATOR, "");
		String loadValueStm = "data.findOneDimensionOrLevelBasicByName<" + typeVar + ">(\"" + getVituralName()
				+ "\", DEFAULT_VALUE_FOR_NUMBER)";

		String fullStm = typeVar + "* " + this.getVituralName() + "=" + loadValueStm
				+ SpecialCharacter.END_OF_STATEMENT;
		return fullStm;
	}

	@Override
	public String generateInputToSavedInFile() throws Exception {
		String output = "";
		for (AbstractDataNode child : getChildren())
			output += child.generateInputToSavedInFile() + SpecialCharacter.LINE_BREAK;
		output += "sizeof(" + getVituralName() + ")=" + getAllocatedSize() + SpecialCharacter.LINE_BREAK;
		return output;
	}
}
