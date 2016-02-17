package shared.exceptions;

/**
 * An exception representing an action being unable to take place due to something lacking the
 * resources to do so.
 * @author Justin
 */
public class InsufficientResourcesException extends InvalidActionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5177392636821001798L;

	public InsufficientResourcesException(String string) {
		super(string);
	}

	public InsufficientResourcesException() {
		super();
	}

}
