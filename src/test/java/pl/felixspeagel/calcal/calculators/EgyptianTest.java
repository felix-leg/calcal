package pl.felixspeagel.calcal.calculators;

import org.junit.jupiter.api.Test;
import pl.felixspeagel.calcal.math.MixedFraction;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class EgyptianTest {
	
	private final MixedFraction EGYPTIAN_YEAR = new MixedFraction( 365, 1, 4 );
	private final MixedFraction EGYPTIAN_MONTH = new MixedFraction( 29, 1, 2 );
	@SuppressWarnings("FieldCanBeLocal")
	private final int MONTHS_IN_YEAR = 12;
	
	@Test
	public void ancient_egypt_calendar() {
		var calendar = new Egyptian( EGYPTIAN_YEAR, EGYPTIAN_MONTH, MONTHS_IN_YEAR );
		
		assertEquals( BigInteger.valueOf( 30 ), calendar.days_in_month );
		assertEquals( BigInteger.valueOf( 360 ), calendar.days_in_year );
		assertEquals( BigInteger.valueOf( 5 ), calendar.epagomenal_days );
		assertEquals( BigInteger.valueOf( 1 ), calendar.add_leap_days );
		assertEquals( BigInteger.valueOf( 4 ), calendar.every_year );
	}
	
}