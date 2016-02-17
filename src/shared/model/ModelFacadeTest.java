package shared.model;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import server.cheat.CheatEnabledDice;
import shared.IDice;
import shared.communication.*;
import shared.definitions.CatanColor;
import shared.definitions.DevCardType;
import shared.definitions.MunicipalityType;
import shared.definitions.ResourceType;
import shared.definitions.TurnStatus;
import shared.exceptions.GameInitializationException;
import shared.exceptions.InsufficientResourcesException;
import shared.exceptions.InvalidActionException;
import shared.exceptions.NotYourTurnException;
import shared.locations.EdgeDirection;
import shared.locations.EdgeLocation;
import shared.locations.HexLocation;
import shared.locations.VertexDirection;
import shared.locations.VertexLocation;

public class ModelFacadeTest {

	private CatanModel model;
	private ModelFacade m;
	private CheatEnabledDice dice;
	
	private PlayerReference justin, steve, jordan, grant;
	
	@Before
	public void setup() throws Exception {
		
		dice = new CheatEnabledDice();
		model = new CatanModel(false, false, false);
		m = new ModelFacade(model, dice);
		model.setHeader(new GameHeader("Dummy Game", 
				UUID.fromString("3d4f073d-7acd-4cf8-8b81-5eb097b58d79"),
				new ArrayList<PlayerHeader>()));
		
		// Put in some players
		m.addPlayer("Justin", CatanColor.GREEN);
		m.addPlayer("Steve", CatanColor.BLUE);
		m.addPlayer("Jordan", CatanColor.ORANGE);
		m.addPlayer("Grant", CatanColor.RED);
		
		List<Player> players = model.getPlayers();
		justin = players.get(0).getReference();
		steve = players.get(1).getReference();
		jordan = players.get(2).getReference();
		grant = players.get(3).getReference();
		
		//System.out.println(model.getTurnTracker());
		
		// Place some starting crap
		m.buildStartingPieces(justin,
				new VertexLocation(0, 2, VertexDirection.NorthEast),
				new EdgeLocation(0, 1, EdgeDirection.SouthEast));
		m.buildStartingPieces(steve,
				new VertexLocation(1, -1, VertexDirection.NorthEast),
				new EdgeLocation(1, -1, EdgeDirection.North));
		m.buildStartingPieces(jordan,
				new VertexLocation(-1, 1, VertexDirection.SouthWest),
				new EdgeLocation(-1, 1, EdgeDirection.SouthWest));
		m.buildStartingPieces(grant,
				new VertexLocation(0, -1, VertexDirection.West),
				new EdgeLocation(0, -1, EdgeDirection.NorthWest));
		
		m.buildStartingPieces(grant,
				new VertexLocation(-2, 1, VertexDirection.NorthEast),
				new EdgeLocation(-2, 1, EdgeDirection.NorthEast));
		m.buildStartingPieces(jordan,
				new VertexLocation(1, 1, VertexDirection.NorthEast),
				new EdgeLocation(2, 0, EdgeDirection.NorthWest));
		m.buildStartingPieces(steve,
				new VertexLocation(1, 0, VertexDirection.NorthEast),
				new EdgeLocation(1, 0, EdgeDirection.North));
		m.buildStartingPieces(justin,
				new VertexLocation(0, 0, VertexDirection.West),
				new EdgeLocation(0, 0, EdgeDirection.SouthWest));
		
		//System.out.println(model.getTurnTracker());
	}
	
	
	@Test
	public void testCanRoll() {
		
		//test if it's players current turn and he hasn't rolled
		assertTrue(m.canRoll(justin));
		
		//test if it is player's turn but he has already rolled
		justin.getPlayer().setHasRolled(true);
		assertFalse(m.canRoll(justin));
		
		//test if its not player's current turn
		assertFalse(m.canRoll(grant));
	}
	
