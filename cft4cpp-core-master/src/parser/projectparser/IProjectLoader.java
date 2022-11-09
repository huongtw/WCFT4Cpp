package parser.projectparser;

import java.io.File;

import interfaces.IGeneration;
import tree.object.INode;
import tree.object.ProjectNode;

/**
 * @author DucAnh
 */
public interface IProjectLoader extends IGeneration {

    static final int OBJECT = 6;
    static final int EXE = 5;
    static final int FOLDER = 3;
    static final int CPP_FILE = 2;
    static final int C_FILE = 1;
    static final int HEADER_FILE = 0;
    static final int UNDEFINED_COMPONENT = -1;
    static final String EXE_SYMBOL = ".exe";

    /**
     * Contains signals for detecting kind of project
     */
    // Represent project that has created by using Dev-Cpp
    static final String MAKEFILE_IN_DEVCPP_SYMBOL = "Makefile.win";
    // Represent project that has created by using Visual studio
    static final String MAKEFILE_IN_VISUAL_LEVEL2 = ".vcxproj";
    static final String MAKEFILE_IN_VISUAL_LEVEL1 = ".sln";
    // Represent project that has an own makefile named Makefile. In Unix, we
    // use command "make all" to run this makefile.
    static final String MAKEFILE_IN_PROJECT_SYMBOL = "Makefile";

    /**
     * Contains signals for detecting kind of file
     */
    static final String CPP_FILE_SYMBOL = ".cpp";
    static final String C_FILE_SYMBOL = ".c";
    static final String OBJECT_FILE_SYMBOL = ".o";
    static final String HEADER_FILE_SYMBOL_TYPE_1 = ".h";
    static final String HEADER_FILE_SYMBOL_TYPE_2 = ".hh";
    static final String[] IGNORED_FILE_SYMBOLS = {};

    /**
     * Construct structure path for the given C/C++ project
     *
     * @param projectPathFile
     * @return
     */
    ProjectNode load(File projectPath);

    /**
     * Generate an unique id for all nodes in the structure tree.
     *
     * @param root
     */
    void generateId(INode root);
}
