package ubc.cosc322;

import java.util.ArrayList;
import java.util.Iterator;

public class Coords implements Iterable<Coords> {
	private ArrayList<Integer> array = new ArrayList<>();
	
	public Coords(int... integers) {
		for (int i = 0; i < integers.length; i++)
			array.add(integers[i]);
	}
	
	public int get(int i) {
		return array.get(i);
	}
	
	public boolean equals(Coords other) {
		return true;
	}
	
	public Iterator<Coords> iterator() {
		Iterator<Coords> iterator = new Iterator();
	}
}
