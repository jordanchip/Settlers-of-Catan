package client.login;

import client.base.*;

import java.util.logging.*;

import client.misc.*;
import shared.communication.IServer;
import shared.communication.Session;
import shared.exceptions.ServerException;
import shared.exceptions.UserException;


/**
 * Implementation for the login controller
 */
public class LoginController extends Controller implements ILoginController {

	private IMessageView messageView;
	private IAction loginAction;
	//private IServer serverProxy = ServerProxy.getInstance();
	private IServer serverProxy = ClientManager.getServer();
	//private ModelFacade modelFacade = ModelFacade.getInstance();
	Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	/**
	 * LoginController constructor
	 * 
	 * @param view Login view
	 * @param messageView Message view (used to display error messages that occur during the login process)
	 */
	public LoginController(ILoginView view, IMessageView messageView) {

		super(view);
		
		this.messageView = messageView;
	}
	
	public ILoginView getLoginView() {
		
		return (ILoginView)super.getView();
	}
	
	public IMessageView getMessageView() {
		
		return messageView;
	}
	
	/**
	 * Sets the action to be executed when the user logs in
	 * 
	 * @param value The action to be executed when the user logs in
	 */
	public void setLoginAction(IAction value) {
		
		loginAction = value;
	}
	
	/**
	 * Returns the action to be executed when the user logs in
	 * 
	 * @return The action to be executed when the user logs in
	 */
	public IAction getLoginAction() {
		
		return loginAction;
	}

	@Override
	public void start() {
		
		getLoginView().showModal();
	}

	@Override
	public void signIn() {
		
		String username = getLoginView().getLoginUsername();
		String password = getLoginView().getLoginPassword();

		try {
			Session player = serverProxy.login(username, password);
			ClientManager.setSession(player);
			logger.log(Level.INFO, "Login was successful");
			// If log in succeeded
			getLoginView().closeModal();
			loginAction.execute();
		} catch (UserException e) {
			messageView.setTitle("Invalid credentials");
			messageView.setMessage("Invalid username/password. Please try again.");
			messageView.showModal();
		} catch (ServerException e) {
			messageView.setTitle("Server Error");
			messageView.setMessage("Unable to reach server at this point");
			messageView.showModal();
		}

	}

	@Override
	public void register() {
		
		String username = getLoginView().getRegisterUsername();
		String password = getLoginView().getRegisterPassword();
		String repeatPassword = getLoginView().getRegisterPasswordRepeat();
		
		//verifies that username is correct length
		if(username.length() < 3 || username.length() > 7){
			warningMessage();
			return;
		}
		
		//verifies that password is correct length
		if(password.length() < 5){
			warningMessage();
			return;
		}
		
		//Check if the passwords are the same
		if (!password.equals(repeatPassword)) {
			warningMessage();
			return;
		}
		
		//Check if the username contains valid characters
		if(invalid(username)){
			warningMessage();
			return;
		}
		
		//Check if the password contains valid characters
		if(invalid(password)){
			warningMessage();
			return;
		}
		
		try {
			Session player = serverProxy.register(username, password);
			ClientManager.setSession(player);
			System.out.println("Register was successful");
			logger.log(Level.INFO, "Register was successful");
			// If register succeeded
			getLoginView().closeModal();
			loginAction.execute();
		} catch (UserException e) {
			messageView.setTitle("Username taken");
			messageView.setMessage("User already exists. Please try again.");
			messageView.showModal();
		} catch (ServerException e) {
			messageView.setTitle("Server Error");
			messageView.setMessage("Unable to reach server at this point");
			messageView.showModal();
		}
	}
	
	public void warningMessage(){
		messageView.setTitle("Warning");
		messageView.setMessage("Invalid username or password");
		messageView.showModal();
	}
	
	public boolean invalid(String word){
		for(int i = 0; i < word.length(); ++i){
			if(Character.isAlphabetic(word.charAt(i))){
				continue;
			}
			if(Character.isDigit(word.charAt(i))){
				continue;
			}
			if(word.charAt(i) == '-'){
				continue;
			}
			if(word.charAt(i) == '_'){
				continue;
			}
			return true;
		}
		return false;
	}

}

