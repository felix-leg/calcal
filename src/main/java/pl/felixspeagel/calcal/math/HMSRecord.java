package pl.felixspeagel.calcal.math;

/**
 * Hour-Minute-Second record.
 * @param hours
 * @param minutes
 * @param seconds
 */
public record HMSRecord(
		int hours,
		int minutes,
		int seconds
) {
	public int minutes_in_day() {
		return hours * minutes;
	}
	public int seconds_in_day() {
		return hours * minutes * seconds;
	}
}
