package pl.felixspeagel.calcal;

import pl.felixspeagel.calcal.controllers.MainController;
import pl.felixspeagel.calcal.file.ProjectReader;
import pl.felixspeagel.calcal.views.CalendarWizard;
import pl.felixspeagel.calcal.views.SimulationView;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends JFrame {
	public static void main(String[] args) {
		var txt = ResourceBundle.getBundle( "main_window", Locale.getDefault() );
		
		JFrame.setDefaultLookAndFeelDecorated( false );
		//set system look and feel
		try {
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		} catch(
				  UnsupportedLookAndFeelException |
				  ClassNotFoundException |
				  InstantiationException |
				  IllegalAccessException e ) {
			//do nothing - use Java's default look and feel
		}
		
		SwingUtilities.invokeLater( () -> {
			MainController controller = new MainController();
			@SuppressWarnings("unused") var window = new Main( txt.getString("app_title"), txt, controller );
			
		} );
	}
	
	private final MainController controller;
	private final ResourceBundle txt;
	
	public Main(String title, ResourceBundle bundle, MainController a_controller) {
		super(title);
		controller = a_controller;
		txt = bundle;
		
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		
		var app_banner_img = new ImageIcon(ClassLoader.getSystemResource( "logo.png" ));
		var app_system_icon = new ImageIcon(ClassLoader.getSystemResource( "calendar_icon_small.png" ));
		this.setIconImage( app_system_icon.getImage() );
		var calendar_create_img = new ImageIcon(ClassLoader.getSystemResource( "create_new_calendar.png" ));
		var calendar_edit_img = new ImageIcon(ClassLoader.getSystemResource( "edit_calendar.png" ));
		var calendar_print_img = new ImageIcon(ClassLoader.getSystemResource( "print_calendar.png" ));
		
		var app_banner = new JLabel(app_banner_img);
		app_banner.setBackground( Color.BLACK );
		this.getContentPane().add( app_banner, BorderLayout.NORTH );
		
		var central_wrapper = new JPanel(new GridBagLayout());
		this.getContentPane().add(central_wrapper, BorderLayout.CENTER);
		var central_panel = new JPanel();
		var c = new GridBagConstraints();
		
		c.gridx = c.gridy = 0;
		c.gridwidth = c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		central_wrapper.add(central_panel, c);
		central_panel.setBorder( BorderFactory.createEmptyBorder(30,30,30,30) );
		
		var new_calendar_button = new JButton(txt.getString( "new_button" ), calendar_create_img);
		var edit_calendar_button = new JButton(txt.getString( "edit_button" ), calendar_edit_img);
		var print_calendar_button = new JButton(txt.getString( "print_button" ), calendar_print_img);
		
		new_calendar_button.setVerticalTextPosition( JButton.BOTTOM );
		new_calendar_button.setHorizontalTextPosition( JButton.CENTER );
		edit_calendar_button.setVerticalTextPosition( JButton.BOTTOM );
		edit_calendar_button.setHorizontalTextPosition( JButton.CENTER );
		print_calendar_button.setVerticalTextPosition( JButton.BOTTOM );
		print_calendar_button.setHorizontalTextPosition( JButton.CENTER );
		
		var buttons_font = new Font( "Sans-serif", Font.BOLD, 14 );
		new_calendar_button.setFont( buttons_font );
		edit_calendar_button.setFont( buttons_font );
		print_calendar_button.setFont( buttons_font );
		
		central_panel.add( new_calendar_button );
		central_panel.add( edit_calendar_button );
		central_panel.add( print_calendar_button );
		
		//events
		new_calendar_button.addActionListener( event -> this.openCreator() );
		edit_calendar_button.addActionListener( event -> this.openEditor() );
		print_calendar_button.addActionListener( event -> this.openSimulator() );
		
		this.pack();
		this.setLocationRelativeTo( null );
		this.setVisible( true );
	}
	
	private void openSimulator() {
		var fileChooser = new FileDialog( this, txt.getString( "editor_file_dialog_title" ) );
		fileChooser.setMode( FileDialog.LOAD );
		fileChooser.setMultipleMode( false );
		fileChooser.setModal( true );
		fileChooser.setFilenameFilter( (file, s) -> s.endsWith( ".xml" ) );
		
		fileChooser.setVisible( true );
		
		var files = fileChooser.getFiles();
		if( files.length != 1 ) return;
		
		try {
			var editWizard = controller.newSimulation( files[0] );
			var wizard = new SimulationView( this, txt.getString( "print_dialog_title" ),
					editWizard
			);
			wizard.setVisible( true );
		} catch( ProjectReader.ReaderException e ) {
			JOptionPane.showMessageDialog(
					this,
					txt.getString( "error_opening_project_file" ),
					txt.getString( "error_opening_project_file_title" ),
					JOptionPane.ERROR_MESSAGE
			);
		}
	}
	
	private void openEditor() {
		var fileChooser = new FileDialog( this, txt.getString( "editor_file_dialog_title" ) );
		fileChooser.setMode( FileDialog.LOAD );
		fileChooser.setMultipleMode( false );
		fileChooser.setModal( true );
		fileChooser.setFilenameFilter( (file, s) -> s.endsWith( ".xml" ) );
		
		fileChooser.setVisible( true );
		
		var files = fileChooser.getFiles();
		if( files.length != 1 ) return;
		
		try {
			var editWizard = controller.newEditWizard( files[0] );
			var wizard = new CalendarWizard( this, txt.getString( "edit_dialog_title" ), editWizard );
			wizard.setVisible( true );
		} catch( ProjectReader.ReaderException e ) {
			JOptionPane.showMessageDialog(
					this,
					txt.getString( "error_opening_project_file" ),
					txt.getString( "error_opening_project_file_title" ),
					JOptionPane.ERROR_MESSAGE
					);
		}
	}
	
	private void openCreator() {
		var creator_controller = controller.newCreateWizard();
		var wizard = new CalendarWizard( this, txt.getString( "new_dialog_title" ), creator_controller );
		wizard.setVisible( true );
	}
}