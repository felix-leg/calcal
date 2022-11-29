package pl.felixspeagel.calcal.calculators.simulation;

import pl.felixspeagel.calcal.math.MixedFraction;

import java.math.BigInteger;

public class TrueRealsCalculator implements RealsCalculator {
	
	private final MixedFraction month_length;
	private final MixedFraction year_length;
	private final MixedFraction month_shift;
	private final MixedFraction year_shift;
	
	//constans
	private final MixedFraction quarter;
	private final MixedFraction half;
	private final MixedFraction three_fourths;
	private final MixedFraction one_eight;
	private final MixedFraction three_eights;
	private final MixedFraction five_eights;
	private final MixedFraction seven_eights;
	
	public TrueRealsCalculator(
			MixedFraction real_month_length, MixedFraction real_year_length,
			MixedFraction real_month_shift, MixedFraction real_year_shift
	) {
		month_length = real_month_length;
		year_length = real_year_length;
		month_shift = real_month_shift;
		year_shift = real_year_shift;
		
		half = MixedFraction.ONE_HALF;
		quarter = half.multiply( half );
		three_fourths = quarter.multiply( 3 );
		one_eight = quarter.multiply( half );
		three_eights = one_eight.multiply( 3 );
		five_eights = one_eight.multiply( 5 );
		seven_eights = one_eight.multiply( 7 );
		
		daysSoFarForMonth = daysSoFarForYear = MixedFraction.ZERO;
		previousMoon = null;
		previousSeason = null;
	}
	
	private MixedFraction daysSoFarForYear;
	private MixedFraction daysSoFarForMonth;
	private MoonPhase previousMoon;
	private Season previousSeason;
	
	@Override
	public void setDaysSoFar(BigInteger days) {
		daysSoFarForMonth = new MixedFraction(days).subtract( month_shift );
		daysSoFarForYear = new MixedFraction(days).subtract( year_shift );
		
		previousMoon = getMoonPhase( daysSoFarForMonth.subtract( MixedFraction.ONE ) );
		previousSeason = getSeason( daysSoFarForYear.subtract( MixedFraction.ONE ) );
		
		currentMoon = getMoonPhase( daysSoFarForMonth );
		currentSeason = getSeason( daysSoFarForYear );
	}
	
	private MoonPhase getMoonPhase(MixedFraction forDay) {
		var f = forDay.abs().divide( month_length ).getFraction();
		if( forDay.isNegative() ) {
			f = MixedFraction.ONE.subtract( f );
		}
		
		if( f.compareTo( MixedFraction.ZERO ) >= 0 && f.compareTo( one_eight ) < 0 ) {
			return MoonPhase.NEW_MOON;
		} else if( f.compareTo( one_eight ) >= 0 && f.compareTo( quarter ) < 0 ) {
			return MoonPhase.WAXING_CRESCENT;
		} else if( f.compareTo( quarter ) >= 0 && f.compareTo( three_eights ) < 0 ) {
			return MoonPhase.FIRST_QUARTER;
		} else if( f.compareTo( three_eights ) >= 0 && f.compareTo( half ) < 0 ) {
			return MoonPhase.WAXING_GIBBOUS;
		} else if( f.compareTo( half ) >= 0 && f.compareTo( five_eights ) < 0 ) {
			return MoonPhase.FULL_MOON;
		} else if( f.compareTo( five_eights ) >= 0 && f.compareTo( three_fourths ) < 0 ) {
			return MoonPhase.WANING_GIBBOUS;
		} else if( f.compareTo( three_fourths ) >= 0 && f.compareTo( seven_eights ) < 0 ) {
			return MoonPhase.LAST_QUARTER;
		} else if( f.compareTo( seven_eights ) >= 0 && f.compareTo( MixedFraction.ONE ) < 0 ) {
			return MoonPhase.WANING_CRESCENT;
		}
		return null; //shouldn't reach
	}
	
	private Season getSeason(MixedFraction forDay) {
		var f = forDay.abs().divide( year_length ).getFraction();
		if( forDay.isNegative() ) {
			f = MixedFraction.ONE.subtract( f );
		}
		
		if( f.compareTo( MixedFraction.ZERO ) >= 0 && f.compareTo( quarter ) < 0 ) {
			return Season.WINTER;
		} else if( f.compareTo( quarter ) >= 0 && f.compareTo( half ) < 0 ) {
			return Season.SPRING;
		} else if( f.compareTo( half ) >= 0 && f.compareTo( three_fourths ) < 0 ) {
			return Season.SUMMER;
		} else if( f.compareTo( three_fourths ) >= 0 && f.compareTo( MixedFraction.ONE ) < 0 ) {
			return Season.FALL;
		}
		return null; //shouldn't reach
	}
	
	private MoonPhase currentMoon;
	private Season currentSeason;
	
	@Override
	public void moveToNextDay() {
		daysSoFarForMonth = daysSoFarForMonth.add( MixedFraction.ONE );
		daysSoFarForYear = daysSoFarForYear.add( MixedFraction.ONE );
		
		previousMoon = currentMoon;
		previousSeason = currentSeason;
		
		currentMoon = getMoonPhase( daysSoFarForMonth );
		currentSeason = getSeason( daysSoFarForYear );
	}
	
	@Override
	public MoonPhase getBorderMoon() {
		if( previousMoon != currentMoon ) {
			return currentMoon;
		} else {
			return null;
		}
	}
	
	@Override
	public Season getBorderSeason() {
		if( previousSeason != currentSeason ) {
			return currentSeason;
		} else {
			return null;
		}
	}
	
	@Override
	public MixedFraction getMonthLength() {
		return month_length;
	}
	
	@Override
	public MixedFraction getMonthShift() {
		return month_shift;
	}
	
	@Override
	public MixedFraction getYearLength() {
		return year_length;
	}
	
	@Override
	public MixedFraction getYearShift() {
		return year_shift;
	}
}
