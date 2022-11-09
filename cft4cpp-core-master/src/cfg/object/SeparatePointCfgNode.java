package cfg.object;

/**
 * This class prevents two nodes that can not be merged
 *
 * @author ducanh
 */
public class SeparatePointCfgNode extends CfgNode {

    @Override
    public boolean isNormalNode() {
        return false;
    }

    @Override
    public boolean shouldDisplayInCFG() {
        return false;
    }

    @Override
    public boolean shouldInBlock() {
        return false;
    }
}
