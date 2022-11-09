package interfaces;

import testdatagen.structuregen.ChangedTokens;

/**
 * Represent normalization behavior for source code, e.g., file level, function
 * level
 *
 * @author ducanhnguyen
 */
public interface INormalizer {
    /**
     * The value display in the result when the normalization catches an
     * unexpected error
     */
    public static final String ERROR = "//error";

    /**
     * Perform normalization
     */
    void normalize();

    /**
     * Get normalized source code
     *
     * @return
     */
    public String getNormalizedSourcecode();

    /**
     * Set normalized source code
     *
     * @param normalizedSourcecode
     */
    public void setNormalizedSourcecode(String normalizedSourcecode);

    /**
     * Get original source code
     *
     * @return
     */
    public String getOriginalSourcecode();

    /**
     * Set original source code
     *
     * @param originalSourcecode
     */
    public void setOriginalSourcecode(String originalSourcecode);

    /**
     * Get changed tokens
     *
     * @return
     */
    public ChangedTokens getTokens();

    /**
     * Set changed tokens
     *
     * @param tokens
     */
    public void setTokens(ChangedTokens tokens);

    /**
     * Should write the content of file to the hark disk or not
     *
     * @return
     */
    default boolean shouldWriteToFile() {
        return true;
    }
}
