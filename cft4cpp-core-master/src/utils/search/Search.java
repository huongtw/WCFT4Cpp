package utils.search;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tree.object.FunctionNode;
import tree.object.IFunctionNode;
import tree.object.INode;
import utils.Utils;

public class Search implements ISearch {

	public synchronized static INode searchFirstNodeByName(INode parent, String name) {
		for (INode child : parent.getChildren()) {
			String nameChild = "";
			if (child instanceof IFunctionNode)
				nameChild = ((FunctionNode) child).getSimpleName();
			else
				nameChild = child.getNewType();

			if (nameChild.equals(name))
				return child;
		}
		return null;
	}

	public synchronized static List<INode> searchNodes(INode root, List<SearchCondition> conditions) {
		List<INode> output = new ArrayList<>();

		for (INode child : root.getChildren()) {
			boolean isSatisfiable = false;

			for (ISearchCondition con : conditions)
				if (con.isSatisfiable(child)) {
					isSatisfiable = true;
					break;
				}

			if (isSatisfiable)
				output.add(child);
			output.addAll(Search.searchNodes(child, conditions));
		}
		return output;
	}

	public synchronized static List<INode> searchNodes(INode root, ISearchCondition condition) {
		List<INode> output = new ArrayList<>();

		for (INode child : root.getChildren()) {
			if (condition.isSatisfiable(child))
				output.add(child);
			output.addAll(Search.searchNodes(child, condition));
		}
		return output;
	}

	public synchronized static List<INode> searchNodes(INode root, ISearchCondition condition, String relativePath) {
		List<INode> output = Search.searchNodes(root, condition);
		relativePath = Utils.normalizePath(relativePath);
		if (!relativePath.startsWith(File.separator))
			relativePath = File.separator + relativePath;

		for (int i = output.size() - 1; i >= 0; i--) {
			INode n = output.get(i);
			if (!n.getAbsolutePath().endsWith(relativePath))
				output.remove(n);
		}
		return output;
	}

}
