package testdatagen.fastcompilation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompoundStatement;

import config.Paths;
import instrument.FunctionInstrumentationForStatementvsBranch_Marker;
import instrument.IFunctionInstrumentationGeneration;
import parser.projectparser.ProjectParser;
import testdatagen.testdataexec.ITestdriverGeneration;
import testdatagen.testdataexec.TestdriverGenerationforCpp;
import testdatagen.testdatainit.VariableTypes;
import tree.object.IFunctionNode;
import tree.object.INode;
import tree.object.IVariableNode;
import tree.object.StructureNode;
import utils.SpecialCharacter;
import utils.Utils;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

/**
 * Generate test driver for function put in an .cpp file
 *
 * @author ducanhnguyen
 */
public class FastTestdriverGenerationforCpp extends TestdriverGenerationforCpp {
	final static Logger logger = Logger.getLogger(FastTestdriverGenerationforCpp.class);

	@Override
	public void generate() throws Exception {
		final String oldContent = Utils.readFileContent(Utils.getSourcecodeFile(functionNode).getAbsolutePath());

		String loadingTestdataSourcecode = Utils.readFileContent(
				new File(Paths.CURRENT_PROJECT.LOCAL_FOLDER).getCanonicalPath() + File.separator + "CPP_lib.cpp");
		loadingTestdataSourcecode = loadingTestdataSourcecode.replace(STRUCTURE_VARIABLES_LOCATION_MARKER,
				createStructureSourcecode(functionNode));

		String instrumentedFunction = generateInstrumentedFunction();
		instrumentedFunction = removeStaticInFunctionDefinition(instrumentedFunction);

		// is this function in unname(annonymous) namespace ?
		if (getFunction().isChildrenOfUnnameNamespace()) {
			instrumentedFunction = "namespace {" + instrumentedFunction + "}";
		}
		final String main = generateMain(initialization, functionNode);

		final String testDriver = "writeContentToFile(\"" + Paths.CURRENT_PROJECT.CURRENT_TESTDRIVER_EXECUTION_PATH
				+ "\", build);";

		final String append = loadingTestdataSourcecode + "\r" + ITestdriverGeneration.CPP.MARK_BEGIN + testDriver
				+ ITestdriverGeneration.CPP.MARK_END + "\r" + "\r" + instrumentedFunction + "\r" + main;

		this.testDriver = oldContent + append;

	}

	private String generateMain(String values, INode testedFunction) {
		final String loadData = "using namespace utils;Elements data = readContentFromFile(" + "\""
				+ Paths.CURRENT_PROJECT.TESTDATA_INPUT_FILE_PATH + "\");";
		return "int main(){" + ITestdriverGeneration.CPP.START_CATCH + loadData + values + functionCall
				+ ITestdriverGeneration.CPP.END_CATCH + "return 0;}";
	}

	@Override
	protected String generateInstrumentedFunction() {
		String instrumentedFunction = "";
//		IFunctionInstrumentationGeneration fnInstrumentation = new AvoidingErrorFunctionInstrumentation(getFunction());
		IFunctionInstrumentationGeneration fnInstrumentation = new FunctionInstrumentationForStatementvsBranch_Marker();
		fnInstrumentation.setFunctionNode(getFunction());
		
		if (fnInstrumentation != null) {
			instrumentedFunction = fnInstrumentation.generateInstrumentedFunction();
//			instrumentedFunction = limitNumberofRecursiveCalls(instrumentedFunction);
//			instrumentedFunction = limitNumberofLoop(instrumentedFunction);

			// logger.debug("Instrument: \n" + instrumentedFunction);
		}
		return instrumentedFunction;
	}

