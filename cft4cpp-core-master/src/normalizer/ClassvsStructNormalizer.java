package normalizer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
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
import testdatagen.testdatainit.VariableTypes;
import tree.object.FunctionNode;
import tree.object.IFunctionNode;
import tree.object.INode;
import tree.object.IVariableNode;
import tree.object.StructureNode;
import tree.object.VariableNode;
import utils.Utils;
import utils.search.FunctionNodeCondition;
import utils.search.Search;
import utils.search.VariableNodeCondition;

/**
 * Normalize calls to attributes of class/struct in function.
 * <p>
 * Ex: sv.getAge() ---normalize--> sv_age
 * <p>
 * <p>
 * <br/>
 * For example, <b>Input:</b>
 * <p>
 * <p>
 * <pre>
 * int class_test3(SinhVien sv) {
 * 	if (sv.getName()[0] == 'a')
 * 		return 0;
 * 	else
 * 		return 1;
 * }
 * </pre>
 * <p>
 * <b>Output</b>
 * <p>
 * <p>
 * <pre>
 * int class_test3(SinhVien sv) {
 * 	if (sv_name[0] == 'a')
 * 		return 0;
 * 	else
 * 		return 1;
 * }
 * </pre>
 *
 * @author DucAnh
 */
@Deprecated
public class ClassvsStructNormalizer extends AbstractFunctionNormalizer
        implements IFunctionNormalizer {
    public static final String DELIMITER = "_____";
    final static Logger logger = Logger.getLogger(ProjectParser.class);

    public ClassvsStructNormalizer() {
    }

    public ClassvsStructNormalizer(IFunctionNode fn) {
        setFunctionNode(fn);
    }

    public static void main(String[] args) throws Exception {
        ProjectParser parser = new ProjectParser(new File(Paths.CORE_UTILS));
        parser.getIgnoreFolders()
                .add(new File(
                        "/home/ducanhnguyen/Desktop/ava/data-test/ducanh/coreutils-8.24/gnulib-tests"));
        IFunctionNode function = (IFunctionNode) Search.searchNodes(
                parser.getRootTree(), new FunctionNodeCondition(),
                "who.c" + File.separator + "main(int,char**)").get(0);

        System.out.println(function.getAST().getRawSignature());
        ClassvsStructNormalizer transformer = new ClassvsStructNormalizer();
        transformer.setFunctionNode(function);
        transformer.normalize();
        System.out.println(transformer.getTokens());
        System.out.println(transformer.getNormalizedSourcecode());
    }

    @Override
    public void normalize() {
        logger.setLevel(Level.OFF);
        if (functionNode != null) {
            normalizeSourcecode = functionNode.getAST().getRawSignature();
            transformPointerOperator(functionNode);

            try {
                normalizeSourcecode = transform(functionNode);
                normalizeSourcecode = replaceFieldReference(normalizeSourcecode);
            } catch (Exception e) {
                e.printStackTrace();
                normalizeSourcecode = originalSourcecode;
            }
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
            if (n instanceof IASTName || n instanceof CPPASTIdExpression
                    || n instanceof ICPPASTLiteralExpression
                    || n instanceof ICPPASTBinaryExpression)
                output.add(n);
            else
                output.addAll(getAllLeaf(n));
        return output;
    }

    /**
     * For example,
     * <p>
     * <p>
     * <pre>
     * int class_test0(SinhVien sv){
     * char c = sv.getName()[2];
     * char* s = sv.getOther()[0].getName();
     * if (sv.getAge() > 0)
     * return 0;
     * else
     * return 1;
     * }
     * </pre>
     * <p>
     * the output : sv.getName()[2], sv.getOther()[0].getName(), sv.getAge()
     *
     * @param classvsStructNames
     * @param astFunction
     * @return
     */
    private List<IASTExpression> getClassvsStructExpressions(
            List<PassingVariableNode> classvsStructNames,
            IASTFunctionDefinition astFunction) {
        logger.debug("getClassvsStructExpressions");
        List<IASTExpression> expressions = new ArrayList<>();
        ASTVisitor visitor = new ASTVisitor() {

            @Override
            public int visit(IASTExpression expression) {
                /*
				 * Ex1: sv.getAge() ------------ CPPASTFunctionCallExpression
				 *
				 * Ex2: sv.age[2] -------------- CPPASTArraySubscriptExpression
				 *
				 * Ex3: sv.age ------------- CPPASTFieldReference
				 */
                if (expression instanceof CPPASTFunctionCallExpression
                        || expression instanceof CPPASTArraySubscriptExpression
                        || expression instanceof CPPASTFieldReference) {

                    ASTVisitor subVisitor = new ASTVisitor() {

                        @Override
                        public int visit(IASTExpression e) {

                            if (e instanceof CPPASTIdExpression) {
                                String nameAccessVar = e.getRawSignature();
                                for (PassingVariableNode classvsStructName : classvsStructNames)
                                    if (classvsStructName.getNameVar().equals(
                                            nameAccessVar)) {
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

    private List<PassingVariableNode> getClassvsStructName(
            IFunctionNode function) {
        logger.debug("getClassvsStructName");
        List<PassingVariableNode> classvsStructNames = new ArrayList<>();

        for (INode paramater : function.getPassingVariables()) {
            String rawType = ((IVariableNode) paramater).getRawType();
            if (VariableTypes.isBasic(rawType)
                    || VariableTypes.isOneDimensionBasic(rawType)
                    || VariableTypes.isTwoDimensionBasic(rawType)
                    || VariableTypes.isOneLevelBasic(rawType)
                    || VariableTypes.isTwoLevelBasic(rawType)) {
                // nothing to do
            } else {
                INode type = ((IVariableNode) paramater).resolveCoreType();
                if (type instanceof StructureNode) {

                    PassingVariableNode m = new PassingVariableNode(
                            paramater.getNewType(),
                            ((IVariableNode) paramater).resolveCoreType());
                    classvsStructNames.add(m);
                }
            }
        }
        return classvsStructNames;
    }

    private ChangedToken newVarGeneration(List<IASTNode> subExpressions,
                                          StructureNode classNode, String oldName) throws Exception {
        String newNameVar = "";
        String currentType = "";
        String reducedName = "";

        INode currentNode = classNode;
        for (IASTNode subExpression : subExpressions) {
            String subExpressionInStr = subExpression.getRawSignature();

            if (subExpression instanceof IASTName) {
                List<INode> searchedNodes = Search.searchNodes(currentNode,
                        new FunctionNodeCondition(), subExpressionInStr + "()");

                if (searchedNodes.size() == 0) {
                    searchedNodes = Search.searchNodes(currentNode,
                            new VariableNodeCondition(), subExpressionInStr);

                    if (searchedNodes.size() == 0)
                        throw new Exception("Dont support "
                                + subExpressionInStr);
                    else {

                        VariableNode v = (VariableNode) searchedNodes.get(0);
                        currentNode = v.resolveCoreType();

                        newNameVar += ClassvsStructNormalizer.DELIMITER
                                + v.getNewType();

                        currentType = v.getRawType();

                        if (v.getRawType().contains("*")
                                && v.resolveCoreType() instanceof StructureNode)
                            reducedName += "." + v.getNewType();
                        else
                            reducedName += "." + v.getNewType();
                    }
                } else {
                    INode searchedNode = searchedNodes.get(0);
                    INode correspondingVar = ((FunctionNode) searchedNode)
                            .isGetter();
                    if (correspondingVar != null) {
                        newNameVar += ClassvsStructNormalizer.DELIMITER
                                + correspondingVar.getNewType();

                        IVariableNode v = (IVariableNode) correspondingVar;
                        currentNode = v.resolveCoreType();

                        currentType = v.getRawType();
						/*
						 *
						 */
                        if (v.getRawType().contains("*")
                                && v.resolveCoreType() instanceof StructureNode)
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
                newNameVar += ClassvsStructNormalizer.DELIMITER
                        + subExpressionInStr;
                reducedName += "[" + subExpressionInStr + "]";

                currentType = currentType.replaceAll("\\*{1}$", "");
                currentType = currentType.replaceAll("\\[.*\\]", "");
            } else if (subExpression instanceof ICPPASTBinaryExpression) {
                subExpressionInStr = new CustomJeval()
                        .evaluate(subExpressionInStr);
                try {
                    int tmp = Integer.parseInt(subExpressionInStr);
                    subExpressionInStr += ClassvsStructNormalizer.DELIMITER
                            + tmp;
                    reducedName += "[" + tmp + "]";
                } catch (Exception e) {
                    throw new Exception(
                            "Chưa hỗ trợ biến mảng là một biểu thức không rút gọn được");
                }
            }
        }
        return new ChangedVariableToken(currentType, newNameVar, oldName,
                reducedName);
    }

    private String transform(IFunctionNode function) throws Exception {
        logger.debug("transform");
        List<PassingVariableNode> classvsStructNames = getClassvsStructName(function);

        List<IASTExpression> classvsStructExpressions = getClassvsStructExpressions(
                classvsStructNames, function.getAST());

        for (IASTExpression classvsStructExpression : classvsStructExpressions) {
            logger.debug("Parse " + classvsStructExpression.getRawSignature());
            List<IASTNode> subExpressions = getAllLeaf(classvsStructExpression);

            INode structureNode = null;

            for (PassingVariableNode classvsStructName : classvsStructNames)
                if (classvsStructName.getNameVar().equals(
                        subExpressions.get(0).getRawSignature())) {
                    structureNode = classvsStructName.getNode();
                    ChangedToken reducedVariable = newVarGeneration(
                            subExpressions, (StructureNode) structureNode,
                            classvsStructExpression.getRawSignature());

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
                newDeclarations += ((ChangedVariableToken) item)
                        .getDeclaration() + ",";
            }

        newCode = newCode.replaceFirst("\\(", "(" + newDeclarations);
        newCode = newCode.replace(",)", ")");
        return newCode;
    }

    private void transformPointerOperator(IFunctionNode function) {
        logger.debug("transformPointerOperator");
        String content = function.getAST().getRawSignature();
		/*
		 * "abc->" --->"abc[0]->"
		 */
        content = content.replaceAll("([a-zA-Z0-9_]+)->", "$1[0]->");

		/*
		 * The above regexes may affect "this->" expression that make it to be
		 * wrong. We need restore the original of "this" expression. Ex:
		 * "this[0]->" ----> "this"
		 */
        content = content.replace("this[0]->", "this->");

        IASTFunctionDefinition ast = Utils.getFunctionsinAST(
                content.toCharArray()).get(0);
        function.setAST(ast);
    }

    /**
     * Ex: "y.level2 = x + 1;"------------>"y_level2 = x + 1;"
     *
     * @param content
     * @return
     */
    private String replaceFieldReference(String content) {
        logger.debug("replaceFieldReference");
        IASTFunctionDefinition ast = Utils.getFunctionsinAST(
                content.toCharArray()).get(0);
        List<ICPPASTFieldReference> references = Utils.getFieldReferences(ast);

        for (ICPPASTFieldReference reference : references) {
            String newRef = reference.getRawSignature().replace(".", "_");
            content = content.replace(reference.getRawSignature(), newRef);
        }
        return content;
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
            return node.getAbsolutePath() + "; name = " + nameVar + "; node="
                    + node.getAbsolutePath();
        }
    }
}
