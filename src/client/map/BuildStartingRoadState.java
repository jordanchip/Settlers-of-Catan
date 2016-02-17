package client.map;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import client.misc.ClientManager;
import shared.definitions.PieceType;
import shared.exceptions.InvalidActionException;
import shared.locations.EdgeLocation;
import shared.locations.VertexLocation;

public class BuildStartingRoadState extends MapControllerState {
	
	VertexLocation settlement;

	public BuildStartingRoadState(MapController controller) {
		super(controller);
	}
	
	public BuildStartingRoadState(MapController controller, VertexLocation vertex) {
		super(controller);
		settlement = vertex;
	}

	@Override
	public MapControllerState placeRoad(final EdgeLocation edge)
			throws InvalidActionException {
		
		getView().placeRoad(edge, getYourColor());
		
		new SwingWorker<String, Object> () {

			@Override
			protected String doInBackground() throws Exception {
				UUID gameUUID = ClientManager.getModel().getGameHeader().getUUID();
				//getServer().buildSettlement(getYourself().getPlayerUUID(), gameUUID, settlement, true);
				//getServer().buildRoad(getYourself().getPlayerUUID(), gameUUID, edge, true);
				return getServer().buildStartingPieces(getYourself().getPlayerUUID(), gameUUID, settlement, edge);
			}

			@Override
			protected void done() {
				try {
					getModel().updateFromJSON(get());
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.execute();
		
		return new NullState(getController());
	}

	@Override
	public MapControllerState cancelMove() throws InvalidActionException {
		getController().refreshPieces();
		getView().startDrop(PieceType.SETTLEMENT, getYourColor(), false);
		return new BuildStartingSettlementState(getController());
	}

	@Override
	public boolean canPlaceRoad(EdgeLocation loc) {
		try {
			return getModel().canBuildStartingPieces(settlement, loc);
		} catch (Exception e) {
			return false;
		}
	}

}
