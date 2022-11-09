package cfg.object;

/**
 * Represent the begin node of CFG
 *
 * @author ducanh
 */
public class BeginFlagCfgNode extends FlagCfgNode {
	public static final String BEGIN_FLAG = "Begin";

	public BeginFlagCfgNode() {
		setContent(BeginFlagCfgNode.BEGIN_FLAG);
	}

}
