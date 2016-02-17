package shared.locations;

public enum VertexDirection
{
	West, NorthWest, NorthEast, East, SouthEast, SouthWest;
	
	private VertexDirection opposite;
	private String symbolString;
	
	static
	{
		West.opposite = East;
		NorthWest.opposite = SouthEast;
		NorthEast.opposite = SouthWest;
		East.opposite = West;
		SouthEast.opposite = NorthWest;
		SouthWest.opposite = NorthEast;

		West.symbolString = "W";
		NorthWest.symbolString = "NW";
		NorthEast.symbolString = "NE";
		East.symbolString = "E";
		SouthEast.symbolString = "SE";
		SouthWest.symbolString = "SW";
	}
	
	public VertexDirection getOppositeDirection()
	{
		return opposite;
	}
	
	public static VertexDirection getDirectionFromString(String input) {
		String lowerInput = input.toLowerCase();
		if      (lowerInput.equals("nw") || lowerInput.equals("northwest")) return NorthWest;
		else if (lowerInput.equals("w")  || lowerInput.equals("north"))     return West;
		else if (lowerInput.equals("ne") || lowerInput.equals("northeast")) return NorthEast;
		else if (lowerInput.equals("sw") || lowerInput.equals("southwest")) return SouthWest;
		else if (lowerInput.equals("e")  || lowerInput.equals("south"))     return East;
		else if (lowerInput.equals("se") || lowerInput.equals("southeast")) return SouthEast;
		else throw new IllegalArgumentException();
	}

	public Object getSymbolString() {
		return symbolString;
	}
}

