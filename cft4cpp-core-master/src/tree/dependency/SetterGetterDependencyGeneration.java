package tree.dependency;

import java.io.File;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompoundStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTExpressionStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionCallExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDefinition;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTReturnStatement;

import config.Paths;
import parser.projectparser.ProjectParser;
import tree.object.FunctionNode;
import tree.object.INode;
import tree.object.IVariableNode;
import tree.object.Node;
import tree.object.StructureNode;
import utils.search.FunctionNodeCondition;
import utils.search.Search;
import utils.tostring.DependencyTreeDisplayer;

public class SetterGetterDependencyGeneration {

    public SetterGetterDependencyGeneration(INode var) {
        if (var.getParent() instanceof StructureNode && ((IVariableNode) var).isPrivate()) {
            INode structureNode = var.getParent();
            List<INode> functionNodes = Search.searchNodes(structureNode, new FunctionNodeCondition());
            for (INode item : functionNodes)
                if (item instanceof FunctionNode) {
                    FunctionNode functionNode = (FunctionNode) item;
                    IASTFunctionDefinition ast = functionNode.getAST();

                    ASTVisitor visitor = new ASTVisitor() {
                        @Override
                        public int visit(IASTExpression expression) {
                            if (expression.getParent() != null && expression.getParent().getParent() != null
                                    && expression.getParent().getParent().getParent() != null
                                    && expression.getParent() instanceof CPPASTExpressionStatement
                                    && expression.getParent().getParent() instanceof ICPPASTCompoundStatement)
                                if (expression instanceof ICPPASTBinaryExpression) {

                                    ICPPASTBinaryExpression assignment = (ICPPASTBinaryExpression) expression;
                                    IASTExpression left = assignment.getOperand1();

                                    String nameLeftVar = left.getRawSignature().replace("this->", "");
                                    if (nameLeftVar.equals(var.getNewType())) {
                                        SetterDependency d = new SetterDependency(var, item);
                                        var.getDependencies().add(d);
                                        item.getDependencies().add(d);
                                        return ASTVisitor.PROCESS_ABORT;
                                    }

                                } else if (expression instanceof CPPASTFunctionCallExpression)
                                    if (expression.getParent().getParent()
                                            .getParent() instanceof CPPASTFunctionDefinition) {

                                        IASTNode[] children = expression.getChildren();
                                        String nameCalledFunction = children[0].getRawSignature();
                                        if (nameCalledFunction.equals("strcpy")
                                                || nameCalledFunction.equals("strcpy_s")) {
                                            String des = children[1].getRawSignature();

                                            if (des.equals(var.getNewType())) {
                                                SetterDependency d = new SetterDependency(var, item);
                                                var.getDependencies().add(d);
                                                item.getDependencies().add(d);
                                                return ASTVisitor.PROCESS_ABORT;
                                            }
                                        }
                                    }
                            return ASTVisitor.PROCESS_CONTINUE;
                        }

                        @Override
                        public int visit(IASTStatement expression) {

                            if (expression instanceof IASTReturnStatement
                                    || expression instanceof CPPASTReturnStatement) {

                                IASTReturnStatement returnStm = (IASTReturnStatement) expression;
                                String returnNameVar = returnStm.getReturnValue().getRawSignature();

                                if (returnNameVar.equals(var.getNewType())) {
                                    GetterDependency d = new GetterDependency(var, item);
                                    var.getDependencies().add(d);
                                    item.getDependencies().add(d);
                                    return ASTVisitor.PROCESS_ABORT;
                                }
                            }
                            return ASTVisitor.PROCESS_CONTINUE;
                        }

                    };
                    visitor.shouldVisitStatements = true;
                    visitor.shouldVisitExpressions = true;
                    ast.accept(visitor);
                }
        }
    }

    public static void main(String[] args) {
        ProjectParser parser = new ProjectParser(new File(Paths.TSDV_R1), null);
        Node root = (Node) parser.getRootTree();

        System.out.println( new DependencyTreeDisplayer(root).getTreeInString());

    }
}
