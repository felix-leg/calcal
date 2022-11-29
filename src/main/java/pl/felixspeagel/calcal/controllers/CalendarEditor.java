package pl.felixspeagel.calcal.controllers;

import pl.felixspeagel.calcal.calculators.IntercalationType;
import pl.felixspeagel.calcal.calculators.Rule;
import pl.felixspeagel.calcal.calendar.Calendar;
import pl.felixspeagel.calcal.calendar.Month;
import pl.felixspeagel.calcal.calendar.SpecialFeature;
import pl.felixspeagel.calcal.calendar.Week;
import pl.felixspeagel.calcal.controllers.models.CalendarTypeData;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * Gives to user a possibility to fine tune his/her calendar.
 */
public class CalendarEditor {

	private BigInteger leap_days_pool;
	private BigInteger normal_days_pool;
	private Calendar edited_calendar;
	private Calendar awaited_calendar;
	private CalendarTypeData previous_data;
	
	private boolean do_not_load_previous_data_calendar;
	
	public CalendarEditor(Calendar calendar) {
		refreshList = new ArrayList<>();
		
		leap_days_pool = BigInteger.ZERO;
		normal_days_pool = BigInteger.ZERO;
		edited_calendar = null;
		awaited_calendar = null;
		previous_data = null;
		surplusYearsInCycle = 0;
		selectedMonthIndex = -1;
		extraMonths = 0;
		do_not_load_previous_data_calendar = false;
		
		if( calendar != null ) {
			edited_calendar = calendar;
			do_not_load_previous_data_calendar = true;
			selectFirstMonth();
		}
	}
	
	public final ArrayList<Refreshable> refreshList;
	private void refreshGUI(boolean full) {
		for(var aRefreshable : refreshList) {
			aRefreshable.refresh(full);
		}
	}
	
	@SuppressWarnings("RedundantIfStatement")
	public boolean inErrorState() {
		if( edited_calendar == null ) return true;
		if( ! normal_days_pool.equals( BigInteger.ZERO ) ) return true;
		if( ! leap_days_pool.equals( BigInteger.ZERO ) ) return true;
		
		if( surplusYearsInCycle != 0 ) return true;
		
		if( mustAssignMonthFeature() ) return true;
		
		return false;
	}
	
	public CalendarTypeData getFinalInput() {
		if( inErrorState() ) return null;
		
		return new CalendarTypeData(
				previous_data.solution(),
				edited_calendar,
				previous_data.previous_input()
		);
	}
	
	public void setPreviousData(CalendarTypeData previous) {
		previous_data = previous;
		if( do_not_load_previous_data_calendar ) {
			do_not_load_previous_data_calendar = false;
			refreshGUI(true);
			return;
		}
		if( edited_calendar == null ) {
			edited_calendar = previous.example_calendar();
			surplusYearsInCycle = 0;
			extraMonths = 0;
			if( monthsInYear() == 0 ) selectedMonthIndex = -1;
			else selectedMonthIndex = 0;
		} else {
			if( edited_calendar.equals( previous.example_calendar(), false ) ) {
				awaited_calendar = null;
				refreshGUI( false );
				return;
			}
			awaited_calendar = previous.example_calendar();
		}
		refreshGUI(true);
	}
	
	/**
	 * Does the editor contain a calendar that
	 * is waiting to be edited?
	 * @return has got an awaiting calendar
	 */
	public boolean isNewCalendarAwaiting() {
		return awaited_calendar != null;
	}
	public void switchToAwaitingCalendar() {
		if( awaited_calendar == null ) return;
		
		var month_names = edited_calendar.getYear().copyMonthNames();
		edited_calendar = awaited_calendar;
		awaited_calendar = null;
		edited_calendar.getYear().overwriteMonthNames( month_names );
		surplusYearsInCycle = 0;
		extraMonths = 0;
		if( monthsInYear() == 0 ) selectedMonthIndex = -1;
		else selectedMonthIndex = 0;
		refreshGUI(true);
	}
	
