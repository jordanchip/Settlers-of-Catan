package shared.locations;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

public class HexLocationTest {

	@Test
	public void testHashCode() {
		HexLocation a, b, c, d, e;
		a = new HexLocation(1, 3);
		b = new HexLocation(3, 1);
		c = new HexLocation(1, 3);
		d = new HexLocation(-1, -3);
		e = new HexLocation(0, 0);

		assertEquals(a.hashCode(), a.hashCode());
		assertNotEquals(a.hashCode(), b.hashCode());
		assertEquals(a.hashCode(), c.hashCode());
		assertNotEquals(a.hashCode(), d.hashCode());
		assertNotEquals(a.hashCode(), e.hashCode());
	}

	@Test
	public void testEqualsObject() {
		HexLocation a, b, c, d, e;
		Integer n = 5;
		a = new HexLocation(1, 3);
		b = new HexLocation(3, 1);
		c = new HexLocation(1, 3);
		d = new HexLocation(-1, -3);
		e = new HexLocation(0, 0);

		assertTrue(a.equals(a));
		assertTrue(a.equals(c));
		assertTrue(c.equals(a));
		assertFalse(a.equals(b));
		assertFalse(b.equals(a));
		assertFalse(a.equals(d));
		assertFalse(a.equals(e));
		assertFalse(e.equals(a));
		assertFalse(a.equals(n));
		assertFalse(a.equals(null));
	}

	@Test
	public void testIsAdjacent() {
		HexLocation a, b, c, d, e, f, g, h, i, j;
		a = new HexLocation(1, 3);
		b = new HexLocation(1, 4);
		c = new HexLocation(2, 3);
		d = new HexLocation(2, 4);
		e = new HexLocation(0, 4);
		f = new HexLocation(-1, 4);
		g = new HexLocation(-1, -4);
		h = new HexLocation(-1, -4);
		i = new HexLocation(-1, -5);
		j = new HexLocation(0, -4);
		

		assertTrue(a.isAdjacent(b));
		assertTrue(b.isAdjacent(a));
		
		assertTrue(a.isAdjacent(c));
		assertFalse(a.isAdjacent(d));
		assertTrue(b.isAdjacent(c));
		assertTrue(b.isAdjacent(d));
		assertTrue(c.isAdjacent(d));
		assertTrue(e.isAdjacent(a));
		assertTrue(a.isAdjacent(e));
		assertTrue(e.isAdjacent(f));

		assertFalse(f.isAdjacent(g));
		assertFalse(h.isAdjacent(g));
		assertFalse(g.isAdjacent(h));
		assertFalse(i.isAdjacent(j));
		
		assertTrue(h.isAdjacent(i));
		assertTrue(h.isAdjacent(j));
	}

	@Test
	public void testGetDistanceFromCenter() {
		assertEquals(new HexLocation(0, 0).getDistanceFromCenter(), 0);
		assertEquals(new HexLocation(15, 0).getDistanceFromCenter(), 15);
		assertEquals(new HexLocation(0, -12).getDistanceFromCenter(), 12);
		assertEquals(new HexLocation(3, 3).getDistanceFromCenter(), 6);
		assertEquals(new HexLocation(-7, -7).getDistanceFromCenter(), 14);
		assertEquals(new HexLocation(-2, 2).getDistanceFromCenter(), 2);
		assertEquals(new HexLocation(40, -40).getDistanceFromCenter(), 40);
		assertEquals(new HexLocation(5, -7).getDistanceFromCenter(), 7);
	}

	@Test
	public void testGetNeighbors() {
		HexLocation a, b, c;
		a = new HexLocation(0, 0);
		b = new HexLocation(2, -2);
		c = new HexLocation(5, 10);
		
		Set<HexLocation> aNeighbors, bNeighbors, cNeighbors;
		aNeighbors = new HashSet<HexLocation>();
		bNeighbors = new HashSet<HexLocation>();
		cNeighbors = new HashSet<HexLocation>();

		aNeighbors.add(new HexLocation(-1, 0));
		aNeighbors.add(new HexLocation(-1, 1));
		aNeighbors.add(new HexLocation(0, -1));
		aNeighbors.add(new HexLocation(0, 1));
		aNeighbors.add(new HexLocation(1, -1));
		aNeighbors.add(new HexLocation(1, 0));

		bNeighbors.add(new HexLocation(1, -2));
		bNeighbors.add(new HexLocation(1, -1));
		bNeighbors.add(new HexLocation(2, -3));
		bNeighbors.add(new HexLocation(2, -1));
		bNeighbors.add(new HexLocation(3, -3));
		bNeighbors.add(new HexLocation(3, -2));

		cNeighbors.add(new HexLocation(4, 10));
		cNeighbors.add(new HexLocation(4, 11));
		cNeighbors.add(new HexLocation(5, 9));
		cNeighbors.add(new HexLocation(5, 11));
		cNeighbors.add(new HexLocation(6, 9));
		cNeighbors.add(new HexLocation(6, 10));

		assertTrue(aNeighbors.equals(new HashSet<>(a.getNeighbors())));
		assertTrue(bNeighbors.equals(new HashSet<>(b.getNeighbors())));
		assertTrue(cNeighbors.equals(new HashSet<>(c.getNeighbors())));
	}

	@Test
	public void testLocationsWithinRadius() {
		int radius = 0;
		for (int i=0; i<5; ++i) {
			int count = 0;
			for (HexLocation hex : HexLocation.locationsWithinRadius(radius)) {
				assertTrue(hex.getDistanceFromCenter() <= radius);
				++count;
			}
			int expected = 1;
			for (int n=0; n<=radius; n++) expected += n * 6;
			assertEquals(expected, count);
			radius *= 2;
			++radius;
		}
	}

}
