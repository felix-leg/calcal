package pl.felixspeagel.calcal.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.felixspeagel.calcal.calculators.IntercalationType;
import pl.felixspeagel.calcal.calculators.Rule;
import pl.felixspeagel.calcal.calendar.Calendar;
import pl.felixspeagel.calcal.calendar.SpecialFeature;
import pl.felixspeagel.calcal.controllers.models.CalendarTypeData;
import pl.felixspeagel.calcal.controllers.models.YearMonthLengthData;
import pl.felixspeagel.calcal.math.HMSRecord;
import pl.felixspeagel.calcal.math.MixedFraction;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class CalendarEditorTest {
	
	@BeforeEach
	void setUp() {
		//this object is not required for this test suit to function
		YearMonthLengthData year_month = new YearMonthLengthData(
				new MixedFraction( 29, 1, 2 ),
				new MixedFraction( 365, 1, 4 ),
				12,
				new HMSRecord( 24, 60, 60 )
		);
		var calendar_cycle = new Calendar( SpecialFeature.LEAP );
		calendar_cycle.setupCycle( new IntercalationType[]{
				IntercalationType.NORMAL,
				IntercalationType.LEAP,
				IntercalationType.NORMAL
		} );
		calendar_cycle.getYear().addMonth( "Test 1", 29, 0 );
		calendar_cycle.getYear().addMonth( "Test 2", 30, 0 );
		calendar_cycle.getYear().setFeatureMonth( calendar_cycle.getYear().getLastMonthIndex() );
		
		calendar_with_a_cycle = new CalendarTypeData(
				CalendarTypeInput.Solution.METON,
				calendar_cycle,
				year_month
		);
		
		var calendar_rules = new Calendar( SpecialFeature.NONE );
		calendar_rules.setupLeapRules( new Rule[]{
				new Rule( BigInteger.valueOf(4), IntercalationType.LEAP )
		} );
		calendar_rules.getYear().addMonth( "Test 1", 29, 1 );
		calendar_rules.getYear().addMonth( "Test 2", 30, 0 );
		
		calendar_with_leap_rules = new CalendarTypeData(
				CalendarTypeInput.Solution.GREGORIAN,
				calendar_rules,
				year_month
		);
	}
	
	private CalendarTypeData calendar_with_a_cycle;
	private CalendarTypeData calendar_with_leap_rules;
	
	@Test
	public void detect_cycles_and_rules() {
		var editor = new CalendarEditor( null );
		editor.setPreviousData( calendar_with_a_cycle );
		
		assertTrue( editor.hasYearCycle() );
		assertFalse( editor.hasLeapRules() );
		
		editor = new CalendarEditor( null );
		editor.setPreviousData( calendar_with_leap_rules );
		
		assertFalse( editor.hasYearCycle() );
		assertTrue( editor.hasLeapRules() );
	}
	
	@Test
	public void turn_years_in_cycle_on_and_off() {
		var editor = new CalendarEditor( null );
		editor.setPreviousData( calendar_with_a_cycle );
		
		assertEquals( 3, editor.getCycle().length );
		
		assertFalse( editor.isYearInCycleTurnedOn( 0 ) );
		assertTrue( editor.isYearInCycleTurnedOn( 1 ) );
		assertFalse( editor.isYearInCycleTurnedOn( 2 ) );
		
		editor.turnYearInCycleOn( 1, false );
		assertFalse( editor.isYearInCycleTurnedOn( 1 ) );
		assertEquals( 1, editor.surplusYearsToAssignInCycle() );
		
		editor.turnYearInCycleOn( 1, true );
		assertTrue( editor.isYearInCycleTurnedOn( 1 ) );
		assertEquals( 0, editor.surplusYearsToAssignInCycle() );
		
		editor.turnYearInCycleOn( 0, true );
		assertTrue( editor.isYearInCycleTurnedOn( 0 ) );
		assertEquals( -1, editor.surplusYearsToAssignInCycle() );
		
		editor.turnYearInCycleOn( 0, false );
		assertFalse( editor.isYearInCycleTurnedOn( 0 ) );
		assertEquals( 0, editor.surplusYearsToAssignInCycle() );
	}
	
	@Test
	public void switch_rules_on_and_off() {
		var editor = new CalendarEditor( null );
		editor.setPreviousData( calendar_with_leap_rules );
		
		assertEquals( 1, editor.getRules().length );
		
		assertTrue( editor.isRuleTurnedOn( 0 ) );
		editor.turnRuleOn( 0, false );
		assertFalse( editor.isRuleTurnedOn( 0 ) );
		editor.turnRuleOn( 0, true );
		assertTrue( editor.isRuleTurnedOn( 0 ) );
	}
	
	@Test
	public void surf_through_months() {
		var editor = new CalendarEditor( null );
		editor.setPreviousData( calendar_with_leap_rules );
		
		assertEquals( 0, editor.getSelectedMonthIndex() );
		
		editor.selectFirstMonth();
		assertEquals( "Test 1", editor.getSelectedMonth().name );
		assertEquals( 29, editor.getSelectedMonth().normal_days );
		assertEquals( 1, editor.getSelectedMonth().leap_days );
		
		editor.selectLastMonth();
		assertEquals( "Test 2", editor.getSelectedMonth().name );
		assertEquals( 30, editor.getSelectedMonth().normal_days );
		assertEquals( 0, editor.getSelectedMonth().leap_days );
		
		editor.selectPreviousMonth();
		assertEquals( "Test 1", editor.getSelectedMonth().name );
		assertEquals( 29, editor.getSelectedMonth().normal_days );
		assertEquals( 1, editor.getSelectedMonth().leap_days );
		
		editor.selectNextMonth();
		assertEquals( "Test 2", editor.getSelectedMonth().name );
		assertEquals( 30, editor.getSelectedMonth().normal_days );
		assertEquals( 0, editor.getSelectedMonth().leap_days );
	}
	
	@Test
	public void add_and_remove_normal_days() {
		var editor = new CalendarEditor( null );
		editor.setPreviousData( calendar_with_leap_rules );
		
		editor.selectLastMonth();
		
		assertTrue( editor.canRemoveNormalDay() );
		editor.removeNormalDay();
		assertEquals( 29, editor.getSelectedMonth().normal_days );
		assertEquals( BigInteger.ONE, editor.getNormalDaysPool() );
		
		editor.selectFirstMonth();
		
		assertTrue( editor.canAddNormalDay() );
		editor.addNormalDay();
		assertEquals( 30, editor.getSelectedMonth().normal_days );
		assertEquals( BigInteger.ZERO, editor.getNormalDaysPool() );
	}
	
	@Test
	public void add_and_remove_leap_days() {
		var editor = new CalendarEditor( null );
		editor.setPreviousData( calendar_with_leap_rules );
		
		editor.selectFirstMonth();
		
		assertTrue( editor.canRemoveLeapDay() );
		editor.removeLeapDay();
		assertEquals( 0, editor.getSelectedMonth().leap_days );
		assertEquals( BigInteger.ONE, editor.getLeapDaysPool() );
		
		editor.selectLastMonth();
		
		assertTrue( editor.canAddLeapDay() );
		editor.addLeapDay();
		assertEquals( 1, editor.getSelectedMonth().leap_days );
		assertEquals( BigInteger.ZERO, editor.getNormalDaysPool() );
	}
	
	@Test
	public void add_and_remove_months() {
		var editor = new CalendarEditor( null );
		editor.setPreviousData( calendar_with_leap_rules );
		
		editor.selectLastMonth();
		
		assertTrue( editor.canRemoveNormalDay() );
		editor.removeNormalDay();
		assertTrue( editor.canRemoveNormalDay() );
		editor.removeNormalDay();
		assertTrue( editor.canRemoveNormalDay() );
		editor.removeNormalDay();
		assertEquals( 27, editor.getSelectedMonth().normal_days );
		assertEquals( BigInteger.valueOf( 3 ), editor.getNormalDaysPool() );
		
		assertTrue( editor.canAddMonth() );
		editor.addMonth();
		assertEquals( 3, editor.getSelectedMonth().normal_days );
		assertEquals( BigInteger.ZERO, editor.getNormalDaysPool() );
		
		assertTrue( editor.canRemoveMonth() );
		editor.removeMonth();
		assertEquals( BigInteger.valueOf( 3 ), editor.getNormalDaysPool() );
		
		assertFalse( editor.canRemoveMonth() );
	}
}