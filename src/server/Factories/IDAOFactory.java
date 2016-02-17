package server.Factories;

import server.DAOs.*;

/**
 * This interface defines the necessary get methods for all other factories to
 * implement.  A factory contains all Database Access Objects necessary to store 
 * the model into a database.  This way, the program can store data into the
 * database whenever it needs, without having to worry about what database it
 * is storing into, or what the implementation looks like.
 * @author jchip
 *
 */
public interface IDAOFactory {

	public IUserDAO getUserDAO();
	public IGameDAO getGameDAO();
	public ICommandDAO getCommandDAO();
	public void startTransaction() throws DatabaseException; // Not needed
	public void endTransaction(boolean commit) throws DatabaseException; // Not needed
	
}