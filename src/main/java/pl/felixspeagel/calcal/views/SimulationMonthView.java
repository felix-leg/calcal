package pl.felixspeagel.calcal.views;

import pl.felixspeagel.calcal.calculators.simulation.DayInMonth;
import pl.felixspeagel.calcal.calculators.simulation.Simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

public class SimulationMonthView extends JPanel {
	
	private final AtomicReference<Simulation> simulation_ref;
	private final Color normal_month_color;
	private final Color epagomenal_month_color;
	private final Color leap_month_color;
	private final JPanel days_panel;
	private final JTextField year_number;
	private final JLabel month_name;
	private final JPanel month_header;
	
	public SimulationMonthView(AtomicReference<Simulation> a_ref) {
		super();
		simulation_ref = a_ref;
		normal_month_color = Color.decode( "#ca1d1d" );
		epagomenal_month_color = Color.decode( "#3934b5" );
		leap_month_color = Color.decode( "#39b534" );
		this.setLayout( new BorderLayout() );
		
		//month header
		month_header = new JPanel(new GridBagLayout());
		month_header.setBackground( normal_month_color );
		var c = new GridBagConstraints();
		this.add( month_header, BorderLayout.NORTH );
		month_header.setBorder( BorderFactory.createEmptyBorder(10,10,10,10) );
		
		var arrow_left = new ImageIcon(ClassLoader.getSystemResource( "arrow_left.png" ));
		var arrow_left_stop = new ImageIcon(ClassLoader.getSystemResource( "arrow_left_stop.png" ));
		var arrow_right = new ImageIcon(ClassLoader.getSystemResource( "arrow_right.png" ));
		var arrow_right_stop = new ImageIcon(ClassLoader.getSystemResource( "arrow_right_stop.png" ));
		
		var previous_year = new JButton(arrow_left);
		var next_year = new JButton(arrow_right);
		var first_month = new JButton(arrow_left_stop);
		var previous_month = new JButton(arrow_left);
		var next_month = new JButton(arrow_right);
		var last_month = new JButton(arrow_right_stop);
		
		year_number = new JTextField("2022");
		year_number.setBackground( null );
		year_number.setFont( year_number.getFont().deriveFont( Font.PLAIN, 18 ) );
		year_number.setBorder( BorderFactory.createEmptyBorder(0,20,3,20) );
		year_number.setHorizontalAlignment( JTextField.CENTER );
		month_name = new JLabel("MONTH");
		month_name.setFont( month_name.getFont().deriveFont( Font.BOLD, 20 ) );
		month_name.setBorder( BorderFactory.createEmptyBorder(3,25,3,25) );
		month_name.setHorizontalTextPosition( JLabel.CENTER );
		
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_END;
		
		c.gridx = 0; c.gridy = 1;
		month_header.add( first_month, c );
		c.gridx++;
		month_header.add( previous_month, c );
		c.gridy = 0;
		month_header.add( previous_year, c );
		
		c.anchor = GridBagConstraints.LINE_START;
		
		c.gridy = 0;
		c.gridx = 3;
		month_header.add( next_year, c );
		c.gridy++;
		month_header.add( next_month, c );
		c.gridx++;
		month_header.add( last_month, c );
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 2;
		
		c.gridy = 0;
		month_header.add( year_number, c );
		c.gridy++;
		month_header.add( month_name, c );
		
		//month days panel
		days_panel = new JPanel();
		this.add( days_panel, BorderLayout.CENTER );
		
		previous_year.addActionListener( event -> {
			if( simulation_ref.get() != null ) {
				simulation_ref.get().gotoPreviousYear();
				update_view();
			}
		} );
		next_year.addActionListener( event -> {
			if( simulation_ref.get() != null ) {
				simulation_ref.get().gotoNextYear();
				update_view();
			}
		} );
		first_month.addActionListener( event -> {
			if( simulation_ref.get() != null ) {
				simulation_ref.get().gotoFirstMonth();
				update_view();
			}
		} );
		previous_month.addActionListener( event -> {
			if( simulation_ref.get() != null ) {
				simulation_ref.get().gotoPreviousMonth();
				update_view();
			}
		} );
		next_month.addActionListener( event -> {
			if( simulation_ref.get() != null ) {
				simulation_ref.get().gotoNextMonth();
				update_view();
			}
		} );
		last_month.addActionListener( event -> {
			if( simulation_ref.get() != null ) {
				simulation_ref.get().gotoLastMonth();
				update_view();
			}
		} );
		year_number.addKeyListener( new KeyListener() {
			@Override
			public void keyTyped(KeyEvent keyEvent) {
			
			}
			
			@Override
			public void keyPressed(KeyEvent keyEvent) {
			
			}
			
			@Override
			public void keyReleased(KeyEvent keyEvent) {
				if( keyEvent.getKeyChar() == '\n' ) {
					if( simulation_ref.get() != null ) {
						System.out.println("Number: "+year_number.getText());
						try {
							var new_value = BigInteger.valueOf( Long.parseLong( year_number.getText() ) );
							simulation_ref.get().setEraYear( new_value );
							update_view();
						}catch( NumberFormatException e ) {
							clearView();
						}
					}
				}
			}
		} );
	}
	
