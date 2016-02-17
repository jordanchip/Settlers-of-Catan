package shared.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import org.json.simple.JSONObject;

import shared.exceptions.SchemaMismatchException;
import shared.locations.EdgeLocation;

/**
 * Represents an object (i.e. road) that exists on the edge of a hex.
 * @author Jordan
 *
 */
public class Road 
implements Serializable {
	private static final long serialVersionUID = 1078837488509791424L;
	
	private PlayerReference owner;
	private EdgeLocation location;

	public Road() {
		
	}
	
	public Road(List<Player> players, JSONObject json) throws SchemaMismatchException {
		try {
			JSONObject playerJSON = (JSONObject) (json.get("owner"));
			UUID playerUUID = UUID.fromString((String)playerJSON.get("playerUUID"));
			owner = new PlayerReference(playerUUID);
			location = new EdgeLocation( (JSONObject)(json.get("location")));
		} catch (ClassCastException | IllegalArgumentException e) {
			e.printStackTrace();
			throw new SchemaMismatchException("The JSON does not follow the expected schema " +
					"for an EdgeObject:\n" + json.toJSONString());
		}
	}

	public Road(EdgeLocation location, PlayerReference owner) {
		this.location = location;
		this.owner = owner;
	}

	/** Gets the owner of the edge (road)
	 * @return the owner
	 */
	public PlayerReference getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(PlayerReference owner) {
		this.owner = owner;
	}

	/**
	 * @return the location
	 */
	public EdgeLocation getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(EdgeLocation location) {
		this.location = location;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Road other = (Road) obj;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		return true;
	}

}
