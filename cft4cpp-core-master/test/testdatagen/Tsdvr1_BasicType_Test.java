package testdatagen;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Assert;
import org.junit.Test;

import config.IFunctionConfig;
import config.Paths;

public class Tsdvr1_BasicType_Test extends AbstractJUnitTest {
    @Test
    public void test1() throws LineUnavailableException {

        Assert.assertEquals(true,
                generateTestdata(Paths.TSDV_R1, "IntTest(int)", null, IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test2() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "SignedIntTest(signed int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test3() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "ShortTest(short)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test4() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "SignedShortTest(signed short)", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test5() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "ShortIntTest(short int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test6() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "SignedShortIntTest(signed short int)", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test7() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "LongTest(long)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test8() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "SignedLongTest(signed long)", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test9() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "LongIntTest(long int)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test10() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "SignedLongIntTest(signed long int)", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test11() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "LongLongTest(long long)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test12() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "SignedLongLongTest(signed long long)", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test14() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "LongLongIntTest(long long int)", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test15() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "SignedLongLongIntTest(signed long long int)", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test16() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "UnsignedIntTest(unsigned int)", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test17() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "UnsignedTest(unsigned)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test18() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "UnsignedShortIntTest(unsigned short int)", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test19() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "UnsignedShortTest(unsigned short)", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test20() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "UnsignedLongIntTest(unsigned long int)", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test21() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "UnsignedLongTest(unsigned long)", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test22() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "UnsignedLongLongIntTest(unsigned long long int)", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test23() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "UnsignedLongLongTest(unsigned long long)", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test24() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "BoolTest(bool)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test25() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "CharTest(char)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test26() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "SignedCharTest(signed char)", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test27() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "UnsigedCharTest(unsigned char)", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }

    @Test
    public void test28() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "FloatTest(float)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test29() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "DoubleTest(double)", null, IFunctionConfig.BRANCH_COVERAGE 
                ));
    }

    @Test
    public void test30() throws LineUnavailableException {

        Assert.assertEquals(true, generateTestdata(Paths.TSDV_R1, "LongDoubleTest(long double)", null,
                IFunctionConfig.BRANCH_COVERAGE ));
    }
}
