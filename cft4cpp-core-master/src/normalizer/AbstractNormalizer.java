package normalizer;

import interfaces.INormalizer;
import testdatagen.structuregen.ChangedTokens;

/**
 * Abstract class for normalization
 *
 * @author ducanhnguyen
 */
public abstract class AbstractNormalizer implements INormalizer {

    protected String normalizeSourcecode = "";

    protected String originalSourcecode = "";

    protected ChangedTokens tokens = new ChangedTokens();

    public AbstractNormalizer() {
    }

    public AbstractNormalizer(String originalSourcecode) {
        this.originalSourcecode = originalSourcecode;
    }

    @Override
    public String getNormalizedSourcecode() {
        return normalizeSourcecode;
    }

    @Override
    public void setNormalizedSourcecode(String normalizeSourcecode) {
        this.normalizeSourcecode = normalizeSourcecode;
    }

    @Override
    public String getOriginalSourcecode() {
        return originalSourcecode;
    }

    @Override
    public void setOriginalSourcecode(String originalSourcecode) {
        this.originalSourcecode = originalSourcecode;
    }

    @Override
    public ChangedTokens getTokens() {
        return tokens;
    }

    @Override
    public void setTokens(ChangedTokens tokens) {
        this.tokens = tokens;
    }
}
