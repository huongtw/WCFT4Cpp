package testdatagen.se.expression;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IASTNode;

import config.Paths;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import parser.projectparser.ProjectParser;
import testdatagen.testdatainit.BasicTypeRandom;
import tree.object.FunctionNode;
import tree.object.INode;
import tree.object.Node;
import utils.Utils;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

public class MultipleConditionSelection {

    private IASTNode conAst;
    private boolean targetValue;

    public MultipleConditionSelection() {

    }

    public static void main(String[] args) throws Exception {
        ProjectParser parser = new ProjectParser(new File(Paths.DATA_GEN_TEST));

        FunctionNode function = (FunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
                "test(int,int*,int[],int[2],char,char*,char[],char[10],SinhVien*,SinhVien,SinhVien[])").get(0);

        Utils.findFirstConditionByName(
                "i>0&&i1[0]==1&&i2[1]==2&&i3[2]==3&&c==65&&c1[0]=='7'&&c2[4]==67&&c3[10]==88&&sv1.age1==1&&sv==NULL",
                function.getAST());

        new MultipleConditionSelection();

    }

    private void findRandomSolution(Node root) throws EvaluationException {
        boolean isSolution = false;
        do {
            String s = randomValue(root);

            if (new Evaluator().evaluate(s).equals("1.0") && targetValue == true)
                isSolution = true;
            else if (new Evaluator().evaluate(s).equals("0.0") && targetValue == false)
                isSolution = true;
        } while (!isSolution);
    }

    public Map<ExpressionNode, Boolean> getASolution(IASTNode conAst, boolean targetValue) throws Exception {
        this.conAst = conAst;
        this.targetValue = targetValue;

        TreeExpressionGeneration conTreeGen = new TreeExpressionGeneration(conAst);
        Node root = conTreeGen.getRoot();
        //

        findRandomSolution(root);

        Map<ExpressionNode, Boolean> solutionsMap = getSolutionMap(root);

        removeUnecessaryExpression(solutionsMap);

        return solutionsMap;
    }

    public IASTNode getConAst() {
        return conAst;
    }

    public void setConAst(IASTNode conAst) {
        this.conAst = conAst;
    }

    private String getExpressionInStr(INode n) {
        String str = "";

        if (n instanceof RootConditionNode)
            return getExpressionInStr(n.getChildren().get(0));
        else if (n instanceof NegativeNode)
            return "!(" + getExpressionInStr(n.getChildren().get(0)) + ")";
        else if (n instanceof OperatorNode) {
            INode child0 = n.getChildren().get(0);
            INode child1 = n.getChildren().get(1);
            str = "(" + getExpressionInStr(child0) + ((OperatorNode) n).getNewType() + getExpressionInStr(child1) + ")";

        } else if (n instanceof ExpressionNode)
            if (((ExpressionNode) n).getValue() == true)
                str = "1";
            else
                str = "0";
        return str;
    }

    private Map<ExpressionNode, Boolean> getSolutionMap(INode n) {
        Map<ExpressionNode, Boolean> solutionMap = new HashMap<>();
        if (n instanceof RootConditionNode || n instanceof NegativeNode)
            solutionMap.putAll(getSolutionMap(n.getChildren().get(0)));
        else if (n instanceof OperatorNode) {
            INode child0 = n.getChildren().get(0);
            solutionMap.putAll(getSolutionMap(child0));

            INode child1 = n.getChildren().get(1);
            solutionMap.putAll(getSolutionMap(child1));
        } else if (n instanceof ExpressionNode)
            solutionMap.put((ExpressionNode) n, ((ExpressionNode) n).getValue());
        return solutionMap;
    }

    public boolean isTargetValue() {
        return targetValue;
    }

    public void setTargetValue(boolean targetValue) {
        this.targetValue = targetValue;
    }

    private String randomValue(INode n) {
        String str = "";

        if (n instanceof RootConditionNode)
            return randomValue(n.getChildren().get(0));
        else if (n instanceof NegativeNode)
            return "!(" + randomValue(n.getChildren().get(0)) + ")";
        else if (n instanceof OperatorNode) {
            INode child0 = n.getChildren().get(0);
            INode child1 = n.getChildren().get(1);
            return str = "(" + randomValue(child0) + ((OperatorNode) n).getNewType() + randomValue(child1) + ")";

        } else if (n instanceof ExpressionNode)
            switch (n.getNewType()) {
                case "1":
                case "true": {
                    ((ExpressionNode) n).setValue(true);
                    return "1";
                }
                case "0":
                case "false": {
                    ((ExpressionNode) n).setValue(false);
                    return "0";
                }
                default: {
                    int randomValue = BasicTypeRandom.generateInt(0, 1);
                    switch (randomValue) {
                        case 0:
                            ((ExpressionNode) n).setValue(false);
                            return "0";
                        case 1:
                            ((ExpressionNode) n).setValue(true);
                            return "1";
                    }

                }
            }
        return str;
    }

    private void removeUnecessaryExpression(Map<ExpressionNode, Boolean> solutionsMap) throws EvaluationException {

        for (ExpressionNode exp : solutionsMap.keySet()) {
            exp.setValue(!exp.getValue());

            String expInStr = getExpressionInStr(Utils.getRoot(exp));

            if (new Evaluator().evaluate(expInStr).equals("0.0") && targetValue == false) {
                solutionsMap.remove(exp);
                break;
            } else if (new Evaluator().evaluate(expInStr).equals("1.0") && targetValue == true) {
                solutionsMap.remove(exp);
                break;
            } else
                continue;
        }
    }

}
