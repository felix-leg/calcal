package pl.felixspeagel.calcal.calculators.simulation;

import pl.felixspeagel.calcal.calculators.Rule;
import pl.felixspeagel.calcal.calendar.Calendar;
import pl.felixspeagel.calcal.calendar.SpecialFeature;
import pl.felixspeagel.calcal.math.MixedFraction;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;

public class Simulation {
	
	private final Calendar the_calendar;
	private final int week_shift;
	private final BigInteger epoch;
	
	private final BigInteger normal_days_in_year;
	private final BigInteger leap_days_in_year;
	private final LeapCalculator leap_calculator;
	
	private int month_number;
	private BigInteger year_number;
	
	private final RealsCalculator reals;
	
	public Simulation copy() {
		return new Simulation(
				the_calendar, week_shift, epoch,
				reals.getMonthLength(), reals.getYearLength(),
				reals.getMonthShift(), reals.getYearShift()
		);
	}
	
	public Simulation(Calendar calendar, int week_day_shift, BigInteger era_begin,
		MixedFraction real_month_length, MixedFraction real_year_length,
	    MixedFraction month_shift, MixedFraction year_shift
	) {
		the_calendar = calendar;
		week_shift = week_day_shift;
		epoch = era_begin;
		if(
				real_month_length != null &&
				real_year_length != null &&
				month_shift != null &&
				year_shift != null
		) {
			reals = new TrueRealsCalculator(real_month_length, real_year_length,
					month_shift, year_shift
			);
		} else {
			reals = new NullRealsCalculator();
		}
		
		BigInteger normal_days = BigInteger.ZERO;
		BigInteger leap_days = BigInteger.ZERO;
		
		var uses_leap_month = the_calendar.getYear().feature == SpecialFeature.LEAP;
		for(int monthID=0; monthID<the_calendar.getYear().getMonthCount(); monthID++) {
			var month = the_calendar.getYear().getMonth( monthID );
			if( uses_leap_month && the_calendar.getYear().isMonthFeature( monthID ) ) {
				leap_days = leap_days.add( BigInteger.valueOf( month.normal_days + month.leap_days ) );
			} else {
				normal_days = normal_days.add( BigInteger.valueOf( month.normal_days ) );
			}
			leap_days = leap_days.add( BigInteger.valueOf( month.leap_days ) );
		}
		normal_days_in_year = normal_days;
		leap_days_in_year = leap_days.add(normal_days);
		
		if( the_calendar.hasCycle() ) {
			leap_calculator = new CycleLeapCalculator( the_calendar.getCycle() );
		} else if( the_calendar.hasLeapRules() ) {
			var rules_array = new ArrayList<Rule>();
			var rules = the_calendar.getRules();
			for(int i=0; i<rules.length; i++) {
				if( the_calendar.isRuleTurnedOn( i ) ) {
					rules_array.add( rules[i] );
				}
			}
			leap_calculator = new RulesLeapCalculator(rules_array.toArray(new Rule[0]));
		} else {
			leap_calculator = new CycleLeapCalculator(null); //shouldn't reach
		}
		
		year_number = BigInteger.ONE;
		leap_calculator.setEraYear( year_number );
		gotoFirstMonth();
	}
	
	public int getWeekLength() {
		return the_calendar.getWeek().length;
	}
	
	public void setEraYear(BigInteger era_year) {
		year_number = era_year.subtract( epoch );
		leap_calculator.setEraYear( era_year );
	}
	public BigInteger getEraYear() {
		return year_number.add( epoch );
	}
	public void gotoNextYear() {
		year_number = year_number.add( BigInteger.ONE );
		leap_calculator.setEraYear( year_number );
	}
	public void gotoPreviousYear() {
		year_number = year_number.subtract( BigInteger.ONE );
		leap_calculator.setEraYear( year_number );
	}
	
	private boolean isThisMonthLeap(int month) {
		if( the_calendar.getYear().feature != SpecialFeature.LEAP ) {
			return false; //there are no leap months
		} else {
			return the_calendar.getYear().isMonthFeature( month );
		}
	}
	
	public void setMonth(int month) {
		if( month < 0 )
			month = 0;
		if( month >= the_calendar.getYear().getLastMonthIndex() )
			month = the_calendar.getYear().getLastMonthIndex();
		month_number = month;
		
		if( isThisMonthLeap(month_number) && ! leap_calculator.isLeapYear() ) {
			gotoPreviousMonth();
		}
	}
	public void gotoFirstMonth() {
		month_number = 0;
		if( isThisMonthLeap(month_number) && ! leap_calculator.isLeapYear() ) {
			gotoNextMonth();
		}
	}
	public void gotoLastMonth() {
		month_number = the_calendar.getYear().getLastMonthIndex();
		if( isThisMonthLeap(month_number) && ! leap_calculator.isLeapYear() ) {
			gotoPreviousMonth();
		}
	}
	
