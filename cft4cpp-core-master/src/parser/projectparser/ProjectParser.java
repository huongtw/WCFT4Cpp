package parser.projectparser;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import config.AbstractSetting;
import config.ISettingv2;
import config.Paths;
import normalizer.AbstractProjectParser;
import normalizer.MakefileOfDevCppNormalizer;
import normalizer.ParentReconstructor;
import tree.dependency.ExtendedDependencyGeneration;
import tree.dependency.IncludeHeaderDependencyGeneration;
import tree.dependency.SetterGetterDependencyGeneration;
import tree.object.INode;
import tree.object.IProcessNotify;
import tree.object.IProjectNode;
import tree.object.SourcecodeFileNode;
import tree.object.StructureNode;
import utils.Utils;
import utils.search.Search;
import utils.search.SourcecodeFileNodeCondition;
import utils.search.UnknownFileNodeCondition;
import utils.search.VariableNodeCondition;
import utils.tostring.DependencyTreeDisplayer;

public class ProjectParser extends AbstractProjectParser implements IProjectParser {
	final static Logger logger = Logger.getLogger(ProjectParser.class);

	public static void main(String[] args) throws Exception {
		ProjectParser projectParser = new ProjectParser(new File(Paths.STUDENT_MANAGEMENT), null);
		IProjectNode projectRoot = projectParser.getRootTree();
		System.out.println(new DependencyTreeDisplayer(projectRoot).getTreeInString());
	}

