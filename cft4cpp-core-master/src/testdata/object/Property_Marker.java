package testdata.object;

/**
 * Represent a property of a statement
 * 
 * @author Duc Anh Nguyen
 *
 */
public class Property_Marker {
	private String key;
	private String value;

	public Property_Marker(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "(" + key + ", " + value + ")";
	}
}
