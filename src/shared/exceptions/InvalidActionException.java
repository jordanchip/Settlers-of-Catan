package shared.exceptions;

/**
 * A general exception that occurs when a player is trying to perform an illegal action.
 * @author Jordan
 *
 */
public class InvalidActionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4748472084760753942L;

	public InvalidActionException(String string) {
		super(string);
	}

	public InvalidActionException() {
		super();
	}

}
