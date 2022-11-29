package pl.felixspeagel.calcal.file;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import pl.felixspeagel.calcal.calculators.IntercalationType;
import pl.felixspeagel.calcal.calculators.Rule;
import pl.felixspeagel.calcal.calendar.Calendar;
import pl.felixspeagel.calcal.calendar.Month;
import pl.felixspeagel.calcal.calendar.SpecialFeature;
import pl.felixspeagel.calcal.calendar.Week;
import pl.felixspeagel.calcal.controllers.CalendarTypeInput;
import pl.felixspeagel.calcal.math.HMSRecord;
import pl.felixspeagel.calcal.math.MixedFraction;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Objects;

/**
 * Reads calendar project from an XML file.
 */
public class ProjectReader {
	
	public static class ReaderException extends Exception {}
	
	public ProjectReader(File xml) throws ReaderException {
		var builderFactory = DocumentBuilderFactory.newDefaultInstance();
		try{
			var documentBuilder = builderFactory.newDocumentBuilder();
			
			var xmlDocument = documentBuilder.parse( xml );
			parseXML(xmlDocument.getDocumentElement());
		} catch( ParserConfigurationException | IOException | SAXException | ReaderException e ) {
			throw new ReaderException();
		}
	}
	
	private void parseXML(Element root) throws ReaderException {
		if( !Objects.equals( root.getTagName(), "project" ) ) {
			throw new ReaderException();
		}
		
		boolean dayNotParsed = true;
		boolean lengthNotParsed = true;
		boolean solutionNotParsed = true;
		boolean calendarNotParsed = true;
		
		for(var node=root.getFirstChild(); node!=null; node=node.getNextSibling()) {
			if( node.getNodeType() == Node.ELEMENT_NODE ) {
				switch( node.getNodeName() ) {
					case "day" -> dayNotParsed = ! parseDay(node);
					case "length" -> lengthNotParsed = ! parseYearMonthLength(node);
					case "solution" -> solutionNotParsed = ! parseSolution(node);
					case "calendar" -> calendarNotParsed = ! parseCalendar(node);
					default -> throw new ReaderException();
				}
			}
		}
		
		if( dayNotParsed || lengthNotParsed || solutionNotParsed || calendarNotParsed ) {
			throw new ReaderException();
		}
	}
	
	public MixedFraction month_length = null;
	public MixedFraction year_length = null;
	
