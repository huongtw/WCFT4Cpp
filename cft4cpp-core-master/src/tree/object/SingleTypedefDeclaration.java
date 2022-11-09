package tree.object;

import java.io.File;

import org.eclipse.cdt.core.dom.ast.IASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTPointer;

import config.Paths;
import parser.projectparser.ProjectParser;
import utils.search.Search;
import utils.search.TypedefNodeCondition;
import utils.tostring.DependencyTreeDisplayer;

/**
 * Represent single typedef declaration. Ex: typedef char char_t
 *
 * @author DucAnh
 */
public class SingleTypedefDeclaration extends CustomASTNode<IASTSimpleDeclaration> {

    public static void main(String[] args) {
        ProjectParser parser = new ProjectParser(new File(Paths.TSDV_R1_4), null);
        System.out.println(new DependencyTreeDisplayer(parser.getRootTree()).getTreeInString());
        SingleTypedefDeclaration n = (SingleTypedefDeclaration) Search.searchNodes(parser.getRootTree(),
                new TypedefNodeCondition(), "TypeDef.cpp" + File.separator + "MyIntPtr").get(0);
        System.out.println(n.getNewType());
        System.out.println(n.getOldName());
    }

    /**
     * Ex1: "typedef int MyIntPtr;"----->"MyIntPtr"
     * <p>
     * <p>
     * Ex1: "typedef int *MyIntPtr;"----->"MyIntPtr"
     */
    @Override
    public String getNewType() {
        return getAST().getDeclarators()[0].getName().toString();
    }

    /**
     * Ex: "typedef int MyIntPtr;"----->"int"
     */
    public String getOldName() {
        String oldName = getAST().getDeclSpecifier().getRawSignature();

        // Ex: "typedef int *MyIntPtr;"
        if (getAST().getDeclarators()[0].getChildren()[0] instanceof CPPASTPointer)
            oldName += "*";

        if (getAST().getDeclSpecifier() instanceof IASTElaboratedTypeSpecifier) {
            IASTElaboratedTypeSpecifier decl = (IASTElaboratedTypeSpecifier) getAST().getDeclSpecifier();
            /**
             * Trường hợp declSpecifier của class là phức tạp, VD "typedef class
             * thisinh SV"
             */
            if (decl.getStorageClass() == IASTElaboratedTypeSpecifier.k_struct)
                oldName = oldName.replaceAll("typedef\\s*class\\s*", "");
        }
        return oldName.replaceAll("^typedef\\s*", "");
    }

    @Override
    public String toString() {
        return getAST().getRawSignature();
    }
}
