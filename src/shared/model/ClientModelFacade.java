package shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.*;

import shared.exceptions.GameInitializationException;
import client.communication.ServerPoller;
import client.data.GameInfo;


public class ClientModelFacade extends ModelFacade 
	implements Serializable {
	
	private static final long serialVersionUID = 4740843052612045852L;

	static final Logger log = Logger.getLogger( ClientModelFacade.class.getName() );
	
	private transient ServerPoller poller;
	
	public ClientModelFacade() throws GameInitializationException {
		//when does any of this get initialized?
		this(new CatanModel(false, false, false));
	}
	
	public ClientModelFacade(CatanModel startingModel) {
		super(startingModel);
		
		listeners = new ArrayList<>();
	}
	
	public void setGameInfo(GameInfo header) {
		model.setHeader(header);
	}
	
	public GameInfo getGameInfo() {
		return model.getGameInfo();
	}
	
	// It's implemented a little differently client-side
	/*This function might need to be changed.
	*/
	@Override
	public synchronized void rollDice(PlayerReference player) {
		Player currentPlayer = getCurrentPlayer().getPlayer();
		
		currentPlayer.setHasRolled(true);
	}
	
	public void notifyGameFinished() {
		if (poller.isRunning()) {
			poller.stop();
		}
		try {
			this.model = new CatanModel(false, false, false);
		} catch (GameInitializationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (IModelListener listener : listeners) {
			try {
				listener.gameFinished();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setPoller(ServerPoller poller) {
		this.poller = poller;
		
	}
}
