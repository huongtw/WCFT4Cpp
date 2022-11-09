package testdatagen;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Assert;
import org.junit.Test;

import config.IFunctionConfig;
import config.Paths;

public class Tsdvr1_Op_Test extends AbstractJUnitTest {
    @Test
    public void test1() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "PlusTest(int,int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test2() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "MinusTest(int,int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test3() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "MultiplyTest(int,int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test4() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "MultiplyTest2(int,int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test5() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "DivideTest(int,int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test6() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "RemainderTest(int,int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test7() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "EqualTest(int,int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test8() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "GreaterOrEqualTest(int,int)", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test9() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "LessThanOrEqualTest(int,int)", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test10() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "GreaterTest(int,int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test11() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "LessThanTest(int,int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test12() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "NotEqualTest(int,int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test14() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "LogicAndTest(int,int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test15() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "LogicOrTest(int,int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test16() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "LogicNotTest(int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test17() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "AssignTest(int,int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test18() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "PlusAndAssignTest(int,int)", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test19() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "MinusAndAssignTest(int,int)", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test20() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "MultiplyAndAssignTest(int,int)", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test21() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "DivideAndAssignTest(int,int)", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test22() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "RemainderAndAssignTest(int,int)", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test23() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "NestedAssignTest(int,int,int)", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test24() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "SizeOfTest(int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test25() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "AddressAndPointerTest(int,int)", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test26() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "ConditionalTest(int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }
}
