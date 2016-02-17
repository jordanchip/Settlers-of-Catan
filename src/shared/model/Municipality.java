package shared.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import org.json.simple.JSONObject;

import shared.definitions.MunicipalityType;
import shared.exceptions.InvalidActionException;
import shared.exceptions.SchemaMismatchException;
import shared.locations.VertexLocation;

/**
 * Represents a city or a settlement that has been placed on a vertex between hexes.
 * Contains reference to the vertex and the player that owns it.
 * @author Jordan
 *
 */
public class Municipality 
implements Serializable {
	private static final long serialVersionUID = 1169769148605917108L;
	
	private PlayerReference owner;
	private VertexLocation location;
	private MunicipalityType type;
	
	public Municipality() {
		
	}

	public Municipality(List<Player> players, JSONObject json, MunicipalityType type) throws SchemaMismatchException {
		try {
			int playerOwner = (int) (long) (json.get("owner"));
			owner = players.get(playerOwner).getReference();
			location = new VertexLocation((JSONObject)(json.get("location")));
			this.type = type;
		} catch (ClassCastException | IllegalArgumentException e) {
			e.printStackTrace();
			throw new SchemaMismatchException("The JSON does not follow the expected schema " +
					"for an EdgeObject:\n" + json.toJSONString());
		}
	}
	
	public Municipality(List<Player> players, JSONObject json) throws SchemaMismatchException {
//			int playerOwner = (int) (long) json.get("owner");
			JSONObject playerOwner = (JSONObject)json.get("owner");
			UUID ownerUUID = UUID.fromString((String)playerOwner.get("playerUUID"));
			owner = new PlayerReference(ownerUUID);
			location = new VertexLocation((JSONObject)(json.get("location")));
			this.type = MunicipalityType.getMunicipalityTypeFromString((String) json.get("type"));
	}

	/**
	 * @param owner
	 * @param location
	 * @param type
	 */
	Municipality(VertexLocation location, MunicipalityType type, PlayerReference owner) {
		super();
		this.owner = owner;
		this.location = location;
		this.type = type;
	}

	/**
	 * @return the owner
	 */
	public PlayerReference getOwner() {
		return owner;
	}

	/**
	 * @return the location
	 */
	public VertexLocation getLocation() {
		return location;
	}
	
	public MunicipalityType getType() {
		return type;
	}
	
	public int getPointValue() {
		return type.getPointValue();
	}
	
	/** Upgrades a settlement to a city
	 * @pre This municipality is a settlement
	 * @post This municipality will be a city
	 * @throws InvalidActionException if this is already a city
	 */
	public void upgrade() throws InvalidActionException {
		if (type != MunicipalityType.SETTLEMENT) {
			throw new InvalidActionException("Attempt to upgrade an already upgraded city.");
		}
		type = MunicipalityType.CITY;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Municipality other = (Municipality) obj;
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
		if (type != other.type)
			return false;
		return true;
	}

	public int getIncome() {
		return type.getIncome();
	}

}