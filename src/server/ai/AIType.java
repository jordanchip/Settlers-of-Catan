package server.ai;

public enum AIType {
	LARGEST_ARMY;
	
	private String stringRepr;
	
	static {
		LARGEST_ARMY.stringRepr = "LARGEST_ARMY";
	}
	
	static public AIType getTypeFromString(String input) throws IllegalArgumentException{
		String lowerInput = input.toLowerCase();
		if (lowerInput.equals("largest_army")) return LARGEST_ARMY;
		else throw new IllegalArgumentException();
	}

	public String toString() {
		return stringRepr;
	}
}
