package tree.object;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;

import config.Paths;
import parser.projectparser.ProjectParser;
import utils.Utils;
import utils.search.DefinitionFunctionNodeCondition;
import utils.search.Search;

/**
 * Represent method are only declared but not defined in this function
 *
 * @author DucAnh
 */
public class DefinitionFunctionNode extends CustomASTNode<CPPASTSimpleDeclaration> {

    public static void main(String[] args) {
        ProjectParser parser = new ProjectParser(new File(Paths.BTL), null);
        Search.searchNodes(parser.getRootTree(), new DefinitionFunctionNodeCondition(), "nhap_qua_File(ifstream&)")
                .get(0);

    }

    public List<VariableNode> getArguments() {
        List<VariableNode> arguments = new ArrayList<>();

        for (IASTNode child : getAST().getDeclarators()[0].getChildren())
            if (child instanceof IASTParameterDeclaration) {
                IASTParameterDeclaration astArgument = (IASTParameterDeclaration) child;

                VariableNode argumentNode = new VariableNode();
                argumentNode.setAST(astArgument);
                argumentNode.setParent(this);
                argumentNode.setAbsolutePath(getAbsolutePath() + File.separator + argumentNode.getNewType());
                getChildren().add(argumentNode);

                arguments.add(argumentNode);
            }
        return arguments;
    }

    @Override
    public String getNewType() {
        String output = getAST().getDeclarators()[0].getName().getRawSignature();
        output += "(";
        for (IVariableNode paramater : getArguments())
            output += paramater.getRawType() + ",";
        output += ")";
        output = output.replace(",)", ")").replaceAll("\\s*\\)", "\\)");
        return output;
    }

    public String getReturnType() {
        String returnType = getAST().getDeclSpecifier().getRawSignature();
        if (getAST().getDeclarators()[0].getRawSignature().startsWith("*"))
            returnType += "*";
        return returnType;
    }

    public String getSimpleName() {
        return Utils.getCPPASTNames(getAST()).get(0).getRawSignature();
    }

    @Override
    public void setAST(CPPASTSimpleDeclaration aST) {
        super.setAST(aST);
    }
}
