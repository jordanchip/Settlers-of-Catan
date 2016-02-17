package client.map;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import client.misc.ClientManager;
import shared.exceptions.InvalidActionException;
import shared.locations.VertexLocation;

public class BuildCityState extends MapControllerState {

	public BuildCityState(MapController controller) {
		super(controller);
	}

	@Override
	public boolean canPlaceCity(VertexLocation loc) {
		return ClientManager.getModel().canBuildCity(loc);
	}

	@Override
	public MapControllerState placeCity(final VertexLocation vertex)
			throws InvalidActionException {
		
		getView().placeCity(vertex, getYourColor());
		
		new SwingWorker<String, Object>() {

			@Override
			protected String doInBackground() throws Exception {
				UUID gameUUID = ClientManager.getModel().getGameHeader().getUUID();
				return ClientManager.getServer().buildCity(getYourself().getPlayerUUID(), gameUUID, vertex);
			}

			@Override
			protected void done() {
				try {
					ClientManager.getModel().updateFromJSON(get());
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

}