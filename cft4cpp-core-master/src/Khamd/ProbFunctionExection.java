package Khamd;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cfg.object.ICfgNode;
import config.FunctionConfig;
import config.ParameterBound;
import config.Paths;
import parser.makefile.object.GPlusPlusExeCondition;
import parser.projectparser.ProjectParser;
import testdatagen.FunctionExecution;
import tree.object.FunctionNode;
import utils.Utils;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

public class ProbFunctionExection extends FunctionExecution{
	
	private Graph graph;
	
	public ProbFunctionExection(Graph graph, String pathToZ3, String pathToMingw32, String pathToGCC, String pathToGPlus) throws Exception {
		super(pathToZ3,pathToMingw32,pathToGCC,pathToGPlus);
		this.graph = graph;
		
	}
	
	public List<ICfgNode> getTestPath(String preparedInput) {
		String testedProject = graph.getPathToFile();
		try {
			File clone = Utils.copy(testedProject);
			Paths.CURRENT_PROJECT.CLONE_PROJECT_PATH = clone.getAbsolutePath();
			ProjectParser parser = new ProjectParser(clone);
			FunctionConfig config = new FunctionConfig();
			config.setCharacterBound(new ParameterBound(32, 100));
			config.setIntegerBound(new ParameterBound(0, 100));
			
			FunctionNode testedFunction = (FunctionNode) Search
					.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), graph.getFunctionNode().getName()).get(0);
			
			/**
			 * Find test path given a test case
			 */

			this.setTestedFunction(testedFunction);
			this.setPreparedInput(preparedInput);
			this.setClonedProject(clone.getCanonicalPath());
			this.setCFG(this.graph.getCfg());
			return this.analyze(this.getTestedFunction(), this.getPreparedInput());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		
	}
	
}
