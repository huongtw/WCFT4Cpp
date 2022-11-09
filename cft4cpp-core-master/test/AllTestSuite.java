
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import cfg.CFGGenerationforBranchvsStatementCoverageTest;
import cfg.CFGGenerationforSubConditionCoverageTest;
import cfg.testpath.PossibleTestpathGenerationTest;
import externalvariable.ExternalVariableDetecterTest;
import normalizer.Cpp11ClassNormalizerTest;
import normalizer.FunctionNameNormalizerTest;
import normalizer.VariableTypeNormalizerTest;
import parser.TypedefNodeNameTest;
import projectparser.ProjectParserTest;
import testdatagen.Tsdvr12_StorageKeyword_Test;
import testdatagen.Tsdvr12_ViewSource_Test;
import testdatagen.Tsdvr13_BasicCpp11_Test;
import testdatagen.Tsdvr14_Age_Test;
import testdatagen.Tsdvr14_Asaxxx_Test;
import testdatagen.Tsdvr14_Cities_Test;
import testdatagen.Tsdvr14_Complex_Test;
import testdatagen.Tsdvr14_NonLinear_Test;
import testdatagen.Tsdvr14_PassArgs_Test;
import testdatagen.Tsdvr14_Typedef_Test;
import testdatagen.Tsdvr14_r85_Test;
import testdatagen.Tsdvr1_ArrayType_Test;
import testdatagen.Tsdvr1_BasicType_Test;
import testdatagen.Tsdvr1_ClassType_Test;
import testdatagen.Tsdvr1_EnumType_Test;
import testdatagen.Tsdvr1_Namespace_Test;
import testdatagen.Tsdvr1_Op_Test;
import testdatagen.Tsdvr1_PointerType_Test;
import testdatagen.Tsdvr1_Struct_Test;
import testdatagen.Vnu_BTL_Test;
import testdatagen.Vnu_Basic_Test;
import testdatagen.Vnu_Combined_Test;
import testdatagen.Vnu_Journal_Test;
import testdatagen.Vnu_Namespace_Test;
import testdatagen.Vnu_Pointer_Test;
import testdatagen.Vnu_Pratical_Test;
import testdatagen.Vnu_Sample1_Test;
import testdatagen.Vnu_SwitchCase_Test;
import testdatagen.se.solver.Z3SolutionParserTest;

@RunWith(Suite.class)

@Suite.SuiteClasses({ 
//	CFGGenerationforBranchvsStatementCoverageTest.class, //
//		CFGGenerationforSubConditionCoverageTest.class, //
//		PossibleTestpathGenerationTest.class, //
//		ExternalVariableDetecterTest.class, //
//		Cpp11ClassNormalizerTest.class, //
//		FunctionNameNormalizerTest.class, //
//		VariableTypeNormalizerTest.class, //
//		TypedefNodeNameTest.class, //
//		ProjectParserTest.class, //
//		Z3SolutionParserTest.class, //
//		// VNU TESTS
//		Vnu_Basic_Test.class, //
//		Vnu_BTL_Test.class, //
//		Vnu_Combined_Test.class, //
		Vnu_Journal_Test.class //
//		Vnu_Namespace_Test.class, //
//		Vnu_Pointer_Test.class, //
//		Vnu_Pratical_Test.class, //
//		Vnu_Sample1_Test.class, //
//		Vnu_SwitchCase_Test.class, //
//		// TSDV TESTS
//		Tsdvr1_ArrayType_Test.class, //
//		Tsdvr1_BasicType_Test.class, //
//		Tsdvr1_ClassType_Test.class, //
//		Tsdvr1_EnumType_Test.class, //
//		Tsdvr1_Namespace_Test.class, //
//		Tsdvr1_Op_Test.class, //
//		Tsdvr1_PointerType_Test.class, //
//		Tsdvr1_Struct_Test.class, //
//		Tsdvr12_StorageKeyword_Test.class, //
//		Tsdvr12_ViewSource_Test.class, //
//		Tsdvr13_BasicCpp11_Test.class, //
//		Tsdvr14_Age_Test.class, //
//		Tsdvr14_Asaxxx_Test.class, //
//		Tsdvr14_Cities_Test.class, //
//		Tsdvr14_Complex_Test.class, //
//		Tsdvr14_NonLinear_Test.class, //
//		Tsdvr14_PassArgs_Test.class, //
//		Tsdvr14_r85_Test.class, //
//		Tsdvr14_Typedef_Test.class 
		


})

public class AllTestSuite {
	@Rule
	public Timeout globalTimeout = Timeout.seconds(12);
}