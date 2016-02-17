package client.communication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import server.ai.AIType;
import server.logging.LogLevel;
import shared.communication.Command;
import shared.communication.GameHeader;
import shared.communication.IServer;
import shared.communication.PlayerHeader;
import shared.communication.Session;
import shared.definitions.CatanColor;
import shared.definitions.ResourceType;
import shared.exceptions.GameInitializationException;
import shared.exceptions.GamePersistenceException;
import shared.exceptions.UserException;
import shared.exceptions.JoinGameException;
import shared.exceptions.ServerException;
import shared.locations.EdgeLocation;
import shared.locations.HexLocation;
import shared.locations.VertexLocation;
import shared.model.ResourceList;
import shared.model.ResourceTradeList;

/**
 * A fake server that will send back hard-coded information
 * This class is to be used for testing purposes only.
 * @author jchip
 *
 */
public class MockServer implements IServer {

	UUID userUUID;
	UUID gameUUID;
	
	public MockServer(){
		userUUID = UUID.fromString("f46d143e-f332-4da0-8b8f-d9c76bda4d92");
		gameUUID = UUID.fromString("3d4f073d-7acd-4cf8-8b81-5eb097b58d79");
	}
	
	@Override
	public Session login(String username, String password)
			throws UserException, ServerException {
		
		return new Session("SAM", "sam", userUUID);
	}

	@Override
	public Session register(String username, String password)
			throws UserException, ServerException {
		
		return new Session("JOE", "joe", userUUID);
	}

	@Override
	public List<GameHeader> getGameList() throws ServerException,
			UserException {
		
		List<GameHeader> returnList = new ArrayList<GameHeader>();
		List<PlayerHeader> players = new ArrayList<PlayerHeader>();
		players.add(new PlayerHeader(CatanColor.BLUE, "Jim", userUUID, 0));
		GameHeader returnGame = new GameHeader("GameTest", gameUUID, players);
		returnList.add(returnGame);
		return returnList;
	}

	@Override
	public GameHeader createGame(String name, boolean randomTiles,
			boolean randomNumbers, boolean randomPorts)
			throws GameInitializationException, UserException,
			ServerException {
		
		List<PlayerHeader> players = new ArrayList<PlayerHeader>();
		players.add(new PlayerHeader(CatanColor.BLUE, "Jim", userUUID, 0));
		GameHeader returnGame = new GameHeader("GameTest", gameUUID, players);
		
		return returnGame;
	}

	@Override
	public Session joinGame(Session player, UUID gameID, CatanColor color)
			throws JoinGameException, ServerException {
		
		return null;
	}

	@Override
	public void saveGame(UUID gameID, String filename)
			throws GamePersistenceException, UserException,
			ServerException {
	}

	@Override
	public void loadGame(String filename) throws ServerException,
			UserException {
	}

	@Override
	public String getModel(UUID gameID, int version) throws ServerException,
			UserException {
		
		try {
			return readJSON();
		} catch (IOException e) {
		}
		return null;
	}

	@Override
	public String resetGame(UUID gameID) throws ServerException,
			UserException {
		
		try {
			return readJSON();
		} catch (IOException e) {
		}
		return null;
	}

	@Override
	public List<Command> getCommands(UUID gameID) throws ServerException,
			UserException {
		return null;
	}

	@Override
	public String executeCommands(UUID gameID, List<Command> commands)
			throws ServerException, UserException {
		return null;
	}

	@Override
	public void addAIPlayer(UUID gameID, AIType type) throws ServerException,
			UserException {
		
	}

	@Override
	public List<String> getAITypes() throws ServerException,
			UserException {
		
		List<String> aiTypes = new ArrayList<String>();
		aiTypes.add("LARGEST_ARMY");
		return aiTypes;
	}

	@Override
	public String sendChat(UUID user, UUID gameID, String message)
			throws ServerException, UserException {
		
		try {
			return readJSON();
		} catch (IOException e) {
		}
		return null;
		
	}

	@Override
	public String rollDice(UUID user, UUID gameID, int number)
			throws ServerException, UserException {
		
		try {
			return readJSON();
		} catch (IOException e) {
		}
		return null;
	}

