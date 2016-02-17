package shared.communication;

/** Used for the /game/commands route...
 * Information is pretty sparse. I have no idea how to specialize this.
 * I just figure it's a good idea to make it immutable.
 * @author Justin Snyder
 *
 */
public class Command {
	
	private Object commandData; // I have no idea what is in this...
	
	public Command(Object cmd) {
		commandData = cmd;
	}
	
	public Object getData() {
		return commandData;
	}

}
