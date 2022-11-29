package pl.felixspeagel.calcal.calculators;

import pl.felixspeagel.calcal.calendar.Calendar;
import pl.felixspeagel.calcal.calendar.SpecialFeature;
import pl.felixspeagel.calcal.math.MixedFraction;

import java.math.BigInteger;

/**
 * Calculates Ancient Egyptian like calendar.
 */
public class Egyptian implements CalendarCreator {
	
	public final BigInteger days_in_month;
	public final BigInteger days_in_year;
	public final BigInteger epagomenal_days;
	public final BigInteger every_year;
	public final BigInteger add_leap_days;
	
	public Egyptian(MixedFraction year_length, MixedFraction month_length, int month_count) {
		days_in_month = month_length.ceil();
		days_in_year = days_in_month.multiply( BigInteger.valueOf( month_count ) );
		epagomenal_days = year_length.floor().subtract( days_in_year );
		var surplus = year_length.getFraction();
		if( ! surplus.isZero() ) {
			every_year = surplus.getDenominator();
			add_leap_days = surplus.getNumerator();
		} else {
			every_year = BigInteger.ONE;
			add_leap_days = BigInteger.ZERO;
		}
	}
	
	@Override
	public Calendar makeExampleCalendar(MixedFraction year_length, MixedFraction month_length, int month_count) {
		var result = new Calendar( SpecialFeature.EPAGOMENAL );
		
		if( ! add_leap_days.equals( BigInteger.ZERO ) ) {
			var leapRule = new Rule[1];
			leapRule[0] = new Rule( every_year, IntercalationType.LEAP );
			result.setupLeapRules( leapRule );
		}
		
		//normal months
		var year = result.getYear();
		for(int month=0; month<month_count; month++) {
			year.addMonth( "#" + ( month + 1 ), days_in_month.intValue(), 0 );
		}
		//epagomenal
		if( epagomenal_days.compareTo( BigInteger.ZERO ) > 0 ) {
			year.addMonth( "--", epagomenal_days.intValue(), add_leap_days.intValue() );
			year.setFeatureMonth( year.getLastMonthIndex() );
		}
		
		return result;
	}
}
