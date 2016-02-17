package client.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import shared.communication.Session;
import shared.exceptions.GameInitializationException;
import shared.exceptions.JoinGameException;
import shared.exceptions.NameAlreadyInUseException;
import shared.exceptions.ServerException;
import shared.exceptions.UserException;

/**
 * 
 * @author Steven Pulsipher
 * Communication between client and server
 */
public class ClientCommunicator {

	private String userCookie = null;
	private String gameCookie = null;
	private String cookies = null;
	private Session playerSession = null;
	
	/**
	 * creates a new ClientCommunicator
	 */
	public ClientCommunicator(){}
	
	
	@SuppressWarnings({ "deprecation" })
	public JSONObject login(JSONObject o)
			throws ServerException, UserException{
		try{
			URL url = new URL((String) o.get("url"));
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod((String) o.get("requestType"));
			con.setDoOutput(true);
			con.connect();
			
			OutputStreamWriter output = new OutputStreamWriter(con.getOutputStream());
			output.write(o.toString());
			output.flush();

			if (con.getResponseCode() == HttpURLConnection.HTTP_OK){
				JSONParser parser = new JSONParser();
				JSONObject JSONOutput;
				
				String header = con.getHeaderField("Set-cookie");
				header = header.substring(0, header.length() - 8);
				String cutHeader = header.substring(11);
				String decoded = URLDecoder.decode(cutHeader);
				JSONOutput = (JSONObject) parser.parse(decoded);
				userCookie = header;
				gameCookie = null;
				cookies = userCookie;
				
				return JSONOutput;
			}
			if(o.get("url").equals("http://localhost:8081/user/register")){
				throw new NameAlreadyInUseException();
			}
			throw new UserException();
		}
		catch(IOException | ParseException e){
			throw new ServerException();
		}
	}

	public JSONObject preJoin(JSONObject o)
			throws ServerException, UserException, GameInitializationException{		
		if(userCookie == null){
			throw new UserException();
		}
		try{
			URL url = new URL((String) o.get("url"));
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("Cookie", userCookie);
			con.setRequestMethod((String) o.get("requestType"));
			con.setDoOutput(true);
			con.connect();
			
			OutputStreamWriter output = new OutputStreamWriter(con.getOutputStream());
			output.write(o.toString());
			output.flush();
			
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK){
				
				InputStream input = con.getInputStream();
				int len = 0;
				
				byte[] buffer = new byte[1024];
				StringBuilder str = new StringBuilder();
				while(-1 != (len = input.read(buffer))){
					str.append(new String(buffer, 0, len));
				}

				JSONParser parser = new JSONParser();
				JSONObject JSONOutput;
				
				if(str.charAt(0) == '['){
					str = new StringBuilder("{\"games\":" + str + "}");
				}
				if(str.charAt(0) != '{'){
					JSONOutput = new JSONObject();
					return JSONOutput;
				}
				JSONOutput = (JSONObject) parser.parse(str.toString());
				return JSONOutput;
			}
			if(o.get("url").equals("http://localhost:8081/games/create")){
				throw new GameInitializationException();
			}
			throw new UserException();
		}
		catch(IOException | ParseException e){
			e.printStackTrace();
			throw new ServerException();
		}
	}
	@SuppressWarnings({ "unchecked" })
	public JSONObject joinGame(JSONObject o)
			throws ServerException, JoinGameException, UserException{
		if(userCookie == null){
			throw new JoinGameException();
		}
		try{
			URL url = new URL((String) o.get("url"));
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("Cookie", cookies);
			con.setRequestMethod((String) o.get("requestType"));
			if((o.get("requestType")).equals("POST")){// || (o.get("url")).equals("http://localhost:8081/game/model")){
				con.setDoOutput(true);
				OutputStreamWriter output = new OutputStreamWriter(con.getOutputStream());
				output.write(o.toString());
				output.flush();
			}
			con.connect();
			
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK){
				
				JSONParser parser = new JSONParser();
				JSONObject JSONOutput;
				
				String header = con.getHeaderField("Set-cookie");
				int indexOfFirstPath = header.indexOf(";Path=/;");
				String userHeader = header.substring(0, indexOfFirstPath);
				userCookie = userHeader;
				
				String cutHeader = userHeader.substring(11);
				String decoded = URLDecoder.decode(cutHeader);
				JSONOutput = (JSONObject) parser.parse(decoded);
				String username = (String)JSONOutput.get("name");
				String password = (String)JSONOutput.get("password");
				UUID playerUUID = UUID.fromString((String)JSONOutput.get("playerUUID"));
				playerSession = new Session(username, password, playerUUID);
				
				String gameHeader = header.substring(indexOfFirstPath+8,header.length()-8);
				gameCookie = gameHeader;
				
				//gameCookie = header;
				cookies = userCookie + ";" + gameCookie;
				
				InputStream input = con.getInputStream();
				int len = 0;
				

				byte[] buffer = new byte[1024];
				StringBuilder str = new StringBuilder();
				while(-1 != (len = input.read(buffer))){
					str.append(new String(buffer, 0, len));
				}
				
				JSONObject JSONOutput2 = new JSONObject();
				JSONOutput2.put("success", str.toString());
		
				return JSONOutput2;
			}
			return null;
		}
		catch(IOException | ParseException e){
			e.printStackTrace();
			throw new ServerException();
		}
	}
	
	/**
	 * Sends to the server
	 * @param o the JSON Object that is going to be sent
	 * @pre JSON Object is valid, and contains a location to be sent as well as "Get" or "Post"
	 * @post Response from the server will be given
	 * @return response object from server
	 * @throws ServerException
	 * @throws UserExceptiosn 
	 */
	public String send(JSONObject o)
			throws ServerException, UserException {

		if(userCookie == null || gameCookie == null){
			throw new UserException();
		}
		try{
			URL url = new URL((String) o.get("url"));
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("Cookie", cookies);
			con.setRequestMethod((String) o.get("requestType"));
			if((o.get("requestType")).equals("POST")){// || (o.get("url")).equals("http://localhost:8081/game/model")){
				con.setDoOutput(true);
				OutputStreamWriter output = new OutputStreamWriter(con.getOutputStream());
				output.write(o.toString());
				output.flush();
			}
			con.connect();
			
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK){
				
				InputStream input = con.getInputStream();
				int len = 0;
				
				byte[] buffer = new byte[1024];
				StringBuilder str = new StringBuilder();
				while(-1 != (len = input.read(buffer))){
					str.append(new String(buffer, 0, len));
				}

				if (str.length() == 0)
					return null;
				if(str.charAt(0) == '['){
					str = new StringBuilder("{\"list\":" + str + "}");
				}
				else if(str.charAt(0) != '{'){
					return null;
				}
				return str.toString();
			}
			InputStream input = con.getErrorStream();
			int len = 0;
			
			byte[] buffer = new byte[1024];
			StringBuilder str = new StringBuilder();
			while(-1 != (len = input.read(buffer))){
				str.append(new String(buffer, 0, len));
			}
			if(str.length() == 0)
				throw new UserException();
			if(str.charAt(0) == '['){
				str = new StringBuilder("{\"list\":" + str + "}");
			}
			System.out.println(str.toString());
			throw new UserException();
		}
		catch(IOException e){
			e.printStackTrace();
			throw new ServerException();
		}
	}
	
	public void setUserCookie(String userCookie) {
		this.userCookie = userCookie;
		cookies = userCookie + ";" + gameCookie;
	}
	public Session getPlayerSession() {
		return playerSession;
	}
}
