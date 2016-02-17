package client.map;

import shared.definitions.PieceType;
import shared.exceptions.InvalidActionException;
import shared.locations.VertexLocation;

public class BuildStartingSettlementState extends MapControllerState {

	public BuildStartingSettlementState(MapController controller) {
		super(controller);
	}

	@Override
	public boolean canPlaceSettlement(VertexLocation loc) {
		try {
			return getModel().canBuildStartingSettlement(loc);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public MapControllerState placeSettlement(VertexLocation vertex)
			throws InvalidActionException {
		getView().placeSettlement(vertex, getYourColor());
		getView().startDrop(PieceType.ROAD, getYourColor(), true);
		return new BuildStartingRoadState(getController(), vertex);
	}

}
