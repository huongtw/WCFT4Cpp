package testdata.object;

import utils.SpecialCharacter;

/**
 * Created by DucToan on 27/07/2017
 */
public class EnumDataNode extends AbstractDataNode {
    /**
     * Represent value of variable
     */
    private String value;

    @Override
    public String getInputForDisplay() throws Exception {
        return this.getType() + " " + this.getName() + " = " + this.getValue() + SpecialCharacter.END_OF_STATEMENT;
    }

    @Override
    public String getInputForGoogleTest() throws Exception {
        if (getExternelVariable() == true) {
            return this.getName() + " = " + this.getValue() + SpecialCharacter.END_OF_STATEMENT;
        } else {
            return this.getType() + " " + this.getName() + " = " + this.getValue() + SpecialCharacter.END_OF_STATEMENT;
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
