package shared.locations;

public enum EdgeDirection
{
	
	NorthWest, North, NorthEast, SouthEast, South, SouthWest;
	
	private EdgeDirection opposite;
	private String symbolString;
	
	static
	{
		NorthWest.opposite = SouthEast;
		North.opposite = South;
		NorthEast.opposite = SouthWest;
		SouthEast.opposite = NorthWest;
		South.opposite = North;
		SouthWest.opposite = NorthEast;
		
		NorthWest.symbolString = "NW";
		North.symbolString = "N";
		NorthEast.symbolString = "NE";
		SouthEast.symbolString = "SE";
		South.symbolString = "S";
		SouthWest.symbolString = "SW";
	}
	
	public EdgeDirection getOppositeDirection()
	{
		return opposite;
	}
	
	public static EdgeDirection getDirectionFromString(String input) {
		String lowerInput = input.toLowerCase();
		if      (lowerInput.equals("nw") || lowerInput.equals("northwest")) return NorthWest;
		else if (lowerInput.equals("n")  || lowerInput.equals("north"))     return North;
		else if (lowerInput.equals("ne") || lowerInput.equals("northeast")) return NorthEast;
		else if (lowerInput.equals("se") || lowerInput.equals("southeast")) return SouthEast;
		else if (lowerInput.equals("s")  || lowerInput.equals("south"))     return South;
		else if (lowerInput.equals("sw") || lowerInput.equals("southwest")) return SouthWest;
		else throw new IllegalArgumentException();
	}

	public Object getSymbolString() {
		return symbolString;
	}
}

