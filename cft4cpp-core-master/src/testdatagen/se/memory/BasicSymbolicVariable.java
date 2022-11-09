package testdatagen.se.memory;

import java.util.ArrayList;
import java.util.List;

import testdatagen.testdatainit.VariableTypes;
import utils.IRegex;

/**
 * Represent basic variable (number and character)
 *
 * @author ducanh
 */
public class BasicSymbolicVariable extends SymbolicVariable {
    protected PhysicalCell physicalCell;

    public BasicSymbolicVariable(String name, String type, int scopeLevel, String value) {
        super(name, type, scopeLevel);
        physicalCell = new PhysicalCell(value);
    }

    @Override
    public String toString() {
        return "name=" + name + ", value=" + getSymbolicValue();
    }

    @Override
    public boolean isBasicType() {
        return true;
    }

    public String getSymbolicValue() {
        return physicalCell.getValue();
    }

    public void setValue(String value) {
        if (!VariableTypes.isNumFloat(type)) {
            if (value.matches(IRegex.INTEGER_NUMBER_REGEX))
                physicalCell.setValue(value);
            else
                // physicalCell.setValue(ISymbolicExecution.TO_INT_Z3 + "*(" +
                // value + "+0)");
                physicalCell.setValue("(" + value + "+0)");
        } else
            physicalCell.setValue(value);
    }

    public PhysicalCell getCell() {
        return physicalCell;
    }

    public void setCell(PhysicalCell cell) {
        physicalCell = cell;
    }

    @Override
    public List<PhysicalCell> getAllPhysicalCells() {
        List<PhysicalCell> physicalCells = new ArrayList<>();
        physicalCells.add(physicalCell);
        return physicalCells;
    }
}
