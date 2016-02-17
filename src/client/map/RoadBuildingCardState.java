package client.map;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import client.misc.ClientManager;
import shared.definitions.PieceType;
import shared.exceptions.InvalidActionException;
import shared.locations.EdgeLocation;

public class RoadBuildingCardState extends MapControllerState {
	
	private EdgeLocation firstRoad = null;

	public RoadBuildingCardState(MapController controller) {
		super(controller);
	}

	@Override
	public MapControllerState placeRoad(final EdgeLocation edge)
			throws InvalidActionException {
		if (firstRoad == null) {
			firstRoad = edge;
			getView().placeRoad(edge, getYourColor());
			getView().startDrop(PieceType.ROAD, getYourColor(), true);
			return this;
		}
		else {
			getView().placeRoad(edge, getYourColor());
			
			new SwingWorker<String, Object> () {

				@Override
				protected String doInBackground() throws Exception {
					UUID gameUUID = ClientManager.getModel().getGameHeader().getUUID();
					return getServer().roadBuilding(getYourself().getPlayerUUID(), gameUUID, firstRoad, edge);
				}

				@Override
				protected void done() {
					try {
						getModel().updateFromJSON(get());
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
				}
				
			}.execute();
			// If this were in a factory class, I would be creating a worker and
			// immediately executing it. lol. The implications.
			
			return new YourTurnState(getController());
		}
	}

	@Override
	public MapControllerState cancelMove() throws InvalidActionException {
		if (firstRoad == null) {
			return new YourTurnState(getController());
		}
		else {
			firstRoad = null;
			// Refresh to previous state to "delete" piece
			getController().refreshPieces();
			// Bring up the road placement overlay for the previous piece
			getView().startDrop(PieceType.ROAD, getYourColor(), true);
			return this;
		}
	}

	@Override
	public boolean canPlaceRoad(EdgeLocation loc) {
		if (firstRoad == null) {
			return getModel().canBuildRoad(loc);
		}
		else {
			return getModel().canBuild2Roads(firstRoad, loc);
		}
	}

}
