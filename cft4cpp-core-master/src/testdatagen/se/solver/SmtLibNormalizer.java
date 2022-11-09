package testdatagen.se.solver;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import normalizer.AbstractNormalizer;
import testdatagen.se.normalization.AbstractPathConstraintNormalizer;
import testdatagen.se.normalization.IPathConstraintNormalizer;

/**
 * @author anhanh
 */
class SmtLibNormalizer extends AbstractPathConstraintNormalizer
		implements IPathConstraintNormalizer, IConvertToSmtLibNormalizer {
	private Map<String, String> map = new HashMap<>();

	private NodeTree root;

	public SmtLibNormalizer() {
	}

	protected SmtLibNormalizer(String trungTo) {
		originalSourcecode = trungTo;
	}

	public static void main(String[] args) {
		AbstractNormalizer norm = new SmtLibNormalizer();
		norm.setOriginalSourcecode("!(a==-1+-2)");
		norm.normalize();
		System.out.println(norm.getNormalizedSourcecode());
	}

	@Override
	public void normalize() {
		if (isAvailable(originalSourcecode)) {
			originalSourcecode = tienXuLyTrungTo(originalSourcecode, map);

			String HauTo = layHauTo(originalSourcecode);

			HauTo = HauTo.replaceAll("\\s+", " ");
			root = taoCayHauTo(HauTo);
			duyetCayHauTo(root);

			normalizeSourcecode = normalizeSourcecode.replace(IConvertToSmtLibNormalizer.PHEP_AND_RUT_GON, "and");
			normalizeSourcecode = normalizeSourcecode.replace(IConvertToSmtLibNormalizer.OR_RUT_GON, "or");
			normalizeSourcecode = normalizeSourcecode.replace(IConvertToSmtLibNormalizer.LON_HON_HOAC_BANG_RUT_GON,
					">=");
			normalizeSourcecode = normalizeSourcecode.replace(IConvertToSmtLibNormalizer.NHO_HON_HOAC_BANG_RUT_GON,
					"<=");

			normalizeSourcecode = normalizeSourcecode.replace("%", IConvertToSmtLibNormalizer.PHEP_DU_SMT_FORMAT);
			normalizeSourcecode = normalizeSourcecode.replace(IConvertToSmtLibNormalizer.SO_SANH_KHAC_RUT_GON, "!");
			normalizeSourcecode = normalizeSourcecode.replace(IConvertToSmtLibNormalizer.PHEP_PHU_DINH,
					IConvertToSmtLibNormalizer.PHEP_PHU_DINH_SMT_FORMAT);

			normalizeSourcecode = thayTheKiTuTuongTrung(normalizeSourcecode, map);

			// Do SMT-Solver khong nhan dien bien mang nen chuan hoa ve bien
			// thuong
			normalizeSourcecode = normalizeSourcecode.replace("[", "_");
			normalizeSourcecode = normalizeSourcecode.replace("]", "_");
			//
		} else
			normalizeSourcecode = null;

	}

	/**
	 * Method duyet cay bieu thuc va luu ket qua (smt-libv2 format) vao mot bien
	 * toan cuc.
	 *
	 * @param root
	 *            root cua cay bieu thuc.
	 */
	private void duyetCayHauTo(NodeTree root) {
		if (root == null)
			return;
		if (root.data.equals("+") || root.data.equals("-") || root.data.equals("*") || root.data.equals("/")
				|| root.data.equals("%") || root.data.equals(IConvertToSmtLibNormalizer.LON_HON_HOAC_BANG_RUT_GON)
				|| root.data.equals(IConvertToSmtLibNormalizer.NHO_HON_HOAC_BANG_RUT_GON)
				|| root.data.equals(IConvertToSmtLibNormalizer.SO_SANH_BANG_RUT_GON)
				|| root.data.equals(IConvertToSmtLibNormalizer.PHEP_AND_RUT_GON)
				|| root.data.equals(IConvertToSmtLibNormalizer.OR_RUT_GON)
				|| root.data.equals(IConvertToSmtLibNormalizer.SO_SANH_LON_HON)
				|| root.data.equals(IConvertToSmtLibNormalizer.SO_SANH_KHAC_RUT_GON)
				|| root.data.equals(IConvertToSmtLibNormalizer.SO_SANH_NHO_HON)
				|| root.data.equals(IConvertToSmtLibNormalizer.PHEP_PHU_DINH)) {
			normalizeSourcecode += "(" + root.data + " ";
			duyetCayHauTo(root.left);
			duyetCayHauTo(root.right);
		} else // Neu la leaf
		if (root.getChild() == 0)
			normalizeSourcecode += root.data + " ";
		// Neu root la nut phai cua cay.
		if (root.isRight)
			normalizeSourcecode += ") ";
	}

	/**
	 * Method tra ve do uu tien toan tu.
	 *
	 * @param operator
	 * @return
	 */
	private int getPriority(char operator) {
		/*
		 * -1: khong can quan tam do uu tien 0: do uu tien thap nhat 3: do uu tien cao
		 * nhat
		 */
		if (operator == '(' || operator == ')')
			return -2;
		if (operator == '&' || operator == '|')
			return -1;

		if (operator == '=' || operator == '$' || operator == '@' || operator == '>' || operator == '<'
				|| operator == '#')
			return 0;

		if (operator == '+' || operator == '-')
			return 1;
		if (operator == '*' || operator == '/' || operator == '%')
			return 2;
		if (operator == '!')
			return 0;

		return -2;
	}

	private boolean isAvailable(String TrungTo) {
		// Kiem tra co so sanh khac nhau khong
		String tmp = new String(TrungTo);
		if (tmp.contains("!="))
			return false;
		//
		tmp = new String(TrungTo);
		tmp = tmp.replaceAll("\\b\\w+\\b\\(", "#");
		if (tmp.contains("#"))
			return false;

		return true;
	}

	/**
	 * Method bien doi bieu thuc trung to ve bieu thuc hau to
	 *
	 * @param trungTo
	 * @return
	 */
	private String layHauTo(String trungTo) {
		// system.out.println("layHauTo()");
		String output = "";
		Stack<Character> operators = new Stack<>();
		String nameVar = "";
		for (Character c : trungTo.toCharArray())
			switch (c) {
			case ' ':
				break;
			case '+':
			case '-':
			case '*':
			case '/':
			case '&':
			case '|':
			case '=':
			case '$':
			case '@':
			case '>':
			case '<':
			case '%':
			case '#':
			case '!':
				output += nameVar + " ";
				nameVar = "";
				/*
				 *
				 */
				while (operators.size() > 0 && getPriority(operators.peek()) >= getPriority(c)) {
					char operator = operators.pop();
					output += operator + " ";
				}
				operators.push(c);
				break;
			case '(':
				output += nameVar + " ";
				nameVar = "";
				operators.push(c);
				break;
			case ')':
				output += nameVar + " ";
				nameVar = "";
				while (operators.peek() != '(') {
					char operator = operators.pop();
					output += operator + " ";
				}
				operators.pop();
				break;
			default:
				nameVar += c;
				break;
			}
		output += nameVar + " ";
		while (operators.size() > 0) {
			char operator = operators.pop();
			output += operator + " ";
		}
		output = output.replace("  ", " ");
		// system.out.println("output=" + output);
		return output;
	}

	/**
	 * Method phan tich bieu thuc hau to de tao cay bieu thuc hoan chinh.
	 *
	 * @param hauTo
	 *            bieu thuc hau to
	 */
	private NodeTree taoCayHauTo(String hauTo) {
		// system.out.println("createTreeExpression()");
		Stack<NodeTree> stack = new Stack<>();
		NodeTree root = new NodeTree();
		for (String item : hauTo.split(" "))
			if (item.equals("+") || item.equals("-") || item.equals("*") || item.equals("/") || item.equals("%")
					|| item.equals(IConvertToSmtLibNormalizer.PHEP_AND_RUT_GON)
					|| item.equals(IConvertToSmtLibNormalizer.OR_RUT_GON)
					|| item.equals(IConvertToSmtLibNormalizer.SO_SANH_BANG_RUT_GON)
					|| item.equals(IConvertToSmtLibNormalizer.LON_HON_HOAC_BANG_RUT_GON)
					|| item.equals(IConvertToSmtLibNormalizer.NHO_HON_HOAC_BANG_RUT_GON)
					|| item.equals(IConvertToSmtLibNormalizer.SO_SANH_LON_HON)
					|| item.equals(IConvertToSmtLibNormalizer.SO_SANH_KHAC_RUT_GON)
					|| item.equals(IConvertToSmtLibNormalizer.SO_SANH_NHO_HON)) {
				NodeTree right = stack.pop();
				right.isRight = true;

				NodeTree left = stack.pop();
				left.isRight = false;
				NodeTree parent = new NodeTree(right, left, item);
				stack.push(parent);
				// dinh cua cay phai phai la phep toan cuoi cung.
				root = parent;
			} else if (item.equals(IConvertToSmtLibNormalizer.PHEP_PHU_DINH)) {
				NodeTree onlyChild = stack.pop();
				onlyChild.isRight = true;
				NodeTree parent = new NodeTree(onlyChild, null, item);
				stack.push(parent);
				root = parent;
			} else {
				NodeTree n = new NodeTree(null, null, item);
				stack.push(n);
			}
		return root;
	}

	/**
	 * Theo chuan smt-lib thi -n hay (-n) khong hop le, (-1)*n la hop le, (- 1)*n la
	 * ko hop le
	 *
	 * @param str
	 *            xau can chuyen doi
	 * @return
	 */
	private String thayTheBienAm(String str) {
		return str = str.replaceAll("\\(-(\\w+)\\)", "(-1)*$1");
	}

	/**
	 * Method chuyen cac gia tri tam thoi ve gia tri that tuong ung.
	 *
	 * @param str
	 *            xau can chuyen doi
	 * @param map
	 *            key: gia tri tam thoi; value: gia tri that
	 * @return
	 */
	private String thayTheKiTuTuongTrung(String str, Map<String, String> map) {
		String output = str;
		boolean orginal = true;
		do {
			orginal = true;
			for (String key : map.keySet())
				if (output.contains(key)) {
					String value = map.get(key);
					output = output.replaceAll("\\b" + key, value);
					orginal = false;
				}
		} while (!orginal);
		return output;
	}

	/**
	 * Method thay the so am thanh mot ten bien tam thoi. Theo chuan smt-lib thi -3
	 * hay (-3) khong hop le, (- 3) la hop le
	 *
	 * @param str
	 *            xau can chuyen doi
	 * @param map
	 *            luu tru quy tac chuyen doi.key:ten bien tam, value: gia tri that
	 * @return
	 */
	private String thayTheSoAm(String str, Map<String, String> map) {
		Pattern p = Pattern.compile("([^\\w\\)\\]])-([\\d\\.]+)");
		Matcher m = p.matcher(str);
		StringBuffer sb = new StringBuffer();

		while (m.find()) {
			String key = map.size() + "map";
			map.put(key, "(- " + m.group(2) + ")");
			m.appendReplacement(sb, m.group(1) + key);

		}
		m.appendTail(sb);
		return sb.toString();
	}

	private String thayTheSoBienMang(String str, Map<String, String> map) {
		Pattern p = Pattern.compile("\\w+(\\[[^\\]]\\])+");
		Matcher m = p.matcher(str);
		StringBuffer sb = new StringBuffer();

		while (m.find()) {
			String key = map.size() + "map";
			map.put(key, m.group(0));
			m.appendReplacement(sb, key);
		}
		m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * @param str
	 * @param map
	 * @return
	 */
	private String tienXuLyTrungTo(String str, Map<String, String> map) {
		// system.out.println("tienXuLi()");
		str = str.replace(">=", IConvertToSmtLibNormalizer.LON_HON_HOAC_BANG_RUT_GON);
		str = str.replace("<=", IConvertToSmtLibNormalizer.NHO_HON_HOAC_BANG_RUT_GON);
		str = str.replace("==", IConvertToSmtLibNormalizer.SO_SANH_BANG_RUT_GON);
		str = str.replace("&&", IConvertToSmtLibNormalizer.PHEP_AND_RUT_GON);
		str = str.replace("||", IConvertToSmtLibNormalizer.OR_RUT_GON);
		str = str.replace("!=", IConvertToSmtLibNormalizer.SO_SANH_KHAC_RUT_GON);
		// str = str.replaceAll("-[\\d.]+", "($0)");
		str = thayTheBienAm(str);
		str = thayTheSoAm(str, map);

		str = thayTheSoBienMang(str, map);
		// system.out.println("output=" + str);
		return str;
	}

	class NodeTree {

		NodeTree right, left;
		String data;
		boolean isRight = false;

		protected NodeTree() {

		}

		protected NodeTree(NodeTree right, NodeTree left, String data) {
			this.right = right;
			this.left = left;
			this.data = data;
		}

		protected int getChild() {
			int numChild = 0;
			if (right != null)
				numChild++;
			if (left != null)
				numChild++;
			return numChild;
		}
	}
}
