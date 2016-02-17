package server.DAOs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import shared.communication.GameHeader;
import shared.model.ModelFacade;

public class MockGameDAO implements IGameDAO {

	@Override
	public void updateGamebyUUID(UUID gameUUID, ModelFacade model)
			throws DatabaseException {
	}

	@Override
	public ModelFacade getGame(UUID gameUUID) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GameHeader> getGameList() {
		return new ArrayList<>();
	}

	@Override
	public void addGame(UUID uuid, ModelFacade model) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeGame(UUID gameUUID) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<UUID, ModelFacade> getAllGames() throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

}
