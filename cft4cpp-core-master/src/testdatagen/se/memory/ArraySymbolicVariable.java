package testdatagen.se.memory;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent array variable
 *
 * @author ducanh
 */
public class ArraySymbolicVariable extends SymbolicVariable {
	public static final int USPECIFIED_SIZE = -1;

	// Reference of variable
	protected LogicBlock logicBlock = null;

	public ArraySymbolicVariable(String name, String type, int scopeLevel) {
		super(name, type, scopeLevel);
		logicBlock = new LogicBlock(ISymbolicVariable.PREFIX_SYMBOLIC_VALUE + name);
	}

	@Override
	public boolean isBasicType() {
		return false;
	}

	@Override
	public String toString() {
		return "name=" + getName() + " | block= " + logicBlock.toString();
	}

	public LogicBlock getBlock() {
		return logicBlock;
	}

	public void setBlock(LogicBlock block) {
		logicBlock = block;
	}

	@Override
	public List<PhysicalCell> getAllPhysicalCells() {
		List<PhysicalCell> physicalCells = new ArrayList<>();

		for (LogicCell logicCell : logicBlock)
			physicalCells.add(logicCell.getPhysicalCell());
		return physicalCells;
	}

	public List<LogicCell> getAllLogicCells() {
		List<LogicCell> logicCells = new ArrayList<>();

		for (LogicCell logicCell : logicBlock)
			logicCells.add(logicCell);
		return logicCells;
	}
}
