package server.DAOs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import server.commands.ICatanCommand;

public class MockCommandDAO implements ICommandDAO {

	@Override
	public void addCommand(UUID gameid, ICatanCommand command)
			throws DatabaseException {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearCommands(UUID gameid) throws DatabaseException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<ICatanCommand> getAll(UUID gameid) throws DatabaseException {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

}
