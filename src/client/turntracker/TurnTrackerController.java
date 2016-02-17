package client.turntracker;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import shared.definitions.CatanColor;
import shared.model.Player;
import shared.model.PlayerReference;
import shared.model.TurnTracker;
import client.base.*;
import client.misc.ClientManager;

/**
 * Implementation for the turn tracker controller
 */
public class TurnTrackerController extends Controller implements ITurnTrackerController {
	
	boolean initialized = false;

	public TurnTrackerController(ITurnTrackerView view) {
		
		super(view);
		
		initFromModel();
	}
	
	@Override
	public ITurnTrackerView getView() {
		
		return (ITurnTrackerView)super.getView();
	}

	@Override
	public void endTurn() {
		new SwingWorker<String, Object> () {

			@Override
			protected String doInBackground() throws Exception {				
				UUID playerUUID = ClientManager.getLocalPlayer().getPlayerUUID();
				UUID gameUUID = ClientManager.getModel().getGameHeader().getUUID();
				return ClientManager.getServer().finishTurn(playerUUID, gameUUID);
			}

			@Override
			protected void done() {
				try {
					ClientManager.getModel().updateFromJSON(get());
				}
				catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		}.execute();
	}
	
	private void initFromModel() {
		if (ClientManager.getLocalPlayer() != null && ClientManager.getLocalPlayer().getPlayer() != null) {
			getView().setLocalPlayerColor(ClientManager.getLocalPlayer().getPlayer().getColor());
		}
		else {
			getView().setLocalPlayerColor(CatanColor.WHITE);
		}
	}
	
	@Override
	public void playersChanged(List<Player> players) {
		
		//If the game is beginning, setup the local player.
		if (!initialized ||
				ClientManager.getModel().getVersion() <= 0) {
			int i = 0;
			for (Player player : players) {
				if (ClientManager.getLocalPlayer().getPlayer().equals(player)) {
					getView().setLocalPlayerColor(player.getColor());
				}
				getView().initializePlayer(i, player.getName(), player.getColor());
				
				//We have all the players, so no need to do this anymore
				if (i >= 3)
					initialized = true;
				i++;
			}
			return;
		}
		else {
			updatePlayerScoreView(ClientManager.getModel().getCurrentPlayer());
		}
	}
	
	@Override
	public void turnTrackerChanged(TurnTracker turnTracker) {
		
		Player player = turnTracker.getCurrentPlayer().getPlayer();

		updatePlayerScoreView(turnTracker.getCurrentPlayer());
		
		if (!player.equals(ClientManager.getLocalPlayer().getPlayer())) {
			getView().updateGameState("It is currently " + player.getColor() + "'s turn", false);
			return;
		}
		switch (turnTracker.getStatus()) {
		case FirstRound:
			getView().updateGameState("First Round", false);
			break;
		case SecondRound:
			getView().updateGameState("Second Round", false);
			break;
		case Rolling:
			getView().updateGameState("Rolling", false);
			break;
		case Discarding:
			getView().updateGameState("Discarding", false);
			break;
		case Playing:
			getView().updateGameState("Click to end your turn", true);
			break;
		default:
			break;
		}
	}
	
	private void updatePlayerScoreView(PlayerReference currentPlayer) {

		PlayerReference armyRef = ClientManager.getModel().getCatanModel().getLargestArmy();
		PlayerReference roadRef = ClientManager.getModel().getCatanModel().getLongestRoad();
		
		System.out.println("Longest Road: " + roadRef);
			
		for (Player player : ClientManager.getModel().getCatanModel().getPlayers()) {		
			boolean largestArmy = player.getReference().equals(armyRef);
			boolean longestRoad = player.getReference().equals(roadRef);
			boolean isTurn = player.getReference().equals(currentPlayer);
			
			System.out.println(player.getName() + (longestRoad? " has ": " does not have ") + "the longest road");

			getView().updatePlayer(player.getPlayerIndex(), player.getVictoryPoints(), isTurn, largestArmy, longestRoad);
		}
	}

}

