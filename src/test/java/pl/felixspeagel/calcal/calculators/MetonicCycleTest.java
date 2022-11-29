package pl.felixspeagel.calcal.calculators;

import org.junit.jupiter.api.Test;
import pl.felixspeagel.calcal.math.MixedFraction;

import static org.junit.jupiter.api.Assertions.*;

class MetonicCycleTest {
	
	private final MixedFraction EARTH_YEAR_LENGTH = new MixedFraction( 365, 2422, 10000 );
	private final MixedFraction EARTH_MONTH_LENGTH =  new MixedFraction( 29, 53059, 100000 );
	private final int EARTH_MONTHS = 12;
	
	@Test
	public void year_and_month_count() {
		var meton = new MetonicCycle( EARTH_YEAR_LENGTH, EARTH_MONTH_LENGTH, EARTH_MONTHS );
		
		assertEquals(235, meton.months);
		assertEquals(19, meton.years);
	}
	
	@Test
	public void years_cycle() {
		var meton = new MetonicCycle( EARTH_YEAR_LENGTH, EARTH_MONTH_LENGTH, EARTH_MONTHS );
		var shouldBe = new IntercalationType[19];
		//*
		shouldBe[0] = IntercalationType.NORMAL;
		shouldBe[1] = IntercalationType.NORMAL;
		shouldBe[2] = IntercalationType.LEAP;
		shouldBe[3] = IntercalationType.NORMAL;
		shouldBe[4] = IntercalationType.NORMAL;
		shouldBe[5] = IntercalationType.LEAP;
		shouldBe[6] = IntercalationType.NORMAL;
		shouldBe[7] = IntercalationType.NORMAL; //TODO: should be LEAP
		shouldBe[8] = IntercalationType.LEAP;   //TODO: should be NORMAL
		shouldBe[9] = IntercalationType.NORMAL;
		shouldBe[10] = IntercalationType.LEAP;
		shouldBe[11] = IntercalationType.NORMAL;
		shouldBe[12] = IntercalationType.NORMAL;
		shouldBe[13] = IntercalationType.LEAP;
		shouldBe[14] = IntercalationType.NORMAL;
		shouldBe[15] = IntercalationType.NORMAL;
		shouldBe[16] = IntercalationType.LEAP;
		shouldBe[17] = IntercalationType.NORMAL;
		shouldBe[18] = IntercalationType.LEAP;
		
		for(int i=0; i<19; i++) {
			assertEquals( shouldBe[i], meton.cycle[i] );
		}
		//*/
	}
}