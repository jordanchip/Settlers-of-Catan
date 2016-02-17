package shared.definitions;

public enum TurnStatus {
	Rolling,
	Robbing,
	Playing,
	Discarding,
	FirstRound,
	SecondRound;

	public static TurnStatus fromString(String input) {
		String lowerInput = input.toLowerCase();
		if (lowerInput.equals("rolling")) return Rolling;
		else if (lowerInput.equals("robbing")) return Robbing;
		else if (lowerInput.equals("playing")) return Playing;
		else if (lowerInput.equals("discarding")) return Discarding;
		else if (lowerInput.equals("firstround")) return FirstRound;
		else if (lowerInput.equals("secondround")) return SecondRound;
		else throw new IllegalArgumentException();
	}
}
