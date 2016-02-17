package client.map;

import client.misc.ClientManager;
import shared.exceptions.InvalidActionException;
import shared.locations.HexLocation;

public class MoveRobberState extends MapControllerState {
	

	public MoveRobberState(MapController controller) {
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
	
}
