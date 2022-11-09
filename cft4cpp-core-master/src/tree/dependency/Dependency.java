package tree.dependency;

import tree.object.INode;

/**
 * If A use/extend B then A and B are start of arrow and end of arrow,
 * respectively
 *
 * @author DucAnh
 */
public abstract class Dependency {

    protected INode startArrow;
    protected INode endArrow;

    public Dependency() {
    }

    public Dependency(INode startArrow, INode endArrow) {
        this.startArrow = startArrow;
        this.endArrow = endArrow;
    }

    public INode getEndArrow() {
        return endArrow;
    }

    public void setEndArrow(INode endArrow) {
        this.endArrow = endArrow;
    }

    public INode getStartArrow() {
        return startArrow;
    }

    public void setStartArrow(INode startArrow) {
        this.startArrow = startArrow;
    }

    @Override
    public String toString() {
        return startArrow.getAbsolutePath() + " -> "
                + endArrow.getAbsolutePath();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Dependency) {
            Dependency objCast = (Dependency) obj;
            if (objCast.getEndArrow().equals(endArrow)
                    && objCast.getStartArrow().equals(startArrow))
                return true;
            else
                return false;
        } else
            return false;
    }
}
