package testdatagen.module;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import config.FunctionConfig;
import config.ParameterBound;
import config.Paths;
import parser.projectparser.ProjectParser;
import testdata.object.IAbstractDataNode;
import testdata.object.RootDataNode;
import testdata.object.StructureDataNode;
import tree.object.AttributeOfStructureVariableNode;
import tree.object.IFunctionNode;
import tree.object.INode;
import tree.object.IVariableNode;
import tree.object.NamespaceNode;
import tree.object.SourcecodeFileNode;
import tree.object.StructureNode;
import utils.SpecialCharacter;
import utils.Utils;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

/**
 * A tree represent value of variables
 *
 * @author DucAnh
 */
public class DataTreeGeneration implements IDataTreeGeneration {
    final static Logger logger = Logger.getLogger(DataTreeGeneration.class);

    int id = 0;
    private Map<String, Object> staticSolution = new HashMap<>();
    private IFunctionNode functionNode;
    private RootDataNode root = new RootDataNode();

    public DataTreeGeneration() {
        root = new RootDataNode();
    }

    public static void main(String[] args) throws Exception {
        ProjectParser parser = new ProjectParser(new File(Paths.TSDV_R1));
        Paths.CURRENT_PROJECT.CLONE_PROJECT_PATH = Paths.TSDV_R1;

        String name = "SimpleMethodTest()";
        IFunctionNode function = (IFunctionNode) Search
                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), name).get(0);

        FunctionConfig functionConfig = new FunctionConfig();
        functionConfig.setCharacterBound(new ParameterBound(59, 86));
        functionConfig.setIntegerBound(new ParameterBound(45, 50));
        functionConfig.setSizeOfArray(3);
        function.setFunctionConfig(functionConfig);

        logger.debug(function.getAST().getRawSignature());
        //
        IDataTreeGeneration dataTreeGen = new DataTreeGeneration();
        dataTreeGen.setFunctionNode(function);

        Map<String, Object> staticSolution = new HashMap<>();
        staticSolution.put("x", "2");
        staticSolution.put("flag", "1");
        // staticSolution.put("n", "NULL");
        // staticSolution.put("c1", "anh");
        // staticSolution.put("i", 2);
        // staticSolution.put("i1[8]", 10);
        // staticSolution.put("sv1.age", 18);
        // staticSolution.put("sv1.name[2]", 95);
        // staticSolution.put("sv[0].tt.name[2]", 77);
        // staticSolution.put("sv", "NULL");
        // staticSolution.put("front!", "NULL");// front!=NULL
        // staticSolution.put("front[0].N!", "NULL");// front[0].N!=NULL
        // staticSolution.put("front=NULL;=NULL;", "97");
        // staticSolution.put("front[0].N!", "NULL");
        dataTreeGen.setStaticSolution(staticSolution);
        dataTreeGen.generateTree();

        System.out.println("Result : ");
        System.out.println(dataTreeGen.getFunctionCall());
    }

    @Override
    public void generateTree() throws Exception {
        root.setName(functionNode.getSimpleName());
        root.setFunctionNode(functionNode);

        new InitialTreeGen().generateTree(root, functionNode);
        genNodeTreefromStaticSolution(staticSolution, root);
        setVituralName(root);

        new RandomValueGen().randomValue(root);
    //    System.out.println(new SimpleTreeDisplayer().toString(root));
    }

    /**
     * Generate tree node from a list of values
     *
     * @param staticSolution
     * @param root
     * @throws Exception
     */
    private void genNodeTreefromStaticSolution(Map<String, Object> staticSolution, RootDataNode root) throws Exception {
        for (String key : staticSolution.keySet()) {
            String value = staticSolution.get(key) + "";

            boolean isNegative = false;
            if (key.endsWith(IDataTreeGeneration.NEGATIVE))
                isNegative = true;

            String[] names = splitStaticName(key);

            TreeExpander expander = new TreeExpander();
            expander.setFunctionNode(functionNode);
            expander.expandTree(root, names);

            if (!value.equals("NULL"))
                new TreeValueUpdater().updateValue(names, value, root);
            else if (isNegative && value.equals("NULL"))
                new TreeValueUpdater().updateNotNullValue(names, root);
            else if (!isNegative && value.equals("NULL"))
                new TreeValueUpdater().updateNullValue(names, root);
        }
    }

    /**
     * Split key into tokens by separators "." and array items . <br/>
     * Ex: key = "sv[1][2]" --> two tokens: "sv", "[1][2]". <br/>
     * Ex2: key = "sv.age[1]" ---> two tokens:"sv","age","[1]"
     *
     * @param key the name of static solution needed to parse into tokens
     * @return
     */
    private String[] splitStaticName(String key) {
        List<String> namesTmp = new ArrayList<>();
        key = key.replace(IDataTreeGeneration.NEGATIVE, "");

        String item = "";
        for (int i = 0; i < key.toCharArray().length; i++) {
            Character c = key.toCharArray()[i];
            if (c == '.') {
                if (item.length() > 0)
                    namesTmp.add(item);
                item = "";
            } else if (c == ']') {
                if (i + 1 < key.toCharArray().length) {
                    Character next = key.toCharArray()[i + 1];
                    if (next != '[') {
                        /*
                         * Case "]["
						 */
                        item += c;
                        if (item.length() > 0)
                            namesTmp.add(item);
                        item = "";
                    } else
                        item += c;
                } else {
                    /*
                     * End of string
					 */
                    item += c;
                    if (item.length() > 0)
                        namesTmp.add(item);
                }

            } else if (c == '[') {
                Character previous = key.toCharArray()[i - 1];
                if (previous != ']') {
                    /*
					 * Case "]["
					 */
                    if (item.length() > 0)
                        namesTmp.add(item);
                    item = "";
                    item += c;
                } else
                    item += c;
            } else if (i == key.toCharArray().length - 1) {
				/*
				 * End of string
				 */
                item += c;
                if (item.length() > 0)
                    namesTmp.add(item);
            } else
                item += c;
        }

        return Utils.convertToArray(namesTmp);
    }

    @Override
    public String getFunctionCall() throws Exception {
        INode realParent = this.functionNode.getRealParent();
        String functionCall = "";
        if (realParent instanceof SourcecodeFileNode) {
            functionCall = this.functionNode.getSimpleName();
            functionCall += "(";
            for (IVariableNode v : this.functionNode.getArguments())
                functionCall += v.getName() + ",";
            functionCall += ")";
            functionCall = functionCall.replace(",)", ")") + SpecialCharacter.END_OF_STATEMENT;

        } else if (realParent instanceof NamespaceNode) {
            if (this.functionNode.getLogicPathFromTopLevel().length() > 0)
                functionCall = "using namespace " + this.functionNode.getLogicPathFromTopLevel() + ";";

            functionCall += this.functionNode.getSimpleName();
            functionCall += "(";
            for (IVariableNode v : this.functionNode.getArguments())
                functionCall += v.getName() + ",";
            functionCall += ")";
            functionCall = functionCall.replace(",)", ")") + SpecialCharacter.END_OF_STATEMENT;

        } else if (realParent instanceof StructureNode) {
            if (this.functionNode.getLogicPathFromTopLevel().length() == 0)
                functionCall = this.functionNode.getLogicPathFromTopLevel()
                        + SpecialCharacter.STRUCTURE_OR_NAMESPACE_ACCESS;
            else
                functionCall = this.functionNode.getLogicPathFromTopLevel();
			/*
			 * Khai bÃ¡o
			 */
            final String nameStructureVar = "__call";
            functionCall += " " + nameStructureVar + SpecialCharacter.END_OF_STATEMENT + SpecialCharacter.LINE_BREAK;
			/*
			 * Náº¡p biáº¿n ngoÃ i
			 */
            for (INode item : this.functionNode.getReducedExternalVariables()) {
                IVariableNode externalVar = (IVariableNode) item;
                for (IAbstractDataNode child : this.root.getChildren())
                    if (child.getCorrespondingVar().equals(externalVar) && item instanceof AttributeOfStructureVariableNode) {
                        // If the current var is private, it must have setter :)
                        if (externalVar.getSetterNode() != null) {
                            String setter = nameStructureVar + "." + externalVar.getSetterNode().getSimpleName() + "("
                                    + item.getName() + ")";
                            functionCall += setter + SpecialCharacter.END_OF_STATEMENT + SpecialCharacter.LINE_BREAK;
                        } else
                            // If the current var is public, it does not have setter :)
                            if (externalVar.getSetterNode() == null) {
                                String setter = nameStructureVar + "." + externalVar.getName() + "="
                                        + item.getName() + SpecialCharacter.END_OF_STATEMENT + SpecialCharacter.LINE_BREAK;
                                functionCall += setter;
                            }
                    }
            }
			/*
			 * Gá»�i hÃ m
			 */
            if (this.functionNode.getSimpleName().contains(SpecialCharacter.STRUCTURE_OR_NAMESPACE_ACCESS))
                functionCall += nameStructureVar + "." + this.functionNode.getSimpleName().substring(
                        this.functionNode.getSimpleName().lastIndexOf(SpecialCharacter.STRUCTURE_OR_NAMESPACE_ACCESS)
                                + 2);
            else
                functionCall += nameStructureVar + "." + this.functionNode.getSimpleName();
            functionCall += "(";
            for (IVariableNode v : this.functionNode.getArguments())
                functionCall += v.getName() + ",";
            functionCall += ")";
            functionCall = functionCall.replace(",)", ")") + SpecialCharacter.END_OF_STATEMENT;

        } else {

        }
        return functionCall;
    }

    @Override
    public IFunctionNode getFunctionNode() {
        return functionNode;
    }

    @Override
    public void setFunctionNode(IFunctionNode functionNode) {
        this.functionNode = functionNode;
    }

    @Override
    public String getInputforDisplay() {
        try {
            String input = root.getInputForDisplay();
            return input;
        } catch (Exception e) {

            return "";
        }
    }

    @Override
    public String getInputforGoogleTest() {
        try {
            String input = root.getInputForGoogleTest();
            input = input.replaceAll(";;", ";");
            return input;
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public Map<String, Object> getStaticSolution() {
        return staticSolution;
    }

    @Override
    public void setStaticSolution(Map<String, Object> staticSolution) {
        this.staticSolution = staticSolution;
    }

    @Override
    public void setRoot(RootDataNode root) {
        this.root = root;
    }

    /**
     * Set virtual for nodes in the tree
     *
     * @param n
     */
    private void setVituralName(IAbstractDataNode n) {
        if (n == null)
            return;
        else if (n.isPassingVariable())
            n.setVituralName(n.getVituralName());
        else if (n.isArrayElement())
            n.setVituralName(n.getParent().getVituralName() + n.getName());
        else if (n.isAttribute() && n.getParent() instanceof StructureDataNode)
            n.setVituralName(n.getParent().getVituralName() + "." + n.getName());
        else {
            n.setVituralName(n.getVituralName() + "_" + this.id);
            this.id++;
        }

        if (n.getChildren() != null)
            for (IAbstractDataNode child : n.getChildren())
                this.setVituralName(child);
    }

    @Override
    public String getInputformFile() {
        try {
            String input = root.generareSourcecodetoReadInputFromFile();
            return input;
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public String getInputSavedInFile() {
        try {
            String input = root.generateInputToSavedInFile();
            return input;
        } catch (Exception e) {
            return "";
        }
    }

}
