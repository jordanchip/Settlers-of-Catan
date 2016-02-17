package shared.exceptions;

/**
 * Occurs when an error happens during the initialization of the game.
 * @author Jordan
 *
 */
public class GameInitializationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6882423696286615099L;

	public GameInitializationException(String string) {
		super(string);
	}

	public GameInitializationException() {
		super();
	}

}
