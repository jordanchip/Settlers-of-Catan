package server.userhandlers;

import java.util.List;
import java.util.UUID;

import org.json.simple.JSONObject;

import server.communication.ServerCommunicator;
import shared.communication.GameHeader;
import shared.communication.Session;
import shared.definitions.CatanColor;
import shared.definitions.ResourceType;
import shared.locations.EdgeDirection;
import shared.locations.EdgeLocation;
import shared.locations.HexLocation;
import shared.locations.VertexDirection;
import shared.locations.VertexLocation;
import shared.model.ResourceList;
import shared.model.ResourceTradeList;
import client.communication.ServerProxy;

public class Test {

	public static void main(String[] args){
		String[] arg = new String[1];
		arg[0] = "8081";
		ServerCommunicator.main(arg);
		try{
			new Test();
		}
		catch(Exception e){
			System.out.println("Failed");
		}
		System.exit(0);
	}
	
	@SuppressWarnings("unchecked")
	public Test() throws Exception{
		Session user;
		GameHeader game;
		List<GameHeader> games;
		Session returned;
		String model;
		ServerProxy server = new ServerProxy("localhost", 8081);
		HexLocation hLocation = new HexLocation(0, 0);
		VertexDirection vDirection = VertexDirection.East;
		VertexLocation vLocation = new VertexLocation(hLocation, vDirection);
		EdgeDirection eDirection = EdgeDirection.North;
		EdgeLocation eLocation = new EdgeLocation(hLocation, eDirection);
		ResourceList rList = new ResourceList(1);
		JSONObject tradeJSON = new JSONObject();
		tradeJSON.put(ResourceType.BRICK.toString().toLowerCase(), -1L);
		tradeJSON.put(ResourceType.ORE.toString().toLowerCase(), 0L);
		tradeJSON.put(ResourceType.SHEEP.toString().toLowerCase(), -1L);
		tradeJSON.put(ResourceType.WHEAT.toString().toLowerCase(), -1L);
		tradeJSON.put(ResourceType.WOOD.toString().toLowerCase(), 0L);
		ResourceTradeList rtList = new ResourceTradeList(tradeJSON);
		ResourceType rType = ResourceType.BRICK;
		UUID userUUID = UUID.randomUUID();
		UUID gameUUID = UUID.randomUUID();
		UUID receiverUUID = UUID.randomUUID();
		//user
		//login
		user = server.login("Sam", "sam");
		System.out.println(user.getUsername() + "\n");
		//register
		user = server.register("Steve", "steve");
		System.out.println(user.getUsername() + "\n");
		
		//game
		//createGame
		game = server.createGame("blah", true, true, true);
		System.out.println(game.getTitle() + "\n");
		//getGameList
		games = server.getGameList();
		System.out.println("Size: " + games.size() + "\n");
		//joinGame
		returned = server.joinGame(user, gameUUID, CatanColor.RED);
		//System.out.println(returned + "\n");
		
		//moves
		//AcceptTrade
		model = server.respondToTrade(userUUID, gameUUID, true);
		System.out.println("Accept Trade:\n" + model.substring(5, 49) + "\n");
		//BuildCity
		model = server.buildCity(userUUID, gameUUID, vLocation);
		System.out.println("Build City:\n" + model.substring(5, 49) + "\n");
		//BuildRoad
		model = server.buildRoad(userUUID, gameUUID, eLocation);
		System.out.println("Build Road:\n" + model.substring(5, 49) + "\n");
		//BuildSettlement
		model = server.buildSettlement(userUUID, gameUUID, vLocation);
		System.out.println("Build Settlement:\n" + model.substring(5, 49) + "\n");
		//BuyDevCard
		model = server.buyDevCard(userUUID, gameUUID);
		System.out.println("Buy Dev Card:\n" + model.substring(5, 49) + "\n");
		//DiscardCards
		model = server.discardCards(userUUID, gameUUID, rList);
		System.out.println("Discard Cards:\n" + model.substring(5, 49) + "\n");
		//FinishTurn
		model = server.finishTurn(userUUID, gameUUID);
		System.out.println("Finish Turn:\n" + model.substring(5, 49) + "\n");
		//MaritimeTrade
		model = server.maritimeTrade(userUUID, gameUUID, rType, rType, 2);
		System.out.println("Maritime Trade:\n" + model.substring(5, 49) + "\n");
		//GetModel
		model = server.getModel(gameUUID, 0);
		System.out.println("Get Model:\n" + model.substring(5, 49) + "\n");
		//Monopoly
		model = server.monopoly(userUUID, gameUUID, rType);
		System.out.println("Monopoly:\n" + model.substring(5, 49) + "\n");
		//Monument
		model = server.monument(userUUID, gameUUID);
		System.out.println("Monument:\n" + model.substring(5, 49) + "\n");
		//OfferTrade
		model = server.offerTrade(userUUID, gameUUID, rtList, receiverUUID);
		System.out.println("Offer Trade:\n" + model.substring(5, 49) + "\n");
		//RoadBuilding
		model = server.roadBuilding(userUUID, gameUUID, eLocation, eLocation);
		System.out.println("Road Building:\n" + model.substring(5, 49) + "\n");
		//RobPlayer
		model = server.robPlayer(userUUID, gameUUID, hLocation, receiverUUID);
		System.out.println("Rob Player:\n" + model.substring(5, 49) + "\n");
		//RollNumber
		model = server.rollDice(userUUID, gameUUID, 3);
		System.out.println("Roll Dice:\n" + model.substring(5, 49) + "\n");
		//SendChat
		model = server.sendChat(userUUID, gameUUID, "Hey");
		System.out.println("Send Chat:\n" + model.substring(5, 49) + "\n");
		//Solier
		model = server.soldier(userUUID, gameUUID, hLocation, receiverUUID);
		System.out.println("Solier:\n" + model.substring(5, 49) + "\n");
		//YearOfPlenty
		model = server.yearOfPlenty(userUUID, gameUUID, rType, rType);
		System.out.println("Year of Plenty:\n" + model.substring(5, 49) + "\n");
				
		server = new ServerProxy("localhost", 8081);
		try{
			server.buyDevCard(userUUID, gameUUID);
			System.out.println("Failed!");
			return;
		}
		catch(Exception e){
			System.out.println("Tried to buy Dev Card\n");
		}
		
		try{
			server.joinGame(user, gameUUID, CatanColor.BLUE);
			System.out.println("Failed!");
			return;
		}
		catch(Exception e){
			System.out.println("Tried to join game\n");
		}
		server.register("Sam", "Sam");
		System.out.println("Logged in\n");
		try{
			server.buyDevCard(userUUID, gameUUID);
			System.out.println("Failed!");
			return;
		}
		catch(Exception e){
			System.out.println("Tried to buy Dev Card\n");
		}
		server.joinGame(user, gameUUID, CatanColor.RED);
		System.out.println("Joined Game\n");
		
		server.buyDevCard(userUUID, gameUUID);
		System.out.println("Bought Dev Card\n");
		
		System.out.println("Finished!");
	}
}
