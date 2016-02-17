package server.communication;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.logging.*;

import server.userhandlers.*;
import server.movehandlers.*;
import server.plugins.PluginRegistry;
import server.gamehandlers.*;

import com.sun.net.httpserver.*;

public class ServerCommunicator {

	private static int SERVER_PORT_NUMBER = 8081;
	private static final int MAX_WAITING_CONNECTIONS = 10;
	
	private static Logger logger;
	
	static {
		try {
			initLog();
		}
		catch (IOException e) {
			System.out.println("Could not initialize log: " + e.getMessage());
		}
	}
	
	private static void initLog() throws IOException {
		
		Level logLevel = Level.FINE;
		
		logger = Logger.getLogger("RecordIndexer"); 
		logger.setLevel(logLevel);
		logger.setUseParentHandlers(false);
		
		Handler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(logLevel);
		consoleHandler.setFormatter(new SimpleFormatter());
		logger.addHandler(consoleHandler);

		FileHandler fileHandler = new FileHandler("log.txt", false);
		fileHandler.setLevel(logLevel);
		fileHandler.setFormatter(new SimpleFormatter());
		logger.addHandler(fileHandler);
	}

	
	private HttpServer server;
	String persistenceType;
	
	public ServerCommunicator() {
		return;
	}
	
	public void run() {
	
		logger.info("Initializing HTTP Server using: " + persistenceType + "database.");
		
		try {
			server = HttpServer.create(new InetSocketAddress(SERVER_PORT_NUMBER),
											MAX_WAITING_CONNECTIONS);
		} 
		catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);			
			return;
		}

		server.setExecutor(null); // use the default executor

		server.createContext("/user/login", loginHandler);
		server.createContext("/user/register", registerHandler);
		server.createContext("/games/list", listHandler);
		server.createContext("/games/create", createHandler);
		server.createContext("/games/join", joinHandler);
		//Correct syntax is /games/model?version=
		server.createContext("/game/model", modelHandler);
		server.createContext("/game/listAI", getAITypeHandler);
		server.createContext("/moves/sendChat", sendChatHandler);
		server.createContext("/moves/rollNumber", rollNumberHandler);
		server.createContext("/moves/robPlayer", robPlayerHandler);
		server.createContext("/moves/buyDevCard", buyDevCardHandler);
		server.createContext("/moves/Year_of_Plenty", yearOfPlentyHandler);
		server.createContext("/moves/Road_Building", roadBuildingHandler);
		server.createContext("/moves/Soldier", soldierHandler);
		server.createContext("/moves/Monopoly", monopolyHandler);
		server.createContext("/moves/buildRoad", buildRoadHandler);
		server.createContext("/moves/buildSettlement", buildSettlementHandler);
		server.createContext("/moves/buildCity", buildCityHandler);
		server.createContext("/moves/offerTrade", offerTradeHandler);
		server.createContext("/moves/acceptTrade", acceptTradeHandler);
		server.createContext("/moves/maritimeTrade", maritimeTradeHandler);
		server.createContext("/moves/discardCards", discardCardsHandler);
		server.createContext("/moves/finishTurn", finishTurnHandler);
		server.createContext("/moves/Monument", monumentHandler);
		server.createContext("/moves/buildStartingPieces", buildStartingPiecesHandler);
		
		server.createContext("/docs/api/data", new Handlers.JSONAppender(""));
		server.createContext("/docs/api/view", new Handlers.BasicFile(""));

		logger.info("Starting HTTP Server");

		server.start();
	}
	private HttpHandler loginHandler = new LoginHandler();
	private HttpHandler registerHandler = new RegisterHandler();
	private HttpHandler createHandler = new CreateHandler();
	private HttpHandler joinHandler = new JoinHandler();
	private HttpHandler listHandler = new ListHandler();
	private HttpHandler modelHandler = new ModelHandler();
	private HttpHandler getAITypeHandler = new GetAITypeHandler();
	private HttpHandler acceptTradeHandler = new AcceptTradeHandler();
	private HttpHandler buildCityHandler = new BuildCityHandler();
	private HttpHandler buildRoadHandler = new BuildRoadHandler();
	private HttpHandler buildSettlementHandler = new BuildSettlementHandler();
	private HttpHandler buildStartingPiecesHandler = new BuildStartingPiecesHandler();
	private HttpHandler buyDevCardHandler = new BuyDevCardHandler();
	private HttpHandler discardCardsHandler = new DiscardCardsHandler();
	private HttpHandler finishTurnHandler = new FinishTurnHandler();
	private HttpHandler maritimeTradeHandler = new MaritimeTradeHandler();
	private HttpHandler monopolyHandler = new MonopolyHandler();
	private HttpHandler monumentHandler = new MonumentHandler();
	private HttpHandler offerTradeHandler = new OfferTradeHandler();
	private HttpHandler roadBuildingHandler = new RoadBuildingHandler();
	private HttpHandler robPlayerHandler = new RobPlayerHandler();
	private HttpHandler rollNumberHandler = new RollNumberHandler();
	private HttpHandler sendChatHandler = new SendChatHandler();
	private HttpHandler soldierHandler = new SoldierHandler();
	private HttpHandler yearOfPlentyHandler = new YearOfPlentyHandler();
	
	
	public static void main(String[] args) {
		System.out.println(Arrays.asList(args));
		if (args.length == 3) {
			Server.setFlushFrequency(Integer.parseInt(args[2]));
		}
		if (args.length >= 2) {
			Server.setPersistenceType(args[1]);
		}
		else {
			Server.setPersistenceType(null);
		}
		ServerCommunicator server = new ServerCommunicator();
		if (args.length >= 2) {
			server.initPortNum(Integer.parseInt(args[0]));
		}
		else if (args.length == 1) {
			server.initPortNum(Integer.parseInt(args[0]));
		}
		else {
			server.initPortNum(SERVER_PORT_NUMBER);
		}
		server.run();
	}

	private void initPortNum(int port) {
		SERVER_PORT_NUMBER = port;
	}
	
	private void setPersistenceType(String type) {
		Server.setPersistenceType(type);
		persistenceType = type;
	}
	
}