package pl.felixspeagel.calcal.views;

import pl.felixspeagel.calcal.controllers.YearMonthLengthInput;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Locale;
import java.util.ResourceBundle;

public class YearMonthLengthView extends JPanel implements KeyListener, DarkThemeAware {
	private final JTextField hours_text;
	private final JTextField minutes_text;
	private final JTextField seconds_text;
	private final JPanel day_length_panel;
	private final JPanel calculation_result;
	
	private final YearMonthLengthInput controller;
	private final JPanel known_values_panel;
	
	private interface ErrorCheckLambda {
		void check();
	}
	
	private final ResourceBundle txt;
	
	public YearMonthLengthView(YearMonthLengthInput a_controller) {
		super();
		txt = ResourceBundle.getBundle( "year_month_input", Locale.getDefault() );
		controller = a_controller;
		
		this.setLayout( new BoxLayout( this, BoxLayout.PAGE_AXIS ) );
		this.setBorder( BorderFactory.createEmptyBorder(20,20,20,20) );
		
		var introText = new JLabel("<html>" + txt.getString( "intro_text" ) + "</html>");
		introText.setFont( new Font( "Serif", Font.PLAIN, 14 ) );
		introText.setAlignmentX( 0.5f );
		this.add(introText);
		
		this.add(Box.createVerticalStrut( 50 ));
		
		//Day length components
		day_length_panel = new JPanel();
		day_length_panel.setLayout( new BoxLayout( day_length_panel, BoxLayout.LINE_AXIS ) );
		day_length_panel.setBorder( BorderFactory.createTitledBorder( txt.getString( "day_length_title" ) ) );
		
		hours_text = create_hms_input(controller.getDayInput().getHoursText());
		day_length_panel.add(hours_text);
		day_length_panel.add(new JLabel(txt.getString( "hours_label" )));
		day_length_panel.add(Box.createHorizontalStrut( 10 ));
		
		minutes_text = create_hms_input(controller.getDayInput().getMinutesText());
		day_length_panel.add(minutes_text);
		day_length_panel.add(new JLabel(txt.getString( "minutes_label" )));
		day_length_panel.add(Box.createHorizontalStrut( 10 ));
		
		seconds_text = create_hms_input(controller.getDayInput().getSecondsText());
		day_length_panel.add(seconds_text);
		day_length_panel.add(new JLabel(txt.getString( "seconds_label" )));
		
		hours_text.addKeyListener( this );
		minutes_text.addKeyListener( this );
		seconds_text.addKeyListener( this );
		
		//day_length_panel.setPreferredSize( day_length_panel.getMinimumSize() );
		this.add(day_length_panel);
		
		//Known values
		this.add(Box.createVerticalStrut( 10 ));
		
		known_values_panel = new JPanel();
		known_values_panel.setLayout( new BoxLayout( known_values_panel, BoxLayout.PAGE_AXIS ) );
		known_values_panel.setBorder( BorderFactory.createTitledBorder(txt.getString("known_values_label")));
		
		known_values_panel.add(
				new LengthInput(txt.getString("month_length_label"),
						txt,
						controller.getMonthInput(),
						this::updateCalculations
				));
		known_values_panel.add(
				new LengthInput(txt.getString("year_length_label"),
						txt,
						controller.getYearInput(),
						this::updateCalculations
				));
		
		//known_values_panel.setPreferredSize( known_values_panel.getMinimumSize() );
		this.add( known_values_panel );
		
		//calculation result
		//this.add(Box.createVerticalGlue());
		this.add(Box.createVerticalStrut(20));
		
		calculation_result = new JPanel();
		calculation_result.setBorder( BorderFactory.createLoweredBevelBorder() );
		calculation_result.setLayout( new BoxLayout( calculation_result, BoxLayout.PAGE_AXIS) );
		calculation_result.setAlignmentX( 0.5f );
		
		this.add(calculation_result);
		
		updateCalculations();
	}
	
