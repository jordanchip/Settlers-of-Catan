package shared.exceptions;

/**
 * An exception that occurs when the user has invalid information
 * @author Jordan
 *
 */
public class UserException extends Exception {

	public UserException(String message) {
		super(message);
	}

	/**
	 * 
	 */
	public UserException() {
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8424304928224465252L;

}
