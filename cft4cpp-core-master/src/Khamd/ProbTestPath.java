package Khamd;

import java.util.ArrayList;
import java.util.List;

import cfg.object.ICfgNode;
import testdatagen.se.IPathConstraints;
import testdatagen.se.PathConstraint;
import testdatagen.se.PathConstraints;
import utils.tostring.ToString;

public class ProbTestPath {
	private List<Edge> edges;
	private int pathNumber;
	private boolean isGenerated;
	private int visitedNumber ;
	private String realString;
	private IPathConstraints constraints;
	private String testCase;
	
	public ProbTestPath(int pathNumber) {
		this.pathNumber=pathNumber;
		this.edges = new ArrayList<Edge>();
		this.visitedNumber=0;
		this.isGenerated=false;
		this.testCase="";
		this.realString="";
//		this.constraint
	}
	
	public List<ICfgNode> getFullCfgNode(){
		List<ICfgNode> fullCfgNode = new ArrayList<ICfgNode>();
		fullCfgNode.add(this.edges.get(0).getNode());
		for(int i=0;i<this.edges.size();i++){
			fullCfgNode.add(this.edges.get(i).getNextNode());
		}
		return fullCfgNode;
	}
	public boolean compare(List<ICfgNode> cfgNodes) {
		
		for(ICfgNode node: this.getFullCfgNode()) {
			if(cfgNodes.indexOf(node)!=this.getFullCfgNode().indexOf(node)) {
				return false;
			}
		}
		return true;
	}
	public String toString() {
		List<ICfgNode> fullCfgNodes =this.getFullCfgNode();
		List<ICfgNode> constraints = new ArrayList<ICfgNode>();
		
		for(PathConstraint c: (PathConstraints) this.getConstraints()) {
	
			if(c.toString().indexOf("!")==0) {
				constraints.add(c.getCfgNode());
			}
		}
		String toString = "";
		for(ICfgNode node: fullCfgNodes) {
			if(node.toString().contains("{")) {
				continue;
			}
			if(constraints.indexOf(node)!=-1) {
				toString+="!( "+node.toString()+" ) => ";
			}
			else {
				toString+="( "+node.toString()+") => ";
			}
		}
		return toString.substring(0,toString.length()-3)+" "+this.getVisitedNumber();
		
	}
	
	public String toCSV() {
		List<ICfgNode> fullCfgNodes =this.getFullCfgNode();
		String toCSV = "";
		for(ICfgNode node : fullCfgNodes) {
			if(node.getContent().equals("{")) continue;
			toCSV+=" ("+node.toString()+") ";
		}
		return toCSV;
	}
	
	public Edge searchEdge(ICfgNode node, ICfgNode nextNode) {
		for(Edge edge : this.edges) {
			if(edge.getNode()==node && edge.getNextNode()==nextNode) {
				return edge;
			}
		}
		return null;
	}
	
	
	public int getWeight() {
		int prob=0;
		for(Edge edge: this.edges) {
			prob+=edge.getWeight();
		}
		return prob;
	}
	public IPathConstraints getConstraints() {
		return this.constraints;
	}
	public void setConstraints(IPathConstraints iPathConstraints) {
		this.constraints=iPathConstraints;
	}
	
	public boolean isGenerated() {
		return isGenerated;
	}
	public void setGenerated(boolean isGenerated) {
		this.isGenerated = isGenerated;
	}
	public String getTestCase() {
		return testCase;
	}
	public void setTestCase(String testCase) {
		this.testCase = testCase;
	}
	public int getVisitedNumber() {
		return visitedNumber;
	}
	public void setVisitedNumber(int visitedNumber) {
		this.visitedNumber += visitedNumber;
	}
	public void addEdge(Edge edge) {
		this.edges.add(edge);
	}
	public List<Edge> getEdge(){
		return this.edges;
	}
	public String getRealString() {
		return realString;
	}
	public void setRealString(String realString) {
		this.realString = realString;
	}
	
	
	
}