	@Test
	public void testRollDice() throws Exception {
		dice.enqueueRoll(10);		
		m.rollDice(justin);
		
		//test that resources have been correctly distributed
		assertEquals(1, justin.getHand().count(ResourceType.BRICK));
		assertEquals(2, jordan.getHand().count(ResourceType.BRICK));
		assertEquals(2, grant.getHand().count(ResourceType.SHEEP));
		
		// Give Justin a bunch of junk to discard
		
		model.getBank().getResources().transfer(justin.getHand(), ResourceType.SHEEP, 5);
		assertEquals(9, justin.getHand().count());
		
		m.finishTurn(justin);
		
		dice.enqueueRoll(7);
		m.rollDice(steve);

		assertEquals(TurnStatus.Discarding, model.getTurnTracker().getStatus());
		Map<ResourceType, Integer> discardMap = new HashMap<>();
		discardMap.put(ResourceType.SHEEP, 4);
		m.discard(justin, discardMap);
		
		assertEquals(TurnStatus.Robbing, model.getTurnTracker().getStatus());
		m.rob(steve, new HexLocation(0, -2), null);
		
		m.finishTurn(steve);
		
		dice.enqueueRoll(7);
		m.rollDice(jordan);
		
		assertEquals(TurnStatus.Robbing, model.getTurnTracker().getStatus());
	}
	
	@Test
	public void testCanRob() throws Exception {
		
		// Valid place/victim, but you haven't rolled a 7 yet.
		assertFalse(m.canRob(justin, new HexLocation(1,-1), steve));
		
		// Give Grant a bunch of junk to discard
		
		model.getBank().getResources().transfer(grant.getHand(), ResourceType.ORE, 5);
		assertEquals(8, grant.getHand().count());

		dice.enqueueRoll(7);
		m.rollDice(justin);
		
		// Grant still needs to discard
		assertFalse(m.canRob(justin, new HexLocation(1,-1), steve));
		
		Map<ResourceType, Integer> discardMap = new HashMap<>();
		discardMap.put(ResourceType.SHEEP, 4);
		// can Grant discard stuff he doesn't have?
		assertFalse(m.canDiscard(grant, discardMap));
		
		discardMap.clear();
		// Discard 1 card too few
		discardMap.put(ResourceType.ORE, 3);
		assertFalse(m.canDiscard(grant, discardMap));
		// Discard 1 card too many
		discardMap.put(ResourceType.ORE, 5);
		assertFalse(m.canDiscard(grant, discardMap));
		// Discard the correct number of cards
		discardMap.put(ResourceType.ORE, 4);
		assertTrue(m.canDiscard(grant, discardMap));
		m.discard(grant, discardMap);
		assertEquals(4, grant.getHand().count());
		
		// Not Steve's turn
		assertFalse(m.canRob(steve, new HexLocation(0, 0), justin));

		// now it's okay to discard
		assertTrue(m.canRob(justin, new HexLocation(1,-1), steve));
		// test player that isn't around the given hex
		assertFalse(m.canRob(justin, new HexLocation(1,-1), grant));
		// you can't steal from yourself
		assertFalse(m.canRob(justin, new HexLocation(0, 0), justin));
		m.rob(justin, new HexLocation(1,-1), steve);
		
		m.finishTurn(justin);
		
		dice.enqueueRoll(7);
		m.rollDice(steve);
		
		// two choices
		assertTrue(m.canRob(steve, new HexLocation(-1,1), justin));
		assertTrue(m.canRob(steve, new HexLocation(-1,1), jordan));
		
	}
	
	@Test 
	public void testDoRob() throws Exception {

		dice.enqueueRoll(7);
		m.rollDice(justin);
		
		m.rob(justin, new HexLocation(1,-1), steve);
		
		assertEquals(new HexLocation(1,-1), model.getMap().getRobberLocation());
		
		assertEquals(4, justin.getHand().count());
		assertEquals(2, steve.getHand().count());
		assertEquals(3, justin.getHand().count(ResourceType.ORE) +
				steve.getHand().count(ResourceType.ORE));
		assertEquals(1, justin.getHand().count(ResourceType.SHEEP) +
				steve.getHand().count(ResourceType.SHEEP));
		assertEquals(2, justin.getHand().count(ResourceType.WHEAT) +
				steve.getHand().count(ResourceType.WHEAT));
		assertEquals(0, justin.getHand().count(ResourceType.BRICK) +
				steve.getHand().count(ResourceType.BRICK));
		assertEquals(0, justin.getHand().count(ResourceType.WOOD) +
				steve.getHand().count(ResourceType.WOOD));
	}
	
