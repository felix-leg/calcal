package pl.felixspeagel.calcal.calculators;

import pl.felixspeagel.calcal.calendar.Calendar;
import pl.felixspeagel.calcal.calendar.SpecialFeature;
import pl.felixspeagel.calcal.math.MixedFraction;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * Calculates a Meton cycle out of year and month lengths.
 */
public class MetonicCycle implements CalendarCreator {
	
	public final int years;
	public final int months;
	public final IntercalationType[] cycle;
	
	public MetonicCycle(MixedFraction year_length, MixedFraction month_length, int expected_months) {
		final var stopMargin = new MixedFraction( BigInteger.ONE );
		ArrayList<IntercalationType> buildCycle = new ArrayList<>();
		
		var thisMargin = year_length.subtract( month_length ).abs();
		int yearCount = 1;
		int monthCount = 0;
		var monthDays = MixedFraction.ZERO;
		var yearDays = year_length;
		
		while( thisMargin.compareTo( stopMargin ) > 0 ) {
			monthCount++;
			monthDays = month_length.multiply( BigInteger.valueOf( monthCount ) );
			thisMargin = yearDays.subtract( monthDays ).abs();
			
			if( yearDays.compareTo( monthDays ) < 0 && thisMargin.compareTo( stopMargin ) > 0 ) {
				yearCount++;
				yearDays = year_length.multiply( BigInteger.valueOf( yearCount ) );
			}
		}
		years = yearCount;
		months = monthCount;
		
		//TODO: find a better algorithm. The bellow gives a *wrong* list
		
		var days_a = month_length.floor();
		var days_b = month_length.ceil();
		BigInteger temp;
		yearDays = MixedFraction.ZERO;
		
		for(int year=1; year<=years; year++) {
			for(int month=0; month<expected_months; month++) {
				yearDays = yearDays.add( month_length );
				//swap a <> b
				temp = days_a;
				days_a = days_b;
				days_b = temp;
			}
			var diff = year_length.multiply( year ).subtract( yearDays );
			//System.out.print("Year "+year+": "+yearDays+"\t"+diff+"\t\t");
			if( diff.compareTo( new MixedFraction( days_a ) ) >= 0 ) {
				//extra month
				yearDays = yearDays.add( month_length );
				//swap a <> b
				temp = days_a;
				days_a = days_b;
				days_b = temp;
				//System.out.println("LEAP");
				buildCycle.add( IntercalationType.LEAP );
			} else {
				//System.out.println("NORMAL");
				buildCycle.add( IntercalationType.NORMAL );
			}
		}
		
		cycle = buildCycle.toArray(new IntercalationType[0]);
	}
	
	@Override
	public Calendar makeExampleCalendar(MixedFraction year_length, MixedFraction month_length, int month_count) {
		var result = new Calendar( SpecialFeature.LEAP );
		var year = result.getYear();
		//TODO: check bellow algorithm if the above method has been changed
		
		var days_a = month_length.floor();
		var days_b = month_length.ceil();
		BigInteger temp;
		
		if( cycle.length > 0 ) {
			month_count += 1;
		}
		
		for(int month=0; month<month_count; month++) {
			year.addMonth( "#"+(month+1), days_a.intValue(), 0 );
			temp = days_a;
			days_a = days_b;
			days_b = temp;
		}
		if( cycle.length > 0 ) {
			year.setFeatureMonth( year.getLastMonthIndex() );
			result.setupCycle( cycle );
		}
		
		return result;
	}
}
