package testdatagen.se.memory;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent pointer variable
 *
 * @author ducanh
 */
public class PointerSymbolicVariable extends SymbolicVariable {
    public static final int FIRST_INDEX = 0;
    /**
     * Reference of variable
     */
    protected Reference reference = null;

    public PointerSymbolicVariable(String name, String type, int scopeLevel) {
        super(name, type, scopeLevel);
        /*
		 * Automatically initialize the reference of the variable by assuming it
		 * is not NULL.
		 */
        reference = new Reference(new LogicBlock(name));
        reference.setStartIndex(PointerSymbolicVariable.FIRST_INDEX + "");
    }

    @Override
    public String toString() {
        if (reference != null)
            return "name=" + name + "\n\t, reference=" + reference.toString();
        else
            return "name=" + name + ", reference=null";
    }

    @Override
    public boolean isBasicType() {
        return false;
    }

    public Reference getReference() {
        return reference;
    }

    public void setReference(Reference reference) {
        this.reference = reference;
    }

    @Override
    public List<PhysicalCell> getAllPhysicalCells() {
        List<PhysicalCell> physicalCells = new ArrayList<>();

        if (reference != null)
            for (LogicCell logicCell : reference.getBlock())
                physicalCells.add(logicCell.getPhysicalCell());
        return physicalCells;
    }

    public List<LogicCell> getAllLogicCells() {
        List<LogicCell> logicCells = new ArrayList<>();

        if (reference != null)
            for (LogicCell logicCell : reference.getBlock())
                logicCells.add(logicCell);
        return logicCells;
    }
}