	@Override
	public String robPlayer(UUID user, UUID gameID, 
			HexLocation newRobberLocation, UUID victim)
			throws ServerException, UserException {
		
		try {
			return readJSON();
		} catch (IOException e) {
		}
		return null;
		
	}

	@Override
	public String buyDevCard(UUID user, UUID gameID) throws ServerException,
			UserException {
		
		try {
			return readJSON();
		} catch (IOException e) {
		}
		return null;
	}

	@Override
	public String yearOfPlenty(UUID user, UUID gameID, ResourceType type1,
			ResourceType type2) throws ServerException, UserException {
		
		try {
			return readJSON();
		} catch (IOException e) {
		}
		return null;
	}

	@Override
	public String roadBuilding(UUID user, UUID gameID, EdgeLocation road1,
			EdgeLocation road2) throws ServerException, UserException {
		
		try {
			return readJSON();
		} catch (IOException e) {
		}
		return null;
	}

	@Override
	public String soldier(UUID user, UUID gameID,
			HexLocation newRobberLocation, UUID victim)
			throws ServerException, UserException {
		
		try {
			return readJSON();
		} catch (IOException e) {
		}
		return null;
	}

	@Override
	public String monopoly(UUID user, UUID gameID, ResourceType type)
			throws ServerException, UserException {
		
		try {
			return readJSON();
		} catch (IOException e) {
		}
		return null;
	}

	@Override
	public String monument(UUID user, UUID gameID) throws ServerException,
			UserException {
		
		try {
			return readJSON();
		} catch (IOException e) {
		}
		return null;
	}

	@Override
	public String buildRoad(UUID user, UUID gameID, EdgeLocation location) throws ServerException, UserException {
		
		try {
			return readJSON();
		} catch (IOException e) {
		}
		return null;
	}

	@Override
	public String buildSettlement(UUID user, UUID gameID,
			VertexLocation location) throws ServerException,
			UserException {
		
		try {
			return readJSON();
		} catch (IOException e) {
		}
		return null;
	}

	@Override
	public String buildCity(UUID user, UUID gameID, VertexLocation location)
			throws ServerException, UserException {
		
		try {
			return readJSON();
		} catch (IOException e) {
		}
		return null;
	}

	@Override
	public String offerTrade(UUID user, UUID gameID, ResourceTradeList offer,
			UUID receiver) throws ServerException,
			UserException {
		
		try {
			return readJSON();
		} catch (IOException e) {
		}
		return null;
	}

	@Override
	public String respondToTrade(UUID user, UUID gameID, boolean accept)
			throws ServerException, UserException {
		
		try {
			return readJSON();
		} catch (IOException e) {
		}
		return null;
	}

	@Override
	public String maritimeTrade(UUID user, UUID gameID,
			ResourceType inResource, ResourceType outResource, int ratio)
			throws ServerException, UserException {
		
		try {
			return readJSON();
		} catch (IOException e) {
		}
		return null;
	}

	@Override
	public String discardCards(UUID user, UUID gameID, ResourceList cards)
			throws ServerException, UserException {
		
		try {
			return readJSON();
		} catch (IOException e) {
		}
		return null;
	}

	@Override
	public String finishTurn(UUID user, UUID gameID) throws ServerException,
			UserException {
		
		try {
			return readJSON();
		} catch (IOException e) {
		}
		return null;
	}

	@Override
	public void changeLogLevel(LogLevel level) throws ServerException,
			UserException {
		
	}

	private String readJSON() throws IOException{
		File input = new File("json_test.json");
		Scanner myScanner = new Scanner(input);
		myScanner.useDelimiter("blahsblasdfksldf");
		String returnThis = myScanner.next();
		myScanner.close();
		return returnThis;
	}

	@Override
	public String buildStartingPieces(UUID user, UUID gameID,
			VertexLocation settlementLoc, EdgeLocation roadLoc)
			throws ServerException, UserException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Session getPlayerSession() {
		// TODO Auto-generated method stub
		return null;
	}
}
