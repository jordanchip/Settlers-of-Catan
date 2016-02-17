package client.misc;

import client.communication.MockServer;
import client.communication.ServerProxy;
import shared.communication.IServer;
import shared.communication.Session;
import shared.exceptions.GameInitializationException;
import shared.model.ClientModelFacade;
import shared.model.PlayerReference;

public class ClientManager {
	
	private static volatile ClientModelFacade model = null;
	private static IServer server;
	private static PlayerReference localPlayer;

	public static final int SERVER_TYPE_PROXY = 0;
	public static final int SERVER_TYPE_SERVER = 1;
	public static final int SERVER_TYPE_MOCK = 2;
	
	public static String host = null;
	public static int port = -1;
	private static Session session = null;
	
	public static final int DEFAULT_SERVER_TYPE = SERVER_TYPE_PROXY;
		
	
	public static void setServerHostAndPort(String host, int port){
		ClientManager.host = host;
		ClientManager.port = port;
	}
	
	public static ClientModelFacade getModel() {
		if (model == null) {
			synchronized (ClientManager.class) {
				if (model == null) {
					try {
						model = new ClientModelFacade();
					} catch (GameInitializationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return model;
	}
	
	public static void initializeServer(int type) {
		if (server != null) {
			throw new RuntimeException("Cannot initialize server twice.");
		}
		switch (type) {
		default:
		case SERVER_TYPE_PROXY:
			server = new ServerProxy(ClientManager.host, ClientManager.port);
			break;
		case SERVER_TYPE_SERVER:
			//server = new Server();
			break;
		case SERVER_TYPE_MOCK:
			server = new MockServer();
			break;
		
		}
	}
	
	public static IServer getServer() {
		if (server == null) {
			try {
				initializeServer(DEFAULT_SERVER_TYPE);
			} catch (Exception e) {
				System.out.println("Averted a double server crisis!");
			}
		}
		return server;
	}

	public static PlayerReference getLocalPlayer() {
		return localPlayer;
	}

	public static void setLocalPlayer(PlayerReference localPlayer) {
		ClientManager.localPlayer = localPlayer;
	}

	public static void setSession(Session session) {
		ClientManager.session  = session;
	}
	
	public static Session getSession() {
		return session;
	}
	

}
