package pl.felixspeagel.calcal.calculators;

import org.junit.jupiter.api.Test;
import pl.felixspeagel.calcal.math.MixedFraction;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class PureLunarTest {
	
	private final MixedFraction MONTH_LENGTH_3_LEAP = new MixedFraction( 1057, 36 );
	private final MixedFraction MONTH_LENGTH_UNCOUNTABLE = new MixedFraction( 29, 53, 100 );
	private final MixedFraction MONTH_LENGTH_TEST = new MixedFraction( 35436707, 1200000 );
	private final Integer MONTHS_IN_YEAR = 12;
	
	@Test
	public void calculate_normal() {
		var result = new PureLunar( MONTH_LENGTH_3_LEAP, MONTHS_IN_YEAR );
		
		assertFalse( result.unable_to_compute );
		assertEquals( 1, result.rules.length );
		assertEquals( BigInteger.valueOf(3), result.rules[0].each_year() );
		assertEquals( IntercalationType.LEAP, result.rules[0].is_leap() );
	}
	
	@Test
	public void calculate_test() {
		var result = new PureLunar( MONTH_LENGTH_TEST, MONTHS_IN_YEAR );
		
		assertEquals( BigInteger.valueOf(354), result.lunar_year);
	}
	
	@Test
	public void calculate_uncountable() {
		var result = new PureLunar( MONTH_LENGTH_UNCOUNTABLE, MONTHS_IN_YEAR );
		assertTrue( result.unable_to_compute );
	}
	
}