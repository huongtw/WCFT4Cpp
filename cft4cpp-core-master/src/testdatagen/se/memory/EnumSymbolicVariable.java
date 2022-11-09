package testdatagen.se.memory;

import java.util.ArrayList;
import java.util.List;

public class EnumSymbolicVariable extends SimpleStructureSymbolicVariable {

	public EnumSymbolicVariable(String name, String type, int scopeLevel) {
		super(name, type, scopeLevel);
	}

	@Override
	public List<PhysicalCell> getAllPhysicalCells() {
		// TODO: Get all physical cells of this symbolic variable
		return new ArrayList<PhysicalCell>();
	}
}
