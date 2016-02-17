package server.cheat;

import java.io.OutputStream;

import server.interpreter.Command;
import server.interpreter.GenericInterpreter;
import server.telnet.*;
import shared.definitions.DevCardType;

public class CatanCheatHandler extends GenericInterpreter {

	private static final int CHEATS_DISABLED = 0;
	private static final int CHEATS_ENABLED = 1;
	
	private final String cheatCode;

	public static void main(String[] args) throws Exception {
		TelnetServer server = new TelnetServer(
				new GenericInterpreterFactory(CatanCheatHandler.class, "qwerty"));

		server.run();
	}

	public CatanCheatHandler(OutputStream ostream) {
		super(ostream);
		
		cheatCode = "secret";
	}

	public CatanCheatHandler(OutputStream ostream, String code) {
		super(ostream);
		
		cheatCode = code;
	}
	
	@Override
	protected String resultString() {
		return "==> ";
	}
	
	@Override
	protected String accessDeniedString() {
		return "Cheats are disabled!";
	}
	
	
	@Override
	protected String requirementDescription(int runLevel) {
		switch(runLevel) {
		case CHEATS_DISABLED:
			return "Cheats do not need to be enabled.";
		case CHEATS_ENABLED:
			return "Cheats must be enabled.";
		default:
			return super.requirementDescription(runLevel);
		}
	}

	@Command(args = {"<code>"}, info = "Enables cheats if the cheat code is correct.")
	public void cheat(String code) {
		if (code.equals(cheatCode)) {
			setAccessLevel(CHEATS_ENABLED);
			getWriter().println("Cheats enabled!");
		} else {
			getWriter().println("Invalid cheat code.");
		}
	}
	
	@Command(info = "Disables cheats.", runLevel = CHEATS_ENABLED)
	public void uncheat() {
		setAccessLevel(CHEATS_DISABLED);
		getWriter().println("Cheats disabled.");
	}

	@Command(args = {"<game id>", "<roll...>"}, info = "Sets the next roll(s).", runLevel = CHEATS_ENABLED)
	public void setRoll(int gameid, Integer... rolls) {
		// TODO: in the future this will actually do something
		for (int roll : rolls) getWriter().println(roll);
		getWriter().println("Success-ish!");
	}

	@Command(args = {"<game id>", "<card...>"}, info = "Sets the next dev card(s).", runLevel = CHEATS_ENABLED)
	public void setDevCard(int gameid, DevCardType... cards) {
		for (DevCardType card : cards) getWriter().println(card);
		getWriter().println("Success-ish!");
	}
	
	/*@Command(args = {"<game id>", "<player id>", "<resource>", "<amount>"}, runLevel = CHEATS_ENABLED,
			info = "gives the amount of a given resource to the given player of a game")
	public void giveResources(int gameid, int player, ResourceType resource, int amount) {
		
	}*/
	
	@Command(info = "saves a game to a file", runLevel = CHEATS_ENABLED, viewLevel = CHEATS_ENABLED)
	public void save(int gameid, String savename) {
		
	}
	
	@Command(info = "saves a game to a file", runLevel = CHEATS_ENABLED, viewLevel = CHEATS_ENABLED)
	public void load(int gameid, String savename) {
		
	}
	
	@Override
	public void echo(String... strings) {
		// This erases echo as a command
	}

}
