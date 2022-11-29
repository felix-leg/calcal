package pl.felixspeagel.calcal.math;

import java.math.BigInteger;

/**
 * Mixed Fraction — a number with an integer and a fraction part
 */
public class MixedFraction implements Comparable<MixedFraction> {
	private final BigInteger numerator;
	private final BigInteger denominator;
	
	private static final BigInteger minusOne = BigInteger.valueOf( -1 );
	
	public static final MixedFraction ZERO = new MixedFraction( BigInteger.ZERO, BigInteger.ZERO, BigInteger.ONE );
	public static final MixedFraction ONE = new MixedFraction( BigInteger.ONE, BigInteger.ZERO, BigInteger.ONE );
	public static final MixedFraction ONE_HALF = new MixedFraction( BigInteger.ZERO, BigInteger.ONE, BigInteger.TWO );
	
	public MixedFraction(BigInteger i, BigInteger n, BigInteger d) {
		if( d.equals( BigInteger.ZERO )) {
			throw new ArithmeticException("Denominator can't be equal zero.");
		}
		
		boolean integerIsNegative = i.compareTo( BigInteger.ZERO ) < 0;
		
		if( d.compareTo( BigInteger.ZERO ) < 0 ) { // d < 0
			n = n.negate();
			d = d.negate();
		}
		
		if( ! i.equals( BigInteger.ZERO )) {
			if( n.compareTo( BigInteger.ZERO ) < 0) { // n < 0
				i = i.negate();
				integerIsNegative = ! integerIsNegative;
				n = n.negate();
			}
		}
		
		var gcd = Functions.gcd( n, d );
		n = n.divide( gcd );
		d = d.divide( gcd );
		
		numerator = n.add(i.abs().multiply( d )).multiply( integerIsNegative ? minusOne : BigInteger.ONE );
		denominator = d;
	}
	
	public MixedFraction(BigInteger i) {
		this(i, BigInteger.ZERO, BigInteger.ONE);
	}
	
	public MixedFraction(BigInteger n, BigInteger d) {
		this( BigInteger.ZERO, n, d);
	}
	
	public MixedFraction(int i, int n, int d) {
		this( BigInteger.valueOf( i ), BigInteger.valueOf( n ), BigInteger.valueOf( d ) );
	}
	public MixedFraction(int i) {
		this( BigInteger.valueOf( i ) );
	}
	public MixedFraction(int n, int d) {
		this( BigInteger.valueOf( n ), BigInteger.valueOf( d ) );
	}
	
	public BigInteger getInteger() {
		if( ! hasIntegerPart() ) { // n < d
			return BigInteger.ZERO;
		} else {
			return numerator.divide( denominator );
		}
	}
	
	public MixedFraction getFraction() {
		return this.subtract( new MixedFraction( this.getInteger() ) );
	}
	
	public boolean hasIntegerPart() {
		return ( numerator.abs().compareTo( denominator ) >= 0 );
	}
	
	public boolean hasFractionPart() {
		return ! getNumerator().equals( BigInteger.ZERO );
	}
	
	public BigInteger getTrueNumerator() {
		return numerator;
	}
	
	public BigInteger getNumerator() {
		return numerator.remainder( denominator );
	}
	
	public BigInteger getDenominator() {
		return denominator;
	}
	
	public boolean isZero() {
		return numerator.equals( BigInteger.ZERO );
	}
	
	public boolean isNegative() {
		return (numerator.compareTo( BigInteger.ZERO ) < 0);
	}
	
	public MixedFraction add(MixedFraction other) {
		return new MixedFraction(
				numerator.multiply(other.denominator).add(other.numerator.multiply(denominator)),
				denominator.multiply(other.denominator)
		);
	}
	
	public MixedFraction add(BigInteger other) {
		return this.add( new MixedFraction( other ) );
	}
	
	public MixedFraction subtract(MixedFraction other) {
		return this.add( other.multiply( minusOne ) );
	}
	
