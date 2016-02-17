package shared.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import shared.definitions.MunicipalityType;
import shared.definitions.ResourceType;
import shared.exceptions.DuplicateKeyException;
import shared.exceptions.GameInitializationException;
import shared.locations.EdgeDirection;
import shared.locations.EdgeLocation;
import shared.locations.HexLocation;
import shared.locations.VertexDirection;
import shared.locations.VertexLocation;

public class BoardTest {
	
	private Board board;
	private PlayerReference red, green, blue, red2;

	private List<Hex> hexList;
	private List<Port> portList;
	private List<Road> roadList;
	private List<Municipality> townList;
	
	private static final ResourceType[] resources = {
		ResourceType.WHEAT,
		
		ResourceType.BRICK,
		ResourceType.SHEEP,
		null, // Desert
		ResourceType.ORE,
		ResourceType.WOOD,
		ResourceType.SHEEP,

		ResourceType.WHEAT,
		ResourceType.WOOD,
		ResourceType.WOOD,
		ResourceType.ORE,
		ResourceType.BRICK,
		ResourceType.WHEAT,
		ResourceType.SHEEP,
		ResourceType.BRICK,
		ResourceType.SHEEP,
		ResourceType.WHEAT,
		ResourceType.ORE,
		ResourceType.WOOD
	};
	
	private static final int[] numbers = {
		4,
		9, 5, Hex.EMPTY_NUMBER, 11, 6, 3,
		4, 11, 2, 6, 10, 3, 8, 9, 12, 10, 5, 8
	};

