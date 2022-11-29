package pl.felixspeagel.calcal.controllers.models;

import pl.felixspeagel.calcal.calendar.Calendar;
import pl.felixspeagel.calcal.controllers.CalendarTypeInput;

public record CalendarTypeData(
		CalendarTypeInput.Solution solution,
		Calendar example_calendar,
		YearMonthLengthData previous_input
) {
}
