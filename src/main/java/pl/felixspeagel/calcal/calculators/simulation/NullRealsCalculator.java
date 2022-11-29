package pl.felixspeagel.calcal.calculators.simulation;

import pl.felixspeagel.calcal.math.MixedFraction;

import java.math.BigInteger;

public class NullRealsCalculator implements RealsCalculator {
	@Override
	public void setDaysSoFar(BigInteger days) {
		//null does nothing
	}
	
	@Override
	public void moveToNextDay() {
		//null does nothing
	}
	
	@Override
	public MoonPhase getBorderMoon() {
		return null; //we don't calculate it
	}
	
	@Override
	public Season getBorderSeason() {
		return null; //we don't calculate it
	}
	
	public NullRealsCalculator() {
	}
	
	@Override
	public MixedFraction getMonthLength() {
		return null;
	}
	
	@Override
	public MixedFraction getMonthShift() {
		return null;
	}
	
	@Override
	public MixedFraction getYearLength() {
		return null;
	}
	
	@Override
	public MixedFraction getYearShift() {
		return null;
	}
}