	@Test
	public void testCanFinishTurn() throws Exception {
		
		dice.enqueueRoll(8);
		
		//test if current player has not rolled
		assertFalse(m.canFinishTurn());
		
		//test if current player has rolled
		PlayerReference currentPlayer = m.getCatanModel().getTurnTracker().getCurrentPlayer();

		m.rollDice(currentPlayer);
		assertTrue(m.canFinishTurn());
		
	}
	
	@Test
	public void testDoFinishTurn() throws InvalidActionException {
		dice.enqueueRoll(8);
		
		PlayerReference currentPlayer = m.getCurrentPlayer();

		m.rollDice(currentPlayer);
		m.finishTurn(currentPlayer);
	}
	
	@Test
	public void testCanBuyDevelopmentCard() throws InvalidActionException {
		dice.enqueueRoll(4);
		m.rollDice(justin);
		
		//test insufficient resources
		assertFalse(m.canBuyDevelopmentCard(justin));
		
		//test hand with sufficient resources
		ResourceList hand = justin.getHand();
		ResourceList bank = model.getBank().getResources();
		bank.transfer(hand, ResourceType.WHEAT, 1);
		bank.transfer(hand, ResourceType.ORE, 1);
		bank.transfer(hand, ResourceType.SHEEP, 1);
		assertTrue(m.canBuyDevelopmentCard(justin));
		
		// Not your turn, but correct number of resources
		assertFalse(m.canBuyDevelopmentCard(steve));
		
		// Test with empty devcard deck
		DevCardList dump = new DevCardList();
		model.getBank().getDevCards().transferAll(dump);
		assertFalse(m.canBuyDevelopmentCard(justin));
	}
	
	@Test
	public void testDoBuyDevelopmentCard() throws Exception {
		
		int expectedTotal = model.getBank().getDevCards().count();

		PlayerReference playerRef = m.getCurrentPlayer();
		Player player = playerRef.getPlayer();
		ResourceList hand = player.getResources();
		ResourceList bank = model.getBank().getResources();

		int wheatCount = hand.count(ResourceType.WHEAT);
		int oreCount = hand.count(ResourceType.ORE);
		int sheepCount = hand.count(ResourceType.SHEEP);
		
		bank.transfer(hand, ResourceType.WHEAT, 1);
		bank.transfer(hand, ResourceType.ORE, 1);
		bank.transfer(hand, ResourceType.SHEEP, 1);
		
		m.buyDevelopmentCard(playerRef);
		
		// verify that there are the correct number of devcards overall.
		assertEquals(expectedTotal, model.getBank().getDevCards().count() +
				player.getNewDevCards().count());
		// And the correct resources were expended.
		assertEquals(wheatCount, hand.count(ResourceType.WHEAT));
		assertEquals(oreCount, hand.count(ResourceType.ORE));
		assertEquals(sheepCount, hand.count(ResourceType.SHEEP));
	}
	
	@Test
	public void testCanBuildRoad() throws Exception {
		dice.enqueueRoll(5);
		m.rollDice(justin);
		// Justin has 2 ore, 1 wheat, 1 wood
		
		// Placement has been tested thoroughly in Board, so I only test resources / turns
		
		// Insufficient resources
		Map<EdgeLocation, Road> roads = m.getCatanModel().getMap().getRoadMap();
		assertFalse(m.canBuildRoad(justin, new EdgeLocation(0, 0, EdgeDirection.South)));
		
		// Sufficient resources
		model.getBank().getResources().transfer(
				justin.getHand(), ResourceType.BRICK, 1);
		assertTrue(m.canBuildRoad(justin, new EdgeLocation(0, 0, EdgeDirection.South)));
		
		// Not enough road pieces
		justin.getPlayer().setRoads(0);
		assertTrue(m.canBuildRoad(justin, new EdgeLocation(0, 0, EdgeDirection.South)));
		
		// Sufficient resources, not your turn
		model.getBank().getResources().transfer(
				steve.getHand(), ResourceType.WOOD, 1);
		assertFalse(m.canBuildRoad(steve, new EdgeLocation(1, -1, EdgeDirection.NorthWest)));
		
	}

