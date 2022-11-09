package testdatagen.module;

import config.IFunctionConfig;
import testdata.object.EnumDataNode;
import testdata.object.IAbstractDataNode;
import testdata.object.NormalCharacterDataNode;
import testdata.object.NormalDataNode;
import testdata.object.NormalNumberDataNode;
import testdata.object.OneDimensionDataNode;
import testdata.object.OneLevelCharacterDataNode;
import testdata.object.OneLevelDataNode;
import testdata.object.OneLevelNumberDataNode;
import testdata.object.OneLevelStructureDataNode;
import testdatagen.testdatainit.BasicTypeRandom;
import tree.object.EnumNode;
import utils.VariableTypesUtils;

/**
 * Generate value for the data nodes which dont have any value.
 * 
 * @author ducanhnguyen
 *
 */
public class RandomValueGen {

	public RandomValueGen() {
	}

	private boolean isLeaf(IAbstractDataNode n) {
		return n.getChildren().size() == 0;
	}

	public void randomValue(IAbstractDataNode n) {
		IFunctionConfig fnConfig = Search2.getRoot(n).getFunctionNode().getFunctionConfig();

		for (IAbstractDataNode child : n.getChildren())
			if (!child.isInStaticSolution())
				if (child instanceof NormalCharacterDataNode)
					((NormalDataNode) child).setValue(BasicTypeRandom.generateInt(
							fnConfig.getCharacterBound().getLower(), fnConfig.getCharacterBound().getUpper()));
				else if (child instanceof NormalNumberDataNode)
					((NormalDataNode) child).setValue(BasicTypeRandom.generateInt(fnConfig.getIntegerBound().getLower(),
							fnConfig.getIntegerBound().getUpper()));
				else if (child instanceof EnumDataNode) {
					((EnumDataNode) child).setValue(randomValueEnum(child));
				} else
				// ONE DIMENSION
				if (child instanceof OneDimensionDataNode && isLeaf(child))
					((OneDimensionDataNode) child).setSize(fnConfig.getSizeOfArray());
				else
				// ONE LEVEL
				// We set the specified size for all of one-level variables
				if ((child instanceof OneLevelNumberDataNode || child instanceof OneLevelStructureDataNode)
						&& isLeaf(child))
					((OneLevelDataNode) child).setAllocatedSize(fnConfig.getSizeOfArray());
				else if (child instanceof OneLevelCharacterDataNode && isLeaf(child))
					// Increase the size by 1 by adding the null terminating character ('\0')
					((OneLevelDataNode) child).setAllocatedSize(fnConfig.getSizeOfArray() + 1);
				else {
					// dont support in the current version
				}
	}

	private String randomValueEnum(IAbstractDataNode child) {
		EnumNode enumNode = VariableTypesUtils.findEnumNode(child.getType());
		return enumNode.getAllNameEnumItems()
				.get(BasicTypeRandom.generateInt(0, enumNode.getAllNameEnumItems().size() - 1));
	}
}
