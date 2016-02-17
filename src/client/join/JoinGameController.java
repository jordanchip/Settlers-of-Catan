package client.join;

import java.util.List;
import java.util.UUID;

import shared.communication.GameHeader;
import shared.communication.IServer;
import shared.communication.PlayerHeader;
import shared.communication.Session;
import shared.definitions.CatanColor;
import shared.exceptions.GameInitializationException;
import shared.exceptions.JoinGameException;
import shared.exceptions.ServerException;
import shared.exceptions.UserException;
import shared.model.CatanModel;
import shared.model.ClientModelFacade;
import client.base.*;
import client.communication.DataConverter;
import client.communication.ServerPoller;
import client.data.*;
import client.misc.*;


/**
 * Implementation for the join game controller
 */
public class JoinGameController extends Controller implements IJoinGameController {

	private INewGameView newGameView;
	private ISelectColorView selectColorView;
	private IMessageView messageView;
	private IAction joinAction;
	private IServer serverProxy = ClientManager.getServer();
	private ClientModelFacade modelFacade = ClientManager.getModel();
	
	/**
	 * JoinGameController constructor
	 * 
	 * @param view Join game view
	 * @param newGameView New game view
	 * @param selectColorView Select color view
	 * @param messageView Message view (used to display error messages that occur while the user is joining a game)
	 */
	public JoinGameController(IJoinGameView view, INewGameView newGameView, 
								ISelectColorView selectColorView, IMessageView messageView) {

		super(view);

		assert modelFacade == ClientManager.getModel();

		setNewGameView(newGameView);
		setSelectColorView(selectColorView);
		setMessageView(messageView);
	}
	
	public IJoinGameView getJoinGameView() {
		
		return (IJoinGameView)super.getView();
	}
	
	/**
	 * Returns the action to be executed when the user joins a game
	 * 
	 * @return The action to be executed when the user joins a game
	 */
	public IAction getJoinAction() {
		
		return joinAction;
	}

	/**
	 * Sets the action to be executed when the user joins a game
	 * 
	 * @param value The action to be executed when the user joins a game
	 */
	public void setJoinAction(IAction value) {	
		
		joinAction = value;
	}
	
	public INewGameView getNewGameView() {
		
		return newGameView;
	}

	public void setNewGameView(INewGameView newGameView) {
		
		this.newGameView = newGameView;
	}
	
	public ISelectColorView getSelectColorView() {
		
		return selectColorView;
	}
	public void setSelectColorView(ISelectColorView selectColorView) {
		
		this.selectColorView = selectColorView;
	}
	
	public IMessageView getMessageView() {
		
		return messageView;
	}
	public void setMessageView(IMessageView messageView) {
		
		this.messageView = messageView;
	}

	/**
	 * get list of games from server, save them into your pre-game model
	 * JoinGameView().setGames(list of games, your player info)
	 * showModal
	 */
	@Override
	public void start() {
		try {
			List<GameHeader> headers = serverProxy.getGameList();
			
			GameInfo[] games = DataConverter.convertGameHeaderToGameInfo(headers);
			PlayerInfo localPlayer = new PlayerInfo();
			//localPlayer.setUUID(modelFacade.getLocalPlayer().getPlayerID());
			localPlayer.setUUID(ClientManager.getSession().getPlayerUUID());
			localPlayer.setName(ClientManager.getSession().getUsername());
			
			getJoinGameView().closeModal();
			getJoinGameView().setGames(games, localPlayer);
			getJoinGameView().showModal();
		}
		catch (ServerException | UserException e) {
			messageView.setTitle("Server Error");
			messageView.setMessage("Unable to reach server at this point");
			messageView.showModal();
		}
	}
	
	@Override
	public void gameFinished() {
		this.start();
	}

	@Override
	public void startCreateNewGame() {
		
		getNewGameView().showModal();
	}

