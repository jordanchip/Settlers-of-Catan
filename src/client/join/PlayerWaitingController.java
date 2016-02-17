package client.join;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.SwingWorker;

import server.ai.AIType;
import shared.communication.IServer;
import shared.exceptions.*;
import shared.model.ModelFacade;
import shared.model.Player;
import client.base.*;
import client.misc.ClientManager;
import client.data.PlayerInfo;


/**
 * Implementation for the player waiting controller
 */
public class PlayerWaitingController extends Controller implements IPlayerWaitingController {

	//private ModelFacade modelFacade = ModelFacade.getInstance();
	private ModelFacade modelFacade = ClientManager.getModel();
	//private IServer serverProxy = ServerProxy.getInstance();
	private IServer serverProxy = ClientManager.getServer();
	
	public PlayerWaitingController(IPlayerWaitingView view) {

		super(view);
	}

	@Override
	public IPlayerWaitingView getView() {

		return (IPlayerWaitingView)super.getView();
	}
	
	public void setFacade(ModelFacade facade) {
		this.modelFacade = facade;
	}

	
	/**
	 * make a list of players from the current game
	 * getView().setPlayers(that list)
	 * getView().setAIChoices(if you happen to have AI)
	 * showModal
	 */
	@Override
	public void start() {
		assert modelFacade ==  ClientManager.getModel();
		
		List<PlayerInfo> players = null;
		if (modelFacade.getCatanModel().getGameInfo() != null)
			players = modelFacade.getCatanModel().getGameInfo().getPlayers();
		else {
			players = new ArrayList<PlayerInfo>();
		}
		if (players.size() >= 4) {
			return;
		}
		List<String> AIChoiceList;
		try {
			AIChoiceList = ClientManager.getServer().getAITypes();
			
			PlayerInfo[] playerList = new PlayerInfo[players.size()];
			for(int i = 0; i < players.size(); i++)
				playerList[i] = players.get(i);
			
			String[] AIChoices = new String[AIChoiceList.size()];
			for(int i = 0; i < AIChoiceList.size(); i++)
				AIChoices[i] = AIChoiceList.get(i);
			
			getView().setPlayers(playerList);
			
			getView().setAIChoices(AIChoices);
			getView().closeModal();
			getView().showModal();
			
			if(playerList.length > 3)
				getView().closeModal();
		}
		catch (ServerException | UserException e) {
			e.printStackTrace();
		}
			
	}

	
	/**
	 * Do whatever you need to do to generate an AI and and it to the player list;
	 */
	@Override
	public void addAI() {
		
		String AITypeName = getView().getSelectedAI();
		
		final AIType aitype = AIType.getTypeFromString(AITypeName);
		
		new SwingWorker<Object, Object> () {

			@Override
			protected Object doInBackground() throws Exception {
				
				UUID gameUUID = ClientManager.getModel().getGameHeader().getUUID();
				serverProxy.addAIPlayer(gameUUID, aitype);
				return null;
			}
			
		}.execute();
	
	}

	@Override
	public void playersChanged(List<Player> players) {
		if (players.size() >= 4) {
			if(getView().isModalShowing()) {
				getView().closeModal();
			}
			return;
		}
		PlayerInfo[] playerList = new PlayerInfo[players.size()];
		for(int i = 0; i < players.size(); i++)
			playerList[i] = new PlayerInfo(players.get(i));
		
		getView().closeModal();
		getView().setPlayers(playerList);
		getView().showModal();
		
		
		if(players.size() > 3)
			getView().closeModal();
	}
}