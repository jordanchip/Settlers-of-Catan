package server.DAOs;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import shared.communication.GameHeader;
import shared.model.ModelFacade;

/**
 * This interface defines all needed operations to enable interaction
 * with a database for users
 * @author jchip
 *
 */
public interface IGameDAO {

	public void addGame(UUID uuid, ModelFacade model) throws DatabaseException;
	public void removeGame(UUID gameUUID) throws DatabaseException; // Not Needed
	public void updateGamebyUUID(UUID gameUUID, ModelFacade model) throws DatabaseException;
	public ModelFacade getGame(UUID gameUUID) throws DatabaseException;
	public List<GameHeader> getGameList() throws DatabaseException;
	Map<UUID, ModelFacade> getAllGames() throws DatabaseException; // Not needed
}
