package cfg.object;

public class MarkFlagCfgNode extends CfgNode {

	private boolean shouldInBlock = false;

	private boolean shouldInSameLine = shouldInBlock();

	public MarkFlagCfgNode(String content) {
		super(content);
	}

	@Override
	public boolean isNormalNode() {
		return true;
	}

	@Override
	public boolean shouldDisplayInCFG() {
		return true;
	}

	@Override
	public boolean shouldInBlock() {
		return shouldInBlock;
	}

	@Override
	public boolean shouldDisplayInSameLine() {
		return shouldInSameLine;
	}

	public void setInBlock(boolean shouldInBlock) {
		this.shouldInBlock = shouldInBlock;
	}

	public void setInSameLine(boolean shouldInSameLine) {
		this.shouldInSameLine = shouldInSameLine;
	}

}
