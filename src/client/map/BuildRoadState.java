package client.map;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import client.misc.ClientManager;
import shared.exceptions.InvalidActionException;
import shared.locations.EdgeLocation;

public class BuildRoadState extends MapControllerState {

	public BuildRoadState(MapController controller) {
		super(controller);
	}

	@Override
	public MapControllerState placeRoad(final EdgeLocation edge)
			throws InvalidActionException {
		new SwingWorker<String, Object>() {

			@Override
			protected String doInBackground() throws Exception {
				MapController controller = getController();
				UUID gameUUID = ClientManager.getModel().getGameHeader().getUUID();
				return controller.getServer().buildRoad(controller.getYourself().getPlayerUUID(), gameUUID, edge);
			}

			@Override
			protected void done() {
				try {
					getController().getModel().updateFromJSON(get());
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		}.execute();
		
		return new YourTurnState(getController());
	}
	
	@Override
	public MapControllerState cancelMove() throws InvalidActionException {
		return new YourTurnState(getController());
	}

	@Override
	public boolean canPlaceRoad(EdgeLocation loc) {
		return getController().getModel().canBuildRoad(loc);
	}	

}