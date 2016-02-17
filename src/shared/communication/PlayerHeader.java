package shared.communication;

import java.io.Serializable;
import java.util.UUID;

import org.json.simple.JSONObject;

import client.data.PlayerInfo;

import shared.definitions.CatanColor;
import shared.exceptions.SchemaMismatchException;

public class PlayerHeader
implements Serializable {

	private static final long serialVersionUID = -5111975177124567347L;
	
	private int playerIndex;
	private CatanColor color;
	private String name;
	private UUID uuid;
	
	public PlayerHeader(JSONObject json) throws SchemaMismatchException {
		try {
			color = CatanColor.getColorFromString((String) json.get("color"));
			name = (String) json.get("name");
			uuid = UUID.fromString((String) json.get("uuid"));
			playerIndex = (int)(long)json.get("playerIndex");
		}
		catch (ClassCastException | IllegalArgumentException e) {
			e.printStackTrace();
			throw new SchemaMismatchException("The JSON does not follow the expected schema " +
					"for a PlayerHeader:\n" + json.toJSONString());
		}
	}
	
	public PlayerHeader(CatanColor color, String name, UUID id, int playerIndex){
		this.color = color;
		this.name = name;
		this.uuid = id;
		this.playerIndex = playerIndex;
	}
	
	public PlayerHeader(PlayerInfo player) {
		color = player.getColor();
		name = player.getName();
		uuid = player.getUUID();
		playerIndex = player.getPlayerIndex();
	}

	/**
	 * @return the color
	 */
	public CatanColor getColor() {
		return color;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return uuid.hashCode();
	}
	
	public UUID getUUID() {
		return uuid;
	}

	public int getIndex(){
		return playerIndex;
	}
}
