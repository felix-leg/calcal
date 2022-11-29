package pl.felixspeagel.calcal.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedList;

/**
 * Common mathematical functions
 */
public class Functions {
	
	/**
	 * Computes The Greatest Common Divisor
	 * @param a first BigInteger
	 * @param b second BigInteger
	 * @return A BigInteger >= 0
	 */
	public static BigInteger gcd(BigInteger a, BigInteger b) {
		if( a.compareTo( BigInteger.ZERO) < 0) { // a < 0
			a = a.multiply( BigInteger.valueOf( -1 ) ); // a *= -1
		}
		if( b.compareTo( BigInteger.ZERO ) < 0 ) { // b < 0
			b = b.multiply( BigInteger.valueOf( -1 ) ); // b *= -1
		}
		
		BigInteger t;
		while(!b.equals( BigInteger.ZERO )) { // b != 0
			t = b;
			b = a.mod( b );
			a = t;
		}
		return a;
	}
	
	public static BigInteger[] divisors_of(BigInteger number) {
		number = number.abs();
		var i = BigInteger.ONE;
		final var sqrt = BigDecimal.valueOf( Math.sqrt( number.doubleValue() ) ).toBigInteger();
		var divisors = new LinkedList<BigInteger>();
		
		while( i.compareTo( sqrt ) <= 0 ) {
			if( number.remainder( i ).equals( BigInteger.ZERO ) ) {
				if( number.divide( i ).equals( i ) ) {
					divisors.add( i );
				} else {
					divisors.add( i );
					divisors.add( number.divide( i ) );
				}
			}
			i = i.add( BigInteger.ONE );
		}
		
		return divisors.stream().sorted().toArray( BigInteger[]::new );
	}
}
