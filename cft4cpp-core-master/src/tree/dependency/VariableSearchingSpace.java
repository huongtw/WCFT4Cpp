package tree.dependency;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import config.Paths;
import parser.projectparser.ProjectParser;
import tree.object.ClassNode;
import tree.object.IFunctionNode;
import tree.object.INode;
import tree.object.NamespaceNode;
import tree.object.StructNode;
import tree.object.VariableNode;
import utils.Utils;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

/**
 * Get searching space of a node in the structure tree.
 * <p>
 * If the node is function/attribute, the searching space consists of the
 * containing file, and all included file (represented in #include)
 *
 * @author DucAnh
 */
public class VariableSearchingSpace implements IVariableSearchingSpace {
	final static Logger logger = Logger.getLogger(VariableSearchingSpace.class);
	public static int STRUCTUTRE_VS_NAMESPACE_INDEX = 0;

	public static int FILE_SCOPE_INDEX = 1;

	public static int INCLUDED_INDEX = 2;

	private List<Level> spaces = new ArrayList<>();

	public VariableSearchingSpace(INode startNode) {
		logger.setLevel(org.apache.log4j.Level.OFF);
		if (startNode instanceof IFunctionNode || startNode instanceof VariableNode)
			spaces = generateSearchingSpace(startNode);
	}

	public static void main(String[] args) {
		ProjectParser parser = new ProjectParser(new File(Paths.TSDV_R1));
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "SimpleTest(Color)").get(0);

		System.out.println(function.getAST().getRawSignature());
		IVariableSearchingSpace searching = new VariableSearchingSpace(function);
		System.out.println(searching.getSpaces().toString());

	}

	private Level getAllCurrentClassvsStructvsNamespace(INode n) {
		Level output = new Level();

		INode parent = null;
		do {
			parent = Utils.getClassvsStructvsNamesapceNodeParent(n);

			if (parent != null) {
				output.add(parent);
				n = parent.getParent();
			}
		} while (parent != null);

		return output;
	}

	private List<INode> getAllIncludedNodes(INode n) {
		logger.debug("getAllIncludedNodes from " + n.getAbsolutePath());
		if (n.getAbsolutePath().equals("/home/ducanhnguyen/Desktop/cloneProject/lib/stdio.h")) {
			int a = 0;
			a++;
		}
		List<INode> output = new ArrayList<>();

		if (n != null)
			try {
				for (Dependency child : n.getDependencies())
					if (child instanceof IncludeHeaderDependency)
						if (child.getStartArrow().equals(n)) {
							output.add(child.getEndArrow());
							/*
							 * In case recursive include
							 */
							output.addAll(getAllIncludedNodes(child.getEndArrow()));
						}
			} catch (StackOverflowError e) {
				e.printStackTrace();
			}
		return output;
	}

	@Override
	public List<Level> getSpaces() {
		return spaces;
	}

	/**
	 * Get the searching space of a node. Notice that the order of class/struct/file
	 * nodes in the structure is very important!
	 *
	 * @param n
	 * @return
	 */
	private List<Level> generateSearchingSpace(INode n) {
		List<Level> outputNodes = new ArrayList<>();
		/*
		 * Firstly, we must all its parents that belong to class, struct or namespace
		 * (highest priority)
		 */
		logger.debug("Search level 1");
		if (n.getParent() instanceof ClassNode || n.getParent() instanceof StructNode
				|| n.getParent() instanceof NamespaceNode) {
			Level space1 = getAllCurrentClassvsStructvsNamespace(n);
			outputNodes.add(space1);
		}
		VariableSearchingSpace.STRUCTUTRE_VS_NAMESPACE_INDEX = 0;
		/*
		 * Secondly, we get the containing file of the given node
		 */
		logger.debug("Search level 2");
		INode sourceCodeFileNode = Utils.getSourcecodeFile(n);
		Level l = new Level();
		l.add(sourceCodeFileNode);
		outputNodes.add(l);
		VariableSearchingSpace.FILE_SCOPE_INDEX = 1;
		/*
		 * Finally, get all included file (lowest priority)
		 */
		logger.debug("Search level 3");
		List<INode> includedNodes = getAllIncludedNodes(sourceCodeFileNode);
		if (includedNodes.size() > 0)
			outputNodes.add(new Level(includedNodes));
		VariableSearchingSpace.INCLUDED_INDEX = 2;
		logger.debug("Search level 3. Done.");
		return outputNodes;
	}
}
