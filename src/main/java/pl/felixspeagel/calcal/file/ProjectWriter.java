package pl.felixspeagel.calcal.file;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pl.felixspeagel.calcal.calculators.IntercalationType;
import pl.felixspeagel.calcal.calculators.Rule;
import pl.felixspeagel.calcal.calendar.Month;
import pl.felixspeagel.calcal.calendar.SpecialFeature;
import pl.felixspeagel.calcal.calendar.Week;
import pl.felixspeagel.calcal.controllers.CalendarTypeInput;
import pl.felixspeagel.calcal.math.MixedFraction;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.LinkedList;

public class ProjectWriter implements SummaryWriter {
	
	private final Document doc;
	private final Element root;
	
	private Element day_element = null;
	private Element month_year_length = null;
	private Element solution_element = null;
	private Element cycle_element = null;
	private Element rules_element = null;
	private final LinkedList<Element> months_element;
	private Element weeks_element = null;
	
	public ProjectWriter() {
		var factory = DocumentBuilderFactory.newDefaultInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = factory.newDocumentBuilder();
		} catch( ParserConfigurationException e ) {
			throw new RuntimeException( e );
		}
		doc = docBuilder.newDocument();
		root = doc.createElement( "project" );
		doc.appendChild( root );
		
		months_element = new LinkedList<>();
	}
	
	public void saveProject(File file) {
		root.appendChild( day_element );
		root.appendChild( solution_element );
		root.appendChild( month_year_length );
		
		var calendarElement = doc.createElement( "calendar" );
		root.appendChild( calendarElement );
		if( cycle_element != null )
			calendarElement.appendChild( cycle_element );
		else if( rules_element != null )
			calendarElement.appendChild( rules_element );
		
		var monthsElement = doc.createElement( "months" );
		calendarElement.appendChild( monthsElement );
		for(var month : months_element) {
			monthsElement.appendChild( month );
		}
		calendarElement.appendChild( weeks_element );
		
		try {
			var transformerFactory = TransformerFactory.newDefaultInstance();
			var transformer = transformerFactory.newTransformer();
			var source = new DOMSource(doc);
			var stream = new StreamResult(file);
			
			transformer.transform( source, stream );
		} catch( TransformerException e ) {
			//ignore exceptions
		}
	}
	
	@Override
	public void writeDayLength(int hours, int minutes, int seconds) {
		var element = doc.createElement( "day" );
		element.setAttribute( "hours", String.valueOf( hours ) );
		element.setAttribute( "minutes", String.valueOf( minutes ) );
		element.setAttribute( "seconds", String.valueOf( seconds ) );
		day_element = element;
	}
	
	@Override
	public void writeDayCount(int normal_days_count, int leap_days_count) {
		//XML ignores this request
	}
	
	@Override
	public void writeYearMonthLength(MixedFraction year, MixedFraction month) {
		var element = doc.createElement( "length" );
		element.setAttribute( "month", month.toString() );
		element.setAttribute( "year", year.toString() );
		month_year_length = element;
	}
	
	@Override
	public void writeUsedSolution(CalendarTypeInput.Solution solution) {
		var element = doc.createElement( "solution" );
		var typeName = "";
		switch( solution ) {
			case ISLAMIC -> typeName = "islamic";
			case PURE_LUNAR -> typeName = "pure_lunar";
			case METON -> typeName = "meton";
			case EGYPTIAN -> typeName = "egyptian";
			case GREGORIAN -> typeName = "gregorian";
		}
		element.setAttribute( "type", typeName );
		solution_element = element;
	}
	
	@Override
	public void writeCycle(IntercalationType[] cycle) {
		var mainElement = doc.createElement( "cycle" );
		mainElement.setAttribute( "length", String.valueOf( cycle.length ) );
		for(var year : cycle) {
			var cycleElement = doc.createElement( "year" );
			switch( year ) {
				case LEAP -> cycleElement.setAttribute( "type", "leap" );
				case NORMAL -> cycleElement.setAttribute( "type", "normal" );
			}
			mainElement.appendChild( cycleElement );
		}
		cycle_element = mainElement;
	}
	
	@Override
	public void writeLeapRules(Rule[] rules, boolean[] active) {
		var mainElement = doc.createElement( "rules" );
		mainElement.setAttribute( "length", String.valueOf( rules.length ) );
		for(int i=0; i<rules.length; i++) {
			var rule = rules[i];
			var ruleElement = doc.createElement( "rule" );
			switch( rule.is_leap() ) {
				case NORMAL -> ruleElement.setAttribute( "type", "normal" );
				case LEAP -> ruleElement.setAttribute( "type", "leap" );
			}
			ruleElement.setAttribute( "div", rule.each_year().toString() );
			ruleElement.setAttribute( "active", String.valueOf( active[i] ) );;
			mainElement.appendChild( ruleElement );
		}
		rules_element = mainElement;
	}
	
	@Override
	public void writeAboutMonth(Month month, int monthNumber, SpecialFeature feature) {
		var mainElement = doc.createElement( "month" );
		
		mainElement.setAttribute( "name", month.name );
		mainElement.setAttribute( "normal_days", String.valueOf( month.normal_days ) );
		mainElement.setAttribute( "leap_days", String.valueOf( month.leap_days ) );
		
		//mainElement.setAttribute( "number", String.valueOf( monthNumber ) );
		
		switch( feature ) {
			case NONE -> mainElement.setAttribute( "feature", "none" );
			case LEAP -> mainElement.setAttribute( "feature", "leap" );
			case EPAGOMENAL -> mainElement.setAttribute( "feature", "epagomenal" );
		}
		
		months_element.add(mainElement);
	}
	
	@Override
	public void writeAboutWeek(Week week) {
		var element = doc.createElement( "week" );
		
		element.setAttribute( "length", String.valueOf( week.length ) );
		if( week.starts_with_month ) {
			element.setAttribute( "policy", "month_start" );
		} else {
			element.setAttribute( "policy", "cross_month" );
		}
		
		weeks_element = element;
	}
}
