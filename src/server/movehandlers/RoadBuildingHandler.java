package server.movehandlers;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import server.communication.Server;
import server.interpreter.ExchangeConverter;
import shared.communication.IServer;
import shared.exceptions.SchemaMismatchException;
import shared.exceptions.ServerException;
import shared.exceptions.UserException;
import shared.locations.EdgeLocation;

/**
 * Handles roadBuilding requests by communicating with the Server Facade,
 * and sends the response back through the httpExchange.
 * @author Jordan
 *
 */
public class RoadBuildingHandler extends AbstractMoveHandler implements HttpHandler {

	IServer server = Server.getSingleton();
//	IServer server = new MockServer();
	Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	@Override
	public void handle(HttpExchange arg0) throws IOException {
		arg0.getResponseHeaders().set("Content-type:", "application/text");
		String address = arg0.getRequestURI().toString();
		logger.log(Level.INFO, "Connection to " + address + " established.");

		try{
			UUID gameUUID = super.checkCookies(arg0, server);
			if(gameUUID == null){
				throw new ServerException();
			}
			JSONObject json = ExchangeConverter.toJSON(arg0);
			/*
			 * Extract needed information from JSON, and call the appropriate server method.
			 */
			UUID index = UUID.fromString((String)json.get("playerIndex"));
			
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject)parser.parse((String)json.get("spot1"));
			EdgeLocation firstRoad = new EdgeLocation(jsonObject);
			jsonObject = (JSONObject)parser.parse((String)json.get("spot2"));
			EdgeLocation secondRoad = new EdgeLocation(jsonObject);

			String gson = server.roadBuilding(index, gameUUID, firstRoad, secondRoad);
			
			arg0.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
			if (gson != null) {
				OutputStreamWriter output = new OutputStreamWriter(arg0.getResponseBody());
				output.write(gson);
				output.flush();
				arg0.getResponseBody().close();
			}
		} catch (ParseException | ServerException | UserException | SchemaMismatchException e) {
			arg0.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 500);
			e.printStackTrace();
		}
	}
}