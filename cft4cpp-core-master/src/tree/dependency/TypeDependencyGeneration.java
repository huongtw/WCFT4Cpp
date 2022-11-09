package tree.dependency;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import config.Paths;
import parser.projectparser.ProjectParser;
import testdatagen.testdatainit.VariableTypes;
import tree.object.AvailableTypeNode;
import tree.object.FunctionNode;
import tree.object.IFunctionNode;
import tree.object.INode;
import tree.object.IVariableNode;
import tree.object.NamespaceNode;
import tree.object.SourcecodeFileNode;
import tree.object.StructureNode;
import tree.object.TypedefDeclaration;
import tree.object.VariableNode;
import utils.Utils;
import utils.search.ClassvsStructvsNamespaceCondition;
import utils.search.FunctionNodeCondition;
import utils.search.ISearchCondition;
import utils.search.NodeCondition;
import utils.search.Search;
import utils.search.StructurevsTypedefCondition;

public class TypeDependencyGeneration {
    public static final int SIMPLE_TYPE_REFERENCE = 0;
    public static final int COMPLEX_TYPE_REFERENCE = 1;
    final static Logger logger = Logger.getLogger(TypeDependencyGeneration.class);
    private INode correspondingNode;

    public TypeDependencyGeneration(IVariableNode resolvedVarNode) {
        parse(resolvedVarNode);
    }

    public TypeDependencyGeneration(String nameType, IFunctionNode function) {
        IVariableNode resolvedVarNode = new VariableNode();
        resolvedVarNode.setParent(function);
        resolvedVarNode.setRawType(nameType);
        resolvedVarNode.setCoreType(nameType);
        resolvedVarNode.setReducedRawType(nameType);

        parse(resolvedVarNode);
    }

    public static void main(String[] args) {
        ProjectParser parser = new ProjectParser(new File(Paths.TSDV_R1));
        FunctionNode sampleNode = (FunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
                File.separator + "Level2MultipleNsTest(::X,::ns1::X,X)").get(0);
        IVariableNode var = sampleNode.getArguments().get(2);

        System.out.println(var.getParent().getAbsolutePath());
        INode correspondingNode = new TypeDependencyGeneration(var).getCorrespondingNode();

        if (correspondingNode instanceof AvailableTypeNode)
            System.out.println(((AvailableTypeNode) correspondingNode).getType());
        else
            System.out.println(correspondingNode.getAbsolutePath());
    }

