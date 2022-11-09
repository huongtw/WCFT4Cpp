package testdatagen.se.normalization;

import normalizer.AbstractStatementNormalizer;
import normalizer.IStatementNormalizer;
import utils.IRegex;

/**
 * Remove unnecessary characters (e.g., continuous spaces)
 *
 * @author DucAnh
 */
public class UnnecessaryCharacterNormalizer extends AbstractStatementNormalizer
        implements IPathConstraintNormalizer, IStatementNormalizer {

    @Override
    public void normalize() {
        if (originalSourcecode != null && originalSourcecode.length() > 0)
            normalizeSourcecode = deleteUnnecessaryCharacter(originalSourcecode);
        else
            normalizeSourcecode = originalSourcecode;
    }

    /**
     * Remove unnecessary characters (e.g., continuous spaces)
     *
     * @param expression
     * @return
     */
    private String deleteUnnecessaryCharacter(String expression) {
        /*
		 * Ex: s=' ' ----->s="@@@@"
		 */
        expression = expression.replaceAll("' '", "@@@@");
		/*
		 * Remove all unnecessary spaces
		 */
        expression = expression.replaceAll(IRegex.SPACES, "");
		/*
		 * Restore
		 * 
		 * Ex: s="@@@@" -----> s=' '
		 */
        expression = expression.replaceAll("@@@@", "' '");
        return expression;
    }
}
