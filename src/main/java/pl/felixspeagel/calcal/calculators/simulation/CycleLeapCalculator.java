package pl.felixspeagel.calcal.calculators.simulation;

import pl.felixspeagel.calcal.calculators.IntercalationType;

import java.math.BigInteger;

public class CycleLeapCalculator implements LeapCalculator {
	
	private final IntercalationType[] the_cycle;
	private final int leap_years_in_cycle;
	
	public CycleLeapCalculator(IntercalationType[] cycle) {
		the_cycle = cycle;
		year_in_cycle = 1;
		
		int leap_years = 0;
		for(int i=0; i<the_cycle.length; i++) {
			if( the_cycle[i] == IntercalationType.LEAP ) {
				leap_years++;
			}
		}
		leap_years_in_cycle = leap_years;
	}
	
	private int year_in_cycle;
	
	@Override
	public void setEraYear(BigInteger year) {
		year_in_cycle = year.remainder( BigInteger.valueOf( the_cycle.length ) ).intValue() - 1;
		if( year_in_cycle < 0 ) {
			year_in_cycle += the_cycle.length;
		}
	}
	
	@Override
	public BigInteger leapYearsUntil(BigInteger year) {
		BigInteger full_cycles;
		if( year.compareTo( BigInteger.ZERO ) <= 0 ) {
			full_cycles = year.subtract( BigInteger.ONE ).abs().divide( BigInteger.valueOf( the_cycle.length ) );
		} else {
			full_cycles = year.divide( BigInteger.valueOf( the_cycle.length ) );
		}
		full_cycles = full_cycles.multiply( BigInteger.valueOf( leap_years_in_cycle ) );
		
		var remainder = year.remainder( BigInteger.valueOf( the_cycle.length ) ).intValue();
		
		if( remainder > 0 ) {
			for( int i = 0; i < remainder; i++ ) {
				if( the_cycle[i] == IntercalationType.LEAP ) {
					full_cycles = full_cycles.add( BigInteger.ONE );
				}
			}
		} else {
			for( int i = (the_cycle.length-1); i >= (the_cycle.length+remainder-1) ; i-- ) {
				if( the_cycle[i] == IntercalationType.LEAP ) {
					full_cycles = full_cycles.add( BigInteger.ONE );
				}
			}
		}
		
		return full_cycles;
	}
	
	@Override
	public boolean isLeapYear() {
		return the_cycle[year_in_cycle] == IntercalationType.LEAP;
	}
}
