package normalizer;

import java.io.File;

import config.AbstractSetting;
import config.ISettingv2;
import config.Paths;
import utils.Utils;

/**
 * Rewrite the path of g++, gcc in makefile.win (project of Dev-Cpp)
 *
 * @author DucAnh
 */
public class MakefileOfDevCppNormalizer extends AbstractMakefileNormalizer implements IMakefileNormalizer {

    public MakefileOfDevCppNormalizer() {
    }

    public MakefileOfDevCppNormalizer(File makefilePath) {
        setMakefilePath(makefilePath);
    }

    public static void main(String[] args) {
        File makefilePath = new File(Paths.BTL + File.separator + "Makefile.win");

        MakefileOfDevCppNormalizer norm = new MakefileOfDevCppNormalizer(makefilePath);
        norm.normalize();
        System.out.println(norm.getNormalizedSourcecode());
    }

    @Override
    public void normalize() {
        originalSourcecode = Utils.readFileContent(makefilePath);
        /*
		 * Ex: CPP="C:/Dev-Cpp/MinGW64/bin/g++.exe"
		 * 
		 * Ex: CC="C:/Dev-Cpp/MinGW64/bin/gcc.exe"
		 */
        normalizeSourcecode = originalSourcecode.replaceAll("CPP\\s*=\\s*.*",
                "CPP=" + Utils.putInString(AbstractSetting.getValue(ISettingv2.GNU_GPlusPlus_PATH)));
        normalizeSourcecode = normalizeSourcecode.replaceAll("CC\\s*=\\s*.*",
                "CC=" + Utils.putInString(AbstractSetting.getValue(ISettingv2.GNU_GCC_PATH)));
		/*
		 * CPP=g++.exe
		 *
		 * CC=gcc.exe
		 */
        normalizeSourcecode = normalizeSourcecode.replaceAll("CPP\\s*=\\s*g\\+\\+\\.exe",
                "CPP=" + Utils.putInString(AbstractSetting.getValue(ISettingv2.GNU_GPlusPlus_PATH)));
        normalizeSourcecode = normalizeSourcecode.replaceAll("CC\\s*=\\s*gcc\\.exe",
                "CC=" + Utils.putInString(AbstractSetting.getValue(ISettingv2.GNU_GCC_PATH)));
		/*
		 * CPP=""
		 *
		 * CC=""
		 */
        normalizeSourcecode = normalizeSourcecode.replaceAll("CPP\\s*=\\s*\"\"",
                "CPP=" + Utils.putInString(AbstractSetting.getValue(ISettingv2.GNU_GPlusPlus_PATH)));
        normalizeSourcecode = normalizeSourcecode.replaceAll("CC\\s*=\\s*\"\"",
                "CC=" + Utils.putInString(AbstractSetting.getValue(ISettingv2.GNU_GCC_PATH)));
    }

}
