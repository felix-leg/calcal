package pl.felixspeagel.calcal.views;

import pl.felixspeagel.calcal.controllers.SimulationSettings;
import pl.felixspeagel.calcal.math.HMSRecord;
import pl.felixspeagel.calcal.math.MixedFraction;
import pl.felixspeagel.calcal.math.NumberConverter;
import pl.felixspeagel.calcal.math.WrongNumberFormat;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigInteger;
import java.util.Locale;
import java.util.ResourceBundle;

public class SimulationSettingsView extends JPanel implements DarkThemeAware {
	
	private final SimulationSettings the_controller;
	private final LengthInput real_month_input;
	private final LengthInput real_year_input;
	private final LengthInput month_shift_input;
	private final LengthInput year_shift_input;
	
	public SimulationSettingsView(SimulationSettings a_controller) {
		super();
		the_controller = a_controller;
		var txt = ResourceBundle.getBundle( "simulation_settings_view", Locale.getDefault() );
		
		this.setLayout( new BoxLayout( this, BoxLayout.PAGE_AXIS ) );
		this.setBorder( BorderFactory.createEmptyBorder(20,20,20,20) );
		
		var introText = new JLabel("<html>" + txt.getString( "intro_text" ) + "</html>");
		introText.setFont( new Font( "Serif", Font.PLAIN, 14 ) );
		introText.setAlignmentX( 0.5f );
		this.add(introText);
		
		this.add(Box.createVerticalStrut( 20 ));
		
		//era input panel
		var era_panel = new JPanel(new GridBagLayout());
		var c = new GridBagConstraints();
		this.add(era_panel);
		
		era_panel.setBorder( BorderFactory.createTitledBorder( txt.getString( "era_input_title" ) ) );
		
		var era_year_label = new JLabel(txt.getString( "era_year_label" ) + ":");
		era_year_label.setAlignmentX( 0.0f );
		var era_year_edit = new JSpinner(new SpinnerNumberModel());
		((JSpinner.DefaultEditor) era_year_edit.getEditor()).getTextField().setColumns( 7 );
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = c.gridy = 0;
		c.gridwidth = c.gridheight = 1;
		c.ipadx = 15;
		era_panel.add( era_year_label, c );
		c.gridx = 1; c.gridwidth = 10;
		c.ipadx = 15;
		c.weightx = 1.0;
		era_panel.add( era_year_edit, c );
		
		era_year_edit.addChangeListener( event -> the_controller.setEraYear(
				BigInteger.valueOf( (Integer)era_year_edit.getValue() )
		) );
		
		if( the_controller.getShiftWeekLength() > 0 ) {
			var week_shift_label = new JLabel(txt.getString( "week_shift_label" ) + ":");
			week_shift_label.setAlignmentX( 0.0f );
			var week_edit = new JSlider(
					-the_controller.getShiftWeekLength(),
					the_controller.getShiftWeekLength(),
					0);
			
			c = new GridBagConstraints();
			c.gridy = 1;
			c.ipadx = 15;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.WEST;
			era_panel.add( week_shift_label, c );
			c.gridx = 1;
			c.gridwidth = 10;
			c.weightx = 1.0;
			era_panel.add( week_edit, c );
			
			week_edit.addChangeListener( event -> the_controller.setEraWeekDay( week_edit.getValue() ) );
		}
		
		this.add(Box.createVerticalStrut( 20 ));
		
		//reals input
		var realsText = new JLabel("<html>" + txt.getString( "reals_intro_text" ) + "</html>");
		realsText.setFont( new Font( "Serif", Font.PLAIN, 14 ) );
		realsText.setAlignmentX( 0.5f );
		this.add(realsText);
		
		this.add(Box.createVerticalStrut( 20 ));
		
		var real_values_panel = new JPanel(new GridBagLayout());
		this.add( real_values_panel );
		
		real_values_panel.setBorder( BorderFactory.createTitledBorder( txt.getString( "reals_input_title" ) ) );
		
		var reals_enabled = new JCheckBox(txt.getString( "reals_enabled" ));
		var real_month_label = new JLabel(txt.getString( "real_month_label" ) + ":");
		var real_year_label = new JLabel(txt.getString( "real_year_label" ) + ":");
		var month_shift_label = new JLabel(txt.getString( "month_shift_label" ) + ":");
		var year_shift_label = new JLabel(txt.getString( "year_shift_label" ) + ":");
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_START;
		real_values_panel.add( reals_enabled, c );
		c.gridy = 1;
		real_values_panel.add( real_month_label, c );
		c.gridy = 2;
		real_values_panel.add( real_year_label, c );
		c.gridy = 3;
		real_values_panel.add( month_shift_label, c );
		c.gridy = 4;
		real_values_panel.add( year_shift_label, c );
		
		real_month_input = new LengthInput(the_controller.getMonthLength(), the_controller.getDayLength(), true);
		real_year_input = new LengthInput(the_controller.getYearLength(), the_controller.getDayLength(), true);
		month_shift_input = new LengthInput( MixedFraction.ZERO, the_controller.getDayLength(), false);
		year_shift_input = new LengthInput( MixedFraction.ZERO, the_controller.getDayLength(), false);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = 1;
		real_values_panel.add( real_month_input, c );
		c.gridy = 2;
		real_values_panel.add( real_year_input, c );
		c.gridy = 3;
		real_values_panel.add( month_shift_input, c );
		c.gridy = 4;
		real_values_panel.add( year_shift_input, c );
		
		real_month_input.setEnabled( false );
		real_year_input.setEnabled( false );
		month_shift_input.setEnabled( false );
		year_shift_input.setEnabled( false );
		reals_enabled.addChangeListener( event -> {
			boolean sel = reals_enabled.isSelected();
			real_month_input.setEnabled( sel );
			real_year_input.setEnabled( sel );
			month_shift_input.setEnabled( sel );
			year_shift_input.setEnabled( sel );
			the_controller.setRealValuesTrackingFlag( sel );
			
			if( sel ) {
				the_controller.setRealMonthLength( real_month_input.the_value );
				the_controller.setRealYearLength( real_year_input.the_value );
				the_controller.setMonthShift( month_shift_input.the_value );
				the_controller.setYearShift( year_shift_input.the_value );
			}
		} );
		real_month_input.notify = the_controller::setRealMonthLength;
		real_year_input.notify = the_controller::setRealYearLength;
		month_shift_input.notify = the_controller::setMonthShift;
		year_shift_input.notify = the_controller::setYearShift;
	}
	
