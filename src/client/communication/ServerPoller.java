package client.communication;

import java.util.Timer;
import java.util.UUID;
import java.util.logging.*;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import client.misc.ClientManager;
import shared.communication.IServer;
import shared.communication.Session;
import shared.exceptions.*;
import shared.model.ModelFacade;



/**
 * The ServerPoller periodically calls the server to check if there is an updated game model,
 * and if so, it retrieves the model and informs other relevant classes to update their model.
 * This is needed due to the fact that the server cannot send the information at any given point
 * to the client, only the client can ping the server.
 * @author Jordan Chipman
 * 
 */
public class ServerPoller {
	/**
	 * The default polling interval (1 second)
	 */
	public final static long DEFAULT_POLL_INTERVAL = 1000;
	
	private static final Logger log = Logger.getLogger( ServerPoller.class.getName() );
	private IServer server;
	private Timer poller = null;
	ClientCommunicator comm;
	Session user;
	ModelFacade modelHandler = ClientManager.getModel();
	
	public static void main() {
		ServerPoller poller = new ServerPoller(new MockServer(), new Session("SAM","sam",UUID.randomUUID()));
		poller.start();
	}

	/**
	 * Creates the Poller
	 * @param server the server or server proxy object to poll.
	 * @pre the server passed in is valid
	 * @post a ServerPoller object will be created.
	 */
	public ServerPoller(IServer server, Session user) {
		this.server = server;	
		this.user = user;
	}
	
	/** Tells you whether this poller is running or not
	 * @return true if the poller is running
	 * @pre None
	 * @post None
	 */
	public boolean isRunning() {
		return poller != null;
	}

	/**
	 * Creates a thread for polling at the specified interval and begins polling.
	 * @param interval The polling rate, in ms.
	 * @pre The poller is not running and the interval is positive
	 * @post The poller will be running
	 */
	public void start(long interval) {
		poller = new Timer(true); // Create a new timer to run as a daemon
		
		poller.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				try {
					//JSONObject modelRequest = new JSONObject();
					int version = modelHandler.getVersion();
					//int game = modelHandler.getGameHeader().getId();
					UUID gameUUID = modelHandler.getGameHeader().getUUID();
					
					final String modelStr = server.getModel(gameUUID, version);
					if (modelStr != null) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								log.fine("Sending new information to modelHander");
								modelHandler = ClientManager.getModel();
								modelHandler.updateFromJSON(modelStr);
							}
							
						});
					}
					
				} catch (ServerException | UserException e) {
					log.severe("Server error, could not connect");
					//e.printStackTrace();
				}
			}
			
		}, 0, interval);
	}
	
	/**
	 * Creates a thread for polling using the default poll interval
	 * @pre The poller is not running
	 * @post The poller will be running
	 */
	public void start() {
		start(DEFAULT_POLL_INTERVAL);
	}

	/**
	 * Stops the polling. <br/>
	 * <b>Make sure to call this when you are done polling, in order
	 * to prevent resource leaks (threads <i>are</i>, in fact,
	 * a limited resource.) </b>
	 * @pre the poller is running
	 * @post the poller will not be running
	 */
	public void stop() {
		poller.cancel();
		poller = null;
	}

	/**
	 * Gets the server object that this ServerPoller uses
	 * @return the server or server proxy object that this ServerPoller uses
	 * @pre None
	 * @post None
	 */
	public IServer getServer() {
		return server;
		
	}

}

