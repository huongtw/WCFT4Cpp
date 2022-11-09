package testdatagen.se;

import java.util.ArrayList;
import java.util.List;

import tree.object.INode;
import tree.object.Node;
import tree.object.VariableNode;

/**
 * Represent the paramaters of a function including the arguments + external
 * variables
 *
 * @author ducanhnguyen
 */
public class Parameter extends ArrayList<INode> {

    /**
     *
     */
    private static final long serialVersionUID = -2583457982870539611L;

    public Parameter() {
    }

    public Parameter(List<VariableNode> paramaters) {
        for (Node var : paramaters)
            this.add(var);
    }
}
