package client.devcards;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import shared.communication.IServer;
import shared.definitions.DevCardType;
import shared.definitions.ResourceType;
import shared.exceptions.ServerException;
import shared.exceptions.UserException;
import shared.model.DevCardList;
import shared.model.ModelFacade;
import shared.model.Player;
import client.base.*;
import client.misc.ClientManager;
import client.misc.IMessageView;
import client.resources.IResourceBarView;
import client.resources.ResourceBarElement;


/**
 * "Dev card" controller implementation
 */
public class DevCardController extends Controller implements IDevCardController {

	private IMessageView messageView;
	private IServer server = ClientManager.getServer();
	private ModelFacade model = ClientManager.getModel();
	private IBuyDevCardView buyCardView;
	private IAction soldierAction;
	private IAction roadAction;
	private IResourceBarView resourceBarView;
	
	/**
	 * DevCardController constructor
	 * 
	 * @param view "Play dev card" view
	 * @param buyCardView "Buy dev card" view
	 * @param soldierAction Action to be executed when the user plays a soldier card.  It calls "mapController.playSoldierCard()".
	 * @param roadAction Action to be executed when the user plays a road building card.  It calls "mapController.playRoadBuildingCard()".
	 */
	public DevCardController(IPlayDevCardView view, IBuyDevCardView buyCardView, 
								IAction soldierAction, IAction roadAction, IResourceBarView resourceBarView) {

		super(view);
		
		this.buyCardView = buyCardView;
		this.soldierAction = soldierAction;
		this.roadAction = roadAction;
		this.resourceBarView = resourceBarView;
	}

	public IPlayDevCardView getPlayCardView() {
		return (IPlayDevCardView)super.getView();
	}

	public IBuyDevCardView getBuyCardView() {
		return buyCardView;
	}

	@Override
	public void startBuyCard() {
		
		getBuyCardView().showModal();
	}

	@Override
	public void cancelBuyCard() {
		
		getBuyCardView().closeModal();
	}

	@Override
	public void buyCard() {
		
		// A little hack to make sure you can't buy 2 dev cards without 2 sets of resources if you're fast
		resourceBarView.setElementEnabled(ResourceBarElement.BUY_CARD, false);
		
		// Using a SwingWorker to make it feel responsive
		new SwingWorker<String, Object> () {

			@Override
			protected String doInBackground() throws Exception {
				int gameID = ClientManager.getModel().getGameHeader().getId();
				int index = ClientManager.getLocalPlayer().getIndex();
				
				UUID gameUUID = ClientManager.getModel().getGameHeader().getUUID();
				UUID playerUUID = ClientManager.getLocalPlayer().getPlayerUUID();
				return server.buyDevCard(playerUUID, gameUUID);
				
				//return server.buyDevCard(index, gameID);
			}
			
			@Override
			protected void done() {
				try {
					ClientManager.getModel().updateFromJSON(get());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					Throwable cause = e.getCause();
				
					if (cause instanceof ServerException){
						messageView.setTitle("Server Error");
						messageView.setMessage("Unable to reach server at this point");
						messageView.showModal();
					}
					else if (cause instanceof UserException) {
						messageView.setTitle("User Error");
						messageView.setMessage("Unable to complete action at this time)");
						messageView.showModal();
					}
				}
			}
			
		}.execute();
		
		getBuyCardView().closeModal();
	}

	@Override
	public void startPlayCard() {
		List<DevCardType> listOfDevCards = new ArrayList<DevCardType>();
		listOfDevCards.add(DevCardType.MONOPOLY);
		listOfDevCards.add(DevCardType.MONUMENT);
		listOfDevCards.add(DevCardType.ROAD_BUILD);
		listOfDevCards.add(DevCardType.SOLDIER);
		listOfDevCards.add(DevCardType.YEAR_OF_PLENTY);
		
		Player currentPlayer = model.getCatanModel().getPlayers().get(ClientManager.getLocalPlayer().getIndex());
		DevCardList oldCards = currentPlayer.getOldDevCards();
		DevCardList newCards = currentPlayer.getNewDevCards();

		for(DevCardType type : listOfDevCards){
			int oldCount = oldCards.count(type);
			int newCount = newCards.count(type);
			if(oldCount + newCount > 0){
				if(oldCount > 0){
					getPlayCardView().setCardEnabled(type, true);
				}
				else{
					getPlayCardView().setCardEnabled(type, false);
				}
				getPlayCardView().setCardAmount(type, oldCount + newCount);
			}
			else{
				getPlayCardView().setCardEnabled(type, false);
				getPlayCardView().setCardAmount(type, 0);
			}
		}
		
		getPlayCardView().showModal();
	}

	@Override
	public void cancelPlayCard() {

		getPlayCardView().closeModal();
	}

	@Override
	public void playMonopolyCard(ResourceType resource) {
		try{
			int gameID = ClientManager.getModel().getGameHeader().getId();
			int index = ClientManager.getLocalPlayer().getIndex();
			
			UUID playerUUID = ClientManager.getLocalPlayer().getPlayerUUID();
			UUID gameUUID = ClientManager.getModel().getGameHeader().getUUID();
			server.monopoly(playerUUID, gameUUID, resource);
			
			//server.monopoly(index, gameID, resource);
		}
		catch (ServerException e){
			messageView.setTitle("Server Error");
			messageView.setMessage("Unable to reach server at this point");
			messageView.showModal();
			return;
		}
		catch (UserException e) {
			messageView.setTitle("User Error");
			messageView.setMessage("Unable to complete action at this time)");
			messageView.showModal();
			return;
		}
		getPlayCardView().closeModal();
	}

	@Override
	public void playMonumentCard() {
		try{
			int gameID = ClientManager.getModel().getGameHeader().getId();
			int index = ClientManager.getLocalPlayer().getIndex();
			
			UUID playerUUID = ClientManager.getLocalPlayer().getPlayerUUID();
			UUID gameUUID = ClientManager.getModel().getGameHeader().getUUID();
			server.monument(playerUUID, gameUUID);
			
			//server.monument(index, gameID);
		}
		catch(ServerException e){
			messageView.setTitle("Server Error");
			messageView.setMessage("Unable to reach server at this point");
			messageView.showModal();
			return;
		}
		catch (UserException e) {
			messageView.setTitle("User Error");
			messageView.setMessage("Unable to complete action at this time)");
			messageView.showModal();
			return;
		}
		getPlayCardView().closeModal();
	}

	@Override
	public void playRoadBuildCard() {
		
		getPlayCardView().closeModal();
		roadAction.execute();
	}

	@Override
	public void playSoldierCard() {

		getPlayCardView().closeModal();
		soldierAction.execute();
	}

	@Override
	public void playYearOfPlentyCard(ResourceType resource1, ResourceType resource2) {
		try{
			int gameID = ClientManager.getModel().getGameHeader().getId();
			int index = ClientManager.getLocalPlayer().getIndex();
			
			UUID playerUUID = ClientManager.getLocalPlayer().getPlayerUUID();
			UUID gameUUID = ClientManager.getModel().getGameHeader().getUUID();
			server.yearOfPlenty(playerUUID, gameUUID, resource1, resource2);
			
			//server.yearOfPlenty(index, gameID, resource1, resource2);
		}
		catch(ServerException e){
			messageView.setTitle("Server Error");
			messageView.setMessage("Unable to reach server at this point");
			messageView.showModal();
			return;
		}
		catch (UserException e) {
			messageView.setTitle("User Error");
			messageView.setMessage("Unable to complete action at this time)");
			messageView.showModal();
			return;
		}
		getPlayCardView().closeModal();
	}
	
}
