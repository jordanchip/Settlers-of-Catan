package server.commands;

import shared.exceptions.InvalidActionException;
import shared.model.ModelFacade;

/** An interface representing an operation on a ModelFacade. Can be serialized.
 * Implementors are expected to be immutable.
 * Implementors should also implement a constructor that takes a JSONObject.
 * @author Justin Snyder
 *
 */
public interface ICatanCommand {

	/** Executes the command on the given ModelFacade
	 * @param model
	 * @throws InvalidActionException if the command was not legally executable.
	 */
	void execute(ModelFacade model) throws InvalidActionException;
	
	SerializableCatanCommand getSerializable();
	
}
