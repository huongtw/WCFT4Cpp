package normalizer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLiteralExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTArraySubscriptExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFieldReference;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionCallExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIdExpression;

import config.Paths;
import parser.projectparser.ProjectParser;
import testdatagen.se.CustomJeval;
import testdatagen.structuregen.ChangedToken;
import testdatagen.structuregen.ChangedVariableToken;
import tree.object.FunctionNode;
import tree.object.IFunctionNode;
import tree.object.INode;
import tree.object.IVariableNode;
import tree.object.StructureNode;
import tree.object.UnionNode;
import tree.object.VariableNode;
import utils.IRegex;
import utils.Utils;
import utils.search.FunctionNodeCondition;
import utils.search.Search;
import utils.search.VariableNodeCondition;

/**
 * Normalize union
 * <p>
 * Ex: sv.getAge() ---normalize--> sv_age
 * <p>
 * <p>
 * <br/>
 * For example, <b>Input:</b>
 * <p>
 * <p>
 * <pre>
 * union RGBA{
 * int color;
 * int aliasColor;
 * };
 *
 * int SimpleTest(RGBA s) {
 * if (s.color > 10 && s.aliasColor > 12)
 * return 1;
 * else
 * return 0;
 * }
 * </pre>
 * <p>
 * <b>Output</b>
 * <p>
 * <p>
 * <pre>
 * union RGBA{
 * int color;
 * int aliasColor;
 * };
 *
 * int SimpleTest(RGBA s) {
 * if (s_color > 10 && s_aliasColor > 12)
 * return 1;
 * else
 * return 0;
 * }
 * </pre>
 *
 * @author DucAnh
 */
public class UnionNormalizer extends AbstractFunctionNormalizer implements IFunctionNormalizer {

    public UnionNormalizer() {
    }

    public UnionNormalizer(IFunctionNode functionNode) {
        this.functionNode = functionNode;
    }

    public static void main(String[] args) throws Exception {
        /**
         * Phân tích project
         */
        ProjectParser parser = new ProjectParser(new File(Paths.TSDV_R1));
        Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "SimpleTest(RGBA)").get(0);

        UnionNormalizer transformer = new UnionNormalizer();

