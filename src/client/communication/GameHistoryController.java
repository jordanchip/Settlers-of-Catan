package client.communication;

import java.util.*;

import client.base.*;
import client.misc.ClientManager;
import shared.definitions.*;
import shared.model.MessageList;
import shared.model.ModelFacade;
import shared.model.Player;


/**
 * Game history controller implementation
 */
public class GameHistoryController extends Controller implements IGameHistoryController {

	private ModelFacade modelFacade = ClientManager.getModel();
	
	public GameHistoryController(IGameHistoryView view) {
		
		super(view);
		
		initFromModel();
	}
	
	@Override
	public IGameHistoryView getView() {
		
		return (IGameHistoryView)super.getView();
	}
	
	private void initFromModel() {
		
		//<temp>
		
		MessageList messageList = modelFacade.getCatanModel().getLog();
		
		List<LogEntry> entries;
		
		if(messageList != null)
			entries = messageListToEntries(messageList);
		else {
			entries = new ArrayList<>();
			entries.add(new LogEntry(CatanColor.WHITE, "Starting Game!"));
		}
			
//		entries.add(new LogEntry(CatanColor.BROWN, "This is a brown message"));
//		entries.add(new LogEntry(CatanColor.ORANGE, "This is an orange message ss x y z w.  This is an orange message.  This is an orange message.  This is an orange message."));
//		entries.add(new LogEntry(CatanColor.BROWN, "This is a brown message"));
//		entries.add(new LogEntry(CatanColor.ORANGE, "This is an orange message ss x y z w.  This is an orange message.  This is an orange message.  This is an orange message."));
//		entries.add(new LogEntry(CatanColor.BROWN, "This is a brown message"));
//		entries.add(new LogEntry(CatanColor.ORANGE, "This is an orange message ss x y z w.  This is an orange message.  This is an orange message.  This is an orange message."));
//		entries.add(new LogEntry(CatanColor.BROWN, "This is a brown message"));
//		entries.add(new LogEntry(CatanColor.ORANGE, "This is an orange message ss x y z w.  This is an orange message.  This is an orange message.  This is an orange message."));
		
		getView().setEntries(entries);
	
		//</temp>
	}
	
	
	public void logChanged(MessageList otherLog) {
		
		MessageList messageList = modelFacade.getCatanModel().getLog();
		
		List<LogEntry> entries = messageListToEntries(messageList);
		
		getView().setEntries(entries);
		
	}
	
	private List<LogEntry> messageListToEntries(MessageList messageList) {
		
		List<LogEntry> entries = new ArrayList<>();
		
		List<String> names = messageList.getSource();
		List<String> messages = messageList.getMessage();
		
		if(names != null){
			for(int i = 0; i < names.size(); i++) {
				
				String name = names.get(i);
				String message = messages.get(i);
					
				CatanColor color = nameToCatanColor(name);
				
				LogEntry entry = new LogEntry(color, message);
				
				entries.add(entry);
			}
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

