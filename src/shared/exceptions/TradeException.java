package shared.exceptions;

/**
 * Occurs when one player attempts to trade more resources than he has available to offer.
 * @author Jordan
 *
 */
public class TradeException extends InsufficientResourcesException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6723403614201481094L;

	public TradeException(String string) {
		super(string);
	}

	public TradeException() {
		super();
	}

}
