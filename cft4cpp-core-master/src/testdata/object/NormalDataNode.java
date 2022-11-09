package testdata.object;

import testdatagen.testdatainit.VariableTypes;
import utils.SpecialCharacter;
import utils.Utils;

/**
 * Represent basic types belong to number of character
 *
 * @author DucAnh
 */
public class NormalDataNode extends AbstractDataNode {
    public static final String CHARACTER_QUOTE = "'";
    /**
     * Represent value of variable
     */
    private String value;

    private String generateAssignmentForDisplay() {

        String varInit = "";
        String valueVar = "";
        if (VariableTypes.isCh(this.getType())) {
            int numberValue = Utils.toInt(this.getValue());

            if (VariableTypes.isChBasic(this.getType()) && Utils.isVisibleCh(numberValue)
                    && !Utils.isSpecialChInVisibleRange(numberValue))
                valueVar = NormalDataNode.CHARACTER_QUOTE + (char) numberValue + NormalDataNode.CHARACTER_QUOTE;
            else
                valueVar = numberValue + "";
        } else
            valueVar = this.getValue() + "";

        varInit = this.getDotSetterInStr(valueVar) + SpecialCharacter.LINE_BREAK;
        return varInit;

    }

    /**
     * @return Ex:"a = 2"
     */
    private String generateAssignmentForGTest() {
        String varInit = "";
        String valueVar = "";

        if (VariableTypes.isCh(this.getType())) {
            int numberValue = Utils.toInt(this.getValue());
            if (VariableTypes.isChBasic(this.getType()) && Utils.isVisibleCh(numberValue)
                    && !Utils.isSpecialChInVisibleRange(numberValue))
                valueVar = NormalDataNode.CHARACTER_QUOTE + (char) numberValue + NormalDataNode.CHARACTER_QUOTE;
            else
                valueVar = numberValue + "";
        } else
            valueVar = this.getValue() + "";
        varInit = this.getSetterInStr(valueVar) + SpecialCharacter.END_OF_STATEMENT;
        return varInit;

    }

    /**
     * Ex output: "int a; a = 2;"
     *
     * @return
     */
    private String generateDeclarationAndIni() {
        String varInit = "";
        String typeVar = VariableTypes.deleteStorageClasses(this.getType())
                .replace(IAbstractDataNode.REFERENCE_OPERATOR, "");
        if (getExternelVariable() == true) {
            typeVar = "";
        }

		/*
         * In case the variable is float/double
		 */
        if (VariableTypes.isNumBasicFloat(typeVar))
            varInit = typeVar + " " + this.getVituralName() + "=" + this.getValue() + SpecialCharacter.END_OF_STATEMENT;
        else
		/*
		 * In case the variable is integer
		 */ {
            String valueVar = "";
            int numberValue = Utils.toInt(this.getValue());

            if (numberValue == Utils.UNDEFINED_TO_INT)
                varInit = typeVar + " " + this.getVituralName() + "=" + this.getValue()
                        + SpecialCharacter.END_OF_STATEMENT;
            else {
                if (VariableTypes.isChBasic(this.getType()) && Utils.isVisibleCh(numberValue)
                        && !Utils.isSpecialChInVisibleRange(numberValue))
                    valueVar = NormalDataNode.CHARACTER_QUOTE + (char) numberValue + NormalDataNode.CHARACTER_QUOTE;
                else
                    valueVar = numberValue + "";

                varInit = typeVar + " " + this.getVituralName() + "=" + valueVar + SpecialCharacter.END_OF_STATEMENT;
            }
        }
        return varInit;

    }

    /**
     * @return Ex: "sv.setAge(2)"
     */
    private String generateSetter() {
        String val = "";
        int iVal = Utils.toInt(this.getValue());

        if (VariableTypes.isChBasic(this.getType()) && Utils.isVisibleCh(iVal)
                && !Utils.isSpecialChInVisibleRange(iVal))
            val = NormalDataNode.CHARACTER_QUOTE + (char) iVal + NormalDataNode.CHARACTER_QUOTE;
        else
            val = this.getValue();
        return this.getSetterInStr(val) + SpecialCharacter.END_OF_STATEMENT;
    }

    @Override
    public String getInputForDisplay() throws Exception {
        String input = "";
        input = this.generateAssignmentForDisplay();
        return input;
    }

    @Override
    public String getInputForGoogleTest() throws Exception {
        String input = "";
        if (this.isPassingVariable())
            input = this.generateDeclarationAndIni();
        else if (this.isAttribute())
            input = this.generateSetter();
        else if (this.isArrayElement())
            input = this.generateAssignmentForGTest();
        else
            throw new Exception("Can not determine the type of variable " + this.getVituralName());
        return input;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getVituralName() {
        if (getParent() instanceof StructDataNode)
            return this.getParent().getVituralName() + "." + getName();
        else
            return this.getParent().getVituralName() + getName();
    }

    public void setValue(int value) {
        this.value = value + "";
    }

    @Override
    public String generareSourcecodetoReadInputFromFile() throws Exception {
        String typeVar = VariableTypes.deleteStorageClasses(this.getType())
                .replace(IAbstractDataNode.REFERENCE_OPERATOR, "");

        String loadValueStm = "data.findValueByName<" + typeVar + ">(\"" + getVituralName() + "\")";

        String fullStm = typeVar + " " + this.getVituralName() + "=" + loadValueStm + SpecialCharacter.END_OF_STATEMENT;
        return fullStm;
    }

    @Override
    public String generateInputToSavedInFile() throws Exception {
        return this.getVituralName() + "=" + this.getValue() + SpecialCharacter.LINE_BREAK;
    }
}
