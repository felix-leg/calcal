package pl.felixspeagel.calcal.file;

import pl.felixspeagel.calcal.calculators.IntercalationType;
import pl.felixspeagel.calcal.calculators.Rule;
import pl.felixspeagel.calcal.calendar.Month;
import pl.felixspeagel.calcal.calendar.SpecialFeature;
import pl.felixspeagel.calcal.calendar.Week;
import pl.felixspeagel.calcal.controllers.CalendarTypeInput;
import pl.felixspeagel.calcal.math.MixedFraction;
import pl.felixspeagel.calcal.math.NumberConverter;

import java.util.LinkedList;

public class MarkdownWriter implements SummaryWriter {
	
	private final TextGetter txt;
	private final String header;
	private String day_count_intro;
	private String leap_rules;
	private final LinkedList<String> month_descriptions;
	private String week_description;
	
	public MarkdownWriter(TextGetter getter, String title) {
		txt = getter;
		month_descriptions = new LinkedList<>();
		
		header = "# " + title + "\n\n\n";
	}
	
	public String getResult() {
		var result = new StringBuilder();
		
		result.append( header );
		result.append( day_count_intro );
		result.append( leap_rules );
		result.append( week_description );
		result.append( month_year_length );
		result.append("\n## ").append( txt.getText( "month_list_start" ) ).append( "\n\n" );
		for(var description : month_descriptions) {
			result.append( description );
		}
		
		return result.toString();
	}
	
	private String html2md(String html) {
		html = html.replace( "<b>", "**" ).replace( "</b>", "**" );
		html = html.replace( "<i>", "*" ).replace( "</i>", "*" );
		html = html.replace( "<br>", "  \n" );
		html = html.replace( "&gt;", ">" ).replace( "&lt;", "<" );
		return html;
	}
	
	@Override
	public void writeDayLength(int hours, int minutes, int seconds) {
		//Writer ignores this request
	}
	
	private String month_year_length;
	@Override
	public void writeYearMonthLength(MixedFraction year, MixedFraction month) {
		var template = html2md( txt.getText("month_year_length") );
		template = template.replace( "{{month}}", NumberConverter.FractionToDecimalText( month ) );
		template = template.replace( "{{year}}", NumberConverter.FractionToDecimalText( year ) );
		
		month_year_length = "\n\n" + template + "\n\n";
	}
	
	@Override
	public void writeDayCount(int normal_days_count, int leap_days_count) {
		String text;
		if( leap_days_count == 0 ) {
			text = html2md(txt.getText( "no_leap_days_intro" ));
			text = text.replace( "{{normal}}", String.valueOf( normal_days_count ) );
		} else {
			text = html2md(txt.getText( "leap_days_intro" ));
			text = text.replace( "{{normal}}", String.valueOf( normal_days_count ) );
			text = text.replace( "{{with_leap}}", String.valueOf( normal_days_count + leap_days_count ) );
		}
		day_count_intro = text + " ";
	}
	
	@Override
	public void writeUsedSolution(CalendarTypeInput.Solution solution) {
		//Writer ignores this request
	}
	
	@SuppressWarnings("StringConcatenationInLoop")
	@Override
	public void writeCycle(IntercalationType[] cycle) {
		leap_rules = html2md(txt.getText( "cycle_summary" )) + " ";
		leap_rules = leap_rules.replace( "{{cycle_length}}", String.valueOf( cycle.length ) );
		
		for(int year=0; year<cycle.length; year++) {
			if( cycle[year] == IntercalationType.LEAP ) {
				leap_rules += "*" + ( year+1 ) + "*";
				if( year < cycle.length-1 ) {
					leap_rules += ", ";
				} else {
					leap_rules += ".";
				}
			}
		}
		
		leap_rules += "\n\n";
	}
	
	@SuppressWarnings("StringConcatenationInLoop")
	@Override
	public void writeLeapRules(Rule[] rules, boolean[] active) {
		leap_rules = html2md( txt.getText("leap_rules_summary") ) + "\n\n";
		
		for(int i=0; i<rules.length; i++) {
			if( ! active[i] ) continue;
			var rule = rules[i];
			String text;
			if( rule.is_leap() == IntercalationType.LEAP ) {
				text = html2md( txt.getText( "leap_rule_div" ) );
				
			} else {
				text = html2md( txt.getText( "normal_rule_div" ) );
				
			}
			text = text.replace( "{{div}}", rule.each_year().toString() );
			leap_rules += " * " + text + "\n";
		}
		leap_rules += "\n\n";
	}
	
	@Override
	public void writeAboutMonth(Month month, int month_number, SpecialFeature feature) {
		var month_header = txt.getText("md_month_header");
		month_header = month_header.replace( "{{number}}", String.valueOf( month_number ) );
		month_header = month_header.replace( "{{name}}", month.name );
		month_header += "\n\n";
		
		String description ;
		if( month.leap_days == 0 ) {
			description = html2md( txt.getText( "month_description_line_no_leap" ) );
			description = description.replace( "{{count}}", String.valueOf( month.normal_days ) );
		} else {
			description = html2md( txt.getText( "month_description_line" ) );
			description = description.replace( "{{normal}}", String.valueOf( month.normal_days ) );
			description = description.replace( "{{leap}}", String.valueOf( month.leap_days ) );
		}
		
		if( feature != SpecialFeature.NONE ) {
			description += "  \n";
			switch( feature ) {
				case EPAGOMENAL -> description += "*" + txt.getText( "epagomenal_description" ) + "*";
				case LEAP -> description += "*" + txt.getText( "leap_month_description" ) + "*";
			}
		}
		description = description + "\n";
		
		month_descriptions.add( month_header + description + "\n" );
	}
	
	@Override
	public void writeAboutWeek(Week week) {
		week_description = html2md( txt.getText( "week_description" ) );
		week_description = week_description.replace( "{{count}}", String.valueOf( week.length ) );
		
		week_description += " ";
		if( week.starts_with_month ) {
			week_description += txt.getText("week_starts_with_month");
		} else {
			week_description += txt.getText("week_continues");
		}
		week_description += "\n\n";
	}
	
	
}
