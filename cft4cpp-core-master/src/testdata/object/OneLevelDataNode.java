package testdata.object;

import testdatagen.testdatainit.VariableTypes;
import utils.SpecialCharacter;
import utils.Utils;

public class OneLevelDataNode extends PointerDataNode {

	/**
	 * The allocated size, including '\0'.
	 * 
	 * Ex1: node="xyz" ---> allocatedSize = 4 <br/>
	 * Ex2: node="" ---> allocatedSize = 1
	 */
	private int allocatedSize;

	public boolean canConvertToString() {
		return false;
	}

	public int getAllocatedSize() {
		return this.allocatedSize;
	}

	public void setAllocatedSize(int allocatedSize) {
		this.allocatedSize = allocatedSize;
	}

	public String getAllocation() {
		String input = "";

		String type = VariableTypes
				.deleteStorageClasses(this.getType().replace(IAbstractDataNode.REFERENCE_OPERATOR, ""));
		String coreType = type.replace(IAbstractDataNode.ONE_LEVEL_POINTER_OPERATOR, "");
		input = String.format("%s = new %s[%s]" + SpecialCharacter.END_OF_STATEMENT + SpecialCharacter.LINE_BREAK,
				this.getVituralName(), coreType, this.getAllocatedSize());

		return input;
	}

	@Override
	public String getInputForDisplay() throws Exception {

		String input = "";
		if (!this.isAttribute()) {
			String type = VariableTypes
					.deleteStorageClasses(this.getType().replace(IAbstractDataNode.REFERENCE_OPERATOR, ""));
			String coreType = type.replace(IAbstractDataNode.ONE_LEVEL_POINTER_OPERATOR, "");
			String allocation = "";

			if (this.isNotNull()) {
				allocation = String.format("%s = new %s[%s]", this.getVituralName(), coreType, this.getAllocatedSize())
						+ SpecialCharacter.LINE_BREAK;
				if (this.getChildren().size() > 0)
					input += super.getInputForDisplay();
			} else
				allocation = String.format("%s = NULL", this.getVituralName()) + SpecialCharacter.LINE_BREAK;
			input += allocation;
		} else {
			String type = VariableTypes
					.deleteStorageClasses(this.getType().replace(IAbstractDataNode.REFERENCE_OPERATOR, ""));
			String coreType = type.replace(IAbstractDataNode.ONE_LEVEL_POINTER_OPERATOR, "");
			if (this.isNotNull())
				input += this.getSetterInStr("new " + coreType + Utils.asIndex(this.getAllocatedSize()))
						+ SpecialCharacter.LINE_BREAK;
			else
				input += this.getSetterInStr("NULL") + SpecialCharacter.LINE_BREAK;
		}

		return input;
	}

	@Override
	public String getInputForGoogleTest() throws Exception {
		String input = "";
		if (!this.isAttribute()) {
			String type = VariableTypes
					.deleteStorageClasses(this.getType().replace(IAbstractDataNode.REFERENCE_OPERATOR, ""));
			String coreType = type.replace(IAbstractDataNode.ONE_LEVEL_POINTER_OPERATOR, "");
			if (getExternelVariable() == true) {
				type = "";
			}
			String allocation = "";

			if (this.isNotNull())
				allocation = String.format("%s %s = new %s[%s]" + SpecialCharacter.END_OF_STATEMENT, type,
						this.getVituralName(), coreType, this.getAllocatedSize());
			else
				allocation = String.format("%s %s = " + IAbstractDataNode.NULL + SpecialCharacter.END_OF_STATEMENT,
						type, this.getVituralName());
			input += allocation;
		} else {
			String type = VariableTypes
					.deleteStorageClasses(this.getType().replace(IAbstractDataNode.REFERENCE_OPERATOR, ""));

			String coreType = type.replace(IAbstractDataNode.ONE_LEVEL_POINTER_OPERATOR, "");
			if (this.isNotNull())
				input += this.getSetterInStr("new " + coreType + Utils.asIndex(this.getAllocatedSize()))
						+ SpecialCharacter.END_OF_STATEMENT;
			else
				input += this.getSetterInStr(IAbstractDataNode.NULL) + SpecialCharacter.END_OF_STATEMENT;
		}

		input += super.getInputForGoogleTest();
		return input;
	}

	public boolean isNotNull() {
		return this.allocatedSize >= 1;
	}
}
