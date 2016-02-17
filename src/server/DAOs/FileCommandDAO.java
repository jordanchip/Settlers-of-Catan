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
import java.util.UUID;

import server.commands.CommandSerializationException;
import server.commands.CommandSerializer;
import server.commands.ICatanCommand;

public class FileCommandDAO implements ICommandDAO {

	private String filePath;
	
	public FileCommandDAO(){
		filePath = "fileStorage/commands/";
		try{
			File f = new File(filePath.substring(0,filePath.length() - 1));
			if(!f.exists()){
				Files.createDirectories(f.toPath());
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void addCommand(UUID gameid, ICatanCommand command)
			throws DatabaseException {
		try{
			List<ICatanCommand> commands = getAll(gameid);
			List<String> newCommands = new ArrayList<String>();
			commands.add(command);
			for(ICatanCommand com : commands){
				newCommands.add(CommandSerializer.serialize(com));
			}
			
			FileOutputStream fileOut = new FileOutputStream(filePath + gameid.toString());
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(newCommands);
			out.close();
			fileOut.close();
		}
		catch(IOException | DatabaseException | CommandSerializationException e){
			e.printStackTrace();
		}
	}

	@Override
	public void clearCommands(UUID gameid) throws DatabaseException {
		try{
			List<ICatanCommand> commands = new ArrayList<ICatanCommand>();;
			FileOutputStream fileOut = new FileOutputStream(filePath + gameid.toString());
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(commands);
			out.close();
			fileOut.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ICatanCommand> getAll(UUID gameid) throws DatabaseException {
		List<ICatanCommand> commands = new ArrayList<ICatanCommand>();
		List<String> commandsString = new ArrayList<String>();
		try{
			FileInputStream fileIn = new FileInputStream(filePath + gameid.toString());
			ObjectInputStream in = new ObjectInputStream(fileIn);

			commandsString = (List<String>) in.readObject();
			
			for(String str : commandsString){
				commands.add(CommandSerializer.deserialize(str));
			}
			
			in.close();
			fileIn.close();
		}
		catch(IOException e){
			System.out.println("File was empty");
		}
		catch (ClassNotFoundException | CommandSerializationException e){
			throw new DatabaseException();
		}
		return commands;
	}

}
