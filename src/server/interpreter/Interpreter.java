package server.interpreter;

public interface Interpreter {
	
	/**
	 * @param command
	 * @param args
	 * @return true if the connection should be kept open, false if it should be closed
	 */
	void interpret(String line);
	
	void onOpen();
	void onClose();
	void prompt();

	boolean isActive();

}
