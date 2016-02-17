package client.map;

import java.util.*;

import javax.swing.SwingUtilities;

import shared.communication.IServer;
import shared.definitions.*;
import shared.exceptions.InvalidActionException;
import shared.locations.*;
import shared.model.*;
import client.base.*;
import client.data.*;
import client.misc.ClientManager;

/**
 * Implementation for the map controller
 */
public class MapController extends Controller implements IMapController {

	private IRobView robView;

	private MapControllerState state;

	public MapController(IMapView view, IRobView robView) {
		// The superclass now registers this as a listener to the client's
		// instance of the model.
		super(view);

		setRobView(robView);

		initFromModel();

		// Default state until you can control things on the map.
		// Active before the game starts and when it isn't your turn.
		// It does NOTHING but throw exceptions and return false. Always.
		state = new NullState(this);
	}
	

	@Override
	public void winnerChanged(PlayerReference winner) {
		getView().resetBoard();
	}

	@Override
	public void mapChanged(Board newMap) {

		System.out.println("MapController: updating map");
		
		getView().resetBoard();
		buildBoard(newMap);

		// Assume (for now) that only pieces will change
		// MUAHAHAHA I WILL DESTROY THIS ASSUMPTION!!!!
		refreshPieces();
	}

	@Override
	public void mapInitialized() {

		System.out.println("MapController: initializing map from server data");

		getView().resetBoard();
		this.initFromModel();
	}

