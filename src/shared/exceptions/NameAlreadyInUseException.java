package shared.exceptions;

/**
 * Occurs when the user tries to register with a username 
 * that is already registered to another user.
 * @author Jordan
 *
 */
public class NameAlreadyInUseException extends UserException {

	public NameAlreadyInUseException(String username) {
		super("The name \"" + username + "\" is already in use.");
	}

	/**
	 * 
	 */
	public NameAlreadyInUseException() {
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5532482530874231364L;

}
