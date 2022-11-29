package pl.felixspeagel.calcal.math;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class MixedFractionTest {
	private final BigInteger zero = BigInteger.ZERO;
	private final BigInteger one = BigInteger.ONE;
	private final BigInteger two = BigInteger.TWO;
	private final BigInteger four = BigInteger.TWO.multiply( BigInteger.TWO );
	private final BigInteger minusOne = BigInteger.valueOf( -1 );
	
	@Test
	public void initializationSimpleNumbers() {
		
		var fraction = new MixedFraction( zero, one, two );
		assertEquals( zero, fraction.getInteger() );
		assertEquals( one, fraction.getNumerator() );
		assertEquals( two, fraction.getDenominator() );
		
		fraction = new MixedFraction( zero, one.multiply( two ), two.multiply( two ) );
		assertEquals( zero, fraction.getInteger() );
		assertEquals( one, fraction.getNumerator() );
		assertEquals( two, fraction.getDenominator() );
		
		fraction = new MixedFraction( one, one, two );
		assertEquals( one, fraction.getInteger() );
		assertEquals( one, fraction.getNumerator() );
		assertEquals( two, fraction.getDenominator() );
		
		fraction = new MixedFraction( one, one.multiply( two ), two.multiply( two ) );
		assertEquals( one, fraction.getInteger() );
		assertEquals( one, fraction.getNumerator() );
		assertEquals( two, fraction.getDenominator() );
	}
	
	@Test
	public void initializationNegativeNumbers() {
		
		var fraction = new MixedFraction( zero, one, two.multiply( minusOne ) );
		assertEquals( zero, fraction.getInteger() );
		assertEquals( one.multiply( minusOne ), fraction.getNumerator() );
		assertEquals( two, fraction.getDenominator() );
		
		fraction = new MixedFraction( zero, one.multiply( minusOne ), two );
		assertEquals( zero, fraction.getInteger() );
		assertEquals( one.multiply( minusOne ), fraction.getNumerator() );
		assertEquals( two, fraction.getDenominator() );
		
		fraction = new MixedFraction( one, one, two.multiply( minusOne ) );
		assertEquals( minusOne , fraction.getInteger() );
		assertEquals( minusOne, fraction.getNumerator() );
		assertEquals( two, fraction.getDenominator() );
		
		fraction = new MixedFraction( one, one.multiply( minusOne ), two );
		assertEquals( minusOne , fraction.getInteger() );
		assertEquals( minusOne, fraction.getNumerator() );
		assertEquals( two, fraction.getDenominator() );
	}
	
	@Test
	public void initializationZeroDenominator() {
		assertThrows( ArithmeticException.class, () -> new MixedFraction( one, one, zero ) );
	}
	
	@Test
	public void isZeroTest() {
		var fraction = new MixedFraction( zero, one, two );
		
		assertFalse( fraction.isZero() );
		
		fraction = new MixedFraction( zero, zero, two );
		
		assertTrue( fraction.isZero() );
	}
	
	@Test
	public void negativeFractions() {
		var fraction = new MixedFraction( zero, one, one );
		assertFalse( fraction.isNegative() );
		
		fraction = new MixedFraction( zero, minusOne, one );
		assertTrue( fraction.isNegative() );
		
		fraction = new MixedFraction( minusOne, zero, one );
		assertTrue( fraction.isNegative() );
		
		fraction = new MixedFraction( minusOne, one, two );
		assertTrue( fraction.isNegative() );
	}
	
	@Test
	public void addition() {
		var fraction = new MixedFraction( zero, one, four );
		var shouldBe = new MixedFraction( zero, one, two );
		
		assertEquals( shouldBe, fraction.add( fraction ) );
		
		fraction = new MixedFraction( one, one, four );
		shouldBe = new MixedFraction( two, one, two );
		
		assertEquals( shouldBe, fraction.add( fraction ) );
		
		fraction = new MixedFraction( zero, one, two );
		shouldBe = new MixedFraction( one, zero, one );
		
		assertEquals( shouldBe, fraction.add( fraction ) );
	}
	
	@Test
	public void subtraction() {
		var fraction1 = new MixedFraction( zero, minusOne, two );
		var fraction2 = new MixedFraction( zero, minusOne, four );
		var shouldBe = new MixedFraction( zero, minusOne, four );
		
		assertEquals( shouldBe, fraction1.subtract( fraction2 ) );
		
		fraction1 = new MixedFraction( minusOne, one, four );
		shouldBe = new MixedFraction( zero, zero, two );
		
		assertEquals( shouldBe, fraction1.subtract( fraction1 ) );
		
		fraction1 = new MixedFraction( minusOne, zero, one );
		fraction2 = new MixedFraction( minusOne.add(minusOne), zero, one );
		shouldBe = new MixedFraction( minusOne, zero, one );
		
		assertEquals( shouldBe, fraction2.subtract( fraction1 ) );
		
		fraction1 = new MixedFraction( one, zero, one );
		fraction2 = new MixedFraction( zero, zero, one );
		shouldBe = new MixedFraction( minusOne, zero, one );
		
		assertEquals( shouldBe, fraction2.subtract( fraction1 ) );
		
		fraction1 = new MixedFraction( minusOne, zero, one );
		fraction2 = new MixedFraction( zero, one, two );
		shouldBe = new MixedFraction( minusOne, one, two );
		
		assertEquals( shouldBe, fraction1.subtract( fraction2 ) );
	}
	
	@Test
	public void multiplication() {
		var fraction = new MixedFraction( one, two );
		var shouldBe = new MixedFraction( one, four );
		
		assertEquals( shouldBe, fraction.multiply( fraction ) );
	}
	
	@Test
	public void division() {
		var fraction = new MixedFraction( one, two );
		var shouldBe = MixedFraction.ONE;
		
		assertEquals( shouldBe, fraction.divide( fraction ) );
	}
	
	@Test
	public void stringRepresentation() {
		var fraction = new MixedFraction( one, zero, one );
		var shouldBe = "1";
		
		assertEquals( shouldBe, fraction.toString() );
		
		fraction = new MixedFraction( zero, one, two );
		shouldBe = "1/2";
		
		assertEquals( shouldBe, fraction.toString() );
		
		fraction = new MixedFraction( one, one, two );
		shouldBe = "1 1/2";
		
		assertEquals( shouldBe, fraction.toString() );
		
		fraction = new MixedFraction( minusOne, zero, one );
		shouldBe = "-1";
		
		assertEquals( shouldBe, fraction.toString() );
		
		fraction = new MixedFraction( zero, minusOne, two );
		shouldBe = "-1/2";
		
		assertEquals( shouldBe, fraction.toString() );
		
		fraction = new MixedFraction( minusOne, one, two );
		shouldBe = "-1 1/2";
		
		assertEquals( shouldBe, fraction.toString() );
		
		fraction = new MixedFraction( one, minusOne, two );
		shouldBe = "-1 1/2";
		
		assertEquals( shouldBe, fraction.toString() );
		
	}
	
	@Test
	public void stringRead() {
		var txt = "1";
		var shouldBe = new MixedFraction( one, zero, one );
		
		assertEquals( shouldBe, MixedFraction.fromString( txt ) );
		
		txt = "1/2";
		shouldBe = new MixedFraction( zero, one, two );
		
		assertEquals( shouldBe, MixedFraction.fromString( txt ) );
		
		txt = "1 1/2";
		shouldBe = new MixedFraction( one, one, two );
		
		assertEquals( shouldBe, MixedFraction.fromString( txt ) );
		
		txt = "-1";
		shouldBe = new MixedFraction( minusOne, zero, one );
		
		assertEquals( shouldBe, MixedFraction.fromString( txt ) );
		
		txt = "-1/2";
		shouldBe = new MixedFraction( zero, minusOne, two );
		
		assertEquals( shouldBe, MixedFraction.fromString( txt ) );
		
		txt = "-1 1/2";
		shouldBe = new MixedFraction( minusOne, one, two );
		
		assertEquals( shouldBe, MixedFraction.fromString( txt ) );
	}
	
	@Test
	public void comparison() {
		var a = new MixedFraction( one );
		var b = new MixedFraction( two );
		
		assertTrue( a.compareTo( b ) < 0 );
		assertTrue( b.compareTo( a ) > 0 );
		
		a = new MixedFraction( two );
		b = new MixedFraction( one, two );
		
		assertTrue( a.compareTo( b ) > 0 );
		assertTrue( b.compareTo( a ) < 0 );
		
		a = new MixedFraction( one, two );
		b = new MixedFraction( one, four );
		
		assertTrue( a.compareTo( b ) > 0 );
		assertTrue( b.compareTo( a ) < 0 );
		
		a = new MixedFraction( one, two );
		b = new MixedFraction( one, two );
		
		assertEquals( 0, a.compareTo( b ) );
	}
	
	@Test
	public void ceil_function() {
		var f = new MixedFraction( 42 );
		var result = f.ceil();
		assertEquals( BigInteger.valueOf( 42 ), result );
		
		f = new MixedFraction( 42, 1, 2 );
		result = f.ceil();
		assertEquals( BigInteger.valueOf( 43 ), result );
		
		f = new MixedFraction( -42 );
		result = f.ceil();
		assertEquals( BigInteger.valueOf( -42 ), result );
		
		f = new MixedFraction( -42, 1, 2 );
		result = f.ceil();
		assertEquals( BigInteger.valueOf( -42 ), result );
		
		f = MixedFraction.ZERO;
		result = f.ceil();
		assertEquals( BigInteger.ZERO, result );
		
		f = new MixedFraction(  1, 2 );
		result = f.ceil();
		assertEquals( BigInteger.ONE, result );
		
		f = new MixedFraction( -1, 2 );
		result = f.ceil();
		assertEquals( BigInteger.ZERO, result );
	}
	
	@Test
	public void floor_function() {
		var f = new MixedFraction( 42 );
		var result = f.floor();
		assertEquals( BigInteger.valueOf( 42 ), result );
		
		f = new MixedFraction( 42, 1, 2 );
		result = f.floor();
		assertEquals( BigInteger.valueOf( 42 ), result );
		
		f = new MixedFraction( -42 );
		result = f.floor();
		assertEquals( BigInteger.valueOf( -42 ), result );
		
		f = new MixedFraction( -42, 1, 2 );
		result = f.floor();
		assertEquals( BigInteger.valueOf( -43 ), result );
		
		f = MixedFraction.ZERO;
		result = f.floor();
		assertEquals( BigInteger.ZERO, result );
		
		f = new MixedFraction(  1, 2 );
		result = f.floor();
		assertEquals( BigInteger.ZERO, result );
		
		f = new MixedFraction( -1, 2 );
		result = f.floor();
		assertEquals( BigInteger.valueOf( -1 ), result );
	}
}