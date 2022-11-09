package normalizer;

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
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionCallExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIdExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTPointer;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTQualifiedName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTryBlockStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTWhileStatement;

import config.Paths;
import parser.projectparser.ProjectParser;
import testdatagen.testdatainit.VariableTypes;
import tree.object.ClassNode;
import tree.object.IFunctionNode;
import tree.object.INode;
import tree.object.IVariableNode;
import tree.object.StructureNode;
import utils.search.ClassNodeCondition;
import utils.search.FunctionNodeCondition;
import utils.search.Search;
import utils.search.StructNodeCondition;
import utils.search.VariableNodeCondition;

/**
 * Transform getter and setter in function to access direct variable
 * <p>
 * Remain: - not support for setter with 2 or more arguments
 *
 * @author phamh_000
 */
public class SetterandGetterFunctionNormalizer extends AbstractFunctionNormalizer implements IFunctionNormalizer {
    /**
     * Array store statements need changing getter and setter
     */
    private ArrayList<MappingVariable> mappingVariables = new ArrayList<>();

    /**
     * Array store declaration variable in function to access its type
     * <p>
     * <p>
     * <pre>
     *  Eg: SinhVien x;
     * 		oldVar: x
     * 		newVar: SinhVien
     * </pre>
     */
    private List<MappingVariable> declarationVariables = new ArrayList<>();

    private int currentLevel = 1;

