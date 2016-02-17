package server.communication;

public class ServerTest {

	/*

	private static IServer p;
	private static CatanModel testModel;
	private static ModelFacade facade;
	
	@BeforeClass
	public static void setup() throws UserException, ServerException, InvalidActionException, JoinGameException {
		String username = "Sam";
		String password = "sam";
		p = new ServerProxy();
		
		p.login(username, password);
		p.joinGame(0, CatanColor.BLUE);
		
		facade = new ModelFacade();
		int version = 1;
		
		JSONObject obj = p.getModel(version);
		testModel = facade.updateFromJSON(obj);
	}
	
	@Test
	public void testLogin() throws UserException, ServerException {
		String username = "John";
		String password = "password";
		
		Session first = p.login(username, password);
	}

	@Test
	public void testRegister() throws UserException, ServerException {
		String username = "John";
		String password = "password";
		
		Session first = p.register(username, password);		
	}
	
	@Test
	public void testGetGameList() throws UserException, ServerException, InvalidActionException {
		
		List<GameHeader> gameHeaders= p.getGameList();
	}
	
	@Test
	public void testCreateGame() throws UserException, ServerException, GameInitializationException, InvalidActionException {
		
		String username = "John";
		String password = "password";
		
		Session user = p.register(username, password);
		String name = "Fun Game!";
		boolean randomTiles = false;
		boolean randomNumbers = false;
		boolean randomPorts = false;
		
		p.createGame(name, randomTiles, randomNumbers, randomPorts);
	}
	
	@Test
	public void testJoinGame() throws UserException, ServerException, JoinGameException {
		
		String username = "John";
		String password = "password";
		
		Session user = p.register(username, password);
		
		int gameID = 0;
		p.joinGame(gameID, CatanColor.BLUE);
	}
	
	
	@Test
	public void testSameGame() throws GamePersistenceException, ServerException, InvalidActionException {
		
		int gameID = 1;
		String filename = "Catan";
		p.saveGame(gameID, filename);
		
	}
	
	@Test
	public void testLoadGame() throws GamePersistenceException, ServerException, InvalidActionException {
		
		int gameID = 1;
		String filename = "Catan";
		p.saveGame(gameID, filename);
		
		p.loadGame(filename);
	}
	
	@Test
	public void testGetModel() throws UserException, ServerException, InvalidActionException{
		
		String username = "John";
		String password = "password";
		
		//Session user = p.register(username, password);
		int version = 1;
		JSONObject model = p.getModel(version);
	}
	
	@Test
	public void testResetGame() throws UserException, ServerException, GameInitializationException, InvalidActionException {
		
		String username = "John";
		String password = "password";
		
		//Session user = p.register(username, password);
		
		JSONObject model = p.resetGame();
	}
	
	@Test
	public void testGetCommands() throws UserException, ServerException, InvalidActionException {
		String username = "John";
		String password = "password";
		
		//Session user = p.register(username, password);
		
		List<Command> commands = p.getCommands();
	}
	
	@Test
	public void testExecuteCommands() throws UserException, ServerException, InvalidActionException {	
		String username = "John";
		String password = "password";
		
		//Session user = p.register(username, password);
		
		List<Command> commands = p.getCommands();
		
		JSONObject model = p.executeCommands(commands);
	}
	
	public void testAddAIPlayer() throws UserException, ServerException {
		String username = "John";
		String password = "password";
		
		//Session user = p.register(username, password);
		
		//p.addAIPlayer(user, type);
	}
	
	public void testGetAITypes() throws UserException, ServerException, InvalidActionException {
		
		String username = "John";
		String password = "password";
		
		//Session user = p.register(username, password);
		
		//p.addAIPlayer(user, type);
		
		List<String> AITypes = p.getAITypes();
	}
	
	@Test
	public void testSendChat() throws UserException, ServerException, InvalidActionException {
		
		String username = "John";
		String password = "password";
		
		//Session user = p.register(username, password);
		PlayerReference user = new PlayerReference(testModel, 1);
		
		String message = "yoyo wutup dawg";
		
		JSONObject model = p.sendChat(user, message);
	}
	
	@Test
	public void testRollDice() throws UserException, ServerException, InvalidActionException {
		String username = "John";
		String password = "password";
		
		//Session user = p.register(username, password);
		PlayerReference user = new PlayerReference(testModel, 1);
		
		int number = 1;
		
		JSONObject model = p.rollDice(user, number);
	}
	
	@Test
	public void restRobPlayer() throws UserException, ServerException, InvalidActionException {
		String username = "John";
		String password = "password";
		
		//Session user = p.register(username, password);
		
		HexLocation newRobberLocation = new HexLocation(2, 2);
		
		
		PlayerReference victim = new PlayerReference(testModel, 1);
		
		PlayerReference userReference = new PlayerReference(testModel, 2); 
		JSONObject model = p.robPlayer(userReference, newRobberLocation, victim);
	}
	
	@Test
	public void testBuyDevCard() throws UserException, ServerException, InvalidActionException {
		
		String username = "John";
		String password = "password";
		
		PlayerReference user = new PlayerReference(testModel, 1);
		JSONObject model = p.buyDevCard(user);
	}
	
	@Test
	public void testYearOfPlenty() throws UserException, ServerException, InvalidActionException {
		String username = "John";
		String password = "password";
		
		PlayerReference user = new PlayerReference(testModel, 1);
		
		JSONObject model = p.yearOfPlenty(user, ResourceType.BRICK, ResourceType.ORE);
	}
	
	@Test
	public void testRoadBuilding() throws UserException, ServerException, InvalidActionException {
		String username = "John";
		String password = "password";
		
		PlayerReference user = new PlayerReference(testModel, 1);
		
		HexLocation one = new HexLocation(1, 1);
		HexLocation two = new HexLocation(2, 2);
		
		EdgeLocation road1 = new EdgeLocation(one, EdgeDirection.North);
		EdgeLocation road2 = new EdgeLocation(two, EdgeDirection.North);
		
		JSONObject model = p.roadBuilding(user, road1, road2);
	}
	
	@Test
	public void testSolder() throws UserException, ServerException, InvalidActionException {
		String username = "John";
		String password = "password";
		
		PlayerReference user = new PlayerReference(testModel, 1);
		
		HexLocation newRobberLocation = new HexLocation(2, 2);
		
		int version = 1;
		JSONObject model = p.getModel(version);
		
		PlayerReference victim = new PlayerReference(testModel, 1);
		
		JSONObject newModel = p.soldier(user, newRobberLocation, victim);
	}
	
	@Test
	public void testMonopoly() throws UserException, ServerException, InvalidActionException {
		String username = "John";
		String password = "password";
		
		PlayerReference user = new PlayerReference(testModel, 1);
		
		JSONObject model = p.monopoly(user, ResourceType.BRICK);
	}
	
	@Test
	public void testBuildRoad() throws UserException, ServerException, InvalidActionException {
		String username = "John";
		String password = "password";
		
		PlayerReference user = new PlayerReference(testModel, 1);
		
		HexLocation one = new HexLocation(1, 1);
		EdgeLocation location = new EdgeLocation(one, EdgeDirection.North);
		
		JSONObject model = p.buildRoad(user, location, true);
	}
	
	@Test
	public void testBuildSettlement() throws UserException, ServerException, InvalidActionException {
		String username = "John";
		String password = "password";
		
		PlayerReference user = new PlayerReference(testModel, 1);
		HexLocation one = new HexLocation(1, 1);
		VertexLocation location = new VertexLocation(one, VertexDirection.East);
		
		JSONObject model = p.buildSettlement(user, location, true);
	}
	
	@Test
	public void testBuildCity() throws UserException, ServerException, InvalidActionException {
		
		String username = "John";
		String password = "password";
		
		PlayerReference user = new PlayerReference(testModel, 1);
		HexLocation one = new HexLocation(1, 1);
		VertexLocation location = new VertexLocation(one, VertexDirection.East);
		
		JSONObject model = p.buildCity(user, location);
	}
	
	@Test
	public void testOfferTrade() throws UserException, ServerException, SchemaMismatchException, InvalidActionException {	
		String username = "John";
		String password = "password";
		
		PlayerReference user = new PlayerReference(testModel, 1);
		PlayerReference secondUser = new PlayerReference(testModel, 2);
		
		JSONObject tradeJSON = new JSONObject();
		tradeJSON.put(ResourceType.BRICK.toString().toLowerCase(), 0L);
		tradeJSON.put(ResourceType.ORE.toString().toLowerCase(), 0L);
		tradeJSON.put(ResourceType.SHEEP.toString().toLowerCase(), 0L);
		tradeJSON.put(ResourceType.WHEAT.toString().toLowerCase(), -1L);
		tradeJSON.put(ResourceType.WOOD.toString().toLowerCase(), 1L);
		ResourceTradeList offer = new ResourceTradeList(tradeJSON);
		
		JSONObject model = p.offerTrade(user, offer, secondUser);
	}
	
	@Test
	public void testRespondToTrade() throws UserException, ServerException, InvalidActionException {
		String username = "John";
		String password = "password";
		
		PlayerReference user = new PlayerReference(testModel, 1);
		
		JSONObject model = p.respondToTrade(user, true);	
	}
	
	@Test
	public void testMaritimeTrade() throws UserException, ServerException, InvalidActionException {
		String username = "John";
		String password = "password";
		
		PlayerReference user = new PlayerReference(testModel, 1);
		int ratio = 3;
		
		JSONObject model = p.maritimeTrade(user, ResourceType.BRICK, ResourceType.WOOD, ratio);
	}
	
	@Test
	public void testDiscardCards() throws UserException, ServerException, InvalidActionException {
		String username = "John";
		String password = "password";
		
		PlayerReference user = new PlayerReference(testModel, 1);
		ResourceList cards = new ResourceList();
		
		JSONObject model = p.discardCards(user, cards);
	}
	
	@Test
	public void testFinishTurn() throws ServerException, InvalidActionException {
		String username = "John";
		String password = "password";
		
		PlayerReference user = new PlayerReference(testModel, 1);
		
		JSONObject model = p.finishTurn(user);
		
	}
	*/
}
