package normalizer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTBreakStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCaseStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCompoundStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTDefaultStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTReturnStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSwitchStatement;

import config.Paths;
import parser.projectparser.ProjectParser;
import tree.object.IFunctionNode;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

/**
 * Convert switch-case to if-else
 *
 * @author DucAnh
 */
public class SwitchCaseNormalizer extends AbstractFunctionNormalizer implements IFunctionNormalizer {

    /*
     * Represent "case ..."
     */
    private static final int CASE_STATEMENT = 0;
    private static final int BREAK_STATEMENT = 1;
    private static final int RETURN_STATEMENT = 2;
    /*
     * The statement puts in pairs of braces
     */
    private static final int COMPOUND_STATEMENT = 3;
    private static final int DEFAULT_STATEMENT = 4;
    private static final int NORMAL_STATEMENT = 5;
    private static final String OPENNING_BRACKET = "{";
    private static final String CLOSING_BRACKET = "}";
    private static final String COMPARISION = "==";
    private static final String OR = "||";

    public SwitchCaseNormalizer() {
    }

    public SwitchCaseNormalizer(IFunctionNode functionNode) {
        this.functionNode = functionNode;
    }

    public static void main(String[] args) throws Exception {
        ProjectParser parser = new ProjectParser(new File(Paths.SYMBOLIC_EXECUTION_TEST));

        IFunctionNode function = (IFunctionNode) Search
                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "TS::xu_li(TS*,char*)").get(0);
        SwitchCaseNormalizer norm = new SwitchCaseNormalizer();
        norm.setFunctionNode(function);
        norm.normalize();
        System.out.println(norm.getNormalizedSourcecode());
        System.out.println(norm.getTokens());
    }

    @Override
    public void normalize() {
        IASTFunctionDefinition astFunction = functionNode.getAST();
        normalizeSourcecode = astFunction.getRawSignature();
        List<CPPASTSwitchStatement> switchCases = getSwitchCases(astFunction);

        for (CPPASTSwitchStatement switchCase : switchCases)
            if (isSupported(switchCase)) {
                String ifElse = convertToIfElse(switchCase);
                normalizeSourcecode = normalizeSourcecode.replace(switchCase.getRawSignature(), ifElse);

            } else {
                // nothing to do
            }
    }

    /**
     * Get all switch cases in the function. The inner switch-cases are always
     * put at the top of the output.
     * <p>
     * <p>
     * For example, switch-case A contains switch-case B. The position of B is
     * put before the position of A
     *
     * @param functionNode Function
     * @return All switch cases in the function
     */
    private List<CPPASTSwitchStatement> getSwitchCases(IASTFunctionDefinition ast) {
        List<CPPASTSwitchStatement> switchCases = new ArrayList<>();

        ASTVisitor visitor = new ASTVisitor() {
            @Override
            public int visit(IASTStatement statement) {
                if (statement instanceof CPPASTSwitchStatement)
                    switchCases.add((CPPASTSwitchStatement) statement);
                return ASTVisitor.PROCESS_CONTINUE;
            }
        };

        visitor.shouldVisitStatements = true;
        ast.accept(visitor);

        return switchCases;
    }

    /**
     * Check whether the default statement is located at the end of
     * <b>switch-case</b> or not
     *
     * @return true if default statement is at the end of switch-case
     */
    private boolean isSupported(CPPASTSwitchStatement switchCase) {
        boolean isSupported = true;

        CPPASTCompoundStatement bodySwitchCase = (CPPASTCompoundStatement) switchCase.getChildren()[1];
        IASTNode[] children = bodySwitchCase.getChildren();

        boolean catchCase = false;
        for (int i = children.length - 1; i >= 0; i--) {
            IASTNode child = children[i];
            switch (getTypeofStatementInSwitch(child)) {
                case CASE_STATEMENT:
                    catchCase = true;
                    break;
                case DEFAULT_STATEMENT:
                    if (catchCase)
                        isSupported = false;
                    break;
            }
        }
        return isSupported;
    }

    /**
     * Convert the switch case statement to if...else
     *
     * @param switchCase
     * @return
     */
    private String convertToIfElse(CPPASTSwitchStatement switchCase) {
        String ifElse = new String();
        String nameVar = switchCase.getChildren()[0].getRawSignature();
        CPPASTCompoundStatement bodySwitchCase = (CPPASTCompoundStatement) switchCase.getChildren()[1];

        IASTNode[] children = bodySwitchCase.getChildren();
        boolean isFirstCase = true;
        for (int i = 0; i < children.length; i++) {
            IASTNode child = children[i];

            switch (getTypeofStatementInSwitch(child)) {
                case CASE_STATEMENT: {
                    if (isFirstCase) {
                        IASTNode value = child.getChildren()[0];
                        ifElse += "if (" + nameVar + SwitchCaseNormalizer.COMPARISION + value.getRawSignature();
                        isFirstCase = false;

                    } else if (isCase(i - 1, children)) {
                    /*
					 * If previous statement is case statement
					 */
                        IASTNode value = child.getChildren()[0];
                        ifElse += SwitchCaseNormalizer.OR + nameVar + SwitchCaseNormalizer.COMPARISION
                                + value.getRawSignature();

                    } else if (!isFirstCase) {
                        IASTNode value = child.getChildren()[0];
                        ifElse += SwitchCaseNormalizer.CLOSING_BRACKET + " else if (" + nameVar
                                + SwitchCaseNormalizer.COMPARISION + value.getRawSignature();
                        isFirstCase = false;
                    }
				/*
				 * If the case is final
				 */
                    if (!isCase(i + 1, children))
                        ifElse += ")" + SwitchCaseNormalizer.OPENNING_BRACKET;

                    break;
                }
                case BREAK_STATEMENT: {
                    // nothing to do
                    break;
                }
                case RETURN_STATEMENT: {
                    ifElse += " return;";
                    break;
                }
                case COMPOUND_STATEMENT: {
                    for (IASTNode item : child.getChildren())
                        if (getTypeofStatementInSwitch(item) == SwitchCaseNormalizer.BREAK_STATEMENT)
                            break;
                        else
                            ifElse += item.getRawSignature();
                    break;
                }
                case DEFAULT_STATEMENT: {
                    ifElse += SwitchCaseNormalizer.CLOSING_BRACKET + " else " + SwitchCaseNormalizer.OPENNING_BRACKET;
                    break;
                }
                case NORMAL_STATEMENT: {
                    if (getTypeofStatementInSwitch(child) != SwitchCaseNormalizer.BREAK_STATEMENT)
                        ifElse += child.getRawSignature();
                    break;
                }
                default:
                    break;
            }
        }
        ifElse += SwitchCaseNormalizer.CLOSING_BRACKET;
        return ifElse;

    }

    /**
     * Check whether next statement in switch-case is case statement
     *
     * @param pos
     * @param children
     * @return
     */
    private boolean isCase(int pos, IASTNode[] children) {
        if (pos < 0 || pos >= children.length)
            return false;
        else if (getTypeofStatementInSwitch(children[pos]) == SwitchCaseNormalizer.CASE_STATEMENT)
            return true;
        else
            return false;
    }

    /**
     * Get type of an AST node in switch-case block
     *
     * @param n
     * @return
     */
    private int getTypeofStatementInSwitch(IASTNode n) {
        if (n instanceof CPPASTCompoundStatement)
            return SwitchCaseNormalizer.COMPOUND_STATEMENT;
        else if (n instanceof CPPASTCaseStatement)
            return SwitchCaseNormalizer.CASE_STATEMENT;
        else if (n instanceof CPPASTBreakStatement)
            return SwitchCaseNormalizer.BREAK_STATEMENT;
        else if (n instanceof CPPASTReturnStatement)
            return SwitchCaseNormalizer.RETURN_STATEMENT;
        else if (n instanceof CPPASTDefaultStatement)
            return SwitchCaseNormalizer.DEFAULT_STATEMENT;
        else
            return SwitchCaseNormalizer.NORMAL_STATEMENT;
    }

}
