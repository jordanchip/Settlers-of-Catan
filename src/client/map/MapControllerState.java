package client.map;

import client.data.RobPlayerInfo;
import shared.communication.IServer;
import shared.definitions.CatanColor;
import shared.definitions.PieceType;
import shared.exceptions.InvalidActionException;
import shared.locations.*;
import shared.model.ModelFacade;
import shared.model.PlayerReference;

public abstract class MapControllerState {
	
	private MapController controller;
	
	public MapControllerState(MapController controller) {
		this.controller = controller;
		System.out.println("New State: " + this.getClass().getSimpleName());
	}

	protected MapController getController() {
		return controller;
	}
	
	protected IMapView getView() {
		return controller.getView();
	}
	
	protected ModelFacade getModel() {
		return controller.getModel();
	}
	
	protected PlayerReference getYourself() {
		return controller.getYourself();
	}

	protected CatanColor getYourColor() {
		return controller.getYourColor();
	}

	protected IServer getServer() {
		return controller.getServer();
	}
	
	// NOTE on any implementations, make sure to do the server work in a SwingWorker thread.

	/** Places a road at the given location, if possible.
	 * @param edge
	 * @return the state to transition to after this runs
	 * @throws InvalidActionException
	 */
	public MapControllerState placeRoad(EdgeLocation edge) throws InvalidActionException {
		throw new InvalidActionException("You cannot build a road at this time.");
	}
	
	/**
	 * @param vertex
	 * @return the state to transition to after this runs
	 * @throws InvalidActionException
	 */
	public MapControllerState placeSettlement(VertexLocation vertex) throws InvalidActionException {
		throw new InvalidActionException("You cannot build a settlement at this time.");
	}
	
	/**
	 * @param vertex
	 * @return the state to transition to after this runs
	 * @throws InvalidActionException
	 */
	public MapControllerState placeCity(VertexLocation vertex) throws InvalidActionException {
		throw new InvalidActionException("You cannot build a city at this time.");
	}
	
	/**
	 * @param hex
	 * @return the state to transition to after this runs
	 * @throws InvalidActionException
	 */
	public MapControllerState placeRobber(HexLocation hex) throws InvalidActionException {
		throw new InvalidActionException("You cannot move the robber at this time.");
	}
	
	/**
	 * @return the state to transition to after this runs
	 * @throws InvalidActionException
	 */
	public MapControllerState playSoldierCard() throws InvalidActionException {	
		throw new InvalidActionException("You cannot play a soldier card at this time.");
	}
	
	/**
	 * @return the state to transition to after this runs
	 * @throws InvalidActionException
	 */
	public MapControllerState playRoadBuildingCard() throws InvalidActionException {	
		throw new InvalidActionException("You cannot play a road building card at this time.");
	}
	
	/**
	 * @param victim
	 * @return the state to transition to after this runs
	 * @throws InvalidActionException
	 */
	public MapControllerState robPlayer(RobPlayerInfo victim) throws InvalidActionException {	
		throw new InvalidActionException("You cannot rob at this time.");
	}
	
	/**
	 * @param pieceType
	 * @param isFree
	 * @param allowDisconnected
	 * @return the state to transition to after this runs
	 * @throws InvalidActionException
	 */
	public MapControllerState startMove(PieceType pieceType, boolean isFree, boolean allowDisconnected)
			throws InvalidActionException {
		throw new InvalidActionException("You cannot start a move at this time.");
	}

	/**
	 * @return the state to transition to after this runs
	 * @throws InvalidActionException
	 */
	public MapControllerState cancelMove()
			throws InvalidActionException {
		throw new InvalidActionException("There is nothing to cancel.");
	}
	
	public boolean canPlaceRoad(EdgeLocation loc) {
		return false;
	}
	
	public boolean canPlaceSettlement(VertexLocation loc) {
		return false;
	}
	
	public boolean canPlaceCity(VertexLocation loc) {
		return false;
	}
	
	public boolean canMoveRobber(HexLocation loc) {
		return false;
	}
}
