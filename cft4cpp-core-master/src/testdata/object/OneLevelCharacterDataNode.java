package testdata.object;

import java.util.Map;
import java.util.TreeMap;

import testdatagen.testdatainit.VariableTypes;
import utils.SpecialCharacter;
import utils.Utils;

public class OneLevelCharacterDataNode extends OneLevelDataNode {

	private String generateDetailedInputforDisplay() throws Exception {
		String input = "";
		for (IAbstractDataNode child : this.getChildren())
			input += child.getInputForDisplay();
		return input;

	}

	private String generateSimplifyInputforDisplay() {
		String input = "";

		Map<Integer, String> values = new TreeMap<>();
		for (IAbstractDataNode child : this.getChildren()) {
			NormalDataNode nChild = (NormalDataNode) child;

			String index = Utils.getIndexOfArray(nChild.getName()).get(0);
			values.put(Utils.toInt(index), nChild.getValue());
		}

		for (Integer key : values.keySet()) {
			int ASCII = Utils.toInt(values.get(key));
			switch (ASCII) {
			case 34:/* nhay kep */
				input += "\\\"";
				break;

			case 92:/* gach cheo */
				input += "\\\\";
				break;
			case 39:
				/* nhay don */
				input += "\\'";

				break;
			default:
				input += (char) ASCII;
			}
		}
		input = this.getDotSetterInStr("\"" + input + "\"") + SpecialCharacter.LINE_BREAK;
		return input;

	}

	private String getDetailedInputforGTest() throws Exception {
		String input = "";
		String initialization = "";
		String declaration = "";

		if (this.canConvertToString() && this.isVisible()) {
			String type = VariableTypes
					.deleteStorageClasses(this.getType().replace(IAbstractDataNode.REFERENCE_OPERATOR, ""));
			if (getExternelVariable() == true) {
				type = "";
			}
			declaration = String.format("%s %s", type, this.getVituralName());

			Map<Integer, String> values = new TreeMap<>();
			for (IAbstractDataNode child : this.getChildren()) {
				NormalDataNode nChild = (NormalDataNode) child;

				String index = Utils.getIndexOfArray(nChild.getName()).get(0);
				values.put(Utils.toInt(index), nChild.getValue());
			}

			for (Integer key : values.keySet()) {
				int ASCII = Utils.toInt(values.get(key));
				switch (ASCII) {
				case 34:/* nhay kep */
					initialization += "\\\"";
					break;

				case 92:/* gach cheo */
					initialization += "\\\\";
					break;

				case 39:
					/* nhay don */
					initialization += "\\'";
					break;

				default:
					initialization += (char) ASCII;
				}
			}
			input = declaration + "=" + Utils.putInString(initialization) + SpecialCharacter.END_OF_STATEMENT;

		} else {
			String type = VariableTypes
					.deleteStorageClasses(this.getType().replace(IAbstractDataNode.REFERENCE_OPERATOR, ""));
			if (getExternelVariable() == true) {
				type = "";
			}
			String coreType = type.replace(IAbstractDataNode.ONE_LEVEL_POINTER_OPERATOR, "");

			if (this.getAllocatedSize() <= 0)
				declaration = String.format("%s %s = " + IAbstractDataNode.NULL + SpecialCharacter.END_OF_STATEMENT,
						type, this.getVituralName());
			else
				declaration = String.format("%s %s = new %s[%s]" + SpecialCharacter.END_OF_STATEMENT, type,
						this.getVituralName(), coreType, this.getAllocatedSize() + 1);

			for (IAbstractDataNode child : this.getChildren())
				initialization += child.getInputForGoogleTest();
			input = declaration + SpecialCharacter.END_OF_STATEMENT + initialization;
		}

		if (this.isAttribute())
			input += this.getSetterInStr(this.getVituralName()) + SpecialCharacter.END_OF_STATEMENT;
		return input;
	}

	@Override
	public String getInputForDisplay() throws Exception {
		String input = "";
		if (this.canConvertToString() && this.isVisible())
			input = this.generateSimplifyInputforDisplay();
		else
			input = this.generateDetailedInputforDisplay();
		return input;
	}

	@Override
	public String getInputForGoogleTest() throws Exception {
		String input = "";

		if (this.isAttribute() && this.canConvertToString() && this.isVisible())
			input = this.getSimplifyInputforGTest();
		else
			input = this.getDetailedInputforGTest();
		return input;
	}

	private String getSimplifyInputforGTest() {

		String input = "";
		String initialization = "";
		String type = VariableTypes
				.deleteStorageClasses(this.getType().replace(IAbstractDataNode.REFERENCE_OPERATOR, ""));
		if (getExternelVariable() == true) {
			type = "";
		}
		String.format("%s %s", type, this.getVituralName());

		Map<Integer, String> values = new TreeMap<>();
		for (IAbstractDataNode child : this.getChildren()) {
			NormalDataNode nChild = (NormalDataNode) child;

			String index = Utils.getIndexOfArray(nChild.getName()).get(0);
			values.put(Utils.toInt(index), nChild.getValue());
		}

		for (Integer key : values.keySet()) {
			int ASCII = Utils.toInt(values.get(key));
			switch (ASCII) {
			case 34:/* nhay kep */
				initialization += "\\\"";
				break;

			case 92:/* gach cheo */
				initialization += "\\\\";
				break;

			case 39:
				/* nhay don */
				initialization += "\\'";
				break;

			default:
				initialization += (char) ASCII;
			}
		}

		if (this.isAttribute())
			input += this.getSetterInStr(Utils.putInString(initialization)) + SpecialCharacter.END_OF_STATEMENT;
		return input;
	}

	private boolean isVisible() {
		for (IAbstractDataNode child : this.getChildren()) {

			int ASCII = Utils.toInt(((NormalDataNode) child).getValue());

			if (!Utils.isVisibleCh(ASCII))
				return false;
		}
		return true;
	}

	@Override
	public String generareSourcecodetoReadInputFromFile() throws Exception {
		String typeVar = VariableTypes.deleteStorageClasses(this.getType())
				.replace(IAbstractDataNode.REFERENCE_OPERATOR, "")
				.replace(IAbstractDataNode.ONE_LEVEL_POINTER_OPERATOR, "");
		String loadValueStm = "data.findOneDimensionOrLevelBasicByName<" + typeVar + ">(\"" + getVituralName()
				+ "\", DEFAULT_VALUE_FOR_CHARACTER)";

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
