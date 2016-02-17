package server.Factories;

import server.DAOs.DatabaseException;
import server.DAOs.ICommandDAO;
import server.DAOs.IGameDAO;
import server.DAOs.IUserDAO;
import server.DAOs.MockCommandDAO;
import server.DAOs.MockGameDAO;
import server.DAOs.MockUserDAO;

public class MockDAOFactory implements IDAOFactory {

	@Override
	public IUserDAO getUserDAO() {
		// TODO Auto-generated method stub
		return new MockUserDAO();
	}

	@Override
	public IGameDAO getGameDAO() {
		// TODO Auto-generated method stub
		return new MockGameDAO();
	}

	@Override
	public ICommandDAO getCommandDAO() {
		// TODO Auto-generated method stub
		return new MockCommandDAO();
	}

	@Override
	public void startTransaction() throws DatabaseException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endTransaction(boolean commit) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}

}
