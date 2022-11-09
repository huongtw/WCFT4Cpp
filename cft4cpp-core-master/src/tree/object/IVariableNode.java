package tree.object;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTNode;

public interface IVariableNode extends INode {

    List<String[]> getAllAttributes(INode n, int level);

    IASTDeclarator getASTDecName();

    IASTDeclSpecifier getASTType();

    /**
     * Ex1, const int & a: <br/>
     * <p>
     * core type: int&<br/>
     * <p>
     * raw type: const int&<br/>
     * <p>
     * Ex1, double** a<br/>
     * <p>
     * core type: double<br/>
     * raw type: double**<br/>
     */
    String getCoreType();

    void setCoreType(String coreType);

    FunctionNode getGetterNode();

    int getLevelOfPointer();

    void setLevelOfPointer(int levelOfPointer);

    /**
     * Get the full path of type of the current variable <br/>
     * Ex1: if variable x is put in class A, then return A::x<br/>
     * <p>
     * Ex2:<br/>
     * <p>
     * <p>
     * <pre>
     * namespace ns1{
     * 		namespace ns2{
     * 			class SinhVien{...}
     * 			SinhVien sv;
     *        }
     * }
     * </pre>
     * <p>
     * The type of <i>sv</i> is ns1::ns2::SinhVien instead of SinhVien
     *
     * @return
     */
    String getFullType();

    /**
     * Ex1, const int & a: <br/>
     * <p>
     * core type: int&<br/>
     * <p>
     * raw type: const int&<br/>
     * <p>
     * Ex1, double** a<br/>
     * <p>
     * core type: double<br/>
     * raw type: double**<br/>
     */
    String getRawType();

    void setRawType(String rawType);

    /**
     * Get real type of variable
     * <p>
     * Ex: "typedef int XXX; XXX a"------------> real type of a = "int" instead
     * of "XXX"
     *
     * @return
     */
    String getRealType();

    String getReducedRawType();

    void setReducedRawType(String reducedRawType);

    FunctionNode getSetterNode();

    int getSizeOfArray();

    boolean isPrivate();

    void setPrivate(boolean isPrivate);

    boolean isReference();

    void setReference(boolean isReference);

    /**
     * Get core type of variable. Ex: <br/>
     * type = "const int*" -> primitive type <br/>
     * type = "const int&" -> primitive type <br/>
     * type = "Student*" -> Student node <br/>
     * type = "Student[]*" -> Student node
     *
     * @return
     */
    INode resolveCoreType();

    void setAST(IASTNode aST);

    void setCoreType();

    void setName();

    void setRawType();

    void setReducedRawType();

    /**
     * Return true if the variable contains "extern" storage, e.g., "extern int
     * MY_MAX_VALUE;"
     *
     * @return
     */
    boolean isExtern();

    /**
     * Get value of variable in ASTNode. Ex: consider "int x = 1+y;". This
     * method return ASTNode corresponding to its initialization "= 1+y"
     *
     * @return
     */
    IASTInitializer getInitializer();
}