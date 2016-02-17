package shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.simple.JSONObject;

import shared.definitions.TurnStatus;
import shared.exceptions.InvalidActionException;
import shared.exceptions.SchemaMismatchException;

/*Keeps track of whose turn it is, as well what part of their turn it is.*/
/**
 * Keeps track of whose turn it is, as well as what part of their turn it is.
 * @author Jordan
 *
 */
public class TurnTracker 
	implements Serializable {
	private static final long serialVersionUID = -5039715952423064946L;

	private transient List<Player> players;
	
	private PlayerReference currentPlayer;
	private TurnStatus status;
	
	public TurnTracker() {
		status = TurnStatus.FirstRound;
	}

	public TurnTracker(List<Player> players) {
		this.players = new ArrayList<>(players);
		
		currentPlayer = players.get(0).getReference();
		status = TurnStatus.FirstRound;
	}
	
	public TurnTracker(List<Player> players, JSONObject json) throws SchemaMismatchException {
		this.players = new ArrayList<>(players);
		try {
			JSONObject curPlayer = (JSONObject) json.get("currentPlayer");
			
			if (curPlayer != null) {
				String temp = (String) curPlayer.get("playerUUID");
				UUID temp2 = UUID.fromString(temp);
				currentPlayer = new PlayerReference(temp2);
			}
			status = TurnStatus.fromString((String) json.get("status"));
		}
		catch (ClassCastException | IllegalArgumentException | NullPointerException e) {
			e.printStackTrace();
			throw new SchemaMismatchException("The JSON does not follow the expected schema " +
					"for a TurnTracker:\n" + json.toJSONString());
		}
	}

	/**
	 * @return the status
	 */
	public TurnStatus getStatus() {
		return status;
	}
	
	/**
	 * @param status the status to set
	 */
	void setStatus(TurnStatus status) {
		this.status = status;
	}

	void roll(int roll) {
		if (roll == 7) {
			boolean discardNeeded = false;
			for (Player player : players) {
				if (player.getResources().count() >= 8) {
					discardNeeded = true;
					player.setHasDiscarded(false);
				}
				else {
					player.setHasDiscarded(true);
				}
			}
			if (discardNeeded) {
				setStatus(TurnStatus.Discarding);
			}
			else {
				setStatus(TurnStatus.Robbing);
			}
		}
		else {
			setStatus(TurnStatus.Playing);
		}
	}
	
	// Needed because players is transient
	public void setPlayerList(List<Player> players) {
		this.players = players;
	}

	/**
	 * @return the currentPlayer
	 */
	public PlayerReference getCurrentPlayer() {
		return currentPlayer;
	}
	
	public void setCurrentPlayer(PlayerReference currentPlayer) {
		this.currentPlayer = currentPlayer;
	}
	
	/** Passes the turn to the next player
	 * @throws InvalidActionException if the current player cannot pass their turn
	 * @pre The current player has finished all mandatory actions
	 * @post Control is passed onto the next player
	 */
	public void passTurn() throws InvalidActionException {
		assert players != null;
		
		int currentPlayerIndex = currentPlayer.getIndex();
		switch(status) {
		case FirstRound:
			if (currentPlayerIndex == 3) {
				status = TurnStatus.SecondRound;
			}
			else {
				currentPlayer = players.get((currentPlayerIndex + 1) % 4).getReference();
			}
			break;
		case SecondRound:
			if (currentPlayerIndex == 0) {
				status = TurnStatus.Rolling;
				currentPlayer.getPlayer().setHasRolled(false);
			}
			else {
				currentPlayer = players.get((currentPlayerIndex + 3) % 4).getReference();
			}
			break;
		case Playing:
			assert currentPlayer.getPlayer().hasRolled();
			currentPlayer.getPlayer().ageDevCards();
			currentPlayer = players.get((currentPlayerIndex + 1) % 4).getReference();
			status = TurnStatus.Rolling;
			currentPlayer.getPlayer().setHasRolled(false);
			break;
		default:
			throw new InvalidActionException();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TurnTracker [currentPlayer=" + currentPlayer + ", status="
				+ status + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((currentPlayer == null) ? 0 : currentPlayer.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TurnTracker other = (TurnTracker) obj;
		if (currentPlayer == null) {
			if (other.currentPlayer != null)
				return false;
		} else if (!currentPlayer.equals(other.currentPlayer))
			return false;
		if (status != other.status)
			return false;
		return true;
	}	
	
}

