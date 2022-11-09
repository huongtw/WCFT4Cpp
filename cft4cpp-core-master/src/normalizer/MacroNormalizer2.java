package normalizer;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ibm.icu.util.Calendar;

import config.AbstractSetting;
import config.ISettingv2;
import config.Paths;
import parser.projectparser.ProjectParser;
import parser.projectparser.SourcecodeFileParser;
import testdatagen.AbstractTestdataGeneration;
import tree.object.IFunctionNode;
import tree.object.INode;
import utils.Utils;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

/**
 * Get the normalized source code after replacing macro
 */
public class MacroNormalizer2 extends AbstractFunctionNormalizer implements IFunctionNormalizer {
	public static final String TEMPORRY_FILE = "tmpxxxxxxxxxxxxx.cpp";
	final static Logger logger = Logger.getLogger(MacroNormalizer2.class);

	public MacroNormalizer2() {
	}

	public MacroNormalizer2(IFunctionNode functionNode) {
		this.functionNode = functionNode;
	}

	public static void main(String[] args) {
		ProjectParser parser = new ProjectParser(new File(Paths.TSDV_R1_4));
		IFunctionNode function = (IFunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
				File.separator + "psi_values(int*,double*,double*)").get(0);

		System.out.println(function.getAST().getRawSignature());
		MacroNormalizer2 normalizer = new MacroNormalizer2(function);
		normalizer.normalize();

		System.out.println(normalizer.getTokens());
		System.out.println(normalizer.getNormalizedSourcecode());
	}

	@Override
	public void normalize() {
		Date startTime = Calendar.getInstance().getTime();
		if (functionNode.getFnMacroNormalizer() == null) {

			String normSrc = replaceMacro();

			String parentPath = functionNode.getParent().getAbsolutePath();
			String mcppOutputFile = "";
			switch (Paths.CURRENT_PROJECT.TYPE_OF_PROJECT) {

			case ISettingv2.PROJECT_ECLIPSE: {
				mcppOutputFile = Paths.CURRENT_PROJECT.ORIGINAL_PROJECT_PATH + File.separator
						+ MacroNormalizer2.TEMPORRY_FILE;
				break;
			}
			case ISettingv2.PROJECT_DEV_CPP:
			case ISettingv2.PROJECT_CUSTOMMAKEFILE:
			case ISettingv2.PROJECT_VISUALSTUDIO: {
				mcppOutputFile = Paths.CURRENT_PROJECT.CLONE_PROJECT_PATH + File.separator
						+ MacroNormalizer2.TEMPORRY_FILE;
				break;
			}
			}
			logger.debug("Mcpp file: " + mcppOutputFile);

			Utils.deleteFileOrFolder(new File(mcppOutputFile));
			if (Utils.isUnix()) {
				new File(mcppOutputFile).setExecutable(true);
				new File(mcppOutputFile).setReadable(true);
				new File(mcppOutputFile).setReadable(true);
			}
			Utils.writeContentToFile(normSrc, mcppOutputFile);

			SourcecodeFileParser parser = new SourcecodeFileParser();
			INode root = null;
			try {
				root = parser.parseSourcecodeFile(new File(mcppOutputFile));
				/*
				 * Find the corresponding function in the normalized tree
				 */
				String normalizedFunction = functionNode.getAST().getRawSignature();
				List<INode> fnNodes = Search.searchNodes(root, new FunctionNodeCondition());
				root.setAbsolutePath(parentPath);

				List<IFunctionNode> candidateFnNodes = getCandidateFunctions(fnNodes, parentPath);

				IFunctionNode bestMatchFunction = chooseTheBestMatchFunction(candidateFnNodes);
				if (bestMatchFunction != null) {
					normalizedFunction = bestMatchFunction.getAST().getRawSignature();
					normalizeSourcecode = normalizedFunction;
				} else
					normalizeSourcecode = functionNode.getAST().getRawSignature();
			} catch (Exception e) {
				e.printStackTrace();
				normalizeSourcecode = functionNode.getAST().getRawSignature();
			} finally {
				Utils.deleteFileOrFolder(new File(mcppOutputFile));

			}
			functionNode.setFnMacroNormalizer(this);
		} else
			normalizeSourcecode = functionNode.getFnMacroNormalizer().getNormalizedSourcecode();

		Date end = Calendar.getInstance().getTime();
		AbstractTestdataGeneration.macroNormalizationTime += end.getTime() - startTime.getTime();
	}

	private IFunctionNode chooseTheBestMatchFunction(List<IFunctionNode> candidateFnNodes) {
		if (candidateFnNodes.size() == 1)
			return candidateFnNodes.get(0);
		else {
			// Filter 1
			for (IFunctionNode candidateFnNode : candidateFnNodes)
				if (candidateFnNode.getNewType().equals(functionNode.getNewType()))
					return candidateFnNode;
			// Filter 2
			for (IFunctionNode candidateFnNode : candidateFnNodes)
				if (candidateFnNode.getFullName().equals(functionNode.getFullName()))
					return candidateFnNode;
		}
		return null;
	}

	/**
	 * Get candidate functions that may be corresponding to the given function
	 *
	 * @param fnNodes
	 * @param parentPath
	 * @return
	 */
	private List<IFunctionNode> getCandidateFunctions(List<INode> fnNodes, String parentPath) {
		List<IFunctionNode> candidateFnNodes = new ArrayList<>();

		for (INode fnNode : fnNodes) {
			IFunctionNode fn = (IFunctionNode) fnNode;

			/*
			 * Get function has the same name, the same number of passing variables, the
			 * same of parent path
			 */
			if (fn.getSimpleName().equals(functionNode.getSimpleName())
					&& fn.getParent().getAbsolutePath().equals(parentPath)
					&& fn.getPassingVariables().size() == functionNode.getPassingVariables().size())
				candidateFnNodes.add(fn);
		}
		return candidateFnNodes;
	}

	private String replaceMacro() {
		String mcppPath = AbstractSetting.getValue(ISettingv2.MCPP_EXE_PATH);
		String mcppBinFolder = new File(mcppPath).getParent();

		McppNormalizer norm = new McppNormalizer();
		norm.setCurrentFile(Utils.getSourcecodeFile(functionNode).getAbsolutePath());
		norm.setMcppBinFolder(mcppBinFolder);
		norm.setExternalIncludedFolder(Utils.getProjectNode(functionNode).getAbsolutePath());
		norm.setMcppPath(mcppPath);

		norm.normalize();
		return norm.getNormalizedSourcecode();
	}
}
