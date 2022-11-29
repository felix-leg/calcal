package pl.felixspeagel.calcal.math;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.regex.Pattern;
/**
 * Utility class for converting between textual and fraction representation
 */
public class NumberConverter {
	
	private static String getDecimalSeparator() {
		DecimalFormat D_format = (DecimalFormat)DecimalFormat.getInstance();
		var symbols = D_format.getDecimalFormatSymbols();
		char decimalSeparator = symbols.getDecimalSeparator();
		return String.valueOf( decimalSeparator );
	}
	private static String getDecimalFormat() {
		var format = getDecimalSeparator();
		format = "(\\d+)(\\" + format + "(\\d+|\\d*\\(\\d+\\)))?";
		return format;
	}
	
	private static final Pattern DHMS_textPattern = Pattern.compile(
			"^(-?\\d+)(\\s+\\d+h)?(\\s+\\d+m)?(\\s+" + getDecimalFormat() +"s)?$"
	);
	
	/**
	 * Day-Hours-Minutes-Seconds text to fractal converter
	 * @param DHMS_text text in form "DD HHh MMm SSs"
	 * @return Fraction
	 */
	public static MixedFraction DHMS_textToFraction(String DHMS_text, HMSRecord one_day) throws WrongNumberFormat {
		DHMS_text = DHMS_text.strip().toLowerCase();
		var match = DHMS_textPattern.matcher( DHMS_text );
		boolean isNegative = false;
		
		if( match.find() ) {
			String temp;
			var daysNumber = Integer.parseInt( match.group(1) );
			
			if( daysNumber < 0 ) {
				isNegative = true;
				daysNumber *= -1;
			}
			
			var hoursNumber = 0;
			if( match.group(2) != null ) {
				temp = match.group(2).strip();
				temp = temp.substring( 0, temp.length() - 1 );
				hoursNumber = Integer.parseInt( temp );
			}
			var minutesNumber = 0;
			if( match.group(3) != null ) {
				temp = match.group(3).strip();
				temp = temp.substring( 0, temp.length() - 1 );
				minutesNumber = Integer.parseInt( temp );
			}
			var secondsNumber = MixedFraction.ZERO;
			if( match.group(4) != null ) {
				temp = match.group(4).strip();
				temp = temp.substring( 0, temp.length() - 1 );
				
				secondsNumber = DecimalTextToFraction( temp );
			}
			
			var result = new MixedFraction( daysNumber );
			result = result.add( new MixedFraction( hoursNumber, one_day.hours() ) );
			result = result.add( new MixedFraction( minutesNumber, one_day.minutes_in_day() ) );
			result = result.add(secondsNumber.divide( one_day.seconds_in_day() ));
			
			if( isNegative ) {
				return result.negate();
			} else {
				return result;
			}
		} else {
			throw new WrongNumberFormat();
		}
	}
	
	/**
	 * Converts fraction to Day-Hour-Minute-Second text
	 * @param fraction A fraction to convert
	 * @return text in format "DD HHh MMm SSs"
	 */
	public static String FractionToDHMS_text(MixedFraction fraction, HMSRecord one_day) {
		StringBuilder result = new StringBuilder();
		
		if( fraction.isZero() ) {
			return "0";
		}
		if( fraction.isNegative() ) {
			result.append( '-' );
			fraction = fraction.abs();
		}
		
		//Days
		if( fraction.hasIntegerPart() ) {
			result.append( fraction.getInteger() ).append( ' ' );
			fraction = fraction.getFraction();
		}
		if( fraction.isZero() ) {
			return result.toString().strip();
		}
		
		//Hours
		fraction = fraction.multiply( one_day.hours() );
		result.append( fraction.getInteger() ).append( "h " );
		fraction = fraction.getFraction();
		if( fraction.isZero() ) {
			return result.toString().strip();
		}
		
		//Minutes
		fraction = fraction.multiply( one_day.minutes() );
		result.append( fraction.getInteger() ).append( "m " );
		fraction = fraction.getFraction();
		if( fraction.isZero() ) {
			return result.toString().strip();
		}
		
		//Seconds
		fraction = fraction.multiply( one_day.seconds() );
		result.append( FractionToDecimalText(fraction) ).append( "s" );
		
		return result.toString().strip();
	}
	
	private static final Pattern DecimalTextPattern = Pattern.compile("^-?" + getDecimalFormat() + "$");
	
