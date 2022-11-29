package pl.felixspeagel.calcal.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class CreateOrEditWizardTest {
	
	@BeforeEach
	void setUp() {
		wizard = new CreateOrEditWizard(null);
	}
	private CreateOrEditWizard wizard;
	
	@Test
	void startAtFirstStage() {
		assertEquals( CreateOrEditWizard.Stage.MONTH_AND_YEAR_DEFINITION, wizard.getCurrentStage() );
		assertFalse( wizard.isCurrentStageInError() );
		
		assertTrue( wizard.isStageActive( CreateOrEditWizard.Stage.MONTH_AND_YEAR_DEFINITION ) );
		assertFalse( wizard.isStageActive( CreateOrEditWizard.Stage.CALENDAR_TYPE ) );
	}
	
	@Test
	void editing_month_and_year_length_sends_notify() {
		AtomicBoolean notified = new AtomicBoolean( false );
		wizard.refreshList.add( (ign) -> notified.set( true ) );
		
		var my_length = wizard.year_month_input;
		
		assertFalse( notified.get() );
		
		my_length.getDayInput().enterHoursText( "12" );
		assertTrue( notified.get() );
		notified.set( false );
		
		my_length.getDayInput().enterMinutesText( "30" );
		assertTrue( notified.get() );
		notified.set( false );
		
		my_length.getDayInput().enterSecondsText( "30" );
		assertTrue( notified.get() );
		notified.set( false );
		
		my_length.getMonthInput().setText( "29" );
		assertTrue( notified.get() );
		notified.set( false );
		
		my_length.getYearInput().setText( "365" );
		assertTrue( notified.get() );
		notified.set( false );
	}
	
	@Test
	public void on_correct_input_can_goto_next_stage() {
		assertFalse( wizard.isCurrentStageInError() );
		
		//can go to next stage (= CalendarType)
		assertTrue( wizard.gotoNextStage() );
		//can't go yet to the next stage
		assertTrue( wizard.isCurrentStageInError() );
		
		//chose some settings
		wizard.calendar_type_input.switchBody( CalendarTypeInput.BodyTracked.SUN );
		wizard.calendar_type_input.chooseSolution( CalendarTypeInput.Solution.GREGORIAN );
		
		//can go to next stage
		assertFalse( wizard.isCurrentStageInError() );
		assertTrue( wizard.gotoNextStage() );
		
		//summary stage
		assertFalse( wizard.isCurrentStageInError() );
		assertTrue( wizard.gotoNextStage() );
		
		assertFalse(wizard.isCurrentStageInError());
	}
	
}