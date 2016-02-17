package server.DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import server.Factories.IDAOFactory;
import server.Factories.SQLDAOFactory;
import server.commands.CatanCommand;
import server.commands.CommandSerializationException;
import server.commands.CommandSerializer;
import server.commands.ICatanCommand;
import shared.locations.EdgeDirection;
import shared.locations.EdgeLocation;
import shared.locations.VertexDirection;
import shared.locations.VertexLocation;
import shared.model.PlayerReference;

public class SQLCommandDAO implements ICommandDAO {

	private static Logger logger = Logger.getLogger("Catan-SQL");
	
	public static void main(String[] args) throws Exception {		
		IDAOFactory factory = new SQLDAOFactory();
		
		ICommandDAO dao = factory.getCommandDAO();

		UUID gameid = UUID.randomUUID();
		PlayerReference justin, jordan, grant, steve;
		justin = new PlayerReference(UUID.randomUUID());
		jordan = new PlayerReference(UUID.randomUUID());
		grant = new PlayerReference(UUID.randomUUID());
		steve = new PlayerReference(UUID.randomUUID());
		
		dao.addCommand(gameid, new CatanCommand("buildStartingPieces", justin,
				new VertexLocation(0, 0, VertexDirection.West),
				new EdgeLocation(0, 0, EdgeDirection.NorthWest)));
		dao.addCommand(gameid, new CatanCommand("buildStartingPieces", jordan,
				new VertexLocation(0, 0, VertexDirection.NorthEast),
				new EdgeLocation(0, 0, EdgeDirection.North)));
		dao.addCommand(gameid, new CatanCommand("buildStartingPieces", grant,
				new VertexLocation(0, 0, VertexDirection.SouthEast),
				new EdgeLocation(0, 0, EdgeDirection.South)));
		dao.addCommand(gameid, new CatanCommand("buildStartingPieces", steve,
				new VertexLocation(1, 1, VertexDirection.West),
				new EdgeLocation(1, 1, EdgeDirection.NorthWest)));
		dao.addCommand(gameid, new CatanCommand("buildStartingPieces", steve,
				new VertexLocation(1, 1, VertexDirection.SouthEast),
				new EdgeLocation(1, 1, EdgeDirection.South)));
		dao.addCommand(gameid, new CatanCommand("buildStartingPieces", grant,
				new VertexLocation(1, 1, VertexDirection.NorthEast),
				new EdgeLocation(1, 1, EdgeDirection.North)));
		dao.addCommand(gameid, new CatanCommand("buildStartingPieces", jordan,
				new VertexLocation(-1, -1, VertexDirection.West),
				new EdgeLocation(-1, -1, EdgeDirection.NorthWest)));
		dao.addCommand(gameid, new CatanCommand("buildStartingPieces", justin,
				new VertexLocation(-1, -1, VertexDirection.NorthEast),
				new EdgeLocation(-1, -1, EdgeDirection.North)));
		
		System.out.println(dao.getAll(gameid));
		
		dao.clearCommands(gameid);
		
		System.out.println(dao.getAll(gameid));
	}

	private SQLDatabase db;
	
	/**
	 * Creates an instance of a CellDAO to interact with the database.
	 */
	public SQLCommandDAO(SQLDatabase db) {
		this.db = db;
	}

	@Override
	public void addCommand(UUID gameid, ICatanCommand command) throws DatabaseException {
		try {
			Connection conn = db.getConnection();
			PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO commands (gameid, command) " +
					"VALUES (?, ?)");

			stmt.setString(1, gameid.toString());
			stmt.setBytes(2, CommandSerializer.serializeBytes(command));
			if (stmt.executeUpdate() != 1) {
				throw new DatabaseException("Failed to store the command.");
			}
			
			stmt.close();
			
			logger.fine("Saved command to game " + gameid.toString());
		} catch (SQLException | CommandSerializationException e) {
			throw new DatabaseException("Failed to store the command.", e);
		}
	}

	@Override
	public void clearCommands(UUID gameid) throws DatabaseException {
		try {
			PreparedStatement stmt = db.getConnection().prepareStatement(
					"DELETE FROM commands WHERE gameid = ?");
			
			stmt.setString(1, gameid.toString());

			stmt.executeUpdate();
			
			stmt.close();

			logger.fine("Cleared commands from game " + gameid.toString());
		} catch (SQLException e) {
			throw new DatabaseException("Failed to delete commands from the game.", e);
		}
	}

	@Override
	public List<ICatanCommand> getAll(UUID gameid) throws DatabaseException {
		try {
			List<ICatanCommand> commands = new ArrayList<>();
			PreparedStatement stmt = db.getConnection().prepareStatement(
					"SELECT command FROM commands WHERE gameid = ? ORDER BY id");
			
			stmt.setString(1, gameid.toString());
			
			ResultSet results = stmt.executeQuery();
			
			while(results.next()) {
				commands.add(CommandSerializer.deserializeBytes(results.getBytes(1)));
			}
			
			results.close();
			stmt.close();
			
			logger.fine("Obtained " + commands.size() + " commands for game " + gameid.toString());
			
			return commands;
		} catch (SQLException | CommandSerializationException e) {
			throw new DatabaseException("Failed to get commands for the game.", e);
		}
	}

}
