package client.map;

import shared.exceptions.InvalidActionException;
import shared.locations.HexLocation;
import client.misc.ClientManager;

public class SoldierMoveState extends MapControllerState {

	public SoldierMoveState(MapController controller) {
		super(controller);
	}

	@Override
	public MapControllerState placeRobber(HexLocation hex)
			throws InvalidActionException {
		getController().robDialog(hex);
		return new RobPlayerState(this, hex);
	}

	@Override
	public boolean canMoveRobber(HexLocation loc) {
		return ClientManager.getModel().canMoveRobberTo(loc);
	}

	@Override
	public MapControllerState cancelMove() throws InvalidActionException {
		return new YourTurnState(getController());
	}

}
