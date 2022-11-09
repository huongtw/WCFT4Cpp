package testdatagen;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Assert;
import org.junit.Test;

import config.IFunctionConfig;
import config.Paths;


public class Tsdvr14_NonLinear_Test extends AbstractJUnitTest {
    @Test
    public void test1() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_4, "PlusTest(int,int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test2() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_4, "PlusTest2(int,int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test3() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_4, "PlusTest3(int,int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test4() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_4, "PlusTest4(int,int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test5() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_4, "getFare(int,int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }
}
