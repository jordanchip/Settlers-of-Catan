package client.discard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONObject;

import shared.communication.IServer;
import shared.definitions.*;
import shared.exceptions.ServerException;
import shared.exceptions.UserException;
import shared.model.ModelFacade;
import shared.model.Player;
import shared.model.PlayerReference;
import shared.model.ResourceList;
import shared.model.TurnTracker;
import client.base.*;
import client.misc.*;


/**
 * Discard controller implementation
 */
public class DiscardController extends Controller implements IDiscardController {

	private IWaitView waitView;
	Map<ResourceType, Integer> cardsToBeDiscarded = initializecardsToBeDiscarded();
	private IServer serverProxy = ClientManager.getServer();
	private ModelFacade modelFacade = ClientManager.getModel();
	boolean maxedOut = false;

	/**
	 * DiscardController constructor
	 * 
	 * @param view View displayed to let the user select cards to discard
	 * @param waitView View displayed to notify the user that they are waiting for other players to discard
	 *disable discardbutton in the getDiscardView()
	 *
	 */
	public DiscardController(IDiscardView view, IWaitView waitView) {
		
		super(view);
		
		this.waitView = waitView;
		
	}

	public IDiscardView getDiscardView() {
		return (IDiscardView)super.getView();
	}
	
	public IWaitView getWaitView() {
		return waitView;
	}

	@Override
	public void turnTrackerChanged(TurnTracker turnTracker) {
		if (turnTracker.getStatus() == TurnStatus.Discarding) {
			System.out.println("Discarding...");
			
			UUID playerID = ClientManager.getLocalPlayer().getPlayerUUID();
			List<Player> players = modelFacade.getCatanModel().getPlayers();
			Player localPlayer = null;
			
			for(Player p : players) {
				if(p.getUUID().equals(playerID))
					localPlayer = p;
			}
			
			ResourceList hand = localPlayer.getResources();
			
			if(hand.count() > 7 && !localPlayer.hasDiscarded()) {
				
				if(!getDiscardView().isModalShowing()) {
					getDiscardView().showModal();
					setMaxAmountsInDiscardView();
				}
				
			}
			else {
				if(!getWaitView().isModalShowing())
					getWaitView().showModal();
			}
			
		}
		else {
			if(getDiscardView().isModalShowing())
				getDiscardView().closeModal();
			if(getWaitView().isModalShowing())
				getWaitView().closeModal();
		}
	}
	
	
	private void setMaxAmountsInDiscardView() {
		
		UUID playerID = ClientManager.getLocalPlayer().getPlayerUUID();
		List<Player> players = modelFacade.getCatanModel().getPlayers();
		Player localPlayer = null;
		
		for(Player p : players) 
			if(p.getUUID().equals(playerID))
				localPlayer = p;
		
		ResourceList hand = localPlayer.getResources();
		
		Map<ResourceType, Integer> resourceMap = hand.getResources();
		
		for(Map.Entry<ResourceType, Integer> entry : resourceMap.entrySet()) {
			
			getDiscardView().setResourceMaxAmount(entry.getKey(), entry.getValue());
			
			if(entry.getValue() > 0)
				getDiscardView().setResourceAmountChangeEnabled(entry.getKey(), true, false);
			
		}
		
		getDiscardView().setStateMessage("0/" + hand.count()/2);
		getDiscardView().setDiscardButtonEnabled(false);
	}
	
	/**
	 * add recource to a list to be discarded
	 * getDiscardView().setStateMessage(message); where message is 
	 * how many out of required amount have been selected
	 * enable discard button if enough cards (but not more) have been selected
	 */
	@Override
	public void increaseAmount(ResourceType resource) {
		
		Map<ResourceType, Integer> hand = getLocalHand();
		
		int temp = cardsToBeDiscarded.get(resource);
		temp++;
		cardsToBeDiscarded.put(resource, temp);
		
		int discardNum = getTotalCardsToDiscard();
		
		getDiscardView().setResourceDiscardAmount(resource, temp);
		
		getDiscardView().setStateMessage( discardCount() + "/" + discardNum);
		
		setEnables(resource, hand, temp);
		
		checkIfMaxedOut(resource, hand, discardNum);
	}


	private void setResourcesInViewToZero() {
		
		for (ResourceType type : ResourceType.values()) {
			
			getDiscardView().setResourceDiscardAmount(type, 0);
		}
	}


	private void checkIfMaxedOut(ResourceType resource, Map<ResourceType, Integer> hand, int maxCards) {
		
		if(discardCount() >= maxCards) {
			
			for(Map.Entry<ResourceType, Integer> entry : hand.entrySet()) {
				
				if(cardsToBeDiscarded.get(entry.getKey()) == 0)
					getDiscardView().setResourceAmountChangeEnabled(entry.getKey(), false, false);
				else
					getDiscardView().setResourceAmountChangeEnabled(entry.getKey(), false, true);
				
			}
			getDiscardView().setDiscardButtonEnabled(true);
			maxedOut = true;
		}
		
	}



