package testdata.object;

import testdatagen.testdatainit.VariableTypes;
import utils.SpecialCharacter;

/**
 * Represent structure variable such as class, struct, etc.
 *
 * @author DucAnh
 */
public class StructureDataNode extends AbstractDataNode {

    @Override
    public String getInputForGoogleTest() throws Exception {
        if (this.isArrayElement() || this.isAttribute())
            return super.getInputForGoogleTest();
        else {
            String declaration = "";
            if (getExternelVariable() == true) {
                declaration = this.getName() + SpecialCharacter.END_OF_STATEMENT;
            } else
                declaration = this.getType().replace(IAbstractDataNode.REFERENCE_OPERATOR, "") + " " + this.getName()
                        + SpecialCharacter.END_OF_STATEMENT;
            return declaration + super.getInputForGoogleTest();
        }
    }

    @Override
    public String generareSourcecodetoReadInputFromFile() throws Exception {
        // Ex: Date d = data.findStructureDateByName("born");
        String typeVar = VariableTypes.deleteStorageClasses(this.getType())
                .replace(IAbstractDataNode.REFERENCE_OPERATOR, "");
        // Ex: A::B, ::B. We need to get B
        if (typeVar.contains("::"))
            typeVar = typeVar.substring(typeVar.lastIndexOf("::") + 2);

        String loadValueStm = "data.findStructure" + typeVar + "ByName" + "(\"" + getVituralName() + "\")";

        String fullStm = typeVar + " " + this.getVituralName() + "=" + loadValueStm + SpecialCharacter.END_OF_STATEMENT;
        return fullStm;
    }

    @Override
    public String generateInputToSavedInFile() throws Exception {
        String output = "";
        for (AbstractDataNode child : getChildren())
            output += child.generateInputToSavedInFile() + SpecialCharacter.LINE_BREAK;
        return output;
    }
}
