package server.DAOs;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import shared.communication.GameHeader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import server.model.User;
import shared.model.ModelFacade;

/**
 * This class contains implementation for storing, deleting, and updating
 * games in a SQL-based relational database system. 
 * @author jchip
 *
 */
public class SQLGameDAO implements IGameDAO {

	private static Logger logger;
	
	static {
		logger = Logger.getLogger("RecordIndexer");
	}

	private SQLDatabase db;
	
	/**
	 * Creates an instance of a CellDAO to interact with the database.
	 */
	public SQLGameDAO(SQLDatabase db) {
		this.db = db;
	}

	@Override
	public void addGame(UUID uuid, ModelFacade model) throws DatabaseException {
		PreparedStatement stmt = null;
		ResultSet keyRS = null;
		try {
			String query = "insert into games (uuid, game, header) values (?, ?, ?)";
			stmt = db.getConnection().prepareStatement(query);
			stmt.setString(1, uuid.toString());
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(model);
			byte[] bytes = baos.toByteArray();
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			
			stmt.setBinaryStream(2, bais, bytes.length);
			
			ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
			ObjectOutputStream oos2 = new ObjectOutputStream(baos2);
			oos2.writeObject(model.getGameHeader());
			byte[] bytes2 = baos2.toByteArray();
			ByteArrayInputStream bais2 = new ByteArrayInputStream(bytes2);
			
			stmt.setBinaryStream(3, bais2, bytes2.length);
			if (stmt.executeUpdate() != 1) {
				throw new DatabaseException("Could not add game.");
			}
		}
		catch (SQLException e) {
			throw new DatabaseException("Could not add game", e);
		}
		catch (NullPointerException e) {
			throw new NullPointerException("Connection object is null (Did you forget start/end transaction?)");
		} catch (IOException e) {
			e.printStackTrace();
			throw new DatabaseException("IOException");
		}
		finally{
			SQLDatabase.safeClose(keyRS);
			SQLDatabase.safeClose(stmt);
		}
	}

	@Override
	public void removeGame(UUID gameUUID) throws DatabaseException {
		PreparedStatement stmt = null;
		ResultSet keyRS = null;
		try {
			String query = "delete from games where uuid=?";
			stmt = db.getConnection().prepareStatement(query);
			stmt.setString(1, gameUUID.toString());
			
			if (stmt.executeUpdate() != 1) {
				throw new DatabaseException("Could not add game.");
			}
		}
		catch (SQLException e) {
			throw new DatabaseException("Could not add game", e);
		}
		catch (NullPointerException e) {
			throw new NullPointerException("Connection object is null (Did you forget start/end transaction?)");
		}
		finally{
			SQLDatabase.safeClose(keyRS);
			SQLDatabase.safeClose(stmt);
		}
	}

	@Override
	public void updateGamebyUUID(UUID gameUUID, ModelFacade model) {
		try {
			db.startTransaction();
			removeGame(gameUUID);
			addGame(gameUUID, model);
			db.endTransaction(true);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public ModelFacade getGame(UUID gameUUID) throws DatabaseException {
		ModelFacade returnModelFacade = null;
		
		PreparedStatement stmt = null;
		ResultSet rs = null;		
		
		try {
			String query = "select game from games where uuid = ?";
			stmt = db.getConnection().prepareStatement(query);
			stmt.setString(1, gameUUID.toString());
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				InputStream blobStream = new ByteArrayInputStream(rs.getBytes(1));
				ObjectInputStream objStream = new ObjectInputStream(blobStream);
				returnModelFacade = (ModelFacade) objStream.readObject();
			}
		}
		catch (SQLException | ClassNotFoundException | IOException e) {
			throw new DatabaseException("Could not get game", e);
		}
		finally {
			SQLDatabase.safeClose(rs);
			SQLDatabase.safeClose(stmt);
		}
		return returnModelFacade;
	}

	@Override
	public List<GameHeader> getGameList() throws DatabaseException {
		List<GameHeader> headers = new ArrayList<>();
		PreparedStatement stmt = null;
		ResultSet rs = null;		
		
		try {
			String query = "select header from games";
			stmt = db.getConnection().prepareStatement(query);
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				InputStream blobStream = new ByteArrayInputStream(rs.getBytes(1));
				ObjectInputStream objStream = new ObjectInputStream(blobStream);
				headers.add((GameHeader) objStream.readObject());
			}
		}
		catch (SQLException | ClassNotFoundException | IOException e) {
			throw new DatabaseException("Could not get games", e);
		}
		finally {
			SQLDatabase.safeClose(rs);
			SQLDatabase.safeClose(stmt);
		}
		return headers;
	}

	@Override
	public Map<UUID, ModelFacade> getAllGames() throws DatabaseException {
		Map<UUID, ModelFacade> games = new HashMap<UUID, ModelFacade>();
		
		PreparedStatement stmt = null;
		ResultSet rs = null;		
		
		try {
			String query = "select game from games";
			stmt = db.getConnection().prepareStatement(query);
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				UUID uuid = UUID.fromString(rs.getString(2));
				ModelFacade game = (ModelFacade) rs.getBlob(3);
				
				games.put(uuid, game);
			}
		}
		catch (SQLException e) {
			throw new DatabaseException("Could not get games", e);
		}
		finally {
			SQLDatabase.safeClose(rs);
			SQLDatabase.safeClose(stmt);
		}
		return games;
	}

}
