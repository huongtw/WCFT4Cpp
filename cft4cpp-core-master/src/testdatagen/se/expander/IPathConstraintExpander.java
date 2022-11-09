package testdatagen.se.expander;

/**
 * Represent constraint expand behaviour, e.g., add constraints about the range
 * of array index
 *
 * @author DucAnh
 */
public interface IPathConstraintExpander {
    /**
     * Generate new constraints based on the given constraints
     */
    void generateNewConstraints();
}
