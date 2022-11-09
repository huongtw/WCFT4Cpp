package testdatagen.structuregen;

import java.util.ArrayList;

public class ChangedTokens extends ArrayList<ChangedToken> {

    private static final long serialVersionUID = 1L;

    /**
     * Check whether a new name that have already put in the list
     *
     * @param newName
     * @return
     */
    public boolean containNewName(String newName) {
        for (ChangedToken token : this)
            if (token.getNewName().equals(newName))
                return true;
        return false;

    }
}
