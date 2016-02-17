package shared.communication;

import java.util.List;
import java.util.UUID;

import server.ai.AIType;
import server.logging.LogLevel;
import shared.definitions.*;
import shared.exceptions.GameInitializationException;
import shared.exceptions.GamePersistenceException;
import shared.exceptions.UserException;
import shared.exceptions.JoinGameException;
import shared.exceptions.ServerException;
import shared.locations.*;
import shared.model.*;

public interface IServer {

	/**
	 * @pre user with username and corresponding password already exists
	 * @post player is signed in and can play.
	 * @author-Grant
	 */
	public Session login(String username, String password)
			throws UserException, ServerException;
	/**
	 * @pre username must be 3-7 characters long.
	 *   Password is at least 5 characters long.  Alphanumerics, underscores, and hyphens not allowed
	 * @post a new user is created and his username and password are stored
	 * @author-Grant
	 */
	public Session register(String username, String password)
			throws UserException, ServerException;
	
	/**
	 * @pre there must be at least one game active
	 * @post a list is generated of game headers
	 * @author-Grant
	 */
	public List<GameHeader> getGameList()
			throws ServerException, UserException;
	/**
	 * @pre user is valid.
	 * game is given a name that is not an empty string
	 * @post a game is created
	 * @author-Grant
	 */
	public GameHeader createGame(String name, boolean randomTiles,
			boolean randomNumbers, boolean randomPorts)
					throws GameInitializationException, UserException, ServerException;
	/**
	 * @pre must be a game to join with already existing gameID
	 * color cannot be taken by another player
	 * @post player joins the game
	 * @author-Grant
	 */
	public Session joinGame(Session player, UUID gameID, CatanColor color)
			throws JoinGameException, ServerException;
	/**
	 * @pre game filename must not be empty string
	 * @post a game is saved with a corresponding filename
	 * @author-Grant
	 */
	public void saveGame(UUID gameID, String filename)
			throws GamePersistenceException, UserException, ServerException;
	/**
	 * @pre a game must exist with a corresponding filename to the filename parameter
	 * @post game is loaded
	 * @author-Grant
	 */
	public void loadGame(String filename)
			throws ServerException, UserException;
	/**
	 * @pre version must be valid
	 * @post corresponding model is returned
	 * @author-Grant
	 */
	public String getModel(UUID gameID, int version)
			throws ServerException, UserException;
	/**
	 * @pre
	 * @post game is reset
	 * @author-Grant
	 */
	public String resetGame(UUID gameID)
			throws ServerException, UserException;
	/**
	 * @pre
	 * @post a list of commands is returned
	 * @author-Grant
	 */
	public List<Command> getCommands(UUID gameID)
			throws ServerException, UserException;
	/**
	 * @pre list of commands must not be empty
	 * @post commands will be executed
	 * @author-Grant
	 */
	public String executeCommands(UUID gameID, List<Command> commands)
			throws ServerException, UserException;
	/**
	 * @pre AI type must be specified and valid
	 * @post an AIPlayer must be added
	 * @author-Grant
	 */
	public void addAIPlayer(UUID gameID, AIType type)
			throws ServerException, UserException;
	/**
	 * @pre
	 * @post the list of AITypes are returned
	 * @author-Grant
	 */
	public List<String> getAITypes()
			throws ServerException, UserException;
	/**
	 * @pre message string must not be empty
	 * @post message is sent
	 * @author-Grant
	 */
	public String sendChat(UUID user, UUID gameID, String message)
			throws ServerException, UserException;
	/**
	 * @pre
	 * @post dice is rolled
	 * @author-Grant
	 */
	public String rollDice(UUID user, UUID gameID, int number)
			throws ServerException, UserException;
	/**
	 * @pre a robber cannot be placed on desert tile.  There must be players on chosen HexLocation.
	 * victim must have at least one card to steal from him.  Otherwise, no card is stolen
	 * @post robber is on placed tile and victim loses a card
	 * @author-Grant
	 */
	public String robPlayer(UUID user, UUID gameID,
			HexLocation newRobberLocation, UUID victim)
					throws ServerException, UserException;
	/**
	 * @pre
	 * @post Development card is bought
	 * @author-Grant
	 */
	public String buyDevCard(UUID user, UUID gameID)
			throws ServerException, UserException;
	/**
	 * @pre two types of valid resources are specified
	 * player must have yearOfPlenty card
	 * @post player receives two specified resources
	 * @author-Grant
	 */
	public String yearOfPlenty(UUID user, UUID gameID, ResourceType type1, ResourceType type2)
			throws ServerException, UserException;
	/**
	 * @pre edge locations must be connected to exisiting road that is owned by player
	 * player must have roadbuilding card
	 * @post two roads are built at specified edge locations
	 * @author-Grant
	 */
	public String roadBuilding(UUID user, UUID gameID, EdgeLocation road1, EdgeLocation road2)
			throws ServerException, UserException;
	/**
	 * @pre a robber cannot be placed on desert tile.  There must be players on chosen HexLocation.
	 * victim must have at least one card to steal from him.  Otherwise, no card is stolen
	 * player must have soldier card
	 * @post robber is on placed tile and victim loses a card
	 * @author-Grant
	 */
	public String soldier(UUID user, UUID gameID,
			HexLocation newRobberLocation, UUID victim)
					throws ServerException, UserException;
	/**
	 * @pre one type of valid resource is specified
	 * player must have monopoly card
	 * @post player receives all cards of specified resource type from all other players
	 * @author-Grant
	 */
	public String monopoly(UUID user, UUID gameID, ResourceType type)
			throws ServerException, UserException;
	
