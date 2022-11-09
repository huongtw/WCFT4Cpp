package testdata.object;

import testdatagen.testdatainit.VariableTypes;
import utils.SpecialCharacter;

public class OneDimensionStructureDataNode extends OneDimensionDataNode {

    @Override
    public String getInputForDisplay() throws Exception {
        String input = "";

        for (IAbstractDataNode child : this.getChildren())
            input += child.getInputForDisplay();
        if (this.isAttribute())
            input += this.getDotSetterInStr(this.getVituralName()) + SpecialCharacter.LINE_BREAK;
        return input;
    }

    @Override
    public String getInputForGoogleTest() throws Exception {
        String input = "";
        String initialization = "";
        String declaration = "";

        String declarationType = VariableTypes
                .deleteStorageClasses(this.getType().replace(IAbstractDataNode.REFERENCE_OPERATOR, ""));
        String coreType = declarationType.replaceAll("\\[.*\\]", "");
        if (getExternelVariable() == true) {
            coreType = "";
        }
        /*
         * Convert array declaration into pointer declaration. Ex:
		 * "SinhVien[]"------> "SinhVien*"
		 */
        declarationType = declarationType.replaceAll("\\[.*\\]", IAbstractDataNode.ONE_LEVEL_POINTER_OPERATOR);

        if (getExternelVariable() == true) {
            declaration = String.format("%s = new %s[%s]", this.getVituralName(), coreType,
                    this.getSize() + 1);
        } else {
            declaration = String.format("%s %s = new %s[%s]", declarationType, this.getVituralName(), coreType,
                    this.getSize() + 1);
        }

        for (IAbstractDataNode child : this.getChildren())
            initialization += child.getInputForGoogleTest();
        input = declaration + SpecialCharacter.END_OF_STATEMENT + initialization;

        if (this.isAttribute())
            input += this.getSetterInStr(this.getVituralName()) + SpecialCharacter.END_OF_STATEMENT;
        return input;
    }
}
