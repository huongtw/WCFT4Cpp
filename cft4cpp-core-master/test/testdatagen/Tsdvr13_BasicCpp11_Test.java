package testdatagen;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Assert;
import org.junit.Test;

import config.IFunctionConfig;
import config.Paths;

public class Tsdvr13_BasicCpp11_Test extends AbstractJUnitTest {

    @Test
    public void test1() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_3, "AutoKwTest(int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test2() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_3, "AutoKwTest2(int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test3() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_3, "AutoKwTest3(int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test4() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_3, "NullPtrTest(int*)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

}
