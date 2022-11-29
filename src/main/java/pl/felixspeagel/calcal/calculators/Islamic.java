package pl.felixspeagel.calcal.calculators;

import pl.felixspeagel.calcal.calendar.Calendar;
import pl.felixspeagel.calcal.calendar.SpecialFeature;
import pl.felixspeagel.calcal.math.MixedFraction;

import java.math.BigInteger;
import java.util.LinkedList;

/**
 * Calculates lunar calendar like a tabular islamic calendar.
 */
public class Islamic implements CalendarCreator {
	
	/**
	 * How many days should be added in a whole cycle.
	 */
	public final BigInteger days_to_add;
	/**
	 * How many days one year have.
	 */
	public final BigInteger days_in_year;
	/**
	 * An array of which year is a leap year (with one extra day) or a normal year.
	 */
	public final IntercalationType[] cycle;
	
	public Islamic(MixedFraction month_length, int months_in_year) {
		final var hollow_month = month_length.floor();
		var shouldBeDays = BigInteger.ZERO;
		var cumulative = MixedFraction.ZERO;
		
		for(int month=1; month<=months_in_year; month++) {
			cumulative = cumulative.add(month_length);
			shouldBeDays = shouldBeDays.add( hollow_month );
			if(month % 2 == 0) {
				shouldBeDays = shouldBeDays.add( BigInteger.ONE );
			}
		}
		cumulative = cumulative.getFraction();
		
		final var cycle_length = cumulative.getDenominator();
		days_to_add = cumulative.getNumerator();
		days_in_year = shouldBeDays;
		var cycle_result = new LinkedList<IntercalationType>();
		
		cumulative = MixedFraction.ZERO;
		for(var year=BigInteger.ONE; year.compareTo( cycle_length ) <= 0; year=year.add(BigInteger.ONE)) {
			cumulative = cumulative.add(month_length.multiply( months_in_year ));
			var surplus = cumulative.subtract( days_in_year.multiply( year ) );
			
			if( surplus.compareTo( MixedFraction.ONE_HALF ) > 0) { // surplus > 1/2
				cumulative = cumulative.subtract( BigInteger.ONE );
				cycle_result.add( IntercalationType.LEAP );
			} else {
				cycle_result.add( IntercalationType.NORMAL );
			}
		}
		
		cycle = cycle_result.toArray(new IntercalationType[0]);
	}
	
	@Override
	public Calendar makeExampleCalendar(MixedFraction year_length, MixedFraction month_length, int month_count) {
		var result = new Calendar( SpecialFeature.NONE );
		
		var year = result.getYear();
		var hollow_month = month_length.floor();
		
		for(int month=1; month<=month_count; month++) {
			var days = hollow_month;
			if(month % 2 == 0) {
				days = days.add( BigInteger.ONE );
			}
			year.addMonth( "#"+month, days.intValue(), 0 );
		}
		
		if( cycle.length > 0 ) {
			result.setupCycle( cycle );
			year.getMonth( year.getLastMonthIndex() ).leap_days += 1;
		}
		
		return result;
	}
}
