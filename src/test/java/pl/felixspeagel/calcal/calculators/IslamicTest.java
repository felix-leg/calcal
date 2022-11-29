package pl.felixspeagel.calcal.calculators;

import org.junit.jupiter.api.Test;
import pl.felixspeagel.calcal.math.MixedFraction;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class IslamicTest {

	private final MixedFraction ISLAMIC_MONTH = new MixedFraction( 29, 191, 360 );
	@SuppressWarnings("FieldCanBeLocal")
	private final int ISLAMIC_MONTHS_IN_YEAR = 12;
	
	@Test
	public void calculate_normal() {
		var result = new Islamic( ISLAMIC_MONTH, ISLAMIC_MONTHS_IN_YEAR );
		
		assertEquals( BigInteger.valueOf( 354 ), result.days_in_year);
		assertEquals( BigInteger.valueOf( 11 ), result.days_to_add);
		assertEquals( 30, result.cycle.length);
		
		assertEquals( IntercalationType.NORMAL, result.cycle[0] ); // 1
		assertEquals( IntercalationType.LEAP, result.cycle[1] ); // 2
		assertEquals( IntercalationType.NORMAL, result.cycle[2] ); // 3
		assertEquals( IntercalationType.NORMAL, result.cycle[3] ); // 4
		assertEquals( IntercalationType.LEAP, result.cycle[4] ); // 5
		assertEquals( IntercalationType.NORMAL, result.cycle[5] ); // 6
		assertEquals( IntercalationType.LEAP, result.cycle[6] ); // 7
		assertEquals( IntercalationType.NORMAL, result.cycle[7] ); // 8
		assertEquals( IntercalationType.NORMAL, result.cycle[8] ); // 9
		assertEquals( IntercalationType.LEAP, result.cycle[9] ); // 10
		assertEquals( IntercalationType.NORMAL, result.cycle[10] ); // 11
		assertEquals( IntercalationType.NORMAL, result.cycle[11] ); // 12
		assertEquals( IntercalationType.LEAP, result.cycle[12] ); // 13
		assertEquals( IntercalationType.NORMAL, result.cycle[13] ); // 14
		assertEquals( IntercalationType.NORMAL, result.cycle[14] ); // 15
		assertEquals( IntercalationType.LEAP, result.cycle[15] ); // 16
		assertEquals( IntercalationType.NORMAL, result.cycle[16] ); // 17
		assertEquals( IntercalationType.LEAP, result.cycle[17] ); // 18
		assertEquals( IntercalationType.NORMAL, result.cycle[18] ); // 19
		assertEquals( IntercalationType.NORMAL, result.cycle[19] ); // 20
		assertEquals( IntercalationType.LEAP, result.cycle[20] ); // 21
		assertEquals( IntercalationType.NORMAL, result.cycle[21] ); // 22
		assertEquals( IntercalationType.NORMAL, result.cycle[22] ); // 23
		assertEquals( IntercalationType.LEAP, result.cycle[23] ); // 24
		assertEquals( IntercalationType.NORMAL, result.cycle[24] ); // 25
		assertEquals( IntercalationType.LEAP, result.cycle[25] ); // 26
		assertEquals( IntercalationType.NORMAL, result.cycle[26] ); // 27
		assertEquals( IntercalationType.NORMAL, result.cycle[27] ); // 28
		assertEquals( IntercalationType.LEAP, result.cycle[28] ); // 29
		assertEquals( IntercalationType.NORMAL, result.cycle[29] ); // 30
		
	}
	
}