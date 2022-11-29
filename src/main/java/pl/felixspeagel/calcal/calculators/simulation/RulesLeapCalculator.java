package pl.felixspeagel.calcal.calculators.simulation;

import pl.felixspeagel.calcal.calculators.IntercalationType;
import pl.felixspeagel.calcal.calculators.Rule;

import java.math.BigInteger;

public class RulesLeapCalculator implements LeapCalculator {
	
	private final Rule[] the_rules;
	private BigInteger current_year;
	
	public RulesLeapCalculator(Rule[] rules) {
		the_rules = rules;
		current_year = BigInteger.ONE;
	}
	
	
	@Override
	public void setEraYear(BigInteger year) {
		current_year = year;
	}
	
	@Override
	public BigInteger leapYearsUntil(BigInteger year) {
		var leap_count = BigInteger.ZERO;
		for(var rule : the_rules) {
			if( rule.is_leap() == IntercalationType.LEAP ) {
				leap_count = leap_count.add( year.abs().divide( rule.each_year() ) );
			} else if( rule.is_leap() == IntercalationType.NORMAL ) {
				leap_count = leap_count.subtract( year.abs().divide( rule.each_year() ) );
			}
		}
		return leap_count;
	}
	
	@Override
	public boolean isLeapYear() {
		var is_leap = false;
		for(var rule : the_rules) {
			if( current_year.remainder( rule.each_year() ).equals( BigInteger.ZERO ) ) {
				is_leap = rule.is_leap() == IntercalationType.LEAP;
			}
		}
		return is_leap;
	}
}
