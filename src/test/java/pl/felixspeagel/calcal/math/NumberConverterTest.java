package pl.felixspeagel.calcal.math;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class NumberConverterTest {
	
	@BeforeEach
	void setUp() {
		Locale.setDefault( Locale.ROOT );
	}
	
	private static final HMSRecord EARTH_DAY = new HMSRecord(24, 60, 60);
	
	@Test
	void DHMS_textToFraction_incorrectText() {
		var random_text = "random text";
		assertThrows( WrongNumberFormat.class, () -> NumberConverter.DHMS_textToFraction( random_text, EARTH_DAY ) );
		
		var double_string = "5 5h 5m 5s 3 3h 3m 3s";
		assertThrows( WrongNumberFormat.class, () -> NumberConverter.DHMS_textToFraction( double_string, EARTH_DAY ) );
	}
	
	@Test
	void DHMS_textToFraction_correctText() throws WrongNumberFormat {
		var text = "45";
		var correct = new MixedFraction( 45 );
		var result = NumberConverter.DHMS_textToFraction( text, EARTH_DAY );
		assertEquals( correct, result );
		
		text = "45 12h";
		correct = new MixedFraction( 45, 1, 2 );
		result = NumberConverter.DHMS_textToFraction( text, EARTH_DAY );
		assertEquals( correct, result );
		
		text = "45 0h 30m";
		correct = new MixedFraction( 45, 1, 48 );
		result = NumberConverter.DHMS_textToFraction( text, EARTH_DAY );
		assertEquals( correct, result );
		
		text = "45 0h 0m 30s";
		correct = new MixedFraction( 45, 1, 2880 );
		result = NumberConverter.DHMS_textToFraction( text, EARTH_DAY );
		assertEquals( correct, result );
	}
	
	@Test
	void FractionToDHMS_text() {
		var correct = "45";
		var result = NumberConverter.FractionToDHMS_text( new MixedFraction( 45 ), EARTH_DAY );
		assertEquals( correct, result );
		
		correct = "45 12h";
		result = NumberConverter.FractionToDHMS_text( new MixedFraction( 45, 1, 2 ), EARTH_DAY );
		assertEquals( correct, result );
		
		correct = "45 0h 30m";
		result = NumberConverter.FractionToDHMS_text( new MixedFraction( 45, 1, 48 ), EARTH_DAY );
		assertEquals( correct, result );
		
		correct = "45 0h 0m 30s";
		result = NumberConverter.FractionToDHMS_text( new MixedFraction( 45, 1, 2880 ), EARTH_DAY );
		assertEquals( correct, result );
	}
	
	@Test
	void FractionToDecimalText_normalFractions() {
		var one_eighth = new MixedFraction( 1, 8 );
		var result = NumberConverter.FractionToDecimalText( one_eighth );
		assertEquals( "0.125", result );
		
		var zero = MixedFraction.ZERO;
		result = NumberConverter.FractionToDecimalText( zero );
		assertEquals( "0", result );
		
		var minus_one_eighth = new MixedFraction( -1, 8 );
		result = NumberConverter.FractionToDecimalText( minus_one_eighth );
		assertEquals( "-0.125", result );
		
		var wholeNumber = new MixedFraction( 27 );
		result = NumberConverter.FractionToDecimalText( wholeNumber );
		assertEquals( "27", result );
		
		var minusWholeNumber = new MixedFraction( -27 );
		result = NumberConverter.FractionToDecimalText( minusWholeNumber );
		assertEquals( "-27", result );
	}
	
	@Test
	void DecimalTextToFraction_normalFractions() throws WrongNumberFormat {
		var one_eighth = new MixedFraction( 1, 8 );
		var result = NumberConverter.DecimalTextToFraction( "0.125" );
		assertEquals( one_eighth, result );
		
		var zero = MixedFraction.ZERO;
		result = NumberConverter.DecimalTextToFraction( "0" );
		assertEquals( zero, result );
		
		var minus_one_eighth = new MixedFraction( -1, 8 );
		result = NumberConverter.DecimalTextToFraction( "-0.125" );
		assertEquals( minus_one_eighth, result );
		
		var wholeNumber = new MixedFraction( 27 );
		result = NumberConverter.DecimalTextToFraction( "27" );
		assertEquals( wholeNumber, result );
		
		var minusWholeNumber = new MixedFraction( -27 );
		result = NumberConverter.DecimalTextToFraction( "-27" );
		assertEquals( minusWholeNumber, result );
	}
	
	@Test
	void FractionToDecimalText_periodFractions() {
		var one_third = new MixedFraction( 1, 3 );
		var result = NumberConverter.FractionToDecimalText( one_third );
		assertEquals( "0.(3)", result );
		
		var complicated = new MixedFraction( 13, 30 );
		result = NumberConverter.FractionToDecimalText( complicated );
		assertEquals( "0.4(3)", result );
		
		var zero_in_period = new MixedFraction( 133, 330 );
		result = NumberConverter.FractionToDecimalText( zero_in_period );
		assertEquals( "0.4(03)", result );
		
		var minus_period = new MixedFraction( -13, 30 );
		result = NumberConverter.FractionToDecimalText( minus_period );
		assertEquals( "-0.4(3)", result );
		
		var with_whole_number = new MixedFraction( 42, 13, 30 );
		result = NumberConverter.FractionToDecimalText( with_whole_number );
		assertEquals( "42.4(3)", result );
	}
	
	@Test
	void DecimalTextToFraction_periodFractions() throws WrongNumberFormat {
		var one_third = new MixedFraction( 1, 3 );
		var result = NumberConverter.DecimalTextToFraction( "0.(3)" );
		assertEquals( one_third, result );
		
		var complicated = new MixedFraction( 13, 30 );
		result = NumberConverter.DecimalTextToFraction( "0.4(3)" );
		assertEquals( complicated, result );
		
		var zero_in_period = new MixedFraction( 133, 330 );
		result = NumberConverter.DecimalTextToFraction( "0.4(03)" );
		assertEquals( zero_in_period, result );
		
		var minus_period = new MixedFraction( -13, 30 );
		result = NumberConverter.DecimalTextToFraction( "-0.4(3)" );
		assertEquals( minus_period, result );
		
		var with_whole_number = new MixedFraction( 42, 13, 30 );
		result = NumberConverter.DecimalTextToFraction( "42.4(3)" );
		assertEquals( with_whole_number, result );
	}
	
}