	@Override
	public void turnTrackerChanged(final TurnTracker turnTracker) {
		// A nasty hack to keep this from interfering with the discard controller.
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				System.out.println("MapController: TurnTracker has changed");
				// System.out.println("Local Player: " +
				// ClientManager.getLocalPlayer().getPlayer().getName()
				// + " (" + ClientManager.getLocalPlayer() + ")");
				// System.out.println("Current Player: " +
				// turnTracker.getCurrentPlayer().getPlayer().getName()
				// + " (" + turnTracker.getCurrentPlayer() + ")");
				System.out.println("Phase: " + turnTracker.getStatus());

				if (turnTracker.getCurrentPlayer().equals(
						ClientManager.getLocalPlayer())
						&& getModel().getCatanModel().getPlayers().size() >= 4) {
					System.out.println("It's your turn!");
					state = new YourTurnState(MapController.this);

					// TODO: logic to check if an overlay is already open.

					switch (turnTracker.getStatus()) {
					case FirstRound:
					case SecondRound:
						System.out.println("Setup Round!");
						startMove(PieceType.SETTLEMENT, true, true);
						break;
					case Robbing:
						// This should occur only if you roll a 7...
						startMove(PieceType.ROBBER, false, false);
						break;
					case Discarding:
						// You can't interact with the map when you need to
						// discard
						state = new NullState(MapController.this);
					default:
						break;
					}
				} else {
					System.out.println("It's not your turn!");
					state = new NullState(MapController.this);
				}
				System.out.println("Done...");
			}
		});
	}

	public IMapView getView() {

		return (IMapView) super.getView();
	}

	private IRobView getRobView() {
		return robView;
	}

	private void setRobView(IRobView robView) {
		this.robView = robView;
	}

	public IServer getServer() {
		return ClientManager.getServer();
	}

	public CatanColor getYourColor() {
		return ClientManager.getLocalPlayer().getPlayer().getColor();
	}

	protected void initFromModel() {

		Board board = getModel().getCatanModel().getMap();
		if (board != null) {
			buildBoard(board);
			placePieces(board);
		}

	}

	private void buildBoard(Board board) {
		IMapView view = getView();

		for (HexLocation hexLoc : HexLocation.locationsWithinRadius(3)) {
			if (hexLoc.getDistanceFromCenter() > 2) {
				view.addHex(hexLoc, HexType.WATER);
			} else {
				Hex hex = board.getHexAt(hexLoc);
				HexType type = HexType.fromResourceType(hex.getResource());
				view.addHex(hex.getLocation(), type);
				if (type != HexType.DESERT) {
					view.addNumber(hex.getLocation(), hex.getNumber());
				}
			}
		}

		for (Port port : board.getPorts()) {
			view.addPort(port.getLocation(),
					PortType.fromResourceType(port.getResource()));
		}
	}

	private void placePieces(Board board) {
		IMapView view = getView();

		for (Road road : board.getRoads()) {
			CatanColor color = road.getOwner().getPlayer().getColor();
			view.placeRoad(road.getLocation(), color);
		}

		for (Municipality town : board.getMunicipalities()) {
			CatanColor color = town.getOwner().getPlayer().getColor();
			switch (town.getType()) {
			case SETTLEMENT:
				view.placeSettlement(town.getLocation(), color);
				break;
			case CITY:
				view.placeCity(town.getLocation(), color);
				break;
			default:
				break;
			}
		}

		view.placeRobber(board.getRobberLocation());
	}

	public boolean canPlaceRoad(EdgeLocation edgeLoc) {
		return state.canPlaceRoad(edgeLoc);
	}

	public boolean canPlaceSettlement(VertexLocation vertLoc) {
		return state.canPlaceSettlement(vertLoc);
	}

	public boolean canPlaceCity(VertexLocation vertLoc) {
		return state.canPlaceCity(vertLoc);
	}

	public boolean canPlaceRobber(HexLocation hexLoc) {
		return state.canMoveRobber(hexLoc);
	}

	public void placeRoad(EdgeLocation edgeLoc) {

		try {
			state = state.placeRoad(edgeLoc);
		} catch (InvalidActionException e) {
			System.out.println(e);
		}
	}

	public void placeSettlement(VertexLocation vertLoc) {

		try {
			state = state.placeSettlement(vertLoc);
		} catch (InvalidActionException e) {
			System.out.println(e);

		}
	}

	public void placeCity(VertexLocation vertLoc) {
		try {
			state = state.placeCity(vertLoc);
		} catch (InvalidActionException e) {
			System.out.println(e);

		}
	}

	public void placeRobber(HexLocation hexLoc) {
		try {
			state = state.placeRobber(hexLoc);
		} catch (InvalidActionException e) {
			System.out.println(e);
		}
	}

	public void startMove(PieceType pieceType, boolean isFree,
			boolean allowDisconnected) {
		try {
			state = state.startMove(pieceType, isFree, allowDisconnected);
		} catch (InvalidActionException e) {
			System.out.println(e);
		}
	}

	public void cancelMove() {
		try {
			state = state.cancelMove();
		} catch (InvalidActionException e) {
			System.out.println(e);

		}
	}

	public void playSoldierCard() {
		try {
			state = state.playSoldierCard();
		} catch (InvalidActionException e) {
			System.out.println(e);

		}
	}

	public void playRoadBuildingCard() {
		try {
			state = state.playRoadBuildingCard();
		} catch (InvalidActionException e) {
			System.out.println(e);

		}
	}

	public void robPlayer(RobPlayerInfo victim) {
		try {
			state = state.robPlayer(victim);
		} catch (InvalidActionException e) {
			System.out.println(e);

		}
	}

	public ModelFacade getModel() {
		return ClientManager.getModel();
	}

	public PlayerReference getYourself() {
		return ClientManager.getLocalPlayer();
	}

	public void robDialog(HexLocation hex) {
		IRobView view = getRobView();

		// Get the victim list
		Collection<Municipality> adjacentTowns = ClientManager.getModel()
				.getMunicipalitiesAround(hex);
		List<RobPlayerInfo> victims = new ArrayList<>();
		boolean[] playerIndicesUsed = { false, false, false, false };
		for (Municipality town : adjacentTowns) {
			PlayerReference ownerRef = town.getOwner();
			// Don't put yourself on the list; it makes no sense to rob yourself.
			if (ownerRef.equals(ClientManager.getLocalPlayer())) continue;
			
			if (!playerIndicesUsed[ownerRef.getIndex()]) {
				Player owner = ownerRef.getPlayer();

				RobPlayerInfo victim = new RobPlayerInfo();
				victim.setName(owner.getName());
				victim.setUUID(owner.getUUID());
				victim.setColor(owner.getColor());
				victim.setPlayerIndex(ownerRef.getIndex());
				victim.setNumCards(owner.getResources().count());
				victims.add(victim);

				playerIndicesUsed[ownerRef.getIndex()] = true;
			}
		}
		// I still don't understand why Java doesn't have a better way to
		// convert lists to arrays.
		RobPlayerInfo[] candidateVictims = victims
				.toArray(new RobPlayerInfo[victims.size()]);

		view.setPlayers(candidateVictims);
		view.showModal();
	}

	public void refreshPieces() {
		getView().removeAllPieces();

		placePieces(getModel().getCatanModel().getMap());
	}

}
