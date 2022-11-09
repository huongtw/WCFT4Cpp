package tree.dependency;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import config.Paths;
import parser.projectparser.ProjectParser;
import tree.object.AvailableTypeNode;
import tree.object.FunctionNode;
import tree.object.INode;
import tree.object.IVariableNode;
import utils.search.FieldMemberCondition;
import utils.search.FunctionNodeCondition;
import utils.search.Search;
import utils.search.VariableNodeCondition;

public class TypeDependencyGenerationTest {

    @Test
    public void test01() throws Exception {
        // ProjectParser parser = new ProjectParser(new
        // File(Paths.TYPE_DEPENDENCY_GENERATION_TEST + "TSDV_log4cpp"));
        // FunctionNode sampleNode = (FunctionNode)
        // Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
        // File.separator + "setAppender(Appender*)").get(0);
        // IVariableNode var = sampleNode.getArguments().get(0);// Appender
        // INode correspondingNode = new
        // TypeDependencyGeneration(var).getCorrespondingNode();
        //
        // Assert.assertEquals(true, correspondingNode.getAbsolutePath()
        // .endsWith("\\include\\log4cpp\\Appender.hh\\log4cpp\\Appender".replace("\\",
        // File.separator)));
    }

    @Test
    public void test02() throws Exception {
        // ProjectParser parser = new ProjectParser(new
        // File(Paths.TYPE_DEPENDENCY_GENERATION_TEST + "TSDV_log4cpp"));
        // FunctionNode sampleNode = (FunctionNode)
        // Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
        // File.separator + "AppenderSkeleton::doAppend(const
        // LoggingEvent&)").get(0);
        // IVariableNode var = sampleNode.getArguments().get(0);// LoggingEvent
        // INode correspondingNode = new
        // TypeDependencyGeneration(var).getCorrespondingNode();
        //
        // Assert.assertEquals(true, correspondingNode.getAbsolutePath()
        // .endsWith("\\include\\log4cpp\\LoggingEvent.hh\\log4cpp\\LoggingEvent".replace("\\",
        // File.separator)));
    }

