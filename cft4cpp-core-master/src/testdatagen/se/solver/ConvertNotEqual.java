package testdatagen.se.solver;

import java.util.ArrayList;

import normalizer.AbstractNormalizer;
import testdatagen.se.normalization.IPathConstraintNormalizer;

/**
 * Dùng phân tích một biểu thức logic (có chứa phép so sánh khác) v�? dạng biểu
 * diễn tương đương. Lớp này được sử dụng để chuẩn hóa biểu thức logic trước khi
 * biến đổi v�? chuẩn SMT-Lib.
 *
 * @author anhanh
 */
class ConvertNotEqual extends AbstractNormalizer implements IPathConstraintNormalizer {

    private static final char AND_SYMBOL_REPLACEMENT = '&';
    private static final char OR_SYMBOL_REPLACEMENT = '|';
    private static final String INEQUATION_SYMBOL = "!=";
    private static final String INEQUATION_SYMBOL_REPLACEMENT = "@";

    /**
     * @param originalSourcecode Biểu thức logic cần viết lại
     */
    protected ConvertNotEqual(String originalSourcecode) {
        this.originalSourcecode = standalizeInput(originalSourcecode);
    }

    public static void main(String[] args) {
        String[] examples = new String[]{"a!=b",
                "!((-M)!=(((((((((((((-N)-(-M))-(-M))-(-M))-(-M))-(-M))-(-M))-(-M))-(-M))-(-M))-(-M))-(-M))-(-M)))"};
        ConvertNotEqual c = new ConvertNotEqual(examples[0]);
        c.normalize();
        System.out.println(c.getNormalizedSourcecode());
    }

    @Override
    public void normalize() {
        normalizeSourcecode = originalSourcecode;
        for (String originalSourcecodeItem : getInequationList(normalizeSourcecode)) {
            String replacement = convert(originalSourcecodeItem);
            if (normalizeSourcecode.contains("(" + originalSourcecodeItem + ")"))
                normalizeSourcecode = normalizeSourcecode.replace("(" + originalSourcecodeItem + ")", replacement);
            else
                normalizeSourcecode = normalizeSourcecode.replace(originalSourcecodeItem, replacement);
        }

        normalizeSourcecode = normalizeSourcecode.replace(ConvertNotEqual.AND_SYMBOL_REPLACEMENT + "", "&&");
        normalizeSourcecode = normalizeSourcecode.replace(ConvertNotEqual.OR_SYMBOL_REPLACEMENT + "", "||");
    }

    /**
     * @param originalSourcecode Biểu thức logic so sánh khác. </br/>
     *                           VD1:1!=2 </br/>
     *                           VD2:a!=b</br/>
     *                           VD3:a+b!=c*3/f
     * @return Biểu diễn phép toán so sánh khác theo cách tương đương với 2 phép
     * so sánh lớn hơn và nh�? hơn.
     */
    private String convert(String originalSourcecode) {
        final String INEQUATION_SYMBOL = "!=";
        String veTrai = originalSourcecode.split(INEQUATION_SYMBOL)[0];
        String vePhai = originalSourcecode.split(INEQUATION_SYMBOL)[1];
        return "(" + veTrai + ">" + vePhai + ConvertNotEqual.OR_SYMBOL_REPLACEMENT + veTrai + "<" + vePhai + ")";
    }

    /**
     * @param expression Một biểu thức logic
     * @return Tập các biểu thức logic chứa phép so sánh khác
     */
    private ArrayList<String> getInequationList(String expression) {
        ArrayList<String> originalSourcecodeList = new ArrayList<>();
        while (expression.contains(ConvertNotEqual.INEQUATION_SYMBOL)) {
            int posInequationSymbol = expression.indexOf(ConvertNotEqual.INEQUATION_SYMBOL);
            String veTrai = MoveBackawards(expression, posInequationSymbol);
            String vePhai = MoveForwards(expression, posInequationSymbol);
            originalSourcecodeList.add(veTrai + "!=" + vePhai);
            expression = expression.replaceFirst(ConvertNotEqual.INEQUATION_SYMBOL,
                    ConvertNotEqual.INEQUATION_SYMBOL_REPLACEMENT);
        }

        return originalSourcecodeList;
    }

    /**
     * @return biểu thức logic tương đương
     */
    protected String getOutput() {
        return originalSourcecode;
    }

    /**
     * @param expression          Một biểu thức logic
     * @param posInequationSymbol Vị trí phép toán so sánh khác
     * @return veTrai Vế trái biếu thức có phép so sánh khác tại vị trí
     * posInequationSymbol
     */
    private String MoveBackawards(String expression, int posInequationSymbol) {
        String veTrai = new String();
        // do some thing here
        int numOfCloseBracket = 0;
        int numOfOpenBracket = 0;
        char T;
        do {
            posInequationSymbol--;
            T = expression.charAt(posInequationSymbol);
            switch (T) {
                case ')':
                    numOfCloseBracket++;
                    veTrai = T + veTrai;
                    break;
                case '(':
                    numOfOpenBracket++;
                    if (numOfOpenBracket <= numOfCloseBracket)
                        veTrai = T + veTrai;
                    break;
                case OR_SYMBOL_REPLACEMENT:
                case AND_SYMBOL_REPLACEMENT:
                    break;
                default:
                    veTrai = T + veTrai;
            }

        } while (!(posInequationSymbol == 0 || numOfCloseBracket < numOfOpenBracket
                || T == ConvertNotEqual.AND_SYMBOL_REPLACEMENT || T == ConvertNotEqual.OR_SYMBOL_REPLACEMENT));
        return veTrai;
    }

    /**
     * @param expression          Một biểu thức logic
     * @param posInequationSymbol Vị trí phép toán so sánh khác
     * @return veTrai Vế phải biếu thức có phép so sánh khác tại vị trí
     * posInequationSymbol
     */
    private String MoveForwards(String expression, int posInequationSymbol) {
        String vePhai = new String();
        // do some thing here
        int numOfCloseBracket = 0;
        int numOfOpenBracket = 0;
        char T;
        posInequationSymbol += 1;
        do {
            posInequationSymbol++;
            T = expression.charAt(posInequationSymbol);
            switch (T) {
                case ')':
                    numOfCloseBracket++;
                    if (numOfOpenBracket >= numOfCloseBracket)
                        vePhai = vePhai + T;
                    break;
                case '(':
                    numOfOpenBracket++;

                    vePhai = vePhai + T;
                    break;
                case OR_SYMBOL_REPLACEMENT:
                case AND_SYMBOL_REPLACEMENT:
                    break;
                default:
                    vePhai = vePhai + T;
                    break;
            }

        } while (!(posInequationSymbol == expression.length() - 1 || numOfCloseBracket > numOfOpenBracket
                || T == ConvertNotEqual.AND_SYMBOL_REPLACEMENT || T == ConvertNotEqual.OR_SYMBOL_REPLACEMENT));
        return vePhai;
    }

    /**
     * Chuẩn hóa biểu thức logic
     */
    private String standalizeInput(String originalSourcecode) {
        originalSourcecode = originalSourcecode.replace("&&", ConvertNotEqual.AND_SYMBOL_REPLACEMENT + "");
        originalSourcecode = originalSourcecode.replace("||", ConvertNotEqual.OR_SYMBOL_REPLACEMENT + "");
        return originalSourcecode;
    }

}
