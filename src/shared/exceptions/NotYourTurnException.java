package shared.exceptions;

/**
 * Occurs when a player attempts to perform a turn-specific action when
 * it is not their turn.
 * @author Jordan
 *
 */
public class NotYourTurnException extends InvalidActionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 677183430658834885L;

	public NotYourTurnException(String string) {
		super(string);
	}

	public NotYourTurnException() {
		super();
	}

}
