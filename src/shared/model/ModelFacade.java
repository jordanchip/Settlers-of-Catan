package shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import client.misc.ClientManager;
import shared.IDice;
import shared.NormalDice;
import shared.communication.GameHeader;
import shared.communication.Session;
import shared.definitions.CatanColor;
import shared.definitions.DevCardType;
import shared.definitions.ResourceType;
import shared.definitions.TurnStatus;
import shared.exceptions.GameInitializationException;
import shared.exceptions.InsufficientResourcesException;
import shared.exceptions.InvalidActionException;
import shared.exceptions.NotYourTurnException;
import shared.exceptions.SchemaMismatchException;
import shared.exceptions.TradeException;
import shared.locations.EdgeLocation;
import shared.locations.HexLocation;
import shared.locations.VertexLocation;

public class ModelFacade 
implements Serializable {
	private static final long serialVersionUID = -8878435068519246110L;

	private final int NUMPLAYERS = 4;

	protected CatanModel model;
	private IDice dice;
	protected transient List<IModelListener> listeners;

	public ModelFacade() throws GameInitializationException {
		this(new CatanModel(false, false, false), new NormalDice());
	}

	public ModelFacade(CatanModel startingModel) {
		this(startingModel, new NormalDice());
	}

	public ModelFacade(CatanModel startingModel, IDice dice) {
		model = startingModel;
		
		this.dice = dice;
		
		listeners = new ArrayList<>();
	}

	// We probably SHOULDN'T have this... but it's kind of too late now.
	// As of the time of writing this comment, it's used in 57 places in the project.
	public synchronized CatanModel getCatanModel() {
		return model;
	}

	public synchronized PlayerReference getCurrentPlayer() {
		if (model.getTurnTracker() == null)
			return null;
		return model.getTurnTracker().getCurrentPlayer();
	}

	public synchronized GameHeader getGameHeader() {
		return model.getHeader();
	}

	/**
	 * @return true if it is the players turn, and 
	 * they haven't rolled already
	 * @return false otherwise
	 */
	public synchronized boolean canRoll(PlayerReference player) {
		return currentPhase() == TurnStatus.Rolling &&
				getCurrentPlayer().equals(player) &&
				!player.getPlayer().hasRolled();
	}
	
	public synchronized void rollDice(PlayerReference player, Integer num) throws InvalidActionException {
		if (!canRoll(player)) {
			throw new InvalidActionException();
		}
		
		model.roll(dice.roll(num));
	}

	public synchronized void rollDice(PlayerReference player) throws InvalidActionException {
		if (!canRoll(player)) {
			throw new InvalidActionException();
		}
		
		model.roll(dice.roll());
	}
	

	/**
	 * @param hexLoc The location on the map where the robber is to be placed
	 * @return true if the hex is not a desert hex.
	 * @return false otherwise
	 */
	public synchronized boolean canMoveRobberTo(HexLocation hexLoc) {		
		return model.getMap().canMoveRobberTo(hexLoc);
	}

	public synchronized boolean canRob(PlayerReference player, HexLocation loc, PlayerReference victim) {
		if (canMoveRobberTo(loc) && isTurn(player) &&
				currentPhase() == TurnStatus.Robbing &&
				!player.equals(victim)) {
			for (Municipality town : model.getMap().getMunicipalitiesAround(loc)) {
				if (town.getOwner().equals(victim)) {
					return true;
				}
			}
			return false;
		}
		else return false;
	}

	public synchronized void rob(PlayerReference player, HexLocation loc, PlayerReference victim)
			throws InvalidActionException {
		if (!canMoveRobberTo(loc)) {
			throw new InvalidActionException("Invalid Robber placement!");
		}
		if (!isTurn(player)) {
			throw new NotYourTurnException();
		}
		if (currentPhase() != TurnStatus.Robbing) {
			throw new InvalidActionException("You may not rob at this time.");
		}
		if (player.equals(victim)) {
			throw new InvalidActionException("You cannot rob yourself.");
		}
		
		model.rob(player, loc, victim, false);
	}
	
	public synchronized boolean canDiscard(PlayerReference player,
			Map<ResourceType, Integer> toDiscard) throws InsufficientResourcesException {
		return model.canDiscard(player, toDiscard);		
	}
	
	public synchronized void discard(PlayerReference player,
			Map<ResourceType, Integer> toDiscard) throws InsufficientResourcesException {
		if (!canDiscard(player, toDiscard)) {
			canDiscard(player, toDiscard);
			throw new InsufficientResourcesException();
		}
		model.discard(player, toDiscard);		
	}
	
	// This is needed for reflection to work
	public synchronized void discard(PlayerReference player,
			HashMap<ResourceType, Integer> toDiscard) throws InsufficientResourcesException {
		discard(player, (Map<ResourceType, Integer>) toDiscard);
	}
	/**
	 * 
	 * @return true if the player has already rolled the die
	 * @return false otherwise
	 */
	public synchronized boolean canFinishTurn() {
		return canFinishTurn(getCurrentPlayer());
	}

	/**
	 * 
	 * @return true if the player has already rolled the die
	 * @return false otherwise
	 */
	public synchronized boolean canFinishTurn(PlayerReference player) {
		return model.getTradeOffer() == null && isTurn(player) && player.getPlayer().hasRolled();
	}

	public synchronized void finishTurn(PlayerReference player) throws InvalidActionException {
		if (!canFinishTurn(player)) {
			throw new InvalidActionException();
		}
		
		model.finishTurn();
	}

	/**
	 * 
	 * @return true if the player has at least one wool, one stone, and one wheat
	 * in their current hand.
	 * @return false if otherwise
	 */
	public synchronized boolean canBuyDevelopmentCard(PlayerReference player) {
		if (!getCurrentPlayer().equals(player)) {
			return false;
		}
		// Make sure there are development cards in the bank.
		if (getCatanModel().getBank().getDevCards().count() <= 0) {
			return false;
		}
		
		return player.getPlayer().canBuyDevCard();
	}
	
	public synchronized boolean canBuyDevelopmentCard() {
		return canBuyDevelopmentCard(getCurrentPlayer());
	}

	public synchronized void buyDevelopmentCard(PlayerReference player)
			throws InvalidActionException {
		model.buyDevCard(player);
	}

	/**
	 * 
	 * @param edgeLoc The location (one of the 6 sides of one hex on the board)
	 *  where the road is to be placed.
	 * @return true if the given edge location is adjacent to at least one of
	 * the player's roads or municipalities (city or settlement), and that there is
	 * no currently placed road at that location.
	 * @return false otherwise
	 */
	public synchronized boolean canBuildRoad(EdgeLocation edgeLoc) {			
		return canBuildRoad(getCurrentPlayer(), edgeLoc);
	}

	/**
	 * 
	 * @param edgeLoc The location (one of the 6 sides of one hex on the board)
	 *  where the road is to be placed.
	 * @return true if the given edge location is adjacent to at least one of
	 * the player's roads or municipalities (city or settlement), and that there is
	 * no currently placed road at that location.
	 * @return false otherwise
	 */
	public synchronized boolean canBuildRoad(PlayerReference player, EdgeLocation edgeLoc) {			
		
		try {
			return player.getPlayer().canBuildRoad() &&
					model.getMap().canBuildRoadAt(player, edgeLoc);
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
		
	}

	public synchronized void buildRoad(PlayerReference player, EdgeLocation loc)
			throws InvalidActionException {
		if (!isTurn(player)) {
			throw new NotYourTurnException();
		}
		TurnStatus phase = currentPhase();
		if (phase == TurnStatus.FirstRound || phase == TurnStatus.SecondRound) {
			throw new InvalidActionException("You cannot build a road during setup; " +
					"Use the buildStartingPieces method instead.");
		}
		else {
			model.buildRoad(player, loc);
		}
	}

	/**
	 * 
	 * @param vertexLoc The location (one of the 6 vertices of one hex on the board)
	 * where the settlement is to be placed.
	 * @return true if the given vertex location is adjacent to at least one road
	 * that the player owns, the given location is empty, and the given location is 
	 * at least 2 vertices away from every other municipality (city/settlement)
	 * @return false otherwise
	 */
	public synchronized boolean canBuildSettlement(VertexLocation vertexLoc) {
		return model.getMap().canBuildSettlement(getCurrentPlayer(), vertexLoc);
	}

	public synchronized void buildSettlement(PlayerReference player, VertexLocation loc)
			throws InvalidActionException {
		if (!isTurn(player)) {
			throw new NotYourTurnException();
		}
		TurnStatus phase = currentPhase();
		if (phase == TurnStatus.FirstRound || phase == TurnStatus.SecondRound) {
			throw new InvalidActionException("You cannot build a settlement during setup; " +
					"Use the buildStartingPieces method instead.");
		}
		else {
			model.buildSettlement(player, loc);
		}
	}

	/**
	 * 
	 * @param vertexLoc The location (one of the 6 vertices of one hex on the board)
	 * where the city is to be placed.
	 * @return true if the given vertex location is adjacent to at least one road
	 * that the player owns, the given location is empty, and the player owns a settlement
	 * at the given location.
	 * @return false otherwise
	 */
	public synchronized boolean canBuildCity(VertexLocation vertexLoc) {
		return canBuildCity(getCurrentPlayer(), vertexLoc);
	}

	/**
	 * 
	 * @param vertexLoc The location (one of the 6 vertices of one hex on the board)
	 * where the city is to be placed.
	 * @return true if the given vertex location is adjacent to at least one road
	 * that the player owns, the given location is empty, and the player owns a settlement
	 * at the given location.
	 * @return false otherwise
	 */
	public synchronized boolean canBuildCity(PlayerReference player,
			VertexLocation vertexLoc) {	
		return model.canBuildCity(player, vertexLoc);
	}

	/**
	 * 
	 * @param vertexLoc
	 * @return
	 * @throws InvalidActionException 
	 */
	public synchronized void buildCity(PlayerReference player, VertexLocation loc)
			throws InvalidActionException {
		model.buildCity(player, loc);
	}

	public synchronized boolean canBuildStartingSettlement(VertexLocation loc) {
		return model.getMap().canPlaceStartingSettlement(loc);
	}

	public synchronized boolean canBuildStartingPieces(VertexLocation settlement, EdgeLocation road) {
		return model.getMap().canPlaceStartingPieces(settlement, road);
	}

	public synchronized void buildStartingPieces(PlayerReference player, 
			VertexLocation settlement, EdgeLocation road) throws InvalidActionException {
		model.buildStartingPieces(player, settlement, road);
	}

	/**
	 * 
	 * @return true if the player owns at least one year of plenty card
	 * @return false otherwise
	 */
	public synchronized boolean canYearOfPlenty(PlayerReference player,
			ResourceType resource1, ResourceType resource2) {	
		ResourceList bank = model.getBank().getResources();
		return isTurn(player) && currentPhase() == TurnStatus.Playing &&
				!player.getPlayer().hasPlayedDevCard() &&
				player.getPlayer().getOldDevCards().count(DevCardType.YEAR_OF_PLENTY) > 0 &&
				(resource1 == resource2 ? bank.count(resource1) >= 2 :
				bank.count(resource1) >= 1 && bank.count(resource2) >= 1);
	}

	public synchronized void yearOfPlenty(PlayerReference player,
			ResourceType resource1, ResourceType resource2)
					throws InvalidActionException {
		if (!canYearOfPlenty(player, resource1, resource2)) {
			throw new InvalidActionException();
		}
		
		model.yearOfPlenty(player, resource1, resource2);
	}

	/**
	 * 
	 * @return true if the player owns at least one road building card,
	 * and has at least one unplaced road.
	 * @return false otherwise
	 */
	public synchronized boolean canRoadBuildingCard(PlayerReference player,
			EdgeLocation road1, EdgeLocation road2) {	
		return isTurn(player) && currentPhase() == TurnStatus.Playing &&
				!player.getPlayer().hasPlayedDevCard() &&
				player.getPlayer().getOldDevCards().count(DevCardType.ROAD_BUILD) > 0 &&
				model.getMap().canBuild2Roads(player, road1, road2);
	}

	public synchronized void roadBuildingCard(PlayerReference player,
			EdgeLocation road1, EdgeLocation road2) throws InvalidActionException {
		if (!canRoadBuildingCard(player, road1, road2)) {
			throw new InvalidActionException();
		}
		
		model.roadBuilding(player, road1, road2);
	}

	/**
	 * 
	 * @return true if the players owns at least one soldier card
	 * @return false otherwise
	 */
	public synchronized boolean canSoldier(PlayerReference playerRef) {
		Player player = playerRef.getPlayer();
		return isTurn(playerRef) && !player.hasPlayedDevCard() &&
				player.getOldDevCards().count(DevCardType.SOLDIER) > 0;
	}

	public synchronized void soldier(PlayerReference player, HexLocation robberLoc,
			PlayerReference victim) throws InvalidActionException {
		model.rob(player, robberLoc, victim, true);
	}

	/**
	 * 
	 * @return true if the player owns at least one monopoly card
	 * @return false if player owns zero monopoly cards
	 */
	public synchronized boolean canMonopoly(PlayerReference playerRef) {
		Player player = playerRef.getPlayer();
		
		return isTurn(playerRef) && currentPhase() == TurnStatus.Playing &&
				player.getOldDevCards().count(DevCardType.MONOPOLY) > 0;
	}

	/**
	 * 
	 * @return
	 * @throws InvalidActionException 
	 */
	public synchronized void monopoly(PlayerReference player, ResourceType resource) throws InvalidActionException {
		if (!canMonopoly(player)) {
			throw new InvalidActionException();
		}
		
		model.monopoly(player, resource);
	}

	/**
	 * 
	 * @return true if the player owns at least one monument card
	 * @return false otherwise
	 */
	public synchronized boolean canMonument(PlayerReference player) {
		return isTurn(player) && !player.getPlayer().hasPlayedDevCard() &&
				player.getPlayer().getOldDevCards().count(DevCardType.MONUMENT) > 0;
	}

	/**
	 * 
	 * @return
	 * @throws InvalidActionException 
	 */
	public synchronized void monument(PlayerReference player) throws InvalidActionException {
		if (!canMonument(player)) {
			throw new InvalidActionException();
		}
		
		model.monument(player);
	}

	/**
	 * 
	 * @return true if it is your turn and you have sufficient cards
	 * @return false otherwise
	 */
	public synchronized boolean canOfferTrade(TradeOffer offer) {
		if (offer.isEmpty() || !isTurn(offer.getSender()) ||
				model.getTradeOffer() != null ||
				currentPhase() != TurnStatus.Playing ||
				offer.getSender().equals(offer.getReceiver())) {
			return false;
		}
		
		ResourceList hand = offer.getSender().getPlayer().getResources();
		
		// Make sure the user has sufficient resources to trade.
		for (Map.Entry<ResourceType, Integer> offered :
			offer.getOffer().getOffered().entrySet()) {
			if (hand.count(offered.getKey()) < offered.getValue()) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * 
	 * @return
	 * @throws InvalidActionException 
	 */
	public synchronized void offerTrade(TradeOffer offer) throws InvalidActionException {
		model.offerTrade(offer);
	}

	/**
	 * 
	 * @return true if player has enough cards
	 * @return false otherwise
	 */
	public synchronized boolean canAcceptTrade() {		
		return model.getTradeOffer().isPossible();
	}

	/**
	 * 
	 * @return
	 * @throws TradeException 
	 */
	public synchronized void acceptTrade() throws TradeException {
		if (!canAcceptTrade()) {
			throw new TradeException();
		}
		
		model.acceptTrade();
	}
	
	/**
	 * 
	 * @return
	 * @throws TradeException 
	 */
	public synchronized void declineTrade() {
		model.declineTrade();
	}

	/**
	 * 
	 * @return true if player has a settlement or city on a port.
	 * @return false otherwise
	 */
	// This isn't useful.
	@Deprecated
	public synchronized boolean ownsPort() {
		
		Board map = model.getMap();
		Map<EdgeLocation, Port> ports = map.getPortMap();
		Map<VertexLocation, Municipality> municipalities = map.getMunicipalityMap();
		Player currentPlayer = getCurrentPlayer().getPlayer();
		
		
		//iterate through all ports
		for(Map.Entry<EdgeLocation, Port> entry : ports.entrySet()) {
			EdgeLocation edge = entry.getKey();
			
			//get vertices off of port edge
			Collection<VertexLocation> vertices = edge.getVertices();
			
			//iterate through all municipalities
			for(Map.Entry<VertexLocation, Municipality> Mentry : municipalities.entrySet()) {
				Municipality municipality = Mentry.getValue();
				
				//if municipality is on the port and it is owned by the player, you're good
				for(VertexLocation vertexLoc : vertices) {
					if(municipality.getLocation().equals(vertexLoc) && municipality.getOwner().getPlayer().equals(currentPlayer))
						return true;
				}
			}
		}
		
		return false;
	}

	public synchronized boolean canMaritimeTrade(PlayerReference player,
	ResourceType fromResource, ResourceType toResource) {
		return model.canMaritimeTrade(player, fromResource,	toResource);
	}

	/**
	 * 
	 * @return
	 * @throws InvalidActionException 
	 */
	public synchronized void maritimeTrade(PlayerReference player,
			ResourceType fromResource, ResourceType toResource) throws InvalidActionException {
		if (!canMaritimeTrade(player, fromResource, toResource)) {
			throw new InvalidActionException("Impossible Maritime Trade");
		}
		
		model.maritimeTrade(player, fromResource, toResource);
	}

	public synchronized int getVersion() {
		return model.getVersion();
	}

	public synchronized boolean canBuild2Roads(EdgeLocation first, EdgeLocation second) {
		Board map = model.getMap();
		PlayerReference currentPlayer = getCurrentPlayer();
		return map.canBuild2Roads(currentPlayer, first, second);
	}

	public synchronized Collection<Municipality> getMunicipalitiesAround(HexLocation hex) {
		return model.getMap().getMunicipalitiesAround(hex);
	}

	protected TurnStatus currentPhase() {
		return model.getTurnTracker().getStatus();
	}

	protected boolean isTurn(PlayerReference player) {
		return model.isTurn(player);
	}

	public synchronized void registerListener(IModelListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public synchronized void unregisterListener(IModelListener listener) {
		listeners.remove(listener);
	}

	/** Updates the model from json
	 * THIS is a CLIENT-ONLY method!
	 * @param json
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public synchronized CatanModel updateFromJSON(String jsonString) {
		if(jsonString==null)
			return getCatanModel();
		JSONObject json;
		try {
			json = (JSONObject) new JSONParser().parse(jsonString);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		int newVersion = (int) (long) json.get("version");
		//We are still waiting for players.
		List<JSONObject> listOfPlayers = (List<JSONObject>)json.get("players");
		int currentPlayerCount = 4;
		for(JSONObject play : listOfPlayers){
			if(play == null){
				--currentPlayerCount;
			}
		}
		if (newVersion == 0 &&
			(currentPlayerCount < NUMPLAYERS ||
					listOfPlayers.size() < NUMPLAYERS)) {
			updatePlayersFromJSON(json);
			return model;
		}
		if (getVersion() == newVersion && newVersion != 0) {
			return null;
		}
		model.setVersion(newVersion);
		
		//BANK
		updateBankFromJSON(json);
		
		//PLAYERS
		List<Player> players = updatePlayersFromJSON(json);
		
		//BOARD
		updateMapFromJSON(json, players);
		
		//TURNTRACKER
		updateTurnTrackerFromJSON(json,players);
		
		//TRADEOFFER
		updateTradeOfferFromJSON(json,players);
		
		//CHAT
		updateChatFromJSON(json);
		
		//LOG
		updateLogFromJSON(json);
		
		//WINNER
		updateWinnerFromJSON(json);
		
		return model;
		
	}

	private void updateBankFromJSON(JSONObject json) {
		//JSONObject object = (JSONObject) json.get("bank");
		try {
			Bank otherBank = new Bank(json);
			if (model.getBank() == null || !model.getBank().equals(otherBank)) {
				model.setBank(otherBank);
				for (IModelListener listener : listeners) {
					listener.bankChanged(otherBank);
				}
			}
		} catch (SchemaMismatchException e) {
			e.printStackTrace();
		}
	}

	private void updateMapFromJSON(JSONObject json, List<Player> players) {
		JSONObject object = (JSONObject) json.get("map");
		try {
			Board otherBoard = new Board(players, object);
			if (model.getMap() == null) 
			{ 
				model.setMap(otherBoard);
				for (IModelListener listener : listeners) {
					try {
						listener.mapInitialized();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			else if (!model.getMap().equals(otherBoard)) {
				model.setMap(otherBoard);
				for (IModelListener listener : listeners) {
					try {
						listener.mapChanged(otherBoard);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (SchemaMismatchException e) {
			e.printStackTrace();
		} catch (GameInitializationException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	private List<Player> updatePlayersFromJSON(JSONObject json) {
		List<Player> players = new ArrayList<Player>();
		for (Object obj : (List) json.get("players")) {
			JSONObject player = (JSONObject) obj;
			if (player != null) {
				try {
					Player newPlayer = new Player(player);
					if (ClientManager.getSession() != null && newPlayer.getUUID().equals(ClientManager.getSession().getPlayerUUID())) {
						ClientManager.setLocalPlayer(newPlayer.getReference());
					}
					players.add(newPlayer);
				} catch (SchemaMismatchException e) {
					e.printStackTrace();
				}
			}
		}
		if (model.getPlayers() == null || !model.getPlayers().equals(players)) {
			model.setPlayers(players);
			for (IModelListener listener : listeners) {
				try {
					listener.playersChanged(players);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return players;
		}
		else
			return model.getPlayers();
	}

	private void updateTurnTrackerFromJSON(JSONObject json, List<Player> players) {
		
		if (json.containsKey("turnTracker")) {
			
			JSONObject object = (JSONObject) json.get("turnTracker");
			try {
				//LARGEST ARMY
				updateLargestArmyFromJSON(json);
				
				//LONGEST ROAD
				updateLongestRoadFromJSON(json);
				
				TurnTracker otherTurnTracker = new TurnTracker(players,object);
				if (model.getTurnTracker() == null || 
					!model.getTurnTracker().equals(otherTurnTracker)) {
					model.setTurnTracker(otherTurnTracker);
					for (IModelListener listener : listeners) {
						try {
							listener.turnTrackerChanged(otherTurnTracker);
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				
			} catch (SchemaMismatchException e) {
				e.printStackTrace();
			}
		}
	}

	private void updateLargestArmyFromJSON(JSONObject json) {
		System.out.println(json.toJSONString());
		if (json.containsKey("largestArmy")) {
			UUID largestArmyUUID = UUID.fromString((String) ((JSONObject) json.get("largestArmy")).get("playerUUID"));
			PlayerReference otherPlayer = new PlayerReference(largestArmyUUID);
			PlayerReference original = model.getLargestArmy();
				model.setLongestRoad(otherPlayer);
			if (original == null || original.equals(otherPlayer)) {
				for (IModelListener listener : listeners) {
					try {
						listener.largestArmyChanged(otherPlayer);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		else {
			model.setLargestArmy(null);
		}
	}

	private void updateLongestRoadFromJSON(JSONObject json) {
		System.out.println(json.toJSONString());
		if (json.containsKey("longestRoad")) {
			UUID longestRoadUUID = UUID.fromString((String) ((JSONObject) json.get("longestRoad")).get("playerUUID"));
			PlayerReference otherPlayer = new PlayerReference(longestRoadUUID);
			PlayerReference original = model.getLongestRoad();
			model.setLongestRoad(otherPlayer);
			System.out.println("* The longest road belongs to " + model.getLongestRoad().getName());
			if (original == null || original.equals(otherPlayer)) {
				for (IModelListener listener : listeners) {
					try {
						listener.longestRoadChanged(otherPlayer);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		else {
			model.setLongestRoad(null);
		}
	}

	private void updateTradeOfferFromJSON(JSONObject json, List<Player> players) {
		if (json.containsKey("tradeOffer")) {
			JSONObject tradeOffer = (JSONObject) json.get("tradeOffer");
			TradeOffer otherOffer;
			try {
				otherOffer = new TradeOffer(tradeOffer);
				if (model.getTradeOffer() == null || !model.getTradeOffer().equals(otherOffer)) {
					model.setTradeOffer(otherOffer);
					for (IModelListener listener : listeners) {
						try {
							listener.tradeOfferChanged(otherOffer);
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} catch (SchemaMismatchException e) {
				e.printStackTrace();
			}
		}
		else{// if (model.getTradeOffer() != null) {
			model.setTradeOffer(null);
			for (IModelListener listener : listeners) {
				try {
					listener.tradeOfferChanged(null);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void updateChatFromJSON(JSONObject json) {
		if (json.containsKey("chat")) {
			JSONObject object = (JSONObject) json.get("chat");
			MessageList otherChat;
			try {
				otherChat = new MessageList(object);
				if (model.getChat() == null || !model.getChat().equals(otherChat)) {
					model.setChat(otherChat);
					for (IModelListener listener : listeners) {
						try {
							listener.chatChanged(otherChat);
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} catch (SchemaMismatchException e) {
				e.printStackTrace();
			}
		}
	}

	private void updateLogFromJSON(JSONObject json) {
		if (json.containsKey("log")) {
			JSONObject object = (JSONObject) json.get("log");
			try {
				MessageList otherLog = new MessageList(object);
				if (!otherLog.equals(model.getLog())) {
					model.setLog(otherLog);
					for (IModelListener listener : listeners) {
						try {
							listener.logChanged(otherLog);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} catch (SchemaMismatchException e) {
				e.printStackTrace();
			}
		}
	}

	private void updateWinnerFromJSON(JSONObject json) {
		if (!json.containsKey("winner")) {
			return;
		}
		PlayerReference winner = new PlayerReference((String) json.get("winner"));
		//PlayerReference otherPlayer = new PlayerReference(model, winner);
		if (model.getWinner() != null || !model.getWinner().equals(winner)) {
			model.setWinner(winner);
			for (IModelListener listener : listeners) {
				try {
					listener.winnerChanged(winner);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public synchronized Player addPlayer(Session player, CatanColor color) throws GameInitializationException {
		if (model.getVersion() > 0 || model.getPlayers().size() >= 4) {
			throw new GameInitializationException();
		}
		
		Player newPlayer = new Player(player, color, model.getPlayers().size());
		//Player newPlayer = new Player(model.getPlayers().size(), player, color);
		model.addPlayer(newPlayer);
		return newPlayer;
	}

	public synchronized void addPlayer(String name, CatanColor color) throws GameInitializationException {
		if (model.getVersion() > 0 || model.getPlayers().size() >= 4) {
			throw new GameInitializationException();
		}
		
		Player newPlayer = new Player(model.getPlayers().size(), name, color);
		model.addPlayer(newPlayer);
	}
	
	public synchronized void sendChat(PlayerReference source, String message) {
		model.addChat(source, message);
	}

	@Override
	public String toString(){
		Gson gson = new Gson();
		JsonObject json = new JsonObject();
		json.add("id", gson.toJsonTree(model.getHeader().getUUID()));
		json.add("deck", gson.toJsonTree(model.getBank().deckToJsonObject()));
		json.add("map", gson.toJsonTree(model.getMap()));
		json.add("players", gson.toJsonTree(model.getPlayers()));
		json.add("log", gson.toJsonTree(model.getLog()));
		json.add("chat", gson.toJsonTree(model.getChat()));
		json.add("bank", gson.toJsonTree(model.getBank().toJsonObject()));
		if(model.getTradeOffer() != null){
			json.add("tradeOffer", gson.toJsonTree(model.getTradeOffer()));
		}
		json.add("turnTracker", gson.toJsonTree(model.getTurnTracker()));
		json.add("version", gson.toJsonTree(model.getVersion()));
		return gson.toJson(json);
	}
	
	// Ridiculous test method
	public void print(ModelFacade mf, String message) {
		System.out.println(message);
	}

	public UUID getUUID() {
		return model.getID();
	}
}