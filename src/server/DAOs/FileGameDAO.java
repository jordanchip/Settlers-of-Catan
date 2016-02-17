package server.DAOs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import shared.communication.GameHeader;
import shared.model.ModelFacade;

/**
 * This class contains implementation for storing, deleting, and updating
 * games in a file-based database system.
 * @author jchip
 *
 */
public class FileGameDAO implements IGameDAO {

	private String filePath;
	private String gameHeaderExtension;

	public FileGameDAO(){
		filePath = "fileStorage/games/";
		gameHeaderExtension = "gameHeaders";
		try{
			File f = new File(filePath.substring(0,filePath.length() - 1));
			if(!f.exists()){
				Files.createDirectories(f.toPath());
			}
			f = new File(filePath + gameHeaderExtension);
			if(!f.exists()){
				Files.createFile(f.toPath());
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void addGame(UUID uuid, ModelFacade model){
		Gson gson = new Gson();
		try{
			FileOutputStream fileOut = new FileOutputStream(filePath + uuid.toString());
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(model);
			out.close();
			fileOut.close();
					
			List<GameHeader> games = getGameList();
			
			games.add(model.getGameHeader());
			
			fileOut = new FileOutputStream(filePath + gameHeaderExtension);
			out = new ObjectOutputStream(fileOut);
			out.writeObject(gson.toJson(games));
			out.close();
			fileOut.close();
		}
		catch(IOException | DatabaseException e){
			e.printStackTrace();
		}
	}

	 //Not Needed
	@Override
	public void removeGame(UUID gameUUID) {
	}

	@Override
	public void updateGamebyUUID(UUID gameUUID, ModelFacade model) {
		try{
			FileOutputStream fileOut = new FileOutputStream(filePath + gameUUID.toString());
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(model);
			out.close();
			fileOut.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public ModelFacade getGame(UUID gameUUID) {
		ModelFacade model = null;
		try{
			FileInputStream fileIn = new FileInputStream(filePath + gameUUID.toString());
			ObjectInputStream in = new ObjectInputStream(fileIn);

			model = (ModelFacade) in.readObject();
			
			in.close();
			fileIn.close();
		}
		catch(IOException | ClassNotFoundException e){
			e.printStackTrace();
		}
		return model;
	}

	@SuppressWarnings("unchecked")
	public List<GameHeader> getGameList() throws DatabaseException {
		Gson gson = new Gson();
		List<GameHeader> games = new ArrayList<GameHeader>();
		try{
			FileInputStream fileIn = new FileInputStream(filePath + gameHeaderExtension);
			ObjectInputStream in = new ObjectInputStream(fileIn);

			String gamesString = (String) in.readObject();
			
			List<Object> gameList = gson.fromJson(gamesString, List.class);
			
			for(Object o : gameList){
				games.add(gson.fromJson(o.toString(), GameHeader.class));
			}

			in.close();
			fileIn.close();
		}
		catch(IOException e){
			System.out.println("File was empty");
		}
		catch (ClassNotFoundException e){
			throw new DatabaseException();
		}
		return games;
	}

	 //Not Needed
	@Override
	public Map<UUID, ModelFacade> getAllGames() throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}
}
