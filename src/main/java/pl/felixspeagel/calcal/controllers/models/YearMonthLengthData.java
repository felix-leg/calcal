package pl.felixspeagel.calcal.controllers.models;

import pl.felixspeagel.calcal.math.HMSRecord;
import pl.felixspeagel.calcal.math.MixedFraction;

public record YearMonthLengthData(
		MixedFraction month,
		MixedFraction year,
		int months_in_year,
		HMSRecord day_length
) {
}
