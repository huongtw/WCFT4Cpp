package _example.nameresolution;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.DefaultLogService;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.ScannerInfo;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPFunction;

import utils.Utils;

public class CDTFunctionCallTest {
	private IASTTranslationUnit translationUnit;

	public static void main(String[] args) throws Exception {
		String test0 = "../cft4cpp-core/data-test/samvu/1_Load_Tree.cpp";
		CDTFunctionCallTest cdt = new CDTFunctionCallTest();
		cdt.parseSourcecodeFile(new File(test0));
	}

	public void parseSourcecodeFile(File filePath) throws Exception {
		translationUnit = getIASTTranslationUnit(Utils.readFileContent(filePath.getAbsolutePath()).toCharArray());

		ASTVisitor visitor = new ASTVisitor() {

			@Override
			public int visit(IASTDeclaration declaration) {
				System.out.println("\n------\nIn declaration: " + declaration.getRawSignature());
				return ASTVisitor.PROCESS_CONTINUE;
			}

			@Override
			public int visit(IASTName name) {
				System.out.println("\nast name:" + name.getRawSignature());
				IBinding binding = name.resolveBinding();

				if (binding instanceof CPPFunction) {
					CPPFunction bindingCast = (CPPFunction) binding;
					IASTNode definition = bindingCast.getDefinition();
					System.out
							.println("Resolve binding: Definition: [" + definition.getParent().getRawSignature() + "]");
				}
				return ASTVisitor.PROCESS_CONTINUE;
			}

		};

		visitor.shouldVisitDeclarations = true;
		visitor.shouldVisitNames = true;
		translationUnit.accept(visitor);
	}

	private IASTTranslationUnit getIASTTranslationUnit(char[] code) throws Exception {
		FileContent fc = FileContent.create("", code);
		Map<String, String> macroDefinitions = new HashMap<>();
		String[] includeSearchPaths = new String[0];
		IScannerInfo si = new ScannerInfo(macroDefinitions, includeSearchPaths);
		IncludeFileContentProvider ifcp = IncludeFileContentProvider.getEmptyFilesProvider();
		IIndex idx = null;
		int options = ILanguage.OPTION_IS_SOURCE_UNIT;
		IParserLogService log = new DefaultLogService();
		return GPPLanguage.getDefault().getASTTranslationUnit(fc, si, ifcp, idx, options, log);
	}

}