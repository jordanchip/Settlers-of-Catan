package server.DAOs;

import java.util.List;
import java.util.UUID;

import server.model.User;

/**
 * This interface defines all needed operations to enable interaction
 * with a database for users
 * @author jchip
 *
 */
public interface IUserDAO {

	public void addUser(User user) throws DatabaseException;
	public void deleteUser(User user) throws DatabaseException; // Not needed
	public void updateUserPassword(User user) throws DatabaseException; // Not needed
	public User getUser(User user) throws DatabaseException; // Not needed
	public List<User> getAllUsers() throws DatabaseException;
}
