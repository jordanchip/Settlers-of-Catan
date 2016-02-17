package shared.definitions;

public enum HexType
{
	
	WOOD, BRICK, SHEEP, WHEAT, ORE, DESERT, WATER;

	public static HexType fromResourceType(ResourceType resource) {
		if (resource == null) return DESERT;
		switch (resource) {
		case WOOD:
			return WOOD;
		case BRICK:
			return BRICK;
		case SHEEP:
			return SHEEP;
		case WHEAT:
			return WHEAT;
		case ORE:
			return ORE;
		default:
			return null;
		}
	}
}

