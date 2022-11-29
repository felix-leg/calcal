package pl.felixspeagel.calcal.math;

public class Pair<F, S> {
	public final F first;
	public final S second;
	
	public Pair(F first_element, S second_element) {
		first = first_element;
		second = second_element;
	}
	
	public boolean equals(Pair<F,S> other) {
		return first.equals( other.first ) && second.equals( other.second );
	}
}
