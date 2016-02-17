package server.Factories;

import server.DAOs.*;
import server.model.User;

/**
 * This factory contains references to all the
 * DAOs necessary to store the necessary model into
 * a SQL based, relational database.
 * @author jchip
 *
 */
public class SQLDAOFactory implements IDAOFactory {
	
	IUserDAO userDAO;
	IGameDAO gameDAO;
	ICommandDAO commandDAO;
	
	SQLDatabase db;
	
	public static void main(String[] args) {
		SQLDAOFactory factory = new SQLDAOFactory();
		factory.testrun();
	}
	
	private void testrun() {
		User user = new User("Sam", "sam");
		try {
			db.startTransaction();
			getUserDAO().addUser(user);
			db.endTransaction(true);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}
	
	
	public SQLDAOFactory() {
		try {
			SQLDatabase.initialize();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		db = new SQLDatabase();
		userDAO = new SQLUserDAO(db);
		gameDAO = new SQLGameDAO(db);
		commandDAO = new SQLCommandDAO(db);
	}
	
	@Override
	public IUserDAO getUserDAO() {
		return userDAO;
	}

	@Override
	public IGameDAO getGameDAO() {
		return gameDAO;
	}

	@Override
	public ICommandDAO getCommandDAO() {
		return commandDAO;
	}

	@Override
	public void startTransaction() throws DatabaseException {
		db.startTransaction();
	}

	@Override
	public void endTransaction(boolean commit) {
		db.endTransaction(commit);
	}


}
