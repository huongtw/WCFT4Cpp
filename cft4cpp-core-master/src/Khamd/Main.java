package Khamd;

import config.Paths;
import normalizer.FunctionNormalizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.List;

import org.fife.ui.rsyntaxtextarea.modes.GroovyTokenMaker;

import cfg.CFGGenerationforSubConditionCoverage;
import cfg.ICFG;
import cfg.object.ICfgNode;
import cfg.testpath.FullTestpath;
import cfg.testpath.IFullTestpath;
import cfg.testpath.PartialTestpath;
import cfg.testpath.PossibleTestpathGeneration;
import parser.projectparser.ProjectParser;
import testdatagen.FunctionExecution;
import testdatagen.se.IPathConstraints;
import testdatagen.se.ISymbolicExecution;
import testdatagen.se.Parameter;
import testdatagen.se.PathConstraint;
import testdatagen.se.PathConstraints;
import testdatagen.se.SymbolicExecution;
import testdatagen.se.solver.RunZ3OnCMD;
import testdatagen.se.solver.SmtLibGeneration;
import testdatagen.se.solver.Z3SolutionParser;
import tree.object.FunctionNode;
import tree.object.IFunctionNode;
import tree.object.INode;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

public class Main {
	public static String pathToZ3 ="D:\\cft4cpp-core\\local\\z3\\bin\\z3.exe";
	public static String pathToMingw32 = "D:\\program files\\Dev-Cpp\\MinGW64\\bin\\mingw32-make.exe";
	public static String pathToGCC = "D:\\program files\\Dev-Cpp\\MinGW64\\bin\\gcc.exe";
	public static String pathToGPlus = "D:\\program files\\Dev-Cpp\\MinGW64\\bin\\g++.exe";
	public static String pathToConstraint = "D:\\cft4cpp-core\\myConstraint.smt2";
	public static SmtLibGeneration smt = new SmtLibGeneration();
	
	public static void main(String[] args) {
		
		Main HMM = new Main();
		int epoch = 1;
		try {
			int maxIterations= 1;
			int randomTestPath = 0;
			Graph graph = HMM.createGraph(Paths.TSDV_R1_2, "forTest(int)", maxIterations);
			graph.addConstraint();
			String solution ;
			FunctionExecution functionExection = new ProbFunctionExection(graph,pathToZ3,pathToMingw32,pathToGCC,pathToGPlus);
			int pathNumber = graph.getNewPath();
			do {
				
				solution = HMM.getSolutionInRandomPath(graph, pathNumber);
				if(solution=="") {
					System.out.println("no test case");
					pathNumber=graph.getNewPath();
					continue;
				}
				ProbTestPath trackedPath = graph.getFullProbTestPaths().get(pathNumber);
				List<ICfgNode> visitedPath = functionExection.getTestPath(solution);
				for(ProbTestPath myTestPath: graph.getFullProbTestPaths()) {
					if(myTestPath.compare(visitedPath)) {
						graph.updateGraph(graph.getFullProbTestPaths().indexOf(myTestPath), 1);
						myTestPath.setTestCase(solution);
					}
				}
				pathNumber=graph.getNewPath();
			}while(pathNumber!=-1);
			
			graph.toTxtFile();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("Finish Generting!");
	}
	
	public String getSolutionInRandomPath(Graph graph, int pathNumber) throws Exception{
		IFunctionNode function = (IFunctionNode) graph.getFunctionNode();

		List<PathConstraint> constraints =new ArrayList<PathConstraint>();
		for (PathConstraint c : (PathConstraints) graph.getFullProbTestPaths().get(pathNumber).getConstraints()) {
			constraints.add(c);
		}
		
		smt.setTestcases(function.getArguments());
		smt.setConstraints(constraints);
		smt.generate();
		BufferedWriter writer = new BufferedWriter(new FileWriter("myConstraint.smt2", false));
		writer.write(smt.getSmtLibContent());
		writer.close();
		RunZ3OnCMD run = new RunZ3OnCMD(pathToZ3, pathToConstraint);
		run.execute();
		return new Z3SolutionParser().getSolution(run.getSolution());
	}
	
	
	public Graph createGraph(String pathtoFile, String functionName,int maxIteration) throws Exception {
		ProjectParser parser = new ProjectParser(new File(pathtoFile));
		IFunctionNode function = (IFunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), functionName).get(0);
		FunctionNormalizer fnNorm = function.normalizedAST();
		function.setAST(fnNorm.getNormalizedAST());
		ICFG cfg;
		cfg = function.generateCFG();
		cfg.generateAllPossibleTestpaths(maxIteration);
		PossibleTestpathGeneration tpGen = new PossibleTestpathGeneration(cfg,maxIteration);
		tpGen.generateTestpaths();
		return new Graph(cfg,tpGen.getPossibleTestpaths(),function,pathtoFile);
	}
}
