package pl.felixspeagel.calcal.controllers;

import pl.felixspeagel.calcal.calculators.simulation.Simulation;
import pl.felixspeagel.calcal.file.ProjectReader;
import pl.felixspeagel.calcal.math.HMSRecord;
import pl.felixspeagel.calcal.math.MixedFraction;

import java.math.BigInteger;

public class SimulationSettings {
	
	private final ProjectReader the_project;
	private boolean updateNeeded;
	
	public SimulationSettings(ProjectReader project) {
		the_project = project;
		updateNeeded = true;
	}
	
	public boolean isUpdateNeeded() {
		return updateNeeded;
	}
	
	public HMSRecord getDayLength() {
		return the_project.day_length;
	}
	public MixedFraction getMonthLength() {
		return the_project.month_length;
	}
	public MixedFraction getYearLength() {
		return the_project.year_length;
	}
	
	/**
	 * How many weeks user can shift forward and backward?
	 * @return week length or zero if week beginning can't be shifted.
	 */
	public int getShiftWeekLength() {
		var week = the_project.calendar.getWeek();
		if( week.starts_with_month ) {
			return 0;
		} else {
			return week.length;
		}
	}
	
	private int era_week_day = 0;
	public void setEraWeekDay(int day) {
		era_week_day = day;
		updateNeeded = true;
	}
	private BigInteger era_year = BigInteger.ZERO;
	public void setEraYear(BigInteger era) {
		era_year = era;
		updateNeeded = true;
	}
	
	private boolean reals_provided = false;
	private MixedFraction real_month_length = null;
	private MixedFraction real_year_length = null;
	private MixedFraction month_shift = null;
	private MixedFraction year_shift = null;
	
	public void setRealValuesTrackingFlag(boolean flag) {
		updateNeeded = true;
		reals_provided = flag;
	}
	public void setRealMonthLength(MixedFraction month) {
		updateNeeded = true;
		real_month_length = month;
	}
	public void setRealYearLength(MixedFraction year) {
		updateNeeded = true;
		real_year_length = year;
	}
	public void setMonthShift(MixedFraction shift) {
		updateNeeded = true;
		month_shift = shift;
	}
	public void setYearShift(MixedFraction shift) {
		updateNeeded = true;
		year_shift = shift;
	}
	
	public Simulation getNewSimulation() {
		updateNeeded = false;
		if( reals_provided ) {
			return new Simulation( the_project.calendar, era_week_day, era_year,
					real_month_length, real_year_length,
					month_shift, year_shift
			);
		} else {
			return new Simulation( the_project.calendar, era_week_day, era_year,
					null, null,
					null, null);
		}
	}
	
}