    @Test
    public void test03() throws Exception {
        ProjectParser parser = new ProjectParser(
                new File(Paths.TYPE_DEPENDENCY_GENERATION_TEST + "Eclipse_Type Simple"));
        FunctionNode sampleNode = (FunctionNode) Search
                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), File.separator + "increment(int_type)")
                .get(0);
        IVariableNode var = sampleNode.getArguments().get(0);// int_type
        INode correspondingNode = new TypeDependencyGeneration(var).getCorrespondingNode();
        Assert.assertEquals("int", ((AvailableTypeNode) correspondingNode).getType());
    }

    @Test
    public void test04() throws Exception {
        ProjectParser parser = new ProjectParser(
                new File(Paths.TYPE_DEPENDENCY_GENERATION_TEST + "Eclipse_Type Simple"));
        FunctionNode sampleNode = (FunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
                "IndividualStore" + File.separator + "addFruit(hoaqua)").get(0);
        IVariableNode var = sampleNode.getArguments().get(0);// int_type
        INode correspondingNode = new TypeDependencyGeneration(var).getCorrespondingNode();

        Assert.assertEquals(true, correspondingNode.getAbsolutePath()
                .endsWith("\\Eclipse_Type Simple\\header1.h\\IndividualStore\\Fruit".replace("\\", File.separator)));
    }

    @Test
    public void test05() throws Exception {
        ProjectParser parser = new ProjectParser(
                new File(Paths.TYPE_DEPENDENCY_GENERATION_TEST + "Eclipse_Type Simple"));
        FunctionNode sampleNode = (FunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
                File.separator + "getInforOfApple(IndividualStore::hoaqua)").get(0);
        IVariableNode var = sampleNode.getArguments().get(0);// IndividualStore::hoaqua
        INode correspondingNode = new TypeDependencyGeneration(var).getCorrespondingNode();

        Assert.assertEquals(true, correspondingNode.getAbsolutePath()
                .endsWith("header1.h\\IndividualStore\\Fruit".replace("\\", File.separator)));
    }

    @Test
    public void test06() throws Exception {
        ProjectParser parser = new ProjectParser(
                new File(Paths.TYPE_DEPENDENCY_GENERATION_TEST + "Eclipse_Type Simple"));
        FunctionNode sampleNode = (FunctionNode) Search
                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), File.separator + "getSQRT(float_type)")
                .get(0);
        IVariableNode var = sampleNode.getArguments().get(0);// float_type
        INode correspondingNode = new TypeDependencyGeneration(var).getCorrespondingNode();

        Assert.assertEquals("float", ((AvailableTypeNode) correspondingNode).getType());
    }

    @Test
    public void test07() throws Exception {

    }

    @Test
    public void test08() throws Exception {
        ProjectParser parser = new ProjectParser(
                new File(Paths.TYPE_DEPENDENCY_GENERATION_TEST + "Eclipse_Type Simple"));
        FunctionNode sampleNode = (FunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
                File.separator + "getInforOfChainStore(ChainStore)").get(0);
        IVariableNode var = sampleNode.getArguments().get(0);// IndividualStore::hoaqua
        INode correspondingNode = new TypeDependencyGeneration(var).getCorrespondingNode();

        Assert.assertEquals(true, correspondingNode.getAbsolutePath()
                .endsWith("\\Eclipse_Type Simple\\header1.h\\ChainStore".replace("\\", File.separator)));
    }

    @Test
    public void test09() throws Exception {
        ProjectParser parser = new ProjectParser(
                new File(Paths.TYPE_DEPENDENCY_GENERATION_TEST + "Eclipse_Type Simple"));
        FunctionNode sampleNode = (FunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
                File.separator + "displayAddress(IndividualStore::House::Accommodation::information)").get(0);
        IVariableNode var = sampleNode.getArguments().get(0);// IndividualStore::hoaqua
        INode correspondingNode = new TypeDependencyGeneration(var).getCorrespondingNode();

        Assert.assertEquals(true, correspondingNode.getAbsolutePath().endsWith(
                "\\Eclipse_Type Simple\\Accommodation.h\\Accommodation\\information".replace("\\", File.separator)));
    }

    @Test
    public void test10() throws Exception {
        ProjectParser parser = new ProjectParser(
                new File(Paths.TYPE_DEPENDENCY_GENERATION_TEST + "Eclipse_Type Simple"));
        FunctionNode sampleNode = (FunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
                File.separator + "getInforOfChainStore(ns1::C1)").get(0);
        IVariableNode var = sampleNode.getArguments().get(0);// ns1::C1
        INode correspondingNode = new TypeDependencyGeneration(var).getCorrespondingNode();

        Assert.assertEquals(true, correspondingNode.getAbsolutePath()
                .endsWith("\\Eclipse_Type Simple\\TH1.cpp\\ns1\\C1".replace("\\", File.separator)));
    }

    @Test
    public void test11() throws Exception {
        ProjectParser parser = new ProjectParser(
                new File(Paths.TYPE_DEPENDENCY_GENERATION_TEST + "Eclipse_Type Simple"));
        FunctionNode sampleNode = (FunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
                File.separator + "displayAddress2(House::Accommodation::information)").get(0);
        IVariableNode var = sampleNode.getArguments().get(0);// IndividualStore::House::Accommodation::information
        INode correspondingNode = new TypeDependencyGeneration(var).getCorrespondingNode();

        Assert.assertEquals(true, correspondingNode.getAbsolutePath().endsWith(
                "\\Eclipse_Type Simple\\Accommodation.h\\Accommodation\\information".replace("\\", File.separator)));
    }

    @Test
    public void test12() throws Exception {

        ProjectParser parser = new ProjectParser(
                new File(Paths.TYPE_DEPENDENCY_GENERATION_TEST + "Eclipse_Type Simple"));
        IVariableNode sampleNode = (IVariableNode) Search.searchNodes(parser.getRootTree(), new FieldMemberCondition(),
                File.separator + "House" + File.separator + "c").get(0);
        INode correspondingNode = new TypeDependencyGeneration(sampleNode).getCorrespondingNode();

        Assert.assertEquals(true, correspondingNode.getAbsolutePath()
                .endsWith("\\Eclipse_Type Simple\\header1.h\\House".replace("\\", File.separator)));
    }

    @Test
    public void test13() throws Exception {
        ProjectParser parser = new ProjectParser(
                new File(Paths.TYPE_DEPENDENCY_GENERATION_TEST + "Eclipse_Type Simple"));
        IVariableNode var = (IVariableNode) Search
                .searchNodes(parser.getRootTree(), new VariableNodeCondition(), "Fruit" + File.separator + "price")
                .get(0);
        INode correspondingNode = new TypeDependencyGeneration(var).getCorrespondingNode();

        Assert.assertEquals("int", ((AvailableTypeNode) correspondingNode).getType());
    }

    @Test
    public void test14() throws Exception {
        ProjectParser parser = new ProjectParser(
                new File(Paths.TYPE_DEPENDENCY_GENERATION_TEST + "Eclipse_Type Simple"));
        FunctionNode sampleNode = (FunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
                File.separator + "testMultipleExtends(Final::A::D::F::sinhvien)").get(0);
        IVariableNode var = sampleNode.getArguments().get(0);
        INode correspondingNode = new TypeDependencyGeneration(var).getCorrespondingNode();

        Assert.assertEquals(true, correspondingNode.getAbsolutePath()
                .endsWith("\\Eclipse_Type Simple\\multipleExtend.cpp\\F\\sinhvien".replace("\\", File.separator)));
    }

    @Test
    public void test15() throws Exception {
        ProjectParser parser = new ProjectParser(
                new File(Paths.TYPE_DEPENDENCY_GENERATION_TEST + "Eclipse_Type Simple"));
        FunctionNode sampleNode = (FunctionNode) Search
                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), File.separator + "testInNs2(ns1::A1)")
                .get(0);
        IVariableNode var = sampleNode.getArguments().get(0);
        INode correspondingNode = new TypeDependencyGeneration(var).getCorrespondingNode();

        Assert.assertEquals(true, correspondingNode.getAbsolutePath()
                .endsWith("\\Eclipse_Type Simple\\namspaceTest.h\\ns1\\A1".replace("\\", File.separator)));
    }

    @Test
    public void test16() throws Exception {
        ProjectParser parser = new ProjectParser(
                new File(Paths.TYPE_DEPENDENCY_GENERATION_TEST + "Eclipse_Type Simple"));
        FunctionNode sampleNode = (FunctionNode) Search
                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), File.separator + "testInNs2_2(ns2::A2)")
                .get(0);
        IVariableNode var = sampleNode.getArguments().get(0);
        INode correspondingNode = new TypeDependencyGeneration(var).getCorrespondingNode();

        Assert.assertEquals(true, correspondingNode.getAbsolutePath()
                .endsWith("\\Eclipse_Type Simple\\namspaceTest.h\\ns2\\A2".replace("\\", File.separator)));
    }

    @Test
    public void test17() throws Exception {
        ProjectParser parser = new ProjectParser(
                new File(Paths.TYPE_DEPENDENCY_GENERATION_TEST + "Eclipse_Type Simple"));
        FunctionNode sampleNode = (FunctionNode) Search
                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), File.separator + "testInNs2_1(A1)")
                .get(0);
        IVariableNode var = sampleNode.getArguments().get(0);
        INode correspondingNode = new TypeDependencyGeneration(var).getCorrespondingNode();

        Assert.assertEquals(true, correspondingNode.getAbsolutePath()
                .endsWith("\\Eclipse_Type Simple\\namspaceTest.h\\ns2\\A1".replace("\\", File.separator)));
    }

    @Test
    public void test18() throws Exception {
        ProjectParser parser = new ProjectParser(new File(Paths.TSDV_R1));
        FunctionNode sampleNode = (FunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
                File.separator + "Level0MultipleNsTest(X,ns1::X,ns1::ns2::X)").get(0);
        IVariableNode var = sampleNode.getArguments().get(0);
        INode correspondingNode = new TypeDependencyGeneration(var).getCorrespondingNode();
        System.out.println(correspondingNode.getAbsolutePath());
        Assert.assertEquals(true,
                correspondingNode.getAbsolutePath().endsWith("\\NamespaceTest.cpp\\X".replace("\\", File.separator)));
    }

    @Test
    public void test19() throws Exception {
        ProjectParser parser = new ProjectParser(new File(Paths.TSDV_R1));
        FunctionNode sampleNode = (FunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
                File.separator + "Level0MultipleNsTest(X,ns1::X,ns1::ns2::X)").get(0);
        IVariableNode var = sampleNode.getArguments().get(1);
        INode correspondingNode = new TypeDependencyGeneration(var).getCorrespondingNode();

        Assert.assertEquals(true, correspondingNode.getAbsolutePath()
                .endsWith("\\NamespaceTest.cpp\\ns1\\X".replace("\\", File.separator)));
    }

    @Test
    public void test20() throws Exception {
        ProjectParser parser = new ProjectParser(new File(Paths.TSDV_R1));
        FunctionNode sampleNode = (FunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
                File.separator + "Level0MultipleNsTest(X,ns1::X,ns1::ns2::X)").get(0);
        IVariableNode var = sampleNode.getArguments().get(2);
        INode correspondingNode = new TypeDependencyGeneration(var).getCorrespondingNode();

        Assert.assertEquals(true, correspondingNode.getAbsolutePath()
                .endsWith("\\NamespaceTest.cpp\\ns1\\ns2\\X".replace("\\", File.separator)));
    }

    @Test
    public void test21() throws Exception {
        ProjectParser parser = new ProjectParser(new File(Paths.SYMBOLIC_EXECUTION_TEST));
        FunctionNode sampleNode = (FunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
                "nsTest4" + File.separator + "func4(XXX)").get(0);
        IVariableNode var = sampleNode.getArguments().get(0);
        INode correspondingNode = new TypeDependencyGeneration(var).getCorrespondingNode();

        Assert.assertEquals(true, correspondingNode.getAbsolutePath()
                .endsWith("\\nsTest4.h\\nsTest4\\XXX".replace("\\", File.separator)));
    }

    @Test
    public void test22() throws Exception {
        ProjectParser parser = new ProjectParser(new File(Paths.SYMBOLIC_EXECUTION_TEST));
        FunctionNode sampleNode = (FunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
                "nsTest4" + File.separator + "func5(::XXX)").get(0);
        IVariableNode var = sampleNode.getArguments().get(0);
        INode correspondingNode = new TypeDependencyGeneration(var).getCorrespondingNode();

        Assert.assertEquals(true,
                correspondingNode.getAbsolutePath().endsWith("\\nsTest4.h\\XXX".replace("\\", File.separator)));
    }

    @Test
    public void test23() throws Exception {
        ProjectParser parser = new ProjectParser(new File(Paths.TSDV_R1));
        FunctionNode sampleNode = (FunctionNode) Search
                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), File.separator + "Level2SimpleTest(X)")
                .get(0);
        IVariableNode var = sampleNode.getArguments().get(0);
        INode correspondingNode = new TypeDependencyGeneration(var).getCorrespondingNode();
        Assert.assertEquals(true, correspondingNode.getAbsolutePath()
                .endsWith("\\NamespaceTest.cpp\\ns1\\n2\\X".replace("\\", File.separator)));
    }
}
