package tree.object;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;

/**
 * Represent macro definitions. The macro may define in single line or multiple
 * lines. <br/>
 * <p>
 * <p>
 * Ex1: <b>#define MIN 100</b><br/>
 * <br/>
 * Ex2: <b>#define foo(x) x, "x"</b><br/>
 * means: foo(bar) ==> bar, "x"<br/>
 * <br/>
 * Ex3: <b>#define min(X, Y) ((X) < (Y) ? (X) : (Y))</b><br/>
 * <p>
 * means: <br/>
 * x = min(a, b); ==> x = ((a) < (b) ? (a) : (b)); <br/>
 * y = min(1, 2); ==> y = ((1) < (2) ? (1) : (2));<br/>
 * z = min(a + 28, *p); ==> z = ((a + 28) < (*p) ? (a + 28) : (*p));<br/>
 * <br/>
 * Ex4 (define in multiple lines - use \ as a line continuation escape
 * character.): <br/>
 * <b>#define MACRO(X,Y)</b> \ <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;( \<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;(cout << "1st arg is:" << (X) << endl), \ <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;(cout << "2nd arg is:" << (Y) << endl), \ <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;(cout << "3rd arg is:" << ((X) + (Y)) << endl),
 * \<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;)
 *
 * @author ducanhnguyen
 */
public class PreprocessorMacroDefinitionNode<N extends IASTPreprocessorMacroDefinition> extends Node {
    protected N AST;

    public N getAST() {
        return this.AST;
    }

    public void setAST(N aST) {
        this.AST = aST;
    }

    @Override
    public String toString() {
        return this.AST.getRawSignature();
    }
}