	public ProjectParser(File projectPath) {
		if (logger.isDebugEnabled())
			try {
				logger.debug("Parse " + projectPath.getCanonicalPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		this.projectPath = projectPath;
		notify = null;
	}

	public ProjectParser(File projectPath, IProcessNotify notify) {
		if (logger.isDebugEnabled())
			logger.debug("Parse " + projectPath);
		this.notify = notify;
		this.projectPath = projectPath;
	}

	private void findorCreateMakefile(INode root) {
		if (Utils.isWindows()) {

			switch (Paths.CURRENT_PROJECT.TYPE_OF_PROJECT) {
			case ISettingv2.PROJECT_ECLIPSE: {
				// create make file
				String eclipsePath = AbstractSetting.getValue(ISettingv2.ECLIPSE_PATH);
				String workspace = new File(Paths.CURRENT_PROJECT.ORIGINAL_PROJECT_PATH).getParent();
				String nameProject = new File(Paths.CURRENT_PROJECT.ORIGINAL_PROJECT_PATH).getName();
				String makefileContent = Utils.toQuote(eclipsePath)
						+ " -nosplash -application org.eclipse.cdt.managedbuilder.core.headlessbuild -data "
						+ Utils.toQuote(workspace) + " -build " + Utils.toQuote(nameProject);

				Paths.CURRENT_PROJECT.MAKEFILE_PATH = Paths.CURRENT_PROJECT.ORIGINAL_PROJECT_PATH + File.separator
						+ "MakefileCft4cpp.bat";
				Utils.writeContentToFile(makefileContent, Paths.CURRENT_PROJECT.MAKEFILE_PATH);

				// Set "chmod 777" for the make file
				new File(Paths.CURRENT_PROJECT.MAKEFILE_PATH).setExecutable(true);
				new File(Paths.CURRENT_PROJECT.MAKEFILE_PATH).setReadable(true);
				new File(Paths.CURRENT_PROJECT.MAKEFILE_PATH).setWritable(true);
			}

			case ISettingv2.PROJECT_DEV_CPP: {
				List<INode> makefileNodes = Search.searchNodes(root, new UnknownFileNodeCondition(),
						File.separator + IProjectLoader.MAKEFILE_IN_DEVCPP_SYMBOL);

				if (makefileNodes != null && makefileNodes.size() == 1) {
					Paths.CURRENT_PROJECT.MAKEFILE_PATH = makefileNodes.get(0).getAbsolutePath();

					// Normalize the Makefile.win of the given project
//					MakefileOfDevCppNormalizer makefileNorm = new MakefileOfDevCppNormalizer(
//							new File(Paths.CURRENT_PROJECT.MAKEFILE_PATH));
//					makefileNorm.normalize();
//					Utils.writeContentToFile(makefileNorm.getNormalizedSourcecode(),
//							Paths.CURRENT_PROJECT.MAKEFILE_PATH);
				} else
					logger.debug("Detect many file " + IProjectLoader.MAKEFILE_IN_DEVCPP_SYMBOL + ".win");
				break;
			}
			case ISettingv2.PROJECT_VISUALSTUDIO: {
				List<INode> makefileNodes = Search.searchNodes(root, new UnknownFileNodeCondition(),
						File.separator + IProjectLoader.MAKEFILE_IN_VISUAL_LEVEL1);
				makefileNodes.addAll(Search.searchNodes(root, new UnknownFileNodeCondition(),
						File.separator + IProjectLoader.MAKEFILE_IN_VISUAL_LEVEL2));

				Paths.CURRENT_PROJECT.MAKEFILE_PATH = makefileNodes.get(0).getAbsolutePath();
				break;
			}
			case ISettingv2.PROJECT_CUSTOMMAKEFILE: {
				Paths.CURRENT_PROJECT.MAKEFILE_PATH = Paths.CURRENT_PROJECT.CLONE_PROJECT_PATH + File.separator
						+ IProjectLoader.MAKEFILE_IN_PROJECT_SYMBOL;
				break;
			}
			default:
				break;
			}

		} else if (Utils.isUnix()) {
			switch (Paths.CURRENT_PROJECT.TYPE_OF_PROJECT) {
			case ISettingv2.PROJECT_ECLIPSE: {
				// create make file
				String eclipsePath = AbstractSetting.getValue(ISettingv2.ECLIPSE_PATH);
				String workspace = new File(Paths.CURRENT_PROJECT.ORIGINAL_PROJECT_PATH).getParent();
				String nameProject = new File(Paths.CURRENT_PROJECT.ORIGINAL_PROJECT_PATH).getName();
				String makefileContent = Utils.toQuote(eclipsePath)
						+ " -nosplash -application org.eclipse.cdt.managedbuilder.core.headlessbuild -data "
						+ Utils.toQuote(workspace) + " -build " + Utils.toQuote(nameProject);

				Paths.CURRENT_PROJECT.MAKEFILE_PATH = Paths.CURRENT_PROJECT.ORIGINAL_PROJECT_PATH + File.separator
						+ "MakefileCft4cpp";
				Utils.writeContentToFile(makefileContent, Paths.CURRENT_PROJECT.MAKEFILE_PATH);

				break;
			}
			case ISettingv2.PROJECT_CUSTOMMAKEFILE: {
				Paths.CURRENT_PROJECT.MAKEFILE_PATH = Paths.CURRENT_PROJECT.CLONE_PROJECT_PATH + File.separator
						+ IProjectLoader.MAKEFILE_IN_PROJECT_SYMBOL;
				break;
			}
			}
			// Set "chmod 777" for the make file
			if (new File(Paths.CURRENT_PROJECT.MAKEFILE_PATH).exists()) {
				new File(Paths.CURRENT_PROJECT.MAKEFILE_PATH).setExecutable(true);
				new File(Paths.CURRENT_PROJECT.MAKEFILE_PATH).setReadable(true);
				new File(Paths.CURRENT_PROJECT.MAKEFILE_PATH).setWritable(true);
			}
		}
		logger.debug("Make file path = " + Paths.CURRENT_PROJECT.MAKEFILE_PATH);
	}

	private void CpptoHeaderDependencyGeneration(INode root) {
		List<INode> sourcecodeFileNodes = Search.searchNodes(root, new SourcecodeFileNodeCondition());
		for (INode cppFileNode : sourcecodeFileNodes)
			new IncludeHeaderDependencyGeneration(cppFileNode);
	}

	/**
	 * Parse all source code file in the given project to expand the structure
	 * to method level
	 */
	private void expandTreeuptoMethodLevel(INode root) throws Exception {
		List<INode> sourcecodeNodes = Search.searchNodes(root, new SourcecodeFileNodeCondition());

		// Count the number of source code files
		int numOfSourcecodeFiles = 0;
		for (INode sourceCodeNode : sourcecodeNodes)
			if (sourceCodeNode instanceof SourcecodeFileNode)
				numOfSourcecodeFiles++;

		// Analyze source code files
		int count = 0;
		for (INode sourceCodeNode : sourcecodeNodes)
			if (sourceCodeNode instanceof SourcecodeFileNode) {
				count++;
				try {
					// logger.debug(sourceCodeNode.getAbsolutePath());
					SourcecodeFileNode fNode = (SourcecodeFileNode) sourceCodeNode;

					if (notify != null)
						notify.notify("[" + Math.round(100.0 * count / numOfSourcecodeFiles) + "%] "
								+ new File(fNode.getAbsolutePath()).getParentFile().getName() + File.separator
								+ fNode.getFile().getName());

					File dir = fNode.getFile();

					if (dir != null) {
						SourcecodeFileParser cppParser = new SourcecodeFileParser();
						cppParser.setSourcecodeNode((SourcecodeFileNode) sourceCodeNode);
						INode sourcecodeTreeRoot = cppParser.generateTree();
						fNode.setAST(cppParser.getTranslationUnit());

						for (INode sourcecodeItem : sourcecodeTreeRoot.getChildren()) {
							sourceCodeNode.getChildren().add(sourcecodeItem);
							sourcecodeItem.setParent(sourceCodeNode);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	}

	private void generateSetterandGetter(INode root) {
		List<INode> variableNodes = Search.searchNodes(root, new VariableNodeCondition());
		for (INode var : variableNodes)
			if (var.getParent() instanceof StructureNode)
				new SetterGetterDependencyGeneration(var);
	}

	@Override
	public IProjectNode getRootTree() {
		ProjectLoader loader = new ProjectLoader();
		loader.setIgnoreFolders(getIgnoreFolders());
		IProjectNode root = loader.load(projectPath);

		if (root != null) {
			try {
				expandTreeuptoMethodLevel(root);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				if (notify != null)
					notify.notify("Create header dependencies");
				CpptoHeaderDependencyGeneration(root);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				if (notify != null)
					notify.notify("Create structure dependencies");
				new ParentReconstructor(root);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (notify != null)
					notify.notify("Create extend dependencies");
				new ExtendedDependencyGeneration(root);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				generateSetterandGetter(root);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			findorCreateMakefile(root);

		}
		return root;
	}

}
