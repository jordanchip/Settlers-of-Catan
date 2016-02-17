package server.commands;

import java.io.Serializable;

public interface SerializableCatanCommand extends Serializable {
	
	ICatanCommand getCommand();

}
