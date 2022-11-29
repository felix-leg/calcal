package pl.felixspeagel.calcal.controllers;

import pl.felixspeagel.calcal.calculators.*;
import pl.felixspeagel.calcal.controllers.models.CalendarTypeData;
import pl.felixspeagel.calcal.controllers.models.YearMonthLengthData;
import pl.felixspeagel.calcal.math.Pair;

import java.util.ArrayList;
import java.util.HashMap;

public class CalendarTypeInput {
	
	public enum BodyTracked {
		SUN,
		MOON,
		SUN_AND_MOON
	}
	
	public enum Solution {
		//sun
		GREGORIAN,
		EGYPTIAN,
		//sun+moon
		METON,
		//moon
		PURE_LUNAR,
		ISLAMIC
	}
	
	private BodyTracked body_tracked;
	private Solution solution_chosen;
	private YearMonthLengthData previous_stage_data;
	private final HashMap<Solution, Boolean> available_solutions;
	
	public final ArrayList<Refreshable> refreshList;
	private void refreshGUI(boolean full) {
		for(var aRefreshable : refreshList) {
			aRefreshable.refresh(full);
		}
	}
	
	public CalendarTypeInput(Solution project_solution) {
		body_tracked = null;
		solution_chosen = null;
		previous_stage_data = null;
		refreshList = new ArrayList<>();
		available_solutions = new HashMap<>();
		
		available_solutions.put( Solution.GREGORIAN, false );
		available_solutions.put( Solution.EGYPTIAN, false );
		available_solutions.put( Solution.METON, false );
		available_solutions.put( Solution.PURE_LUNAR, false );
		available_solutions.put( Solution.ISLAMIC, false );
		
		if( project_solution != null ) {
			solution_chosen = project_solution;
			BodyTracked body = null;
			switch( project_solution ) {
				case GREGORIAN, EGYPTIAN -> body = BodyTracked.SUN;
				case METON -> body = BodyTracked.SUN_AND_MOON;
				case ISLAMIC, PURE_LUNAR -> body = BodyTracked.MOON;
			}
			switchBody( body );
		}
	}
	
	public boolean inErrorState() {
		if( body_tracked == null || solution_chosen == null || solution_object == null )
			return true;
		if( solution_object instanceof Gregorian gregorian ) {
			return gregorian.unable_to_compute;
		}
		if( solution_object instanceof PureLunar lunar ) {
			return lunar.unable_to_compute;
		}
		return false;
	}
	
	public Pair<BodyTracked, Solution> getSelection() {
		if( body_tracked == null ) {
			return new Pair<>( null, null );
		} else if( solution_chosen == null ) {
			return new Pair<>( body_tracked, null );
		} else {
			return new Pair<>( body_tracked, solution_chosen );
		}
	}
	
	
	public void switchBody(BodyTracked body) {
		if( body == null ) {
			return;
		}
		if( body.equals( body_tracked ) ) {
			return;
		}
		body_tracked = body;
		
		switch( body_tracked ) {
			case SUN -> {
				available_solutions.put( Solution.GREGORIAN, true );
				available_solutions.put( Solution.EGYPTIAN, true );
				available_solutions.put( Solution.METON, false );
				available_solutions.put( Solution.PURE_LUNAR, false );
				available_solutions.put( Solution.ISLAMIC, false );
			}
			case SUN_AND_MOON -> {
				available_solutions.put( Solution.GREGORIAN, false );
				available_solutions.put( Solution.EGYPTIAN, false );
				available_solutions.put( Solution.METON, true );
				available_solutions.put( Solution.PURE_LUNAR, false );
				available_solutions.put( Solution.ISLAMIC, false );
			}
			case MOON -> {
				available_solutions.put( Solution.GREGORIAN, false );
				available_solutions.put( Solution.EGYPTIAN, false );
				available_solutions.put( Solution.METON, false );
				available_solutions.put( Solution.PURE_LUNAR, true );
				available_solutions.put( Solution.ISLAMIC, true );
			}
		}
		refreshGUI(false);
	}
	
	public boolean isSolutionAvailable(Solution solution) {
		return available_solutions.get( solution );
	}
	
	public void chooseSolution(Solution solution) {
		if( solution == null ) {
			solution_chosen = null;
			updateSolution(false);
		} else if( isSolutionAvailable( solution ) ) {
			solution_chosen = solution;
			updateSolution(false);
		}
	}
	
	public void setPreviousData(YearMonthLengthData data) {
		previous_stage_data = data;
		updateSolution(true);
	}
	
	private CalendarCreator solution_object = null;
	
	private void updateSolution(boolean full) {
		if( solution_chosen == null || previous_stage_data == null) {
			solution_object = null;
			refreshGUI(full);
			return;
		}
		
		switch( solution_chosen ) {
			case METON -> solution_object = new MetonicCycle(
					previous_stage_data.year(),
					previous_stage_data.month(),
					previous_stage_data.months_in_year()
			);
			case GREGORIAN -> solution_object = new Gregorian( previous_stage_data.year() );
			case EGYPTIAN -> solution_object = new Egyptian(
					previous_stage_data.year(),
					previous_stage_data.month(),
					previous_stage_data.months_in_year()
			);
			case PURE_LUNAR -> solution_object = new PureLunar(
					previous_stage_data.month(),
					previous_stage_data.months_in_year()
			);
			case ISLAMIC -> solution_object = new Islamic(
					previous_stage_data.month(),
					previous_stage_data.months_in_year()
			);
			
			default -> solution_object = null;
		}
		
		refreshGUI(full);
	}
	
	public CalendarCreator getSolutionObject() {
		return solution_object;
	}
	
	public CalendarTypeData getFinalInput() {
		if( inErrorState() )
			return null;
		
		return new CalendarTypeData(
				solution_chosen,
				solution_object.makeExampleCalendar(
						previous_stage_data.year(),
						previous_stage_data.month(),
						previous_stage_data.months_in_year()
				),
				previous_stage_data
		);
	}
	
}
