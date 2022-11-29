package pl.felixspeagel.calcal.calculators;

import pl.felixspeagel.calcal.calendar.Calendar;
import pl.felixspeagel.calcal.calendar.SpecialFeature;
import pl.felixspeagel.calcal.math.MixedFraction;

import java.math.BigInteger;

/**
 * Calculates a pure lunar calendar with leap days rules
 */
public class PureLunar implements CalendarCreator {
	
	/**
	 * rules to compute leap days
	 */
	public final Rule[] rules;
	public final boolean unable_to_compute;
	/**
	 * how many days are in a year.
 	 */
	public final BigInteger lunar_year;
	
	public PureLunar(MixedFraction month_length, int months_in_year) {
		var days_in_year = month_length.multiply( months_in_year );
		lunar_year = days_in_year.getInteger();
		//need help from Gregorian calculator
		var result = new Gregorian( days_in_year );
		rules = result.rules;
		unable_to_compute = result.unable_to_compute;
	}
	
	@Override
	public Calendar makeExampleCalendar(MixedFraction year_length, MixedFraction month_length, int month_count) {
		if( unable_to_compute )
			return null;
		
		var result = new Calendar( SpecialFeature.NONE );
		var year = result.getYear();
		
		var hollow_month = month_length.floor();
		var extra_days = lunar_year.subtract(
				hollow_month.multiply( BigInteger.valueOf( month_count ) )
		);
		for(int month=1; month<=month_count; month++) {
			var days = hollow_month;
			if( extra_days.compareTo( BigInteger.ZERO ) > 0 ) {
				days = days.add( BigInteger.ONE );
				extra_days = extra_days.subtract( BigInteger.ONE );
			}
			year.addMonth( "#"+month, days.intValue(), 0 );
		}
		
		if( rules.length > 0 ) {
			result.setupLeapRules( rules );
			year.getMonth( year.getLastMonthIndex() ).leap_days += 1;
		}
		
		return result;
	}
}
