package shared.communication;

import java.util.UUID;

/** This class represents a user session, stores session info, and converts to and from session cookies.
 *
 */
public class Session {
	private String username;
	private String password;
	private UUID playerUUID;
	
	/**
	 * @param username The player's username
	 * @param password The player's password
	 * @param playerID Their unique player ID
	 */
	public Session(String username, String password, UUID playerUUID) {
		this.username = username;
		this.password = password;
		this.playerUUID = playerUUID;
	}
	
	/** Creates a session from a cookie
	 * @param cookie the cookie
	 */
	public Session(String cookie) {
		
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

	/**
	 * @return the playerID
	 */
	public UUID getPlayerUUID() {
		return playerUUID;
	}
	
	/**
	 * @return The URL-encoded cookie representing the session
	 */
	public String getCookie() {
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result + playerUUID.hashCode();
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Session other = (Session) obj;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (playerUUID != other.playerUUID)
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	

}