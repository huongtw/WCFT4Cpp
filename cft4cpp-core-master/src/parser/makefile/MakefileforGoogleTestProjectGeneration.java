package parser.makefile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import parser.projectparser.ProjectLoader;
import tree.object.INode;
import tree.object.IProjectNode;
import utils.search.FolderNodeCondition;
import utils.search.Search;

/**
 * Generate make file for a google test project that can compiled in MingW
 */

public class MakefileforGoogleTestProjectGeneration implements IMakefileGeneration {
    final static Logger logger = Logger.getLogger(MakefileforGoogleTestProjectGeneration.class);
    /**
     * Represent content of the generated make file
     */
    private String generatedSourcecode = "";

    /**
     * List of source code files put in the make file
     */
    private List<String> nameFileSources = new ArrayList<>();

    /**
     * The path of a google test project that does not have make file. We need
     * to create a make file to run this project by MINGW
     */
    private String googleTestProjectPath = "";

    /**
     * Represent Dev-Cpp folder
     */
    private IProjectNode DevCppFolder = null;

    public MakefileforGoogleTestProjectGeneration(String pathDevCpp, List<String> nameFileSources,
                                                  String googleTestProjectPath) {
        this.nameFileSources = nameFileSources;
        DevCppFolder = new ProjectLoader().load(new File(pathDevCpp));
        this.googleTestProjectPath = googleTestProjectPath;

    }

    public static void main(String[] args) throws IOException {
        String pathDevCpp = "D:/Dev-Cpp";
        List<String> nameFileSources = new ArrayList<>();
        nameFileSources.add("simple_test_test.cpp");
        nameFileSources.add("advanced_test_test.cpp");
        String googleTestProjectPath = "D:\\RunAndCompile";

        IMakefileGeneration test = new MakefileforGoogleTestProjectGeneration(pathDevCpp, nameFileSources,
                googleTestProjectPath);
        test.generate();
        logger.debug(test.getGeneratedSourcecode());
    }

    @Override
    public void generate() {
        if (nameFileSources.size() > 0 && googleTestProjectPath.length() > 0 && DevCppFolder != null
                && IMakefileGeneration.EXE_NAME.length() > 0) {
            String content = "";
            content += "#The make file is generated automatically to run the google test project\r\n";
            content += "#Project: RunGoogleTest \r\n#Makefile created by CFT4Cpp\r\n";

            content += "CPP      = g++.exe \r\nCC       = gcc.exe\r\n";
            content += "WINDRES  = windres.exe\r\n";

            content += createObj(nameFileSources) + createLIB(DevCppFolder)
                    + createINCS(DevCppFolder, googleTestProjectPath);

            content += "BIN      = " + IMakefileGeneration.EXE_NAME
                    + "\r\nCXXFLAGS = $(CXXINCS) -std=gnu++11 \r\nCFLAGS   = $(INCS) -std=gnu++11\r\nRM       = rm.exe -f\r\n\n"
                    + ".PHONY: all all-before all-after clean clean-custom\r\n\nall: all-before $(BIN) all-after\r\n\n"
                    + "clean: clean-custom\r\n\t${RM} $(OBJ) $(BIN)\r\n\n"
                    + "$(BIN): $(OBJ)\r\n\t$(CPP) $(LINKOBJ) -o $(BIN) $(LIBS)\r\n\r\n";

            content += createTarget(nameFileSources);

            generatedSourcecode = content;
        } else
            generatedSourcecode = "#(error)";
    }

    /**
     * Create INCS and CXXINCS in makefile
     *
     * @return
     */
    private String createINCS(IProjectNode rootDevCppFolder, String googleTestProjectPath) {
        String incs = "";
        String temp = "";

		/*
         * Create INCS in makefile
		 */
        List<String> includePaths = getListGGTestLib(rootDevCppFolder, googleTestProjectPath);
        for (String includePath : includePaths)
            temp += "-I\"" + includePath + "\" ";
        incs = "INCS     = " + temp + "\r\n";

		/*
		 * Create CXXINCS in makefile
		 */
        List<INode> includeNodes = Search.searchNodes(rootDevCppFolder, new FolderNodeCondition(), "c++");
        List<String> includePaths2 = new ArrayList<>();
        for (INode includeNode : includeNodes)
            includePaths2.add(includeNode.getAbsolutePath().replaceAll("\\\\", "/"));

        for (String path : includePaths2)
            temp += "-I\"" + path + "\" ";
        incs += "CXXINCS  = " + temp + "\r\n";

        return incs;
    }

