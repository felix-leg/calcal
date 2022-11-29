package pl.felixspeagel.calcal.file;

import pl.felixspeagel.calcal.calculators.IntercalationType;
import pl.felixspeagel.calcal.calculators.Rule;
import pl.felixspeagel.calcal.calendar.Month;
import pl.felixspeagel.calcal.calendar.Week;
import pl.felixspeagel.calcal.controllers.CalendarTypeInput;
import pl.felixspeagel.calcal.calendar.SpecialFeature;
import pl.felixspeagel.calcal.math.MixedFraction;

public interface SummaryWriter {
	void writeDayLength(int hours, int minutes, int seconds);
	
	void writeYearMonthLength(MixedFraction year, MixedFraction month);
	
	void writeDayCount(int normal_days_count, int leap_days_count);
	
	void writeUsedSolution(CalendarTypeInput.Solution solution);
	
	void writeCycle(IntercalationType[] cycle);
	
	void writeLeapRules(Rule[] rules, boolean[] active);
	
	void writeAboutMonth(Month month, int monthNumber, SpecialFeature feature);
	
	void writeAboutWeek(Week week);
}
