package pl.felixspeagel.calcal.controllers;

import pl.felixspeagel.calcal.controllers.models.YearMonthLengthData;
import pl.felixspeagel.calcal.math.*;

import java.util.ArrayList;

/**
 * The controller for gathering length of the user's year and month.
 */
public class YearMonthLengthInput {
	
	public YearMonthLengthInput() {
		//set with default for Earth
		this(24, 60, 60,
				new MixedFraction( 365, 2425, 10000 ),
				new MixedFraction( 29, 191, 360 )
		);
	}
	
	public YearMonthLengthInput(int hours, int minutes, int seconds, MixedFraction year, MixedFraction month) {
		day_input = new DayLengthInput( hours, minutes, seconds );
		year_input = new LengthInput( year, LengthInput.NumberFormat.DECIMAL, day_input );
		month_input = new LengthInput( month, LengthInput.NumberFormat.DECIMAL, day_input );
		
		refreshList = new ArrayList<>();
		day_input.refreshList.add( this::refreshGUI );
		year_input.refreshList.add( this::refreshGUI );
		month_input.refreshList.add( this::refreshGUI );
	}
	
	public final ArrayList<Refreshable> refreshList;
	private void refreshGUI(boolean full) {
		for(var aRefreshable : refreshList) {
			aRefreshable.refresh(full);
		}
	}
	
	private final DayLengthInput day_input;
	private final LengthInput year_input;
	private final LengthInput month_input;
	public DayLengthInput getDayInput() {
		return day_input;
	}
	public LengthInput getYearInput() {
		return year_input;
	}
	public LengthInput getMonthInput() {
		return month_input;
	}
	
	/**
	 * Produces the final value from user inputs
	 * @return object of month and year input
	 */
	public YearMonthLengthData getFinalInput() {
		if( this.inErrorState() ) {
			return null;
		}
		return new YearMonthLengthData(
				month_input.getStoredValue(),
				year_input.getStoredValue(),
				getCalculatedMonthCount(),
				day_input.getStoredValue()
			);
	}
	
	public Integer getCalculatedMonthCount() {
		if( this.inErrorState() ) {
			return null;
		} else {
			var year_int = year_input.getStoredValue().getInteger();
			var month_int = month_input.getStoredValue().getInteger();
			return year_int.divide( month_int ).intValue();
		}
	}
	
	public boolean inErrorState() {
		return day_input.inErrorState() || month_input.inErrorState() || year_input.inErrorState();
	}
	
	public static class DayLengthInput {
		
		public DayLengthInput(int hours, int minutes, int seconds) {
			stored_hours = hours;
			stored_minutes = minutes;
			stored_seconds = seconds;
			
			hours_text = String.valueOf( hours );
			minutes_text = String.valueOf( minutes );
			seconds_text = String.valueOf( seconds );
			
			hours_error = minutes_error = seconds_error = false;
			
			refreshList = new ArrayList<>();
		}
		
		public final ArrayList<Refreshable> refreshList;
		private void refreshGUI() {
			for(var aRefreshable : refreshList) {
				aRefreshable.refresh(false);
			}
		}
		
		private int stored_hours, stored_minutes, stored_seconds;
		private String hours_text, minutes_text, seconds_text;
		private boolean hours_error, minutes_error, seconds_error;
		
		public boolean inErrorState() {
			return hours_error || minutes_error || seconds_error;
		}
		
		public void enterHoursText(String text) {
			hours_text = text.strip();
			try {
				int value = Integer.parseInt( hours_text );
				if( value <= 0 ) {
					hours_error = true;
				} else {
					stored_hours = value;
					hours_error = false;
				}
			} catch(NumberFormatException e) {
				hours_error = true;
			}
			refreshGUI();
		}
		public String getHoursText() {
			return hours_text;
		}
		
		public void enterMinutesText(String text) {
			minutes_text = text.strip();
			try {
				int value = Integer.parseInt( minutes_text );
				if( value <= 0 ) {
					minutes_error = true;
				} else {
					stored_minutes = value;
					minutes_error = false;
				}
			} catch(NumberFormatException e) {
				minutes_error = true;
			}
			refreshGUI();
		}
		public String getMinutesText() {
			return minutes_text;
		}
		
		public void enterSecondsText(String text) {
			seconds_text = text.strip();
			try {
				int value = Integer.parseInt( seconds_text );
				if( value <= 0 ) {
					seconds_error = true;
				} else {
					stored_seconds = value;
					seconds_error = false;
				}
			} catch(NumberFormatException e) {
				seconds_error = true;
			}
			refreshGUI();
		}
		public String getSecondsText() {
			return seconds_text;
		}
		
		public HMSRecord getStoredValue() {
			return new HMSRecord( stored_hours, stored_minutes, stored_seconds );
		}
	}
	
	public static class LengthInput {
		
		public enum NumberFormat {
			FRACTAL,
			DECIMAL,
			DAYS_AND_TIME
		}
		
		public LengthInput(MixedFraction value, NumberFormat format, DayLengthInput day_length) {
			stored_value = value;
			number_format = format;
			ref_day = day_length;
			error_state = false;
			refreshList = new ArrayList<>();
			formatValue();
		}
		
		public final ArrayList<Refreshable> refreshList;
		private void refreshGUI() {
			for(var aRefreshable : refreshList) {
				aRefreshable.refresh(false);
			}
		}
		
		private final DayLengthInput ref_day;
		private NumberFormat number_format;
		public NumberFormat getNumberFormat() {
			return number_format;
		}
		private MixedFraction stored_value;
		public MixedFraction getStoredValue() {
			return stored_value;
		}
		private boolean error_state;
		public boolean inErrorState() {
			return error_state;
		}
		
		private String text_value;
		private void formatValue() {
			switch( number_format ) {
				case DECIMAL ->
						text_value = NumberConverter.FractionToDecimalText( stored_value );
				case FRACTAL ->
						text_value = NumberConverter.FractionToText( stored_value );
				case DAYS_AND_TIME ->
						text_value = NumberConverter.FractionToDHMS_text( stored_value, ref_day.getStoredValue() );
			}
		}
		public String getText() {
			return text_value;
		}
		public void setText(String text) {
			text_value = text.strip();
			try{
				var value = MixedFraction.ZERO;
				switch( number_format ) {
					case DECIMAL ->
							value = NumberConverter.DecimalTextToFraction( text );
					case FRACTAL ->
							value = NumberConverter.TextToFraction( text );
					case DAYS_AND_TIME ->
							value = NumberConverter.DHMS_textToFraction( text, ref_day.getStoredValue() );
				}
				if( value.isZero() || value.isNegative() ) {
					error_state = true;
				} else {
					stored_value = value;
					error_state = false;
				}
			}catch( WrongNumberFormat e ) {
				error_state = true;
			}
			refreshGUI();
		}
		
		public void switchToFormat(NumberFormat new_format) {
			number_format = new_format;
			formatValue();
			refreshGUI();
		}
	}
	
}
