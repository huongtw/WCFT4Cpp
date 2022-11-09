package testdatagen.se.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTArraySubscriptExpression;

import normalizer.AbstractNormalizer;
import testdatagen.se.CustomJeval;
import testdatagen.se.normalization.AbstractPathConstraintNormalizer;
import testdatagen.se.normalization.IPathConstraintNormalizer;
import utils.ASTUtils;
import utils.IRegex;
import utils.Utils;

/**
 *
 * @author anhanh
 */
@Deprecated
public class SmtLibv2Normalizer extends AbstractPathConstraintNormalizer implements IPathConstraintNormalizer {
	public final String PREFIX_MARK = "tyuio"; // default

	public SmtLibv2Normalizer(String expression) {
		originalSourcecode = expression;
	}

	public SmtLibv2Normalizer() {
	}

	public static void main(String[] args) {
		String[] samples = new String[] { "((((-((-(tvw_a)+(-1)*1+0))+0))+1+0))>0",
				"((tvwb_w)/((tvwhe)*(tvwhe)/10000))<19",
				"!((tvwkey)==tvwarray[(to_int*(((0)+(to_int*((tvwsize)+0)))/2+0))+0])",
				"tvwp[0+0+0][0+0+0]>=(-10)&&tvwp[0+0+0][0+0+0]<=20",
				"(to_int*(16807*((tvwseed)-(to_int*((tvwseed)/127773))*127773)-(to_int*((tvwseed)/127773))*2836))<0" };

		AbstractNormalizer norm = new SmtLibv2Normalizer();
		norm.setOriginalSourcecode(samples[0]);
		norm.normalize();
		System.out.println(norm.getNormalizedSourcecode());
	}

	@Override
	public void normalize() {
		ConvertNotEqual equalNorm = new ConvertNotEqual(originalSourcecode);
		equalNorm.normalize();
		normalizeSourcecode = equalNorm.getNormalizedSourcecode();

		Map<String, String> arrayItemMap = new HashMap<>();
		while (normalizeSourcecode.contains("[")) {
			ArrayList<String> arrayItems = getArrayItemList(normalizeSourcecode);

			for (String arrayItem : arrayItems)

				if (isSimpleArrayItem(arrayItem)) {
					String arrayItemSmtLib = "(" + Utils.getNameVariable(arrayItem) + " ";

					for (String index : Utils.getIndexOfArray(arrayItem)) {
						String shortenIndex = new CustomJeval().evaluate(index);

						if (shortenIndex.matches(IRegex.INTEGER_NUMBER_REGEX))
							arrayItemSmtLib += shortenIndex + " ";
						else {
							SmtLibNormalizer norm = new SmtLibNormalizer();
							norm.setOriginalSourcecode(index);
							norm.normalize();

							if (norm.getNormalizedSourcecode().startsWith("("))
								arrayItemSmtLib += norm.getNormalizedSourcecode() + " ";
							else
								arrayItemSmtLib += "(" + norm.getNormalizedSourcecode() + ")" + " ";
						}

					}

					arrayItemSmtLib += ") ";
					//
					String newName = PREFIX_MARK + arrayItemMap.size();
					arrayItemMap.put(newName, arrayItemSmtLib);
					normalizeSourcecode = normalizeSourcecode.replace(arrayItem, newName);
				}
		}

		//
		SmtLibNormalizer norm = new SmtLibNormalizer();
		norm.setOriginalSourcecode(normalizeSourcecode);
		norm.normalize();
		normalizeSourcecode = norm.getNormalizedSourcecode();

		//
		for (String arrayItem : arrayItemMap.keySet())
			normalizeSourcecode = normalizeSourcecode.replace(arrayItem, arrayItemMap.get(arrayItem));
	}

	/**
	 * @param arrayItem
	 * @return true if the input does not contains array item inside it.
	 */
	private boolean isSimpleArrayItem(String arrayItem) {
		int numArray = 0;
		for (Character ch : arrayItem.toCharArray()) {
			if (ch == '[')
				numArray++;
			else if (ch == ']')
				numArray--;
			if (numArray == 2)
				return false;
		}
		return true;

	}

	private ArrayList<String> getArrayItemList(String expression) {
		ArrayList<String> arrayItemList = new ArrayList<>();
		List<ICPPASTArraySubscriptExpression> arrayItemASTs = Utils
				.getArraySubscriptExpression(ASTUtils.convertToIAST(expression));

		for (ICPPASTArraySubscriptExpression arrayItemAST : arrayItemASTs)
			if (!arrayItemList.contains(arrayItemAST.getRawSignature()))
				arrayItemList.add(arrayItemAST.getRawSignature());
		return arrayItemList;
	}
}
