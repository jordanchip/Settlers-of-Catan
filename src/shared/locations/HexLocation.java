package shared.locations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.json.simple.JSONObject;

import shared.exceptions.SchemaMismatchException;

/**
 * An immutable representation of the location of a hex on a hex map
 */
public class HexLocation
implements Serializable 
{
	private static final long serialVersionUID = -6878967596753764682L;
	
	private int x;
	private int y;
	
	public HexLocation(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public static void main(String[] args) {
		for (HexLocation loc : locationsWithinRadius(3)) {
			System.out.printf("%s (%d)\n", loc.toString(), loc.getDistanceFromCenter());
		}
	}
	
	public HexLocation(JSONObject json) throws SchemaMismatchException {
		try {
			if (json.containsKey("hexLoc")) {
				json = (JSONObject)json.get("hexLoc");
			}
			x = (int) (long) json.get("x");
			y = (int) (long) json.get("y");
		}
		catch (ClassCastException | IllegalArgumentException e) {
			e.printStackTrace();
			throw new SchemaMismatchException("The JSON does not follow the expected schema " +
					"for a HexLocation:\n" + json.toJSONString());
		}
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();

		json.put("x", x);
		json.put("y", y);
		
		return json;
	}

	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	@Override
	public String toString()
	{
		return "HexLocation [x=" + x + ", y=" + y + "]";
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		HexLocation other = (HexLocation)obj;
		if(x != other.x)
			return false;
		if(y != other.y)
			return false;
		return true;
	}
	
	public HexLocation getNeighborLoc(EdgeDirection dir)
	{
		switch (dir)
		{
			case NorthWest:
				return new HexLocation(x - 1, y);
			case North:
				return new HexLocation(x, y - 1);
			case NorthEast:
				return new HexLocation(x + 1, y - 1);
			case SouthWest:
				return new HexLocation(x - 1, y + 1);
			case South:
				return new HexLocation(x, y + 1);
			case SouthEast:
				return new HexLocation(x + 1, y);
			default:
				assert false;
				return null;
		}
	}
	
	public boolean isAdjacent(HexLocation other) {
		if (this.equals(other)) return false;
		int dx, dy;
		dx = x - other.x;
		dy = y - other.y;
		// North/South
		if (dx == 0 && Math.abs(dy) == 1) return true;
		// NorthWest/SouthEast
		if (dy == 0 && Math.abs(dx) == 1) return true;
		// NorthEast/SouthWest
		if (dx == -dy && Math.abs(dx) == 1) return true;
		
		return false;
	}
	
	/** Tells you how many hexes away from the center this location is.
	 * @return the distance from the center. 0 if the location IS the center.
	 */
	public int getDistanceFromCenter() {
		// Check along the axes - easy
		if (x == 0) {
			return Math.abs(y);
		}
		else if (y == 0) {
			return Math.abs(x);
		}
		// And between the axes on the acute end- think of it as a square grid
		else if (Math.signum(x) == Math.signum(y)) {
			return Math.abs(x + y);
		}
		/* And the obtuse section is a little more... obtuse
		 * It's like the diagonals of the square grid.
		 * Draw a picture using the adjacency rules in getNeighborLoc()
		 * I promise, it works.
		 */
		else {
			return Math.max(Math.abs(x), Math.abs(y));
		}
	}
	
	public Collection<VertexLocation> getVertices()	{
		List<VertexLocation> vertices = new ArrayList<>();
		for (VertexDirection dir : VertexDirection.values()) {
			vertices.add(new VertexLocation(this, dir));
		}
		return vertices;
	}
	
	public Collection<EdgeLocation> getEdges()	{
		List<EdgeLocation> edges = new ArrayList<>();
		for (EdgeDirection dir : EdgeDirection.values()) {
			edges.add(new EdgeLocation(this, dir));
		}
		return edges;
	}
	
	public Collection<HexLocation> getNeighbors()	{
		List<HexLocation> neighbors = new ArrayList<>();
		for (EdgeDirection dir : EdgeDirection.values()) {
			neighbors.add(getNeighborLoc(dir));
		}
		return neighbors;
	}
	
	/** Returns an iterator of all locations within a certain distance of the center
	 * Iterates in a clockwise outward spiral pattern, from the top in each circle
	 * @param maxRadius the maximum distance from the center allowed in the iterable.
	 * @return an Iterable over all HexLocations within the given radius.
	 */
	public static Iterable<HexLocation> locationsWithinRadius(final int maxRadius) {
		// So much ugly!...
		return new Iterable<HexLocation> () {

			@Override
			public Iterator<HexLocation> iterator() {
				return new Iterator<HexLocation> () {
					
					private HexLocation location = new HexLocation(0, 0);
					/** This keeps track of the distance from the center */
					private int curRadius = 0;
					/** This is a little confusing. This keeps track of the position on the spiral,
					 * going clockwise from the top.
					 */
					private int spiralIndex = 0;

					@Override
					public boolean hasNext() {
						return location != null;
					}

					@Override
					public HexLocation next() {
						if (!hasNext()) {
							throw new NoSuchElementException();
						}
						HexLocation next = location;
						
						if (curRadius == 0) {
							if (maxRadius > 0) {
								location = new HexLocation(0, -1);
							}
							else {
								location = null;
								return next;
							}
							curRadius = 1;
							spiralIndex = 0;
							return next;
						}

						/* Do the spiral thing!
						 * Get the side (and direction) - takes advantage of integer division.
						 * Justification behind the math: the length of a side is always one more
						 * than the distance it is from the center hex. Since you always have to 
						 * change directions on the corners, which would be evenly divisible by the current
						 * radius, you just move from one hex to the next in the clockwise pattern.
						 * Yes, it's confusing. Drawing a picture helps.
						 */
						switch (spiralIndex / curRadius) {
						case 0:
							location = location.getNeighborLoc(EdgeDirection.SouthEast);
							break;
						case 1:
							location = location.getNeighborLoc(EdgeDirection.South);
							break;
						case 2:
							location = location.getNeighborLoc(EdgeDirection.SouthWest);
							break;
						case 3:
							location = location.getNeighborLoc(EdgeDirection.NorthWest);
							break;
						case 4:
							location = location.getNeighborLoc(EdgeDirection.North);
							break;
						case 5:
							location = location.getNeighborLoc(EdgeDirection.NorthEast);
							break;
						default:
							assert false;
							break;
						}
						++spiralIndex;
						// Next circle
						if (spiralIndex >= 6 * curRadius) {
							++curRadius;
							spiralIndex = 0;
							if (curRadius <= maxRadius) {
								location = new HexLocation(0, -curRadius);
							}
							else { // We're outside the radius, so we're done.
								location = null;
							}
						}
						
						return next;
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}
	
}

