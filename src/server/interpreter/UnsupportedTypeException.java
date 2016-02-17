package server.interpreter;

@SuppressWarnings("serial")
public class UnsupportedTypeException extends Exception {

	public UnsupportedTypeException(Class<?> fromType, Class<?> toType) {
		super("Unsupported conversion from " + fromType + " to " + toType);
	}
	
}
