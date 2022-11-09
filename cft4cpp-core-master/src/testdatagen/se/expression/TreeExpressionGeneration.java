package testdatagen.se.expression;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTLiteralExpression;

import config.Paths;
import parser.projectparser.ProjectParser;
import tree.object.FunctionNode;
import tree.object.Node;
import utils.Utils;
import utils.search.FunctionNodeCondition;
import utils.search.Search;
import utils.tostring.ConditionTreeDisplayer;

public class TreeExpressionGeneration {
    final static Logger logger = Logger.getLogger(TreeExpressionGeneration.class);
    protected Node root = new RootConditionNode();

    public TreeExpressionGeneration(IASTNode expressison) throws Exception {
        root.setName(expressison.getRawSignature());
        /*
		 * - TH1: 1
		 *
		 * - TH2: (((a>0)))
		 *
		 * - TH3: VD: a>0
		 */
        Node newNode = null;
        // TH1
        if (expressison instanceof CPPASTLiteralExpression) {
            newNode = new ExpressionNode(expressison);
            newNode.setParent(root);
            root.getChildren().add(newNode);
        } else // TH2, TH3
            if (expressison instanceof IASTBinaryExpression || expressison instanceof IASTUnaryExpression) {

                if (expressison instanceof IASTUnaryExpression)
                    do {
                        if (((IASTUnaryExpression) expressison).getOperator() == IASTUnaryExpression.op_not) {
                            int operator = ((IASTUnaryExpression) expressison).getOperator();
                            NegativeNode op = new NegativeNode(operator);
                            op.setParent(newNode);
                            root.getChildren().add(op);
                            newNode = op;
                        }
                        expressison = ((IASTUnaryExpression) expressison).getChildren()[0];
                    } while (expressison instanceof IASTUnaryExpression);
                if (expressison instanceof IASTBinaryExpression) {
				/*
				 *
				 * - TH1: a>0 && b>0
				 *
				 * - TH2: a>0
				 */
                    int operator = getOperator((IASTBinaryExpression) expressison);

                    switch (operator) {
                        case IASTBinaryExpression.op_binaryAnd:
                        case IASTBinaryExpression.op_logicalAnd:
                        case IASTBinaryExpression.op_binaryOr:
                        case IASTBinaryExpression.op_logicalOr:
                            // TH1
                            OperatorNode op = new OperatorNode(operator);
                            op.setParent(newNode);
                            root.getChildren().add(op);
                            for (IASTNode child : expressison.getChildren())
                                nodesStackGeneration(child, op);
                            break;
                        default:
                            // TH2
                            ExpressionNode exp = new ExpressionNode(expressison);
                            exp.setParent(newNode);
                            root.getChildren().add(exp);
                    }
                } else if (expressison instanceof IASTLiteralExpression) {
                    ExpressionNode exp = new ExpressionNode(expressison);
                    exp.setParent(newNode);
                    root.getChildren().add(exp);
                }
            }
    }

    public static void main(String[] args) throws Exception {
        ProjectParser parser = new ProjectParser(new File(Paths.TREE_EXPRESSION_GENERATION_TEST));

        FunctionNode function = (FunctionNode) Search
                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "test(int,int,int)").get(0);

        IASTNode expressionNodes = Utils.findFirstConditionByName("a>0 || (b > 0 && b<9)", function.getAST());

        TreeExpressionGeneration treeGen = new TreeExpressionGeneration(expressionNodes);
        logger.debug("\n" + new ConditionTreeDisplayer(treeGen.getRoot()).getTreeInString());
    }

    private int getOperator(IASTBinaryExpression expression) {
        int operator = expression.getOperator();
        return operator;
    }

    private void nodesStackGeneration(IASTNode expression, Node parent) throws Exception {
		/*
		 *
		 * - TH1: (x>1)
		 *
		 * - TH2: Binart expression
		 *
		 * - TH3: 1
		 */
        if (expression instanceof IASTUnaryExpression) {
            do {
                if (((IASTUnaryExpression) expression).getOperator() == IASTUnaryExpression.op_not) {
                    int operator = IASTUnaryExpression.op_not;
                    NegativeNode op = new NegativeNode(operator);
                    op.setParent(parent);
                    parent.getChildren().add(op);
                    parent = op;
                }
                expression = ((IASTUnaryExpression) expression).getChildren()[0];
            } while (expression instanceof IASTUnaryExpression);

            nodesStackGeneration(expression, parent);

        } else if (expression instanceof IASTBinaryExpression) {
            // TH: !a>0
            if (expression.getRawSignature().matches("!\\s*[^\\(]"))
                throw new Exception("Dont support " + expression.getRawSignature());
            else {
				/*
				 * - TH1: a>0 && b>0
				 *
				 * - TH2: a>0
				 */
                int operator = getOperator((IASTBinaryExpression) expression);

                switch (operator) {
                    case IASTBinaryExpression.op_binaryAnd:
                    case IASTBinaryExpression.op_logicalAnd:
                    case IASTBinaryExpression.op_binaryOr:
                    case IASTBinaryExpression.op_logicalOr:
                        // TH1
                        Node subParent = new OperatorNode(operator);
                        parent.getChildren().add(subParent);
                        subParent.setParent(parent);

                        for (IASTNode child : expression.getChildren())
                            nodesStackGeneration(child, subParent);
                        break;

                    default:
                        // TH2
                        ExpressionNode conNode = new ExpressionNode(expression);

                        parent.getChildren().add(conNode);
                        conNode.setParent(parent);
                        return;
                }
            }
        } else if (expression instanceof CPPASTLiteralExpression) {
            ExpressionNode conNode = new ExpressionNode(expression);

            parent.getChildren().add(conNode);
            conNode.setParent(parent);
        }
    }

    public Node getRoot() {
        return root;
    }

}
