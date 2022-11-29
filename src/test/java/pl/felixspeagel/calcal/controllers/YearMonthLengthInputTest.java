package pl.felixspeagel.calcal.controllers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class YearMonthLengthInputTest {
	
	@Test
	public void wrong_inputs_on_hms() {
		var input = new YearMonthLengthInput();
		
		input.getDayInput().enterHoursText( "" );
		assertTrue( input.getDayInput().inErrorState() );
		input.getDayInput().enterHoursText( "-12" );
		assertTrue( input.getDayInput().inErrorState() );
		input.getDayInput().enterHoursText( "12" );
		assertFalse( input.getDayInput().inErrorState() );
		
		input.getDayInput().enterMinutesText( "" );
		assertTrue( input.getDayInput().inErrorState() );
		input.getDayInput().enterMinutesText( "-12" );
		assertTrue( input.getDayInput().inErrorState() );
		input.getDayInput().enterMinutesText( "12" );
		assertFalse( input.getDayInput().inErrorState() );
		
		input.getDayInput().enterSecondsText( "" );
		assertTrue( input.getDayInput().inErrorState() );
		input.getDayInput().enterSecondsText( "-12" );
		assertTrue( input.getDayInput().inErrorState() );
		input.getDayInput().enterSecondsText( "12" );
		assertFalse( input.getDayInput().inErrorState() );
	}
	
	@Test
	public void wrong_inputs_on_year() {
		var input = new YearMonthLengthInput();
		var tested = input.getYearInput();
		
		tested.setText( "" );
		assertTrue( tested.inErrorState() );
		
		tested.setText( "wrong" );
		assertTrue( tested.inErrorState() );
		
		tested.setText( "-123" );
		assertTrue( tested.inErrorState() );
		
		tested.setText( "123" );
		assertFalse( tested.inErrorState() );
	}
	
	@Test
	public void wrong_inputs_on_month() {
		var input = new YearMonthLengthInput();
		var tested = input.getMonthInput();
		
		tested.setText( "" );
		assertTrue( tested.inErrorState() );
		
		tested.setText( "wrong" );
		assertTrue( tested.inErrorState() );
		
		tested.setText( "-123" );
		assertTrue( tested.inErrorState() );
		
		tested.setText( "123" );
		assertFalse( tested.inErrorState() );
	}
}