package shared.locations;

import static org.junit.Assert.*;

import org.junit.Test;

public class EdgeLocationTest {

	@Test
	public void testHashCode() {
		EdgeLocation a, b, c, d, e;
		a = new EdgeLocation(1, 0, EdgeDirection.South);
		b = new EdgeLocation(1, 1, EdgeDirection.North);
		c = new EdgeLocation(1, 0, EdgeDirection.South);
		d = new EdgeLocation(-1, 2, EdgeDirection.SouthEast);
		e = new EdgeLocation(0, 2, EdgeDirection.NorthWest);

		assertEquals(a.hashCode(), a.hashCode());
		assertEquals(a.hashCode(), b.hashCode());
		assertEquals(a.hashCode(), c.hashCode());
		assertNotEquals(a.hashCode(), d.hashCode());
		assertNotEquals(a.hashCode(), e.hashCode());
		assertEquals(d.hashCode(), e.hashCode());
	}

	@Test
	public void testIsAdjacent() {
		EdgeLocation a, b, c, d, e, f, g, h, i;
		a = new EdgeLocation(1, 0, EdgeDirection.South);
		b = new EdgeLocation(1, 1, EdgeDirection.North);
		c = new EdgeLocation(1, 0, EdgeDirection.South);
		d = new EdgeLocation(1, 0, EdgeDirection.SouthWest);
		e = new EdgeLocation(1, 0, EdgeDirection.NorthWest);
		f = new EdgeLocation(-2, 2, EdgeDirection.SouthWest);
		g = new EdgeLocation(0, 0, EdgeDirection.North);
		h = new EdgeLocation(-1, 0, EdgeDirection.SouthEast);
		i = new EdgeLocation(1, -1, EdgeDirection.NorthWest);

		assertFalse(a.isAdjacent(a));
		assertFalse(a.isAdjacent(b));
		assertFalse(b.isAdjacent(a));
		assertFalse(a.isAdjacent(c));

		assertTrue(a.isAdjacent(d));
		assertTrue(d.isAdjacent(a));
		assertTrue(d.isAdjacent(e));
		assertFalse(e.isAdjacent(a));
		assertFalse(a.isAdjacent(f));
		assertFalse(b.isAdjacent(f));
		assertFalse(e.isAdjacent(f));
		
		assertTrue(g.isAdjacent(h));
		assertTrue(g.isAdjacent(i));
		assertTrue(h.isAdjacent(g));
		assertTrue(i.isAdjacent(g));
		assertFalse(h.isAdjacent(i));
		assertFalse(i.isAdjacent(h));
	}

	@Test
	public void testGetDistanceFromCenter() {
		assertEquals(0, new EdgeLocation(0, 0, EdgeDirection.North).getDistanceFromCenter());
		assertEquals(1, new EdgeLocation(0, 1, EdgeDirection.SouthEast).getDistanceFromCenter());
		assertEquals(1, new EdgeLocation(0, 1, EdgeDirection.SouthWest).getDistanceFromCenter());
		assertEquals(1, new EdgeLocation(1, -2, EdgeDirection.South).getDistanceFromCenter());
		assertEquals(1, new EdgeLocation(-1, 0, EdgeDirection.South).getDistanceFromCenter());
		assertEquals(1, new EdgeLocation(0, 2, EdgeDirection.North).getDistanceFromCenter());
		assertEquals(2, new EdgeLocation(1, 1, EdgeDirection.South).getDistanceFromCenter());
		assertEquals(3, new EdgeLocation(4, -2, EdgeDirection.SouthWest).getDistanceFromCenter());
	}

	@Test
	public void testIsSpoke() {
		assertFalse(new EdgeLocation(0, 0, EdgeDirection.North).isSpoke());
		assertFalse(new EdgeLocation(0, 0, EdgeDirection.SouthWest).isSpoke());
		assertFalse(new EdgeLocation(0, 1, EdgeDirection.North).isSpoke());
		assertFalse(new EdgeLocation(-1, 2, EdgeDirection.NorthEast).isSpoke());
		assertTrue(new EdgeLocation(-1, 2, EdgeDirection.SouthEast).isSpoke());
		assertTrue(new EdgeLocation(-1, 0, EdgeDirection.NorthEast).isSpoke());
		assertTrue(new EdgeLocation(1, -1, EdgeDirection.South).isSpoke());
		assertTrue(new EdgeLocation(0, -1, EdgeDirection.SouthEast).isSpoke());
	}

	@Test
	public void testEqualsObject() {
		EdgeLocation a, b, c, d, e;
		Integer n = 6;
		a = new EdgeLocation(1, 0, EdgeDirection.South);
		b = new EdgeLocation(1, 1, EdgeDirection.North);
		c = new EdgeLocation(1, 0, EdgeDirection.South);
		d = new EdgeLocation(-1, 2, EdgeDirection.SouthEast);
		e = new EdgeLocation(0, 2, EdgeDirection.NorthWest);

		assertTrue(a.equals(a));
		assertTrue(a.equals(b));
		assertTrue(b.equals(a));
		assertTrue(a.equals(c));
		assertTrue(d.equals(e));
		assertTrue(e.equals(d));

		assertFalse(d.equals(a));
		assertFalse(d.equals(n));
		assertFalse(b.equals(d));
	}

	@Test
	public void testGetNeighbors() {
		EdgeLocation a, b, c;
		a = new EdgeLocation(1, 0, EdgeDirection.South);
		b = new EdgeLocation(-1, 2, EdgeDirection.SouthEast);
		c = new EdgeLocation(7, 2, EdgeDirection.NorthWest);

		assertEquals(4, a.getNeighbors().size());
		assertEquals(4, b.getNeighbors().size());
		assertEquals(4, c.getNeighbors().size());
		
		for (EdgeLocation edge : a.getNeighbors()) {
			assertTrue(a.isAdjacent(edge));
		}
		
		for (EdgeLocation edge : b.getNeighbors()) {
			assertTrue(b.isAdjacent(edge));
		}
		
		for (EdgeLocation edge : c.getNeighbors()) {
			assertTrue(c.isAdjacent(edge));
		}
	}

}