	public void gotoNextMonth() {
		month_number++;
		if( month_number > the_calendar.getYear().getLastMonthIndex() ) {
			month_number -= the_calendar.getYear().getLastMonthIndex();
			month_number -= 2;
			gotoNextYear();
			gotoNextMonth();
		}
		if( isThisMonthLeap(month_number) && ! leap_calculator.isLeapYear() ) {
			gotoNextMonth();
		}
	}
	public void gotoPreviousMonth() {
		month_number--;
		if( month_number < 0 )  {
			month_number += the_calendar.getYear().getLastMonthIndex();
			month_number += 2;
			gotoPreviousYear();
			gotoPreviousMonth();
		}
		if( isThisMonthLeap(month_number) && ! leap_calculator.isLeapYear() ) {
			gotoPreviousMonth();
		}
	}
	public String getMonthName() {
		return the_calendar.getYear().getMonth( month_number ).name;
	}
	public SpecialFeature getMonthFeature() {
		if( the_calendar.getYear().isMonthFeature( month_number ) ) {
			return the_calendar.getYear().feature;
		} else {
			return SpecialFeature.NONE;
		}
	}
	
	public DayInMonth[][] getListOfDaysInTheMonth() {
		var week = the_calendar.getWeek();
		var month = the_calendar.getYear().getMonth( month_number );
		int day_in_week;
		
		var days_so_far = calculateDaysSoFar();
		reals.setDaysSoFar( days_so_far );
		
		if( week.starts_with_month ) {
			//easy - month always start with the week beginning
			day_in_week = 0;
		} else {
			//tough
			var days_so_far_shifted = days_so_far.add( BigInteger.valueOf( week_shift ) );
			//finally calculate the weekday
			day_in_week = days_so_far_shifted.remainder( BigInteger.valueOf( week.length ) ).intValue();
			if( day_in_week < 0 )
				day_in_week += week.length;
		}
		
		var days_in_month = month.normal_days;
		if( leap_calculator.isLeapYear() ) {
			days_in_month += month.leap_days;
		}
		
		var weeks_in_month = new LinkedList<DayInMonth[]>();
		var current_week = new DayInMonth[week.length];
		for(var day=1; day<days_in_month+1; day++) {
			
			current_week[day_in_week] = new DayInMonth( day, reals.getBorderMoon(), reals.getBorderSeason() );
			day_in_week++;
			if( day_in_week == week.length ) {
				weeks_in_month.add( current_week );
				current_week = new DayInMonth[week.length];
				day_in_week = 0;
			}
			
			reals.moveToNextDay();
		}
		
		if( day_in_week > 0 ) {
			weeks_in_month.add( current_week );
		}
		return weeks_in_month.toArray(new DayInMonth[0][]);
	}
	
	private BigInteger calculateDaysSoFar() {
		var year = year_number;
		var zeroOrBellow = year.compareTo( BigInteger.ZERO ) <= 0;
		
		var leap_years_count = leap_calculator.leapYearsUntil( year );
		BigInteger normal_years_count;
		if( zeroOrBellow ) {
			normal_years_count = year.abs().add( BigInteger.ONE ).subtract( leap_years_count );
		} else {
			normal_years_count = year.subtract( leap_years_count );
		}
		
		var daysFromYearBegin = normal_years_count.multiply( normal_days_in_year );
		daysFromYearBegin = daysFromYearBegin.add(leap_years_count.multiply( leap_days_in_year ));
		var absoluteDaysFromZero = daysFromYearBegin;
		
		if( zeroOrBellow && ! year.equals( BigInteger.ZERO ) ) {
			leap_years_count = leap_calculator.leapYearsUntil( year.add( BigInteger.ONE ) );
			normal_years_count = year.add( BigInteger.ONE ).abs().add( BigInteger.ONE ).subtract( leap_years_count );
			
			daysFromYearBegin = daysFromYearBegin.subtract( normal_years_count.multiply( normal_days_in_year ) );
			daysFromYearBegin = daysFromYearBegin.subtract( leap_years_count.multiply( leap_days_in_year ) );
		}
		
		boolean isCurrentYearLeap = leap_calculator.isLeapYear();
		for(int monthID=the_calendar.getYear().getLastMonthIndex(); monthID>month_number-1; monthID--) {
			var month = the_calendar.getYear().getMonth( monthID );
			
			if( isCurrentYearLeap && isThisMonthLeap( monthID ) ) {
				daysFromYearBegin = daysFromYearBegin.subtract( BigInteger.valueOf( month.normal_days + month.leap_days ) );
				continue;
			} else if( !isCurrentYearLeap && isThisMonthLeap( monthID )) {
				continue;
			}
			
			daysFromYearBegin = daysFromYearBegin.subtract( BigInteger.valueOf( month.normal_days ) );
			if( isCurrentYearLeap ) {
				daysFromYearBegin = daysFromYearBegin.subtract( BigInteger.valueOf( month.leap_days ) );
			}
		}
		
		if( zeroOrBellow ) {
			if( daysFromYearBegin.compareTo( BigInteger.ZERO ) < 0 ) { //ugly hack
				daysFromYearBegin = BigInteger.ZERO;
			}
			return absoluteDaysFromZero.subtract( daysFromYearBegin ).negate();
		} else {
			return daysFromYearBegin;
		}
	}
}