	public MixedFraction subtract(BigInteger other) {
		return this.subtract( new MixedFraction( other ) );
	}
	
	public MixedFraction multiply(BigInteger scalar) {
		return new MixedFraction(
				numerator.multiply(scalar),
				denominator
		);
	}
	public MixedFraction multiply(int scalar) {
		return multiply( BigInteger.valueOf( scalar ) );
	}
	
	public MixedFraction multiply(MixedFraction other) {
		return new MixedFraction(
				numerator.multiply( other.numerator ),
				denominator.multiply( other.denominator )
		);
	}
	
	public MixedFraction divide(MixedFraction other) {
		return new MixedFraction(
				numerator.multiply( other.denominator ),
				denominator.multiply( other.numerator )
		);
	}
	
	public MixedFraction divide(BigInteger scalar) {
		return divide( new MixedFraction( scalar ) );
	}
	
	public MixedFraction divide(int scalar) {
		return divide( BigInteger.valueOf( scalar ) );
	}
	
	public MixedFraction abs() {
		return new MixedFraction(
				numerator.abs(),
				denominator
		);
	}
	
	public MixedFraction negate() {
		return new MixedFraction(
				numerator.negate(),
				denominator
		);
	}
	
	public BigInteger ceil() {
		if( ! isNegative() ) {
			var result = getInteger();
			if( hasFractionPart() ) {
				result = result.add( BigInteger.ONE );
			}
			return result;
		} else {
			return getInteger();
		}
	}
	
	public BigInteger floor() {
		if( isNegative() ) {
			var result = getInteger();
			if( hasFractionPart() ) {
				result = result.subtract( BigInteger.ONE );
			}
			return result;
		} else {
			return getInteger();
		}
	}
	
	@SuppressWarnings("RedundantIfStatement")
	@Override
	public boolean equals(Object obj) {
		if( obj instanceof MixedFraction fraction) {
			if( ! numerator.equals( fraction.numerator ) ) return false;
			if( ! denominator.equals( fraction.denominator ) ) return false;
			
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		final BigInteger prime = BigInteger.valueOf( 919 );
		return prime.multiply( numerator ).add( denominator ).hashCode();
	}
	
	@Override
	public String toString() {
		String result = "";
		boolean integerMinus = false;
		if( hasIntegerPart() ) {
			result = getInteger().toString() + " ";
			if(result.startsWith( "-" )) {
				integerMinus = true;
			}
		}
		if( hasFractionPart() ) {
			if( integerMinus )
				result += getNumerator().negate() + "/" + getDenominator().toString();
			else
				result += getNumerator() + "/" + getDenominator().toString();
		}
		result = result.strip();
		if( result.equals( "" ) ) {
			result = "0";
		}
		return result;
	}
	
	public static MixedFraction fromString(String str) {
		str = str.strip();
		BigInteger i, n, d;
		
		if( str.contains( " " ) ) {
			var strI = str.substring( 0, str.indexOf( ' ' ) );
			str = str.substring( str.indexOf( ' ' )+1 ).strip();
			
			i = new BigInteger( strI );
		} else {
			if( str.contains( "/" ) )
				i = BigInteger.ZERO;
			else {
				i = new BigInteger( str );
				str = "";
			}
		}
		
		if( str.contains( "/" ) ) {
			var strN = str.substring( 0, str.indexOf( '/' ) );
			var strD = str.substring( str.indexOf( '/' )+1 );
			
			n = new BigInteger( strN );
			d = new BigInteger( strD );
		} else {
			n = BigInteger.ZERO;
			d = BigInteger.ONE;
		}
		
		return new MixedFraction( i, n, d );
	}
	
	@Override
	public int compareTo(MixedFraction other) {
		var n1 = numerator.multiply( other.denominator );
		var n2 = other.numerator.multiply( denominator );
		return n1.compareTo( n2 );
	}
}
