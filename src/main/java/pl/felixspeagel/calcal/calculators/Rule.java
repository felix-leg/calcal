package pl.felixspeagel.calcal.calculators;

import java.math.BigInteger;

public record Rule(
		BigInteger each_year,
		IntercalationType is_leap
) {}
