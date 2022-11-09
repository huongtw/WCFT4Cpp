package parser.makefile;

import interfaces.IGeneration;

/**
 * Generate make file
 *
 * @author ducanhnguyen
 */
public interface IMakefileGeneration extends IGeneration {
    final String EXE_NAME = "RunGoogleTest.exe";

    /**
     * Generate make file for a project
     */
    void generate();

    /**
     * Get the content of the generated make file
     *
     * @return
     */
    String getGeneratedSourcecode();

}