	// All tests should be on the same board.
	@Before
	public void setUp() throws Exception {
		hexList = new ArrayList<Hex>();
		portList = new ArrayList<Port>();
		roadList = new ArrayList<Road>();
		townList = new ArrayList<Municipality>();
		
		red = PlayerReference.getDummyPlayerReference(0);
		red2 = PlayerReference.getDummyPlayerReference(0);
		green = PlayerReference.getDummyPlayerReference(1);
		blue = PlayerReference.getDummyPlayerReference(2);
		
		int hexIndex=0;
		for (HexLocation location: HexLocation.locationsWithinRadius(2)) {
			hexList.add(new Hex(location, resources[hexIndex], numbers[hexIndex]));
			++hexIndex;
		}

		portList.add(new Port(new EdgeLocation( 0,-2, EdgeDirection.North), ResourceType.WOOD));
		portList.add(new Port(new EdgeLocation( 1,-2, EdgeDirection.NorthEast), ResourceType.WHEAT));
		portList.add(new Port(new EdgeLocation( 2,-1, EdgeDirection.NorthEast), null));
		portList.add(new Port(new EdgeLocation( 2, 0, EdgeDirection.SouthEast), ResourceType.SHEEP));
		portList.add(new Port(new EdgeLocation( 1, 1, EdgeDirection.South), null));
		portList.add(new Port(new EdgeLocation(-1, 2, EdgeDirection.South), ResourceType.BRICK));
		portList.add(new Port(new EdgeLocation(-2, 2, EdgeDirection.SouthWest), null));
		portList.add(new Port(new EdgeLocation(-2, 1, EdgeDirection.NorthWest), ResourceType.ORE));
		portList.add(new Port(new EdgeLocation(-1,-1, EdgeDirection.NorthWest), null));
		
		roadList.add(new Road(new EdgeLocation( 0,-1, EdgeDirection.NorthEast), red));		
		roadList.add(new Road(new EdgeLocation( 0, 0, EdgeDirection.SouthEast), red));		
		roadList.add(new Road(new EdgeLocation( 0, 0, EdgeDirection.South),     red));		
		roadList.add(new Road(new EdgeLocation( 0, 0, EdgeDirection.SouthWest), red));			
		roadList.add(new Road(new EdgeLocation(-1, 1, EdgeDirection.SouthEast), red));
		townList.add(new Municipality(new VertexLocation(-1, 2, VertexDirection.NorthEast),
				MunicipalityType.SETTLEMENT, red));
		townList.add(new Municipality(new VertexLocation( 1,-2, VertexDirection.West),
				MunicipalityType.SETTLEMENT, red));
		townList.add(new Municipality(new VertexLocation( 0, 0, VertexDirection.East),
				MunicipalityType.CITY, red));

		roadList.add(new Road(new EdgeLocation(-1, 1, EdgeDirection.North),     green));
		roadList.add(new Road(new EdgeLocation(-1, 1, EdgeDirection.NorthWest), green));
		roadList.add(new Road(new EdgeLocation(-1, 0, EdgeDirection.SouthWest), green));
		roadList.add(new Road(new EdgeLocation( 1,-1, EdgeDirection.SouthEast), green));
		roadList.add(new Road(new EdgeLocation(-2, 0, EdgeDirection.NorthEast), green));
		roadList.add(new Road(new EdgeLocation(-2, 0, EdgeDirection.SouthEast), green));
		roadList.add(new Road(new EdgeLocation(-1, 1, EdgeDirection.SouthWest), green));
		roadList.add(new Road(new EdgeLocation(-1, 1, EdgeDirection.South),     green));
		roadList.add(new Road(new EdgeLocation(-1,-1, EdgeDirection.NorthWest), green));
		townList.add(new Municipality(new VertexLocation(-2, 1, VertexDirection.NorthEast),
				MunicipalityType.SETTLEMENT, green));
		townList.add(new Municipality(new VertexLocation(-2, 1, VertexDirection.SouthEast),
				MunicipalityType.SETTLEMENT, green));
		townList.add(new Municipality(new VertexLocation( 1,-1, VertexDirection.East),
				MunicipalityType.SETTLEMENT, green));

		roadList.add(new Road(new EdgeLocation( 2, 0, EdgeDirection.North),     blue));
		roadList.add(new Road(new EdgeLocation( 2, 0, EdgeDirection.NorthEast), blue));
		roadList.add(new Road(new EdgeLocation( 2, 0, EdgeDirection.SouthEast), blue));
		roadList.add(new Road(new EdgeLocation( 2,-1, EdgeDirection.SouthEast), blue));
		roadList.add(new Road(new EdgeLocation( 1, 0, EdgeDirection.North),     blue));
		roadList.add(new Road(new EdgeLocation( 1, 0, EdgeDirection.NorthEast), blue));
		townList.add(new Municipality(new VertexLocation(-1,-1, VertexDirection.SouthEast),
				MunicipalityType.SETTLEMENT, blue));
		townList.add(new Municipality(new VertexLocation( 2,-1, VertexDirection.East),
				MunicipalityType.SETTLEMENT, blue));
		townList.add(new Municipality(new VertexLocation( 2, 0, VertexDirection.NorthWest),
				MunicipalityType.SETTLEMENT, blue));
		townList.add(new Municipality(new VertexLocation( 2, 0, VertexDirection.SouthEast),
				MunicipalityType.CITY, blue));
		
		board = new Board(2, hexList, portList, roadList, townList, new HexLocation(1,1));
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testConstructor() {
		// Make sure the stock/test board is constructed correctly
		int hexIndex=0;
		for (HexLocation location: HexLocation.locationsWithinRadius(2)) {
			Hex hex = board.getHexAt(location);
			assertEquals(numbers[hexIndex], hex.getNumber());
			assertEquals(resources[hexIndex], hex.getResource());
			++hexIndex;
		}

		Collection<Port> ports = board.getPorts();
		Collection<Road> roads = board.getRoads();
		Collection<Municipality> towns = board.getMunicipalities();

		// Check that there are the right number of ports, roads, and towns
		assertEquals(portList.size(), ports.size());
		assertEquals(roadList.size(), roads.size());
		assertEquals(townList.size(), towns.size());
		
		assertEquals(ResourceType.WOOD, board.getPortAt(new EdgeLocation( 0,-2, EdgeDirection.North)).getResource());
		assertEquals(ResourceType.WHEAT, board.getPortAt(new EdgeLocation( 1,-2, EdgeDirection.NorthEast)).getResource());
		assertEquals(null, board.getPortAt(new EdgeLocation( 2,-1, EdgeDirection.NorthEast)).getResource());
		assertEquals(ResourceType.SHEEP, board.getPortAt(new EdgeLocation( 2, 0, EdgeDirection.SouthEast)).getResource());
		assertEquals(null, board.getPortAt(new EdgeLocation( 1, 1, EdgeDirection.South)).getResource());
		assertEquals(ResourceType.BRICK, board.getPortAt(new EdgeLocation(-1, 2, EdgeDirection.South)).getResource());
		assertEquals(null, board.getPortAt(new EdgeLocation(-2, 2, EdgeDirection.SouthWest)).getResource());
		assertEquals(ResourceType.ORE, board.getPortAt(new EdgeLocation(-2, 1, EdgeDirection.NorthWest)).getResource());
		assertEquals(null, board.getPortAt(new EdgeLocation(-1,-1, EdgeDirection.NorthWest)).getResource());
		
		// test for fails/exceptions
		List<Hex> badHexes;
		List<Port> badPorts;
		List<Road> badRoads;
		List<Municipality> badTowns;
		
		try {
			assertEquals(new Board(false, false, false), new Board(false, false, false));
		} catch (GameInitializationException e1) {
			fail("Default board failed to initialize");
		}
		
		Random rand = new Random();
		for (int i=0; i<500; ++i) {
			boolean randomHexes = rand.nextBoolean(),
					randomNumbers = rand.nextBoolean(),
					randomPorts = rand.nextBoolean();
			try {
				new Board(randomHexes, randomNumbers, randomPorts);
			} catch (GameInitializationException e) {
				fail("Board could not random-initialize on iteration " + i + "\n(" +
					(randomHexes ? "Random Hexes, " : "Default Hexes, ") +
					(randomNumbers ? "Random Numbers, " : "Default Ports, ") +
					(randomPorts ? "Random Ports) " : "Default Ports)"));
			}
		}

		// Hex out of bounds
		try {
			badHexes = new ArrayList<>(hexList);
			badHexes.add(new Hex(new HexLocation( 3,-1), ResourceType.BRICK, 4));
			new Board(2, badHexes, portList, roadList, townList, new HexLocation(1,1));
			fail("expected an exception.");
		} catch (IndexOutOfBoundsException e) {
			
		} catch (Exception e) {
			fail("Wrong exception");
		}
		
		// 2 Hexes at same location
		try {
			badHexes = new ArrayList<>(hexList);
			badHexes.add(new Hex(new HexLocation(-1, 1), ResourceType.BRICK, 4));
			new Board(2, badHexes, portList, roadList, townList, new HexLocation(1,1));
			fail("expected an exception.");
		} catch (DuplicateKeyException e) {
			
		} catch (Exception e) {
			fail("Wrong exception");
		}
		
		// 1 Hex is missing
		try {
			badHexes = new ArrayList<>(hexList);
			badHexes.remove(3);
			new Board(2, badHexes, portList, roadList, townList, new HexLocation(1,1));
			fail("expected an exception.");
		} catch (GameInitializationException e) {
			assertEquals("Some hexes are missing.", e.getMessage());
		} catch (Exception e) {
			fail("Wrong exception");
		}

		// Duplicate port
		try {
			badPorts = new ArrayList<>(portList);
			badPorts.add(new Port(new EdgeLocation( 2, 0, EdgeDirection.SouthEast), ResourceType.SHEEP));
			new Board(2, hexList, badPorts, roadList, townList, new HexLocation(1,1));
			fail("expected an exception.");
		} catch (DuplicateKeyException e) {
			
		} catch (Exception e) {
			fail("Wrong exception");
		}

		// 2 ports too close to each other
		try {
			badPorts = new ArrayList<>(portList);
			badPorts.add(new Port(new EdgeLocation( 2, 0, EdgeDirection.NorthEast), null));
			new Board(2, hexList, badPorts, roadList, townList, new HexLocation(1,1));
			//fail("expected an exception.");
		} catch (GameInitializationException e) {
			
		} catch (Exception e) {
			fail("Wrong exception");
		}

		// Port in middle of board
		try {
			badPorts = new ArrayList<>(portList);
			badPorts.add(new Port(new EdgeLocation( 0, 0, EdgeDirection.South), ResourceType.WOOD));
			new Board(2, hexList, badPorts, roadList, townList, new HexLocation(1,1));
			//fail("expected an exception.");
		} catch (IndexOutOfBoundsException e) {
			
		} catch (Exception e) {
			fail("Wrong exception");
		}

		// Port outside board
		try {
			badPorts = new ArrayList<>(portList);
			badPorts.add(new Port(new EdgeLocation( 0, -5, EdgeDirection.North), ResourceType.WOOD));
			new Board(2, hexList, badPorts, roadList, townList, new HexLocation(1,1));
			//fail("expected an exception.");
		} catch (IndexOutOfBoundsException e) {
			
		} catch (Exception e) {
			fail("Wrong exception");
		}
		
		// Duplicate road
		try {
			badRoads = new ArrayList<>(roadList);
			badRoads.add(new Road(new EdgeLocation( 2, 0, EdgeDirection.NorthEast), blue));
			new Board(2, hexList, portList, badRoads, townList, new HexLocation(1,1));
			fail("expected an exception.");
		} catch (DuplicateKeyException e) {
			
		} catch (Exception e) {
			fail("Wrong exception");
		}
		
		// Two roads at same location
		try {
			badRoads = new ArrayList<>(roadList);
			badRoads.add(new Road(new EdgeLocation( 3,-1, EdgeDirection.SouthWest), red));
			new Board(2, hexList, portList, badRoads, townList, new HexLocation(1,1));
			fail("expected an exception.");
		} catch (DuplicateKeyException e) {
			
		} catch (Exception e) {
			fail("Wrong exception");
		}
		
		// Road outside board
		try {
			badRoads = new ArrayList<>(roadList);
			badRoads.add(new Road(new EdgeLocation( 30,-1, EdgeDirection.SouthWest), red));
			new Board(2, hexList, portList, badRoads, townList, new HexLocation(1,1));
			fail("expected an exception.");
		} catch (IndexOutOfBoundsException e) {
			
		} catch (Exception e) {
			fail("Wrong exception");
		} 
		
		// duplicate municipality
		try {
			badTowns = new ArrayList<>(townList);
			badTowns.add(new Municipality(new VertexLocation(-1,-1, VertexDirection.SouthEast),
					MunicipalityType.SETTLEMENT, blue));
			new Board(2, hexList, portList, roadList, badTowns, new HexLocation(1,1));
			fail("expected an exception.");
		} catch (DuplicateKeyException e) {
			
		} catch (Exception e) {
			fail("Wrong exception");
		} 
		
		// 2 municipalities at same location
		try {
			badTowns = new ArrayList<>(townList);
			badTowns.add(new Municipality(new VertexLocation(0,-1, VertexDirection.West),
					MunicipalityType.SETTLEMENT, red));
			new Board(2, hexList, portList, roadList, badTowns, new HexLocation(1,1));
			fail("expected an exception.");
		} catch (DuplicateKeyException e) {
			
		} catch (Exception e) {
			fail("Wrong exception");
		} 
		
		// settlement outside board
		try {
			badTowns = new ArrayList<>(townList);
			badTowns.add(new Municipality(new VertexLocation(10,-5, VertexDirection.NorthEast),
					MunicipalityType.SETTLEMENT, green));
			new Board(2, hexList, portList, roadList, badTowns, new HexLocation(1,1));
			fail("expected an exception.");
		} catch (IndexOutOfBoundsException e) {
			
		} catch (Exception e) {
			fail("Wrong exception");
		} 
		
		// distance rule violation
		try {
			badTowns = new ArrayList<>(townList);
			badTowns.add(new Municipality(new VertexLocation( 1, 0, VertexDirection.NorthEast),
					MunicipalityType.SETTLEMENT, green));
			new Board(2, hexList, portList, roadList, badTowns, new HexLocation(1,1));
			fail("expected an exception.");
		} catch (GameInitializationException e) {
			assertEquals("Distance Rule violation!", e.getMessage());
		} catch (Exception e) {
			fail("Wrong exception");
		} 
		
		// Robber outside board
		try {
			new Board(2, hexList, portList, roadList, townList, new HexLocation(10,1));
			fail("expected an exception.");
		} catch (IndexOutOfBoundsException e) {
			
		} catch (Exception e) {
			fail("Wrong exception");
		} 
		
		// Smaller board radius
		try {
			new Board(1, hexList, portList, roadList, townList, new HexLocation(1,1));
			fail("expected an exception.");
		} catch (IndexOutOfBoundsException e) {
			// ANYTHING could be out of bounds
		} catch (Exception e) {
			fail("Wrong exception");
		} 
		
		// Larger board radius
		try {
			new Board(4, hexList, portList, roadList, townList, new HexLocation(1,1));
			fail("expected an exception.");
		} catch (Exception e) {
			// Which exception is thrown doesn't really matter.
		} 
		
	}
	
	@Test
	public void testGetHexesByNumber() {
		assertEquals(1, board.getHexesByNumber(2).size());
		assertEquals(1, board.getHexesByNumber(12).size());
		assertEquals(1, board.getHexesByNumber(Hex.EMPTY_NUMBER).size());
		assertEquals(0, board.getHexesByNumber(7).size());
		
		for (int i=3; i<=11; ++i) {
			if (i == 7) continue;
			assertEquals(2, board.getHexesByNumber(i).size());
		}
		
		Collection<Hex> setA, setB, setC;
		setA = board.getHexesByNumber(4);
		setB = board.getHexesByNumber(8);
		setC = board.getHexesByNumber(11);

		assertTrue(setA.contains(new Hex( 0, 0, ResourceType.WHEAT, 4)));
		assertTrue(setA.contains(new Hex( 0,-2, ResourceType.WHEAT, 4)));
		assertTrue(setB.contains(new Hex(-1,-1, ResourceType.WOOD, 8)));
		assertTrue(setB.contains(new Hex( 0, 2, ResourceType.SHEEP, 8)));
		assertTrue(setC.contains(new Hex( 0, 1, ResourceType.ORE, 11)));
		assertTrue(setC.contains(new Hex( 1,-2, ResourceType.WOOD, 11)));
		
		List<Hex> bigBoard = new ArrayList<>();
		Map<Integer, List<Hex>> numberTable = new HashMap<>();
		
		Random rand = new Random(12345L); // We need a consistent random to make this test better.
		final int radius = 5;
		
		for (HexLocation loc : HexLocation.locationsWithinRadius(radius)) {
			int number = rand.nextInt(10);
			number += (number < 5) ? 2 : 3;
			
			if (!numberTable.containsKey(number)) {
				numberTable.put(number, new ArrayList<Hex>());
			}
			
			// Resource type doesn't matter.
			Hex hex = new Hex(loc, ResourceType.SHEEP, number);
			numberTable.get(number).add(hex);
			bigBoard.add(hex);
		}
		
		try {
			Board testBoard = new Board(radius, bigBoard, new ArrayList<Port>(),
					new ArrayList<Road>(), new ArrayList<Municipality>(), new HexLocation(0,0));
			
			for (int i=2; i<=12; ++i) {
				if (i == 7) continue;
				Collection<Hex> a = numberTable.get(i);
				Collection<Hex> b = testBoard.getHexesByNumber(i);
				// Confirm that they contain the same hexes
				assertTrue(a.containsAll(b));
				assertTrue(b.containsAll(a));
			}
			
		} catch (DuplicateKeyException e) {
			e.printStackTrace();
			fail("Unexpected exception");
		} catch (GameInitializationException e) {
			e.printStackTrace();
			fail("Unexpected exception");
		}
	}

	@Test
	public void testGetPortOwner() {
		assertEquals(blue, board.getPortOwner(
				new Port(new EdgeLocation( 2, 0, EdgeDirection.SouthEast), ResourceType.SHEEP)));
		assertEquals(blue, board.getPortOwner(
				new Port(new EdgeLocation( 2,-1, EdgeDirection.NorthEast), null)));
		assertEquals(null, board.getPortOwner(
				new Port(new EdgeLocation(-1, 2, EdgeDirection.South), ResourceType.BRICK)));
	}

	@Test
	public void testGetOwnerOfPortAt() {
		assertEquals(blue, board.getOwnerOfPortAt(new EdgeLocation(2, 0, EdgeDirection.SouthEast)));
		assertEquals(blue, board.getOwnerOfPortAt(new EdgeLocation(2,-1, EdgeDirection.NorthEast)));

		// Location in middle of board
		try {
			board.getOwnerOfPortAt(new EdgeLocation(0, 0, EdgeDirection.South));
			fail("expected an exception.");
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail("Wrong Exception");
		}
		
		// Location on edge with no port
		try {
			board.getOwnerOfPortAt(new EdgeLocation(0,-2, EdgeDirection.NorthWest));
			fail("expected an exception.");
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail("Wrong Exception");
		}
	}

	@Test
	public void testCanBuildRoadAt() {
		assertFalse(board.canBuildRoadAt(red,
				new EdgeLocation( 0, 0, EdgeDirection.South))); // already your road there
		assertFalse(board.canBuildRoadAt(red,
				new EdgeLocation(-1, 0, EdgeDirection.South))); // already opponent's road there
		assertFalse(board.canBuildRoadAt(green,
				new EdgeLocation(-1,-2, EdgeDirection.SouthWest))); // just outside board, but still connected

		assertTrue(board.canBuildRoadAt(green,
				new EdgeLocation( 0, 0, EdgeDirection.NorthWest)));
		assertTrue(board.canBuildRoadAt(red,
				new EdgeLocation( 0, 0, EdgeDirection.NorthWest)));
		assertTrue(board.canBuildRoadAt(red2,
				new EdgeLocation( 0, 0, EdgeDirection.NorthWest))); // make sure they are .equal, not just ==
		assertTrue(board.canBuildRoadAt(blue,
				new EdgeLocation( 0,-1, EdgeDirection.NorthWest))); // No road, only a settlement (can't technically happen... whatever)

		assertFalse(board.canBuildRoadAt(green,
				new EdgeLocation( 0, 1, EdgeDirection.SouthWest))); // opponent's settlement is in the way
		assertTrue(board.canBuildRoadAt(blue,
				new EdgeLocation( 2, 0, EdgeDirection.NorthWest))); // Your settlement is "in the way" (but the move is still valid)
	}

	@Test
	public void testCanBuildSettlement() {
		assertFalse(board.canBuildSettlement(red, 
				new VertexLocation(-1, 1, VertexDirection.SouthEast))); // already your settlement there
		assertFalse(board.canBuildSettlement(red, 
				new VertexLocation(-1, 10, VertexDirection.SouthEast))); // outside the board
		assertFalse(board.canBuildSettlement(red, 
				new VertexLocation(0,0, VertexDirection.East))); // already your city there
		assertFalse(board.canBuildSettlement(blue, 
				new VertexLocation(0,0, VertexDirection.East))); // already another player's city there
		assertFalse(board.canBuildSettlement(red, 
				new VertexLocation(1,0, VertexDirection.SouthWest))); // no roads to location
		assertFalse(board.canBuildSettlement(green, 
				new VertexLocation(1,0, VertexDirection.NorthEast))); // distance rule
		assertFalse(board.canBuildSettlement(blue, 
				new VertexLocation(0,0, VertexDirection.West))); // roads belong to another player
		assertFalse(board.canBuildSettlement(red, 
				new VertexLocation(-1,-1, VertexDirection.West))); // roads belong to another player
		
		assertTrue(board.canBuildSettlement(green, 
				new VertexLocation(0,0, VertexDirection.West)));
		assertTrue(board.canBuildSettlement(red, 
				new VertexLocation(0,0, VertexDirection.West)));
		assertTrue(board.canBuildSettlement(red2, 
				new VertexLocation(0,0, VertexDirection.West))); // Make sure it still works with a different object of the same value
		assertTrue(board.canBuildSettlement(green, 
				new VertexLocation(-1,-1, VertexDirection.West)));
	}

	@Test
	public void testCanBuildCity() {
		assertFalse(board.canBuildCity(green, 
				new VertexLocation(-1, 1, VertexDirection.SouthEast))); // opponent's settlement
		assertFalse(board.canBuildCity(green, 
				new VertexLocation(-1, 1, VertexDirection.NorthEast))); // no settlement there, but valid place for settlement
		assertFalse(board.canBuildCity(blue, 
				new VertexLocation(-1, 8, VertexDirection.SouthEast))); // outside board
		assertFalse(board.canBuildCity(red, 
				new VertexLocation( 0, 0, VertexDirection.East))); // already your own city there
		assertFalse(board.canBuildCity(blue, 
				new VertexLocation( 0, 0, VertexDirection.East))); // opponent's city there

		assertTrue(board.canBuildCity(red, 
				new VertexLocation(-1, 1, VertexDirection.SouthEast)));
		assertTrue(board.canBuildCity(red2, 
				new VertexLocation(-1, 1, VertexDirection.SouthEast))); // Check by value as well as reference
		assertTrue(board.canBuildCity(green, 
				new VertexLocation(-1, 1, VertexDirection.West)));
		
	}

	@Test
	public void testCanMoveRobberTo() {
		assertFalse(board.canMoveRobberTo(new HexLocation(1, 0))); // Desert
		assertFalse(board.canMoveRobberTo(new HexLocation(1, 1))); // Same place as before
		assertFalse(board.canMoveRobberTo(new HexLocation(20, 20))); // Off the board

		assertTrue(board.canMoveRobberTo(new HexLocation(2, 0)));
		assertTrue(board.canMoveRobberTo(new HexLocation(0,-2)));
	}
	
	@Test
	public void testCanPlaceStartingPieces () {
		// In theory, we would do this on a FRESH board, but the stock test board will do just fine.
		assertTrue(board.canPlaceStartingPieces(
				new VertexLocation( 0, 0, VertexDirection.West),
				new EdgeLocation(   0, 0, EdgeDirection.NorthWest)));
		assertTrue(board.canPlaceStartingPieces(
				new VertexLocation( 1, 1, VertexDirection.NorthWest),
				new EdgeLocation(   1, 1, EdgeDirection.NorthWest)));
		
		assertFalse(board.canPlaceStartingPieces( // Road spot taken
				new VertexLocation( 0, 0, VertexDirection.West),
				new EdgeLocation(   0, 0, EdgeDirection.SouthWest)));
		assertFalse(board.canPlaceStartingPieces( // Town spot taken
				new VertexLocation( 0, 0, VertexDirection.East),
				new EdgeLocation(   0, 0, EdgeDirection.NorthEast)));
		assertFalse(board.canPlaceStartingPieces( // Distance Rule
				new VertexLocation( 0, 0, VertexDirection.NorthEast),
				new EdgeLocation(   0, 0, EdgeDirection.North)));
		assertFalse(board.canPlaceStartingPieces( // Road off the board
				new VertexLocation( 1, 1, VertexDirection.SouthEast),
				new EdgeLocation(   2, 1, EdgeDirection.SouthWest)));
		assertFalse(board.canPlaceStartingPieces( // Both off the board
				new VertexLocation(10, 2, VertexDirection.West),
				new EdgeLocation(  10, 2, EdgeDirection.SouthWest)));
		assertFalse(board.canPlaceStartingPieces( // Settlement not adjacent to road
				new VertexLocation( 0, 0, VertexDirection.West),
				new EdgeLocation(   1, 0, EdgeDirection.SouthWest)));
	}

}
