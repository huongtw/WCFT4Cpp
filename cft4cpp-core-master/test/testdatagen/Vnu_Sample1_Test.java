package testdatagen;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import config.IFunctionConfig;
import config.Paths;

@Ignore
public class Vnu_Sample1_Test extends AbstractJUnitTest {
    @Test
    public void test1() throws LineUnavailableException {

        Assert.assertEquals(true,
                generateTestdata(Paths.SAMPLE01, "StackLinkedList::push(Node*)", null, IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test2() throws LineUnavailableException {

        Assert.assertEquals(true,
                generateTestdata(Paths.SAMPLE01, "StackLinkedList::pop1()", null, IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test3() throws LineUnavailableException {

        Assert.assertEquals(true,
                generateTestdata(Paths.SAMPLE01, "StackLinkedList::pop2()", null, IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test4() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.SAMPLE01, "StackLinkedList::destroyList()", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test5() throws LineUnavailableException {

        Assert.assertEquals(true,
                generateTestdata(Paths.SAMPLE01, "disp(Node*)", null, IFunctionConfig.BRANCH_COVERAGE ));
    }
}
