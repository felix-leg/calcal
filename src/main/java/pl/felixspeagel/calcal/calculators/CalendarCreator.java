package pl.felixspeagel.calcal.calculators;

import pl.felixspeagel.calcal.calendar.Calendar;
import pl.felixspeagel.calcal.math.MixedFraction;

public interface CalendarCreator {
	Calendar makeExampleCalendar(MixedFraction year_length, MixedFraction month_length, int month_count);
}
