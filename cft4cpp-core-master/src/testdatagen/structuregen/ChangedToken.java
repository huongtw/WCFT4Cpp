package testdatagen.structuregen;

/**
 * Mapping between old token and new token.
 * <p>
 * Ex, in old source code, we have a test path "sv.getAge()>0 => return a". We
 * will normalize this test path and it becomes "sv.age>0 => return a".
 * <p>
 * So in this case, we have token (sv.getAge(), sv.age). The old token is
 * "sv.getAge()", and the new token is "sv.age"
 *
 * @author DucAnh
 */
public class ChangedToken {

    protected String oldToken;
    protected String newToken;

    public ChangedToken() {

    }

    public ChangedToken(String newName, String oldName) {
        oldToken = oldName;
        newToken = newName;
    }

    public String getNewName() {
        return newToken;
    }

    public String getOldName() {
        return oldToken;
    }

    @Override
    public String toString() {
        return "new token = " + newToken + ", oldToken = " + oldToken + "\n";
    }
}
