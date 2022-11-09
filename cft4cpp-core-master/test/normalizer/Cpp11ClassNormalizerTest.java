package normalizer;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class Cpp11ClassNormalizerTest {
    final static Logger logger = Logger.getLogger(Cpp11ClassNormalizerTest.class);
    private Cpp11ClassNormalizer normalizer;

    @Before
    public void setUpBefore() throws Exception {
        normalizer = new Cpp11ClassNormalizer();
    }

    @Test
    public void testClassFinal() {
        String origin = "class LOGCPP Category final : Animal { int x; }",
                output = "class        Category final : Animal { int x; }";
        normalizer.setOriginalSourcecode(origin);
        normalizer.normalize();
        Assert.assertEquals(output, normalizer.getNormalizedSourcecode());
    }

    @Test
    public void testInlineComment() {
        String origin = "//This is class Category\n" + "class LOGCPP Category : Animal { int x; }",
                output = "//This is class Category\n" + "class        Category : Animal { int x; }";
        normalizer.setOriginalSourcecode(origin);
        normalizer.normalize();
        Assert.assertEquals(output, normalizer.getNormalizedSourcecode());
    }

    @Test
    public void testInlineComment2() {
        String origin = "//This is class Category\n" + "class LOGCPP Category : Animal { int x; }\r\n"
                + "//This is class Dog\n" + "class ANIMAL Dog {int age;}",
                output = "//This is class Category\n" + "class        Category : Animal { int x; }\r\n"
                        + "//This is class Dog\n" + "class        Dog {int age;}";
        normalizer.setOriginalSourcecode(origin);
        normalizer.normalize();
        Assert.assertEquals(output, normalizer.getNormalizedSourcecode());
    }

    @Test
    public void testMultipleAttr() {
        String origin = "class LOGCPP __int32_ x64 Category { int x; }",
                output = "class                     Category { int x; }";
        normalizer.setOriginalSourcecode(origin);
        normalizer.normalize();
        Assert.assertEquals(output, normalizer.getNormalizedSourcecode());
    }

    @Test
    public void testNewLine() {
        String origin = "class LOG4CPP_EXPORT FactoryParams\n{", output = "class                FactoryParams\n{";
        normalizer.setOriginalSourcecode(origin);
        normalizer.normalize();
        Assert.assertEquals(output, normalizer.getNormalizedSourcecode());
    }

    @Test
    public void testNothing() {
        String origin = "class Category{ int x; }", output = "class Category{ int x; }";
        normalizer.setOriginalSourcecode(origin);
        normalizer.normalize();
        Assert.assertEquals(output, normalizer.getNormalizedSourcecode());
    }

    @Test
    public void testSimple() {
        String origin = "class LOGCPP Category{ int x; }", output = "class        Category{ int x; }";
        normalizer.setOriginalSourcecode(origin);
        normalizer.normalize();
        Assert.assertEquals(output, normalizer.getNormalizedSourcecode());
    }

    @Test
    public void testTwoClass() {
        String origin = "class LOGCPP Category : Animal { int x; } " + "class ANIMAL Dog {int age;}",
                output = "class        Category : Animal { int x; } " + "class        Dog {int age;}";
        normalizer.setOriginalSourcecode(origin);
        normalizer.normalize();
        Assert.assertEquals(output, normalizer.getNormalizedSourcecode());
    }

    @Test
    public void testWithBase() {
        String origin = "class LOGCPP Category : Animal { int x; }",
                output = "class        Category : Animal { int x; }";
        normalizer.setOriginalSourcecode(origin);
        normalizer.normalize();
        Assert.assertEquals(output, normalizer.getNormalizedSourcecode());
    }

    @Test
    public void testWithMultipleBase() {
        String origin = "class LOGCPP Category : Animal, Dog { int x; }",
                output = "class        Category : Animal, Dog { int x; }";
        normalizer.setOriginalSourcecode(origin);
        normalizer.normalize();
        Assert.assertEquals(output, normalizer.getNormalizedSourcecode());
    }
}
