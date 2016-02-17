package client.points;

import java.util.List;

import client.base.*;
import client.misc.ClientManager;
import shared.model.Player;
import shared.model.PlayerReference;


/**
 * Implementation for the points controller
 */
public class PointsController extends Controller implements IPointsController {

	private IGameFinishedView finishedView;
	
	/**
	 * PointsController constructor
	 * 
	 * @param view Points view
	 * @param finishedView Game finished view, which is displayed when the game is over
	 */
	public PointsController(IPointsView view, IGameFinishedView finishedView) {
		
		super(view);
		
		setFinishedView(finishedView);
		
		initFromModel();
	}
	
	public IPointsView getPointsView() {
		
		return (IPointsView)super.getView();
	}
	
	public IGameFinishedView getFinishedView() {
		return finishedView;
	}
	public void setFinishedView(IGameFinishedView finishedView) {
		this.finishedView = finishedView;
	}

	/**
	 * getPointsView().setPoints( the amount of points for this player in the model);
	 */
	private void initFromModel() {
		//<temp>
		getPointsView().setPoints(5);
		//</temp>
	}
	
	
	public void pointsChanged(int points) {
		getPointsView().setPoints(points);
	}
	
	@Override
	public void playersChanged(List<Player> players) {
		for (Player player : players) {
			if (player.getPlayerID() == (ClientManager.getLocalPlayer().getPlayer().getPlayerID())) {
				pointsChanged(player.getVictoryPoints());
				break;
			}
		}
	}
	
	@Override
	public void winnerChanged(PlayerReference winner) {
		if (winner == null) return;
		
		Player winningPlayer = null;
		for (Player player : ClientManager.getModel().getCatanModel().getPlayers()) {
			if (player.getReference().equals(winner)) {
				winningPlayer = player;
				break;
			}
		}
		
		if (winningPlayer == null) return;
		
		String winnerName = winningPlayer.getName();
		PlayerReference localPlayerRef = ClientManager.getLocalPlayer();
		
		if (localPlayerRef.equals(winner)) {
			getFinishedView().setWinner(winnerName, true);
		}
		else {
			getFinishedView().setWinner(winnerName, false);
		}
		getFinishedView().showModal();
	}
	
	@Override
	public void gameFinishedFromView() {
		ClientManager.getModel().notifyGameFinished();
	}
}