	@Test
	public void testDoBuildRoad() throws Exception {
		dice.enqueueRoll(10);
		m.rollDice(justin);
		
		ResourceList bank = model.getBank().getResources();

		bank.transfer(justin.getHand(), ResourceType.BRICK, 2);
		bank.transfer(justin.getHand(), ResourceType.WOOD, 3);
		
		assertEquals(null, model.getLongestRoad());
		assertEquals(2, justin.getPlayer().getVictoryPoints());

		m.buildRoad(justin, new EdgeLocation(0, 0, EdgeDirection.South));
		m.buildRoad(justin, new EdgeLocation(0, 1, EdgeDirection.NorthEast));
		
		// Road isn't long enough yet
		assertEquals(null, model.getLongestRoad());
		
		m.buildRoad(justin, new EdgeLocation(0, 1, EdgeDirection.South));
		
		// now it is.
		assertEquals(justin, model.getLongestRoad());
		assertEquals(4, justin.getPlayer().getVictoryPoints());
		
		m.finishTurn(justin);
		
		// Also rudimentarily test longest road algorithm
		dice.enqueueRoll(4);
		m.rollDice(steve);

		bank.transfer(steve.getHand(), ResourceType.BRICK, 3);
		bank.transfer(steve.getHand(), ResourceType.WOOD, 4);

		// This makes a hexagonal path
		m.buildRoad(steve, new EdgeLocation(1, -1, EdgeDirection.SouthWest));
		m.buildRoad(steve, new EdgeLocation(1, -1, EdgeDirection.SouthEast));
		m.buildRoad(steve, new EdgeLocation(1, -1, EdgeDirection.NorthWest));
		m.buildRoad(steve, new EdgeLocation(1, -1, EdgeDirection.NorthEast));
		
		assertEquals(steve, model.getLongestRoad());
		assertEquals(4, steve.getPlayer().getVictoryPoints());
		
		m.finishTurn(steve);
		
		dice.enqueueRoll(6);
		m.rollDice(jordan);
		
		bank.transfer(jordan.getHand(), ResourceType.BRICK, 4);
		bank.transfer(jordan.getHand(), ResourceType.WOOD, 4);

		// Another hexagonal path
		m.buildRoad(jordan, new EdgeLocation(-2, 2, EdgeDirection.North));
		m.buildRoad(jordan, new EdgeLocation(-2, 2, EdgeDirection.NorthWest));
		m.buildRoad(jordan, new EdgeLocation(-2, 2, EdgeDirection.SouthWest));
		m.buildRoad(jordan, new EdgeLocation(-2, 2, EdgeDirection.South));
		m.buildRoad(jordan, new EdgeLocation(-2, 2, EdgeDirection.SouthEast));
		
		// Steve should still have the longest road, even though Jordan has more roads
		assertEquals(steve, model.getLongestRoad());
		
		// w00t! it works!
	}
	
	@Test
	public void testCanBuildSettlement() {
		
	}
	
	@Test
	public void testCanBuildCity() throws Exception {
		dice.enqueueRoll(8);
		m.rollDice(justin);
		
		//test nothing at current location
		HexLocation hexLoc = new HexLocation(0, 0);
		VertexLocation vertexLoc = new VertexLocation(hexLoc, VertexDirection.East);
		assertFalse(m.canBuildCity(vertexLoc));
		
		//test settlement at current location
		PlayerReference currentPlayer = m.getCatanModel().getTurnTracker().getCurrentPlayer();
		Municipality municipality = new Municipality(vertexLoc, MunicipalityType.SETTLEMENT, currentPlayer);
		Map<VertexLocation, Municipality> municipalities = m.getCatanModel().getMap().getMunicipalityMap();
		municipalities.put(municipality.getLocation(), municipality);
		m.getCatanModel().getMap().setMunicipalities(municipalities);
		
		// Still not enough resources
		assertFalse(m.canBuildCity(municipality.getLocation()));

		ResourceList hand = m.getCurrentPlayer().getHand();
		ResourceList bank = model.getBank().getResources();
		
		bank.transfer(hand, ResourceType.WHEAT, 2);
		bank.transfer(hand, ResourceType.ORE, 3);
		
		assertTrue(m.canBuildCity(municipality.getLocation()));
	}
	
