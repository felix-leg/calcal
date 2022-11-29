package pl.felixspeagel.calcal.calculators.simulation;

import java.math.BigInteger;

public interface LeapCalculator {
	void setEraYear(BigInteger year);
	
	BigInteger leapYearsUntil(BigInteger year);
	boolean isLeapYear();
}
