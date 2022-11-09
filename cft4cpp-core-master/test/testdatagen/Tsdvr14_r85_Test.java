package testdatagen;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Assert;
import org.junit.Test;

import config.IFunctionConfig;
import config.Paths;

public class Tsdvr14_r85_Test extends AbstractJUnitTest {
    @Test
    public void test1() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_4, "i4_log_10(int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test2() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_4, "i4_power(int,int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test3() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1_4, "r8_uniform_01(int&)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }
}
