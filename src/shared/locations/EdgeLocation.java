package shared.locations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;

import shared.exceptions.SchemaMismatchException;

/**
 * Represents the location of an edge on a hex map
 */
public class EdgeLocation
implements Serializable 
{
	private static final long serialVersionUID = -4159809329290147972L;
	
	private HexLocation hexLoc;
	private EdgeDirection dir;
	
	public EdgeLocation(JSONObject json) throws SchemaMismatchException {
		if (json.containsKey("hexLoc"))
			hexLoc = new HexLocation((JSONObject)json.get("hexLoc"));
		else
			hexLoc = new HexLocation(json);
		if (json.containsKey("dir"))
			dir = EdgeDirection.getDirectionFromString((String) json.get("dir"));
		else if (json.containsKey("direction"))
			dir = EdgeDirection.getDirectionFromString((String) json.get("direction"));
		//else throw schema exception.
	}
	
	public EdgeLocation(HexLocation hexLoc, EdgeDirection dir)
	{
		setHexLoc(hexLoc);
		setDir(dir);
	}
	
	public EdgeLocation(int x, int y, EdgeDirection dir) {
		hexLoc = new HexLocation(x, y);
		this.dir = dir;
	}

	public HexLocation getHexLoc()
	{
		return hexLoc;
	}
	
	private void setHexLoc(HexLocation hexLoc)
	{
		if(hexLoc == null)
		{
			throw new IllegalArgumentException("hexLoc cannot be null");
		}
		this.hexLoc = hexLoc;
	}
	
	public EdgeDirection getDir()
	{
		return dir;
	}
	
	private void setDir(EdgeDirection dir)
	{
		this.dir = dir;
	}
	
	/** Tells you if this edge is adjacent to another
	 * @param other the other edge
	 * @return true iff adjacent and not at the same location
	 */
	public boolean isAdjacent(EdgeLocation other) {
		if (this.equals(other)) return false;
		EdgeLocation self = getNormalizedLocation();
		other = other.getNormalizedLocation();
		if (self.hexLoc.equals(other.hexLoc)) {
			switch (self.dir) {
			case NorthWest:
			case NorthEast:
				return other.dir == EdgeDirection.North;
			case North:
				return other.dir == EdgeDirection.NorthEast ||
						other.dir == EdgeDirection.NorthWest;
			default:
				break;
			}
		}
		if (!self.hexLoc.isAdjacent(other.hexLoc)) {
			return false;
		}
		// HexLocation MUST be adjacent at this point
		int dx = other.hexLoc.getX() - self.hexLoc.getX();
		int dy = other.hexLoc.getY() - self.hexLoc.getY();
		switch (self.dir) {
		case NorthWest:
			if (dx == -1) {
				if (other.dir == EdgeDirection.NorthEast) return true;
				else return dy == 1 && other.dir == EdgeDirection.North;
			}
			else return false;
		case NorthEast:
			if (dx == 1) {
				if (other.dir == EdgeDirection.NorthWest) return true;
				else return dy == 0 && other.dir == EdgeDirection.North;
			}
			else return false;
		case North:
			return (other.dir == EdgeDirection.NorthEast && dx == -1 && dy == 0) ||
				   (other.dir == EdgeDirection.NorthWest && dx == 1 && dy == -1);
		default:
			assert false;
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		JSONObject json = hexLoc.toJSONObject();
		
		json.put("direction", dir.getSymbolString());
		
		return json;
	}
	
	/** Gives the distance from the center of the bordering hex that is closest to the center.
	 * @return
	 */
	public int getDistanceFromCenter() {
		return Math.min(hexLoc.getDistanceFromCenter(),
				hexLoc.getNeighborLoc(dir).getDistanceFromCenter());
	}
	
	/** Tells you whether or not this edge is a "spoke"
	 * A spoke refers to an edge that points away from the center, whereas a non-spoke (wheel)
	 * is an edge that lines the border at a radius.
	 * @return
	 */
	public boolean isSpoke() {
		return hexLoc.getDistanceFromCenter() == hexLoc.getNeighborLoc(dir).getDistanceFromCenter();
	}
	
	/** Gives the two vertices this is attached to
	 * @return the end point VertexLocations
	 */
	public Collection<VertexLocation> getVertices() {
		List<VertexLocation> result = new ArrayList<>();
		EdgeLocation normal = getNormalizedLocation();
		switch(normal.dir) {
		case NorthWest:
			result.add(new VertexLocation(normal.hexLoc, VertexDirection.West));
			result.add(new VertexLocation(normal.hexLoc, VertexDirection.NorthWest));
			break;
		case North:
			result.add(new VertexLocation(normal.hexLoc, VertexDirection.NorthWest));
			result.add(new VertexLocation(normal.hexLoc, VertexDirection.NorthEast));
			break;
		case NorthEast:
			result.add(new VertexLocation(normal.hexLoc, VertexDirection.NorthEast));
			result.add(new VertexLocation(normal.hexLoc, VertexDirection.East));
			break;
		default:
			break;
		}
		return result;
	}
	
	/** Gives the two HexLocations that border this edge
	 * @return
	 */
	public Collection<HexLocation> getHexes() {
		List<HexLocation> hexes = new ArrayList<>();
		hexes.add(hexLoc);
		hexes.add(hexLoc.getNeighborLoc(dir));
		return hexes;
	}
	
	@Override
	public String toString()
	{
		return "EdgeLocation [hexLoc=" + hexLoc + ", dir=" + dir + "]";
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		EdgeLocation self = getNormalizedLocation();
		int result = 1;
		result = prime * result + ((self.dir == null) ? 0 : self.dir.hashCode());
		result = prime * result + ((self.hexLoc == null) ? 0 : self.hexLoc.hashCode());
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
		EdgeLocation other = ((EdgeLocation) obj).getNormalizedLocation();
		EdgeLocation self = getNormalizedLocation();
		if(self.dir != other.dir)
			return false;
		if(self.hexLoc == null)
		{
			if(other.hexLoc != null)
				return false;
		}
		else if(!self.hexLoc.equals(other.hexLoc))
			return false;
		return true;
	}
	
	/**
	 * Returns a canonical (i.e., unique) value for this edge location. Since
	 * each edge has two different locations on a map, this method converts a
	 * hex location to a single canonical form. This is useful for using hex
	 * locations as map keys.
	 * 
	 * @return Normalized hex location
	 */
	public EdgeLocation getNormalizedLocation()
	{
		
		// Return an EdgeLocation that has direction NW, N, or NE
		
		switch (dir)
		{
			case NorthWest:
			case North:
			case NorthEast:
				return this;
			case SouthWest:
			case South:
			case SouthEast:
				return flip();
			default:
				assert false;
				return null;
		}
	}
	
	public EdgeLocation flip() {
		return new EdgeLocation(hexLoc.getNeighborLoc(dir),
				dir.getOppositeDirection());
	}

	public Collection<EdgeLocation> getNeighbors() {
		List<EdgeLocation> neighbors = new ArrayList<>();
		EdgeLocation normal = getNormalizedLocation();
		HexLocation a, b;
		switch (normal.dir) {
		case NorthWest:
			a = normal.hexLoc.getNeighborLoc(EdgeDirection.SouthWest);
			b = normal.hexLoc.getNeighborLoc(EdgeDirection.North);
			neighbors.add(new EdgeLocation(a, EdgeDirection.North));
			neighbors.add(new EdgeLocation(a, EdgeDirection.NorthEast));
			neighbors.add(new EdgeLocation(b, EdgeDirection.South));
			neighbors.add(new EdgeLocation(b, EdgeDirection.SouthWest));
			break;
		case North:
			a = normal.hexLoc.getNeighborLoc(EdgeDirection.NorthWest);
			b = normal.hexLoc.getNeighborLoc(EdgeDirection.NorthEast);
			neighbors.add(new EdgeLocation(a, EdgeDirection.SouthEast));
			neighbors.add(new EdgeLocation(a, EdgeDirection.NorthEast));
			neighbors.add(new EdgeLocation(b, EdgeDirection.NorthWest));
			neighbors.add(new EdgeLocation(b, EdgeDirection.SouthWest));
			break;
		case NorthEast:
			a = normal.hexLoc.getNeighborLoc(EdgeDirection.SouthEast);
			b = normal.hexLoc.getNeighborLoc(EdgeDirection.North);
			neighbors.add(new EdgeLocation(a, EdgeDirection.North));
			neighbors.add(new EdgeLocation(a, EdgeDirection.NorthWest));
			neighbors.add(new EdgeLocation(b, EdgeDirection.South));
			neighbors.add(new EdgeLocation(b, EdgeDirection.SouthEast));
			break;
		default:
			assert false;
			return null;
		}
		
		return neighbors;
	}
	
	public VertexLocation getVertexBetween(EdgeLocation other) {
		if (!this.isAdjacent(other)) {
			throw new IllegalArgumentException();
		}
		Set<VertexLocation> a, b;
		a = new HashSet<>(getVertices());
		b = new HashSet<>(other.getVertices());
		
		a.retainAll(b);
		
		for (VertexLocation loc: a) {
			return loc;
		}
		assert false;
		return null;
	}
}

