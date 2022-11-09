package normalizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Convert "private" to "public" in class definition
 *
 * @author ducanhnguyen
 */
public class PrivateToPublicNormalizer extends AbstractSourcecodeFileNormalizer implements IClassNormalizer {

    public PrivateToPublicNormalizer() {
    }

    public static void main(String[] args) {
        String test = "class Category { private: int x; public: int getX(){return x;} }";

        PrivateToPublicNormalizer norm = new PrivateToPublicNormalizer();
        norm.setOriginalSourcecode(test);
        norm.normalize();
        System.out.println(norm.getNormalizedSourcecode());
    }

    @Override
    public void normalize() {
        if (originalSourcecode != null && originalSourcecode.length() > 0) {
            Pattern p = Pattern.compile("private(\\s*:)");
            Matcher m = p.matcher(originalSourcecode);
            StringBuffer sb = new StringBuffer(originalSourcecode.length());

            while (m.find()) {
                String after = m.group(1);
                m.appendReplacement(sb, Matcher.quoteReplacement("public " + after));
            }

            m.appendTail(sb);
            normalizeSourcecode = sb.toString();
        }
    }
}
