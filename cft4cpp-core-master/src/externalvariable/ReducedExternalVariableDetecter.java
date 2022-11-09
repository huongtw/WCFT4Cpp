package externalvariable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTArraySubscriptExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTDeclarationStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTDoStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFieldReference;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTForStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIdExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTLiteralExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTPointer;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTQualifiedName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTryBlockStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTWhileStatement;

import parser.projectparser.ProjectParser;
import tree.object.IFunctionNode;
import tree.object.INode;
import tree.object.IVariableNode;
import tree.object.Node;
import tree.object.VariableNode;
import utils.Utils;
import utils.search.FunctionNodeCondition;
import utils.search.Search;
import utils.search.VariableNodeCondition;

/**
 * Find all external variable of a function
 * <p>
 * Remain: not detected variable through setter and getter yet
 *
 * @author hungpq
 */
public class ReducedExternalVariableDetecter implements IExternalVariableDetecter {

    protected IFunctionNode function;

    private List<String> tempExternalVariables = new ArrayList<>();

    private List<String> tempRelativePathes = new ArrayList<>();

    private List<String> relativePathes = new ArrayList<>();

    private List<String> declarationVariables = new ArrayList<>();

    private List<Integer> varDeclarLevels = new ArrayList<>();

    private int currentLevel = 1;

    public ReducedExternalVariableDetecter(IFunctionNode function) {
        this.function = function;
    }

    public static void main(String[] args) {
        ProjectParser parser = new ProjectParser(new File("..\\ava\\data-test\\ducanh\\ExternalVariableDetecterTest"));

        IFunctionNode function = (IFunctionNode) Search
                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "C::test(A*,int)").get(0);

        System.out.println(function.getAST().getRawSignature());

