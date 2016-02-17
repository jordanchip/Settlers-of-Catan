package client.data;

import java.util.*;

import shared.communication.GameHeader;
import shared.communication.PlayerHeader;

/**
 * Used to pass game information into views<br>
 * <br>
 * PROPERTIES:<br>
 * <ul>
 * <li>Id: Unique game ID</li>
 * <li>Title: Game title (non-empty string)</li>
 * <li>Players: List of players who have joined the game (can be empty)</li>
 * </ul>
 * 
 */
public class GameInfo
{
	private UUID uuid;
	private String title;
	private List<PlayerInfo> players;
	
	public GameInfo()
	{
		uuid = null;
		setTitle("");
		players = new ArrayList<PlayerInfo>();
	}
	
	public GameInfo(GameHeader gameHeader) {
		uuid = gameHeader.getUUID();
		setTitle(gameHeader.getTitle());
		players = new ArrayList<>();
		for (PlayerHeader player : gameHeader.getPlayers()) {
			players.add(new PlayerInfo(player));
		}
	}

	public int getId()
	{
		return Math.abs(uuid.hashCode()) % 100;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public void addPlayer(PlayerInfo newPlayer)
	{
		players.add(newPlayer);
	}
	
	public List<PlayerInfo> getPlayers()
	{
		return Collections.unmodifiableList(players);
	}
}

