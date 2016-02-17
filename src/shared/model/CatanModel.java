package shared.model;

import java.io.Serializable;
import java.util.*;

import client.data.GameInfo;
import shared.communication.GameHeader;
import shared.communication.PlayerHeader;
import shared.definitions.DevCardType;
import shared.definitions.ResourceType;
import shared.definitions.TurnStatus;
import shared.exceptions.GameInitializationException;
import shared.exceptions.InsufficientResourcesException;
import shared.exceptions.InvalidActionException;
import shared.exceptions.NotYourTurnException;
import shared.exceptions.TradeException;
import shared.locations.EdgeLocation;
import shared.locations.HexLocation;
import shared.locations.VertexLocation;

/**
 * Contains all information about the current game: references the map, players, chat, and bank
 * for the current game.  All information relevant to a particular game can be accessed through
 * this class.
 * @author Jordan
 *
 */
public class CatanModel 
	implements Serializable {
	private static final long serialVersionUID = 2103176853646879835L;
	
	private UUID id;
	private String title;
	
	private MessageList chat;
	private MessageList log;
	private Board map;
	private TradeOffer tradeOffer;
	private TurnTracker turnTracker;
	private Bank bank;
	private List<Player> players;
	private PlayerReference longestRoad;
	private PlayerReference largestArmy;
	
	private PlayerReference winner = null;
	
	private int version;

	/** Makes a brand spanking new Model
	 * @throws GameInitializationException 
	 * 
	 */
	public CatanModel() throws GameInitializationException {
		this(false, false, false);
	}

	/** Makes a brand spanking new Model
	 * @param randomHexes TODO
	 * @param randomNumbers TODO
	 * @param randomPorts TODO
	 * @throws GameInitializationException 
	 * 
	 */
	public CatanModel(boolean randomHexes, boolean randomNumbers, boolean randomPorts) throws GameInitializationException {
		version = 0;
		winner = null;
		
		id = UUID.randomUUID();
		players = new ArrayList<Player>();
		turnTracker = null; //new TurnTracker();
		bank = new Bank();
		chat = new MessageList();
		chat.add("", "First Round");
		log = new MessageList();
		
		map = new Board(randomHexes, randomNumbers, randomPorts);
		longestRoad = null; //new PlayerReference(UUID.randomUUID(),-1);
		largestArmy = null; //new PlayerReference(UUID.randomUUID(),-1);
	}
	
	public UUID getID() {
		return id;
	}
	
	public int getShortID() {
		return Math.abs(id.hashCode()) % 1000;
	}

	/**
	 * @return the chat
	 */
	public MessageList getChat() {
		return chat;
	}

	/**
	 * @return the log
	 */
	public MessageList getLog() {
		return log;
	}

	/**
	 * @return the tradeOffer
	 */
	public TradeOffer getTradeOffer() {
		return tradeOffer;
	}

	/**
	 * @param tradeOffer the tradeOffer to set
	 */
	void setTradeOffer(TradeOffer tradeOffer) {
		this.tradeOffer = tradeOffer;
	}

	/**
	 * @return the longestRoad
	 */
	public PlayerReference getLongestRoad() {
		return longestRoad;
	}

	/**
	 * @param longestRoad the longestRoad to set
	 */
	void setLongestRoad(PlayerReference longestRoad) {
		this.longestRoad = longestRoad;
		
		updateScores();
	}

	/**
	 * @return the largestArmy
	 */
	public PlayerReference getLargestArmy() {
		return largestArmy;
	}

	/**
	 * @param largestArmy the largestArmy to set
	 */
	void setLargestArmy(PlayerReference largestArmy) {
		this.largestArmy = largestArmy;
		
		updateScores();
	}

	/**
	 * @return the map
	 */
	public Board getMap() {
		return map;
	}

	/**
	 * @return the turnTracker
	 */
	public TurnTracker getTurnTracker() {
		return turnTracker;
	}

	/**
	 * @return the bank
	 */
	public Bank getBank() {
		return bank;
	}

	/**
	 * @return the players
	 */
	public List<Player> getPlayers() {
		return players;
	}

	/**
	 * @return the winner
	 */
	public PlayerReference getWinner() {
		return winner;
	}

	void setChat(MessageList chat) {
		this.chat = chat;
	}
	
	public void addChat(PlayerReference source, String message) {
		chat.add(source.getName(), message);
		version++;
	}

	void setLog(MessageList log) {
		this.log = log;
	}

	void setMap(Board map) {
		this.map = map;
	}

	public void setTurnTracker(TurnTracker turnTracker) {
		this.turnTracker = turnTracker;
	}

	void setBank(Bank bank) {
		this.bank = bank;
	}

	void setPlayers(List<Player> players) {
		this.players = players;
	}

	void setWinner(PlayerReference winner) {
		this.winner = winner;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	public void incrementVersion() {
		version = version++;
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	public GameInfo getGameInfo() {
		return new GameInfo(getHeader());
	}

	public GameHeader getHeader() {
		List<PlayerHeader> players = new ArrayList<>();
		for (Player player : getPlayers()) {
			players.add(player.getHeader());
		}
		return new GameHeader(title, id, players);
	}

	public void setHeader(GameInfo info) {
		title  = info.getTitle();
		id = info.getUUID();
		
	}

	public void setHeader(GameHeader gameHeader) {
		title  = gameHeader.getTitle();
		id = gameHeader.getUUID();
	}

	public String getTitle() {
		return title;
	}

	void setTitle(String title) {
		this.title = title;
	}

	public boolean canBuildCity(PlayerReference player,	VertexLocation loc) {
		return isTurn(player) && player.getPlayer().canBuildCity()
				&& map.canBuildCity(player, loc);
	}

	public boolean isTurn(PlayerReference player) {
		return player.equals(turnTracker.getCurrentPlayer());
	}

	public Map<ResourceType, Integer> getMaritimeRatios(PlayerReference player) {
		Map<ResourceType, Integer> ratios = new HashMap<>();
		int defaultRatio = 4;
		
		Board map = getMap();
		
		for (Port port : map.getPorts()) {
			if (player.equals(map.getOwnerOfPortAt(port.getLocation()))) {
				if (port.getResource() == null) {
					defaultRatio = 3;
				}
				else {
					ratios.put(port.getResource(), 2);
				}
			}
		}
		
		for (ResourceType resource : ResourceType.values()) {
			if (!ratios.containsKey(resource)) {
				ratios.put(resource, defaultRatio);
			}
		}
		
		return ratios;
	}

	public boolean canMaritimeTrade(PlayerReference player, ResourceType fromResource, ResourceType toResource) {
		// It must be your turn to trade
		if (!isTurn(player)) {
			return false;
		}
		
		ResourceList bank = getBank().getResources();
		
		if (bank.count(toResource) < 1) {
			return false;
		}
		
		Map<ResourceType, Integer> ratios = getMaritimeRatios(player);
		
		return player.getPlayer().getResources().count(fromResource) >= ratios.get(fromResource);
	}

	void buildStartingPieces(PlayerReference player, VertexLocation settlement,
			EdgeLocation road) throws InvalidActionException {
		if (!map.canPlaceStartingPieces(settlement, road)) {
			throw new InvalidActionException("Invalid Starting Piece Placement");
		}
		// This movement is free.
		map.placeStartingPieces(player, settlement, road);
		
		// Give starting resources
		if (turnTracker.getStatus() == TurnStatus.SecondRound) {
			ResourceList bank = this.bank.getResources();
			ResourceList hand = player.getPlayer().getResources();
			for (HexLocation hexLoc : settlement.getHexes()) {
				try {
					Hex hex = map.getHexAt(hexLoc);
					ResourceType resource = hex.getResource();
					if (resource != null) {
						bank.transfer(hand, resource, 1);
					}
				} catch (IndexOutOfBoundsException e) {
					continue;
				}
			}
		}

		player.getPlayer().useRoad();
		player.getPlayer().useSettlement();
		
		turnTracker.passTurn();
		
		updateScores();
		
		log.add(player.getName(), player.getName() + " placed thier starting pieces.");
		
		++version;
	}

	void buildRoad(PlayerReference player, EdgeLocation loc)
			throws InvalidActionException, InsufficientResourcesException {
		if (!map.canBuildRoadAt(player, loc)) {
			throw new InvalidActionException("Invalid Road Placement");
		}
		// Check resource counts
		ResourceList hand = player.getHand();
		if (!player.getPlayer().canBuildRoad()) {
			throw new InsufficientResourcesException("Insufficient " +
					"resources for a road.");
		}
		ResourceList bank = getBank().getResources();
		hand.transfer(bank, ResourceType.WOOD, 1);
		hand.transfer(bank, ResourceType.BRICK, 1);
		getMap().buildRoad(player, loc);
		player.getPlayer().useRoad();
		
		checkLongestRoad(player);
		
		log.add(player.getName(), player.getName() + " built a road.");
		
		++version;
	}

	void buildSettlement(PlayerReference player, VertexLocation loc)
			throws InvalidActionException, InsufficientResourcesException {
		if (!map.canBuildSettlement(player, loc)) {
			throw new InvalidActionException("Invalid Road Placement");
		}
		if (!player.getPlayer().canBuildSettlement()) {
			throw new InsufficientResourcesException("Insufficient resources " +
					"for a settlement.");
		}
		ResourceList hand = player.getHand();
		ResourceList bank = getBank().getResources();
		hand.transfer(bank, ResourceType.WOOD, 1);
		hand.transfer(bank, ResourceType.BRICK, 1);
		hand.transfer(bank, ResourceType.SHEEP, 1);
		hand.transfer(bank, ResourceType.WHEAT, 1);
		map.buildSettlement(player, loc);
		player.getPlayer().useSettlement();
		
		updateScores();
		
		log.add(player.getName(), player.getName() + " built a road.");
		
		++version;
	}

	/**
	 * 
	 * @param player TODO
	 * @param loc TODO
	 * @return
	 * @throws InvalidActionException 
	 */
	void buildCity(PlayerReference player, VertexLocation loc)
			throws InvalidActionException {
		if (!isTurn(player)) {
			throw new NotYourTurnException();
		}
		if (!player.getPlayer().canBuildCity()) {
			throw new InsufficientResourcesException("You do not have the sufficient " +
					"resources for a city.");
		}
		if (!canBuildCity(player, loc)) {
			throw new InvalidActionException("You must build a city over one " +
					"of your existing settlements.");
		}
		
		ResourceList hand = player.getPlayer().getResources();
		ResourceList bank = getBank().getResources();
		hand.transfer(bank, ResourceType.ORE, 3);
		hand.transfer(bank, ResourceType.WHEAT, 2);
		
		getMap().upgradeSettlementAt(player, loc);
		player.getPlayer().useCity();
		
		updateScores();
	
		log.add(player.getName(), player.getName() + " upgraded a settlement into a city.");
		
		++version;
	}

	void buyDevCard(PlayerReference player) throws InvalidActionException {
		assert isTurn(player);
		assert player.getPlayer().canBuyDevCard();
		assert bank.getDevCards().count() >= 1;
		
		ResourceList hand = player.getHand();
		ResourceList bankRes = bank.getResources();

		hand.transfer(bankRes, ResourceType.ORE, 1);
		hand.transfer(bankRes, ResourceType.SHEEP, 1);
		hand.transfer(bankRes, ResourceType.WHEAT, 1);
		
		bank.getDevCards().transferRandomCardTo(player.getPlayer().getNewDevCards());
		
		log.add(player.getName(), player.getName() + " bought a development card.");
		
		++version;
	}

	/**
	 * @param roll
	 * @pre The current phase is the rolling phase and the roll is valid
	 * @post Appropriate resources will be given, the robber will trigger if a 7 was rolled,
	 * and players will be required to discard if necessary.
	 */
	void roll(int roll) {
		assert turnTracker.getStatus() == TurnStatus.Rolling;
		assert roll >= 2 && roll <= 12;
		
		// Give resources to the appropriate players
		ResourceList resBank = bank.getResources();
		for (Hex hex : map.getHexesByNumber(roll)) {
			if (!map.getRobberLocation().equals(hex.getLocation())) { // Check for the robber
				for (Municipality town : map.getMunicipalitiesAround(hex.getLocation())) {
					resBank.transferAtMost(town.getOwner().getPlayer().getResources(),
							hex.getResource(), town.getIncome());
				}
				// Note that you may not get your full amount, which is expected behavior
				// Who gets resources first is currently undefined.
			}
		}
		
		// Change the status of the game
		turnTracker.roll(roll);
		
		turnTracker.getCurrentPlayer().getPlayer().setHasRolled(true);
		
		log.add(turnTracker.getCurrentPlayer().getName(),
				turnTracker.getCurrentPlayer().getName() + " rolled a " + roll);
		
		++version;
	}

	void rob(PlayerReference player, HexLocation loc,
			PlayerReference victim, boolean isSoldierCard) throws InvalidActionException {
		assert player != null;
		assert isTurn(player);
		assert map.canMoveRobberTo(loc);
		assert !player.equals(victim);
		assert turnTracker.getStatus() == TurnStatus.Robbing;
		
		if (victim != null && victim.getPlayerUUID() != null) {
			boolean valid = false;
			for (Municipality town : map.getMunicipalitiesAround(loc)) {
				if (town.getOwner().equals(victim)) {
					valid = true;
					break;
				}
			}
			if (!valid) {
				throw new InvalidActionException("The given victim does not have any " +
						"cities or settlements near the given hex.");
			}
			
			victim.getHand().transferRandomCard(player.getHand());
			
			if (isSoldierCard) {
				useSoldierCard(player);
			}
			else {
				turnTracker.setStatus(TurnStatus.Playing);
			}
		
			log.add(player.getName(), player.getName() + " robbed " + victim.getName());
		}
		else {
			if (isSoldierCard) {
				useSoldierCard(player);
			}
			else {
				turnTracker.setStatus(TurnStatus.Playing);
			}
			
			log.add(player.getName(), player.getName() + " moved the robber.");
		}
		
		map.moveRobber(loc);
		
		++version;
	}

	private void useSoldierCard(PlayerReference playerRef)
			throws InvalidActionException {
		assert playerRef.getPlayer().getOldDevCards().count(DevCardType.SOLDIER) >= 1;
		
		Player player = playerRef.getPlayer();
		
		player.getOldDevCards().useCard(DevCardType.SOLDIER);
		player.setSoldiers(player.getSoldiers() + 1);
		
		if (player.getSoldiers() >= 3) {
			if (getLargestArmy() == null ||
				player.getSoldiers() > getLargestArmy().getPlayer().getSoldiers()) {
				setLargestArmy(playerRef);
			}
		}
		
		log.add(playerRef.getName(), playerRef.getName() + " played a soldier card.");
	}

	void finishTurn() throws InvalidActionException {
		assert tradeOffer == null;
		
		PlayerReference curPlayer = turnTracker.getCurrentPlayer();
		
		turnTracker.passTurn();
		
		log.add(curPlayer.getName(), curPlayer.getName() + " finished their turn.");
		
		++version;
	}

	void maritimeTrade(PlayerReference player,
			ResourceType fromResource, ResourceType toResource)
					throws InsufficientResourcesException {
		assert canMaritimeTrade(player, fromResource, toResource);
		
		ResourceList bankRes = bank.getResources();
		ResourceList hand = player.getHand();
		
		hand.transfer(bankRes, fromResource, getMaritimeRatios(player).get(fromResource));
		bankRes.transfer(hand, toResource, 1);
		
		log.add(player.getName(), player.getName() + " traded " + fromResource +
				" for " + toResource + " with the bank.");
		
		++version;
	}

	void offerTrade(TradeOffer offer) throws InvalidActionException {
		if (tradeOffer != null) {
			throw new InvalidActionException("You cannot offer a trade while " +
					"there is already a trade waiting to be accepted");
		}
		
		log.add(offer.getSender().getName(), offer.getSender().getName() +
				" offered to trade with " +	offer.getReceiver().getName());
		
		tradeOffer = offer;
		
		++version;
	}

	void acceptTrade() throws TradeException {
		tradeOffer.makeTrade();
		
		log.add(tradeOffer.getReceiver().getName(), tradeOffer.getReceiver().getName() +
				" accepted " +	tradeOffer.getSender().getName() + "'s trade offer.");
		
		tradeOffer = null;
		
		++version;
	}

	void declineTrade() {
		log.add(tradeOffer.getReceiver().getName(), tradeOffer.getReceiver().getName() 
				+ " declined " + tradeOffer.getSender().getName() + "'s trade offer.");
		
		tradeOffer = null;
		
		++version;
	}

	void yearOfPlenty(PlayerReference player,
			ResourceType resource1,	ResourceType resource2)
					throws InvalidActionException {
		ResourceList bankRes = bank.getResources();
		ResourceList hand = player.getHand();
		
		assert resource1 == resource2 ?	bankRes.count(resource1) >= 2 :
				bankRes.count(resource1) >= 1 && bankRes.count(resource2) >= 1;

		bankRes.transfer(hand, resource1, 1);
		bankRes.transfer(hand, resource2, 1);
		player.getPlayer().getOldDevCards().useCard(DevCardType.YEAR_OF_PLENTY);
		
		log.add(player.getName(), player.getName() + " played a year of plenty card.");
		
		++version;
	}

	void roadBuilding(PlayerReference player,
			EdgeLocation road1,	EdgeLocation road2) throws InvalidActionException {
		player.getPlayer().getOldDevCards().useCard(DevCardType.ROAD_BUILD);

		map.buildRoad(player, road1);
		map.buildRoad(player, road2);
		
		player.getPlayer().useRoad();
		player.getPlayer().useRoad();
		
		checkLongestRoad(player);
		
		log.add(player.getName(), player.getName() + " played a road building card.");
		
		++version;
	}

	/**
	 * 
	 */
	private void checkLongestRoad(PlayerReference player) {
		// The player already has the longest road.
		if (player.equals(getLongestRoad())) {
			return; // Move along. Nothing to do here.
		}
		
		int currentBest = 4;
		if (getLongestRoad() != null) {
			currentBest = map.lengthOfLongestRoute(getLongestRoad());
		}
		
		int length = map.lengthOfLongestRoute(player);
		if (length > currentBest) {
			currentBest = length;
			setLongestRoad(player);
		}
		
		if (currentBest == 4) {
			setLongestRoad(null);
		}
	}

	void monopoly(PlayerReference player, ResourceType resource) {
		assert player.getPlayer().getOldDevCards().count(DevCardType.MONOPOLY) >= 1;
		
		ResourceList hand = player.getHand();
		
		for (Player opponent : players) {
			// It makes no sense to take from yourself...
			if (player.equals(opponent.getReference())) {
				continue;
			}
			
			// Transfer ALL the resources
			opponent.getResources().transferAtMost(hand, resource, Integer.MAX_VALUE);
		}
		
		log.add(player.getName(), player.getName() + " monopolized all of the " + resource);
		
		++version;
	}

	void monument(PlayerReference player) throws InvalidActionException {
		player.getPlayer().playMonument();
		
		updateScores();
		
		log.add(player.getName(), player.getName() + " played a monument.");
		
		++version;
	}

	public boolean canDiscard(PlayerReference player,
			Map<ResourceType, Integer> toDiscard) {
		// You can't discard if you have already discarded.
		if (player.getPlayer().hasDiscarded()) {
			return false;
		}
		
		ResourceList hand = player.getHand();
	
		int totalCards = 0;
		for (Map.Entry<ResourceType, Integer> card : toDiscard.entrySet()) {
			if (hand.count(card.getKey()) < card.getValue()) {
				return false;
			}
			totalCards += card.getValue();
		}
		
		// Make sure this is discarding the correct number of cards
		return totalCards == hand.count() / 2;
	}

	void discard(PlayerReference player,
			Map<ResourceType, Integer> toDiscard) throws InsufficientResourcesException {
		assert canDiscard(player, toDiscard);
		
		ResourceList hand = player.getHand();
		ResourceList bankRes = bank.getResources();
		
		int numDiscarded = 0;
		for (Map.Entry<ResourceType, Integer> card : toDiscard.entrySet()) {
			hand.transfer(bankRes, card.getKey(), card.getValue());
			numDiscarded += card.getValue();
		}
		
		player.getPlayer().setHasDiscarded(true);
		
		boolean allDiscarded = true;
		for (Player playr : players) {
			if (!playr.hasDiscarded()) {
				allDiscarded = false;
			}
		}
		if (allDiscarded) {
			turnTracker.setStatus(TurnStatus.Robbing);
		}
		
		log.add(player.getName(), player.getName() + " discarded " +
				numDiscarded + " cards.");
		
		++version;
	}

	private void updateScores() {
		for (Player player : players) {
			int score = 0;
			for (Municipality town : map.getMunicipalitiesOwnedBy(player.getReference())) {
				score += town.getPointValue();
			}
			if (player.getReference().equals(getLongestRoad())) {
				score += 2;
			}
			if (player.getReference().equals(getLargestArmy())) {
				score += 2;
			}
			score += player.getMonuments();
			
			player.setVictoryPoints(score);
		}
	}

	public void addPlayer(Player newPlayer) {
		players.add(newPlayer);
		
		if (players.size() == 4) {
			turnTracker = new TurnTracker(players);
		}
	}

	public boolean hasStarted() {
		return turnTracker != null && version > 0;
	}
	
	/** Tells you if the game is ready to play
	 * @return
	 */
	public boolean ready() {
		return turnTracker != null && players.size() == 4;
	}
}
