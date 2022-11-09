package testdatagen.se.memory;

/**
 * Represent one level pointer
 *
 * @author ducanh
 */
public class OneLevelSymbolicVariable extends PointerSymbolicVariable {
    /**
     * Size of the variable. Ex: char* s = new char[100];
     */
    private String size;

    public OneLevelSymbolicVariable(String name, String type, int scopeLevel) {
        super(name, type, scopeLevel);
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
