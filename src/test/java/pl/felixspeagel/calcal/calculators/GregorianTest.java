package pl.felixspeagel.calcal.calculators;

import org.junit.jupiter.api.Test;
import pl.felixspeagel.calcal.math.MixedFraction;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class GregorianTest {
	
	@Test
	public void rules_as_Gregory_said() {
		var Gregory_year_value = new MixedFraction( 365, 2425, 10000); // 365.2425
		
		var result = new Gregorian( Gregory_year_value );
		assertEquals(3, result.rules.length);
		
		// each year divisible by 4 is a leap year...
		assertEquals( BigInteger.valueOf( 4 ), result.rules[0].each_year());
		assertEquals( IntercalationType.LEAP, result.rules[0].is_leap());
		// except of those divisible by 100...
		assertEquals( BigInteger.valueOf( 100 ), result.rules[1].each_year());
		assertEquals( IntercalationType.NORMAL, result.rules[1].is_leap());
		// except of those divisible by 400
		assertEquals( BigInteger.valueOf( 400 ), result.rules[2].each_year());
		assertEquals( IntercalationType.LEAP, result.rules[2].is_leap());
	}
	
}