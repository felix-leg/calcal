package pl.felixspeagel.calcal.math;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class FunctionsTest {
	
	@Test
	public void knownGCD() {
		var a = BigInteger.valueOf( 4 );
		var b = BigInteger.valueOf( 6 );
		var shouldBe = BigInteger.valueOf( 2 );
		
		var result = Functions.gcd( a, b );
		
		assertEquals( shouldBe, result );
	}
	
	@Test
	public void negativeArgumentsGCD() {
		var a = BigInteger.valueOf( 4 );
		var b = BigInteger.valueOf( 6 );
		var minusOne = BigInteger.valueOf( -1 );
		var shouldBe = BigInteger.valueOf( 2 );
		
		var result = Functions.gcd( a.multiply( minusOne ), b );
		assertEquals( shouldBe, result );
		
		result = Functions.gcd( a, b.multiply( minusOne ) );
		assertEquals( shouldBe, result );
		
		result = Functions.gcd( a.multiply( minusOne ), b.multiply( minusOne ) );
		assertEquals( shouldBe, result );
	}
	
	@Test
	public void argumentsReversedGCD() {
		var a = BigInteger.valueOf( 4 );
		var b = BigInteger.valueOf( 6 );
		var shouldBe = BigInteger.valueOf( 2 );
		
		var result = Functions.gcd( b, a );
		
		assertEquals( shouldBe, result );
	}
	
	@Test
	public void zeroGCD() {
		var a = BigInteger.ZERO;
		var num = BigInteger.valueOf( 4 );
		
		var result = Functions.gcd( a, num );
		
		assertEquals( num, result );
		
		result = Functions.gcd( num, a );
		
		assertEquals( num, result );
	}
	
	@Test
	public void bothArgumentsZeroGCD() {
		var result = Functions.gcd( BigInteger.ZERO, BigInteger.ZERO );
		
		assertEquals( BigInteger.ZERO, result );
	}
	
	@Test
	public void divisors_of_400() {
		var result = Functions.divisors_of( BigInteger.valueOf( 400 ) );
		var shouldBe = new Integer[] {
				1,
				2,
				4,
				5,
				8,
				10,
				16,
				20,
				25,
				40,
				50,
				80,
				100,
				200,
				400
		};
		
		assertEquals( shouldBe.length, result.length );
		for(int i=0; i<result.length; i++) {
			assertEquals( BigInteger.valueOf( shouldBe[i] ), result[i] );
		}
	}
	
	@Test
	public void divisors_of_zero() {
		var result = Functions.divisors_of( BigInteger.ZERO );
		assertEquals(0, result.length); // no divisors
	}
	
	@Test
	public void divisors_of_a_negative_number() {
		var result = Functions.divisors_of( BigInteger.TEN.negate() );
		
		assertEquals( 4, result.length );
		
		assertEquals( BigInteger.valueOf( 1 ), result[0]);
		assertEquals( BigInteger.valueOf( 2 ), result[1]);
		assertEquals( BigInteger.valueOf( 5 ), result[2]);
		assertEquals( BigInteger.valueOf( 10 ), result[3]);
	}
}