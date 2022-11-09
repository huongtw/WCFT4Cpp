package testdatagen.module;

import java.io.File;
import java.util.List;

import config.Paths;
import parser.projectparser.ProjectParser;
import testdata.object.AbstractDataNode;
import testdata.object.ClassDataNode;
import testdata.object.EnumDataNode;
import testdata.object.IAbstractDataNode;
import testdata.object.NormalCharacterDataNode;
import testdata.object.NormalNumberDataNode;
import testdata.object.OneDimensionCharacterDataNode;
import testdata.object.OneDimensionDataNode;
import testdata.object.OneDimensionNumberDataNode;
import testdata.object.OneDimensionStructureDataNode;
import testdata.object.OneLevelCharacterDataNode;
import testdata.object.OneLevelDataNode;
import testdata.object.OneLevelNumberDataNode;
import testdata.object.OneLevelStructureDataNode;
import testdata.object.PointerDataNode;
import testdata.object.RootDataNode;
import testdata.object.StructDataNode;
import testdata.object.StructureDataNode;
import testdata.object.TwoDimensionCharacterDataNode;
import testdata.object.TwoDimensionDataNode;
import testdata.object.TwoDimensionNumberDataNode;
import testdata.object.TwoDimensionStructureDataNode;
import testdata.object.TwoLevelCharacterDataNode;
import testdata.object.TwoLevelDataNode;
import testdata.object.TwoLevelNumberDataNode;
import testdata.object.TwoLevelStructureDataNode;
import testdata.object.UnionDataNode;
import testdatagen.testdatainit.VariableTypes;
import tree.object.ClassNode;
import tree.object.ExternalVariableNode;
import tree.object.FunctionNode;
import tree.object.IFunctionNode;
import tree.object.INode;
import tree.object.IVariableNode;
import tree.object.StructNode;
import tree.object.UnionNode;
import tree.object.VariableNode;
import utils.Utils;
import utils.VariableTypesUtils;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

/**
 * Táº¡o cÃ¢y khá»Ÿi Ä‘áº§u dá»±a trÃªn arguments vÃ  external variable Táº¥t cáº£ má»�i biáº¿n
 * truyá»�n vÃ o hÃ m thuá»™c ba loáº¡i:
 * <p>
 * + Biáº¿n cÆ¡ báº£n: Ä�Æ°á»£c sinh giÃ¡ trá»‹ ngáº«u nhiÃªn
 * <p>
 * + Biáº¿n máº£ng: máº·c Ä‘á»‹nh sá»‘ pháº§n tá»­ lÃ  0
 * <p>
 * + Biáº¿n con trá»�: máº·c Ä‘á»‹nh gÃ­a trá»‹ lÃ  null
 *
 * @author ducanh
 */
public class InitialTreeGen {
    private IFunctionNode functionNode;

    private IAbstractDataNode root;

    public InitialTreeGen() {
    }

