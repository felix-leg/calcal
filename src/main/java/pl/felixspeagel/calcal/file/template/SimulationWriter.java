package pl.felixspeagel.calcal.file.template;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import pl.felixspeagel.calcal.calculators.simulation.Simulation;

public class SimulationWriter {

	private final Map<String, Template> templates;
	private final AtomicReference<Simulation> ref;
	
	public final Settings settings;
	
	private SimulationWriter(Map<String, Template> the_templates, AtomicReference<Simulation> a_simulation) {
		templates = the_templates;
		ref = a_simulation;
		settings = new Settings();
	}
	
	public static SimulationWriter getInstance(AtomicReference<Simulation> a_simulation) {
		final String dir = "templates";
		Map<String, Template> the_templates = new HashMap<>();
		
		var root = Template.getTemplate( dir, "root.html" );
		if( root == null ) return null;
		the_templates.put( "root", root );
		
		var year = Template.getTemplate( dir, "year.html" );
		if( year == null ) return null;
		the_templates.put( "year", year );
		
		var month = Template.getTemplate( dir, "month.html" );
		if( month == null ) return null;
		the_templates.put( "month", month );
		
		var week = Template.getTemplate( dir, "week.html" );
		if( week == null ) return null;
		the_templates.put( "week", week );
		
		var day = Template.getTemplate( dir, "day.html" );
		if( day == null ) return null;
		the_templates.put( "day", day );
		
		return new SimulationWriter( the_templates, a_simulation );
	}
	
	public static class Settings {
		public String title = "";
		
		public BigInteger year_start = BigInteger.ZERO;
		public BigInteger year_end = BigInteger.ZERO;
		
		public Color normal_bg = Color.RED;
		public Color normal_fg = Color.BLACK;
		
		public Color leap_bg = Color.GREEN;
		public Color leap_fg = Color.BLACK;
		
		public Color epi_bg = Color.BLUE;
		public Color epi_fg = Color.BLACK;
	}
	
	public void writeFile(File file) {
		if( file == null ) return;
		if( ref.get() == null ) return;
		var simulation = ref.get().copy();
		
		var root = templates.get( "root" );
		var a_year = templates.get( "year" );
		var a_month = templates.get( "month" );
		var a_week = templates.get( "week" );
		var a_day = templates.get( "day" );
		
		root.reset();
		
		for(var year=settings.year_start;
			year.compareTo( settings.year_end ) <= 0;
			year = year.add( BigInteger.ONE )
		) {
			a_year.reset();
			a_year.setContent( "year.number", year.toString() );
			
			simulation.gotoFirstMonth();
			simulation.setEraYear( year );
			
			while( simulation.getEraYear().equals( year ) ) {
				a_month.reset();
				
				a_month.setContent( "month.name", simulation.getMonthName() );
				a_month.setContent( "week.length", String.valueOf(simulation.getWeekLength()) );
				switch( simulation.getMonthFeature() ) {
					case NONE -> a_month.setContent( "month.type", " normal-month" );
					case LEAP -> a_month.setContent( "month.type", " leap-month" );
					case EPAGOMENAL -> a_month.setContent( "month.type", " epagomenal-month" );
				}
				
				for(var weeks_list : simulation.getListOfDaysInTheMonth()) {
					a_week.reset();
					for(var one_day : weeks_list) {
						a_day.reset();
						if( one_day == null ) {
							a_day.clearContent( "day.number" );
							a_day.clearContent( "day.moon" );
							a_day.clearContent( "day.season" );
						} else {
							a_day.setContent( "day.number", String.valueOf( one_day.number() ) );
							
							if( one_day.moon_phase() == null ) {
								a_day.clearContent( "day.moon" );
							} else {
								switch( one_day.moon_phase() ) {
									case NEW_MOON -> a_day.setContent( "day.moon", "new-moon" );
									case WAXING_CRESCENT -> a_day.setContent( "day.moon", "waxing-crescent" );
									case FIRST_QUARTER -> a_day.setContent( "day.moon", "first-quarter" );
									case WAXING_GIBBOUS -> a_day.setContent( "day.moon", "waxing-gibbous" );
									case FULL_MOON -> a_day.setContent( "day.moon", "full-moon" );
									case WANING_GIBBOUS -> a_day.setContent( "day.moon", "waning-gibbous" );
									case LAST_QUARTER -> a_day.setContent( "day.moon", "last-quarter" );
									case WANING_CRESCENT -> a_day.setContent( "day.moon", "waning-crescent " );
								}
							}
							
							if( one_day.season() == null ) {
								a_day.clearContent( "day.season" );
							} else {
								switch( one_day.season() ) {
									case WINTER -> a_day.setContent( "day.season", "winter" );
									case SUMMER -> a_day.setContent( "day.season", "summer" );
									case SPRING -> a_day.setContent( "day.season", "spring" );
									case FALL -> a_day.setContent( "day.season", "fall" );
								}
							}
						}
						
						a_week.addContent( "week.content", a_day.getResult() );
					}
					a_month.addContent( "month.days", a_week.getResult() );
				}
				
				a_year.addContent( "year.content", a_month.getResult() );
				simulation.gotoNextMonth();
			}
			
			root.addContent( "calendar.body", a_year.getResult() );
		}
		
		root.setContent( "calendar.title", settings.title );
		root.setContent( "color.normal.bg", settings.normal_bg );
		root.setContent( "color.normal.fg", settings.normal_fg );
		root.setContent( "color.leap.bg", settings.leap_bg );
		root.setContent( "color.leap.fg", settings.leap_fg );
		root.setContent( "color.epagomenal.bg", settings.epi_bg );
		root.setContent( "color.epagomenal.fg", settings.epi_fg );
		
		try( var printer = new PrintWriter( file ) ) {
			printer.print( root.getResult() );
		} catch( FileNotFoundException ignored ) {
		
		}
	}

}