	private void clearView() {
		days_panel.removeAll();
		days_panel.revalidate();
		days_panel.repaint();
	}
	
	public void update_view() {
		if( simulation_ref.get() == null ) return;
		
		int week_length = simulation_ref.get().getWeekLength();
		days_panel.setLayout( new GridLayout(0,week_length) );
		days_panel.removeAll();
		
		var daysInMonth = simulation_ref.get().getListOfDaysInTheMonth();
		for(var oneWeek : daysInMonth) {
			for(var oneDay : oneWeek) {
				if( oneDay == null ) {
					days_panel.add(new JLabel(""));
				} else {
					days_panel.add(new DayBox( oneDay ));
				}
			}
		}
		
		month_name.setText( simulation_ref.get().getMonthName() );
		year_number.setText( simulation_ref.get().getEraYear().toString() );
		switch( simulation_ref.get().getMonthFeature() ) {
			case NONE -> month_header.setBackground( normal_month_color );
			case EPAGOMENAL -> month_header.setBackground( epagomenal_month_color );
			case LEAP -> month_header.setBackground( leap_month_color );
		}
		month_header.repaint();
		
		days_panel.revalidate();
		days_panel.repaint();
	}
	
	private static class DayBox extends JPanel {
		
		public DayBox(DayInMonth day) {
			super(new GridBagLayout());
			this.setBackground( Color.WHITE );
			this.setBorder( BorderFactory.createEmptyBorder(5,5,5,5) );
			
			var day_number = new JLabel(String.valueOf( day.number() ));
			day_number.setFont( new Font( "Serif", Font.BOLD, 18 ) );
			day_number.setForeground( Color.BLACK );
			
			var c = new GridBagConstraints();
			c.anchor = GridBagConstraints.CENTER;
			c.gridx = c.gridy = 0;
			c.gridwidth = 2;
			this.add( day_number, c );
			
			var moon_phase = new JLabel("");
			c.gridx = 0; c.gridy = 1;
			c.gridwidth = 1;
			c.anchor = GridBagConstraints.LINE_START;
			this.add( moon_phase, c );
			if( day.moon_phase() != null) {
				var icon_path = "";
				switch( day.moon_phase() ) {
					case NEW_MOON -> icon_path = "new_moon";
					case FULL_MOON -> icon_path = "full_moon";
					case LAST_QUARTER -> icon_path = "last_quarter";
					case FIRST_QUARTER -> icon_path = "first_quarter";
					case WANING_GIBBOUS -> icon_path = "waning_gibbous";
					case WAXING_GIBBOUS -> icon_path = "waxing_gibbous";
					case WANING_CRESCENT -> icon_path = "waning_crescent";
					case WAXING_CRESCENT -> icon_path = "waxing_crescent";
				}
				var icon = new ImageIcon(ClassLoader.getSystemResource( icon_path + ".png" ));
				moon_phase.setIcon( icon );
			}
			
			var season = new JLabel("");
			c.gridx = 1;
			c.anchor = GridBagConstraints.LINE_END;
			this.add( season, c );
			if( day.season() != null ) {
				var icon_path = "";
				switch( day.season() ) {
					case WINTER -> icon_path = "winter";
					case FALL -> icon_path = "fall";
					case SPRING -> icon_path = "spring";
					case SUMMER -> icon_path = "summer";
				}
				var icon = new ImageIcon(ClassLoader.getSystemResource( icon_path + ".png" ));
				season.setIcon( icon );
			}
		}
		
	}
	
}
