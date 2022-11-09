package externalvariable;

import java.util.List;

import tree.object.IFunctionNode;
import tree.object.IVariableNode;
import utils.search.ISearch;

/**
 * Find all external variables of a function
 *
 * @author ducanhnguyen
 */
public interface IExternalVariableDetecter extends ISearch {
    /**
     * Find external variables of a function
     *
     * @return
     */
    List<IVariableNode> findExternalVariables();

    IFunctionNode getFunction();

    void setFunction(IFunctionNode function);
}