package cfg.testpath;

/**
 * Represent a list of test paths
 *
 * @author ducanhnguyen
 */
public interface ITestpaths {

    /**
     * Get the longest test path by it real size
     *
     * @return
     */
    ITestpathInCFG getLongestTestpath();

    Testpaths cast();
}