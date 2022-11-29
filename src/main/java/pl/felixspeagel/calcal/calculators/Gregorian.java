package pl.felixspeagel.calcal.calculators;

import pl.felixspeagel.calcal.calendar.Calendar;
import pl.felixspeagel.calcal.calendar.SpecialFeature;
import pl.felixspeagel.calcal.math.Functions;
import pl.felixspeagel.calcal.math.MixedFraction;

import java.math.BigInteger;
import java.util.LinkedList;

/**
 * Calculates the rules for leap years like in gregorian calendar.
 */
public class Gregorian implements CalendarCreator {
	
	public final Rule[] rules;
	public final boolean unable_to_compute;
	
	public Gregorian(MixedFraction year_length) {
		var surplus = year_length.getFraction();
		if( surplus.isZero() ) {
			rules = new Rule[0];
			unable_to_compute = false;
			return;
		}
		
		LinkedList<Rule> result;
		boolean unable;
		try {
			var divisors = Functions.divisors_of( surplus.getDenominator() );
			result = plus( divisors, surplus );
			
			assert result != null;
			unable = false;
		}catch( StackOverflowError e ) {
			result = new LinkedList<>();
			unable = true;
		}
		rules = result.toArray( new Rule[0] );
		unable_to_compute = unable;
	}
	
	private LinkedList<Rule> plus(BigInteger[] divisors, MixedFraction surplus) {
		var last_divisor = BigInteger.ONE;
		
		for(var divisor : divisors) {
			if( surplus.multiply( divisor ).getInteger().compareTo( BigInteger.ZERO ) > 0 ) {
				if( surplus.multiply( divisor ).equals( MixedFraction.ONE ) ) {
					last_divisor = divisor;
				}
				
				surplus = surplus.subtract( new MixedFraction( BigInteger.ONE, last_divisor ) );
				var result = new LinkedList<Rule>();
				result.add( new Rule(last_divisor, IntercalationType.LEAP) );
				
				if( surplus.compareTo( MixedFraction.ZERO ) > 0 ) { // surplus > 0
					result.addAll( plus(divisors, surplus) );
				} else if( surplus.compareTo( MixedFraction.ZERO ) < 0 ) { // surplus < 0
					result.addAll( minus(divisors, surplus) );
				}
				return result;
			}
			last_divisor = divisor;
		}
		
		return null; //shouldn't reach
	}
	
	private LinkedList<Rule> minus(BigInteger[] divisors, MixedFraction surplus) {
		var last_divisor = BigInteger.ONE;
		
		for(var divisor : divisors) {
			if( surplus.negate().multiply( divisor ).getInteger().compareTo( BigInteger.ZERO ) > 0 ) {
				if( surplus.negate().multiply( divisor ).equals( MixedFraction.ONE ) ) {
					last_divisor = divisor;
				}
				
				surplus = surplus.add( new MixedFraction( BigInteger.ONE, last_divisor ) );
				var result = new LinkedList<Rule>();
				result.add( new Rule(last_divisor, IntercalationType.NORMAL) );
				
				if( surplus.compareTo( MixedFraction.ZERO ) > 0 ) { // surplus > 0
					result.addAll( plus(divisors, surplus) );
				} else if( surplus.compareTo( MixedFraction.ZERO ) < 0 ) { // surplus < 0
					result.addAll( minus(divisors, surplus) );
				}
				return result;
			}
			last_divisor = divisor;
		}
		
		return null; //shouldn't reach
	}
	
	@Override
	public Calendar makeExampleCalendar(MixedFraction year_length, MixedFraction month_length, int month_count) {
		if( unable_to_compute )
			return null;
		
		var result = new Calendar( SpecialFeature.NONE );
		var year = result.getYear();
		int month;
		
		var short_month_length = month_length.floor();
		for(month=0; month<month_count; month++) {
			year.addMonth( "#" + (month+1), short_month_length.intValue(), 0 );
		}
		
		var extra_days = year_length.floor().subtract(
				short_month_length.multiply( BigInteger.valueOf( month_count ) )
		);
		month = 0;
		while( extra_days.compareTo( BigInteger.ZERO ) > 0 ) {
			year.getMonth( month ).normal_days += 1;
			extra_days = extra_days.subtract( BigInteger.ONE );
			month = (month + 1) % month_count;
		}
		
		if( rules.length > 0 ) {
			result.setupLeapRules( rules );
			year.getMonth( year.getLastMonthIndex() ).leap_days += 1;
		}
		
		return result;
	}
}
