package server.DAOs;

import java.util.ArrayList;
import java.util.List;

import server.model.User;

public class MockUserDAO implements IUserDAO {

	@Override
	public void addUser(User user) throws DatabaseException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteUser(User user) throws DatabaseException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateUserPassword(User user) throws DatabaseException {
		// TODO Auto-generated method stub

	}

	@Override
	public User getUser(User user) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> getAllUsers() throws DatabaseException {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

}
