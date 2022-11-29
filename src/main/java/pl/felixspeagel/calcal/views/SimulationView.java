package pl.felixspeagel.calcal.views;

import pl.felixspeagel.calcal.controllers.Simulation;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class SimulationView extends JDialog {
	
	private final Simulation the_controller;
	private final JLabel title_label;
	private final CardLayout cards;
	private final JPanel central_panel;
	private final ResourceBundle txt;
	private final SimulationMonthView month_view;
	
	public SimulationView(Frame parent, String dialog_title, Simulation a_simulation) {
		super(parent, dialog_title, true);
		the_controller = a_simulation;
		txt = ResourceBundle.getBundle( "simulation_view", Locale.getDefault() );
		
		this.setLayout( new BorderLayout() );
		
		// the panel with a list of view buttons
		var leftPanel = new JPanel();
		this.add( leftPanel, BorderLayout.WEST );
		leftPanel.setLayout( new GridLayout(10,1) );
		leftPanel.setBorder(
				BorderFactory.createMatteBorder( 0, 0, 0, 2, Color.decode( "#404070" ) )
		);
		
		var simulation_settings_toggle = new JToggleButton( txt.getString( "settings_toggle" ));
		leftPanel.add(simulation_settings_toggle);
		var simulation_month_toggle = new JToggleButton( txt.getString( "month_view_toggle" ));
		leftPanel.add(simulation_month_toggle);
		JToggleButton simulation_printer_toggle = null;
		if( the_controller.writer != null ) {
			simulation_printer_toggle = new JToggleButton( txt.getString( "printer_toggle" ) );
			leftPanel.add( simulation_printer_toggle );
		}
		
		var toggle_group = new ButtonGroup();
		toggle_group.add( simulation_settings_toggle );
		toggle_group.add( simulation_month_toggle );
		if( simulation_printer_toggle != null )
			toggle_group.add( simulation_printer_toggle );
		
		//the panel with view title
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
		titlePanel.add( title_label );
		title_label.setFont( new Font("Sans-serif", Font.ITALIC, 48) );
		title_label.setHorizontalTextPosition( JLabel.LEADING );
		titlePanel.setBackground( Color.decode( "#e0e0ff" ) );
		titlePanel.setForeground( Color.BLACK );
		
		//central panel with the content
		central_panel = new JPanel();
		this.add( central_panel, BorderLayout.CENTER);
		
		central_panel.setLayout( new CardLayout() );
		cards = (CardLayout) central_panel.getLayout();
		
		var settings_view = new SimulationSettingsView( the_controller.settings );
		central_panel.add(wrap(settings_view), "settings");
		month_view = new SimulationMonthView( the_controller.simulation );
		central_panel.add(wrap( month_view ), "month_view");
		if( simulation_printer_toggle != null ) {
			var print_view = new SimulationPrinterView(the_controller.writer);
			central_panel.add( wrap(print_view), "print_view" );
		}
		
		//events
		simulation_settings_toggle.addActionListener( event -> this.showSettings() );
		simulation_month_toggle.addActionListener( event -> this.showMonthView() );
		if( simulation_printer_toggle != null )
			simulation_printer_toggle.addActionListener( event -> this.showPrinter() );
		
		simulation_settings_toggle.doClick();
		
		if( testForDarkTheme( simulation_month_toggle ) ) {
			settings_view.switchToDarkTheme();
		}
		
		this.pack();
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
	
	private void showSettings() {
		cards.show( central_panel, "settings" );
		title_label.setText( txt.getString( "settings_title" ) );
	}
	private void showMonthView() {
		the_controller.updateSimulationIfNecessary();
		month_view.update_view();
		
		cards.show( central_panel, "month_view" );
		title_label.setText( txt.getString( "month_view_title" ) );
	}
	private void showPrinter() {
		the_controller.updateSimulationIfNecessary();
		
		cards.show( central_panel, "print_view" );
		title_label.setText( txt.getString( "print_view_title" ) );
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
	
}