	public boolean hasLeapRules() {
		if( edited_calendar == null ) return false;
		return edited_calendar.hasLeapRules();
	}
	public boolean hasYearCycle() {
		if( edited_calendar == null ) return false;
		return edited_calendar.hasCycle();
	}
	public IntercalationType[] getCycle() {
		if( edited_calendar == null ) return null;
		return edited_calendar.getCycle();
	}
	public Rule[] getRules() {
		if( edited_calendar == null ) return null;
		return edited_calendar.getRules();
	}
	
	public void turnRuleOn(int index, boolean value) {
		if( edited_calendar == null ) return;
		edited_calendar.switchRule( index, value );
	}
	public boolean isRuleTurnedOn(int index) {
		if( edited_calendar == null ) return false;
		return edited_calendar.isRuleTurnedOn( index );
	}
	
	private int surplusYearsInCycle;
	
	public void turnYearInCycleOn(int index, boolean value) {
		if( edited_calendar == null ) return;
		
		if( edited_calendar.isCycleOf( index, IntercalationType.LEAP ) && !value ) {
			surplusYearsInCycle += 1;
			edited_calendar.switchCycleOn( index, IntercalationType.NORMAL );
		} else if( edited_calendar.isCycleOf( index, IntercalationType.NORMAL ) && value ) {
			surplusYearsInCycle -= 1;
			edited_calendar.switchCycleOn( index, IntercalationType.LEAP );
		}
		refreshGUI( false );
	}
	public boolean isYearInCycleTurnedOn(int index) {
		if( edited_calendar == null ) return false;
		
		return edited_calendar.isCycleOf( index, IntercalationType.LEAP );
	}
	/**
	 * How many years the user has to yet assign in a cycle
	 * @return extra years
	 */
	public int surplusYearsToAssignInCycle() {
		return surplusYearsInCycle;
	}
	
	public int monthsInYear() {
		if( edited_calendar == null ) return 0;
		
		return edited_calendar.getYear().getMonthCount();
	}
	
	private int selectedMonthIndex;
	public boolean canSelectNextMonth() {
		if( edited_calendar == null ) return false;
		if( monthsInYear() == 0 ) return false;
		return selectedMonthIndex != edited_calendar.getYear().getLastMonthIndex();
	}
	public boolean canSelectPreviousMonth() {
		if( edited_calendar == null ) return false;
		if( monthsInYear() == 0 ) return false;
		return selectedMonthIndex != 0;
	}
	public void selectFirstMonth() {
		if( edited_calendar == null ) return;
		if( monthsInYear() == 0 ) return;
		selectedMonthIndex = 0;
		refreshGUI(false);
	}
	public void selectLastMonth() {
		if( edited_calendar == null ) return;
		if( monthsInYear() == 0 ) return;
		selectedMonthIndex = edited_calendar.getYear().getLastMonthIndex();
		refreshGUI(false);
	}
	public void selectNextMonth() {
		if( edited_calendar == null ) return;
		if( selectedMonthIndex == -1 ) return;
		
		if( selectedMonthIndex == edited_calendar.getYear().getLastMonthIndex() )
			return;
		selectedMonthIndex++;
		refreshGUI(false);
	}
	public void selectPreviousMonth() {
		if( edited_calendar == null ) return;
		if( selectedMonthIndex == -1 ) return;
		
		if( selectedMonthIndex == 0 )
			return;
		selectedMonthIndex--;
		refreshGUI(false);
	}
	public int getSelectedMonthIndex() {
		return selectedMonthIndex;
	}
	public Month getSelectedMonth() {
		if( edited_calendar == null ) return null;
		if( selectedMonthIndex == -1 ) return null;
		
		return edited_calendar.getYear().getMonth( selectedMonthIndex );
	}
	
