package client.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import shared.communication.IServer;
import shared.definitions.CatanColor;
import shared.exceptions.ServerException;
import shared.exceptions.UserException;
import shared.model.MessageList;
import shared.model.ModelFacade;
import shared.model.Player;
import shared.model.PlayerReference;
import client.base.*;
import client.misc.ClientManager;


/**
 * Chat controller implementation
 */
public class ChatController extends Controller implements IChatController {

	private IServer serverProxy = ClientManager.getServer();
	private ModelFacade modelFacade = ClientManager.getModel();
	
	public ChatController(IChatView view) {
		
		super(view);
	}

	@Override
	public IChatView getView() {
		return (IChatView)super.getView();
	}

	@Override
	public void sendMessage(String message) {
			
		try {
			
			
			PlayerReference localPlayer = ClientManager.getLocalPlayer();
			int index = localPlayer.getIndex();
			int gameIndex = ClientManager.getModel().getGameHeader().getId();
			
			UUID gameUUID = ClientManager.getModel().getGameHeader().getUUID();
			UUID playerUUID = localPlayer.getPlayerUUID();
			serverProxy.sendChat(playerUUID, gameUUID, message);
			
			//serverProxy.sendChat(index, gameIndex, message);

		} catch (ServerException | UserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void chatChanged(MessageList otherChat) {
		
		MessageList messageList = modelFacade.getCatanModel().getChat();
		
		List<LogEntry> entries = messageListToEntries(messageList);
		
		getView().setEntries(entries);
	}

	private List<LogEntry> messageListToEntries(MessageList messageList) {
		
		List<LogEntry> entries = new ArrayList<>();
		
		List<String> names = messageList.getSource();
		List<String> messages = messageList.getMessage();
		
		for(int i = 0; i < names.size(); i++) {
			
			String name = names.get(i);
			String message = messages.get(i);
			
			CatanColor color = nameToCatanColor(name);
			
			LogEntry entry = new LogEntry(color, message);
			
			entries.add(entry);
		}
		
		return entries;
	}

	private CatanColor nameToCatanColor(String name) {
		
		List<Player> players = modelFacade.getCatanModel().getPlayers();
		
		for(Player p : players) {
			
			if(p.getName().equals(name))
				return p.getColor();
		}
		
		return players.get(0).getColor();
	}
	
	
}