	@Override
	public void switchToDarkTheme() {
		real_month_input.switchToDarkTheme();
		real_year_input.switchToDarkTheme();
		month_shift_input.switchToDarkTheme();
		year_shift_input.switchToDarkTheme();
	}
	
	private interface UpdateNotify {
		void update(MixedFraction fraction);
	}
	
	private static class LengthInput extends JPanel implements KeyListener {
		
		private final JToggleButton decimal_button;
		private final JToggleButton fractal_button;
		private final JToggleButton hms_button;
		private final JTextField text_field;
		private final Border ok_border;
		private final boolean positive_only;
		
		private MixedFraction the_value;
		private final HMSRecord the_hms;
		
		public UpdateNotify notify;
		
		public LengthInput(MixedFraction value, HMSRecord hms, boolean onlyPositiveNumbers) {
			super(new FlowLayout());
			the_value = value;
			the_hms = hms;
			positive_only = onlyPositiveNumbers;
			notify = null;
			
			text_field = new JTextField();
			text_field.setColumns( 20 );
			text_field.setFont( text_field.getFont().deriveFont( Font.PLAIN, 20.0f ) );
			text_field.setHorizontalAlignment( JTextField.RIGHT );
			ok_border = text_field.getBorder();
			this.add( text_field );
			
			var decimal_icon = new ImageIcon(ClassLoader.getSystemResource( "decimal_icon.png" ));
			var fractal_icon = new ImageIcon(ClassLoader.getSystemResource( "fraction_icon.png" ));
			var hms_icon = new ImageIcon(ClassLoader.getSystemResource( "hms_icon.png" ));
			
			decimal_button = new JToggleButton(decimal_icon);
			this.add( decimal_button );
			fractal_button = new JToggleButton(fractal_icon);
			this.add( fractal_button );
			hms_button = new JToggleButton(hms_icon);
			this.add( hms_button );
			
			var button_group = new ButtonGroup();
			button_group.add( decimal_button );
			button_group.add( fractal_button );
			button_group.add( hms_button );
			decimal_button.doClick();
			
			decimal_button.addActionListener( event -> this.switch_to_decimal() );
			fractal_button.addActionListener( event -> this.switch_to_fractal() );
			hms_button.addActionListener( event -> this.switch_to_HMS() );
			text_field.addKeyListener( this );
			
			switch_to_decimal();
		}
		
		@Override
		public void setEnabled(boolean enabled) {
			text_field.setEnabled(enabled);
			decimal_button.setEnabled(enabled);
			fractal_button.setEnabled(enabled);
			hms_button.setEnabled(enabled);
		}
		
		private void switch_to_decimal() {
			var text = NumberConverter.FractionToDecimalText(the_value);
			text_field.setText( text );
		}
		private void switch_to_fractal() {
			var text = NumberConverter.FractionToText( the_value );
			text_field.setText( text );
		}
		private void switch_to_HMS() {
			var text = NumberConverter.FractionToDHMS_text( the_value, the_hms );
			text_field.setText( text );
		}
		
		public void switchToDarkTheme() {
			var decimal_icon = new ImageIcon(ClassLoader.getSystemResource( "decimal_icon_light.png" ));
			var fractal_icon = new ImageIcon(ClassLoader.getSystemResource( "fraction_icon_light.png" ));
			
			decimal_button.setIcon(decimal_icon);
			fractal_button.setIcon(fractal_icon);
		}
		
		@Override
		public void keyTyped(KeyEvent keyEvent) {
			//do nothing
		}
		
		@Override
		public void keyPressed(KeyEvent keyEvent) {
			//do nothing
		}
		
		@Override
		public void keyReleased(KeyEvent keyEvent) {
			try {
				MixedFraction value = MixedFraction.ZERO;
				if( decimal_button.isSelected() ) {
					value = NumberConverter.DecimalTextToFraction( text_field.getText() );
				} else if( fractal_button.isSelected() ) {
					value = NumberConverter.TextToFraction( text_field.getText() );
				} else if( hms_button.isSelected() ) {
					value = NumberConverter.DHMS_textToFraction( text_field.getText(), the_hms );
				}
				
				if( positive_only && value.compareTo( MixedFraction.ZERO ) <= 0 ) {
					throw new WrongNumberFormat();
				}
				the_value = value;
				
				text_field.setBorder( ok_border );
				if( notify != null ) {
					notify.update( the_value );
				}
			}catch( WrongNumberFormat e ) {
				text_field.setBorder( BorderFactory.createLineBorder( Color.RED ) );
			}
		}
	}
}
