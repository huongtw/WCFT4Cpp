package testdata.object;

import java.util.Map;
import java.util.TreeMap;

import testdatagen.testdatainit.VariableTypes;
import utils.SpecialCharacter;
import utils.Utils;

public class OneDimensionCharacterDataNode extends OneDimensionDataNode {
    private String generateDetailedInputforDisplay() throws Exception {
        String input = "";
        for (IAbstractDataNode child : this.getChildren())
            input += child.getInputForDisplay();
        return input;

    }

    private String generateDetailedInputforGTest() throws Exception {
        String input = "";

        String type = VariableTypes
                .deleteStorageClasses(this.getType().replace(IAbstractDataNode.REFERENCE_OPERATOR, ""));
        type = type.substring(0, type.indexOf("["));
        if (getExternelVariable() == true) {
            type = "";
        }
        String declaration;
        if (this.getSize() > 0) {
            /**
             * Máº·c Ä‘á»‹nh táº¥t cáº£ má»�i pháº§n tá»­ trong máº£ng Ä‘á»�u lÃ  kÃ­ tá»±
             * tráº¯ng. Ta BUá»˜C pháº£i lÃ m Ä‘iá»�u nÃ y Ä‘á»ƒ cÃ¡c pháº§n tá»­ trong
             * máº£ng liÃªn tá»¥c
             */
            String space = "";
            for (int i = 0; i < this.getSize(); i++)
                space += " ";
            /**
             *
             */
            declaration = String.format("%s %s[%s]=\"%s\"", type, this.getVituralName(), this.getSize() + 1, space);
        } else
            declaration = String.format("%s %s[%s]", type, this.getVituralName(), this.getSize() + 1);
        String initialization = "";

        for (IAbstractDataNode child : this.getChildren())
            initialization += child.getInputForGoogleTest();
        input = declaration + SpecialCharacter.END_OF_STATEMENT + initialization;

        if (this.isAttribute())
            input += this.getSetterInStr(this.getVituralName()) + SpecialCharacter.END_OF_STATEMENT;
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

    private String generateSimplifyInputforGTest() {
        String input = "";

        String type = VariableTypes
                .deleteStorageClasses(this.getType().replace(IAbstractDataNode.REFERENCE_OPERATOR, ""));
        type = type.substring(0, type.indexOf("["));
        if (getExternelVariable() == true) {
            type = "";
        }

        String initialization = "";

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
            input = this.getSetterInStr(Utils.putInString(initialization)) + SpecialCharacter.END_OF_STATEMENT;
        else if (this.isPassingVariable())
            input = type + " " + this.getVituralName() + "[]=" + Utils.putInString(initialization)
                    + SpecialCharacter.END_OF_STATEMENT;
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

        if (this.canConvertToString() && this.isVisible())
            input = this.generateSimplifyInputforGTest();
        else
            input = this.generateDetailedInputforGTest();
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
        if (getParent() instanceof RootDataNode) {
            String typeVar = VariableTypes.deleteStorageClasses(this.getType())
                    .replace(IAbstractDataNode.REFERENCE_OPERATOR, "")
                    .replace(IAbstractDataNode.ONE_LEVEL_POINTER_OPERATOR, "");
            typeVar = typeVar.substring(0, typeVar.indexOf("["));

            String loadValueStm = "data.findOneDimensionOrLevelBasicByName<" + typeVar + ">(\"" + getVituralName()
                    + "\", DEFAULT_VALUE_FOR_CHARACTER)";

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
