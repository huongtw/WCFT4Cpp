package tree.dependency;

import tree.object.INode;

public class ExtendDependency extends Dependency {

    public ExtendDependency(INode owner, INode refferedNode) {
        startArrow = owner;
        endArrow = refferedNode;
    }

}
