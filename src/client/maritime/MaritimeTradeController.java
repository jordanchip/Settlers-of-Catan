package client.maritime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import shared.communication.IServer;
import shared.definitions.*;
import shared.exceptions.ServerException;
import shared.exceptions.UserException;
import shared.model.Board;
import shared.model.ModelFacade;
import shared.model.Player;
import shared.model.PlayerReference;
import shared.model.Port;
import shared.model.ResourceList;
import shared.model.TurnTracker;
import client.base.*;
import client.misc.ClientManager;
import client.misc.IMessageView;


/**
 * Implementation for the maritime trade controller
 */
public class MaritimeTradeController extends Controller implements IMaritimeTradeController {

	private IMessageView messageView;
	private IMaritimeTradeOverlay tradeOverlay;
	private IServer server;
	private ModelFacade modelFacade;
	private PlayerReference localPlayer;
	private Board board;
	private List<ResourceType> typesOfResources;
	private ResourceType inResource;
	private ResourceType outResource;
	private int ratio;
	
	public MaritimeTradeController(IMaritimeTradeView tradeView, IMaritimeTradeOverlay tradeOverlay) {
		
		super(tradeView);
		inResource = null;
		outResource = null;
		ratio = 4;
		typesOfResources = new ArrayList<ResourceType>();
		typesOfResources.add(ResourceType.BRICK);
		typesOfResources.add(ResourceType.ORE);
		typesOfResources.add(ResourceType.SHEEP);
		typesOfResources.add(ResourceType.WHEAT);
		typesOfResources.add(ResourceType.WOOD);

		setTradeOverlay(tradeOverlay);
	}
	
	public IMaritimeTradeView getTradeView() {
		
		return (IMaritimeTradeView)super.getView();
	}
	
	public IMaritimeTradeOverlay getTradeOverlay() {
		return tradeOverlay;
	}

	public void setTradeOverlay(IMaritimeTradeOverlay tradeOverlay) {
		this.tradeOverlay = tradeOverlay;
	}

	
	/**
	 *Gets called by the view when the roll dice button is pressed
	 *To Do (not necessarily in this class):
	 *Create and array of possible resource trade options (Resource types of a sufficent amount to trade) 
	 *and pass them into the overlay in .showGiveOptions(resurce[])
	 *disable trade button in view
	 *showModal on tradeOverlay
	 */
	@Override
	public void startTrade() {
		inResource = null;
		outResource = null;
		ratio = 4;
		getTradeOverlay().reset();
		server = ClientManager.getServer();
		modelFacade = ClientManager.getModel();
		localPlayer = ClientManager.getLocalPlayer();
		board = ClientManager.getModel().getCatanModel().getMap();
		List<Player> players = modelFacade.getCatanModel().getPlayers();
		Player thisPlayer = players.get(localPlayer.getIndex());
		ResourceList resources = thisPlayer.getResources();
		Collection<Port> ports = board.getPorts();
		Set<ResourceType> resourceSet = new TreeSet<ResourceType>();
		int tradeRatio = 4;
		for(Port port : ports){
			PlayerReference owner = board.getPortOwner(port);
			if(owner == null){
				continue;
			}
			if(owner.getIndex() == localPlayer.getIndex()){
				if(port.getRatio() != 3){
					if(resources.count(port.getResource()) >= 2){
						resourceSet.add(port.getResource());
					}
				}
				else{
					tradeRatio = 3;
				}
			}
		}
		
		for(ResourceType type : typesOfResources){
			if(resources.count(type) >= tradeRatio){
				resourceSet.add(type);
			}
		}
		ResourceType[] resourceArray = new ResourceType[resourceSet.size()];
		int i = 0;
		for(ResourceType type : resourceSet){
			resourceArray[i] = type;
			++i;
		}
		getTradeOverlay().showGiveOptions(resourceArray);
		getTradeOverlay().setTradeEnabled(false);
		getTradeOverlay().showModal();
	}

	
	
	
	/**
	 * Verify there is a resource to give and recieve
	 * Send request to server
	 * closeModal
	 * */
	@Override
	public void makeTrade() {
		try{
			int gameID = ClientManager.getModel().getGameHeader().getId();
			
			UUID playerUUID = ClientManager.getLocalPlayer().getPlayerUUID();
			UUID gameUUID = ClientManager.getModel().getGameHeader().getUUID();
			server.maritimeTrade(playerUUID, gameUUID, inResource, outResource, ratio);
			
//			server.maritimeTrade(ClientManager.getLocalPlayer().getIndex(), gameID, inResource, outResource, ratio);
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
		getTradeOverlay().closeModal();
	}

	
	/**
	 * closeModal()
	 */
	@Override
	public void cancelTrade() {

		getTradeOverlay().closeModal();
	}

	
	/**
	 * check to make sure bank has the resource
	 * call selectGetOption() in tradeOverlay
	 * enable/disable trade button based on validity 
	 */
	@Override
	public void setGetResource(ResourceType resource) {
		outResource = resource;
		getTradeOverlay().setTradeEnabled(true);
		getTradeOverlay().selectGetOption(resource, 1);
	}

	/**
	 * find out trade ratio for resource to give
	 * call selectGiveOption() in tradeOverlay
	 */
	@Override
	public void setGiveResource(ResourceType resource) {
		inResource = resource;
		ratio = ClientManager.getModel().getCatanModel()
				.getMaritimeRatios(ClientManager.getLocalPlayer())
				.get(resource);
		/*Collection<Port> ports = board.getPorts();
		for(Port port : ports){
			if(port.getRatio() == 3){
				if(board.getPortOwner(port) == null){
					continue;
				}
				if(board.getPortOwner(port).equals(ClientManager.getLocalPlayer())){
					ratio = 3;
				}
			}
			else if(port.getResource().equals(resource)){
				if(board.getPortOwner(port) == null){
					continue;
				}
				if(board.getPortOwner(port).equals(ClientManager.getLocalPlayer())){
					ratio = 2;
					break;
				}
			}
		}*/
		getTradeOverlay().selectGiveOption(resource, ratio);
	}

	
	/**
	 * disable trade button
	 * redisplay the trade Options for getting resource
	 */
	@Override
	public void unsetGetValue() {
		outResource = null;
		ResourceType[] resources = new ResourceType[5];
		resources[0] = ResourceType.BRICK;
		resources[1] = ResourceType.ORE;
		resources[2] = ResourceType.SHEEP;
		resources[3] = ResourceType.WHEAT;
		resources[4] = ResourceType.WOOD;
		getTradeOverlay().showGetOptions(resources);
		getTradeOverlay().setTradeEnabled(false);
	}

	
	/**
	 * disable trade button
	 * redisplay the trade Options for getting resource
	 */
	@Override
	public void unsetGiveValue() {
		inResource = null;
		outResource = null;
		ratio = 4;
		getTradeOverlay().hideGetOptions();
		getTradeOverlay().setTradeEnabled(false);
		startTrade();
	}
	
	@Override
	public void turnTrackerChanged(TurnTracker turnTracker) {
		if (turnTracker.getCurrentPlayer().getIndex() == 
				ClientManager.getLocalPlayer().getIndex() &&
				turnTracker.getStatus().equals(TurnStatus.Playing)) {
			getTradeView().enableMaritimeTrade(true);
		}
		else {
			getTradeView().enableMaritimeTrade(false);
		}
	}

}

