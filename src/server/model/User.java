package server.model;

import java.io.Serializable;
import java.util.*;

import shared.exceptions.NameAlreadyInUseException;
import shared.exceptions.UserException;

/** A class representing a user.
 * There should be only one user object per username.
 * @author Justin Snyder
 *
 */
public class User
implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 373204178843485095L;

	private static Map<String, User> userTable = new HashMap<String, User>();
	
	final private String username;
	private String password;
	
	/** Creates a NEW user
	 * @param username
	 * @param password
	 * @throws NameAlreadyInUseException 
	 */
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	/** Registers a user with the given username and password
	 * @param username
	 * @param password
	 * @return
	 * @throws NameAlreadyInUseException
	 */
	public static User register(String username, String password) throws NameAlreadyInUseException {
		if (userTable.containsKey(username)) {
			throw new NameAlreadyInUseException(username);
		}
		User user = new User(username, password);
		userTable.put(username, user);
		return user;
	}
	
	/** Registers a user with the given username and password
	 * @param username
	 * @param password
	 * @return
	 * @throws NameAlreadyInUseException
	 */
	public static User register(User otherUser) throws NameAlreadyInUseException {
		if (userTable.containsKey(otherUser.username)) {
			throw new NameAlreadyInUseException(otherUser.username);
		}
		userTable.put(otherUser.username, otherUser);
		return otherUser;
	}
	
	/**
	 * @param username
	 * @param password
	 * @return
	 * @throws UserException
	 */
	public static User login(String username, String password) throws UserException {
		if (userTable.containsKey(username)) {
			User user = userTable.get(username);
			if (password.equals(user.password)) {
				return user;
			}
			else {
				throw new UserException("Incorrect Password.");
			}
		}
		else {
			throw new UserException("No such user: " + username);
		}
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	
	@Override
	public String toString() {
		return "username: " + username + "; password: " + password;
	}

}
