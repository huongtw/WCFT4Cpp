package cfg.object;

/**
 * Represent flag statement, e.g., "{", "}"
 *
 * @author ducvu
 */
public class FlagCfgNode extends CfgNode {

	@Override
	public boolean isNormalNode() {
		return false;
	}

	@Override
	public boolean shouldDisplayInCFG() {
		return true;
	}

	@Override
	public boolean shouldInBlock() {
		return false;
	}
}