	public boolean canAddNormalDay() {
		return ! normal_days_pool.equals( BigInteger.ZERO );
	}
	public void addNormalDay() {
		var month = getSelectedMonth();
		if( month == null ) return;
		if( ! canAddNormalDay() ) return;
		
		month.normal_days += 1;
		normal_days_pool = normal_days_pool.subtract( BigInteger.ONE );
		refreshGUI( false );
	}
	public boolean canRemoveNormalDay() {
		var month = getSelectedMonth();
		if( month == null ) return false;
		
		return month.normal_days > 0;
	}
	public void removeNormalDay() {
		var month = getSelectedMonth();
		if( month == null ) return;
		if( month.normal_days == 0 ) return;
		
		month.normal_days -= 1;
		normal_days_pool = normal_days_pool.add( BigInteger.ONE );
		refreshGUI( false );
	}
	public BigInteger getNormalDaysPool() {
		return normal_days_pool;
	}
	
	public boolean canAddLeapDay() {
		return ! leap_days_pool.equals( BigInteger.ZERO );
	}
	public void addLeapDay() {
		var month = getSelectedMonth();
		if( month == null ) return;
		if( ! canAddLeapDay() ) return;
		
		month.leap_days += 1;
		leap_days_pool = leap_days_pool.subtract( BigInteger.ONE );
		refreshGUI( false );
	}
	public boolean canRemoveLeapDay() {
		var month = getSelectedMonth();
		if( month == null ) return false;
		
		return month.leap_days > 0;
	}
	public void removeLeapDay() {
		var month = getSelectedMonth();
		if( month == null ) return;
		if( month.leap_days == 0 ) return;
		
		month.leap_days -= 1;
		leap_days_pool = leap_days_pool.add( BigInteger.ONE );
		refreshGUI( false );
	}
	public BigInteger getLeapDaysPool() {
		return leap_days_pool;
	}
	
	private int extraMonths;
	
	public boolean canAddMonth() {
		if( edited_calendar == null ) return false;
		return ! normal_days_pool.equals( BigInteger.ZERO );
	}
	public boolean canRemoveMonth() {
		if( edited_calendar == null ) return false;
		if( isSelectedMonthFeatured() ) return false;
		return extraMonths > 0;
	}
	public void addMonth() {
		if( ! canAddMonth() ) return;
		
		edited_calendar.getYear().addMonth( "???", normal_days_pool.intValue(), 0 );
		normal_days_pool = BigInteger.ZERO;
		extraMonths += 1;
		selectLastMonth();
	}
	public void removeMonth() {
		if( ! canRemoveMonth() ) return;
		
		var selected = getSelectedMonth();
		if( selected == null ) return;
		normal_days_pool = normal_days_pool.add( BigInteger.valueOf( selected.normal_days ) );
		leap_days_pool = leap_days_pool.add( BigInteger.valueOf( selected.leap_days ) );
		
		edited_calendar.getYear().removeMonth( selectedMonthIndex );
		extraMonths -= 1;
		selectLastMonth();
	}
	
	public boolean isSelectedMonthFeatured() {
		if( edited_calendar == null ) return false;
		if( selectedMonthIndex == -1 ) return false;
		
		return edited_calendar.getYear().isMonthFeature( selectedMonthIndex );
	}
	
	public SpecialFeature getFeatureOfYear() {
		if( edited_calendar == null ) return null;
		
		return edited_calendar.getYear().feature;
	}
	
	private boolean needAssignMonthFeature = false;
	
	public void removeMonthFeature() {
		if( edited_calendar == null ) return;
		
		edited_calendar.getYear().removeMonthFeature();
		needAssignMonthFeature = true;
		refreshGUI( false );
	}
	public void assignMonthFeature() {
		if( ! needAssignMonthFeature ) return;
		if( edited_calendar == null ) return;
		if( selectedMonthIndex < 0 ) return;
		
		edited_calendar.getYear().setFeatureMonth( selectedMonthIndex );
		needAssignMonthFeature = false;
		refreshGUI( false );
	}
	
	public boolean mustAssignMonthFeature() {
		return needAssignMonthFeature;
	}
	
	public Week getWeek() {
		if( edited_calendar == null ) return null;
		
		return edited_calendar.getWeek();
	}

}
