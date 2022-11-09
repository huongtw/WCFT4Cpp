package normalizer;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;

import tree.object.FunctionNode;
import tree.object.IFunctionNode;
import utils.ASTUtils;
import utils.SpecialCharacter;
import utils.Utils;

/**
 * Ex: "bool tmp = a>b"------->"int bool; if (a>b) tmp = true; else tmp =
 * false;"
 *
 * @author ducanhnguyen
 */
public class ConditionCovertNormalizer extends AbstractFunctionNormalizer implements IFunctionNormalizer {

    public ConditionCovertNormalizer() {
    }

    public ConditionCovertNormalizer(IFunctionNode functionNode) {
        this.functionNode = functionNode;
    }

    public static void main(String[] args) throws Exception {
        String test = "void test(int x,int y){\nbool greater = x > y;\ngreater = x > y;\n}";
        FunctionNode fn = new FunctionNode();
        fn.setAST(Utils.getFunctionsinAST(test.toCharArray()).get(0));
        ConditionCovertNormalizer norm = new ConditionCovertNormalizer();
        norm.setFunctionNode(fn);
        norm.normalize();
        System.out.println(norm.getNormalizedSourcecode());
    }

    @Override
    public void normalize() {
        normalizeSourcecode = functionNode.getAST().getRawSignature();

		/*
         * Analyze type 1
		 */
        List<IASTSimpleDeclaration> declarationASTs = Utils.getSimpleDeclarations(functionNode.getAST());

        for (IASTSimpleDeclaration declaration : declarationASTs) {
            IASTDeclSpecifier decl = declaration.getDeclSpecifier();

            IASTDeclarator[] declarators = declaration.getDeclarators();
            for (IASTDeclarator declarator : declarators) {
                IASTInitializer ini = declarator.getInitializer();
                if (ini != null) {
                    String newStatement = decl.getRawSignature() + " " + declarator.getRawSignature();
                    // Ex: bool greater = x > y
                    normalizeSourcecode = normalizeStatement(newStatement);
                }
            }
        }

		/*
		 * Analyze type 2
		 */
        IASTFunctionDefinition newFn = Utils.getFunctionsinAST(normalizeSourcecode.toCharArray()).get(0);
        List<ICPPASTBinaryExpression> binaryASTs = Utils.getBinaryExpressions(newFn);

        for (ICPPASTBinaryExpression binaryAST : binaryASTs)
            if (binaryAST.getOperator() == IASTBinaryExpression.op_assign) {
                IASTNode right = binaryAST.getOperand2();
                IASTNode left = binaryAST.getOperand1();
                if (Utils.isCondition(right.getRawSignature())
                        && (left instanceof IASTIdExpression || left instanceof ICPPASTFieldReference))
					/*
					 * Ex: greater = x == y
					 */
                    normalizeSourcecode = normalizeStatement(binaryAST.getRawSignature());
            }
    }

    private String normalizeStatement(String source) {
        Pattern p = Pattern.compile("(?<name>[^=\r\n]+)=" + "(?<cond>[^?\r\n;]+)");
        Matcher m = p.matcher(source);
        StringBuffer sb = new StringBuffer(source.length());

        while (m.find()) {
            String stm = m.group(0) + SpecialCharacter.END_OF_STATEMENT;
            IASTTranslationUnit asts = null;
            try {
                asts = Utils.getIASTTranslationUnitforCpp(stm.toCharArray());
            } catch (Exception ex) {
                Logger.getLogger(TernaryConvertNormalizer.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (asts != null) {
				/*
				 * Two cases:
				 *
				 * 1. "int c = (a>b) ? a : b;" (Exist declarations)
				 *
				 * 2. "c = (a>b) ? a : b;" (Dont exist declarations)
				 */
                IASTNode firstChild = asts.getChildren()[0];

                if (firstChild instanceof CPPASTSimpleDeclaration) {
                    IASTInitializer initializer = ((CPPASTSimpleDeclaration) firstChild).getDeclarators()[0]
                            .getInitializer();

                    if (initializer != null) {
                        IASTNode ini = initializer.getChildren()[0];

                        if (ini instanceof ICPPASTBinaryExpression)
                            switch (((ICPPASTBinaryExpression) ini).getOperator()) {
                                case IASTBinaryExpression.op_greaterEqual:
                                case IASTBinaryExpression.op_greaterThan:
                                case IASTBinaryExpression.op_lessEqual:
                                case IASTBinaryExpression.op_lessThan:
                                case IASTBinaryExpression.op_logicalAnd:
                                case IASTBinaryExpression.op_logicalOr:
                                case IASTBinaryExpression.op_equals:
                                case IASTBinaryExpression.op_notequals:

								/*
								 * Case 1
								 */
                                    IASTNode typeVariable = firstChild.getChildren()[0];
                                    IASTNode declaration = firstChild.getChildren()[1];
                                    IASTNode nameVar = declaration.getChildren()[0];

                                    String cond = m.group("cond");

                                    m.appendReplacement(sb,
                                            Matcher.quoteReplacement(String.format("%s %s;if (%s) %s = %s; else %s = %s",
                                                    typeVariable.getRawSignature(), nameVar.getRawSignature(), cond,
                                                    nameVar.getRawSignature(), "1", nameVar.getRawSignature(), "0")));
                                    break;
                            }
                    }
                } else {
					/*
					 * Case 2
					 */
                    String name = m.group("name"), cond = m.group("cond");
                    IASTNode condAST = ASTUtils.convertToIAST(cond);

                    if (condAST instanceof ICPPASTBinaryExpression)
                        switch (((ICPPASTBinaryExpression) condAST).getOperator()) {
                            case IASTBinaryExpression.op_greaterEqual:
                            case IASTBinaryExpression.op_greaterThan:
                            case IASTBinaryExpression.op_lessEqual:
                            case IASTBinaryExpression.op_lessThan:
                            case IASTBinaryExpression.op_logicalAnd:
                            case IASTBinaryExpression.op_logicalOr:
                            case IASTBinaryExpression.op_equals:
                            case IASTBinaryExpression.op_notequals:
                                m.appendReplacement(sb, Matcher.quoteReplacement(
                                        String.format("if (%s) %s = %s; else %s = %s", cond, name, "1", name, "0")));
                        }
                }
            } else {
                // nothing to do
            }
        }
        m.appendTail(sb);

        String normalizedDeclaration = sb.toString();
        normalizeSourcecode = normalizeSourcecode.replace(source, normalizedDeclaration);
        return normalizeSourcecode;
    }
}
