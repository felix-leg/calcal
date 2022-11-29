package pl.felixspeagel.calcal.views;

import pl.felixspeagel.calcal.controllers.CreateOrEditWizard;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class CalendarWizard extends JDialog {
	
	private final ResourceBundle txt;
	private final CreateOrEditWizard the_wizard;
	public CalendarWizard(JFrame parent, String title, CreateOrEditWizard wizard) {
		super(parent, title, true);
		txt = ResourceBundle.getBundle( "calendar_wizard", Locale.getDefault() );
		the_wizard = wizard;
		
		this.setLayout( new BorderLayout() );
		
		// the panel with a list of stage buttons
		var leftPanel = new JPanel();
		this.add( leftPanel, BorderLayout.WEST );
		leftPanel.setLayout( new GridLayout(10,1) );
		leftPanel.setBorder(
				BorderFactory.createMatteBorder( 0, 0, 0, 2, Color.decode( "#404070" ) )
		);
		
		year_month_stage_button = new JToggleButton(txt.getString( "year_and_month_stage_button" ));
		//year_month_stage_button.doClick();
		leftPanel.add( year_month_stage_button );
		calendar_type_stage_button = new JToggleButton(txt.getString( "calendar_type_stage_button" ));
		leftPanel.add( calendar_type_stage_button );
		calendar_setup_stage_button = new JToggleButton(txt.getString( "calendar_setup_stage_button" ));
		leftPanel.add( calendar_setup_stage_button );
		summary_stage_button = new JToggleButton(txt.getString( "summary_stage_button" ));
		leftPanel.add( summary_stage_button );
		
		var leftPanelButtonGroup = new ButtonGroup();
		leftPanelButtonGroup.add( year_month_stage_button );
		leftPanelButtonGroup.add( calendar_type_stage_button );
		leftPanelButtonGroup.add( calendar_setup_stage_button );
		leftPanelButtonGroup.add( summary_stage_button );
		
		//the panel with stage title
		var titlePanel = new JPanel();
		this.add( titlePanel, BorderLayout.NORTH );
		titlePanel.setBorder(
				BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder( 0, 4, 4, 0,
				Color.decode( "#404070" )
				), BorderFactory.createEmptyBorder(3,20,10,3)	)
		);
		titlePanel.setLayout( new BoxLayout( titlePanel, BoxLayout.LINE_AXIS ) );
		
		title_label = new JLabel("");
		titlePanel.add(title_label);
		title_label.setFont( new Font("Sans-serif", Font.ITALIC, 48) );
		title_label.setHorizontalTextPosition( JLabel.LEADING );
		titlePanel.setBackground( Color.decode( "#e0e0ff" ) );
		titlePanel.setForeground( Color.BLACK );
		
		//central panel with the content
		central_panel = new JPanel();
		this.add(central_panel, BorderLayout.CENTER);
		
		central_panel.setLayout( new CardLayout() );
		cards = (CardLayout) central_panel.getLayout();
		
		year_month_view = new YearMonthLengthView( the_wizard.year_month_input );
		calendar_type_view = new CalendarTypeView( the_wizard.calendar_type_input );
		var calendar_editor_view = new CalendarEditorView( the_wizard.calendar_editor_input );
		var summary_view = new SummaryView( the_wizard.summary_output );
		central_panel.add(wrap(year_month_view), "year_month");
		central_panel.add(wrap(calendar_type_view), "calendar_type");
		central_panel.add(wrap(calendar_editor_view), "setup");
		central_panel.add(wrap(summary_view), "summary");
		
		//bottom panel with "Next" and "Previous" buttons
		var bottom_panel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		this.add(bottom_panel, BorderLayout.SOUTH);
		bottom_panel.setBorder( BorderFactory.createEmptyBorder(5,5,5,5) );
		
		var prev_icon = new ImageIcon(ClassLoader.getSystemResource( "prev_arrows.png" ));
		var next_icon = new ImageIcon(ClassLoader.getSystemResource( "next_arrows.png" ));
		prevButton = new JButton(txt.getString( "prev_button" ), prev_icon);
		nextButton = new JButton(txt.getString( "next_button" ), next_icon);
		nextButton.setHorizontalTextPosition( AbstractButton.LEFT );
		prevButton.setHorizontalTextPosition( AbstractButton.RIGHT );
		
		bottom_panel.add(prevButton);
		bottom_panel.add(Box.createHorizontalStrut( 10 ));
		bottom_panel.add(nextButton);
		
		//finish
		updateStageView();
		updateStageButtons();
		this.pack();
		
		if( testForDarkTheme( year_month_stage_button ) ) {
			//dark theme!
			for(var panel : central_panel.getComponents()) {
				if( panel instanceof Container container ) {
					var wrapped = container.getComponent( 0 );
					if( wrapped instanceof DarkThemeAware aware_panel ) {
						aware_panel.switchToDarkTheme();
					}
				}
			}
		}
		
		year_month_stage_button.addActionListener( event -> switchToStage(
				CreateOrEditWizard.Stage.MONTH_AND_YEAR_DEFINITION
		) );
		calendar_type_stage_button.addActionListener( event -> switchToStage(
				CreateOrEditWizard.Stage.CALENDAR_TYPE
		) );
		calendar_setup_stage_button.addActionListener( event -> switchToStage(
				CreateOrEditWizard.Stage.CALENDAR_CREATION
		) );
		summary_stage_button.addActionListener( event -> switchToStage(
				CreateOrEditWizard.Stage.SUMMARY
		) );
		
		nextButton.addActionListener( event -> gotoNextStage() );
		prevButton.addActionListener( event -> gotoPrevStage() );
		
		the_wizard.refreshList.add( this::refreshGUI );
		
		switch( the_wizard.getCurrentStage() ) {
			case MONTH_AND_YEAR_DEFINITION -> year_month_stage_button.doClick();
			case CALENDAR_TYPE -> calendar_type_stage_button.doClick();
			case CALENDAR_CREATION -> calendar_setup_stage_button.doClick();
			case SUMMARY -> summary_stage_button.doClick();
		}
	}
	
	private boolean testForDarkTheme(AbstractButton test_button) {
		var buttons_color = test_button.getBackground();
		var hsv = Color.RGBtoHSB(
				buttons_color.getRed(),
				buttons_color.getGreen(),
				buttons_color.getBlue(),
				new float[3]
		);
		return hsv[2] <= 0.5f;
	}
	
	private JPanel wrap(JPanel panel) {
		var wrapper = new JPanel();
		wrapper.setLayout( new GridBagLayout() );
		var c = new GridBagConstraints();
		c.gridx = c.gridy = 0;
		c.gridwidth = c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.PAGE_START;
		c.weighty = 1.0;
		c.weightx = 0.5;
		wrapper.add(panel, c);
		return wrapper;
	}
	
	private final JLabel title_label;
	private final JToggleButton year_month_stage_button;
	private final JToggleButton calendar_type_stage_button;
	private final JToggleButton calendar_setup_stage_button;
	private final JToggleButton summary_stage_button;
	private final CardLayout cards;
	private final JPanel central_panel;
	private final JButton prevButton;
	private final JButton nextButton;
	
	private final YearMonthLengthView year_month_view;
	private final CalendarTypeView calendar_type_view;
	
	@SuppressWarnings("unused")
	private void refreshGUI(boolean full) {
		year_month_view.updateCalculations();
		calendar_type_view.updateContent();
		updateStageButtons();
		updateStageView();
		this.pack();
	}
	
	private void switchToStage(CreateOrEditWizard.Stage stage) {
		the_wizard.gotoStage( stage );
		updateStageView();
		updateStageButtons();
		this.pack();
	}
	
	private void updateStageView() {
		if( year_month_stage_button.isSelected() ) {
			title_label.setText( txt.getString( "year_and_month_stage_title" ) );
			cards.show( central_panel, "year_month" );
		} else if( calendar_type_stage_button.isSelected() ) {
			title_label.setText( txt.getString( "calendar_type_stage_title" ) );
			cards.show( central_panel, "calendar_type" );
		} else if( calendar_setup_stage_button.isSelected() ) {
			title_label.setText( txt.getString( "calendar_setup_stage_title" ) );
			cards.show( central_panel, "setup" );
		} else if( summary_stage_button.isSelected() ) {
			title_label.setText( txt.getString( "summary_stage_title" ) );
			cards.show( central_panel, "summary" );
		}
	}
	
	private void updateStageButtons() {
		year_month_stage_button.setEnabled(
				the_wizard.isStageActive( CreateOrEditWizard.Stage.MONTH_AND_YEAR_DEFINITION )
		);
		calendar_type_stage_button.setEnabled(
				the_wizard.isStageActive( CreateOrEditWizard.Stage.CALENDAR_TYPE )
		);
		calendar_setup_stage_button.setEnabled(
				the_wizard.isStageActive( CreateOrEditWizard.Stage.CALENDAR_CREATION )
		);
		summary_stage_button.setEnabled(
				the_wizard.isStageActive( CreateOrEditWizard.Stage.SUMMARY )
		);
		
		nextButton.setEnabled( the_wizard.canGotoNextStage() );
		prevButton.setEnabled( the_wizard.canGotoPreviousStage() );
	}
	
	private void gotoNextStage() {
		if( ! the_wizard.gotoNextStage() ) return;
		
		updateStageButtons();
		switch( the_wizard.getCurrentStage() ) {
			case MONTH_AND_YEAR_DEFINITION -> year_month_stage_button.doClick();
			case CALENDAR_TYPE -> calendar_type_stage_button.doClick();
			case CALENDAR_CREATION -> calendar_setup_stage_button.doClick();
			case SUMMARY -> summary_stage_button.doClick();
		}
		this.pack();
	}
	
	private void gotoPrevStage() {
		the_wizard.gotoPreviousStage();
		
		updateStageButtons();
		switch( the_wizard.getCurrentStage() ) {
			case MONTH_AND_YEAR_DEFINITION -> year_month_stage_button.doClick();
			case CALENDAR_TYPE -> calendar_type_stage_button.doClick();
			case CALENDAR_CREATION -> calendar_setup_stage_button.doClick();
		}
		this.pack();
	}
}
