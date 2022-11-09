package testdatagen.se.expander;

import java.util.ArrayList;
import java.util.List;

import testdatagen.se.memory.IVariableNodeTable;

public abstract class AbstractPathConstraintExpander implements IPathConstraintExpander {
    /**
     * The input constraint needed to be analyzed
     */
    protected String inputConstraint;

    /**
     * Table of variable
     */
    protected IVariableNodeTable tableMapping;

    /**
     * A list of new constraints generated from the input constraint
     */
    protected List<String> newConstraints = new ArrayList<>();

    public String getInputConstraint() {
        return inputConstraint;
    }

    public void setInputConstraint(String inputConstraint) {
        this.inputConstraint = inputConstraint;
    }

    public IVariableNodeTable getTableMapping() {
        return tableMapping;
    }

    public void setTableMapping(IVariableNodeTable tableMapping) {
        this.tableMapping = tableMapping;
    }

    public List<String> getNewConstraints() {
        return newConstraints;
    }

    public void setNewConstraints(List<String> newConstraints) {
        this.newConstraints = newConstraints;
    }

}