	/**
	 * undoes things you did in increase function
	 */
	@Override
	public void decreaseAmount(ResourceType resource) {
		
		Map<ResourceType, Integer> hand = getLocalHand();
		
		int temp = cardsToBeDiscarded.get(resource);
		temp--;
		cardsToBeDiscarded.put(resource, temp);
		
		int discardNum = getTotalCardsToDiscard();
		
		getDiscardView().setResourceDiscardAmount(resource, temp);
		
		getDiscardView().setStateMessage( discardCount() + "/" + discardNum);
		
		setEnables(resource, hand, temp);
		
		checkIfWasMaxedOut(hand);
	}

	private void checkIfWasMaxedOut(Map<ResourceType, Integer> hand) {
		
		if(maxedOut) {
			
			for(Map.Entry<ResourceType, Integer> entry : cardsToBeDiscarded.entrySet()) {
				
				setEnables(entry.getKey(), hand, entry.getValue());
			}
			getDiscardView().setDiscardButtonEnabled(false);
			maxedOut = false;
		}
		
	}



	/**
	 * send request to server with a map of resource types and how many of each are discarded
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void discard() {
		try {
				
			PlayerReference localPlayer = ClientManager.getLocalPlayer();
			
			JSONObject discardMap = ResourcesToLowerCase(cardsToBeDiscarded);
			
			ResourceList cards = new ResourceList(discardMap);
			
			int gameID = ClientManager.getModel().getGameHeader().getId();
			int index = localPlayer.getIndex();
			
			UUID playerUUID = ClientManager.getLocalPlayer().getPlayerUUID();
			UUID gameUUID = ClientManager.getModel().getGameHeader().getUUID();
			serverProxy.discardCards(playerUUID, gameUUID, cards);
			
			//serverProxy.discardCards(index, gameID, cards);
			
			setResourcesInViewToZero();
			
			cardsToBeDiscarded = initializecardsToBeDiscarded();
			
			if(getDiscardView().isModalShowing())
				getDiscardView().closeModal();
			
			if(!getWaitView().isModalShowing())
				getWaitView().showModal();
			
			
		}
		catch (ServerException | UserException e) {
			e.printStackTrace();
		}
		
	}

	private Map<ResourceType, Integer> initializecardsToBeDiscarded() {
		
		Map<ResourceType, Integer> cardsToBeDiscarded = new HashMap<ResourceType, Integer>();
		
		cardsToBeDiscarded.put(ResourceType.BRICK, 0);
		cardsToBeDiscarded.put(ResourceType.WOOD, 0);
		cardsToBeDiscarded.put(ResourceType.SHEEP, 0);
		cardsToBeDiscarded.put(ResourceType.ORE, 0);
		cardsToBeDiscarded.put(ResourceType.WHEAT, 0);
		
		return cardsToBeDiscarded;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject ResourcesToLowerCase(Map<ResourceType, Integer> map) {
		
		JSONObject lowerCaseMap = new JSONObject();
		
		for(Map.Entry<ResourceType, Integer> entry : map.entrySet()) {
			
			String stringTemp = entry.getKey().toString().toLowerCase();
			int valueTemp = entry.getValue();
			
			lowerCaseMap.put(stringTemp, valueTemp);
		}
		
		return lowerCaseMap;
	}
	
	private int getTotalCardsToDiscard() {
		
		Player localPlayer = ClientManager.getLocalPlayer().getPlayer();
		
		ResourceList hand = localPlayer.getResources();
		
		int discardRequirement = hand.count()/2;
		return discardRequirement;
	}
	
	private int discardCount() {
		int total = 0;
		for (int count : cardsToBeDiscarded.values()) {
			total += count;
		}
		return total;
	}
	
	private Map<ResourceType, Integer> getLocalHand() {
		
		Player localPlayer = ClientManager.getLocalPlayer().getPlayer();
		
		ResourceList hand = localPlayer.getResources();
		
		return hand.getResources();
		
	}
	
	private void setEnables(ResourceType resource, Map<ResourceType, Integer> hand, int temp) {
		if(temp >= hand.get(resource))
			getDiscardView().setResourceAmountChangeEnabled(resource, false, true);
		
		if(temp <= 0)
			getDiscardView().setResourceAmountChangeEnabled(resource, true, false);
		
		if(temp > 0 && temp < hand.get(resource))
			getDiscardView().setResourceAmountChangeEnabled(resource, true, true);
		
		if(hand.get(resource) == 0)
			getDiscardView().setResourceAmountChangeEnabled(resource, false, false);
		
	}

	
}

