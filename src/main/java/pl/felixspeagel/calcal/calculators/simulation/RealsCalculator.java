package pl.felixspeagel.calcal.calculators.simulation;

import pl.felixspeagel.calcal.math.MixedFraction;

import java.math.BigInteger;

public interface RealsCalculator {
	void setDaysSoFar(BigInteger days);
	void moveToNextDay();
	MoonPhase getBorderMoon();
	Season getBorderSeason();
	
	MixedFraction getMonthLength();
	MixedFraction getMonthShift();
	
	MixedFraction getYearLength();
	MixedFraction getYearShift();
}
