package pl.felixspeagel.calcal.views;

import pl.felixspeagel.calcal.file.template.SimulationWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.math.BigInteger;
import java.util.Locale;
import java.util.ResourceBundle;

public class SimulationPrinterView extends JPanel implements KeyListener {
	
	private final JTextField title_edit;
	private final JButton export_button;
	private final SimulationWriter the_writer;
	private final ResourceBundle txt;
	private final JSpinner range_from;
	private final JSpinner range_to;
	private final ColorSetter normal_month_color;
	private final ColorSetter leap_month_color;
	private final ColorSetter epagomenal_month_color;
	
	public SimulationPrinterView(SimulationWriter a_writer) {
		super(new BorderLayout(5, 10) );
		the_writer = a_writer;
		txt = ResourceBundle.getBundle( "simulation_printer_view", Locale.getDefault() );
		
		//range input
		var range_panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 10));
		this.add(range_panel, BorderLayout.NORTH);
		
		var range_label = new JLabel( txt.getString( "range_label" ) + ": ");
		range_panel.add( range_label );
		range_from = new JSpinner();
		range_from.setEditor( new JSpinner.NumberEditor( range_from ) );
		range_panel.add( range_from );
		var range_sep = new JLabel(" " + txt.getString( "range_sep" ) + " ");
		range_panel.add( range_sep );
		range_to = new JSpinner();
		range_to.setEditor( new JSpinner.NumberEditor( range_to ) );
		range_panel.add( range_to );
		
		((JSpinner.NumberEditor)range_from.getEditor()).getTextField().setColumns( 5 );
		((JSpinner.NumberEditor)range_to.getEditor()).getTextField().setColumns( 5 );
		
		//colors panel
		var colors_panel = new JPanel(new GridLayout(3,1));
		this.add( colors_panel, BorderLayout.CENTER );
		
		normal_month_color = new ColorSetter( txt, "normal_month", Color.decode( "#ca1d1d" ), Color.BLACK);
		leap_month_color = new ColorSetter( txt, "leap_month", Color.decode( "#39b534" ), Color.BLACK);
		epagomenal_month_color = new ColorSetter( txt, "epagomenal_month", Color.decode( "#3934b5" ), Color.BLACK);
		
		colors_panel.add( normal_month_color );
		colors_panel.add( leap_month_color );
		colors_panel.add( epagomenal_month_color );
		
		//bottom panel
		var bottom_panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 10));
		this.add( bottom_panel, BorderLayout.SOUTH );
		
		var title_label = new JLabel( txt.getString( "title_label" ) + ": ");
		bottom_panel.add( title_label );
		title_edit = new JTextField();
		title_edit.setColumns( 20 );
		bottom_panel.add( title_edit );
		export_button = new JButton( txt.getString( "export_button" ));
		bottom_panel.add( export_button );
		
		export_button.setEnabled( false );
		
		//events
		title_edit.addKeyListener( this );
		export_button.addActionListener( event -> this.doExport() );
	}
	
	private void doExport() {
		Container top = this;
		File to_save = null;
		do{
			top = top.getParent();
		}while( ! ( top instanceof Frame ) );
		var fileChooser = new FileDialog( (Frame)top, txt.getString( "export_dialog_title" ) );
		fileChooser.setMode( FileDialog.SAVE );
		fileChooser.setMultipleMode( false );
		fileChooser.setModal( true );
		fileChooser.setFilenameFilter( (file, s) -> s.endsWith( ".html" ) );
		
		fileChooser.setVisible( true );
		
		var files = fileChooser.getFiles();
		if( files.length != 1 ) return;
		else {
			if( ! files[0].getName().endsWith( ".html" ) ) {
				to_save = new File( files[0].getAbsolutePath() + ".html" );
			} else {
				to_save = files[0];
			}
		}
		
		the_writer.settings.title = title_edit.getText().strip();
		the_writer.settings.year_start = BigInteger.valueOf( (Integer) range_from.getValue() );
		the_writer.settings.year_end = BigInteger.valueOf( (Integer) range_to.getValue() );
		if( the_writer.settings.year_end.compareTo( the_writer.settings.year_start ) < 0 ) {
			var temp = the_writer.settings.year_start;
			the_writer.settings.year_start = the_writer.settings.year_end;
			the_writer.settings.year_end = temp;
		}
		the_writer.settings.normal_bg = normal_month_color.getSelectedBackground();
		the_writer.settings.normal_fg = normal_month_color.getSelectedForeground();
		the_writer.settings.leap_bg = leap_month_color.getSelectedBackground();
		the_writer.settings.leap_fg = leap_month_color.getSelectedForeground();
		the_writer.settings.epi_bg = epagomenal_month_color.getSelectedBackground();
		the_writer.settings.epi_fg = epagomenal_month_color.getSelectedForeground();
		
		the_writer.writeFile( to_save );
	}
	
	@Override
	public void keyTyped(KeyEvent keyEvent) {
	
	}
	
	@Override
	public void keyPressed(KeyEvent keyEvent) {
	
	}
	
	@Override
	public void keyReleased(KeyEvent keyEvent) {
		export_button.setEnabled( ! title_edit.getText().isBlank() );
	}
	
	private static class ColorSetter extends JPanel {
		
		private final ResourceBundle txt;
		private final JLabel color_label;
		private final JPanel color_panel;
		private Color background_color;
		private Color foreground_color;
		
		public ColorSetter(ResourceBundle bundle, String title_id, Color background, Color foreground) {
			super(new GridBagLayout());
			txt = bundle;
			
			this.setBorder( BorderFactory.createTitledBorder( txt.getString( title_id ) ) );
			
			var c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.anchor = GridBagConstraints.CENTER;
			c.weightx = 1.0/3.0;
			
			JButton background_button = new JButton( txt.getString( "background_button" ) );
			JButton foreground_button = new JButton( txt.getString( "foreground_button" ) );
			this.add( background_button, c );
			c.gridy++;
			this.add( foreground_button, c );
			
			color_label = new JLabel(txt.getString( "example_text" ));
			color_panel = new JPanel(new BorderLayout());
			color_panel.add( color_label, BorderLayout.CENTER );
			color_panel.setBorder( BorderFactory.createEmptyBorder(20,20,20,20) );
			color_panel.setBackground( background );
			color_label.setForeground( foreground );
			color_label.setFont( color_label.getFont().deriveFont( Font.BOLD, 24 ) );
			
			c = new GridBagConstraints();
			c.gridx = 2;
			c.gridwidth = 2;
			c.fill = GridBagConstraints.BOTH;
			c.anchor = GridBagConstraints.CENTER;
			c.weightx = 1.0;
			this.add( color_panel, c );
			
			background_color = background;
			foreground_color = foreground;
			background_button.addActionListener( event -> this.showBackgroundDialog() );
			foreground_button.addActionListener( event -> this.showForegroundDialog() );
		}
		
		public Color getSelectedBackground() {
			return background_color;
		}
		
		public Color getSelectedForeground() {
			return foreground_color;
		}
		
		private void showBackgroundDialog() {
			var new_color = JColorChooser.showDialog(
					this,
					txt.getString("select_background"),
					background_color
			);
			
			if( new_color != null ) {
				background_color = new_color;
				color_panel.setBackground( new_color );
			}
		}
		
		private void showForegroundDialog() {
			var new_color = JColorChooser.showDialog(
					this,
					txt.getString("select_foreground"),
					foreground_color
			);
			
			if( new_color != null ) {
				foreground_color = new_color;
				color_label.setForeground( new_color );
			}
		}
		
	}
	
}