	private JTextField create_hms_input(String initial_text) {
		final var height = 25;
		final var  width = 50;
		
		var input = new JTextField(initial_text);
		input.setPreferredSize( new Dimension( width, height ) );
		input.setMaximumSize( new Dimension( width, height ) );
		input.setHorizontalAlignment( JTextField.RIGHT );
		
		return input;
	}
	
	@Override
	public void switchToDarkTheme() {
		for(var component : known_values_panel.getComponents()) {
			if( component instanceof LengthInput aware) {
				aware.switchToDarkTheme();
			}
		}
	}
	
	@Override
	public void keyTyped(KeyEvent keyEvent) {
		//do nothing
	}
	
	@Override
	public void keyPressed(KeyEvent keyEvent) {
		//do noting
	}
	
	@Override
	public void keyReleased(KeyEvent keyEvent) {
		var source = keyEvent.getComponent();
		var day_input = controller.getDayInput();
		if(hours_text == source) {
			day_input.enterHoursText( hours_text.getText() );
		} else if(minutes_text == source) {
			day_input.enterMinutesText( minutes_text.getText() );
		} else if(seconds_text == source) {
			day_input.enterSecondsText( seconds_text.getText() );
		}
		
		String border_title = "";
		var border = day_length_panel.getBorder();
		if( border instanceof TitledBorder titled ) {
			border_title = titled.getTitle();
		} else if( border instanceof CompoundBorder comp ) {
			border = comp.getOutsideBorder();
			if( border instanceof TitledBorder titled ) {
				border_title = titled.getTitle();
			}
		}
		
		if( day_input.inErrorState() ) {
			day_length_panel.setBorder( BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder( border_title ),
					BorderFactory.createLineBorder( Color.RED )
			) );
		} else {
			day_length_panel.setBorder( BorderFactory.createTitledBorder( border_title ) );
		}
		
