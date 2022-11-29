package pl.felixspeagel.calcal.calendar;

import pl.felixspeagel.calcal.calculators.IntercalationType;
import pl.felixspeagel.calcal.calculators.Rule;

import java.util.Arrays;

public class Calendar {
	
	private final Year year_structure;
	private final Week week_setup;
	private IntercalationType[] cycle;
	private Rule[] leap_rules;
	private boolean[] rules_used;
	
	public Calendar(SpecialFeature calendar_feature) {
		year_structure = new Year(calendar_feature);// HAPPY NEW YEAR!
		week_setup = new Week();
		cycle = null;
		leap_rules = null;
		rules_used = null;
	}
	
	@Override
	public boolean equals(Object object) {
		if( object instanceof Calendar other) {
			return equals( other, true );
		}
		return false;
	}
	
	public boolean equals(Calendar other, boolean strict) {
	
		//test cycle
		if(cycle != null && other.cycle == null)
			return false;
		if(cycle == null && other.cycle != null)
			return false;
		if( cycle != null ) {
			if( cycle.length != other.cycle.length )
				return false;
			
			if( strict ) {
				for( int i = 0; i < cycle.length; i++ ) {
					if( ! cycle[i].equals( other.cycle[i] ) )
						return false;
				}
			}
		}
		
		//test rules
		if(leap_rules != null && other.leap_rules == null)
			return false;
		if(leap_rules == null && other.leap_rules != null)
			return false;
		if( leap_rules != null ) {
			if(leap_rules.length != other.leap_rules.length)
				return false;
			
			if( strict ) {
				for( int i = 0; i < leap_rules.length; i++ ) {
					if( ! leap_rules[i].equals( other.leap_rules[i] ) )
						return false;
				}
			}
		}
		
		//week
		if( strict && ! week_setup.equals( other.week_setup ) )
			return false;
		//year
		return year_structure.equals( other.year_structure, strict );
		
	}
	
	public Year getYear() {
		return year_structure;
	}
	
	public Week getWeek() {
		return week_setup;
	}
	
	public boolean hasCycle() {
		return cycle != null;
	}
	public boolean hasLeapRules() {
		return leap_rules != null;
	}
	
	public void setupCycle(IntercalationType[] new_cycle) {
		cycle = new_cycle.clone();
		leap_rules = null;
		rules_used = null;
	}
	public void setupLeapRules(Rule[] new_rules) {
		leap_rules = new_rules.clone();
		rules_used = new boolean[new_rules.length];
		cycle = null;
		
		Arrays.fill( rules_used, true );
	}
	public void setupLeapRules(Rule[] new_rules, boolean[] active) {
		leap_rules = new_rules.clone();
		rules_used = active;
		cycle = null;
	}
	
	public void switchRule(int index, boolean value) {
		if( rules_used == null ) return;
		if( index < 0 || index >= rules_used.length) return;
		
		rules_used[index] = value;
	}
	public boolean isRuleTurnedOn(int index) {
		if( rules_used == null ) return false;
		if( index < 0 || index >= rules_used.length) return false;
		
		return rules_used[index];
	}
	public void switchCycleOn(int index, IntercalationType to_type) {
		if( cycle == null ) return;
		if( index < 0 || index >= cycle.length ) return;
		
		cycle[index] = to_type;
	}
	public boolean isCycleOf(int index, IntercalationType a_type) {
		if( cycle == null ) return false;
		if( index < 0 || index >= cycle.length ) return false;
		
		return cycle[index] == a_type;
	}
	
	public IntercalationType[] getCycle() {
		return cycle;
	}
	public Rule[] getRules() {
		return leap_rules;
	}
	
}