	/**
	 * Convert a decimal to a fraction
	 * @param decimal Input in form "1.23(45)"
	 * @return Fraction
	 * @throws WrongNumberFormat if the decimal text is wrong
	 */
	public static MixedFraction DecimalTextToFraction(String decimal) throws WrongNumberFormat {
		decimal = decimal.strip();
		var match = DecimalTextPattern.matcher( decimal );
		
		if( ! match.find() ) {
			throw new WrongNumberFormat();
		}
		
		boolean neg = decimal.startsWith( "-" );
		
		var integerPart = BigInteger.valueOf( Long.parseLong( match.group(1) ) );
		var fractionPart = MixedFraction.ZERO;
		
		if( match.group(2) != null) {
			var temp = match.group(2);
			//remove decimal separator
			temp = temp.substring( 1 );
			
			if( temp.contains( "(" ) ) {
				//divide to normal and period parts
				var normalPartText = temp.substring( 0, temp.indexOf( "(" ) );
				var periodPartText = temp.substring( temp.indexOf( "(" )+1, temp.length()-1 );
				
				//normal part
				var normalNumerator = BigInteger.ZERO;
				if( ! normalPartText.isEmpty() ) {
					normalNumerator = BigInteger.valueOf( Long.parseLong( normalPartText ) );
				}
				var normalDenominator = BigInteger.TEN.pow( normalPartText.length() );
				fractionPart = new MixedFraction( normalNumerator, normalDenominator );
				
				//period part
				var periodNumerator = BigInteger.valueOf( Long.parseLong( periodPartText ) );
				var periodDenominator =
						BigInteger.valueOf( Long.parseLong( "9".repeat( periodPartText.length() ) ) );
				periodDenominator = periodDenominator.multiply( normalDenominator );
				
				fractionPart = fractionPart.add( new MixedFraction( periodNumerator, periodDenominator ) );
			} else {
				var numerator = BigInteger.valueOf( Long.parseLong( temp ) );
				var denominator = BigInteger.TEN.pow( temp.length() );
				fractionPart = new MixedFraction( numerator, denominator );
			}
		}
		
		return new MixedFraction( integerPart ).add(fractionPart).multiply( neg?-1:1 );
	}
	
	/**
	 * Converts a fraction into a decimal representation.
	 * @param fraction Fraction to convert
	 * @return Decimal in form "1.23(45)"
	 */
	public static String FractionToDecimalText(MixedFraction fraction) {
		StringBuilder resultText = new StringBuilder();
		if( fraction.isNegative() ) {
			resultText.append( '-' );
			fraction = fraction.abs();
		}
		
		if(fraction.hasIntegerPart()) {
			resultText.append( fraction.getInteger().toString() );
			fraction = fraction.getFraction();
		} else {
			resultText.append( '0' );
		}
		if( fraction.isZero() ) {
			return resultText.toString();
		}
		resultText.append( getDecimalSeparator() );
		
		var num = fraction.getNumerator();
		var den = fraction.getDenominator();
		//quotient
		var res = num.divide( den );
		
		//if remainder is 0, return result
		var remainder = num.remainder( den ).multiply( BigInteger.TEN );
		if( remainder.equals( BigInteger.ZERO )) {
			return resultText.toString();
		}
		
		//right-hand side of decimal point
		var map = new HashMap<BigInteger, Integer>();
		while( ! remainder.equals( BigInteger.ZERO ) ) {
			//if digits repeat
			if( map.containsKey( remainder ) ) {
				int beg = map.get(remainder);
				String part1 = resultText.substring( 0, beg );
				String part2 = resultText.substring( beg );
				return part1 + "(" + part2 + ")";
			}
			
			//continue
			map.put( remainder, resultText.length() );
			res = remainder.divide( den );
			resultText.append( res );
			remainder = remainder.remainder( den ).multiply( BigInteger.TEN );
		}
		
		return resultText.toString();
	}
	
	/**
	 * Converts fraction to its textual representation
	 * @param fraction Fraction
	 * @return Fraction as String
	 */
	public static String FractionToText(MixedFraction fraction) {
		return fraction.toString();
	}
	
	private static final Pattern fractionPattern = Pattern.compile(
			"^-?(\\d+|\\d+/\\d+|\\d+ +\\d+/\\d+)$"
	);
	
	/**
	 * Converts text to fraction
	 * @param text String in format "I N/D"
	 * @return fraction
	 */
	public static MixedFraction TextToFraction(String text) throws WrongNumberFormat {
		text = text.strip();
		var match = fractionPattern.matcher( text );
		
		if( ! match.find() ) {
			throw new WrongNumberFormat();
		}
		
		return MixedFraction.fromString( text );
	}
}
