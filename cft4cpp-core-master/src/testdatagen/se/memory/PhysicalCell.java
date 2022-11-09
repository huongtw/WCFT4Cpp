package testdatagen.se.memory;

import testdatagen.se.CustomJeval;

/**
 * Represent a physical cell. A physical cell contains value in kind of
 * number/string
 *
 * @author DucAnh
 */
public class PhysicalCell {
	public static final String DEFAULT_VALUE = "0";

	/**
	 * Represent the value of cell
	 */
	protected String value = "";

	public PhysicalCell(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = new CustomJeval().evaluate(value);
	}

	@Override
	public String toString() {
		return "(value=" + value + ")";
	}

}
