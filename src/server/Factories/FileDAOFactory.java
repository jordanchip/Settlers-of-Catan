package server.Factories;

import java.util.List;
import java.util.UUID;

import server.DAOs.FileCommandDAO;
import server.DAOs.FileGameDAO;
import server.DAOs.FileUserDAO;
import server.DAOs.ICommandDAO;
import server.DAOs.IGameDAO;
import server.DAOs.IUserDAO;
import server.commands.CatanCommand;
import server.model.User;
import shared.communication.GameHeader;
import shared.communication.Session;
import shared.definitions.CatanColor;
import shared.model.ModelFacade;
import shared.model.PlayerReference;

/**
 * This factory contains references to all the
 * DAOs necessary to store the necessary model into
 * a basic file-based database.
 * @author jchip
 *
 */
public class FileDAOFactory implements IDAOFactory {
	
	public static void main(String[] args){
		FileDAOFactory factory = new FileDAOFactory();
		IUserDAO user = factory.getUserDAO();
		IGameDAO game = factory.getGameDAO();
		ICommandDAO command = factory.getCommandDAO();
		
		try{
			user.addUser(new User("Steve", "steve"));
			user.addUser(new User("Bob", "bob"));
			List<User> users = user.getAllUsers();
			System.out.println("getAllUsers:");
			System.out.println(users);
			System.out.println();
			
			UUID uuid = UUID.randomUUID();
			ModelFacade model = new ModelFacade();
			game.addGame(uuid, model);
			uuid = UUID.randomUUID();
			model = new ModelFacade();
			game.addGame(uuid, model);
			model.addPlayer(new Session("Steve", "steve", UUID.randomUUID()), CatanColor.PURPLE);
			game.updateGamebyUUID(uuid, model);
			List<GameHeader> games = game.getGameList();
			System.out.println("getGameList:");
			System.out.println(games);
			System.out.println();
			
			System.out.println("games[0]:");
			System.out.println(games.get(0).getUUID().toString());
			System.out.println();
			
			System.out.println("getGame:");
			System.out.println(game.getGame(uuid));
			System.out.println();
			
			command.addCommand(uuid, new CatanCommand("rollDice", new PlayerReference(UUID.randomUUID()), 5));
			command.addCommand(uuid, new CatanCommand("rollDice", new PlayerReference(UUID.randomUUID()), 5));
			command.addCommand(uuid, new CatanCommand("rollDice", new PlayerReference(UUID.randomUUID()), 5));
			command.addCommand(uuid, new CatanCommand("rollDice", new PlayerReference(UUID.randomUUID()), 5));

			System.out.println("getAll:");
			System.out.println(command.getAll(uuid));
			System.out.println();
			command.clearCommands(uuid);
			System.out.println("getAll:");
			System.out.println(command.getAll(uuid));
			System.out.println();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	IUserDAO userDAO;
	IGameDAO gameDAO;
	ICommandDAO commandDAO;

	public FileDAOFactory(){
		userDAO = new FileUserDAO();
		gameDAO = new FileGameDAO();
		commandDAO = new FileCommandDAO();
	}
	
	@Override
	public IUserDAO getUserDAO() {
		return userDAO;
	}

	@Override
	public IGameDAO getGameDAO() {
		return gameDAO;
	}

	@Override
	public ICommandDAO getCommandDAO() {
		return commandDAO;
	}

	@Override
	public void startTransaction() {}

	@Override
	public void endTransaction(boolean commit) {}

}
