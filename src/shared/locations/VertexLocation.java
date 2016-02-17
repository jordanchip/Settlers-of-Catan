package shared.locations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.simple.JSONObject;

import shared.exceptions.SchemaMismatchException;

/**
 * Represents the location of a vertex on a hex map
 */
public class VertexLocation
implements Serializable 
{
	private static final long serialVersionUID = -2721996254584938224L;
	
	private HexLocation hexLoc;
	private VertexDirection dir;
	
	public VertexLocation(JSONObject json) throws SchemaMismatchException {
		if (json.containsKey("hexLoc"))
			hexLoc = new HexLocation((JSONObject)json.get("hexLoc"));
		else
			hexLoc = new HexLocation(json);
		if (json.containsKey("direction"))
			dir = VertexDirection.getDirectionFromString((String) json.get("direction"));
		else if (json.containsKey("dir"))
			dir = VertexDirection.getDirectionFromString((String) json.get("dir"));
		//else throw schema exception.
	}
	
	public VertexLocation(HexLocation hexLoc, VertexDirection dir)
	{
		this.hexLoc = hexLoc;
		this.dir = dir;
	}
	
	public VertexLocation(int x, int y, VertexDirection dir) {
		this.hexLoc = new HexLocation(x, y);
		this.dir = dir;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		JSONObject json = hexLoc.toJSONObject();
		
		json.put("direction", dir.getSymbolString());
		
		return json;
	}

	public HexLocation getHexLoc()
	{
		return hexLoc;
	}
	
	public VertexDirection getDir()
	{
		return dir;
	}
	
	/** Gives the three edges that are/would be connected to this VertexLocation
	 * @return
	 */
	public Collection<EdgeLocation> getEdges() {
		VertexLocation normal = getNormalizedLocation();
		HexLocation northHex = normal.hexLoc.getNeighborLoc(EdgeDirection.North);
		List<EdgeLocation> edges = new ArrayList<>();
		edges.add(new EdgeLocation(normal.hexLoc, EdgeDirection.North));
		switch (normal.dir) {
		case NorthWest:
			edges.add(new EdgeLocation(normal.hexLoc, EdgeDirection.NorthWest));
			edges.add(new EdgeLocation(northHex, EdgeDirection.SouthWest));
			break;
		case NorthEast:
			edges.add(new EdgeLocation(normal.hexLoc, EdgeDirection.NorthEast));
			edges.add(new EdgeLocation(northHex, EdgeDirection.SouthEast));
			break;
		default:
			assert false;
			break;
		}
		return edges;
	}
	
	public Collection<HexLocation> getHexes() {
		VertexLocation normal = getNormalizedLocation();
		List<HexLocation> hexes = new ArrayList<>();
		hexes.add(normal.hexLoc);
		hexes.add(normal.hexLoc.getNeighborLoc(EdgeDirection.North));
		switch (normal.dir) {
		case NorthWest:
			hexes.add(normal.hexLoc.getNeighborLoc(EdgeDirection.NorthWest));
			break;
		case NorthEast:
			hexes.add(normal.hexLoc.getNeighborLoc(EdgeDirection.NorthEast));
			break;
		default:
			assert false;
			break;
		}
		return hexes;
	}
	
	public Collection<VertexLocation> getNeighbors() {
		VertexLocation normal = getNormalizedLocation();
		List<VertexLocation> neighbors = new ArrayList<>();
		HexLocation nearHex;
		switch (normal.dir) {
		case NorthWest:
			neighbors.add(new VertexLocation(normal.hexLoc, VertexDirection.NorthEast));
			nearHex = normal.hexLoc.getNeighborLoc(EdgeDirection.NorthWest);
			neighbors.add(new VertexLocation(nearHex, VertexDirection.NorthEast));
			neighbors.add(new VertexLocation(nearHex, VertexDirection.SouthEast));
			break;
		case NorthEast:
			neighbors.add(new VertexLocation(normal.hexLoc, VertexDirection.NorthWest));
			nearHex = normal.hexLoc.getNeighborLoc(EdgeDirection.NorthEast);
			neighbors.add(new VertexLocation(nearHex, VertexDirection.NorthWest));
			neighbors.add(new VertexLocation(nearHex, VertexDirection.SouthWest));
			break;
		default:
			assert false;
			break;
		}
		return neighbors;
	}
	
	public boolean isAdjacent(VertexLocation other) {
		if (this.equals(other)) return false;
		VertexLocation self = getNormalizedLocation();
		other = other.getNormalizedLocation();
		
		if (self.hexLoc.equals(other.hexLoc)) return true;
		if (!self.hexLoc.isAdjacent(other.hexLoc)) return false;

		int dx = other.hexLoc.getX() - self.hexLoc.getX();
		switch (self.dir) {
		case NorthWest:
			return dx == -1 && other.dir == VertexDirection.NorthEast;
		case NorthEast:
			return dx == 1 && other.dir == VertexDirection.NorthWest;
		default:
			assert false;
			return false;
		}
	}
	
	public int getDistanceFromCenter() {
		int closestDist = Integer.MAX_VALUE;
		for (HexLocation hex : getHexes()) {
			int dist = hex.getDistanceFromCenter();
			if (dist < closestDist) {
				closestDist = dist;
			}
		}
		return closestDist;
	}
	
	@Override
	public String toString()
	{
		return "VertexLocation [hexLoc=" + hexLoc + ", dir=" + dir + "]";
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		VertexLocation self = getNormalizedLocation();
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
		VertexLocation other = ((VertexLocation) obj).getNormalizedLocation();
		VertexLocation self = getNormalizedLocation();
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
	 * Returns a canonical (i.e., unique) value for this vertex location. Since
	 * each vertex has three different locations on a map, this method converts
	 * a vertex location to a single canonical form. This is useful for using
	 * vertex locations as map keys.
	 * 
	 * @return Normalized vertex location
	 */
	public VertexLocation getNormalizedLocation()
	{
		
		// Return location that has direction NW or NE
		
		switch (dir)
		{
			case NorthWest:
			case NorthEast:
				return this;
			case West:
				return new VertexLocation(
										  hexLoc.getNeighborLoc(EdgeDirection.SouthWest),
										  VertexDirection.NorthEast);
			case SouthWest:
				return new VertexLocation(
										  hexLoc.getNeighborLoc(EdgeDirection.South),
										  VertexDirection.NorthWest);
			case SouthEast:
				return new VertexLocation(
										  hexLoc.getNeighborLoc(EdgeDirection.South),
										  VertexDirection.NorthEast);
			case East:
				return new VertexLocation(
										  hexLoc.getNeighborLoc(EdgeDirection.SouthEast),
										  VertexDirection.NorthWest);
			default:
				assert false;
				return null;
		}
	}

	public VertexLocation traverse(EdgeLocation edge) {
		Collection<VertexLocation> vertices = edge.getVertices();
		if (vertices.contains(this)) {
			for (VertexLocation vertex : vertices) {
				if (!vertex.equals(this)) return vertex;
			}
		}
		else {
			throw new IllegalArgumentException("Expected adjacent edge.");
		}
		return null;
	}
}

