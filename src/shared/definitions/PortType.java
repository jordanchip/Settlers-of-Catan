package shared.definitions;

public enum PortType
{
	
	WOOD, BRICK, SHEEP, WHEAT, ORE, THREE;

	public static PortType fromResourceType(ResourceType resource) {
		if (resource == null) return THREE;
		
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

