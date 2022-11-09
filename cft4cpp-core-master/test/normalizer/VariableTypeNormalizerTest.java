package normalizer;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import config.Paths;
import parser.projectparser.ProjectParser;
import tree.object.IFunctionNode;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

public class VariableTypeNormalizerTest {
    @Test
    public void test01() throws Exception {
        ProjectParser parser = new ProjectParser(new File(Paths.SAMPLE01));
        IFunctionNode function = (IFunctionNode) Search
                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "StackLinkedList::destroyList()")
                .get(0);

        ArgumentTypeNormalizer normalizer = new ArgumentTypeNormalizer();
        normalizer.setFunctionNode(function);
        normalizer.normalize();
        Assert.assertEquals(normalizer.getNormalizedSourcecode().contains(" StackLinkedList::destroyList()"), true);
    }

    @Test
    public void test02() throws Exception {
        ProjectParser parser = new ProjectParser(new File(Paths.SYMBOLIC_EXECUTION_TEST));
        IFunctionNode function = (IFunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
                "nsTest4" + File.separator + "func5(::XXX)").get(0);

        ArgumentTypeNormalizer normalizer = new ArgumentTypeNormalizer();
        normalizer.setFunctionNode(function);
        normalizer.normalize();

        Assert.assertEquals(normalizer.getNormalizedSourcecode().contains(" func5(::XXX"), true);
    }

    @Test
    public void test03() throws Exception {
        ProjectParser parser = new ProjectParser(new File(Paths.TSDV_R1));
        IFunctionNode function = (IFunctionNode) Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(),
                "Level0MultipleNsTest(X,ns1::X,ns1::ns2::X)").get(0);

        ArgumentTypeNormalizer normalizer = new ArgumentTypeNormalizer();
        normalizer.setFunctionNode(function);
        normalizer.normalize();

        Assert.assertEquals(
                normalizer.getNormalizedSourcecode().contains(" Level0MultipleNsTest(::X x0,ns1::X x1,ns1::ns2::X x2)"),
                true);
    }

    @Test
    public void test04() throws Exception {
        ProjectParser parser = new ProjectParser(new File(Paths.SAMPLE01));
        IFunctionNode function = (IFunctionNode) Search
                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "StackLinkedList::pop2()").get(0);

        ArgumentTypeNormalizer normalizer = new ArgumentTypeNormalizer();
        normalizer.setFunctionNode(function);
        normalizer.normalize();
        Assert.assertEquals(normalizer.getNormalizedSourcecode().contains(" StackLinkedList::pop2()"), true);
    }

    @Test
    public void test05() throws Exception {
        ProjectParser parser = new ProjectParser(new File(Paths.TSDV_R1));
        IFunctionNode function = (IFunctionNode) Search
                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "getXEx()").get(0);

        ArgumentTypeNormalizer normalizer = new ArgumentTypeNormalizer();
        normalizer.setFunctionNode(function);
        normalizer.normalize();
        Assert.assertEquals(normalizer.getNormalizedSourcecode().contains(" getXEx() const"), true);
    }
}
