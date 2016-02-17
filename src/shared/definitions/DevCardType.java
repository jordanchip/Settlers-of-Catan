package shared.definitions;

public enum DevCardType
{
	SOLDIER, YEAR_OF_PLENTY, MONOPOLY, ROAD_BUILD, MONUMENT;
	
	private String stringRepr;
	
	static {
		SOLDIER.stringRepr = "soldier";
		YEAR_OF_PLENTY.stringRepr = "yearOfPlenty";
		MONOPOLY.stringRepr = "monopoly";
		ROAD_BUILD.stringRepr = "roadBuilding";
		MONUMENT.stringRepr = "monument";
	}
	
	public String toString() {
		return stringRepr;
	}
	
	public static DevCardType fromString(String input) {
		switch (input.toLowerCase()) {
		case "soldier":
			return SOLDIER;
		case "yearofplenty":
			return YEAR_OF_PLENTY;
		case "monopoly":
			return MONOPOLY;
		case "roadbuilding":
		case "roadbuild":
			return ROAD_BUILD;
		case "monument":
			return MONUMENT;
		default:
			return null;
		}
	}
}

