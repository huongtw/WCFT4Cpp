package utils;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Assert;
import org.junit.Test;

import config.Paths;

public class UtilsTest {

	@Test
	public void testTransformFloatNegativeE() throws LineUnavailableException {
		Assert.assertEquals("1", Utils.transformFloatNegativeE("1E-0"));
		Assert.assertEquals("0.1", Utils.transformFloatNegativeE("1E-1"));
		Assert.assertEquals("0.01", Utils.transformFloatNegativeE("1E-2"));
		Assert.assertEquals("12.3", Utils.transformFloatNegativeE("123E-1"));
		Assert.assertEquals("123", Utils.transformFloatNegativeE("123E-0"));
		Assert.assertEquals("12.3", Utils.transformFloatNegativeE("123E-1"));
		Assert.assertEquals("0.0123", Utils.transformFloatNegativeE("123E-4"));
	}

	@Test
	public void testTransformFloatPositiveE() throws LineUnavailableException {
		Assert.assertEquals("1", Utils.transformFloatPositiveE("1E+0"));
		Assert.assertEquals("10", Utils.transformFloatPositiveE("1E+1"));
		Assert.assertEquals("100", Utils.transformFloatPositiveE("1E+2"));
		Assert.assertEquals("1230", Utils.transformFloatPositiveE("123E+1"));
		Assert.assertEquals("123", Utils.transformFloatPositiveE("123E+0"));
		Assert.assertEquals("123000", Utils.transformFloatPositiveE("123E+3"));
	}

	@Test
	public void testNameOfExeInDevCppMakefile() {
		Assert.assertEquals("Project1.exe",
				Utils.getNameOfExeInDevCppMakefile(Paths.GET_NAME_OF_EXE_IN_DEVCPP_MAKEFILE + "Makefile1.win"));
		Assert.assertEquals("BTL.exe",
				Utils.getNameOfExeInDevCppMakefile(Paths.GET_NAME_OF_EXE_IN_DEVCPP_MAKEFILE + "Makefile2.win"));

	}
}