	private boolean parseYearMonthLength(Node node) {
		if( node.hasAttributes() ) {
			try {
				var attr = node.getAttributes().getNamedItem( "month" );
				if( attr == null ) return false;
				
				month_length = MixedFraction.fromString( attr.getNodeValue() );
				
				attr = node.getAttributes().getNamedItem( "year" );
				if( attr == null ) return false;
				
				year_length = MixedFraction.fromString( attr.getNodeValue() );
				
				return true;
			}
			catch( Exception e ) {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public Calendar calendar;
	private SpecialFeature specialFeature;
	private LinkedList<Month> monthList;
	
	private boolean parseCalendar(Node node) {
		boolean cycleParsed = false;
		boolean rulesParsed = false;
		boolean monthsNotParsed = true;
		boolean weeksNotParsed = true;
		
		calendar = null;
		monthList = new LinkedList<>();
		specialFeature = SpecialFeature.NONE; //by default
		
		for(var subNode=node.getFirstChild(); subNode!=null; subNode=subNode.getNextSibling()) {
			if( subNode.getNodeType() == Node.ELEMENT_NODE ) {
				switch( subNode.getNodeName() ) {
					case "cycle" -> cycleParsed = parseCycle(subNode);
					case "rules" -> rulesParsed = parseRules(subNode);
					case "months" -> monthsNotParsed = ! parseMonths(subNode);
					case "week" -> weeksNotParsed = ! parseWeek(subNode);
					default -> { return false; }
				}
			}
		}
		
		if( monthsNotParsed || weeksNotParsed )
			return false;
		
		calendar = new Calendar( specialFeature );
		if( cycleParsed ) {
			calendar.setupCycle( cycle );
		}
		if( rulesParsed ) {
			calendar.setupLeapRules( rules, rules_active );
		}
		for(var month : monthList) {
			calendar.getYear().addMonth( month );
		}
		if( specialFeatureMonthIndex >= 0 ) {
			calendar.getYear().setFeatureMonth( specialFeatureMonthIndex );
		}
		calendar.getWeek().starts_with_month = week.starts_with_month;
		calendar.getWeek().length = week.length;
		
		return true;
	}
	
	private Week week;
	
	private boolean parseWeek(Node node) {
		week = new Week();
		if( node.hasAttributes() ) {
			var attrs = node.getAttributes();
			
			var attr = attrs.getNamedItem( "length" );
			if( attr == null ) return false;
			try{
				week.length = Integer.parseInt( attr.getNodeValue() );
			}catch( NumberFormatException e ) {
				return false;
			}
			
			attr = attrs.getNamedItem( "policy" );
			if( attr == null ) return false;
			switch( attr.getNodeValue() ) {
				case "month_start" -> week.starts_with_month = true;
				case "cross_month" -> week.starts_with_month = false;
				default -> { return false; }
			}
			return true;
		} else {
			return false;
		}
	}
	
	private int specialFeatureMonthIndex = -1;
	
	private boolean parseMonths(Node node) {
		int index = 0;
		for(var subNode=node.getFirstChild(); subNode!=null; subNode=subNode.getNextSibling()) {
			if( subNode.getNodeType() == Node.ELEMENT_NODE ) {
				if( !subNode.getNodeName().equals( "month" ) )
					return false;
				
				var name = "";
				int normal_days;
				int leap_days;
				
				if( !subNode.hasAttributes() )
					return false;
				
				Node attr;
				
				attr = subNode.getAttributes().getNamedItem( "name" );
				if( attr == null ) return false;
				name = attr.getNodeValue();
				
				try {
					attr = subNode.getAttributes().getNamedItem( "normal_days" );
					if( attr == null ) return false;
					normal_days = Integer.parseInt( attr.getNodeValue() );
					
					attr = subNode.getAttributes().getNamedItem( "leap_days" );
					if( attr == null ) return false;
					leap_days = Integer.parseInt( attr.getNodeValue() );
				}
				catch( NumberFormatException e ) {
					return false;
				}
				
				attr = subNode.getAttributes().getNamedItem( "feature" );
				if( attr == null ) return false;
				switch( attr.getNodeValue() ) {
					case "leap" -> {
						specialFeature = SpecialFeature.LEAP;
						specialFeatureMonthIndex = index;
					}
					case "epagomenal" -> {
						specialFeature = SpecialFeature.EPAGOMENAL;
						specialFeatureMonthIndex = index;
					}
				}
				
				monthList.add( new Month( name, normal_days, leap_days ) );
				index++;
			}
		}
		return true;
	}
	
	public HMSRecord day_length;
	
	private boolean parseDay(Node node) {
		Integer hours = null;
		Integer minutes = null;
		Integer seconds = null;
		
		if( node.hasAttributes() ) {
			var attrs = node.getAttributes();
			try {
				var attr = attrs.getNamedItem( "hours" );
				if( attr != null ) {
					hours = Integer.valueOf( attr.getNodeValue() );
					if( hours < 1 ) return false;
				}
				attr = attrs.getNamedItem( "minutes" );
				if( attr != null ) {
					minutes = Integer.valueOf( attr.getNodeValue() );
					if( minutes < 1 ) return false;
				}
				attr = attrs.getNamedItem( "seconds" );
				if( attr != null ) {
					seconds = Integer.valueOf( attr.getNodeValue() );
					if( seconds < 1 ) return false;
				}
				
				if( hours == null || minutes == null || seconds == null )
					return false;
				
				day_length = new HMSRecord( hours, minutes, seconds );
				return true;
			}
			catch( NumberFormatException e ) {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public CalendarTypeInput.Solution solution;
	
	private boolean parseSolution(Node node) {
		if( node.hasAttributes() ) {
			var attr = node.getAttributes().getNamedItem( "type" );
			if( attr == null ) return false;
			
			switch( attr.getNodeValue() ) {
				case "islamic" -> solution = CalendarTypeInput.Solution.ISLAMIC;
				case "pure_lunar" -> solution = CalendarTypeInput.Solution.PURE_LUNAR;
				case "meton" -> solution = CalendarTypeInput.Solution.METON;
				case "egyptian" -> solution = CalendarTypeInput.Solution.EGYPTIAN;
				case "gregorian" -> solution = CalendarTypeInput.Solution.GREGORIAN;
				default -> { return false; }
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	private IntercalationType[] cycle = null;
	
	private boolean parseCycle(Node node) {
		var length = 0;
		if( node.hasAttributes() ) {
			var attr = node.getAttributes().getNamedItem( "length" );
			if( attr != null ) {
				try {
					length = Integer.parseInt( attr.getNodeValue() );
				}catch( NumberFormatException e ) {
					return false;
				}
			}
		}
		if( length < 1 )
			return false;
		
		cycle = new IntercalationType[length];
		int parsedNode = 0;
		
		for(var subNode=node.getFirstChild(); subNode!=null; subNode=subNode.getNextSibling()) {
			if( subNode.getNodeType() == Node.ELEMENT_NODE ) {
				if( subNode.getNodeName().equals( "year" ) ) {
					var attr = subNode.getAttributes().getNamedItem( "type" );
					if( attr == null ) return false;
					
					switch( attr.getNodeValue() ) {
						case "leap" -> cycle[parsedNode] = IntercalationType.LEAP;
						case "normal" -> cycle[parsedNode] = IntercalationType.NORMAL;
						default -> {return false;}
					}
					parsedNode++;
				} else {
					return false;
				}
			}
		}
		
		return ( parsedNode == length );
	}
	
	private Rule[] rules = null;
	private boolean[] rules_active = null;
	
	private boolean parseRules(Node node) {
		var length = 0;
		if( node.hasAttributes() ) {
			var attr = node.getAttributes().getNamedItem( "length" );
			if( attr != null ) {
				try {
					length = Integer.parseInt( attr.getNodeValue() );
				}catch( NumberFormatException e ) {
					return false;
				}
			}
		}
		if( length < 1 )
			return false;
		
		rules = new Rule[length];
		rules_active = new boolean[length];
		int parsedNode = 0;
		
		for(var subNode=node.getFirstChild(); subNode!=null; subNode=subNode.getNextSibling()) {
			if( subNode.getNodeType() == Node.ELEMENT_NODE ) {
				if( subNode.getNodeName().equals( "rule" ) ) {
					var attr = subNode.getAttributes().getNamedItem( "type" );
					if( attr == null ) return false;
					
					IntercalationType type;
					switch( attr.getNodeValue() ) {
						case "leap" -> type = IntercalationType.LEAP;
						case "normal" -> type = IntercalationType.NORMAL;
						default -> {return false;}
					}
					
					attr = subNode.getAttributes().getNamedItem( "div" );
					if( attr == null ) return false;
					
					try {
						BigInteger value = BigInteger.valueOf(
								Long.parseLong( attr.getNodeValue() )
						);
						
						rules[parsedNode] = new Rule( value, type );
					}catch( NumberFormatException e ) {
						return false;
					}
					
					attr = subNode.getAttributes().getNamedItem( "active" );
					if( attr == null ) return false;
					
					rules_active[parsedNode] = Boolean.parseBoolean( attr.getNodeValue() );
					
					parsedNode++;
				} else {
					return false;
				}
			}
		}
		
		return ( parsedNode == length );
	}
	
}
