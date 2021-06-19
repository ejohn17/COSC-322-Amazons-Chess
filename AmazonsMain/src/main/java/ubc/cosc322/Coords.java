package ubc.cosc322;

import java.util.ArrayList;

/**
 * This class represents a tile on the board, with the added .hashCode() and overridden .equals() methods so that it can be easily searched for within a HashSet
 * (so that in our MiniMax's heuristic #3, we can easily search the HashSet of already-visited coordinates for this Coords many times, efficiently). 
 * Originally, we had only been using primitive integer arrays to represent coordinates, for efficiency, but we ended up needing this class so that we could use
 * it in HashSets when necessary.
 */
public class Coords {
	private ArrayList<Integer> array = new ArrayList<>(2);
	
	public Coords(int[] coords) {
		array.add(coords[0]);
		array.add(coords[1]);
	}
	
	public int get(int i) {
		return array.get(i);
	}
	
	public int set(int i, Integer val) {
		return array.set(i, val);
	}
	
	public boolean equals(Object other) {
		if (!(other instanceof Coords))
			return false;
		
		if ((array.get(0).equals(((Coords)other).get(0))) && (array.get(1).equals(((Coords)other).get(1))))
			return true;
		else
			return false;
	}
	
	public String toString() {
		return "[" + array.get(0) + ", " + array.get(1) + "]";
	}
	
	public int hashCode() {
		return array.hashCode();
	}
}
