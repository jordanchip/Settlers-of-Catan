package server.gamehandlers;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import server.communication.Server;
import shared.communication.IServer;
import shared.communication.Session;

import com.sun.net.httpserver.HttpExchange;

public abstract class AbstractGameHandler {

	@SuppressWarnings("deprecation")
	public boolean checkCookies(HttpExchange exchange, IServer server){
		
		exchange.getResponseHeaders().set("Content-type:", "application/text");
		
		List<String> cookies = exchange.getRequestHeaders().get("Cookie");
		if(cookies.size() != 1){
			return false;
		}
		
		JSONParser parser = new JSONParser();
		
		String cookieEncoded = cookies.get(0);
		String cookieDecoded = URLDecoder.decode(cookieEncoded);
		cookieDecoded = cookieDecoded.substring(11);
		
		//These lines are necessary due to the game header containing Catan.game= sequence,
		//which will break when trying to cast it cast a JSON object.  If you have a better
		//way, please apply.
		int indexOfGameCookie = cookieDecoded.indexOf("};Catan.game={");
		if (indexOfGameCookie != -1) {
			String gameCookie = cookieDecoded.substring(indexOfGameCookie+14);
			cookieDecoded = cookieDecoded.substring(0,indexOfGameCookie).concat(",").concat(gameCookie);
		}
		else {
			indexOfGameCookie = cookieDecoded.indexOf("}; Catan.game={");
			if (indexOfGameCookie != -1) {
				String gameCookie = cookieDecoded.substring(indexOfGameCookie+15);
				cookieDecoded = cookieDecoded.substring(0,indexOfGameCookie).concat(",").concat(gameCookie);
			}
		}
		
		try{
			JSONObject cookie = (JSONObject) parser.parse(cookieDecoded);
			String username = (String) cookie.get("name");
			String password = (String) cookie.get("password");
			//UUID userID = UUID.fromString((String) cookie.get("playerUUID"));
			
			Session user = server.login(username, password);
			
//			if(!userID.equals(user.getPlayerUUID())){
//				return false;
//			}
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
		
	}
	
	@SuppressWarnings("deprecation")
	public Session getPlayerSessionFromCookie(HttpExchange arg0, IServer server) {
		List<String> cookies = arg0.getRequestHeaders().get("Cookie");
		if(cookies.size() != 1){
			return null;
		}
		
		JSONParser parser = new JSONParser();
		
		String cookieEncoded = cookies.get(0);
		String cookieDecoded = URLDecoder.decode(cookieEncoded);
		cookieDecoded = cookieDecoded.substring(11);
		
		int indexOfGameCookie = cookieDecoded.indexOf("};Catan.game={");
		if (indexOfGameCookie != -1) {
			String gameCookie = cookieDecoded.substring(indexOfGameCookie+14);
			cookieDecoded = cookieDecoded.substring(0,indexOfGameCookie).concat(",").concat(gameCookie);
		}
		else {
			indexOfGameCookie = cookieDecoded.indexOf("}; Catan.game={");
			if (indexOfGameCookie != -1) {
				String gameCookie = cookieDecoded.substring(indexOfGameCookie+15);
				cookieDecoded = cookieDecoded.substring(0,indexOfGameCookie).concat(",").concat(gameCookie);
			}
		}
		
		try{
			JSONObject cookie = (JSONObject) parser.parse(cookieDecoded);
			String username = (String) cookie.get("name");
			String password = (String) cookie.get("password");
			//UUID userID = UUID.fromString((String) cookie.get("playerUUID"));
			
			Session user = server.login(username, password);
			
//			if(!userID.equals(user.getPlayerUUID())){
//				return false;
//			}
			return user;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
