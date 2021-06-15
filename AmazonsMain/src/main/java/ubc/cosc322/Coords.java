package ubc.cosc322;

import java.util.ArrayList;

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
