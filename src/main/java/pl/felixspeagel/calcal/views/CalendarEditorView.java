package pl.felixspeagel.calcal.views;

import pl.felixspeagel.calcal.calculators.IntercalationType;
import pl.felixspeagel.calcal.controllers.CalendarEditor;
import pl.felixspeagel.calcal.controllers.Refreshable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class CalendarEditorView extends JPanel {
	private final CalendarEditor controller;
	
	public final ArrayList<Refreshable> refreshList;
	private void refreshGUI(boolean full) {
		for(var aRefreshable : refreshList) {
			aRefreshable.refresh(full);
		}
		updateContent(full);
	}
	
	private final JPanel update_panel;
	
	public CalendarEditorView(CalendarEditor a_controller) {
		super();
		controller = a_controller;
		ResourceBundle txt = ResourceBundle.getBundle( "calendar_editor", Locale.getDefault() );
		refreshList = new ArrayList<>();
		
		this.setLayout( new BorderLayout() );
		
		var tabs = new JTabbedPane();
		this.add( tabs, BorderLayout.CENTER );
		
		//the panel with update button
		update_panel = new JPanel();
		update_panel.setBackground( Color.decode( "#f5d14a" ) );
		var updateText = new JLabel( txt.getString( "update_warn" ));
		updateText.setForeground( Color.BLACK );
		update_panel.add( updateText );
		
		var updateButton = new JButton( txt.getString( "update_button" ));
		updateButton.addActionListener( event -> controller.switchToAwaitingCalendar() );
		
		this.add( update_panel, BorderLayout.NORTH );
		update_panel.add(updateText);
		update_panel.add( updateButton );
		update_panel.setVisible( false );
		
		//fill the tabs
		cycle_or_rules = new CycleOrRulesPanel( controller, txt );
		tabs.addTab( txt.getString("cycle_or_rules_tab"), cycle_or_rules );
		months_panel = new MonthsPanel( controller, txt );
		tabs.addTab( txt.getString("months_tab"), months_panel );
		week_panel = new WeekPanel( controller, txt );
		tabs.addTab( txt.getString("week_tab"), week_panel );
		tabs.setSelectedIndex( 1 );
		
		updateContent(true);
		controller.refreshList.add( this::refreshGUI );
	}
	
	private final CycleOrRulesPanel cycle_or_rules;
	
	private static class CycleOrRulesPanel extends JPanel {
		
		private final CalendarEditor controller;
		
		private final JLabel rules_info;
		private final JLabel cycles_info;
		private final JLabel cycles_left;
		private final String cycles_left_text;
		private final String leap_rule_text;
		private final String normal_rule_text;
		
		public CycleOrRulesPanel(CalendarEditor a_controller, ResourceBundle txt) {
			controller = a_controller;
			
			this.setLayout( new BorderLayout() );
			this.setBorder( BorderFactory.createEmptyBorder(5,5,5,5) );
			
			rules_info = new JLabel(txt.getString( "rules_info_label" ));
			cycles_info = new JLabel(txt.getString( "cycles_info_label" ));
			cycles_left = new JLabel("");
			cycles_left.setFont( cycles_left.getFont().deriveFont( Font.PLAIN, 12 ) );
			cycles_left_text = "<html>" + txt.getString( "cycles_left" ) + "</html>";
			leap_rule_text = "<html>" + txt.getString( "leap_rule_text" ) + "</html>";
			normal_rule_text = "<html>" + txt.getString( "normal_rule_text" ) + "</html>";
		}
		
		public void updateContent() {
			this.removeAll();
			
			if( controller.hasLeapRules() ) {
				this.add( rules_info, BorderLayout.NORTH );
				
				var toggles_list = new JPanel();
				toggles_list.setLayout( new GridLayout(0,1) );
				this.add( toggles_list, BorderLayout.CENTER );
				
				var rules = controller.getRules();
				for(int ruleID=0; ruleID<rules.length; ruleID++) {
					var toggle = new JCheckBox();
					if( rules[ruleID].is_leap() == IntercalationType.LEAP ) {
						toggle.setText( leap_rule_text.replace( "{{div}}",
								rules[ruleID].each_year().toString()
								) );
					} else {
						toggle.setText( normal_rule_text.replace( "{{div}}",
								rules[ruleID].each_year().toString()
						) );
					}
					toggle.setSelected( controller.isRuleTurnedOn( ruleID ) );
					toggle.setFont( toggle.getFont().deriveFont( Font.PLAIN ) );
					
					int finalRuleID = ruleID;
					toggle.addActionListener( event -> controller.turnRuleOn( finalRuleID, toggle.isSelected() ) );
					toggles_list.add(toggle);
				}
			}
			if( controller.hasYearCycle() ) {
				this.add( cycles_info, BorderLayout.NORTH );
				
				var cycle_list = new JPanel();
				cycle_list.setLayout( new GridLayout(0,8) );
				this.add( cycle_list, BorderLayout.CENTER );
				
				var the_cycle = controller.getCycle();
				for(int year=0; year<the_cycle.length; year++) {
					var button = new JToggleButton(String.valueOf( year+1 ));
					if( the_cycle[year] == IntercalationType.LEAP ) {
						button.setSelected( true );
					}
					cycle_list.add( button );
					int finalYear = year;
					button.addActionListener( event -> this.updateCycle( finalYear, button ) );
				}
				
				cycles_left.setText(cycles_left_text.replace( "{{cycles}}", "0" ));
				this.add(cycles_left, BorderLayout.SOUTH);
			}
			
			this.revalidate();
			this.repaint();
		}
		
		private void updateCycle(int year, JToggleButton button) {
			controller.turnYearInCycleOn( year, button.isSelected() );
			cycles_left.setText( cycles_left_text.replace( "{{cycles}}",
					String.valueOf(controller.surplusYearsToAssignInCycle())
					) );
		}
		
	}
	
	private final MonthsPanel months_panel;
	
	private static class MonthsPanel extends JPanel implements KeyListener {
		
		private final CalendarEditor controller;
		private final ResourceBundle txt;
		private final JTextField month_name_field;
		private final JButton special_month_indicator;
		private final JLabel normal_days_counter;
		private final JButton normal_add;
		private final JButton normal_remove;
		private final JLabel leap_days_counter;
		private final JButton leap_add;
		private final JButton leap_remove;
		private final JButton month_add;
		private final JButton month_remove;
		private final JLabel days_pool_info;
		private final JButton previous_month_s;
		private final JButton next_month_s;
		
		public MonthsPanel(CalendarEditor a_controller, ResourceBundle bundle) {
			super();
			controller = a_controller;
			txt = bundle;
			
			this.setLayout( new GridBagLayout() );
			
			var plus_icon = new ImageIcon(ClassLoader.getSystemResource( "plus.png" ));
			var minus_icon = new ImageIcon(ClassLoader.getSystemResource( "minus.png" ));
			
			var c = new GridBagConstraints();
			c.gridy = 1;
			c.fill = GridBagConstraints.NONE;
			//--------
			var left_stop_icon = new ImageIcon(ClassLoader.getSystemResource( "arrow_left_stop.png" ));
			var left_icon = new ImageIcon(ClassLoader.getSystemResource( "arrow_left.png" ));
			var right_stop_icon = new ImageIcon(ClassLoader.getSystemResource( "arrow_right_stop.png" ));
			var right_icon = new ImageIcon(ClassLoader.getSystemResource( "arrow_right.png" ));
			//--------
			var first_month_s = new JButton(left_stop_icon);
			previous_month_s = new JButton(left_icon);
			next_month_s = new JButton(right_icon);
			var last_month_s = new JButton(right_stop_icon);
			month_name_field = new JTextField("");
			//--------
			c.gridx = 0;
			this.add(first_month_s, c);
			c.gridx = 1;
			this.add( previous_month_s, c);
			c.gridx = 5;
			this.add( next_month_s, c);
			c.gridx = 6;
			this.add(last_month_s, c);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;
			c.gridx = 2;
			c.gridwidth = 3;
			this.add(month_name_field, c);
			
			var month_name_label = new JLabel(txt.getString( "month_name_label" ));
			special_month_indicator = new JButton("Assign");
			
			var normal_days_label = new JLabel(txt.getString( "normal_days_label" ));
			var leap_days_label = new JLabel(txt.getString( "leap_days_label" ));
			normal_days_counter = new JLabel("0");
			leap_days_counter = new JLabel("0");
			normal_add = new JButton(plus_icon);
			normal_remove = new JButton(minus_icon);
			leap_add = new JButton(plus_icon);
			leap_remove = new JButton(minus_icon);
			month_add = new JButton(txt.getString( "month_add" ), plus_icon);
			month_remove = new JButton(txt.getString( "month_remove" ), minus_icon);
			days_pool_info = new JLabel("");
			
			//decorate components
			var counters_font = normal_days_counter.getFont().deriveFont( Font.PLAIN, 24 );
			var counters_border = BorderFactory.createCompoundBorder(
					BorderFactory.createLoweredBevelBorder(),
					BorderFactory.createEmptyBorder(5,5,5,5)
			);
			
			normal_days_counter.setFont( counters_font );
			leap_days_counter.setFont( counters_font );
			normal_days_counter.setBorder( counters_border );
			leap_days_counter.setBorder( counters_border );
			month_name_field.setFont( counters_font );
			days_pool_info.setFont( counters_font.deriveFont( Font.PLAIN, 14 ) );
			
			leap_days_label.setHorizontalAlignment( JLabel.TRAILING );
			
			//adding components
			c = new GridBagConstraints();
			c.gridwidth = 3;
			c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets( 4,4,4,4 );
			
			c.gridx = c.gridy = 0;
			this.add(month_name_label, c);
			
			c.gridx = 4;
			this.add( special_month_indicator, c);
			
			c.gridy = 2; c.gridx = 0;
			c.anchor = GridBagConstraints.LINE_START;
			this.add(normal_days_label, c);
			
			c.gridx = 4;
			c.anchor = GridBagConstraints.LINE_END;
			this.add(leap_days_label, c);
			
			c.gridwidth = 1;
			c.gridy = 3;
			c.ipady = c.ipadx = 0;
			c.anchor = GridBagConstraints.CENTER;
			c.fill = GridBagConstraints.NONE;
			
			c.gridx = 0;
			this.add( normal_days_counter, c);
			c.gridx++;
			this.add( normal_add, c);
			c.gridx++;
			this.add( normal_remove, c);
			
			c.anchor = GridBagConstraints.LINE_END;
			c.gridx = 4;
			this.add( leap_days_counter, c);
			c.gridx++;
			this.add( leap_add, c);
			c.gridx++;
			this.add( leap_remove, c);
			
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.LINE_START;
			c.gridx = 0; c.gridy = 4;
			c.gridwidth = 3;
			this.add( month_add, c);
			c.gridx = 4;
			c.anchor = GridBagConstraints.LINE_END;
			this.add( month_remove, c);
			
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.CENTER;
			c.gridx = 0; c.gridy = 5;
			c.gridwidth = 7;
			this.add( days_pool_info, c );
			
			c.gridx = 3; c.gridy = 0;
			c.gridwidth = 1;
			this.add(Box.createHorizontalGlue(), c);
			
			//events
			first_month_s.addActionListener( event -> controller.selectFirstMonth() );
			previous_month_s.addActionListener( event -> controller.selectPreviousMonth() );
			next_month_s.addActionListener( event -> controller.selectNextMonth() );
			last_month_s.addActionListener( event -> controller.selectLastMonth() );
			normal_add.addActionListener( event -> controller.addNormalDay() );
			normal_remove.addActionListener( event -> controller.removeNormalDay() );
			leap_add.addActionListener( event -> controller.addLeapDay() );
			leap_remove.addActionListener( event -> controller.removeLeapDay() );
			month_add.addActionListener( event -> controller.addMonth() );
			month_remove.addActionListener( event -> controller.removeMonth() );
			month_name_field.addKeyListener( this );
			special_month_indicator.addActionListener( event -> {
				if( controller.mustAssignMonthFeature() ) {
					controller.assignMonthFeature();
				} else {
					controller.removeMonthFeature();
				}
			} );
		}
		
		public void updateContents() {
			if( controller.getSelectedMonth() == null ) {
				return;
			}
			var month = controller.getSelectedMonth();
			
			month_name_field.setText( month.name );
			if( controller.mustAssignMonthFeature() ) {
				special_month_indicator.setVisible( true );
				special_month_indicator.setText( txt.getString( "special_normal" ) );
			} else if( controller.isSelectedMonthFeatured() ) {
				special_month_indicator.setVisible( true );
				switch( controller.getFeatureOfYear() ) {
					case LEAP ->
							special_month_indicator.setText( txt.getString( "special_leap" ) );
					case EPAGOMENAL ->
							special_month_indicator.setText( txt.getString( "special_epagomenal" ) );
					case NONE -> special_month_indicator.setVisible( false );
				}
			} else {
				special_month_indicator.setVisible( false );
			}
			
			//select buttons
			previous_month_s.setEnabled( controller.canSelectPreviousMonth() );
			next_month_s.setEnabled( controller.canSelectNextMonth() );
			
			//normal days in month
			normal_days_counter.setText( String.valueOf( month.normal_days ) );
			normal_add.setEnabled( controller.canAddNormalDay() );
			normal_remove.setEnabled( controller.canRemoveNormalDay() );
			//leap days in month
			leap_days_counter.setText( String.valueOf( month.leap_days ) );
			leap_add.setEnabled( controller.canAddLeapDay() );
			leap_remove.setEnabled( controller.canRemoveLeapDay() );
			//month add/remove
			month_add.setEnabled( controller.canAddMonth() );
			month_remove.setEnabled( controller.canRemoveMonth() );
			//pool info
			var pool_text = "<html>" + txt.getString( "pool_text" );// + "</html>";
			pool_text = pool_text.replace( "{{normal}}", controller.getNormalDaysPool().toString() );
			pool_text = pool_text.replace( "{{leap}}", controller.getLeapDaysPool().toString() );
			if( controller.mustAssignMonthFeature() ) {
				pool_text += "<br>";
				switch( controller.getFeatureOfYear() ) {
					case EPAGOMENAL -> pool_text += txt.getString( "must_assign_epagomenal" );
					case LEAP -> pool_text += txt.getString( "must_assign_leap" );
				}
			}
			pool_text += "</html>";
			days_pool_info.setText( pool_text );
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
			if( controller.getSelectedMonth() == null ) {
				return;
			}
			controller.getSelectedMonth().name = month_name_field.getText().strip();
		}
	}
	
	private final WeekPanel week_panel;
	
	private static class WeekPanel extends JPanel {
		
		private final CalendarEditor controller;
		private final JSpinner days_in_week_field;
		private final JRadioButton beginning_button;
		private final JRadioButton continuing_button;
		
		public WeekPanel(CalendarEditor a_controller, ResourceBundle txt) {
			super();
			controller = a_controller;
			
			this.setLayout( new GridBagLayout() );
			var c = new GridBagConstraints();
			
			var days_in_week_label = new JLabel(txt.getString( "days_in_week_label" ));
			var days_model = new SpinnerNumberModel();
			days_model.setMinimum( 1 );
			days_in_week_field = new JSpinner(days_model);
			var pref = days_in_week_field.getPreferredSize();
			pref.width *= 3;
			days_in_week_field.setPreferredSize( pref );
			
			//week start type panel
			var week_type_panel = new JPanel(new GridLayout(2,1));
			week_type_panel.setBorder( BorderFactory.createTitledBorder( txt.getString( "week_type_label" ) ) );
			beginning_button = new JRadioButton(txt.getString( "week_month_start" ));
			continuing_button = new JRadioButton(txt.getString( "week_continues" ));
			
			week_type_panel.add( beginning_button );
			week_type_panel.add( continuing_button );
			
			var button_group = new ButtonGroup();
			button_group.add( beginning_button );
			button_group.add( continuing_button );
			
			//------
			c.gridy = 0;
			c.gridx = 0;
			c.ipadx = c.ipady = 5;
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.LINE_START;
			this.add( days_in_week_label, c );
			
			c.gridx = 1;
			c.anchor = GridBagConstraints.LINE_END;
			this.add( days_in_week_field, c );
			
			c.gridx = 0;
			c.gridy = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.CENTER;
			c.gridwidth = 2;
			this.add( week_type_panel, c );
			
			//events
			days_in_week_field.addChangeListener( event -> updateWeekLength() );
			beginning_button.addActionListener( event -> updateWeekType(true) );
			continuing_button.addActionListener( event -> updateWeekType(false) );
		}
		
		private void updateWeekType(boolean value) {
			var week = controller.getWeek();
			if( week == null ) return;
			
			week.starts_with_month = value;
		}
		
		private void updateWeekLength() {
			var week = controller.getWeek();
			if( week == null ) return;
			
			week.length = (Integer) days_in_week_field.getValue();
		}
		
		public void updateContent() {
			var week = controller.getWeek();
			if( week == null ) return;
			
			days_in_week_field.setValue( week.length );
			
			if( week.starts_with_month ) {
				beginning_button.setSelected( true );
			} else {
				continuing_button.setSelected( true );
			}
		}
	}
	
	public void updateContent(boolean full) {
		if(full) {
			cycle_or_rules.updateContent();
			week_panel.updateContent();
		}
		update_panel.setVisible( controller.isNewCalendarAwaiting() );
		months_panel.updateContents();
	}
	
}
