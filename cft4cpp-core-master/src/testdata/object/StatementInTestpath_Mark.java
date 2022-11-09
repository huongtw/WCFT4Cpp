package testdata.object;

import java.util.HashSet;
import java.util.Set;

public class StatementInTestpath_Mark {
	private Set<Property_Marker> properties = new HashSet<>();

	public StatementInTestpath_Mark() {

	}

	public StatementInTestpath_Mark(Property_Marker property) {
		this.properties.add(property);
	}

	public StatementInTestpath_Mark(Property_Marker[] properties) {
		for (Property_Marker property : properties)
			this.properties.add(property);
	}

	public Set<Property_Marker> getProperties() {
		return properties;
	}

	public void setProperties(Set<Property_Marker> properties) {
		this.properties = properties;
	}

	public Property_Marker getPropertyByName(String name) {
		for (Property_Marker property : properties)
			if (property.getKey().equals(name))
				return property;
		return null;
	}

	@Override
	public String toString() {
		String output = "";
		for (Property_Marker property : properties)
			output += property.toString();
		output += "";
		return output;
	}
}