	private String limitNumberofLoop(String function) {
		IASTFunctionDefinition ast = Utils.getFunctionsinAST(function.toCharArray()).get(0);

		// Get all loops
		List<IASTStatement> loops = new ArrayList<>();
		ASTVisitor astVisitor = new ASTVisitor() {
			@Override
			public int visit(IASTStatement statement) {
				if (statement instanceof IASTDoStatement || statement instanceof IASTWhileStatement
						|| statement instanceof IASTForStatement)
					loops.add(statement);
				return ASTVisitor.PROCESS_CONTINUE;
			}
		};
		astVisitor.shouldVisitStatements = true;
		ast.accept(astVisitor);

		// Add source code to avoid infinite loop/too large iterations
		int[] insertPoints = new int[loops.size()];
		for (int i = loops.size() - 1; i >= 0; i--) {
			IASTStatement loop = loops.get(i);
			if (loop instanceof IASTDoStatement) {
				IASTDoStatement cast = (IASTDoStatement) loop;
				if (cast.getBody() instanceof ICPPASTCompoundStatement) {
					int insertPoint = cast.getBody().getChildren()[0].getFileLocation().getNodeOffset();
					insertPoints[i] = insertPoint;
				} else {
					int insertPoint = cast.getBody().getFileLocation().getNodeOffset();
					insertPoints[i] = insertPoint;
				}

			} else if (loop instanceof IASTWhileStatement) {
				IASTWhileStatement cast = (IASTWhileStatement) loop;
				if (cast.getBody() instanceof ICPPASTCompoundStatement) {
					int insertPoint = cast.getBody().getChildren()[0].getFileLocation().getNodeOffset();
					insertPoints[i] = insertPoint;
				} else {
					int insertPoint = cast.getBody().getFileLocation().getNodeOffset();
					insertPoints[i] = insertPoint;
				}

			} else if (loop instanceof IASTForStatement) {
				IASTForStatement cast = (IASTForStatement) loop;
				if (cast.getBody() instanceof ICPPASTCompoundStatement) {
					int insertPoint = cast.getBody().getChildren()[0].getFileLocation().getNodeOffset();
					insertPoints[i] = insertPoint;
				} else {
					int insertPoint = cast.getBody().getFileLocation().getNodeOffset();
					insertPoints[i] = insertPoint;
				}
			}
		}
		StringBuffer bufFunction = new StringBuffer(function);
		for (int i = insertPoints.length - 1; i >= 0; i--) {
			bufFunction.insert(insertPoints[i], "if (++iteration[" + i + "]>=MAX_LOOP_ITERATIONS) exit(0);\n");
		}
		return bufFunction.toString();
	}

	/**
	 * In case a function contains recursive call, we need to limit the maximum
	 * number of recursive calls.
	 * 
	 * Imagine that a function calls itself up to one thousand times, what would
	 * happen? :))
	 * 
	 * @param function
	 * @return
	 */
	protected String limitNumberofRecursiveCalls(String function) {
		String body = function.substring(function.indexOf("{") + 1);
		boolean containRecursiveCall = body.contains(functionNode.getSimpleName() + "(");
		if (containRecursiveCall) {
			String nameFunction = function.substring(0, function.indexOf("{") + 1);
			function = nameFunction + "if (++NUM_RECURSIVE>MAX_RECURSIVE) exit(0);" + body;
		}
		return function;
	}

	private String createStructureSourcecode(IFunctionNode functionNode) {
		String structureReaderCode = "";
		for (IVariableNode var : functionNode.getArguments())
			if (var.resolveCoreType() instanceof StructureNode) {
				StructureNode cast = (StructureNode) var.resolveCoreType();
				// Ex: "SinhVien findStructureSinhVienByName(string nameStructure) {"
				structureReaderCode += cast.getName() + " findStructure" + cast.getName()
						+ "ByName(string nameStructure) {" + SpecialCharacter.LINE_BREAK;

				// Ex: "SinhVien structure;"
				structureReaderCode += SpecialCharacter.TAB + cast.getName() + " structure;"
						+ SpecialCharacter.LINE_BREAK;
				structureReaderCode += addStructureVariablesReaderCode(var, "structure.", "nameStructure+\".")
						+ SpecialCharacter.LINE_BREAK + SpecialCharacter.LINE_BREAK;

				structureReaderCode += SpecialCharacter.TAB + "return structure;" + SpecialCharacter.LINE_BREAK;
				structureReaderCode += "}" + SpecialCharacter.LINE_BREAK;
			}
		return structureReaderCode;
	}