	/**
	 * 
	 */
	public String monument(UUID user, UUID gameID)
			throws ServerException, UserException;
	
	/**
	 * @pre edge location is specified and must be connected to existing road, settlement, or city owned by player
	 * @post road is built at specified location
	 * @author-Grant
	 */
	public String buildRoad(UUID user, UUID gameID, EdgeLocation location)
			throws ServerException, UserException;
	/**
	 * @pre vertex location is specified and must be connected to road owned by player.  Must be at least two
	 * edges away from other existing settlements and cities
	 * @post settlement is built at existing location
	 * @author-Grant
	 */
	public String buildSettlement(UUID user, UUID gameID, VertexLocation location)
			throws ServerException, UserException;
	

	public String buildStartingPieces(UUID user, UUID gameID, VertexLocation settlementLoc,
									EdgeLocation roadLoc)
			throws ServerException, UserException;
	
	/**
	 * @pre vertex location is specified and settlement that is owned by the player already exists at vertex location
	 * @post city replaces settlement
	 * @author-Grant
	 */
	public String buildCity(UUID user, UUID gameID, VertexLocation location)
			throws ServerException, UserException;
	/**
	 * @pre offer is specified and player must have at least as many resources in his hand as his offer does
	 * @post offer is made to another player
	 * @author-Grant
	 */
	public String offerTrade(UUID user, UUID gameID, ResourceTradeList offer,
			UUID receiver)
					throws ServerException, UserException;
	/**
	 * @pre user chooses to accept or decline trade
	 * @post trade is declined or accepted according to user's decision
	 * @author-Grant
	 */
	public String respondToTrade(UUID user, UUID gameID, boolean accept)
			throws ServerException, UserException;
	/**
	 * @pre user must have correct ratio for trade that he wants to do
	 * ratios are obtained when you have settlements or cities on sea ports at the edge
	 * of the board.  
	 * @post user trades in specified resources for specified resource
	 * @author-Grant
	 */
	public String maritimeTrade(UUID user, UUID gameID, 
			ResourceType inResource, ResourceType outResource, int ratio)
					throws ServerException, UserException;
	/**
	 * @pre seven must be rolled and player must have more than 7 cards
	 * @post player loses half of his cards, rounding down
	 * @author-Grant
	 */
	public String discardCards(UUID user, UUID gameID, ResourceList cards)
			throws ServerException, UserException;
	/**
	 * @pre player must have rolled
	 * @post player ends turn
	 * @author-Grant
	 */
	public String finishTurn(UUID user, UUID gameID)
			throws ServerException, UserException;
	/**
	 * @pre
	 * @post
	 * 
	 */
	public void changeLogLevel(LogLevel level)
			throws ServerException, UserException;
	public Session getPlayerSession();
	
}