    public static void main(String[] args) {
        ProjectParser parser = new ProjectParser(new File(Paths.DATA_GEN_TEST));

        IFunctionNode function = (IFunctionNode) Search
                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "main()").get(0);
        SetterandGetterFunctionNormalizer norm = new SetterandGetterFunctionNormalizer();
        norm.setFunctionNode(function);
        norm.normalize();
    }

    @Override
    public void normalize() {
        IASTFunctionDefinition ast = functionNode.getAST();

        ASTVisitor visitor = new ASTVisitor() {
            @Override
            public int leave(IASTStatement statement) {
                /*
				 * Reduce level when leave for, while, ...
				 */
                if (statement instanceof CPPASTForStatement || statement instanceof CPPASTDoStatement
                        || statement instanceof CPPASTWhileStatement || statement instanceof CPPASTTryBlockStatement) {
                    int i = declarationVariables.size() - 1;
                    if (i < 0) {
                        currentLevel--;
                        return ASTVisitor.PROCESS_CONTINUE;
                    }
                    while (declarationVariables.get(i).getLevel() == currentLevel) {
                        declarationVariables.remove(i);
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

				/*
				 * Catch statement getter | setter
				 */
                if (statement instanceof CPPASTFunctionCallExpression
                        && statement.getChildren()[0] instanceof CPPASTFieldReference) {

                    String stringStatement = statement.getRawSignature();
                    String newStatement = "";

					/*
					 * Need try catch to avoid not searching variable of getter
					 * and setter
					 */
                    try {

						/*
						 * Find classNode of variable having getter | setter
						 */
                        StructureNode classKind = SetterandGetterFunctionNormalizer.this
                                .findStructureKind((IASTExpression) statement.getChildren()[0]);

                        String stringFunctOld = statement.getChildren()[0].getChildren()[1].getRawSignature();
                        IVariableNode variableNameNew = null;

                        if (stringStatement.contains("set")) {
                            if (statement.getChildren().length == 2) {

                                variableNameNew = classKind.findFunctionsBySimpleName(stringFunctOld).get(0)
                                        .getCorrespondingVariable();
                                newStatement = stringStatement.replaceAll(stringFunctOld + "(.*)",
                                        variableNameNew.getNewType() + "=" + statement.getChildren()[1].getRawSignature());

                            } else {

                            }
                        } else if (stringStatement.contains("get")) {

                            variableNameNew = classKind.findFunctionsBySimpleName(stringFunctOld).get(0)
                                    .getCorrespondingVariable();

							/*
							 * regex to replace last part of statement Ex:
							 * sv[0].getSv_()->getSv_()->setAge_(1);
							 */
                            newStatement = stringStatement.replaceFirst("(?s)(.*)" + stringFunctOld + "\\(\\)",
                                    "$1" + variableNameNew.getNewType());

							/*
							 * if get return a pointer and don't have index so
							 * it reference to index 0
							 */
                            if (variableNameNew != null)
                                if (!(statement.getParent() instanceof CPPASTArraySubscriptExpression))
                                    if (VariableTypes.isOneLevel(variableNameNew.getRawType()))
                                        newStatement += "[0]";
                        }
                    } catch (NullPointerException | IndexOutOfBoundsException e) {

                        newStatement = stringStatement;
                    }

                    int nodeOffset = statement.getFileLocation().getNodeOffset();
                    mappingVariables.add(new MappingVariable(stringStatement, newStatement, nodeOffset, 1));
                }

                return ASTVisitor.PROCESS_CONTINUE;
            }

            @Override
            public int visit(IASTStatement statement) {
				/*
				 * Catch declaration statement
				 */
                if (statement instanceof CPPASTDeclarationStatement)
                /**
                 * <pre>
                 * case Node *temp
                 * case int asd, sum;
                 * </pre>
                 */
                    for (int i = 1; i < statement.getChildren()[0].getChildren().length; i++)
                        if (statement.getChildren()[0].getChildren()[i].getChildren()[0] instanceof CPPASTName)
                            declarationVariables.add(new MappingVariable(
                                    statement.getChildren()[0].getChildren()[i].getChildren()[0].getRawSignature(),
                                    statement.getChildren()[0].getChildren()[0].getRawSignature(), currentLevel));
                        else if (statement.getChildren()[0].getChildren()[i].getChildren()[0] instanceof CPPASTPointer)
                            declarationVariables.add(new MappingVariable(
                                    statement.getChildren()[0].getChildren()[i].getChildren()[1].getRawSignature(),
                                    statement.getChildren()[0].getChildren()[0].getRawSignature(), currentLevel));

                return ASTVisitor.PROCESS_CONTINUE;
            }

        };

        visitor.shouldVisitStatements = true;
        visitor.shouldVisitDeclarations = true;
        visitor.shouldVisitExpressions = true;

        ast.accept(visitor);

        megaTranform();
    }

    private StructureNode findClassNode(String path) {
        INode realParent = functionNode.getRealParent();
		/*
		 * return StructureNode
		 */
        if (Search.searchNodes(realParent, new StructNodeCondition(), path).size() != 0) {
            StructureNode tempClassNode = (StructureNode) Search
                    .searchNodes(realParent, new StructNodeCondition(), path).get(0);
            return tempClassNode;
        } else {
            INode parent = functionNode.getParent();
            while (parent != null && parent instanceof INode) {
                if (Search.searchNodes(parent, new StructNodeCondition(), path).size() != 0) {
                    StructureNode tempClassNode = (StructureNode) Search
                            .searchNodes(parent, new StructNodeCondition(), path).get(0);
                    return tempClassNode;
                }
                parent = parent.getParent();
            }
        }

		/*
		 * return ClassNode
		 */
        if (Search.searchNodes(realParent, new ClassNodeCondition(), path).size() != 0) {
            ClassNode tempClassNode = (ClassNode) Search.searchNodes(realParent, new ClassNodeCondition(), path).get(0);
            return tempClassNode;
        } else {
            INode parent = functionNode.getParent();
            while (parent != null && parent instanceof INode) {
                if (Search.searchNodes(parent, new ClassNodeCondition(), path).size() != 0) {
                    ClassNode tempClassNode = (ClassNode) Search.searchNodes(parent, new ClassNodeCondition(), path)
                            .get(0);
                    return tempClassNode;
                }
                parent = parent.getParent();
            }
        }

        return null;
    }

    /**
     * Find external variable node of the variable named astPath
     *
     * @param astPath
     * @return
     */
    private IVariableNode findExternalVarNode(String astPath) {
        INode realParent = functionNode.getRealParent();
        if (Search.searchNodes(realParent, new VariableNodeCondition(), astPath).size() != 0) {
            IVariableNode tempVariableNode = (IVariableNode) Search
                    .searchNodes(realParent, new VariableNodeCondition(), astPath).get(0);
            return tempVariableNode;
        } else {
            INode parent = functionNode.getParent();
            while (parent != null && parent instanceof INode) {
                if (Search.searchNodes(parent, new VariableNodeCondition(), astPath).size() != 0) {
                    IVariableNode tempVariableNode = (IVariableNode) Search
                            .searchNodes(parent, new VariableNodeCondition(), astPath).get(0);
                    return tempVariableNode;
                }
                parent = parent.getParent();
            }
        }
        return null;
    }

    /**
     * Find class node of the variable returned by getter
     *
     * @param statement
     * @return
     */
    private StructureNode findStructureGetterKind(IASTExpression statement) {
        /**
         * find classNode of variable having getter | setter
         */

        StructureNode classKind = findStructureKind((IASTExpression) statement.getChildren()[0]);

        String stringFunctOld = statement.getChildren()[0].getChildren()[1].getRawSignature();
        IVariableNode variableNameNew = null;

        variableNameNew = classKind.findFunctionsBySimpleName(stringFunctOld).get(0).getCorrespondingVariable();

        return findClassNode(variableNameNew.getCoreType());

    }

    /**
     * Find class node of variable has getter | setter
     *
     * @param statement
     * @return
     */
    private StructureNode findStructureKind(IASTExpression statement) {
        if (statement.getChildren()[0] instanceof CPPASTFunctionCallExpression
                && statement.getChildren()[0].getChildren()[0] instanceof CPPASTFieldReference)
            return findStructureGetterKind((IASTExpression) statement.getChildren()[0]);
        else if (statement.getChildren()[0].getChildren()[0] instanceof CPPASTFunctionCallExpression
                && statement.getChildren()[0].getChildren()[0].getChildren()[0] instanceof CPPASTFieldReference)
            return findStructureGetterKind((IASTExpression) statement.getChildren()[0].getChildren()[0]);

        /**
         * <pre>
         *	case: 	sv.other[0].eeee
         *  astPath = other
         *  astName = sv
         * </pre>
         */
        String astPath = statement.getChildren()[0].getRawSignature();
        String astName = statement.getChildren()[0].getRawSignature();

		/*
		 * case other[0].eeee case sv.other[0].eeee
		 */
        if (statement.getChildren()[0] instanceof CPPASTArraySubscriptExpression) {
            astPath = statement.getChildren()[0].getChildren()[0].getRawSignature();
            astName = astPath;
            if (statement.getChildren()[0].getChildren()[0] instanceof CPPASTFieldReference)
                astPath = statement.getChildren()[0].getChildren()[0].getChildren()[1].getRawSignature();
        }

        if (statement.getChildren()[0] instanceof CPPASTFieldReference)
            astPath = statement.getChildren()[0].getChildren()[1].getRawSignature();

        /**
         * Find type of class in declar variable
         *
         * <pre>
         * case 	other[0].getAge();
         * case		sv.getAge();
         * </pre>
         */
        if (astName.equals(astPath))
            for (int i = declarationVariables.size() - 1; i >= 0; i--) {
                MappingVariable mappingVariable = declarationVariables.get(i);
                if (mappingVariable.isEqual(astName))
                    return findClassNode(mappingVariable.getNewVariable());
            }

		/*
		 * case A::staticNode->N
		 */
        if (statement.getChildren()[0] instanceof CPPASTIdExpression
                && statement.getChildren()[0].getChildren()[0] instanceof CPPASTQualifiedName)
            astPath = statement.getChildren()[0].getChildren()[0].getChildren()[0].getRawSignature() + File.separator
                    + statement.getChildren()[0].getChildren()[0].getChildren()[1].getRawSignature();

        IVariableNode externalVar = findExternalVarNode(astPath);
        if (externalVar != null)
            return findClassNode(externalVar.getCoreType());
        else
            return null;
    }

    /**
     * Replace mapping variable to function
     */
    private void megaTranform() {
        String oldFunction = functionNode.getAST().getRawSignature();

		/*
		 * functionOffset : offset of function adjustment : the length function
		 * changed by replacing getter|setter
		 */
        int functionOffset = functionNode.getAST().getFileLocation().getNodeOffset();
        int adjustment = 0;
        int tempAdjustment = 0;

		/*
		 * Notice: mapping statement with the same offset -> adjustment changed
		 * but still not affect to the offset of statement Ex :
		 * sv[0].getTT_().getQwe_()
		 */
        for (int i = 0; i < mappingVariables.size(); i++) {
            MappingVariable variableToMap = mappingVariables.get(i);

			/*
			 * Notice: mapping statement with the same offset means many getter
			 * or setter in the same line. so we don't count adjustment here
			 */
            if (i != 0)
                if (variableToMap.getOffset() != mappingVariables.get(i - 1).getOffset())
                    tempAdjustment = adjustment;
            String befStr = oldFunction.substring(0, tempAdjustment + variableToMap.getOffset() - functionOffset);

            String str = oldFunction.substring(tempAdjustment + variableToMap.getOffset() - functionOffset,
                    variableToMap.getOffset() + variableToMap.getOldVariable().length() - functionOffset
                            + tempAdjustment);

            str = str.replace(variableToMap.getOldVariable(), variableToMap.getNewVariable());

            String aftStr = oldFunction.substring(tempAdjustment + variableToMap.getOffset()
                    + variableToMap.getOldVariable().length() - functionOffset, oldFunction.length());

            adjustment += variableToMap.getNewVariable().length() - variableToMap.getOldVariable().length();

            oldFunction = befStr + str + aftStr;

        }

        normalizeSourcecode = oldFunction;
    }

    class MappingVariable {

        private String oldVariable = "";
        private String newVariable = "";
        private int level = 1;
        private int offset = 0;

        public MappingVariable(String oldVariable, int level) {
            this.oldVariable = oldVariable;
            this.level = level;
        }

        public MappingVariable(String oldVariable, int offset, int level) {
            this.oldVariable = oldVariable;
            this.offset = offset;
            this.level = level;
        }

        public MappingVariable(String oldVariable, String newVariable) {
            this.oldVariable = oldVariable;
            this.newVariable = newVariable;
        }

        public MappingVariable(String oldVariable, String newVariable, int level) {
            this.oldVariable = oldVariable;
            this.newVariable = newVariable;
            this.level = level;
        }

        public MappingVariable(String oldVariable, String newVariable, int offset, int level) {
            this.oldVariable = oldVariable;
            this.newVariable = newVariable;
            this.offset = offset;
            this.level = level;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getNewVariable() {
            return newVariable;
        }

        public void setNewVariable(String newVariable) {
            this.newVariable = newVariable;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public String getOldVariable() {
            return oldVariable;
        }

        public void setOldVariable(String oldVariable) {
            this.oldVariable = oldVariable;
        }

        public boolean isEqual(String oldVariable) {
            if (this.oldVariable.equals(oldVariable))
                return true;
            return false;
        }

        public boolean isEqual(String oldVariable, int level) {
            if (this.oldVariable.equals(oldVariable) && this.level == level)
                return true;
            return false;
        }

        @Override
        public String toString() {
            return oldVariable + "\t" + newVariable + "\t" + level + "\t" + offset;
        }

    }

}
