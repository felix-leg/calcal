package pl.felixspeagel.calcal.controllers;

import pl.felixspeagel.calcal.calculators.Rule;
import pl.felixspeagel.calcal.calendar.SpecialFeature;
import pl.felixspeagel.calcal.controllers.models.CalendarTypeData;
import pl.felixspeagel.calcal.file.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays to user the final shape of his/her calendar
 * and gives the possibility to save the result.
 */
public class Summary {
	
	private CalendarTypeData the_data;
	public TextGetter text_getter;
	
	public final ArrayList<Refreshable> refreshList;
	private void refreshGUI() {
		for(var aRefreshable : refreshList) {
			aRefreshable.refresh( true );
		}
	}
	
	public Summary() {
		the_data = null;
		text_getter = null;
		refreshList = new ArrayList<>();
	}
	
	public void setPreviousData(CalendarTypeData previous) {
		the_data = previous;
		refreshGUI();
	}
	
	public String getSummary() {
		if( the_data == null || text_getter == null )
			return "";
		
		var text = new TextFileWriter( text_getter, null );
		fillWriter( text );
		return text.getResult();
	}
	
	public void saveXML(File file) {
		if( the_data == null || text_getter == null || file == null )
			return;
		
		var xml = new ProjectWriter();
		fillWriter( xml );
		xml.saveProject( file );
	}
	
	public void saveTXT(File selectedFile, String title) {
		if( the_data == null || text_getter == null )
			return;
		
		var text = new TextFileWriter( text_getter, title );
		fillWriter( text );
		
		try( PrintWriter writer = new PrintWriter( selectedFile ) ) {
			writer.print( text.getResult() );
		} catch( FileNotFoundException e ) {
			//ignore
		}
	}
	
	public void saveMD(File selectedFile, String title) {
		if( the_data == null || text_getter == null || title == null )
			return;
		
		var text = new MarkdownWriter( text_getter, title );
		fillWriter( text );
		
		try( PrintWriter writer = new PrintWriter( selectedFile ) ) {
			writer.print( text.getResult() );
		} catch( FileNotFoundException e ) {
			//ignore
		}
	}
	
	public void saveHTML(File selectedFile, String title) {
		if( the_data == null || text_getter == null )
			return;
		
		var text = new HtmlWriter( text_getter, title );
		fillWriter( text );
		
		try( PrintWriter writer = new PrintWriter( selectedFile ) ) {
			writer.print( text.getResult() );
		} catch( FileNotFoundException e ) {
			//ignore
		}
	}
	
	private void fillWriter(SummaryWriter the_writer) {
		//day length
		var day_length = the_data.previous_input().day_length();
		the_writer.writeDayLength(day_length.hours(), day_length.minutes(), day_length.seconds());
		
		the_writer.writeYearMonthLength( the_data.previous_input().year(), the_data.previous_input().month() );
		
		//days count
		var calendar = the_data.example_calendar();
		var normal_days_count = 0;
		var leap_days_count = 0;
		var has_leap_month = calendar.getYear().feature == SpecialFeature.LEAP;
		
		for(var monthID = 0; monthID<calendar.getYear().getMonthCount(); monthID++) {
			var month = calendar.getYear().getMonth( monthID );
			if( has_leap_month && calendar.getYear().isMonthFeature( monthID ) ) {
				leap_days_count += month.normal_days + month.leap_days;
			} else {
				normal_days_count += month.normal_days;
				leap_days_count += month.leap_days;
			}
		}
		the_writer.writeDayCount(normal_days_count, leap_days_count);
		
		//used solution
		the_writer.writeUsedSolution(the_data.solution());
		
		//leap cycles
		if( the_data.example_calendar().hasCycle() ) {
			the_writer.writeCycle(calendar.getCycle());
		} else if( the_data.example_calendar().hasLeapRules() ) {
			var rules = calendar.getRules();
			var active = new boolean[rules.length];
			for(int i=0; i<rules.length; i++) {
				active[i] = calendar.isRuleTurnedOn( i );
			}
			the_writer.writeLeapRules( rules, active );
		}
		
		//month descriptions
		var feature = calendar.getYear().feature;
		for(var monthID=0; monthID < calendar.getYear().getMonthCount(); monthID++) {
			the_writer.writeAboutMonth(
					calendar.getYear().getMonth( monthID ),
					monthID+1,
					calendar.getYear().isMonthFeature( monthID ) ? feature : SpecialFeature.NONE
			);
		}
		
		//weeks description
		the_writer.writeAboutWeek(calendar.getWeek());
	}
}