	/**
	 * Each structure variable has it own source code for loading values.
	 * 
	 * @return
	 */
	protected String addStructureVariablesReaderCode(IVariableNode var, String prefixNameofVariable,
			String searchingName) {
		String structureVariableReader = "";
		/**
		 * Consider this code:
		 * 
		 * <pre>
		 * struct Others;
				struct SinhVien {
					int age;
					char* name;
					Others* other;
				};
				
				struct Others {
					char* eeee;
				};
		 * </pre>
		 */
		if (var.resolveCoreType() instanceof StructureNode) {
			StructureNode cast = (StructureNode) var.resolveCoreType();
			// Generate source code for reading attributes
			for (IVariableNode attribute : cast.getAttributes())

				if (VariableTypes.isBasic(attribute.getRawType()))
					// Ex: "structure.age = findValueByName<int>(nameStructure + ".age");"
					// type of structure.age is integer
					structureVariableReader += SpecialCharacter.TAB + prefixNameofVariable + attribute.getName()
							+ " = findValueByName<" + attribute.getCoreType() + ">(" + searchingName
							+ attribute.getName() + "\");" + SpecialCharacter.LINE_BREAK;

				else if (VariableTypes.isNumOneLevel(attribute.getRawType()))
					structureVariableReader += SpecialCharacter.TAB + prefixNameofVariable + attribute.getName()
							+ " = findOneDimensionOrLevelBasicByName<" + attribute.getCoreType()
							+ ">(nameStructure + \"." + attribute.getName() + "\", DEFAULT_VALUE_FOR_NUMBER);"
							+ SpecialCharacter.LINE_BREAK;

				else if (VariableTypes.isNumOneDimension(attribute.getRawType())) {
					// Example:
					// char* structure_name = findOneDimensionOrLevelBasicByName<char>
					// (nameStructure+".name", DEFAULT_VALUE_FOR_CHARACTER);
					// for (int i = 0; i <= 20; i++)
					// structure.name[i] = structure_name[i];
					String tmpName = (prefixNameofVariable + attribute.getName()).replace(".", "_").replace("[", "_")
							.replace("]", "_").replace("->", "_") + new java.util.Random().nextInt(10000);

					structureVariableReader += SpecialCharacter.TAB + attribute.getCoreType() + "* " + tmpName
							+ " = findOneDimensionOrLevelBasicByName<" + attribute.getCoreType()
							+ ">(nameStructure + \"." + attribute.getName() + "\", DEFAULT_VALUE_FOR_NUMBER);"
							+ SpecialCharacter.LINE_BREAK;

					structureVariableReader += "for(int xxx=0;xxx<" + attribute.getSizeOfArray() + ";xxx++) {"
							+ prefixNameofVariable + attribute.getName() + "[xxx]=" + tmpName + "[xxx]"
							+ SpecialCharacter.END_OF_STATEMENT + "}";

				} else if (VariableTypes.isChOneLevel(attribute.getRawType()))
					// Ex: "structure.name = findOneDimensionOrLevelBasicByName<char>(nameStructure
					// +
					// ".name", DEFAULT_VALUE_FOR_CHARACTER);"
					structureVariableReader += SpecialCharacter.TAB + prefixNameofVariable + attribute.getName()
							+ " = findOneDimensionOrLevelBasicByName<" + attribute.getCoreType() + ">(" + searchingName
							+ attribute.getName() + "\", DEFAULT_VALUE_FOR_CHARACTER);" + SpecialCharacter.LINE_BREAK;
				else if (VariableTypes.isChOneDimension(attribute.getRawType())) {

					// Example:
					// char* structure_name = findOneDimensionOrLevelBasicByName<char>
					// (nameStructure+".name", DEFAULT_VALUE_FOR_CHARACTER);
					// for (int i = 0; i <= 20; i++)
					// structure.name[i] = structure_name[i];
					String tmpName = (prefixNameofVariable + attribute.getName()).replace(".", "_").replace("[", "_")
							.replace("]", "_").replace("->", "_") + new java.util.Random().nextInt(10000);

					structureVariableReader += SpecialCharacter.TAB + attribute.getCoreType() + "* " + tmpName
							+ " = findOneDimensionOrLevelBasicByName<" + attribute.getCoreType()
							+ ">(nameStructure + \"." + attribute.getName() + "\", DEFAULT_VALUE_FOR_CHARACTER);"
							+ SpecialCharacter.LINE_BREAK;

					structureVariableReader += "for(int xxx=0;xxx<" + attribute.getSizeOfArray() + ";xxx++) {"
							+ prefixNameofVariable + attribute.getName() + "[xxx]=" + tmpName + "[xxx]"
							+ SpecialCharacter.END_OF_STATEMENT + "}";

				} else
				// If the core type of the attribute is a structure, and its current type is one
				// level pointer.
				// Ex: attribute named "other" in class "SinhVien"
				if (attribute.resolveCoreType() instanceof StructureNode
						&& VariableTypes.isOneLevel(attribute.getRawType())) {
					// Ex: "structure.other =
					// findOneDimensionOrLevelStructureByName<Others>(nameStructure + ".other");"
					String attributeName = prefixNameofVariable + attribute.getName();
					structureVariableReader += SpecialCharacter.TAB + attributeName
							+ " = findOneDimensionOrLevelStructureByName<" + attribute.getCoreType() + ">("
							+ searchingName + attribute.getName() + "\");" + SpecialCharacter.LINE_BREAK;

					structureVariableReader += SpecialCharacter.TAB + "if (" + attributeName + " != NULL) {"
							+ SpecialCharacter.LINE_BREAK;
					// RECENT_STRUCTURE_SIZE store the latest size of structure pointer
					structureVariableReader += "for (int xxx=0;xxx<RECENT_STRUCTURE_SIZE;xxx++){";
					String newPrefixofNameVariable = prefixNameofVariable + attribute.getName() + "[xxx]" + ".";
					String tmpSearchingName = prefixNameofVariable.replaceFirst("structure", "nameStructure+\"")
							+ attribute.getName() + "[\"+integer_to_string(xxx)+\"]" + ".";
					structureVariableReader += addStructureVariablesReaderCode(attribute, newPrefixofNameVariable,
							tmpSearchingName);
					structureVariableReader += "}";

					structureVariableReader += SpecialCharacter.TAB + "}" + SpecialCharacter.LINE_BREAK;

				} else
					// For types but basic types, ignore it in this version.
					structureVariableReader += SpecialCharacter.TAB + "//dont support to set value of " + cast.getName()
							+ "." + attribute.getName() + SpecialCharacter.LINE_BREAK;
		}
		return structureVariableReader;
	}

	public static final String STRUCTURE_VARIABLES_LOCATION_MARKER = "//add structure reader here";

	public static void main(String[] args) throws Exception {
		ProjectParser parser = null;
		FastTestdriverGenerationforCpp gen = null;
		IFunctionNode function = null;

		parser = new ProjectParser(new File(Paths.COMBINED_STATIC_AND_DYNAMIC_GENERATION));
		function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "class_test1(SinhVien)").get(0);
		gen = new FastTestdriverGenerationforCpp();
		gen.setTestedFunction(function);
		gen.generate();
		logger.debug(gen.getCompleteSourceFile());
	}
}