    /**
     * Create LIBS in makefile
     *
     * @return
     */
    private String createLIB(IProjectNode rootDevCppFolder) {
        String lib = "";

		/*
		 * Get path absolute of file lib in folder DevC
		 */
        List<INode> includeNodes = Search.searchNodes(rootDevCppFolder, new FolderNodeCondition(), "\\lib");
        List<String> libPaths = new ArrayList<>();
        for (INode includeNode : includeNodes)
            if (!includeNode.getAbsolutePath().contains("bin"))
                libPaths.add(includeNode.getAbsolutePath().replaceAll("\\\\", "/"));

		/*
		 * Create lib for makefile
		 */
        lib += "LIBS     = ";
        for (String libPath : libPaths)
            lib += "-L\"" + libPath + "\" ";
        lib += " -static-libgcc\r\n";

        return lib;
    }

    /**
     * Create OBJ and LINKOBJ in makefile
     *
     * @return
     */
    private String createObj(List<String> nameFileSources) {
        String obj = "";

        obj += " GoogleTest/src/gtest_main.o GoogleTest/src/gtest-all.o ";
        for (String nameFileSource : nameFileSources)
            obj += nameFileSource.substring(0, nameFileSource.lastIndexOf(".")) + ".o ";
        obj += "\r\n";

        return "OBJ      =" + obj + "LINKOBJ  =" + obj;
    }

    /**
     * Create target in makefile
     *
     * @return
     */
    private String createTarget(List<String> nameFileSources) {
        String target = "";

        target += "GoogleTest/src/gtest_main.o: GoogleTest/src/gtest_main.cc"
                + "\n\t$(CPP) -c GoogleTest/src/gtest_main.cc -o GoogleTest/src/gtest_main.o $(CXXFLAGS)\r\n\n"
                + "GoogleTest/src/gtest-all.o: GoogleTest/src/gtest-all.cc"
                + "\n\t$(CPP) -c GoogleTest/src/gtest-all.cc -o GoogleTest/src/gtest-all.o $(CXXFLAGS)\r\n\n";

		/*
		 * Files google test
		 */
        for (String nameFileSource : nameFileSources)
            target += nameFileSource.substring(0, nameFileSource.lastIndexOf(".")) + ".o" + ": " + nameFileSource
                    + "\r\n" + "\t$(CPP) -c " + nameFileSource + " -o "
                    + nameFileSource.substring(0, nameFileSource.lastIndexOf(".")) + ".o" + " $(CXXFLAGS)\r\n\r\n";
        return target;
    }

    /**
     * Get list folders "include" in Dev-C and folder google test, google
     * test/include
     *
     * @return
     */
    private List<String> getListGGTestLib(IProjectNode rootDevCppFolder, String googleTestProjectPath) {
		/*
		 * Get path of google test and google test/include
		 */
        List<INode> includeNodes = Search.searchNodes(rootDevCppFolder, new FolderNodeCondition(), "include");
        List<String> includePaths = new ArrayList<>();

        for (INode includeNode : includeNodes)
            if (!includeNode.getAbsolutePath().contains("install-tools"))
                includePaths.add(includeNode.getAbsolutePath().replaceAll("\\\\", "/"));

		/*
		 * Get path of google test and google test/include
		 */
        String temp = googleTestProjectPath.replaceAll(File.separator + File.separator, "/");
        includePaths.add(temp + "/GoogleTest");
        includePaths.add(temp + "/GoogleTest" + "/include");

        return includePaths;
    }

    @Override
    public String getGeneratedSourcecode() {
        return generatedSourcecode;
    }
}
