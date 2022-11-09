package normalizer;

import utils.IRegex;

/**
 * Ex: "!(front == NULL)" ------> "front!=NULL"
 *
 * @author DucAnh
 */
public class NullStatementNormalizer extends AbstractStatementNormalizer implements IStatementNormalizer {

    @Override
    public void normalize() {
        if (originalSourcecode != null)
            normalizeSourcecode = transformNullExpression(originalSourcecode);
    }

    /**
     * Ex: "!(front == NULL)" ------> "front!=NULL"
     *
     * @param expression
     * @return
     */
    private String transformNullExpression(String expression) {
        expression = expression.replaceAll("!\\s*\\((" + IRegex.NAME_REGEX + ")\\s*==\\s*NULL\\)", "$1!=NULL");
        expression = expression.replaceAll("!\\s*\\((" + IRegex.NAME_REGEX + ")\\s*!=\\s*NULL\\)", "$1==NULL");
        return expression;
    }

}
