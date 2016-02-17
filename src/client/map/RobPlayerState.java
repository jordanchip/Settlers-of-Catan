package client.map;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import shared.exceptions.InvalidActionException;
import shared.locations.HexLocation;
import shared.model.PlayerReference;
import client.data.RobPlayerInfo;
import client.misc.ClientManager;

public class RobPlayerState extends MapControllerState {

	private HexLocation newRobberLoc;
	private MapControllerState previous;

	public RobPlayerState(MapController controller) {
		super(controller);
		assert false;
	}

	public RobPlayerState(MapControllerState previous, HexLocation hex) {
		super(previous.getController());

		newRobberLoc = hex;
		this.previous = previous;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see client.map.MapControllerState#robPlayer(client.data.RobPlayerInfo)
	 */
	@Override
	public MapControllerState robPlayer(final RobPlayerInfo victimInfo)
			throws InvalidActionException {
		new SwingWorker<String, Object>() {

			@Override
			protected String doInBackground() throws Exception {
			
				UUID victim =  null;
				
				if (victimInfo != null) {
					victim = new PlayerReference(ClientManager
						.getModel().getCatanModel(),
						victimInfo.getPlayerIndex()).getPlayerUUID();
				}

				UUID gameUUID = ClientManager.getModel().getGameHeader().getUUID();
				if (previous instanceof SoldierMoveState) {
					return ClientManager.getServer().soldier(getYourself().getPlayerUUID(), gameUUID,
							newRobberLoc, victim);
				} else if (previous instanceof MoveRobberState) {
					return ClientManager.getServer().robPlayer(getYourself().getPlayerUUID(), gameUUID,
							newRobberLoc, victim);
				} else return null;
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
		return previous;
	}

}
