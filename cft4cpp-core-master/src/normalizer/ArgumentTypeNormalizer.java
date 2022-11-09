package normalizer;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import config.Paths;
import parser.projectparser.ProjectParser;
import tree.object.IFunctionNode;
import tree.object.IVariableNode;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

/**
 * Rewrite arguments in the given function. Ex:
 * "Level0MultipleNsTest(X,ns1::X,ns1::ns2::X)"
 * ----------->"Level0MultipleNsTest(::X,ns1::X,ns1::ns2::X)"
 *
 * @author ducanhnguyen
 */
public class ArgumentTypeNormalizer extends AbstractFunctionNormalizer
        implements IFunctionNormalizer {
    final static Logger logger = Logger.getLogger(ArgumentTypeNormalizer.class);

    public static void main(String[] args) {
        ProjectParser parser = new ProjectParser(new File(Paths.CORE_UTILS));
        parser.getIgnoreFolders()
                .add(new File(
                        "/home/ducanhnguyen/Desktop/ava/data-test/ducanh/coreutils-8.24/gnulib-tests"));
        IFunctionNode function = (IFunctionNode) Search.searchNodes(
                parser.getRootTree(), new FunctionNodeCondition(),
                "who.c" + File.separator + "main(int,char**)").get(0);

        System.out.println(function.getAST().getRawSignature());
        ArgumentTypeNormalizer normalizer = new ArgumentTypeNormalizer();
        normalizer.setFunctionNode(function);
        normalizer.normalize();

        System.out.println(normalizer.getTokens());
        System.out.println(normalizer.getNormalizedSourcecode());
    }

    @Override
    public void normalize() {
//		List<IVariableNode> variableNodes = getFunctionNode().getArguments();
//
//		if (variableNodes.size() > 0) {
//			String newVarDeclarations = "";
//			String originalDeclaration = getFunctionNode().getDeclaration();
//			for (IVariableNode variableNode : variableNodes)
//				if (VariableTypes.isStructureOneDimension(variableNode
//						.getRawType())
//						|| VariableTypes.isStructureTwoDimension(variableNode
//								.getRawType())
//						|| VariableTypes.isStructureOneLevel(variableNode
//								.getRawType())
//						|| VariableTypes.isStructureTwoLevel(variableNode
//								.getRawType())) {
//					/*
//					 * Ex: "int PassAsConstRefTest(const BigData& b)" ------->
//					 * "int PassAsConstRefTest(const ::BigData& b)"
//					 */
//					String fullCoreType = variableNode.getFullType();
//					String newType = variableNode.getRawType().replace(
//							variableNode.getCoreType(), fullCoreType);
//					String newVar = "";
//
//					if (newType.indexOf("[") > 0)
//						newVar = newType.substring(0, newType.indexOf("["))
//								+ " " + variableNode.getNewType()
//								+ newType.substring(newType.indexOf("["));
//					else
//						newVar = newType + " " + variableNode.getNewType();
//
//					newVarDeclarations += newVar + ",";
//				} else
//					newVarDeclarations += ((VariableNode) variableNode)
//							.getFullType()
//							+ " "
//							+ ((VariableNode) variableNode).getName() + ",";
//
//			String newFunctionDeclaration = getFunctionNode().getSimpleName()
//					+ "(" + newVarDeclarations + ")";
//			// merge all
//			newFunctionDeclaration = newFunctionDeclaration.replace(",)", ")");
//			normalizeSourcecode = getFunctionNode().getAST().getRawSignature()
//					.replace(originalDeclaration, newFunctionDeclaration);
//		} else
//			normalizeSourcecode = getFunctionNode().getAST().getRawSignature();

        List<IVariableNode> variableNodes = this.getFunctionNode().getArguments();

        if (variableNodes.size() > 0) {
            String newVarDeclarations = "";
            String originalDeclaration = this.getFunctionNode().getDeclaration();
            for (IVariableNode variableNode : variableNodes) {
                /*
				 * Ex: "int PassAsConstRefTest(const BigData& b)" ------->
				 * "int PassAsConstRefTest(const ::BigData& b)"
				 */
                String fullCoreType = variableNode.getFullType();
                String newType = variableNode.getRawType().replace(variableNode.getCoreType(), fullCoreType);
                String newVar = "";

                if (newType.indexOf("[") > 0)
                    newVar = newType.substring(0, newType.indexOf("[")) + " " + variableNode.getName()
                            + newType.substring(newType.indexOf("["));
                else
                    newVar = newType + " " + variableNode.getName();

                newVarDeclarations += newVar + ",";
            }
            String newFunctionDeclaration = this.getFunctionNode().getSimpleName() + "(" + newVarDeclarations + ")";
            // merge all
            newFunctionDeclaration = newFunctionDeclaration.replace(",)", ")");
            this.normalizeSourcecode = this.getFunctionNode().getAST().getRawSignature().replace(originalDeclaration,
                    newFunctionDeclaration);
        } else
            this.normalizeSourcecode = this.getFunctionNode().getAST().getRawSignature();


    }
}