    public static void main(String[] args) throws Exception {
        ProjectParser parser = new ProjectParser(new File(Paths.TSDV_R1));

        String name = "SimpleMethodTest()";
//         String name = "StackLinkedList::push(Node*)";
        FunctionNode function = (FunctionNode) Search
                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), name).get(0);

		/*
         *
		 */
        RootDataNode root = new RootDataNode();
        InitialTreeGen dataTreeGen = new InitialTreeGen();
        dataTreeGen.generateTree(root, function);
        System.out.println(new SimpleTreeDisplayer().toString(root));
    }

    public void generateTree(RootDataNode root, IFunctionNode functionNode) throws Exception {
        this.root = root;
        this.functionNode = functionNode;

        root.setName(functionNode.getSimpleName());

        List<IVariableNode> passingVariables = functionNode.getPassingVariables();
        for (INode passingVariable : passingVariables)
            genInitialTree((VariableNode) passingVariable, root);

        setVituralName(root);
    }

    private void genInitialTree(VariableNode vCurrentChild, AbstractDataNode nCurrentParent) throws Exception {
        String rawType = vCurrentChild.getRealType();
        if (rawType.contains("::"))
            rawType = rawType.split("::")[rawType.split("::").length - 1];

        if (VariableTypes.isBasic(rawType))
            initialBasicType(vCurrentChild, nCurrentParent);
        else if (VariableTypes.isOneLevel(rawType))
            initialOneLevelType(vCurrentChild, nCurrentParent);
        else if (VariableTypes.isOneDimension(rawType))
            initialOneDimensionType(vCurrentChild, nCurrentParent);
        else if (VariableTypes.isStructureSimple(rawType))
            if (VariableTypesUtils.isEnumNode(rawType))
                initialEnumType(vCurrentChild, nCurrentParent);
            else
                initialStructureType(vCurrentChild, nCurrentParent);
        else if (VariableTypes.isTwoDimension(rawType))
            initialTwoDimensionType(vCurrentChild, nCurrentParent);
        else if (VariableTypes.isTwoLevel(rawType))
            initialTwoLevelType(vCurrentChild, nCurrentParent);
        else
            throw new Exception("Loi khi xu ly " + vCurrentChild.toString());
    }

    public IFunctionNode getFunctionNode() {
        return functionNode;
    }

    public void setFunctionNode(FunctionNode functionNode) {
        this.functionNode = functionNode;
    }

    public IAbstractDataNode getRoot() {
        return root;
    }

    public void setRoot(IAbstractDataNode root) {
        this.root = root;
    }

    /**
     * Khá»Ÿi táº¡o biáº¿n truyá»�n vÃ o lÃ  kiá»ƒu cÆ¡ báº£n
     *
     * @param vParent
     * @param nParent
     */
    private void initialBasicType(VariableNode vParent, AbstractDataNode nParent) {
        AbstractDataNode child = null;
        if (VariableTypes.isCh(vParent.getRealType()))
            child = new NormalCharacterDataNode();
        else
            child = new NormalNumberDataNode();

        child.setChildren(null);
        child.setParent(nParent);
        child.setType(vParent.getRealType());
        child.setName(vParent.getNewType());
        child.setCorrespondingVar(vParent);
        if (vParent instanceof ExternalVariableNode)
            child.setExternelVariable(true);
        nParent.addChild(child);
    }

    /**
     * Khá»Ÿi táº¡o biáº¿n truyá»�n vÃ o lÃ  máº£ng má»™t chiá»�u
     *
     * @param vParent
     * @param nParent
     * @throws Exception
     */
    private void initialOneDimensionType(VariableNode vParent, AbstractDataNode nParent) throws Exception {
        String rawType = vParent.getRealType();
        AbstractDataNode child = null;
        if (VariableTypes.isCh(rawType))
            child = new OneDimensionCharacterDataNode();
        else if (VariableTypes.isNum(rawType))
            child = new OneDimensionNumberDataNode();
        else
            child = new OneDimensionStructureDataNode();
        /**
         *
         */
        child.setParent(nParent);
        child.setType(vParent.getRealType());
        child.setName(vParent.getNewType());
        child.setCorrespondingVar(vParent);
        ((OneDimensionDataNode) child).setSize(-1);
        if (vParent instanceof ExternalVariableNode)
            child.setExternelVariable(true);

        nParent.addChild(child);

    }

    /**
     * Khá»Ÿi táº¡o biáº¿n truyá»�n vÃ o lÃ  con trá»� má»™t má»©c
     *
     * @param vParent
     * @param nParent
     * @throws Exception
     */
    private void initialOneLevelType(VariableNode vParent, AbstractDataNode nParent) throws Exception {
        String rawType = Utils.getRealType(vParent.getReducedRawType(), vParent.getParent());
        AbstractDataNode child = null;
        if (VariableTypes.isCh(rawType))
            child = new OneLevelCharacterDataNode();
        else if (VariableTypes.isNum(rawType))
            child = new OneLevelNumberDataNode();
        else
            child = new OneLevelStructureDataNode();

        child.setParent(nParent);
        child.setType(vParent.getRealType());
        child.setName(vParent.getNewType());
        child.setCorrespondingVar(vParent);
        ((OneLevelDataNode) child).setAllocatedSize(PointerDataNode.NULL_VALUE);
        if (vParent instanceof ExternalVariableNode)
            child.setExternelVariable(true);

        nParent.addChild(child);
    }

    /**
     * Khá»Ÿi táº¡o biáº¿n truyá»�n vÃ o lÃ  kiá»ƒu cáº¥u trÃºc
     *
     * @param vParent
     * @param nParent
     * @throws Exception
     */
    private void initialStructureType(VariableNode vParent, AbstractDataNode nParent) throws Exception {
        INode correspondingNode = vParent.resolveCoreType();
        StructureDataNode child = null;

        if (correspondingNode instanceof StructNode)
            child = new StructDataNode();
        else if (correspondingNode instanceof ClassNode)
            child = new ClassDataNode();
        else if (correspondingNode instanceof UnionNode)
            child = new UnionDataNode();

        child.setParent(nParent);
        child.setName(vParent.getNewType());
        child.setType(vParent.getFullType());
        child.setCorrespondingVar(vParent);
        if (vParent instanceof ExternalVariableNode)
            child.setExternelVariable(true);
        nParent.addChild(child);
    }

    private void initialEnumType(VariableNode vParent, AbstractDataNode nParent) {
        EnumDataNode child = new EnumDataNode();

        child.setParent(nParent);
        child.setName(vParent.getNewType());
        child.setType(vParent.getRealType());
        child.setCorrespondingVar(vParent);
        if (vParent instanceof ExternalVariableNode)
            child.setExternelVariable(true);
        nParent.addChild(child);
    }

    /**
     * CÃ i tÃªn áº£o cho cÃ¢y
     *
     * @param n
     */
    private void setVituralName(IAbstractDataNode n) {
        if (n == null)
            return;
        else
            n.setVituralName(n.getName());
        if (n.getChildren() != null)
            for (IAbstractDataNode child : n.getChildren())
                setVituralName(child);
    }

    /**
     * Khá»Ÿi táº¡o biáº¿n truyá»�n vÃ o lÃ  máº£ng hai chiá»�u
     *
     * @param vParent
     * @param nParent
     * @throws Exception
     */
    private void initialTwoDimensionType(VariableNode vParent, AbstractDataNode nParent) throws Exception {
        String rawType = vParent.getRealType();
        AbstractDataNode child = null;
        if (VariableTypes.isChTwoDimension(rawType))
            child = new TwoDimensionCharacterDataNode();
        else if (VariableTypes.isNumTwoDimension(rawType))
            child = new TwoDimensionNumberDataNode();
        else
            child = new TwoDimensionStructureDataNode();
        /**
         *
         */
        child.setParent(nParent);
        child.setType(vParent.getRealType());
        child.setName(vParent.getNewType());
        child.setCorrespondingVar(vParent);
        ((TwoDimensionDataNode) child).setSizeA(-1);
        ((TwoDimensionDataNode) child).setSizeB(-1);
        if (vParent instanceof ExternalVariableNode)
            child.setExternelVariable(true);

        nParent.addChild(child);
    }

    /**
     * Khá»Ÿi táº¡o biáº¿n truyá»�n vÃ o lÃ  con trá»� hai má»©c
     *
     * @param vParent
     * @param nParent
     * @throws Exception
     */
    private void initialTwoLevelType(VariableNode vParent, AbstractDataNode nParent) throws Exception {
        String rawType = vParent.getRealType();
        AbstractDataNode child = null;
        if (VariableTypes.isChTwoLevel(rawType))
            child = new TwoLevelCharacterDataNode();
        else if (VariableTypes.isNumTwoLevel(rawType))
            child = new TwoLevelNumberDataNode();
        else
            child = new TwoLevelStructureDataNode();

        child.setParent(nParent);
        child.setType(vParent.getRealType());
        child.setName(vParent.getNewType());
        child.setCorrespondingVar(vParent);
        ((TwoLevelDataNode) child).setAllocatedSizeA(-1);
        ((TwoLevelDataNode) child).setAllocatedSizeB(-1);
        if (vParent instanceof ExternalVariableNode)
            child.setExternelVariable(true);

        nParent.addChild(child);
    }

}
