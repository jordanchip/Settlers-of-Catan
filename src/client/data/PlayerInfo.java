package client.data;

import java.util.UUID;

import shared.communication.PlayerHeader;
import shared.definitions.*;
import shared.model.Player;

/**
 * Used to pass player information into views<br>
 * <br>
 * PROPERTIES:<br>
 * <ul>
 * <li>Id: Unique player ID</li>
 * <li>PlayerIndex: Player's order in the game [0-3]</li>
 * <li>Name: Player's name (non-empty string)</li>
 * <li>Color: Player's color (cannot be null)</li>
 * </ul>
 * 
 */
public class PlayerInfo
{
	
	private UUID uuid;
	private int playerIndex;
	private String name;
	private CatanColor color;
	
	public PlayerInfo()
	{
		setUUID(null);
		setPlayerIndex(-1);
		setName("");
		setColor(CatanColor.WHITE);
	}
	
	public PlayerInfo(PlayerHeader player) {
		setUUID(player.getUUID());
		setPlayerIndex(player.getIndex());
		setName(player.getName());
		setColor(player.getColor());
	}
	
	public PlayerInfo(Player player) {
		
		setUUID(player.getUUID());
		setPlayerIndex(player.getPlayerIndex());
		setName(player.getName());
		setColor(player.getColor());
	}

	public int getId()
	{
		return uuid.hashCode();
	}
	
	public void setUUID(UUID id)
	{
		this.uuid = id;
	}
	
	public int getPlayerIndex()
	{
		return playerIndex;
	}
	
	public void setPlayerIndex(int playerIndex)
	{
		this.playerIndex = playerIndex;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public CatanColor getColor()
	{
		return color;
	}
	
	public void setColor(CatanColor color)
	{
		this.color = color;
	}

	@Override
	public int hashCode()
	{
		return 31 * uuid.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final PlayerInfo other = (PlayerInfo) obj;
		
		return this.uuid.equals(other.uuid);
	}
	
	public boolean otherEquals(Object obj) {
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final PlayerInfo other = (PlayerInfo) obj;
		
		return this.name.equals(other.name);
	}

	public UUID getUUID() {
		return uuid;
	}
}

