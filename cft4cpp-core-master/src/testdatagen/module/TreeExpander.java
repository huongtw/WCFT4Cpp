package testdatagen.module;

import java.io.File;

import config.Paths;
import parser.projectparser.ProjectParser;
import testdata.object.AbstractDataNode;
import testdata.object.NormalCharacterDataNode;
import testdata.object.NormalDataNode;
import testdata.object.NormalNumberDataNode;
import testdata.object.OneDimensionCharacterDataNode;
import testdata.object.OneDimensionDataNode;
import testdata.object.OneDimensionNumberDataNode;
import testdata.object.OneDimensionStructureDataNode;
import testdata.object.OneLevelCharacterDataNode;
import testdata.object.OneLevelDataNode;
import testdata.object.OneLevelNumberDataNode;
import testdata.object.OneLevelStructureDataNode;
import testdata.object.RootDataNode;
import testdata.object.StructureDataNode;
import testdata.object.TwoDimensionCharacterDataNode;
import testdata.object.TwoDimensionDataNode;
import testdata.object.TwoDimensionNumberDataNode;
import testdata.object.TwoLevelCharacterDataNode;
import testdata.object.TwoLevelDataNode;
import testdata.object.TwoLevelNumberDataNode;
import testdatagen.testdatainit.VariableTypes;
import tree.object.FunctionNode;
import tree.object.IFunctionNode;
import tree.object.INode;
import tree.object.VariableNode;
import utils.IRegex;
import utils.Utils;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

/**
 * Create a data tree from a list of variables.
 * 
 * Ex: We have these variables: "a[0]", "b", "c.d" -----> create tree containing
 * all of these variables
 *
 * @author ducanh
 */
public class TreeExpander {
	int id = 0;

	private IFunctionNode functionNode;

	public TreeExpander() {
	}

