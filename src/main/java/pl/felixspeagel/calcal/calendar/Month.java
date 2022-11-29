package pl.felixspeagel.calcal.calendar;

public class Month {
	
	public String name;
	public int normal_days;
	public int leap_days;
	
	public Month(String month_name, int n, int l) {
		name = month_name;
		normal_days = n;
		leap_days = l;
	}
	public Month(int n, int l) {
		this("", n, l);
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof Month other) {
			return equals( other, true );
		}
		return false;
	}
	public boolean equals(Month other, boolean strict) {
		if( strict && ! name.equals( other.name ) )
			return false;
		if( normal_days != other.normal_days )
			return false;
		
		return leap_days == other.leap_days;
	}
	
}
