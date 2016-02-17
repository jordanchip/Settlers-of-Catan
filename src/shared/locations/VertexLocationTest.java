package shared.locations;

import static org.junit.Assert.*;

import org.junit.Test;

public class VertexLocationTest {

	@Test
	public void testHashCode() {
		VertexLocation a, b, c, d, e;
		a = new VertexLocation(0, 0, VertexDirection.East);
		b = new VertexLocation(1, -1, VertexDirection.SouthWest);
		c = new VertexLocation(0, 0, VertexDirection.East);
		d = new VertexLocation(1, 0, VertexDirection.NorthWest);
		e = new VertexLocation(-1, 1, VertexDirection.NorthWest);

		assertEquals(a.hashCode(), a.hashCode());
		assertEquals(a.hashCode(), b.hashCode());
		assertEquals(a.hashCode(), c.hashCode());
		assertEquals(a.hashCode(), d.hashCode());
		assertNotEquals(a.hashCode(), e.hashCode());
		assertNotEquals(b.hashCode(), e.hashCode());
	}

	@Test
	public void testGetNeighbors() {
		VertexLocation a, b, c;
		a = new VertexLocation(0, 0, VertexDirection.East);
		b = new VertexLocation(-24, 20, VertexDirection.West);
		c = new VertexLocation(-2, -1, VertexDirection.NorthEast);

		for (VertexLocation neighbor : a.getNeighbors()) {
			assertTrue(a.isAdjacent(neighbor));
		}
		
		for (VertexLocation neighbor : b.getNeighbors()) {
			assertTrue(b.isAdjacent(neighbor));
		}
		
		for (VertexLocation neighbor : c.getNeighbors()) {
			assertTrue(c.isAdjacent(neighbor));
		}
	}

	@Test
	public void testIsAdjacent() {
		VertexLocation a, b, c, d, e, f, g;
		a = new VertexLocation(0, 0, VertexDirection.East);
		b = new VertexLocation(1, -1, VertexDirection.SouthWest);
		c = new VertexLocation(0, 0, VertexDirection.East);
		d = new VertexLocation(1, 0, VertexDirection.NorthWest);
		e = new VertexLocation(-1, 1, VertexDirection.NorthWest);
		f = new VertexLocation(0, 0, VertexDirection.NorthEast);
		g = new VertexLocation(1, 0, VertexDirection.West);

		assertFalse(a.isAdjacent(a));
		assertFalse(a.isAdjacent(b));
		assertFalse(b.isAdjacent(a));
		assertFalse(a.isAdjacent(c));
		assertFalse(a.isAdjacent(d));
		assertFalse(a.isAdjacent(e));

		assertTrue(a.isAdjacent(f));
		assertTrue(f.isAdjacent(a));
		assertTrue(a.isAdjacent(g));
		assertTrue(g.isAdjacent(a));

		assertFalse(f.isAdjacent(g));
		assertFalse(e.isAdjacent(g));
	}

	@Test
	public void testGetDistanceFromCenter() {
		assertEquals(0, new VertexLocation(0, 0, VertexDirection.NorthEast).getDistanceFromCenter());
		assertEquals(0, new VertexLocation(0, 0, VertexDirection.West).getDistanceFromCenter());
		assertEquals(0, new VertexLocation(1, 0, VertexDirection.West).getDistanceFromCenter());
		assertEquals(1, new VertexLocation(1, 0, VertexDirection.East).getDistanceFromCenter());
		assertEquals(1, new VertexLocation(-1, 2, VertexDirection.East).getDistanceFromCenter());
		assertEquals(1, new VertexLocation(-1, -1, VertexDirection.SouthWest).getDistanceFromCenter());
		assertEquals(2, new VertexLocation(0, -2, VertexDirection.NorthWest).getDistanceFromCenter());
		assertEquals(2, new VertexLocation(-2, 2, VertexDirection.SouthEast).getDistanceFromCenter());
	}

	@Test
	public void testEquals() {
		VertexLocation a, b, c, d, e;
		Integer n = 4;
		a = new VertexLocation(0, 0, VertexDirection.East);
		b = new VertexLocation(1, -1, VertexDirection.SouthWest);
		c = new VertexLocation(0, 0, VertexDirection.East);
		d = new VertexLocation(1, 0, VertexDirection.NorthWest);
		e = new VertexLocation(-1, 1, VertexDirection.NorthWest);

		assertTrue(a.equals(a));
		assertTrue(a.equals(b));
		assertTrue(b.equals(a));
		assertTrue(a.equals(c));
		assertTrue(a.equals(d));
		assertFalse(a.equals(e));
		assertFalse(a.equals(n));
		assertFalse(a.equals(null));
	}

}
