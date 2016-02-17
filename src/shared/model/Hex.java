package shared.model;

import java.io.Serializable;

import org.json.simple.JSONObject;

import shared.definitions.ResourceType;
import shared.exceptions.SchemaMismatchException;
import shared.locations.HexLocation;

/**
 * Represents a hex location on the map, as well as what resource it represents.
 * This class is immutable, as a hex will not be changed mid game.
 * @author Jordan
 *
 */
public class Hex 
implements Serializable {
	private static final long serialVersionUID = 5709089448387940400L;
	
	public static final int EMPTY_NUMBER = -1;
	private HexLocation location;
	private ResourceType resource;
	private int number;
	
	public Hex() {
		this(null, null, EMPTY_NUMBER);
	}
	
	public Hex(HexLocation location, ResourceType resource) {
		this(location, resource, EMPTY_NUMBER);
	}
	
	public Hex(HexLocation location, ResourceType resource, int number) {
		this.location = location;
		this.resource = resource;
		setNumber(number);
	}

	public Hex(int x, int y, ResourceType resource, int number) {
		location = new HexLocation(x, y);
		this.resource = resource;
		setNumber(number);
	}

	public Hex(JSONObject json) throws SchemaMismatchException {
		try {
			location = new HexLocation((JSONObject) json.get("location"));
			if (json.containsKey("resource")) {
				resource = ResourceType.fromString((String) json.get("resource"));
				setNumber((int) (long) json.get("number"));
			}
			else {
				resource = null;
				number = EMPTY_NUMBER;
			}
		}
		catch (ClassCastException | IllegalArgumentException e) {
			e.printStackTrace();
			throw new SchemaMismatchException("The JSON does not follow the expected schema " +
					"for a Hex:\n" + json.toJSONString());
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Hex [location=" + location + ", resource=" + resource
				+ ", number=" + number + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result + number;
		result = prime * result
				+ ((resource == null) ? 0 : resource.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Hex other = (Hex) obj;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (number != other.number)
			return false;
		if (resource != other.resource)
			return false;
		return true;
	}

	/**
	 * @return the location
	 */
	public HexLocation getLocation() {
		return location;
	}

	/**
	 * @return the resource
	 */
	public ResourceType getResource() {
		return resource;
	}

	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}
	
	private void setNumber(int number) {
		if (number != EMPTY_NUMBER && (number < 2 || number > 12 || number == 7)) {
			throw new IllegalArgumentException("Invalid number for a Hex.");
		}
		this.number = number;
	}

}