	@Test
	public void testDoBuildSettlement() throws InvalidActionException {
		
		HexLocation hexLoc = new HexLocation(0, 0);
		VertexLocation vertexLoc = new VertexLocation(hexLoc, VertexDirection.East);
		//m.buildSettlement(m.getCurrentPlayer(), vertexLoc);
	}
	
	@Test
	public void testCanYearOfPlenty() throws InvalidActionException {
		dice.enqueueRoll(8);
		m.rollDice(justin);
		
		//test with empty hand
		assertFalse(m.canYearOfPlenty(justin,
				ResourceType.BRICK, ResourceType.WOOD));
		
		//test with yearOfPlenty card in hand
		DevCardList bank = new DevCardList(2,2,2);
		bank.transferCardTo(justin.getPlayer().getOldDevCards(),
				DevCardType.YEAR_OF_PLENTY);
		bank.transferCardTo(steve.getPlayer().getOldDevCards(),
				DevCardType.YEAR_OF_PLENTY);

		assertTrue(m.canYearOfPlenty(justin,
				ResourceType.BRICK, ResourceType.WOOD));
		// not Steve's turn
		assertFalse(m.canYearOfPlenty(steve,
				ResourceType.BRICK, ResourceType.WOOD));
	}
	
	@Test
	public void testDoYearOfPlenty() {
		
		//m.doYearOfPlenty();
	}
	
	@Test
	public void testCanRoadBuildingCard() throws InvalidActionException {
		//boolean can = m.canRoadBuildingCard();
		//assertFalse(can);
		
		//test with roadBuilding card in hand
		Player currentPlayer = m.getCatanModel().getTurnTracker().getCurrentPlayer().getPlayer();
		DevCardList hand = currentPlayer.getOldDevCards();
		DevCardList bank = m.getCatanModel().getBank().getDevCards();
		bank = new DevCardList(1,1,1);
		bank.transferCardTo(hand, DevCardType.ROAD_BUILD);
		//can = m.canRoadBuildingCard();
		//assertTrue(can);
	}
	
	@Test
	public void testDoRoadBuildingCard() {
		
		//m.doRoadBuildCard();
	}
	
	@Test
	public void testCanSoldier() throws InvalidActionException {
		
		//test with empty hand
		//boolean can = m.canSoldier();
		//assertFalse(can);
				
		//test with soldier card in hand
		Player currentPlayer = model.getTurnTracker().getCurrentPlayer().getPlayer();
		DevCardList hand = currentPlayer.getOldDevCards();
		DevCardList bank = m.getCatanModel().getBank().getDevCards();
		bank = new DevCardList(1,1,1);
		bank.transferCardTo(hand, DevCardType.SOLDIER);
		//can = m.canSoldier();
		//assertTrue(can);
	}
	
	@Test
	public void testDoSoldier() {
		
		//m.soldier();
	}
	
	@Test
	public void testCanMonopoly() throws InvalidActionException {
		
		//test with empty hand
		//boolean can = m.canMonopoly();
		//assertFalse(can);
						
		//test with monopoly card in hand
		Player currentPlayer = model.getTurnTracker().getCurrentPlayer().getPlayer();
		DevCardList hand = currentPlayer.getOldDevCards();
		DevCardList bank = m.getCatanModel().getBank().getDevCards();
		bank = new DevCardList(1,1,1);
		bank.transferCardTo(hand, DevCardType.MONOPOLY);
		//can = m.canMonopoly();
		//assertTrue(can);
	}
	
	@Test
	public void testDoMonopoly() {
		
		//m.doMonopoly();
	}
	
	@Test
	public void testCanMonument() throws InvalidActionException {
		
		//test with empty hand
		//boolean can = m.canMonument();
		//assertFalse(can);
						
		//test with monument card in hand
		Player currentPlayer = model.getTurnTracker().getCurrentPlayer().getPlayer();
		DevCardList hand = currentPlayer.getOldDevCards();
		DevCardList bank = m.getCatanModel().getBank().getDevCards();
		bank = new DevCardList(1,1,1);
		bank.transferCardTo(hand, DevCardType.MONUMENT);
		//can = m.canMonument();
		//assertTrue(can);
	}
	
	@Test
	public void testDoMonument() {
		
		//m.monument();
	}
	