        for (INode n : function.getReducedExternalVariables())
            System.out.println(n.getAbsolutePath());
    }

    @Override
    public List<IVariableNode> findExternalVariables() {
        List<IVariableNode> reducedExternalVariableNodes = new ArrayList<>();

        INode realParent = function.getRealParent();

        for (int i = 0; i < function.getArguments().size(); i++) {
            declarationVariables.add(function.getArguments().get(i).getNewType());
            varDeclarLevels.add(currentLevel);
        }

        parse();

        for (String tempVariable : tempExternalVariables) {
            if (Search.searchNodes(realParent, new VariableNodeCondition(), tempVariable).size() != 0) {
                VariableNode tempVariableNode = (VariableNode) Search
                        .searchNodes(realParent, new VariableNodeCondition(), tempVariable).get(0);
                reducedExternalVariableNodes.add(tempVariableNode);
                continue;
            }
            INode parent = function.getParent();
            while (parent != null && parent instanceof Node) {
                if (Search.searchNodes(parent, new VariableNodeCondition(), tempVariable).size() != 0) {
                    VariableNode tempVariableNode = (VariableNode) Search
                            .searchNodes(parent, new VariableNodeCondition(), tempVariable).get(0);
                    reducedExternalVariableNodes.add(tempVariableNode);
                    break;
                }
                parent = parent.getParent();
            }
        }

        /**
         * reduce external variable path
         */
        for (String relativePath : tempRelativePathes) {
            String temp[] = Utils.convertToArray(Utils.split(relativePath, File.separator));
            if (tempExternalVariables.size() == 0)
                findRelativePathes(temp);

            for (int i = 0; i < tempExternalVariables.size(); i++) {
                String tempVar = tempExternalVariables.get(i);
                if (temp[0].equals(tempVar))
                    break;
                if (i == tempExternalVariables.size() - 1)
                    findRelativePathes(temp);
            }
        }

        for (String relativePath : relativePathes) {
            INode parent = function.getParent();

            while (parent != null && parent instanceof Node) {
                if (Search.searchNodes(parent, new VariableNodeCondition(), relativePath).size() != 0) {
                    VariableNode tempVariableNode = (VariableNode) Search
                            .searchNodes(parent, new VariableNodeCondition(), relativePath).get(0);
                    reducedExternalVariableNodes.add(tempVariableNode);
                    break;
                }
                parent = parent.getParent();
            }
        }

        return reducedExternalVariableNodes;
    }

    /**
     * Find perfect variable path
     *
     * @param tempPath
     */
    private void findRelativePathes(String tempPath[]) {
        if (tempPath.length != 2)
            tempPath[0] = tempPath[0] + File.separator + tempPath[1];

        INode realParent = function.getRealParent();
        if (Search.searchNodes(realParent, new VariableNodeCondition(), tempPath[0]).size() != 0) {
            IVariableNode tempVariableNode = (IVariableNode) Search
                    .searchNodes(realParent, new VariableNodeCondition(), tempPath[0]).get(0);
            String path = tempVariableNode.getCoreType() + File.separator + tempPath[tempPath.length - 1];
            if (!isIn(path))
                relativePathes.add(path);

        } else {
            INode parent = function.getParent();
            while (parent != null && parent instanceof Node) {
                if (Search.searchNodes(parent, new VariableNodeCondition(), tempPath[0]).size() != 0) {
                    IVariableNode tempVariableNode = (IVariableNode) Search
                            .searchNodes(parent, new VariableNodeCondition(), tempPath[0]).get(0);

                    String path = tempVariableNode.getCoreType() + File.separator + tempPath[tempPath.length - 1];
                    if (!isIn(path))
                        relativePathes.add(path);
                    break;
                }
                parent = parent.getParent();
            }
        }

    }

    private boolean isIn(String n) {
        if (n.equals("NULL") || n.equals("null"))
            return true;

        for (int i = 0; i < tempExternalVariables.size(); i++)
            if (tempExternalVariables.get(i).equals(n))
                return true;

        for (String variablePath : tempRelativePathes)
            if (variablePath.equals(n))
                return true;

        for (String variablePath : relativePathes)
            if (variablePath.equals(n))
                return true;

        return false;
    }

    private void parse() {
        /*
		 * Transform function with getter and setter
		 */
        String newFunctionCode = function.performSettervsGetterTransformer().getNormalizedSourcecode();

		/*
		 * Get ast from string function
		 */
        IASTFunctionDefinition ast = Utils.getFunctionsinAST(newFunctionCode.toCharArray()).get(0);

        ASTVisitor visitor = new ASTVisitor() {

            @Override
            public int leave(IASTStatement statement) {
                if (statement instanceof CPPASTForStatement || statement instanceof CPPASTDoStatement
                        || statement instanceof CPPASTWhileStatement) {
                    int i = varDeclarLevels.size() - 1;
                    if (i < 0) {
                        currentLevel--;
                        return ASTVisitor.PROCESS_CONTINUE;
                    }
                    while (varDeclarLevels.get(i) == currentLevel) {
                        declarationVariables.remove(i);
                        varDeclarLevels.remove(i);
                        i--;
                        if (i < 0)
                            break;
                    }
                    currentLevel--;
                }
                return ASTVisitor.PROCESS_CONTINUE;
            }

            @Override
            public int visit(IASTExpression statement) {
                if (statement instanceof CPPASTIdExpression && !(statement.getParent() instanceof CPPASTFieldReference)
                        && !(statement.getParent() instanceof CPPASTArraySubscriptExpression
                        && statement.getParent().getParent() instanceof CPPASTFieldReference))
                    if (statement.getChildren()[0] instanceof CPPASTQualifiedName)
						/*
						 * case A::x
						 */

                        if (statement.getChildren()[0].getChildren().length == 2) {
                            String path = statement.getChildren()[0].getChildren()[0].getRawSignature() + File.separator
                                    + statement.getChildren()[0].getChildren()[1].getRawSignature();
                            if (!ReducedExternalVariableDetecter.this.isIn(path))
                                relativePathes.add(path);
                        }
						/*
						 * case A::B::C::x
						 */
                        else {
                            String path = new String("");
                            for (int i = 0; i < statement.getChildren()[0].getChildren().length; i++) {
                                if (i == statement.getChildren()[0].getChildren().length - 1) {
                                    path += statement.getChildren()[0].getChildren()[i].getRawSignature();
                                    break;
                                }
                                path += statement.getChildren()[0].getChildren()[i].getRawSignature() + File.separator;
                            }
                            if (!ReducedExternalVariableDetecter.this.isIn(path))
                                relativePathes.add(path);

                        }
                    else {
                        if (declarationVariables.size() == 0)
                            if (!ReducedExternalVariableDetecter.this.isIn(statement.getRawSignature()))
                                tempExternalVariables.add(statement.getRawSignature());
                        for (int i = 0; i < declarationVariables.size(); i++) {
                            if (statement.getChildren()[0].getRawSignature().equals(declarationVariables.get(i)))
                                break;
                            if (i == declarationVariables.size() - 1)
                                if (!ReducedExternalVariableDetecter.this.isIn(statement.getRawSignature()))
                                    tempExternalVariables.add(statement.getRawSignature());
                        }
                    }

                /**
                 * <pre>
                 * case:	 classB.classA.classC.f
                 * case:	 A::staticNode->P->intnode++
                 * case:	 sv.other[0].other[0].eee
                 * </pre>
                 */
                if (statement instanceof CPPASTFieldReference
                        && !(statement.getParent() instanceof CPPASTFieldReference)
                        && !(statement.getParent() instanceof CPPASTArraySubscriptExpression
                        && statement.getParent().getParent() instanceof CPPASTFieldReference)) {

                    // f
                    String astName = statement.getChildren()[1].getRawSignature();
                    // classC de tim variableNode f
                    String astPath = statement.getChildren()[0].getRawSignature();
                    // classB de xet xem no duoc khai bao chua
                    String astDeclair = statement.getChildren()[0].getRawSignature();

                    /**
                     * <pre>
                     * case	 other[0].eeee
                     * case	 sv.other[0].eeee
                     * </pre>
                     */
                    if (statement.getChildren()[0] instanceof CPPASTArraySubscriptExpression) {
                        astPath = statement.getChildren()[0].getChildren()[0].getRawSignature();
                        astDeclair = astPath;
                        if (statement.getChildren()[0].getChildren()[0] instanceof CPPASTFieldReference)
                            astPath = statement.getChildren()[0].getChildren()[0].getChildren()[1].getRawSignature();
                    }

                    if (statement.getChildren()[0] instanceof CPPASTFieldReference)
                        astPath = statement.getChildren()[0].getChildren()[1].getRawSignature();

					/*
					 * case this->f
					 */
                    if (statement.getChildren()[0] instanceof CPPASTLiteralExpression)
                        astPath = "";

					/*
					 * case A::staticNode->N
					 */
                    if (statement.getChildren()[0] instanceof CPPASTIdExpression
                            && statement.getChildren()[0].getChildren()[0] instanceof CPPASTQualifiedName)
                        astPath = statement.getChildren()[0].getChildren()[0].getChildren()[0].getRawSignature()
                                + File.separator
                                + statement.getChildren()[0].getChildren()[0].getChildren()[1].getRawSignature();

                    while (statement.getRawSignature().contains(".") || statement.getRawSignature().contains("[")) {
                        statement = (IASTExpression) statement.getChildren()[0];
                        if (!statement.getRawSignature().contains(".") && !statement.getRawSignature().contains("["))
                            astDeclair = statement.getChildren()[0].getRawSignature();
                    }

                    if (declarationVariables.size() == 0)
                    /**
                     * Tim relative path cho kieu bien
                     *
                     * <pre>
                     * case		sv.o case
                     * case		classB.classA.f
                     * case		classB->classA->f
                     * </pre>
                     */

                        if (astPath.equals("")) {
                            if (!ReducedExternalVariableDetecter.this.isIn(astName))
                                tempExternalVariables.add(astName);
                        } else {
                            String path = astPath + File.separator + astName;
                            if (!ReducedExternalVariableDetecter.this.isIn(path))
                                tempRelativePathes.add(path);
                        }

                    for (int i = 0; i < declarationVariables.size(); i++) {
                        if (astDeclair.equals(declarationVariables.get(i)))
                            break;

                        if (i == declarationVariables.size() - 1)
                        /**
                         * Tim relative path cho kieu bien
                         *
                         * <pre>
                         * case		sv.o case
                         * case		classB.classA.f
                         * case		classB->classA->f
                         * </pre>
                         */

                            if (astPath.equals("")) {
                                if (!ReducedExternalVariableDetecter.this.isIn(astName))
                                    tempExternalVariables.add(astName);
                            } else {
                                String path = astPath + File.separator + astName;
                                if (!ReducedExternalVariableDetecter.this.isIn(path))
                                    tempRelativePathes.add(path);
                            }
                    }

                }

                return ASTVisitor.PROCESS_CONTINUE;
            }

            @Override
            public int visit(IASTStatement statement) {
                if (statement instanceof CPPASTForStatement || statement instanceof CPPASTDoStatement
                        || statement instanceof CPPASTWhileStatement || statement instanceof CPPASTTryBlockStatement)
                    currentLevel++;
                if (statement instanceof CPPASTDeclarationStatement)
                /**
                 * <pre>
                 * case		Node *temp
                 * case		int asd, sum;
                 * </pre>
                 */

                    for (int i = 1; i < statement.getChildren()[0].getChildren().length; i++)
                        if (statement.getChildren()[0].getChildren()[i].getChildren()[0] instanceof CPPASTName) {
                            declarationVariables.add(
                                    statement.getChildren()[0].getChildren()[i].getChildren()[0].getRawSignature());
                            varDeclarLevels.add(currentLevel);
                        } else if (statement.getChildren()[0].getChildren()[i]
                                .getChildren()[0] instanceof CPPASTPointer) {
                            declarationVariables.add(
                                    statement.getChildren()[0].getChildren()[i].getChildren()[1].getRawSignature());
                            varDeclarLevels.add(currentLevel);
                        }
                return ASTVisitor.PROCESS_CONTINUE;
            }
        };

        visitor.shouldVisitStatements = true;
        visitor.shouldVisitDeclarations = true;
        visitor.shouldVisitExpressions = true;
        visitor.shouldVisitNames = true;

        ast.accept(visitor);

    }

    @Override
    public IFunctionNode getFunction() {
        return function;
    }

    @Override
    public void setFunction(IFunctionNode function) {
        this.function = function;
    }

}
