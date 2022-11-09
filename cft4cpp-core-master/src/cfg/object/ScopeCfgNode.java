package cfg.object;

public class ScopeCfgNode extends CfgNode {

	public static final String SCOPE_OPEN = "{";

	public static final String SCOPE_CLOSE = "}";

	public ScopeCfgNode(String content) {
		super(content);
	}

	public ScopeCfgNode(String content, CfgNode next) {
		super(content);
		setBranch(next);
	}

	public static ScopeCfgNode newCloseScope(ICfgNode... branch) {
		ScopeCfgNode close = new ScopeCfgNode(ScopeCfgNode.SCOPE_CLOSE);

		if (branch.length == 1)
			close.setBranch(branch[0]);
		return close;
	}

	public static ScopeCfgNode newOpenScope(CfgNode... branch) {
		ScopeCfgNode open = new ScopeCfgNode(ScopeCfgNode.SCOPE_OPEN);

		if (branch.length == 1)
			open.setBranch(branch[0]);

		return open;
	}

	@Override
	public boolean isNormalNode() {
		return false;
	}

	public boolean isOpenScope() {
		return ScopeCfgNode.SCOPE_OPEN.equals(toString());
	}

	@Override
	public boolean shouldInBlock() {
		return true;
	}

}
