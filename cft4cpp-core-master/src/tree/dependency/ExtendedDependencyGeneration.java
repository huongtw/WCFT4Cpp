package tree.dependency;

import java.io.File;
import java.util.List;

import config.Paths;
import parser.projectparser.ProjectParser;
import tree.object.AvailableTypeNode;
import tree.object.INode;
import tree.object.StructureNode;
import tree.object.VariableNode;
import utils.search.ClassvsStructvsNamespaceCondition;
import utils.search.Search;
import utils.tostring.ReducedDependencyTreeDisplayer;
import utils.tostring.ToString;

public class ExtendedDependencyGeneration {

    public ExtendedDependencyGeneration(INode root) {
        dependencyGeneration(root);
    }

    public static void main(String[] args) {
        ProjectParser parser = new ProjectParser(new File(Paths.TSDV_R1), null);
        new ExtendedDependencyGeneration(parser.getRootTree());

        ToString treeDisplayer = new ReducedDependencyTreeDisplayer(parser.getRootTree());
        System.out.println(treeDisplayer.getTreeInString());
    }

    public void dependencyGeneration(INode root) {
        List<INode> nodes = Search.searchNodes(root, new ClassvsStructvsNamespaceCondition());
        for (INode n : nodes)
            if (n instanceof StructureNode) {
                List<String> extendClassNames = ((StructureNode) n).getExtendedNames();
                for (String extendClassName : extendClassNames) {
                    /*
					 * Create temporary variable
					 */
                    VariableNode v = new VariableNode();
                    v.setCoreType(extendClassName);
                    v.setRawType(extendClassName);
                    v.setParent(n);
					/*
					 * Find type of temporary variable
					 */
                    TypeDependencyGeneration typeGen;
                    try {
                        typeGen = new TypeDependencyGeneration(v);
                        INode correspondingNode = typeGen.getCorrespondingNode();
                        if (correspondingNode != null && !(correspondingNode instanceof AvailableTypeNode)) {
                            INode refferedNode = correspondingNode;

                            ExtendDependency d = new ExtendDependency(n, refferedNode);
                            n.getDependencies().add(d);
                            refferedNode.getDependencies().add(d);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
    }
}
