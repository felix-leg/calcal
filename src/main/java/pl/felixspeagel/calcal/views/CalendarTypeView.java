package pl.felixspeagel.calcal.views;

import pl.felixspeagel.calcal.calculators.*;
import pl.felixspeagel.calcal.controllers.CalendarTypeInput;
import pl.felixspeagel.calcal.controllers.Refreshable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class CalendarTypeView extends JPanel {
	
	private final CalendarTypeInput controller;
	private final ResourceBundle txt;
	
	private final JLabel explain_text;
	private final JPanel solution_panel;
	private final JComboBox<SolutionFormatter> solutions_combo;
	
	private final ComboBoxModel<SolutionFormatter> model_sun;
	private final ComboBoxModel<SolutionFormatter> model_sun_moon;
	private final ComboBoxModel<SolutionFormatter> model_moon;
	private final JScrollPane solution_wrapper;
	
	private record SolutionFormatter(
			CalendarTypeInput.Solution solution,
			String description
			) {
		@Override
		public String toString() {
			return description;
		}
	}
	
	public final ArrayList<Refreshable> refreshList;
	private void refreshGUI(boolean full) {
		for(var aRefreshable : refreshList) {
			aRefreshable.refresh(full);
		}
		if(full) {
			var selection = controller.getSelection();
			switch_body( selection.first );
			for(int i=0; i<solutions_combo.getItemCount(); i++) {
				var item =solutions_combo.getItemAt( i );
				if( item == null ) {
					continue;
				}
				if( item.solution() == null ) {
					continue;
				}
				if( item.solution().equals( selection.second ) ) {
					solutions_combo.setSelectedIndex( i );
					break;
				}
			}
		}
		updateContent();
	}
	
	public CalendarTypeView(CalendarTypeInput a_controller) {
		super();
		controller = a_controller;
		txt = ResourceBundle.getBundle( "calendar_type_input", Locale.getDefault() );
		refreshList = new ArrayList<>();
		
		this.setLayout( new BoxLayout( this, BoxLayout.PAGE_AXIS ) );
		
		//body chooser
		var bodyTypePanel = new JPanel(new GridLayout(1,3));
		this.add( bodyTypePanel );
		
		var sun_icon = new ImageIcon(ClassLoader.getSystemResource( "sun.png" ));
		var moon_icon = new ImageIcon(ClassLoader.getSystemResource( "moon.png" ));
		var sun_moon_icon = new ImageIcon(ClassLoader.getSystemResource( "sun_moon.png" ));
		
		var big_font = new Font( "Sans-serif", Font.BOLD, 28 );
		var sun_button = new JToggleButton(txt.getString( "solar_button" ), sun_icon);
		sun_button.setVerticalTextPosition( JButton.BOTTOM );
		sun_button.setHorizontalTextPosition( JButton.CENTER );
		sun_button.setFont( big_font );
		var moon_button = new JToggleButton(txt.getString( "lunar_button" ), moon_icon);
		moon_button.setVerticalTextPosition( JButton.BOTTOM );
		moon_button.setHorizontalTextPosition( JButton.CENTER );
		moon_button.setFont( big_font );
		var sun_moon_button = new JToggleButton(txt.getString( "lunisolar_button" ), sun_moon_icon);
		sun_moon_button.setVerticalTextPosition( JButton.BOTTOM );
		sun_moon_button.setHorizontalTextPosition( JButton.CENTER );
		sun_moon_button.setFont( big_font );
		
		bodyTypePanel.add( sun_button );
		bodyTypePanel.add( sun_moon_button );
		bodyTypePanel.add( moon_button );
		var body_group = new ButtonGroup();
		body_group.add( sun_button );
		body_group.add( sun_moon_button );
		body_group.add( moon_button );
		
		// explain text
		this.add(Box.createVerticalStrut( 45 ));
		
		explain_text = new JLabel(txt.getString( "intro_body_explain_text" ));
		explain_text.setAlignmentX( 0.5f );
		explain_text.setHorizontalTextPosition( JLabel.CENTER );
		explain_text.setFont( explain_text.getFont().deriveFont( Font.PLAIN, 20 ) );
		this.add( explain_text );
		
		//solution chooser
		this.add( Box.createVerticalStrut( 45 ) );
		
		solutions_combo = new JComboBox<>();
		solutions_combo.setFont( solutions_combo.getFont().deriveFont( Font.PLAIN, 14 ) );
		
		solutions_combo.addItem( new SolutionFormatter(
				CalendarTypeInput.Solution.GREGORIAN,
				txt.getString("combo_gregorian_description")
		) );
		solutions_combo.addItem( new SolutionFormatter(
				CalendarTypeInput.Solution.EGYPTIAN,
				txt.getString("combo_egyptian_description")
		) );
		model_sun = solutions_combo.getModel();
		solutions_combo.setModel( new DefaultComboBoxModel<>() );
		
		solutions_combo.addItem( new SolutionFormatter(
				CalendarTypeInput.Solution.METON,
				txt.getString("combo_meton_description")
		) );
		model_sun_moon = solutions_combo.getModel();
		solutions_combo.setModel( new DefaultComboBoxModel<>() );
		
		solutions_combo.addItem( new SolutionFormatter(
				CalendarTypeInput.Solution.PURE_LUNAR,
				txt.getString("combo_pure_lunar_description")
		) );
		solutions_combo.addItem( new SolutionFormatter(
				CalendarTypeInput.Solution.ISLAMIC,
				txt.getString( "combo_islamic_description" )
		) );
		model_moon = solutions_combo.getModel();
		solutions_combo.setModel( new DefaultComboBoxModel<>() );
		
		solutions_combo.addItem( new SolutionFormatter( null, txt.getString( "empty_solution" ) ) );
		
		this.add( solutions_combo );
		
		//solution description panel
		solution_panel = new JPanel();
		solution_panel.setLayout( new BoxLayout( solution_panel, BoxLayout.PAGE_AXIS ) );
		solution_panel.add(new JLabel(txt.getString( "choose_solution_incentive" )));
		
		solution_wrapper = new JScrollPane(solution_panel);
		solution_wrapper.setBorder( BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(15, 5, 0, 5),
				BorderFactory.createCompoundBorder(
						BorderFactory.createLoweredBevelBorder(),
						BorderFactory.createEmptyBorder(10,10,10,10)
				)
		) );
		solution_wrapper.setAlignmentX( 0.5f );
		solution_wrapper.setMaximumSize( new Dimension(Integer.MAX_VALUE, 200) );
		solution_wrapper.setPreferredSize( new Dimension(300, 200) );
		solution_wrapper.setMinimumSize( new Dimension(100, 100) );
		this.add( solution_wrapper );
		
		//set unnecessary elements invisible
		solutions_combo.setVisible( false );
		solution_wrapper.setVisible( false );
		
		//events
		sun_button.addActionListener( event -> this.switch_body( CalendarTypeInput.BodyTracked.SUN ) );
		sun_moon_button.addActionListener( event -> this.switch_body( CalendarTypeInput.BodyTracked.SUN_AND_MOON ) );
		moon_button.addActionListener( event -> this.switch_body( CalendarTypeInput.BodyTracked.MOON ) );
		solutions_combo.addActionListener( event -> this.switch_solution() );
		controller.refreshList.add( this::refreshGUI );
		
		var selection = controller.getSelection();
		if( selection.first != null ) {
			switch( selection.first ) {
				case SUN -> sun_button.doClick();
				case SUN_AND_MOON -> sun_moon_button.doClick();
				case MOON -> moon_button.doClick();
			}
		}
		if( selection.second != null) {
			switch( selection.second ) {
				case GREGORIAN, METON, PURE_LUNAR -> solutions_combo.setSelectedIndex( 0 );
				case EGYPTIAN, ISLAMIC -> solutions_combo.setSelectedIndex( 1 );
			}
		}
	}
	
	public void updateContent() {
		var solution_object = controller.getSolutionObject();
		solution_panel.removeAll();
		
		if( solution_object != null ) {
			
			if( solution_object instanceof MetonicCycle meton ) {
				displayMetonicCycle( meton );
			} else if( solution_object instanceof Gregorian gregorian ) {
				displayGregorian( gregorian );
			} else if( solution_object instanceof Egyptian egyptian ) {
				displayEgyptian( egyptian );
			} else if( solution_object instanceof PureLunar pure ) {
				displayLunar( pure );
			} else if( solution_object instanceof Islamic islamic ) {
				displayIslamic( islamic );
			}
			
			solution_wrapper.setVisible( true );
		} else {
			solution_wrapper.setVisible( false );
		}
		solution_panel.revalidate();
		solution_panel.repaint();
		
		this.revalidate();
		this.repaint();
	}
	
	private void switch_body(CalendarTypeInput.BodyTracked body) {
		if( body == null ) {
			return;
		}
		
		controller.switchBody( body );
		explain_text.setText( txt.getString( "select_solution_explain_text" ) );
		solutions_combo.setVisible( true );
		solution_panel.setVisible( true );
		
		switch( body ) {
			case SUN -> solutions_combo.setModel( model_sun );
			case SUN_AND_MOON -> solutions_combo.setModel( model_sun_moon );
			case MOON -> solutions_combo.setModel( model_moon );
		}
		solutions_combo.setSelectedIndex( 0 );
		
		this.revalidate();
		this.repaint();
		refreshGUI(false);
	}
	
	private void switch_solution() {
		var selected = (SolutionFormatter) solutions_combo.getSelectedItem();
		if( selected == null ) {
			controller.chooseSolution( null );
			return;
		}
		if( selected.solution() == null ) {
			controller.chooseSolution( null );
			return;
		}
		
		controller.chooseSolution( selected.solution() );
	
		refreshGUI(false);
	}
	
	private JLabel plainTextLabel(String text) {
		var label = new JLabel(text);
		label.setFont( label.getFont().deriveFont( Font.PLAIN ) );
		label.setAlignmentX( 0.5f );
		return label;
	}
	
	private void displayMetonicCycle(MetonicCycle meton) {
		var info_text = "<html>" + txt.getString( "metonic_cycle_description" ) + "</html>";
		info_text = info_text.replace( "{{months}}", String.valueOf(meton.months) ).
				replace( "{{years}}", String.valueOf( meton.years ) );
		solution_panel.add(plainTextLabel(info_text));
		
		var years_table = new JPanel();
		years_table.setLayout( new GridLayout(0,8) );
		solution_panel.add(years_table);
		
		years_table.setBorder( BorderFactory.createEmptyBorder(4,4,4,4) );
		for(int year=1; year<=meton.years; year++) {
			var year_label = plainTextLabel("");
			if( meton.cycle[year-1] == IntercalationType.LEAP ) {
				year_label.setText( "<html><b>" + year + "</b></html>" );
			} else {
				year_label.setText( String.valueOf( year ) );
			}
			years_table.add(year_label);
		}
	}
	
	private void displayGregorian(Gregorian gregorian) {
		JLabel info_label;
		solution_panel.add(plainTextLabel(txt.getString( "gregorian_description" )));
		
		if( gregorian.unable_to_compute ) {
			info_label = new JLabel(txt.getString( "gregorian_fail" ));
			info_label.setForeground( Color.RED );
			solution_panel.add(info_label);
			return;
		}
		
		if( gregorian.rules.length == 0 ) {
			info_label = new JLabel(txt.getString( "gregorian_no_rules" ));
			solution_panel.add(info_label);
		} else {
			solution_panel.add(Box.createVerticalStrut( 5 ));
			
			final var leap_text = txt.getString( "gregorian_leap_rule" );
			final var normal_text = txt.getString( "gregorian_normal_rule" );
			
			for(int i=0; i<gregorian.rules.length; i++) {
				String text;
				
				if( gregorian.rules[i].is_leap() == IntercalationType.LEAP ) {
					text = leap_text.replace( "{{div}}", String.valueOf( gregorian.rules[i].each_year() ) );
				} else {
					text = normal_text.replace( "{{div}}", String.valueOf( gregorian.rules[i].each_year() ) );
				}
				
				if( i < gregorian.rules.length - 1) {
					text = text + ",";
				} else {
					text = text + ".";
				}
				
				solution_panel.add(plainTextLabel("<html>" + text + "</html>"));
			}
		}
	}
	
	private void displayEgyptian(Egyptian egyptian) {
		var summary_text = txt.getString( "egyptian_summary" );
		summary_text = summary_text
				               .replace( "{{days_in_month}}", egyptian.days_in_month.toString() )
				               .replace( "{{days_in_year}}", egyptian.days_in_year.toString() )
				               .replace( "{{epagomenal_days}}", egyptian.epagomenal_days.toString() )
				               .replace( "{{every_year}}", egyptian.every_year.toString() )
				               .replace( "{{add_leap_days}}", egyptian.add_leap_days.toString() )
				;
		solution_panel.add( plainTextLabel( "<html>" + summary_text + "</html>" ) );
	}
	
	private void displayLunar(PureLunar pure) {
		JLabel info_label;
		var text = txt.getString( "pure_lunar_description" );
		text = text.replace( "{{days_in_year}}", pure.lunar_year.toString() );
		solution_panel.add(plainTextLabel("<html>" + text + "</html>"));
		
		if( pure.unable_to_compute ) {
			info_label = new JLabel(txt.getString( "pure_lunar_fail" ));
			info_label.setForeground( Color.RED );
			solution_panel.add(info_label);
			return;
		}
		
		if( pure.rules.length == 0 ) {
			info_label = new JLabel(txt.getString( "pure_lunar_no_rules" ));
			solution_panel.add(info_label);
		} else {
			solution_panel.add(Box.createVerticalStrut( 5 ));
			
			final var leap_text = txt.getString( "pure_lunar_leap_rule" );
			final var normal_text = txt.getString( "pure_lunar_normal_rule" );
			
			for(int i=0; i<pure.rules.length; i++) {
				if( pure.rules[i].is_leap() == IntercalationType.LEAP ) {
					text = leap_text.replace( "{{div}}", String.valueOf( pure.rules[i].each_year() ) );
				} else {
					text = normal_text.replace( "{{div}}", String.valueOf( pure.rules[i].each_year() ) );
				}
				
				if( i < pure.rules.length - 1) {
					text = text + ",";
				} else {
					text = text + ".";
				}
				
				solution_panel.add(plainTextLabel("<html>" + text + "</html>"));
			}
		}
	}
	
	private void displayIslamic(Islamic islamic) {
		var text = txt.getString( "islamic_summary" );
		text = text
				.replace( "{{cycle_length}}", String.valueOf( islamic.cycle.length ) )
				.replace( "{{days_in_year}}", islamic.days_in_year.toString() )
				.replace( "{{days_to_add}}", islamic.days_to_add.toString() )
		;
		solution_panel.add( plainTextLabel( "<html>" + text + "</html>" ) );
		
		var years_table = new JPanel();// 29 191/360
		years_table.setLayout( new GridLayout(0,8) );
		solution_panel.add(years_table);
		
		years_table.setBorder( BorderFactory.createEmptyBorder(4,4,4,4) );
		for(int year=1; year<=islamic.cycle.length; year++) {
			var year_label = plainTextLabel("");
			if( islamic.cycle[year-1] == IntercalationType.LEAP ) {
				year_label.setText( "<html><b>" + year + "</b></html>" );
			} else {
				year_label.setText( String.valueOf( year ) );
			}
			years_table.add(year_label);
		}
	}
	
}
