package testdatagen.se.normalization;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import normalizer.AbstractStatementNormalizer;
import normalizer.IStatementNormalizer;
import utils.Utils;

/**
 * Ex: 'a' -----convert---> 65
 *
 * @author DucAnh
 */
public class CharToNumberNormalizer extends AbstractStatementNormalizer
        implements IPathConstraintNormalizer, IStatementNormalizer {
    public static void main(String[] args) {
        String[] tests = new String[]{"while (a[c] != '\0'){", "while (a[c] != '0'){"};

        for (String test : tests) {
            CharToNumberNormalizer norm = new CharToNumberNormalizer();
            norm.setOriginalSourcecode(test);
            norm.normalize();
            System.out.println(norm.getNormalizedSourcecode());
        }
    }

    @Override
    public void normalize() {
        if (originalSourcecode != null && originalSourcecode.length() > 0)
            normalizeSourcecode = convertCharToNumber(originalSourcecode);
        else
            normalizeSourcecode = originalSourcecode;
    }

    /**
     * Ex: 'a' -----convert---> 65
     *
     * @param expression
     * @return
     */
    private String convertCharToNumber(String expression) {
        Pattern p = Pattern.compile("'(.{1})'");

        Matcher m = p.matcher(expression);
        StringBuffer sb = new StringBuffer(expression.length());

        while (m.find()) {
            String str = m.group(1);
            m.appendReplacement(sb, Utils.getASCII(str.toCharArray()[0]) + "");
        }

        m.appendTail(sb);
        return sb.toString();
    }

}
