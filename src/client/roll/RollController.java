package client.roll;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import shared.definitions.TurnStatus;
import shared.model.TurnTracker;
import client.base.*;
import client.misc.ClientManager;

/**
 * Implementation for the roll controller
 */
public class RollController extends Controller implements IRollController {

	private IRollResultView resultView;

	/**
	 * RollController constructor
	 * 
	 * @param view Roll view
	 * @param resultView Roll result view
	 */
	public RollController(IRollView view, IRollResultView resultView) {

		super(view);
		
		setResultView(resultView);
	}
	
	
	@Override
	public void turnTrackerChanged(TurnTracker turnTracker) {
		if (turnTracker.getCurrentPlayer().equals(ClientManager.getLocalPlayer())
				&& turnTracker.getStatus() == TurnStatus.Rolling) {
			System.out.println("Showing Roll View...");
			getRollView().closeModal();
			
			getRollView().showModal();
		}
		else {
			getRollView().closeModal();
		}
	}

	public IRollResultView getResultView() {
		return resultView;
	}
	public void setResultView(IRollResultView resultView) {
		this.resultView = resultView;
	}

	public IRollView getRollView() {
		return (IRollView)getView();
	}
	
	@Override
	public void rollDice() {
		Random rand = new Random();
		final int result = rand.nextInt(6) + rand.nextInt(6) + 2;
		getResultView().setRollValue(result);
		getResultView().showModal();
		
		new SwingWorker<String, Object> () {

			@Override
			protected String doInBackground() throws Exception {
				int gameID = ClientManager.getModel().getGameHeader().getId();
				
				UUID playerUUID = ClientManager.getLocalPlayer().getPlayerUUID();
				UUID gameUUID = ClientManager.getModel().getGameHeader().getUUID();
				return ClientManager.getServer().rollDice(playerUUID, gameUUID, result);
				
//				return ClientManager.getServer().rollDice(ClientManager.getLocalPlayer().getIndex(), gameID, result);
			}
			
			@Override
			protected void done() {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						try {
							getRollView().closeModal();
							getResultView().showModal();
							ClientManager.getModel().updateFromJSON(get());
						} catch (InterruptedException | ExecutionException e) {
							e.printStackTrace();
						}
					}
					
				});
			}
			
		}.execute();
	}

}