	public static void main(String[] args) throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.TSDV_R1_4));

		String name = "PointerTypeDefTest(MyIntPtr)";
		// String name = "StackLinkedList::push(Node*)";
		FunctionNode function = (FunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), name).get(0);

		//
		RootDataNode root = new RootDataNode();
		InitialTreeGen dataTreeGen = new InitialTreeGen();
		dataTreeGen.generateTree(root, function);

		// new TreeExpander().expandTree(root, new String[] { "front", "N" });
		TreeExpander expander = new TreeExpander();
		expander.setFunctionNode(function);
		// expander.expandTree(root, new String[] { "front", "[3]", "data",
		// "fname", "[2]" });
		// expander.expandTree(root, new String[] { "front", "[4]" });

		expander.expandTree(root, new String[] { "x", "[0]" });
		System.out.println(new SimpleTreeDisplayer().toString(root));
	}

	public void expandTree(RootDataNode root, String[] names) throws Exception {
		AbstractDataNode currentParent = root;
		for (String name : names) {
			AbstractDataNode n = Search2.findNodeByName(name, currentParent);
			if (n == null) {
				// Case 1: Array index, e.g, [0]
				if (name.matches("\\[.*\\]")) {
					int index = Utils.toInt(Utils.getIndexOfArray(name).get(0));

					if (currentParent instanceof OneLevelDataNode) {
						int oldIndex = getMax(((OneLevelDataNode) currentParent).getAllocatedSize(),
								functionNode.getFunctionConfig().getSizeOfArray());

						if (oldIndex < index + 1)
							// we increase index by 1 to contain the null terminating character ('\0')
							((OneLevelDataNode) currentParent).setAllocatedSize(index + 1);
						else
							((OneLevelDataNode) currentParent).setAllocatedSize(oldIndex);

					} else if (currentParent instanceof TwoLevelDataNode) {
						// TODO: Dont support in this version
					}
				}

				if (currentParent instanceof StructureDataNode) {
					INode nParent = currentParent.getCorrespondingVar().resolveCoreType();
					if (nParent != null) {
						VariableNode searchedNode = (VariableNode) Search.searchFirstNodeByName(nParent, name);

						if (searchedNode != null)
							currentParent = generateStructureItem(searchedNode, name, currentParent);
					}
				} else if (currentParent instanceof OneLevelStructureDataNode
						|| currentParent instanceof OneDimensionStructureDataNode)
					currentParent = generateArrayItemInStructureVariable(name, currentParent);
				else if (currentParent instanceof OneDimensionCharacterDataNode
						|| currentParent instanceof OneDimensionNumberDataNode
						|| currentParent instanceof OneLevelCharacterDataNode
						|| currentParent instanceof OneLevelNumberDataNode)
					currentParent = generateArrayItemInBasicVariable(name, currentParent);
				else if (currentParent instanceof TwoDimensionCharacterDataNode
						|| currentParent instanceof TwoDimensionNumberDataNode
						|| currentParent instanceof TwoLevelCharacterDataNode
						|| currentParent instanceof TwoLevelNumberDataNode)
					currentParent = generateArrayItemInBasicVariable(name, currentParent);
			} else
				currentParent = n;
		}
	}

	private NormalDataNode generateArrayItemInBasicVariable(String element, AbstractDataNode currentParent) {
		int index = getMax(Utils.toInt(Utils.getIndexOfArray(element).get(0)),
				functionNode.getFunctionConfig().getSizeOfArray());
		// STEP 1.
		if (currentParent instanceof OneDimensionDataNode) {
			if (((OneDimensionDataNode) currentParent).getSize() < index + 1)
				((OneDimensionDataNode) currentParent).setSize(index + 1);
		} else if (currentParent instanceof OneLevelDataNode) {
			if (((OneLevelDataNode) currentParent).getAllocatedSize() < index)
				((OneLevelDataNode) currentParent).setAllocatedSize(index);
		} else if (currentParent instanceof TwoDimensionDataNode) {
			if (((TwoDimensionDataNode) currentParent).getSizeA() < index)
				((TwoDimensionDataNode) currentParent).setSizeA(index);
			if (((TwoDimensionDataNode) currentParent).getSizeB() < index)
				((TwoDimensionDataNode) currentParent).setSizeB(index);
		} else if (currentParent instanceof TwoLevelDataNode) {
			if (((TwoLevelDataNode) currentParent).getAllocatedSizeA() < index)
				((TwoLevelDataNode) currentParent).setAllocatedSizeA(index);
			if (((TwoLevelDataNode) currentParent).getAllocatedSizeB() < index)
				((TwoLevelDataNode) currentParent).setAllocatedSizeB(index);
		}
		// STEP 2.
		VariableNode v = new VariableNode();
		v.setName(element);
		v.setParent(currentParent.getCorrespondingVar());

		String rType = VariableTypes.deleteStorageClasses(currentParent.getCorrespondingVar().getReducedRawType());
		rType = rType.replace("*", "").replaceAll("\\[.*\\]", "");

		v.setRawType(rType);
		v.setCoreType(rType);
		v.setReducedRawType(rType);

		// STEP 3.
		NormalDataNode child = null;
		if (VariableTypes.isCh(v.getRawType()))
			child = new NormalCharacterDataNode();
		else
			child = new NormalNumberDataNode();

		child.setParent(currentParent);
		child.setCorrespondingVar(v);
		child.setType(v.getRawType());
		child.setName(element);
		currentParent.addChild(child);

		return child;
	}

	private AbstractDataNode generateArrayItemInStructureVariable(String element, AbstractDataNode currentParent) {
		// STEP 1.
		VariableNode v = new VariableNode();
		v.setName(element);
		v.setParent(currentParent.getCorrespondingVar());

		String rType = VariableTypes.deleteStorageClasses(currentParent.getCorrespondingVar().getReducedRawType());
		rType = rType.replace("*", "").replaceAll(IRegex.ARRAY_INDEX, "");

		v.setRawType(rType);
		v.setCoreType(rType);
		v.setReducedRawType(rType);

		// STEP 2.
		StructureDataNode child = new StructureDataNode();
		child.setParent(currentParent);
		child.setCorrespondingVar(v);
		child.setType(v.getRawType());
		child.setName(element);

		currentParent.addChild(child);

		return child;
	}

	private AbstractDataNode generateStructureItem(VariableNode vChild, String element, AbstractDataNode currentParent)
			throws Exception {
		AbstractDataNode child = null;

		if (VariableTypes.isBasic(vChild.getRawType())) {

			if (VariableTypes.isCh(vChild.getRawType()))
				child = new NormalCharacterDataNode();
			else
				child = new NormalNumberDataNode();

			child.setCorrespondingVar(vChild);
			child.setName(element);
			child.setType(vChild.getRawType());
			child.setParent(currentParent);
			currentParent.addChild(child);

			currentParent = child;

		} else if (VariableTypes.isChOneDimension(vChild.getRawType())) {
			child = new OneDimensionCharacterDataNode();
			child.setCorrespondingVar(vChild);
			child.setType(vChild.getRawType());
			child.setName(element);
			currentParent.addChild(child);
			child.setParent(currentParent);

		} else if (VariableTypes.isChOneLevel(vChild.getRawType())) {

			child = new OneLevelCharacterDataNode();
			child.setCorrespondingVar(vChild);
			child.setType(vChild.getReducedRawType());
			child.setName(element);
			currentParent.addChild(child);
			child.setParent(currentParent);

		} else if (VariableTypes.isNumOneLevel(vChild.getRawType())) {
			child = new OneLevelNumberDataNode();
			child.setCorrespondingVar(vChild);
			child.setName(element);
			child.setType(vChild.getRawType());
			currentParent.addChild(child);
			child.setParent(currentParent);

		} else if (VariableTypes.isNumOneDimension(vChild.getRawType())) {
			child = new OneDimensionNumberDataNode();
			child.setCorrespondingVar(vChild);
			child.setType(vChild.getRawType());
			child.setName(element);
			currentParent.addChild(child);
			child.setParent(currentParent);

		} else if (VariableTypes.isStructureSimple(vChild.getRawType())) {
			child = new StructureDataNode();
			child.setCorrespondingVar(vChild);
			child.setType(vChild.getRawType());
			child.setName(element);
			currentParent.addChild(child);
			child.setParent(currentParent);

		} else if (VariableTypes.isStructureOneLevel(vChild.getRawType())) {
			child = new OneLevelStructureDataNode();
			child.setCorrespondingVar(vChild);
			child.setType(vChild.getRawType());
			child.setName(element);
			currentParent.addChild(child);
			child.setParent(currentParent);

		} else if (VariableTypes.isStructureOneDimension(vChild.getRawType())) {
			child = new OneDimensionStructureDataNode();
			child.setCorrespondingVar(vChild);
			child.setType(vChild.getRawType());
			child.setName(element);
			currentParent.addChild(child);
			child.setParent(currentParent);

		} else
			throw new Exception("Chua xu ly " + element + " trong generateStructureItem");

		return child;
	}

	private int getMax(int a, int b) {
		return a > b ? a : b;
	}

	public IFunctionNode getFunctionNode() {
		return functionNode;
	}

	public void setFunctionNode(IFunctionNode functionNode) {
		this.functionNode = functionNode;
	}
}
