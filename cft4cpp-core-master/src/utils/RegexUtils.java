package utils;

import interfaces.IUtils;

public class RegexUtils implements IUtils, IRegex {

	/**
	 * Convert a string into regex
	 *
	 * @param str
	 * @return
	 */
	public static String toRegex(String str) {
		str = str.replace(".", "\\.").replace("*", "\\*").replace(" ", "\\s*").replace("_", "\\_")
				.replace("[", "\\s*\\[\\s*").replace("]", "\\s*\\]\\s*").replace("(", "\\s*\\(\\s*")
				.replace(")", "\\s*\\)\\s*").replace("-", "\\-").replace(">", "\\>");

		// Add bound of word at the beginning
		if (str.toCharArray()[0] >= 'A' && str.toCharArray()[0] <= 'Z'
				|| str.toCharArray()[0] >= 'a' && str.toCharArray()[0] <= 'z')
			str = "\\b" + str;

		// Add bound of word at the end
		int last = str.toCharArray().length - 1;
		if (str.toCharArray()[last] >= 'A' && str.toCharArray()[last] <= 'Z'
				|| str.toCharArray()[last] >= 'a' && str.toCharArray()[last] <= 'z')
			str += "\\b";
		return str;
	}

}
