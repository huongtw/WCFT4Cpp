package testdatagen.structuregen;

import utils.Utils;

/**
 * The class used to represent token in case it is corresponding to
 * variable/attribute
 *
 * @author ducanhnguyen
 */
public class ChangedVariableToken extends ChangedToken {

    private String type;
    private String reducedName;

    public ChangedVariableToken() {
        super();
    }

    public ChangedVariableToken(String newName, String oldName) {
        super(newName, oldName);
    }

    public ChangedVariableToken(String type, String newName, String oldName, String reducedName) {
        super(newName, oldName);
        this.type = type;
        this.reducedName = reducedName;
    }

    public String getDeclaration() {
        if (type.contains("[") && type.contains("]")) {
            String nameType = Utils.getNameVariable(type);
            String index = type.replace(nameType, "");
            return nameType + " " + newToken + index;
        } else
            return type + " " + newToken;
    }

    @Override
    public String getNewName() {
        return newToken;
    }

    @Override
    public String getOldName() {
        return oldToken;
    }

    public String getReducedName() {
        return reducedName;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "type = " + type + ", new token = " + newToken + ", oldToken = " + oldToken + ", reduced name = "
                + reducedName + "\n";
    }
}
