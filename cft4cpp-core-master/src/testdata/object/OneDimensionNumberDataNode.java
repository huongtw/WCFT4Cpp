package testdata.object;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import testdatagen.testdatainit.VariableTypes;
import utils.SpecialCharacter;
import utils.Utils;

public class OneDimensionNumberDataNode extends OneDimensionDataNode {

	@Override
	public String getInputForDisplay() throws Exception {

		String input = "";
		String initialization = "";
		String declaration = "";

		if (this.canConvertToString()) {
			declaration = String.format("%s=", this.getVituralName());

			Map<Integer, String> values = new TreeMap<>();
			for (IAbstractDataNode child : this.getChildren()) {
				NormalDataNode nChild = (NormalDataNode) child;

				String index = Utils.getIndexOfArray(nChild.getName()).get(0);
				values.put(Utils.toInt(index), nChild.getValue());
			}

			for (Integer key : values.keySet()) {
				int value = Utils.toInt(values.get(key));
				initialization += value + ",";
			}
			input = declaration + "={" + initialization + "}" + SpecialCharacter.LINE_BREAK;
			input = input.replace(",}", "}");

		} else {
			for (IAbstractDataNode child : this.getChildren())
				initialization += child.getInputForGoogleTest();
			input = initialization;
		}
		if (this.isAttribute())
			input += this.getDotSetterInStr(this.getVituralName()) + SpecialCharacter.LINE_BREAK;
		return input;
	}

	@Override
	public String getInputForGoogleTest() throws Exception {

		String input = "";
		String initialization = "";
		String declaration = "";

		/*
		 * If we can shorten the input for google test, the length of the input will be
		 * reduced significantly!
		 */
		if (this.canConvertToString()) {
			String type = VariableTypes
					.deleteStorageClasses(this.getType().replace(IAbstractDataNode.REFERENCE_OPERATOR, ""));
			type = type.replaceAll("\\[.*\\]", "");
            if (getExternelVariable() == true) {
                type = "";
            }
			declaration = String.format("%s %s[]", type, this.getVituralName());

			Map<Integer, String> values = new TreeMap<>();
			for (IAbstractDataNode child : this.getChildren()) {
				NormalDataNode nChild = (NormalDataNode) child;

				String index = Utils.getIndexOfArray(nChild.getName()).get(0);
				values.put(Utils.toInt(index), nChild.getValue());
			}

			for (Integer key : values.keySet()) {
				int value = Utils.toInt(values.get(key));
				initialization += value + ",";
			}
			input = declaration + "={" + initialization + "}" + SpecialCharacter.END_OF_STATEMENT;
			input = input.replace(",}", "}");

		} else {
			String type = VariableTypes
					.deleteStorageClasses(this.getType().replace(IAbstractDataNode.REFERENCE_OPERATOR, ""));
			String coreType = type.replaceAll("\\[.*\\]", "");
            if (getExternelVariable() == true) {
                coreType = "";
            }
			List<String> indexes = Utils.getIndexOfArray(type);

			if (indexes.size() > 0) {
				String dimension = "";
				for (String index : indexes)
					if (index.length() == 0)
						dimension += Utils.asIndex(this.getSize());
					else
						dimension += Utils.asIndex(index);

				if (this.getParent() instanceof StructureDataNode)
					declaration = "";
				else
					declaration = String.format("%s %s%s" + SpecialCharacter.END_OF_STATEMENT, coreType,
							this.getVituralName(), dimension);
			} else if (this.getParent() instanceof StructureDataNode)
				declaration = "";
			else
				declaration = String.format("%s %s[%s]" + SpecialCharacter.END_OF_STATEMENT, coreType,
						this.getVituralName(), this.getSize());

			for (IAbstractDataNode child : this.getChildren())
				initialization += child.getInputForGoogleTest();
			input = declaration + SpecialCharacter.END_OF_STATEMENT + initialization;
		}

		// if (isAttribute()) {
		// input += getSetterInStr(getVituralName()) +
		// SpecialCharacter.END_OF_STATEMENT;
		// }
		return input;
	}

	@Override
	public String generareSourcecodetoReadInputFromFile() throws Exception {
		if (getParent() instanceof RootDataNode) {
			String typeVar = VariableTypes.deleteStorageClasses(this.getType())
					.replace(IAbstractDataNode.REFERENCE_OPERATOR, "")
					.replace(IAbstractDataNode.ONE_LEVEL_POINTER_OPERATOR, "");
			typeVar = typeVar.substring(0, typeVar.indexOf("["));

			String loadValueStm = "data.findOneDimensionOrLevelBasicByName<" + typeVar + ">(\"" + getVituralName()
					+ "\", DEFAULT_VALUE_FOR_NUMBER)";

			String fullStm = typeVar + "* " + this.getVituralName() + "=" + loadValueStm
					+ SpecialCharacter.END_OF_STATEMENT;
			return fullStm;
		} else {
			// belong to structure node
			// Handle later;
			return "";
		}
	}
}
