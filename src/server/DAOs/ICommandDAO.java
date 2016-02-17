package server.DAOs;

import java.util.List;
import java.util.UUID;

import server.commands.ICatanCommand;

public interface ICommandDAO {

	public void addCommand(UUID gameid, ICatanCommand command) throws DatabaseException;
	public void clearCommands(UUID gameid) throws DatabaseException;
	public List<ICatanCommand> getAll(UUID gameid) throws DatabaseException;
	
}