	@Test
	public void testCanOfferTrade() throws Exception {
		
		Map<ResourceType, Integer> offered = new HashMap<>();
		Map<ResourceType, Integer> wanted = new HashMap<>();
		ResourceTradeList trade = new ResourceTradeList(offered, wanted);
		offered.put(ResourceType.ORE, 1);
		wanted.put(ResourceType.BRICK, 1);
		
		// you can't offer trades before rolling the dice.
		assertFalse(m.canOfferTrade(new TradeOffer(justin, grant, trade)));
		
		dice.enqueueRoll(8);
		m.rollDice(justin);
		
		offered.clear();
		wanted.clear();
		
		// you may not trade nothing for nothing
		assertFalse(m.canOfferTrade(new TradeOffer(justin, grant, trade)));
		
		// Valid Trade offer
		offered.put(ResourceType.ORE, 1);
		wanted.put(ResourceType.BRICK, 1);
		assertTrue(m.canOfferTrade(new TradeOffer(justin, grant, trade)));
		// Offer to trade for something they don't have
		assertTrue(m.canOfferTrade(new TradeOffer(justin, steve, trade)));
		// Attempt to trade with yourself
		assertFalse(m.canOfferTrade(new TradeOffer(justin, justin, trade)));
		// It isn't Steve's turn
		assertFalse(m.canOfferTrade(new TradeOffer(steve, justin, trade)));
		
		// You don't have what you offered
		offered.put(ResourceType.WOOD, 1);
		offered.put(ResourceType.ORE, 0);
		assertFalse(m.canOfferTrade(new TradeOffer(justin, grant, trade)));
	}
	
	@Test
	public void testDoOfferTrade() throws Exception {

		dice.enqueueRoll(8);
		m.rollDice(justin);
		
		Map<ResourceType, Integer> offered = new HashMap<>();
		Map<ResourceType, Integer> wanted = new HashMap<>();
		ResourceTradeList trade = new ResourceTradeList(offered, wanted);
		offered.put(ResourceType.ORE, 1);
		wanted.put(ResourceType.BRICK, 1);
		
		m.offerTrade(new TradeOffer(justin, grant, trade));
		
		assertFalse(m.canFinishTurn(justin));
	}
	
	@Test
	public void testCanAcceptTrade() throws Exception {

		dice.enqueueRoll(8);
		m.rollDice(justin);
		
		Map<ResourceType, Integer> offered = new HashMap<>();
		Map<ResourceType, Integer> wanted = new HashMap<>();
		ResourceTradeList trade = new ResourceTradeList(offered, wanted);
		offered.put(ResourceType.ORE, 1);
		wanted.put(ResourceType.BRICK, 1);
		
		m.offerTrade(new TradeOffer(justin, grant, trade));
		
		assertTrue(m.canAcceptTrade());
	}
	
	@Test
	public void testDoAcceptTrade() throws Exception {

		dice.enqueueRoll(8);
		m.rollDice(justin);
		
		Map<ResourceType, Integer> offered = new HashMap<>();
		Map<ResourceType, Integer> wanted = new HashMap<>();
		ResourceTradeList trade = new ResourceTradeList(offered, wanted);
		offered.put(ResourceType.ORE, 1);
		wanted.put(ResourceType.BRICK, 1);
		
		m.offerTrade(new TradeOffer(justin, grant, trade));
		
		m.acceptTrade();

		assertEquals(1, grant.getHand().count(ResourceType.ORE));
		assertEquals(1, grant.getHand().count(ResourceType.SHEEP));
		assertEquals(1, grant.getHand().count(ResourceType.WHEAT));
		assertEquals(0, grant.getHand().count(ResourceType.BRICK));
		assertEquals(0, grant.getHand().count(ResourceType.WOOD));
		
		assertEquals(1, justin.getHand().count(ResourceType.ORE));
		assertEquals(1, justin.getHand().count(ResourceType.BRICK));
		assertEquals(1, justin.getHand().count(ResourceType.WHEAT));
		assertEquals(1, justin.getHand().count(ResourceType.SHEEP));
		assertEquals(0, justin.getHand().count(ResourceType.WOOD));
	}
	
	@Test
	public void testCanMaritimeTrade() {
		
		
	}
	
	@Test
	public void testDoMaritimeTrade() {
		
		//m.maritimeTrade();
	}
	

}
