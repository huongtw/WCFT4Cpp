package cfg.testpath;

import interfaces.IGeneration;
import tree.object.IFunctionNode;

/**
 * Generate static solution of a test path
 *
 * @author ducanhnguyen
 */
public interface IStaticSolutionGeneration extends IGeneration {
    final String NO_SOLUTION = "";
    final String EVERY_SOLUTION = " ";

    /**
     * Generate static solution
     *
     * @return
     * @throws Exception
     */
    void generateStaticSolution() throws Exception;

    /**
     * Get test path
     *
     * @return
     */
    ITestpathInCFG getTestpath();

    /**
     * Set test path
     *
     * @param testpath
     */
    void setTestpath(AbstractTestpath testpath);

    /**
     * Get function node
     *
     * @return
     */
    IFunctionNode getFunctionNode();

    /**
     * Set function node
     *
     * @param functionNode
     */
    void setFunctionNode(IFunctionNode functionNode);

    /**
     * Get static solution
     *
     * @return
     */
    String getStaticSolution();

}