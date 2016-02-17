package server.gamehandlers;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import server.communication.Server;
import server.interpreter.ExchangeConverter;
import shared.communication.IServer;
import shared.communication.Session;
import shared.definitions.CatanColor;
import shared.exceptions.ServerException;

/**
 * Handles join requests by communicating with the Server Facade,
 * and sends the response back through the httpExchange.
 * @author Jordan
 *
 */
public class JoinHandler extends AbstractGameHandler implements HttpHandler {
	
	IServer server = Server.getSingleton();
//	IServer server = new MockServer();
	Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	@SuppressWarnings("unchecked")
	@Override
	public void handle(HttpExchange arg0) throws IOException {
		arg0.getResponseHeaders().set("Content-type:", "application/text");
		String address = arg0.getRequestURI().toString();
		logger.log(Level.INFO, "Connection to " + address + " established.");

		try{
			if(!super.checkCookies(arg0, server)){
				throw new ServerException();
			}
			JSONObject json = ExchangeConverter.toJSON(arg0);
			UUID gameUUID = UUID.fromString((String)json.get("id"));
			CatanColor color = CatanColor.getColorFromString((String) json.get("color"));
			
			Session player = this.getPlayerSessionFromCookie(arg0, server);
			Session returnedPlayer = server.joinGame(player, gameUUID, color);
			
			String outputMsg = "";
			if (returnedPlayer != null)
				outputMsg = "Success";
			else
				outputMsg = "Failed";
			
			JSONObject header = new JSONObject();
			header.put("name", returnedPlayer.getUsername());
			header.put("password", returnedPlayer.getPassword());
			header.put("playerUUID", returnedPlayer.getPlayerUUID().toString());
			StringBuilder str = new StringBuilder();
			str.append("catan.user=");
			str.append(URLEncoder.encode(header.toJSONString()));
			str.append(";Path=/;");
			String cookie = str.toString();
			
			JSONObject header2 = new JSONObject();
			header2.put("gameUUID", gameUUID.toString());
			str = new StringBuilder();
			str.append("Catan.game=");
			str.append(URLEncoder.encode(header2.toJSONString()));
			str.append(";Path=/;");
			cookie += str.toString();
			
			arg0.getResponseHeaders().add("Set-cookie", cookie);
			arg0.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
			OutputStreamWriter output = new OutputStreamWriter(arg0.getResponseBody());
			output.write(outputMsg);
			output.flush();
			arg0.getResponseBody().close();
		} catch (Exception e) {
			e.printStackTrace();
			arg0.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, -1);
			arg0.getResponseBody().close();
		}
	}
}