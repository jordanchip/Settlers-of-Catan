package shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.json.simple.JSONObject;

import shared.definitions.MunicipalityType;
import shared.definitions.ResourceType;
import shared.exceptions.DuplicateKeyException;
import shared.exceptions.GameInitializationException;
import shared.exceptions.InvalidActionException;
import shared.exceptions.SchemaMismatchException;
import shared.locations.EdgeDirection;
import shared.locations.EdgeLocation;
import shared.locations.HexLocation;
import shared.locations.VertexLocation;

/**
 * Contains references to the entire layout of the map at any given moment:
 * ports, roads, settlements, cities, and the robber.
 * @author Jordan
 *
 */
public class Board 
implements Serializable {
	private static final long serialVersionUID = 2690058481554653945L;
	
	// IMPORTANT: mutator functions should ALWAYS be package private (no modifier).
	// All modifications to model elements should be done through the Facade
	
	private Map<HexLocation, Hex> hexes;
	// This is an internal representation of the size of the board. It differs from the JSON
	// in that it does not include the center or water hexes.
	private int radius = 2;
	
	private Map<EdgeLocation, Port> ports;
	private Map<EdgeLocation, Road> roads;
	private Map<VertexLocation, Municipality> municipalities;
	
	private HexLocation robber;

	public Board() throws GameInitializationException {
		this(false, false, false);
	}
	
	public Board(boolean hasRandomHexes, boolean hasRandomNumbers,
			boolean hasRandomPorts) throws GameInitializationException {
		List<ResourceType> hexTypes;
		if (!hasRandomHexes) {
			hexTypes = initDefaultHexTypes();
		}
		else {
			hexTypes = initRandomHexTypes();
		}
		if (!hasRandomNumbers) {
			initHexesWithDefaultNumbers(hexTypes);
		}
		else {
			initRandomNumbers(hexTypes);
		}
		if (!hasRandomPorts) {
			initDefaultPorts();
		}
		else {
			initRandomPorts();
		}
		roads = new HashMap<EdgeLocation, Road>();
		municipalities = new HashMap<VertexLocation, Municipality>();
	}

	private static final List<ResourceType> defaultHexTypes = Arrays.asList(
			new ResourceType[] {
		ResourceType.ORE,  ResourceType.SHEEP, ResourceType.SHEEP, 
		ResourceType.WHEAT, ResourceType.WOOD, ResourceType.ORE,
		ResourceType.WHEAT, ResourceType.WOOD, ResourceType.BRICK, 
		ResourceType.WOOD, ResourceType.ORE, ResourceType.WHEAT, 
		ResourceType.BRICK, ResourceType.SHEEP, ResourceType.WHEAT, 
		ResourceType.WOOD, ResourceType.BRICK, ResourceType.SHEEP,
		null
		});
	private List<ResourceType> initDefaultHexTypes() {
		return defaultHexTypes;
	}

	private List<ResourceType> initRandomHexTypes() {
		List<ResourceType> resources = new ArrayList<>(defaultHexTypes);
		Collections.shuffle(resources);
		return resources;
	}

	// This is actually slightly different from the official number layout,
	// but is modified to keep 6s and 8s and same numbers apart when iterating
	// the way the cycle iterates.
	private static final List<Integer> defaultNumbers = Arrays.asList(new Integer[]
			{11, 10, 3, 6, 5, 4, 9, 8, 4, 11, 12, 9, 10, 8, 3, 6, 2, 5});
	
	private void initHexesWithDefaultNumbers(List<ResourceType> resources) throws GameInitializationException {
		try {
			initializeHexesFromList(makeHexes(resources, defaultNumbers));
		} catch (DuplicateKeyException e) {
			e.printStackTrace();
			assert false;
		}
	}

	private void initRandomNumbers(List<ResourceType> resources) throws GameInitializationException {
		List<Integer> numbers = new ArrayList<>(defaultNumbers);
		Collections.shuffle(numbers);
		try {
			initializeHexesFromList(makeHexes(resources, numbers));
		} catch (DuplicateKeyException e) {
			e.printStackTrace();
			assert false;
		}
	}

	private List<Hex> makeHexes(List<ResourceType> resources, List<Integer> numbers) {
		Iterator<ResourceType> resIter = resources.iterator();
		Iterator<Integer> numIter = numbers.iterator();
		List<Hex> hexes = new ArrayList<>();
		for (HexLocation hexLoc : HexLocation.locationsWithinRadius(2)) {
			ResourceType resource = resIter.next();
			if (resource != null) {
				hexes.add(new Hex(hexLoc, resource, numIter.next()));
			}
			else {
				// Desert
				hexes.add(new Hex(hexLoc, resource));
				robber = hexLoc;
			}
		}
		return hexes;
	}
	
	private static final EdgeLocation[] portLocations = {
		new EdgeLocation( 0, -2, EdgeDirection.North),
		new EdgeLocation( 1, -2, EdgeDirection.NorthEast),
		new EdgeLocation( 2, -1, EdgeDirection.NorthEast),
		new EdgeLocation( 2,  0, EdgeDirection.SouthEast),
		new EdgeLocation( 1,  1, EdgeDirection.South),
		new EdgeLocation(-1,  2, EdgeDirection.South),
		new EdgeLocation(-2,  2, EdgeDirection.SouthWest),
		new EdgeLocation(-2,  1, EdgeDirection.NorthWest),
		new EdgeLocation(-1, -1, EdgeDirection.NorthWest),
	};
	
	private static final List<ResourceType> defaultPortTypes = Arrays.asList(
			new ResourceType[] {
					null,
					ResourceType.BRICK,
					ResourceType.WHEAT,
					null,
					null,
					ResourceType.SHEEP,
					null,
					ResourceType.WOOD,
					ResourceType.ORE,
			});

	private void initDefaultPorts() throws GameInitializationException {
		try {
			initializePortsFromList(makePorts(defaultPortTypes));
		} catch (DuplicateKeyException e) {
			e.printStackTrace();
			assert false;
		}
	}
	
	private void initRandomPorts() throws GameInitializationException {
		List<ResourceType> portTypes = new ArrayList<>(defaultPortTypes);
		Collections.shuffle(portTypes);
		try {
			initializePortsFromList(makePorts(portTypes));
		} catch (DuplicateKeyException e) {
			e.printStackTrace();
			assert false;
		}
	}

	private List<Port> makePorts(List<ResourceType> portTypes) {
		Iterator<ResourceType> resIter = portTypes.iterator();
		List<Port> ports = new ArrayList<>();
		for (EdgeLocation edge : portLocations) {
			ports.add(new Port(edge, resIter.next()));
		}
		return ports;
	}

	public Board(int boardRadius, List<Hex> hexList, List<Port> ports,
			List<Road> roads, List<Municipality> towns,	HexLocation robberLocation)
					throws DuplicateKeyException, GameInitializationException {
		radius = boardRadius;
		initializeHexesFromList(hexList);
		initializePortsFromList(ports);
		initializeRoadsFromList(roads);
		initializeMunicipalitiesFromList(towns);
		if (robberLocation.getDistanceFromCenter() > boardRadius) {
			throw new IndexOutOfBoundsException();
		}
		robber = robberLocation;
	}
	
	@SuppressWarnings("unchecked")
	public Board(List<Player> players, JSONObject json)
			throws SchemaMismatchException, GameInitializationException {
		try {
			/*
			 * PLEASE NOTE: REMOVED RADIUS - 1, MIGHT CAUSE
			 * PROBLEMS LATER ON.
			 */
			radius = (int) (long) json.get("radius"); 
			if (json.containsKey("hexes")) {
				List<Hex> hexData = new ArrayList<>();
				//TODO: Fix this line, can't can't lists from JSON!
				Map<Object, Object> obj = (Map<Object, Object>)json.get("hexes");
//				for (Object obj : (List<Object>) json.get("hexes")) {
//					hexData.add(new Hex((JSONObject) obj));
//				}
				for (Object value : obj.values()) {
					hexData.add(new Hex((JSONObject) value));
				}
				initializeHexesFromList(hexData);
			}
			else throw new SchemaMismatchException("Board data is missing from the JSON:" +
					json.toJSONString());
			
			robber = new HexLocation((JSONObject) json.get("robber"));
			
			List<Port> portData = new ArrayList<>();
//			for (Object obj : (List<Object>) json.get("ports")) {
//				portData.add(new Port((JSONObject) obj));
//			}
			Map<Object, Object> obj = (Map<Object, Object>)json.get("ports");
			for (Object value : obj.values()) {
				portData.add(new Port((JSONObject) value));
			}
			initializePortsFromList(portData);
			
			List<Road> roadData = new ArrayList<>();
//			for (Object obj : (List<Object>) json.get("roads")) {
//				roadData.add(new Road(players, (JSONObject) obj));
//			}
			obj = (Map<Object, Object>)json.get("roads");
			for (Object value : obj.values()) {
				roadData.add(new Road(players, (JSONObject) value));
			}
			initializeRoadsFromList(roadData);
			
			List<Municipality> towns = new ArrayList<>();
//			for (Object obj : (List<Object>) json.get("settlements")) {
//				towns.add(new Municipality(players, (JSONObject) obj, MunicipalityType.SETTLEMENT));
//			}
//			for (Object obj : (List<Object>) json.get("cities")) {
//				towns.add(new Municipality(players, (JSONObject) obj, MunicipalityType.CITY));
//			}
			obj = (Map<Object, Object>)json.get("municipalities");
			for (Object value : obj.values()) {
				towns.add(new Municipality(players, (JSONObject) value));
			}
//			obj = (Map<Object, Object>)json.get("cities");
//			for (Object value : obj.values()) {
//				towns.add(new Municipality(players, (JSONObject) obj, MunicipalityType.CITY));
//			}
			initializeMunicipalitiesFromList(towns);
		}
		catch (ClassCastException | IllegalArgumentException e) {
			e.printStackTrace();
			throw new SchemaMismatchException("The JSON does not follow the expected schema " +
					"for a Board:\n" + json.toJSONString());
		}
		catch (DuplicateKeyException e) {
			e.printStackTrace();
			throw new SchemaMismatchException("Two (or more) objects share the same location:\n"
					+ json.toJSONString());
		} catch (GameInitializationException e) {
			throw new SchemaMismatchException(e.getMessage() + ":\n"
					+ json.toJSONString());
		}
	}
	
	private void initializePortsFromList(List<Port> portData)
			throws DuplicateKeyException, GameInitializationException {
		ports = new HashMap<>();
		for (Port port : portData) {
			// Make sure the port is on the edge of the board
			EdgeLocation location = port.getLocation();
			if (location.getDistanceFromCenter() != radius || location.isSpoke()) {
				throw new IndexOutOfBoundsException();
			}
			// Ensure ports are not too close together
			for (EdgeLocation neighbor : location.getNeighbors()) {
				if (ports.containsKey(neighbor)) {
					throw new GameInitializationException();
				}
			}
			// Ensure there aren't two ports at the same location
			if (ports.containsKey(location)) {
				throw new DuplicateKeyException();
			}
			if (location.getHexLoc().getDistanceFromCenter() == radius) {
				port.setLocation(location.flip());
			}
			ports.put(location, port);
		}
	}

	private void initializeRoadsFromList(List<Road> roadData) throws DuplicateKeyException {
		roads = new HashMap<>();
		for (Road road : roadData) {
			// Make sure the road is on the board
			EdgeLocation location = road.getLocation();
			if (location.getDistanceFromCenter() > radius) {
				throw new IndexOutOfBoundsException();
			}
			if (roads.containsKey(location)) {
				throw new DuplicateKeyException();
			}
			roads.put(location, road);
		}
	}

	private void initializeMunicipalitiesFromList(List<Municipality> towns)
			throws DuplicateKeyException, GameInitializationException {
		municipalities = new HashMap<>();
		for (Municipality town : towns) {
			// make sure the city is on the board.
			VertexLocation location = town.getLocation();
			if (location.getDistanceFromCenter() > radius) {
				throw new IndexOutOfBoundsException();
			}
			// enforce Distance Rule
			for (VertexLocation neighbor : location.getNeighbors()) {
				if (municipalities.containsKey(neighbor)) {
					throw new GameInitializationException("Distance Rule violation!");
				}
			}
			if (municipalities.containsKey(location)) {
				throw new DuplicateKeyException();
			}
			municipalities.put(location, town);
		}
	}

	/**
	 * @param hexList
	 * @throws DuplicateKeyException if any of the hexes are repeated.
	 * @throws IndexOutOfBoundsException if any of the hex locations are outside the board's
	 * boundaries, based on radius.
	 * @throws GameInitializationException if there are not enough hexes to fill the board
	 */
	private void initializeHexesFromList(List<Hex> hexList)
			throws DuplicateKeyException, GameInitializationException {
		hexes = new HashMap<>();
		for (Hex hex : hexList) {
			HexLocation location = hex.getLocation();
			if (location.getDistanceFromCenter() > radius) {
				throw new IndexOutOfBoundsException();
			}
			if (hexes.containsKey(location)) {
				throw new DuplicateKeyException();
			}
			hexes.put(location, hex);
		}
		
		for (HexLocation location : HexLocation.locationsWithinRadius(radius)) {
			if (!hexes.containsKey(location)) {
				throw new GameInitializationException("Some hexes are missing.");
			}
		}
	}
	
	/**
	 * @return a Collection of the all the hexes on the board (in no particular order)
	 */
	public Collection<Hex> getHexes() {
		return hexes.values();
	}

	/** Gets the Hex at the given location
	 * @return the hex at the given location
	 * @pre the location is actually on the board
	 * @post none
	 * @throws IndexOutOfBoundsException if the location is outside the board
	 */
	public Hex getHexAt(HexLocation location) {
		if (location.getDistanceFromCenter() > radius) {
			throw new IndexOutOfBoundsException();
		}
		if (hexes.containsKey(location)) {
			return hexes.get(location);
		}
		else {
			throw new IndexOutOfBoundsException();
		}
	}
	
	/** Gives a collection of all Hexes with the given number
	 * @param number the number to search for
	 * @return A Collection containing the hexes with the given number
	 */
	public Collection<Hex> getHexesByNumber(int number) {
		List<Hex> result = new ArrayList<>();
		for (Hex hex : hexes.values()) {
			if (hex.getNumber() == number) {
				result.add(hex);
			}
		}
		return result;
	}

	/** Gives the location of the desert tile
	 * 
	 * @return location of the desert tile
	 */
	public HexLocation getDesertLocation() {
		
		
		for(Hex hex : hexes.values()) {
			if(hex.getResource() == null)
				return hex.getLocation();
		}
		
		return null;
	}
	
	/**
	 * @return a Collection of all the ports on the board (in no particular order)
	 */
	public Collection<Port> getPorts() {
		return ports.values();
	}
	
	/** Returns a port (if there is one) at the given location
	 * @param location the EdgeLocation to check
	 * @return the port if there is a port at the given edge
	 * @return null otherwise
	 */
	public Port getPortAt(EdgeLocation location) {
		if (location.getDistanceFromCenter() > radius) {
			throw new IndexOutOfBoundsException();
		}
		if (ports.containsKey(location)) {
			return ports.get(location);
		}
		else return null;
	}
	

	/** Returns a port (if there is one) at the given location
	 * @param location the VertexLocation to check
	 * @return the port if there is a port at the given edge
	 * @return null otherwise
	 */
	public Port getPortAt(VertexLocation location) {
		if (location.getDistanceFromCenter() > radius) {
			throw new IndexOutOfBoundsException();
		}
		Port port = null;
		for (EdgeLocation loc: location.getEdges()) {
			Port p = getPortAt(loc);
			if (p != null) {
				if (port == null) {
					port = p;
				}
				else assert false; // This should not be allowed to happen
				// ports should never be allowed to be on adjacent edges
			}
		}
		return port;
	}
	
	/** Gets the owner of a particular port on the board
	 * @param port
	 * @return
	 */
	public PlayerReference getPortOwner(Port port) {
		for (VertexLocation location : port.getLocation().getVertices()) {
			Municipality town = getMunicipalityAt(location);
			if (town != null) return town.getOwner();
		}
		return null;
	}
	
	/** Gets the owner of a port at a specific location on the board. (Useful?)
	 * @param edge
	 * @return
	 */
	public PlayerReference getOwnerOfPortAt(EdgeLocation edge) {
		Port port = getPortAt(edge);
		if (port == null) {
			throw new IllegalArgumentException("There is no Port at that location!");
		}
		else return getPortOwner(port);
	}

	/**
	 * @return a Collection of all the roads on the board (in no particular order)
	 */
	public Collection<Road> getRoads() {
		return roads.values();
	}
	
	/**
	 * @param location
	 * @return the road at the specified location
	 * @return null if there is no road there
	 * @pre the location is on the board
	 * @throws IndexOutOfBoundsException if the road outside the boundaries of the board
	 */
	public Road getRoadAt(EdgeLocation location) {
		if (location.getDistanceFromCenter() > radius) {
			throw new IndexOutOfBoundsException();
		}
		if (roads.containsKey(location)) {
			return roads.get(location);
		}
		else return null;
	}
	
	/** Tells you if the given location is a valid place for the given player to build a road.
	 * This does NOT check resource requirements!
	 * @param player
	 * @param location
	 * @return true if the location is a legal place for the player to build a road
	 * @return false otherwise
	 * @pre none
	 * @post none
	 */
	public boolean canBuildRoadAt(PlayerReference player, EdgeLocation location) {
		if (location.getDistanceFromCenter() > radius) return false;
		if (getRoadAt(location) == null) {
			// Adjacent road
			for (EdgeLocation neighbor : location.getNeighbors()) {
				if (neighbor.getDistanceFromCenter() > radius) continue;
				
				Road road = getRoadAt(neighbor);
				if (road != null && player.equals(road.getOwner())) {
					// check if blocked by opponent's municipality
					VertexLocation townLoc = neighbor.getVertexBetween(location);
					Municipality town = getMunicipalityAt(townLoc);
					if (town != null && !player.equals(town.getOwner())) {
						continue;
					}
					return true;
				}
			}
			// Adjacent municipality
			for (VertexLocation vertex : location.getVertices()) {
				if (vertex.getDistanceFromCenter() > radius) continue;
				
				Municipality town = getMunicipalityAt(vertex);
				if (town != null && player.equals(town.getOwner())) return true;
			}
		}
		return false;
	}

	/**
	 * @return a Collection of all the municipalities on the board (in no particular order)
	 */
	public Collection<Municipality> getMunicipalities() {
		return municipalities.values();
	}
	
	public Municipality getMunicipalityAt(VertexLocation location) {
		if (location.getDistanceFromCenter() > radius) {
			throw new IndexOutOfBoundsException();
		}
		if (municipalities.containsKey(location)) {
			return municipalities.get(location);
		}
		else return null;
	}
	
	/** Tells you if the given location is a valid place for the given player to build a settlement.
	 * This does NOT check resource requirements!
	 * @param player
	 * @param location
	 * @return true if the given location is a valid place for the player to build a settlement
	 */
	public boolean canBuildSettlement(PlayerReference player, VertexLocation location) {
		if (location.getDistanceFromCenter() > radius) {
			return false;
		}
		if (getMunicipalityAt(location) == null) { // spot is open
			// Apply Distance Rule
			for (VertexLocation neighbor : location.getNeighbors()) {
				if (neighbor.getDistanceFromCenter() > radius) continue;
				
				Municipality town = getMunicipalityAt(neighbor);
				if (town != null) return false;
			}
			// There must be one of your roads next to the vertex
			for (EdgeLocation edge : location.getEdges()) {
				if (edge.getDistanceFromCenter() > radius) continue;
				
				Road road = getRoadAt(edge);
				if (road != null && player.equals(road.getOwner())) return true;
			}
		}
		return false;
	}
	
	/** Tells you if the given location is a valid place for the given player to build a city.
	 * This does NOT check resource requirements!
	 * @param player
	 * @param location
	 * @return true if settlement belonging to the player is at the given location
	 * @return false otherwise.
	 */
	public boolean canBuildCity(PlayerReference player, VertexLocation location) {
		if (location.getDistanceFromCenter() > radius) return false;
		
		Municipality town = getMunicipalityAt(location);
		if (town == null) return false; // no settlement at that location
		return (town.getType() == MunicipalityType.SETTLEMENT
				&& player.equals(town.getOwner()));
	}
	
	/** Tells if the location is a valid location for a starting settlement/road pair
	 * @param settlement
	 * @param road
	 * @return
	 */
	public boolean canPlaceStartingPieces(VertexLocation settlement, EdgeLocation road) {
		// The road must be next to the settlement
		if (!road.getVertices().contains(settlement)) return false;
		// Needs to be on the board
		if (road.getDistanceFromCenter() > radius) return false;
		if (settlement.getDistanceFromCenter() > radius) return false;
		// There must not be a road at the location
		if (getRoadAt(road) != null) return false;
		
		if (getMunicipalityAt(settlement) == null) { // spot is open
			// Apply Distance Rule
			for (VertexLocation neighbor : settlement.getNeighbors()) {
				if (neighbor.getDistanceFromCenter() > radius) continue;
				
				Municipality town = getMunicipalityAt(neighbor);
				if (town != null) return false;
			}
			return true;
		}
		return false;
	}
	
	/** Tells if the location is a valid location for a starting settlement
	 * @param settlement
	 * @param road
	 * @return
	 */
	public boolean canPlaceStartingSettlement(VertexLocation settlement) {
		if (settlement.getDistanceFromCenter() > radius) return false;
		
		if (getMunicipalityAt(settlement) == null) { // spot is open
			// Apply Distance Rule
			for (VertexLocation neighbor : settlement.getNeighbors()) {
				if (neighbor.getDistanceFromCenter() > radius) continue;
				Municipality town = getMunicipalityAt(neighbor);
				if (town != null) return false;
			}
			return true;
		}
		return false;
	}

	/** This gives the radius that is needed by the HexGrid constructor.
	 * @return the radius, including the center hex and water hexes. 
	 */
	public int getDrawableRadius() {
		return radius + 2;
	}

	/**
	 * @return the location of the robber
	 */
	public HexLocation getRobberLocation() {
		return robber;
	}
	
	public boolean canMoveRobberTo(HexLocation location) {
		if (location.equals(robber)) {
			return false;
		}
		if (location.getDistanceFromCenter() > radius) { // Robber must stay on the board.
			return false;
		}
		if (getHexAt(location).getResource() == null) { // Can't move to the desert
			return false;
		}
		return true;
	}
	
	// NO MODIFIER: package private; this is a mutator
	void moveRobber(HexLocation location) throws InvalidActionException {
		if (!canMoveRobberTo(location))  {
			throw new InvalidActionException("The robber cannot be moved there.");
		}
		robber = location;
	}
	
	public Map<EdgeLocation, Port> getPortMap() {
		return new HashMap<>(ports);
	}
	
	public Map<VertexLocation, Municipality> getMunicipalityMap() {
		return new HashMap<>(municipalities);
	}
	
	public Map<EdgeLocation, Road> getRoadMap() {
		return new HashMap<>(roads);
	}
	
	void setRoads(Map<EdgeLocation, Road> roads) {
		this.roads = roads;
	}

	void setMunicipalities(Map<VertexLocation, Municipality> municipalities) {
		this.municipalities = municipalities;
	}

	/** Asks if 2 roads can be built, one after the other.
	 * @param player
	 * @param firstRoad
	 * @param secondRoad
	 * @return true if the 2 roads can be built. Note that order matters.
	 */
	public boolean canBuild2Roads(PlayerReference player,
			EdgeLocation firstRoad, EdgeLocation secondRoad) {
		if (firstRoad.equals(secondRoad)) return false;
		if (!canBuildRoadAt(player, firstRoad))	{
			return false;
		}
		// Road 1 is buildable from this point forward...
		// Case 1: Road 2 is not dependent on the placement of Road 1
		if (canBuildRoadAt(player, secondRoad)) {
			return true;
		}
		// Case 2: Road 2 is immediately adjacent to Road 1, but not connected to the rest
		// of the roads AND there is not another player's settlement between them
		Municipality townBetween = getMunicipalityAt(firstRoad.getVertexBetween(secondRoad));
		return secondRoad.isAdjacent(firstRoad) && 
				(townBetween == null) || townBetween.getOwner().equals(player);
	}

	public Collection<Municipality> getMunicipalitiesAround(HexLocation hex) {
		Collection<Municipality> result = new ArrayList<>();
		for (VertexLocation loc : hex.getVertices()) {
			Municipality town = getMunicipalityAt(loc);
			if (town != null) {
				result.add(town);
			}
		}
		return result;
	}

	void buildRoad(PlayerReference player, EdgeLocation loc) throws InvalidActionException {
		if (!canBuildRoadAt(player, loc)) {
			throw new InvalidActionException();
		}

		roads.put(loc, new Road(loc, player));
	}

	void placeStartingPieces(PlayerReference player,
			VertexLocation settlement, EdgeLocation road) throws InvalidActionException {
		if (!canPlaceStartingPieces(settlement, road)) {
			throw new InvalidActionException();
		}

		municipalities.put(settlement, 
				new Municipality(settlement, MunicipalityType.SETTLEMENT, player));
		roads.put(road, new Road(road, player));
		
	}

	void buildSettlement(PlayerReference player, VertexLocation loc) throws InvalidActionException {
		if (!canBuildSettlement(player, loc)) {
			throw new InvalidActionException();
		}

		municipalities.put(loc, 
				new Municipality(loc, MunicipalityType.SETTLEMENT, player));
		
	}

	void upgradeSettlementAt(PlayerReference player, VertexLocation loc) throws InvalidActionException {
		assert loc.getDistanceFromCenter() <= radius;
		assert player != null;
		
		Municipality town = getMunicipalityAt(loc);
		
		if (town == null) {
			throw new InvalidActionException();
		}
		
		if (town.getOwner().equals(player)) {
			town.upgrade();
		}
		else {
			throw new InvalidActionException("The settlement at the given location is" +
					"owned by another player.");
		}
	}
	
	public int lengthOfLongestRoute(PlayerReference player) {
		int best = 0;
		
		// TODO Testing!
		
		// Reduce to a graph problem
		Set<EdgeLocation> ownedRoads = getRoadsOwnedByPlayer(player);
		
		// Used to give priority to nodes that are likely to be endpoints of a path
		//Set<VertexLocation> nodes = new HashSet<>(); // 0, 1, 2, 3
		@SuppressWarnings("unchecked") // I can't figure out another way
		// to initialize this without errors.
		Set<VertexLocation>[] nodesByEdgeCounts = new Set[4]; // 0, 1, 2, 3
		
		for (int i=0; i<4; ++i) {
			nodesByEdgeCounts[i] = new HashSet<>();
		}
		
		// Get the nodes of the graph
		for (EdgeLocation roadLoc : ownedRoads) {
			for (VertexLocation node : roadLoc.getVertices()) {
				int count = 0;
				for (EdgeLocation edge : node.getEdges()) {
					if (edge.getDistanceFromCenter() > radius) continue;
					
					Road road = getRoadAt(edge);
					if (road == null) continue;
					
					if (road.getOwner().equals(player)) {
						++count;
					}
				}
				nodesByEdgeCounts[count].add(node);
			}
		}
		
		
		// This will be used later to significantly reduce combinatorial explosion.
		Set<EdgeLocation> unvisited = new HashSet<>(ownedRoads);
		
		for (int i : new int[]{1, 3, 2}) {
			Set<VertexLocation> nodes = nodesByEdgeCounts[i];
			for (VertexLocation node : nodes) {
				List<EdgeLocation> path = dfsPath(node, ownedRoads, new ArrayList<EdgeLocation>(), player);
				
				if (path.size() > best) {
					best = path.size();
				}
				unvisited.removeAll(path);
			}
		}
		
		return best;
	}
	
	private List<EdgeLocation> dfsPath(VertexLocation node,
			Set<EdgeLocation> edges, List<EdgeLocation> visitedEdges,
			PlayerReference player) {
		// enemies cut up roads
		Municipality town = getMunicipalityAt(node);
		if (town != null &&
			!town.getOwner().equals(player)) {
			return visitedEdges; // you can't go farther
		}
		
		List<EdgeLocation> best = visitedEdges;
		for (EdgeLocation edge : node.getEdges()) {
			if (edges.contains(edge) && !visitedEdges.contains(edge)) {
				List<EdgeLocation> visited = new ArrayList<>(visitedEdges);
				visited.add(edge);
				List<EdgeLocation> path = dfsPath(node.traverse(edge), edges, visited, player);
				if (path.size() > best.size()) {
					best = path;
				}
			}
		}
		
		return best;
	}

	private Set<EdgeLocation> getRoadsOwnedByPlayer(
			PlayerReference player) {
		Set<EdgeLocation> result = new HashSet<>();
		
		for (Map.Entry<EdgeLocation, Road> roadEntry : roads.entrySet()) {
			if (roadEntry.getValue().getOwner().equals(player)) {
				result.add(roadEntry.getKey());
			}
		}
		
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hexes == null) ? 0 : hexes.hashCode());
		result = prime * result
				+ ((municipalities == null) ? 0 : municipalities.hashCode());
		result = prime * result + ((ports == null) ? 0 : ports.hashCode());
		result = prime * result + radius;
		result = prime * result + ((roads == null) ? 0 : roads.hashCode());
		result = prime * result + ((robber == null) ? 0 : robber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Board other = (Board) obj;
		if (hexes == null) {
			if (other.hexes != null)
				return false;
		} else if (!hexes.equals(other.hexes))
			return false;
		if (municipalities == null) {
			if (other.municipalities != null)
				return false;
		} else if (!municipalities.equals(other.municipalities))
			return false;
		if (ports == null) {
			if (other.ports != null)
				return false;
		} else if (!ports.equals(other.ports))
			return false;
		if (radius != other.radius)
			return false;
		if (roads == null) {
			if (other.roads != null)
				return false;
		} else if (!roads.equals(other.roads))
			return false;
		if (robber == null) {
			if (other.robber != null)
				return false;
		} else if (!robber.equals(other.robber))
			return false;
		return true;
	}

	public Collection<Municipality> getMunicipalitiesOwnedBy(PlayerReference player) {
		Collection<Municipality> result = new ArrayList<>();
		
		for (Municipality town : municipalities.values()) {
			if (player.equals(town.getOwner())) {
				result.add(town);
			}
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Board [hexes=");
		builder.append(getHexes());
		builder.append(", radius=");
		builder.append(radius);
		builder.append(", ports=");
		builder.append(getPorts());
		builder.append(", roads=");
		builder.append(getRoads());
		builder.append(", municipalities=");
		builder.append(getMunicipalities());
		builder.append(", robber=");
		builder.append(robber);
		builder.append("]");
		return builder.toString();
	}
	
	

}