        transformer.normalize();
        System.out.println(transformer.getTokens());
    }

    @Override
    public void normalize() {
        transformPointerOperator(functionNode);
        try {
            normalizeSourcecode = transform(functionNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Input: sv.getName()[2+1]<br/>
     * Output: sv, getName, 2+1
     *
     * @param e
     * @return
     */
    private List<IASTNode> getAllLeaf(IASTNode e) {
        List<IASTNode> output = new ArrayList<>();
        for (IASTNode n : e.getChildren())
            if (n instanceof IASTName || n instanceof CPPASTIdExpression || n instanceof ICPPASTLiteralExpression
                    || n instanceof ICPPASTBinaryExpression)
                output.add(n);
            else
                output.addAll(getAllLeaf(n));
        return output;
    }

    private List<IASTExpression> getUnionExpressions(List<PassingVariableNode> unionNames,
                                                     IASTFunctionDefinition astFunction) {

        List<IASTExpression> expressions = new ArrayList<>();
        ASTVisitor visitor = new ASTVisitor() {

            @Override
            public int visit(IASTExpression expression) {
                if (expression instanceof CPPASTFunctionCallExpression
                        || expression instanceof CPPASTArraySubscriptExpression
                        || expression instanceof CPPASTFieldReference) {

                    ASTVisitor subVisitor = new ASTVisitor() {

                        @Override
                        public int visit(IASTExpression e) {
                            if (e instanceof CPPASTIdExpression) {

                                String nameAccessVar = e.getRawSignature();
                                for (PassingVariableNode unionName : unionNames)
                                    if (unionName.getNameVar().equals(nameAccessVar)) {
                                        expressions.add(expression);
                                        break;
                                    }
                            }
                            return ASTVisitor.PROCESS_CONTINUE;
                        }

                    };
                    subVisitor.shouldVisitExpressions = true;
                    expression.accept(subVisitor);
                    return ASTVisitor.PROCESS_SKIP;
                }
                return ASTVisitor.PROCESS_CONTINUE;
            }

        };
        visitor.shouldVisitExpressions = true;
        astFunction.accept(visitor);

        return expressions;
    }

    private List<PassingVariableNode> getUnionName(IFunctionNode function) {

        List<PassingVariableNode> UnionNames = new ArrayList<>();

        for (INode paramater : function.getPassingVariables())
            if (((IVariableNode) paramater).resolveCoreType() instanceof UnionNode) {

                PassingVariableNode m = new PassingVariableNode(paramater.getNewType(),
                        ((IVariableNode) paramater).resolveCoreType());
                UnionNames.add(m);
            }
        return UnionNames;
    }

    private ChangedToken newVarGeneration(List<IASTNode> subExpressions, UnionNode unionNode, String oldName)
            throws Exception {
        final String METHOD_SIGNAL = "()";
        String newNameVar = "";
        String currentType = "";
        String reducedName = "";

        INode currentNode = unionNode;
        for (IASTNode subExpression : subExpressions) {
            String subExpressionInStr = subExpression.getRawSignature();

            if (subExpression instanceof IASTName) {
                List<INode> searchedNodes = Search.searchNodes(currentNode, new FunctionNodeCondition(),
                        subExpressionInStr + METHOD_SIGNAL);

                if (searchedNodes.size() == 0) {
                    searchedNodes = Search.searchNodes(currentNode, new VariableNodeCondition(), subExpressionInStr);

                    if (searchedNodes.size() == 0)
                        throw new Exception("Dont support " + subExpressionInStr);
                    else {

                        VariableNode v = (VariableNode) searchedNodes.get(0);
                        currentNode = v.resolveCoreType();

                        newNameVar += ClassvsStructNormalizer.DELIMITER + v.getNewType();

                        currentType = v.getRawType();

                        if (v.getRawType().contains("*") && v.resolveCoreType() instanceof StructureNode)
                            reducedName += "." + v.getNewType();
                        else
                            reducedName += "." + v.getNewType();
                    }
                } else {
                    INode searchedNode = searchedNodes.get(0);
                    INode correspondingVar = ((FunctionNode) searchedNode).isGetter();
                    if (correspondingVar != null) {
                        newNameVar += ClassvsStructNormalizer.DELIMITER + correspondingVar.getNewType();

                        IVariableNode v = (IVariableNode) correspondingVar;
                        currentNode = v.resolveCoreType();

                        currentType = v.getRawType();
                        /*
						 *
						 */
                        if (v.getRawType().contains("*") && v.resolveCoreType() instanceof StructureNode)
                            reducedName += "." + correspondingVar.getNewType();
                        else
                            reducedName += "." + correspondingVar.getNewType();
                    } else
                        break;
                }

            } else if (subExpression instanceof CPPASTIdExpression) {
                newNameVar += subExpressionInStr;
                reducedName += subExpressionInStr;

            } else if (subExpression instanceof ICPPASTLiteralExpression) {
                newNameVar += ClassvsStructNormalizer.DELIMITER + subExpressionInStr;
                reducedName += "[" + subExpressionInStr + "]";

                currentType = currentType.replaceAll("\\*{1}$", "");
                currentType = currentType.replaceAll("\\[.*\\]", "");

            } else if (subExpression instanceof ICPPASTBinaryExpression) {
                subExpressionInStr = new CustomJeval().evaluate(subExpressionInStr);
                try {
                    int tmp = Integer.parseInt(subExpressionInStr);
                    subExpressionInStr += ClassvsStructNormalizer.DELIMITER + tmp;
                    reducedName += "[" + tmp + "]";
                } catch (Exception e) {
                    throw new Exception("Chưa hỗ trợ biến mảng là một biểu thức không rút gọn được");
                }
            }
        }
        return new ChangedVariableToken(currentType, newNameVar, oldName, reducedName);
    }

    private String transform(IFunctionNode function) throws Exception {
        List<PassingVariableNode> unionNames = getUnionName(function);

        List<IASTExpression> unionExpressions = getUnionExpressions(unionNames, function.getAST());

        for (IASTExpression unionExpression : unionExpressions) {
            List<IASTNode> subExpressions = getAllLeaf(unionExpression);

            INode unionNode = null;

            for (PassingVariableNode unionName : unionNames)
                if (unionName.getNameVar().equals(subExpressions.get(0).getRawSignature())) {
                    unionNode = unionName.getNode();
                    ChangedToken reducedVariable = newVarGeneration(subExpressions, (UnionNode) unionNode,
                            unionExpression.getRawSignature());

                    tokens.add(reducedVariable);
                    break;
                }
        }

		/*
		 *
		 */
        String oldCode = function.getAST().getRawSignature();
        String newCode = oldCode;
        String newDeclarations = "";
        for (ChangedToken item : tokens)
            if (item instanceof ChangedVariableToken) {
                newCode = newCode.replace(item.getOldName(), item.getNewName());
                newDeclarations += ((ChangedVariableToken) item).getDeclaration() + ",";
            }

        newCode = newCode.replaceFirst("\\(", "(" + newDeclarations);
        newCode = newCode.replace(",)", ")");
        return newCode;
    }

    private void transformPointerOperator(IFunctionNode function) {
        String content = function.getAST().getRawSignature();
		/*
		 * "abc->" --->"abc[0]->"
		 */
        content = content.replaceAll("(" + IRegex.NAME_REGEX + ")->", "$1[0]->");
        IASTFunctionDefinition ast = Utils.getFunctionsinAST(content.toCharArray()).get(0);
        function.setAST(ast);
    }
}

class PassingVariableNode {

    String nameVar;
    INode node;

    public PassingVariableNode(String nameVar, INode node) {
        this.nameVar = nameVar;
        this.node = node;
    }

    public String getNameVar() {
        return nameVar;
    }

    public INode getNode() {
        return node;
    }

    @Override
    public String toString() {
        return node.getAbsolutePath() + "; name = " + nameVar + "; node=" + node.getAbsolutePath();
    }
}
