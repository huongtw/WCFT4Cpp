package testdatagen.se.normalization;

import normalizer.AbstractNormalizer;
import testdatagen.se.memory.IVariableNodeTable;

/**
 * Normalize path constraint
 *
 * @author DucAnh
 */
public abstract class AbstractPathConstraintNormalizer extends AbstractNormalizer implements IPathConstraintNormalizer {

    /**
     * Table of variables
     */
    protected IVariableNodeTable tableMapping;

    public IVariableNodeTable getTableMapping() {
        return tableMapping;
    }

    public void setTableMapping(IVariableNodeTable tableMapping) {
        this.tableMapping = tableMapping;
    }

}
