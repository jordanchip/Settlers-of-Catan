package shared.model;

import java.util.List;

public abstract class AbstractModelListener implements IModelListener {

	public void mapInitialized() {}
	@Override
	public void mapChanged(Board newMap) {}
	@Override
	public void playersChanged(List<Player> players) {}
	@Override
	public void bankChanged(Bank otherBank) {}
	@Override
	public void turnTrackerChanged(TurnTracker otherTurnTracker) {}
	@Override
	public void largestArmyChanged(PlayerReference otherPlayer) {}
	@Override
	public void longestRoadChanged(PlayerReference otherPlayer) {}
	@Override
	public void tradeOfferChanged(TradeOffer otherOffer) {}
	@Override
	public void chatChanged(MessageList otherChat) {}
	@Override
	public void winnerChanged(PlayerReference winner) {}
	@Override
	public void logChanged(MessageList otherLog) {}
	@Override
	public void gameFinished() {}
	
	
}
