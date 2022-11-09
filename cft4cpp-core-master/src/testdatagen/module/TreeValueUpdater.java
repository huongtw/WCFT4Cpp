package testdatagen.module;

import java.io.File;

import config.Paths;
import parser.projectparser.ProjectParser;
import testdata.object.EnumDataNode;
import testdata.object.IAbstractDataNode;
import testdata.object.NormalDataNode;
import testdata.object.OneLevelDataNode;
import testdata.object.PointerDataNode;
import testdata.object.RootDataNode;
import testdata.object.TwoLevelDataNode;
import tree.object.FunctionNode;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

public class TreeValueUpdater {

	public TreeValueUpdater() {
	}

	public static void main(String[] args) throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.DATA_GEN_TEST));

		String name = "test(int,int*,int[],int[2],char,char*,char[],char[10],SinhVien*,SinhVien,SinhVien[])";
		FunctionNode function = (FunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), name).get(0);

		/**
		 *
		 */
		RootDataNode root = new RootDataNode();
		InitialTreeGen dataTreeGen = new InitialTreeGen();
		dataTreeGen.generateTree(root, function);

		String names[] = new String[] { "sv", "[0]", "tt", "name", "[2]" };
		TreeExpander expander = new TreeExpander();
		expander.expandTree(root, names);

		new TreeValueUpdater().updateValue(names, "98", root);

	}

	public void updateNotNullValue(String[] names, RootDataNode root) {
		IAbstractDataNode n = Search2.findNodeByChainName(names, root);
		if (n != null && n instanceof OneLevelDataNode) {
			// assume the size of node, e.g., size = 1,2,3,etc.
			((OneLevelDataNode) n).setAllocatedSize(1);
			n.setInStaticSolution(true);

		} else if (n != null && n instanceof TwoLevelDataNode) {
			// assume the size of node, e.g., size = 1,2,3,etc.
			((TwoLevelDataNode) n).setAllocatedSizeA(1);
			((TwoLevelDataNode) n).setAllocatedSizeB(1);
			n.setInStaticSolution(true);
		}
	}

	public void updateNullValue(String[] names, RootDataNode root) {
		IAbstractDataNode n = Search2.findNodeByChainName(names, root);
		if (n != null && n instanceof OneLevelDataNode) {
			((OneLevelDataNode) n).setAllocatedSize(PointerDataNode.NULL_VALUE);
			n.setInStaticSolution(true);

		} else if (n != null && n instanceof TwoLevelDataNode) {
			((TwoLevelDataNode) n).setAllocatedSizeA(PointerDataNode.NULL_VALUE);
			((TwoLevelDataNode) n).setAllocatedSizeB(PointerDataNode.NULL_VALUE);
			n.setInStaticSolution(true);
		}
	}

	public void updateValue(String[] names, String value, RootDataNode root) {
		IAbstractDataNode n = Search2.findNodeByChainName(names, root);
		if (n != null)
			if (n instanceof NormalDataNode) {
				((NormalDataNode) n).setValue(value);
				n.setInStaticSolution(true);

			} else if (n instanceof EnumDataNode) {
				((EnumDataNode) n).setValue(value);
				n.setInStaticSolution(true);
			} else if ((n instanceof OneLevelDataNode || n instanceof TwoLevelDataNode) && value.equals("NULL")) {
				((OneLevelDataNode) n).setAllocatedSize(PointerDataNode.NULL_VALUE);
				n.getChildren().removeAll(n.getChildren());
				n.setInStaticSolution(true);
			}
	}
}