	@Override
	public void cancelCreateNewGame() {
		
		getNewGameView().closeModal();
	}

	
	/**
	 * Create a new Game board based on the options in the View(Random or not)
	 * Send create game request to server
	 * update Game List
	 * closeModal
	 */
	@Override
	public void createNewGame() {
		String title = getNewGameView().getTitle();
		boolean randomTiles = getNewGameView().getRandomlyPlaceHexes();
		boolean randomNumbers = getNewGameView().getRandomlyPlaceNumbers();
		boolean randomPorts = getNewGameView().getUseRandomPorts();
		try {
			GameHeader thisGame = serverProxy.createGame(title, randomTiles, randomNumbers, randomPorts);
			modelFacade.setGameInfo(DataConverter.convertHeaderToInfo(thisGame));
			getNewGameView().closeModal();
			if (getJoinGameView().isModalShowing())  getJoinGameView().closeModal();
			List<PlayerHeader> players = thisGame.getPlayers();
			for (PlayerHeader player : players) {
				if (player.getUUID() == ClientManager.getLocalPlayer().getPlayerUUID()) {
					joinAction.execute();
					return;
				}
			}
			getSelectColorView().showModal();
		}
		catch (GameInitializationException e) {
			getMessageView().setTitle("Setup Error");
			getMessageView().setMessage("Could not initialize game.");
			getMessageView().showModal();
		}
		catch (UserException e) {
			getMessageView().setTitle("Invalid Action Error");
			getMessageView().setMessage("Invalid Action was performed.");
			getMessageView().showModal();
		}
		catch (ServerException e) {
			getMessageView().setTitle("Server Error");
			getMessageView().setMessage("Unable to connect to the server.");
			getMessageView().showModal();
		} 
	}

	
	/**
	 * Iterate through player in GameInfo and 
	 * disable each color that has already been used in ColorSelectView
	 * check if you are already in
	 * if so,call JoinGame with the color you had already picked
	 */
	@Override
	public void startJoinGame(GameInfo game) {
		
		modelFacade = ClientManager.getModel();
		//Updates the facade with all the relevant game header information.
		modelFacade.setGameInfo(game);
		
		for (PlayerInfo player : game.getPlayers()) {
//			if (!player.getUUID().equals(ClientManager.getSession().getPlayerUUID())) {
//				getSelectColorView().setColorEnabled(player.getColor(), false);
//			}
			if (!player.getName().equals(ClientManager.getSession().getUsername())) {
				getSelectColorView().setColorEnabled(player.getColor(), false);
			}
			else{
				
				joinGame(player.getColor());
				return;
			}
		}
		//Give the player a chance to select a color and join
		if (getJoinGameView().isModalShowing())  getJoinGameView().closeModal();
		getSelectColorView().showModal();
	}

	@Override
	public void cancelJoinGame() {
		getSelectColorView().closeModal();
		getJoinGameView().showModal();
	}

	
	/**
	 * call join game on server
	 */
	@Override
	public void joinGame(CatanColor color) {
		try{
			Session user = ClientManager.getSession();
			Session afterJoin = serverProxy.joinGame(user, modelFacade.getGameInfo().getUUID(), color);
			if (afterJoin != null) {
				ClientManager.setSession(afterJoin);
				if (getJoinGameView().isModalShowing()) getJoinGameView().closeModal();
				if (getSelectColorView().isModalShowing()) {
					getSelectColorView().closeModal();
				}
				List<GameHeader> gameHeaders = serverProxy.getGameList();
				GameHeader thisHeader = null;
				for(GameHeader game : gameHeaders){
					if(game.getUUID().equals(modelFacade.getGameInfo().getUUID())){
						thisHeader = game;
						break;
					}
				}
				modelFacade.setGameInfo(DataConverter.convertHeaderToInfo(thisHeader));
				
				UUID gameUUID = ClientManager.getModel().getGameHeader().getUUID();
				
				String model = ClientManager.getServer().getModel(gameUUID, -1);
				
				modelFacade.updateFromJSON(model);
			}
			if (ClientManager.getSession() != null) {
				ServerPoller poller = new ServerPoller(serverProxy,ClientManager.getSession());
				modelFacade.setPoller(poller);
				poller.start();
			}
			// If join succeeded
			joinAction.execute();
		}
		catch(ServerException e){
			messageView.setTitle("Server Error");
			messageView.setMessage("Unable to reach server at this point");
			messageView.showModal();			
		}
		catch (JoinGameException e) {
			messageView.setTitle("Join Game Error");
			messageView.setMessage("Unable to join game at this point");
			messageView.showModal();
		}
		catch (UserException e) {
			messageView.setTitle("User Error");
			messageView.setMessage("The User had an error");
			messageView.showModal();
		}
	}

}

