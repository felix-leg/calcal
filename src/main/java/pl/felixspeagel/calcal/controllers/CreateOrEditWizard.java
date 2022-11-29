package pl.felixspeagel.calcal.controllers;

import pl.felixspeagel.calcal.file.ProjectReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the process of creating/editing a calendar.
 */
public class CreateOrEditWizard {
	
	public enum Stage {
		MONTH_AND_YEAR_DEFINITION,
		CALENDAR_TYPE,
		CALENDAR_CREATION,
		SUMMARY
	}
	
	public final YearMonthLengthInput year_month_input;
	public final CalendarTypeInput calendar_type_input;
	public final CalendarEditor calendar_editor_input;
	public final Summary summary_output;
	private Stage current_stage;
	
	public CreateOrEditWizard(ProjectReader loaded_project) {
		summary_output = new Summary();
		if( loaded_project == null ) {
			year_month_input = new YearMonthLengthInput();
			calendar_type_input = new CalendarTypeInput(null);
			calendar_editor_input = new CalendarEditor(null);
			current_stage = Stage.MONTH_AND_YEAR_DEFINITION;
		} else {
			year_month_input = new YearMonthLengthInput(
					loaded_project.day_length.hours(),
					loaded_project.day_length.minutes(),
					loaded_project.day_length.seconds(),
					loaded_project.year_length,
					loaded_project.month_length
			);
			
			calendar_type_input = new CalendarTypeInput(loaded_project.solution);
			calendar_type_input.setPreviousData( year_month_input.getFinalInput() );
			
			calendar_editor_input = new CalendarEditor(loaded_project.calendar);
			calendar_editor_input.setPreviousData( calendar_type_input.getFinalInput() );
			
			current_stage = Stage.CALENDAR_CREATION;
		}
		refreshList = new ArrayList<>();
		
		year_month_input.refreshList.add( this::refreshGUI );
		calendar_type_input.refreshList.add( this::refreshGUI );
		calendar_editor_input.refreshList.add( this::refreshGUI );
		summary_output.refreshList.add( this::refreshGUI );
		
		active_stages = new HashMap<>();
		active_stages.put( Stage.MONTH_AND_YEAR_DEFINITION, true );
		if( loaded_project == null ) {
			active_stages.put( Stage.CALENDAR_TYPE, false );
			active_stages.put( Stage.CALENDAR_CREATION, false );
			active_stages.put( Stage.SUMMARY, false );
		} else {
			active_stages.put( Stage.CALENDAR_TYPE, true );
			active_stages.put( Stage.CALENDAR_CREATION, true );
			active_stages.put( Stage.SUMMARY, true );
		}
	}
	public final ArrayList<Refreshable> refreshList;
	private void refreshGUI(boolean full) {
		for(var aRefreshable : refreshList) {
			aRefreshable.refresh(full);
		}
	}
	
	public boolean isCurrentStageInError() {
		switch( current_stage ) {
			case MONTH_AND_YEAR_DEFINITION -> { return year_month_input.inErrorState(); }
			case CALENDAR_TYPE -> { return calendar_type_input.inErrorState(); }
			case CALENDAR_CREATION -> { return calendar_editor_input.inErrorState(); }
			case SUMMARY -> { return false; }
		}
		return true;
	}
	public Stage getCurrentStage() {
		return current_stage;
	}
	
	private final Map<Stage, Boolean> active_stages;
	public boolean isStageActive(Stage test_stage) {
		return active_stages.get( test_stage );
	}
	
	public boolean gotoNextStage() {
		if( isCurrentStageInError() ) {
			return false;
		}
		
		switch( current_stage ) {
			case MONTH_AND_YEAR_DEFINITION -> {
				var final_input = year_month_input.getFinalInput();
				calendar_type_input.setPreviousData( final_input );
				current_stage = Stage.CALENDAR_TYPE;
				active_stages.put( Stage.CALENDAR_TYPE, true );
			}
			case CALENDAR_TYPE -> {
				var final_input = calendar_type_input.getFinalInput();
				calendar_editor_input.setPreviousData( final_input );
				current_stage = Stage.CALENDAR_CREATION;
				active_stages.put( Stage.CALENDAR_CREATION, true);
			}
			case CALENDAR_CREATION -> {
				var final_input = calendar_editor_input.getFinalInput();
				summary_output.setPreviousData( final_input );
				current_stage = Stage.SUMMARY;
				active_stages.put( Stage.SUMMARY, true);
			}
			case SUMMARY -> {
				return false; //there is no stage after that
			}
		}
		refreshGUI(false);
		return true;
	}
	
	public void gotoPreviousStage() {
		switch( current_stage ) {
			case SUMMARY -> current_stage = Stage.CALENDAR_CREATION;
			case CALENDAR_CREATION -> current_stage = Stage.CALENDAR_TYPE;
			case CALENDAR_TYPE -> current_stage = Stage.MONTH_AND_YEAR_DEFINITION;
			case MONTH_AND_YEAR_DEFINITION -> { return; }
		}
		refreshGUI(false);
	}
	
	public boolean canGotoPreviousStage() {
		return current_stage != Stage.MONTH_AND_YEAR_DEFINITION;
	}
	public boolean canGotoNextStage() {
		if( current_stage == Stage.SUMMARY )
			return false;
		return ! this.isCurrentStageInError();
	}
	
	public void gotoStage( Stage goto_stage ) {
		var stage_list = Stage.values();
		int current_index = 0, goto_index = 0, index_diff;
		
		for(int i=0; i<stage_list.length; i++) {
			if( current_stage.equals( stage_list[i] ) ) {
				current_index = i;
				break;
			}
		}
		for(int i=0; i<stage_list.length; i++) {
			if( goto_stage.equals( stage_list[i] ) ) {
				goto_index = i;
				break;
			}
		}
		index_diff = goto_index - current_index;
		
		if( index_diff > 0 ) {
			while( index_diff > 0 ) {
				if( ! gotoNextStage() ) return;
				index_diff--;
			}
		} else if( index_diff < 0 ) {
			while( index_diff < 0 ) {
				gotoPreviousStage();
				index_diff++;
			}
		}
	}
}
