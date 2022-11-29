package pl.felixspeagel.calcal.calendar;

public class Week {
	
	public int length;
	/**
	 * Is a week always starts within first day in a month?
	 */
	public boolean starts_with_month;
	
	public Week() {
		length = 7;
		starts_with_month = false;
	}
	
	@Override
	public boolean equals(Object object) {
		if( object instanceof Week week ) {
			if( length != week.length )
				return false;
			return starts_with_month == week.starts_with_month;
		}
		return false;
	}
	
}
