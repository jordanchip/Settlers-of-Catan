package shared.definitions;

public enum ResourceType
{
	WOOD, BRICK, SHEEP, WHEAT, ORE;
	
	static public ResourceType fromString(String input) throws IllegalArgumentException{
		String lowerInput = input.toLowerCase();
		if (lowerInput.equals("wood")) return WOOD;
		else if (lowerInput.equals("brick")) return BRICK;
		else if (lowerInput.equals("sheep")) return SHEEP;
		else if (lowerInput.equals("wheat")) return WHEAT;
		else if (lowerInput.equals("ore")) return ORE;
		else throw new IllegalArgumentException();
	}
}

