package normalizer;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTConditionalExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;

import tree.object.FunctionNode;
import tree.object.IFunctionNode;
import utils.SpecialCharacter;
import utils.Utils;

/**
 * Ex: "int c = (a>b) ? a : b;"------->"int c;if ( (a>b) ) c = a ; else c = b;"
 *
 * @author ducanhnguyen
 */
public class TernaryConvertNormalizer extends AbstractFunctionNormalizer
        implements IFunctionNormalizer {

    public TernaryConvertNormalizer() {
    }

    public TernaryConvertNormalizer(IFunctionNode functionNode) {
        this.functionNode = functionNode;
    }

    public static void main(String[] args) throws Exception {
        String[] test = new String[]{
                "int test(bool a){\nreturn a? 1:0;}",
                "void test(){\nint c = (a>b) ? a : b;\nc = a>b&&1==0 ? a : b;\n}",
                "int ConditionalTest(int x){\n	int t = x > 0 ? -1 : 1;\nif (t > 0)\n		return 1;"};

        FunctionNode fn = new FunctionNode();
        fn.setAST(Utils.getFunctionsinAST(test[0].toCharArray()).get(0));
        TernaryConvertNormalizer norm = new TernaryConvertNormalizer();
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
        List<IASTSimpleDeclaration> declarationASTs = Utils
                .getSimpleDeclarations(functionNode.getAST());

        for (IASTSimpleDeclaration declaration : declarationASTs)
            // Ex: bool greater = x > y
            normalizeSourcecode = normalizeStatement(declaration
                    .getRawSignature());

		/*
         * Analyze type 2
		 */
        IASTFunctionDefinition newFn = Utils.getFunctionsinAST(
                normalizeSourcecode.toCharArray()).get(0);
        List<ICPPASTBinaryExpression> binaryASTs = Utils
                .getBinaryExpressions(newFn);

        for (ICPPASTBinaryExpression binaryAST : binaryASTs)
            if (binaryAST.getOperator() == IASTBinaryExpression.op_assign) {
                IASTNode right = binaryAST.getOperand2();
                IASTNode left = binaryAST.getOperand1();
                if (right instanceof CPPASTConditionalExpression
                        && (left instanceof IASTIdExpression || left instanceof ICPPASTFieldReference))
					/*
					 * Ex: greater = x == y
					 */
                    normalizeSourcecode = normalizeStatement(binaryAST
                            .getRawSignature());
            }
    }

    public String normalizeStatement(String source) {
        Pattern p = Pattern.compile("(?<name>[^=\r\n]+)="
                + "(?<cond>[^?\r\n]+)\\?" + "(?<oTrue>[^:\r\n]+):"
                + "(?<oFalse>[^;\r\n]+)");
        Matcher m = p.matcher(source);
        StringBuffer sb = new StringBuffer(source.length());

        while (m.find()) {
            String stm = m.group(0) + SpecialCharacter.END_OF_STATEMENT;
            IASTTranslationUnit asts = null;
            try {
                asts = Utils.getIASTTranslationUnitforCpp(stm.toCharArray());
            } catch (Exception ex) {
                Logger.getLogger(TernaryConvertNormalizer.class.getName()).log(
                        Level.SEVERE, null, ex);
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
					/*
					 * Case 1
					 */
                    IASTNode typeVariable = firstChild.getChildren()[0];
                    IASTNode declaration = firstChild.getChildren()[1];
                    IASTNode nameVar = declaration.getChildren()[0];

                    String cond = m.group("cond"), oTrue = m.group("oTrue"), oFalse = m
                            .group("oFalse");

                    m.appendReplacement(sb, Matcher.quoteReplacement(String
                            .format("%s %s;if (%s) %s = %s; else %s = %s",
                                    typeVariable.getRawSignature(),
                                    nameVar.getRawSignature(), cond,
                                    nameVar.getRawSignature(), oTrue,
                                    nameVar.getRawSignature(), oFalse)));
                } else {
					/*
					 * Case 2
					 */
                    String name = m.group("name"), cond = m.group("cond"), oTrue = m
                            .group("oTrue"), oFalse = m.group("oFalse");
                    m.appendReplacement(sb, Matcher.quoteReplacement(String
                            .format("if (%s) %s = %s; else %s = %s", cond,
                                    name, oTrue, name, oFalse)));
                }
            } else {
                // nothing to do
            }
        }

        m.appendTail(sb);
        String normalizedDeclaration = sb.toString();
        normalizeSourcecode = normalizeSourcecode.replace(source,
                normalizedDeclaration);
        return normalizeSourcecode;
    }

}