		updateCalculations();
	}
	
	public void updateCalculations() {
		calculation_result.removeAll();
		
		boolean noError = true;
		if( controller.getDayInput().inErrorState() ) {
			var label = new JLabel(txt.getString( "incorrect_day_length" ));
			label.setForeground( Color.RED );
			label.setAlignmentX( 0.5f );
			calculation_result.add(label);
			noError = false;
		}
		if( controller.getMonthInput().inErrorState() ) {
			var label = new JLabel(txt.getString( "incorrect_month_length" ));
			label.setForeground( Color.RED );
			label.setAlignmentX( 0.5f );
			calculation_result.add(label);
			noError = false;
		}
		if( controller.getYearInput().inErrorState() ) {
			var label = new JLabel(txt.getString( "incorrect_year_length" ));
			label.setForeground( Color.RED );
			label.setAlignmentX( 0.5f );
			calculation_result.add(label);
			noError = false;
		}
		
		if(noError) {
			var months = controller.getCalculatedMonthCount().toString();
			var result = "<html>" + txt.getString( "average_month_in_year" ) + "</html>";
			result = result.replace( "{{count}}", months );
			var label = new JLabel(result);
			label.setAlignmentX( 0.5f );
			calculation_result.add(label);
		}
		
		calculation_result.revalidate();
		calculation_result.repaint();
		this.revalidate();
	}
	
	private static class LengthInput extends JPanel implements ActionListener, KeyListener {
		
		private final JTextField the_input_field;
		
		private final YearMonthLengthInput.LengthInput the_input;
		
		private final Border okBorder;
		private final Color errorColor = Color.RED;
		
		private final ErrorCheckLambda errorChecker;
		private final JToggleButton decimal_switch;
		private final JToggleButton fractal_switch;
		@SuppressWarnings("FieldCanBeLocal")
		private final JToggleButton hms_switch;
		
		public LengthInput(String name, ResourceBundle txt, YearMonthLengthInput.LengthInput input, ErrorCheckLambda l) {
			super();
			the_input = input;
			errorChecker = l;
			
			this.setLayout( new BoxLayout( this, BoxLayout.LINE_AXIS ) );
			this.setBorder( BorderFactory.createEmptyBorder( 3, 3, 3, 3 ) );
			
			this.add( new JLabel(name + ":") );
			this.add(Box.createHorizontalGlue());
			
			the_input_field = new JTextField(the_input.getText());
			the_input_field.setFont( the_input_field.getFont().deriveFont( Font.PLAIN, 20.0f ) );
			the_input_field.setHorizontalAlignment( JTextField.RIGHT );
			okBorder = the_input_field.getBorder();
			this.add(the_input_field);
			
			var height = 25;
			var width = 250;
			the_input_field.setPreferredSize( new Dimension( width, height ) );
			the_input_field.setMaximumSize( new Dimension( width, height ) );
			
			var decimal_icon = new ImageIcon(ClassLoader.getSystemResource( "decimal_icon.png" ));
			var fractal_icon = new ImageIcon(ClassLoader.getSystemResource( "fraction_icon.png" ));
			var hms_icon = new ImageIcon(ClassLoader.getSystemResource( "hms_icon.png" ));
			
			decimal_switch = new JToggleButton( decimal_icon );
			fractal_switch = new JToggleButton( fractal_icon );
			hms_switch = new JToggleButton( hms_icon );
			
			decimal_switch.setSize( 17, 17 );
			fractal_switch.setSize( 17, 17 );
			hms_switch.setSize( 17, 17 );
			
			decimal_switch.setToolTipText( txt.getString( "decimal_tooltip" ) );
			fractal_switch.setToolTipText( txt.getString( "fraction_tooltip" ) );
			hms_switch.setToolTipText( txt.getString( "hms_tooltip" ) );
			
			this.add( decimal_switch );
			this.add( fractal_switch );
			this.add( hms_switch );
			
			var switch_group = new ButtonGroup();
			switch_group.add( decimal_switch );
			switch_group.add( fractal_switch );
			switch_group.add( hms_switch );
			switch( input.getNumberFormat() ) {
				case DECIMAL -> decimal_switch.doClick();
				case FRACTAL -> fractal_switch.doClick();
				case DAYS_AND_TIME -> hms_switch.doClick();
			}
			
			decimal_switch.addActionListener( this );
			decimal_switch.setActionCommand( "DECIMAL" );
			fractal_switch.addActionListener( this );
			fractal_switch.setActionCommand( "FRACTION" );
			hms_switch.addActionListener( this );
			hms_switch.setActionCommand( "HMS" );
			
			the_input_field.addKeyListener( this );
		}
		
		void switchToDarkTheme() {
			var decimal_icon = new ImageIcon(ClassLoader.getSystemResource( "decimal_icon_light.png" ));
			var fractal_icon = new ImageIcon(ClassLoader.getSystemResource( "fraction_icon_light.png" ));
			
			decimal_switch.setIcon( decimal_icon );
			fractal_switch.setIcon( fractal_icon );
		}
		
		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			var command = actionEvent.getActionCommand();
			switch( command ) {
				case "DECIMAL" -> the_input.switchToFormat( YearMonthLengthInput.LengthInput.NumberFormat.DECIMAL );
				case "FRACTION" -> the_input.switchToFormat( YearMonthLengthInput.LengthInput.NumberFormat.FRACTAL );
				case "HMS" -> the_input.switchToFormat( YearMonthLengthInput.LengthInput.NumberFormat.DAYS_AND_TIME );
			}
			the_input_field.setText( the_input.getText() );
		}
		
		@Override
		public void keyTyped(KeyEvent keyEvent) {
			//do nothing
		}
		
		@Override
		public void keyPressed(KeyEvent keyEvent) {
			//do noting
		}
		
		@Override
		public void keyReleased(KeyEvent keyEvent) {
			the_input.setText( the_input_field.getText() );
			if( the_input.inErrorState() ) {
				the_input_field.setBorder( BorderFactory.createLineBorder( errorColor ) );
			} else {
				the_input_field.setBorder( okBorder );
			}
			errorChecker.check(  );
		}
	}
}