    private void parse(IVariableNode resolvedVarNode) {
        if (resolvedVarNode instanceof VariableNode) {
            /*
			 * Initialize variable searching space
			 */
            List<Level> spaces = new VariableSearchingSpace(resolvedVarNode).getSpaces();

            resolvedVarNode.getCoreType();
            try {
                correspondingNode = performSimpleSearch(spaces, resolvedVarNode);
                if (correspondingNode == null)
                    correspondingNode = performComplexSearch(spaces, resolvedVarNode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * In this search strategy, we search the corresponding node based on its
     * path
     *
     * @param spaces          The variable searching space
     * @param resolvedVarNode
     * @return
     * @throws Exception
     */
    private INode performSimpleSearch(List<Level> spaces, IVariableNode resolvedVarNode) throws Exception {
        INode correspondingNode = null;
        String searchedPath = resolvedVarNode.getCoreType().replace("::", File.separator);
        String type = resolvedVarNode.getRawType();

        if (VariableTypes.isBasic(type) || VariableTypes.isOneDimensionBasic(type)
                || VariableTypes.isTwoDimensionBasic(type) || VariableTypes.isOneLevelBasic(type)
                || VariableTypes.isTwoLevelBasic(type)) {
			/*
			 * If the type of variable is basic (int, char), one dimension
			 * basic, two dimension basic, one level basic, two level basic
			 */
            correspondingNode = new AvailableTypeNode();
            ((AvailableTypeNode) correspondingNode).setName(resolvedVarNode.getNewType());
            ((AvailableTypeNode) correspondingNode).setType(type);

        } else
            do {
                List<INode> correspondingNodes = searchInSpace(spaces, new StructurevsTypedefCondition(), searchedPath);

				/*
				 * Case has a lot of Node
				 */
                if (correspondingNodes.size() == 1) {
                    // correspondingNode = this.isEqualNode(correspondingNodes,
                    // resolvedVarNode);
                    correspondingNode = correspondingNodes.get(0);

                    if (correspondingNode instanceof TypedefDeclaration) {
						/*
						 * In this case, we need resolve typedef declaration
						 */
                        type = ((TypedefDeclaration) correspondingNode).getOldType();
                        searchedPath = File.separator + type;
						/*
						 * If we dont found the variable node corresponding to
						 * the given type, we save it in case its type belongs
						 * to basic
						 */
                        if (VariableTypes.isBasic(type) || VariableTypes.isOneDimensionBasic(type)
                                || VariableTypes.isTwoDimensionBasic(type) || VariableTypes.isOneLevelBasic(type)
                                || VariableTypes.isTwoLevelBasic(type)) {
							/*
							 * If the type of variable is basic (int, char), one
							 * dimension basic, two dimension basic, one level
							 * basic, two level basic
							 */
                            correspondingNode = new AvailableTypeNode();
                            ((AvailableTypeNode) correspondingNode).setType(type);
                            break;
                        } else
                            continue;
                    }
                } else if (correspondingNode == null)
                    break;

            } while (correspondingNode instanceof TypedefDeclaration);

        return correspondingNode;
    }

    /**
     * We perform the complex search
     *
     * @param spaces
     * @param node
     * @return
     * @throws Exception
     */
    private INode performComplexSearch(List<Level> spaces, IVariableNode node) throws Exception {
        INode outputNode = null;

		/*
		 * Get the prefix of variable to start searching progress.
		 * 
		 * Ex1: "X"-------------"X"
		 *
		 * Ex2: "::X"-------------"X"
		 *
		 * Ex2: "X::Y"-------------"X"
		 */
        String firstPrefix = "";
        String type = node.getCoreType();
        if (type.startsWith("::"))
            firstPrefix = type.split("::")[1];
        else if (type.contains("::"))
            firstPrefix = type.split("::")[0];
        else
            firstPrefix = type;

        List<INode> candidateNodes = searchInSpace(spaces, new ClassvsStructvsNamespaceCondition(),
                File.separator + firstPrefix);

        if (candidateNodes.size() == 1) {
            outputNode = candidateNodes.get(0);

            if (outputNode != null && outputNode instanceof StructureNode) {
                ArrayList<ArrayList<INode>> extendPaths = ((StructureNode) outputNode).getExtendPaths();
                for (ArrayList<INode> path : extendPaths) {
                    String pathInString = getExtendPathInString(path);

                    if (type.startsWith(pathInString)) {
                        INode tmpNode = path.get(path.size() - 1);
                        String searchPath = type.replace(pathInString, "").replace("::", File.separator);
                        outputNode = Search.searchNodes(tmpNode, new NodeCondition(), searchPath).get(0);
                        return outputNode;
                    }
                }
            } else if (outputNode != null && outputNode instanceof NamespaceNode) {
                ArrayList<ArrayList<INode>> extendPaths = ((NamespaceNode) outputNode).getExtendPaths();

                for (ArrayList<INode> path : extendPaths) {
                    String pathInString = getExtendPathInString(path);

                    if (type.startsWith(pathInString)) {
                        INode tmpNode = path.get(path.size() - 1);
                        String searchPath = type.replace(pathInString, "").replace("::", File.separator);
                        outputNode = Search.searchNodes(tmpNode, new NodeCondition(), searchPath).get(0);
                        return outputNode;
                    }
                }
            }
        } else if (candidateNodes.size() > 1) {
			/*
			 * We apply many filter to get the best matching node
			 */

            // FILTER 1. If the type of the input node start with "::".In this
            // case, the scope of the given node belong to its
            // parent as source code file.
            //
            // This occur in function
            // (SYMBOLIC_EXECUTION_TEST,nsTest4\func5(::XXX))
            if (type.startsWith("::")) {
                List<INode> tmp = new ArrayList<>();
                for (INode candidateNode : candidateNodes)
                    if (candidateNode.getParent() instanceof SourcecodeFileNode)
                        tmp.add(candidateNode);

                // If only one matching node
                if (tmp.size() == 1)
                    outputNode = tmp.get(0);
                else if (tmp.size() == 0)
                    throw new Exception("Dont found the matching node with " + type);
                else {
                    logger.debug(tmp.toString());
                    throw new Exception("Detect may corresponding node of " + type);
                }

            } else {
                // FILTER 2. Get the output node based on the top-level parent
                // as namespace, structure (class, struct, etc.)
                List<INode> tmp = new ArrayList<>();

                INode topParentNodeofGivenNode = Utils.getTopLevelClassvsStructvsNamesapceNodeParent(node);
                if (topParentNodeofGivenNode != null)
                    for (INode candidateNode : candidateNodes) {
                        INode topParentNodeofCandidateNode = Utils
                                .getTopLevelClassvsStructvsNamesapceNodeParent(candidateNode);
                        if (topParentNodeofCandidateNode != null && topParentNodeofCandidateNode.getNewType()
                                .equals(topParentNodeofGivenNode.getNewType()))
                            tmp.add(candidateNode);
                    }
                else {
					/*
					 * FILTER 3. Get the nearest parent
					 * 
					 * This occur in function (TSDV_R1,
					 * Level0MultipleNsTest(X,ns1::X,ns1::ns2::X))
					 */
                    INode givenNodeParent = node.getParent();
                    if (givenNodeParent instanceof IFunctionNode)
                        givenNodeParent = givenNodeParent.getParent();

                    for (INode candidateNode : candidateNodes) {
                        INode candidateParent = candidateNode.getParent();
                        if (candidateParent != null
                                && candidateParent.getNewType().equals(givenNodeParent.getNewType()))
                            tmp.add(candidateNode);
                    }
                }
				/*
				 * FILTER 4: Closest level
				 * 
				 */
                if (tmp.size() > 1)
                    for (int i = tmp.size() - 1; i >= 0; i--) {
                        INode tmpItem = tmp.get(i);

						/*
						 * Return the class/struct/namespace/.cpp/.h/...
						 * containing the function (the input variable not is
						 * the parameter of this function)
						 */
                        INode givenNodeParent = node.getParent();
                        if (givenNodeParent instanceof IFunctionNode)
                            givenNodeParent = givenNodeParent.getParent();

                        if (!tmpItem.getParent().getAbsolutePath().equals(givenNodeParent.getAbsolutePath()))
                            tmp.remove(tmpItem);
                    }

                // If only one matching node
                if (tmp.size() == 1)
                    outputNode = tmp.get(0);
                else if (tmp.size() == 0)
                    throw new Exception("Dont found the matching node with " + type);
                else {
                    logger.debug(tmp.toString());
                    throw new Exception("Detect many corresponding node of " + type);
                }
            }
            if (outputNode == null) {
                System.out.println(candidateNodes.toString());
                throw new Exception("Detect may corresponding node of " + type);
            }
        }
        return outputNode;
    }

    private String getExtendPathInString(ArrayList<INode> path) {
        String s = "";
        for (INode child : path)
            s += child.getNewType() + "::";
        return s;
    }

    private List<INode> searchInSpace(List<Level> spaces, ISearchCondition c, String searchedPath) {
        List<INode> potentialCorrespondingNodes = new ArrayList<>();
        for (Level l : spaces) {
            for (INode n : l) {
                potentialCorrespondingNodes = Search.searchNodes(n, c, searchedPath);

                if (potentialCorrespondingNodes != null && potentialCorrespondingNodes.size() > 0)
                    break;
            }

            if (potentialCorrespondingNodes != null && potentialCorrespondingNodes.size() > 0)
                break;
        }
        return potentialCorrespondingNodes;
    }

    public INode getCorrespondingNode() {
        return correspondingNode;
    }

}
