package pl.felixspeagel.calcal.views;

import pl.felixspeagel.calcal.controllers.Summary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

public class SummaryView extends JPanel {
	
	private final Summary controller;
	private final JLabel summary_text;
	private final JPopupMenu popup;
	private final ResourceBundle txt;
	
	public SummaryView(Summary a_controller) {
		super();
		controller = a_controller;
		txt = ResourceBundle.getBundle( "summary_view", Locale.getDefault() );
		var template = ResourceBundle.getBundle( "summary_text_templates", Locale.getDefault() );
		
		this.setLayout( new BorderLayout() );
		
		var desc_panel = new JPanel(new BorderLayout());
		desc_panel.setBorder( BorderFactory.createLoweredBevelBorder() );
		desc_panel.setMaximumSize( new Dimension(Integer.MAX_VALUE, 400) );
		desc_panel.setPreferredSize( new Dimension(300, 400) );
		this.add( desc_panel, BorderLayout.CENTER );
		
		summary_text = new JLabel("");
		
		var scroll_panel = new JScrollPane( summary_text );
		desc_panel.add( scroll_panel, BorderLayout.CENTER );
		
		var save_button = new JButton( txt.getString( "save_button" ));
		this.add( save_button, BorderLayout.SOUTH );
		
		//save menu
		popup = new JPopupMenu();
		var popup_xml = new JMenuItem( txt.getString( "popup_xml" ));
		var popup_txt = new JMenuItem( txt.getString( "popup_txt" ));
		var popup_md = new JMenuItem( txt.getString( "popup_md" ));
		var popup_html = new JMenuItem( txt.getString( "popup_html" ));
		
		popup.add( popup_xml );
		popup.addSeparator();
		popup.add(popup_txt);
		popup.add(popup_md);
		popup.add(popup_html);
		
		//events
		save_button.addActionListener( this::showMenu );
		popup_xml.addActionListener( event -> this.saveProject() );
		popup_txt.addActionListener( event -> this.savePlainText() );
		popup_md.addActionListener( event -> this.saveMarkdown() );
		popup_html.addActionListener( event -> this.saveHTML() );
		
		controller.text_getter = template::getString;
		controller.refreshList.add( this::updateContent );
	}
	
	private void showMenu(ActionEvent event) {
		var component = (Component) event.getSource();
		
		popup.show(component, component.getWidth()/2, component.getHeight()/2);
	}
	
	private File openSaveDialog(String title, String ext) {
		Container top = this;
		do{
			top = top.getParent();
		}while( ! ( top instanceof Frame ) );
		var fileChooser = new FileDialog( (Frame)top, title );
		fileChooser.setMode( FileDialog.SAVE );
		fileChooser.setMultipleMode( false );
		fileChooser.setModal( true );
		fileChooser.setFilenameFilter( (file, s) -> s.endsWith( ext ) );
		
		fileChooser.setVisible( true );
		
		var files = fileChooser.getFiles();
		if( files.length != 1 ) return null;
		else {
			if( ! files[0].getName().endsWith( ext ) ) {
				return new File( files[0].getAbsolutePath() + ext );
			} else {
				return files[0];
			}
		}
	}
	
	private void savePlainText() {
		var title = JOptionPane.showInputDialog( txt.getString( "get_title_dialog" ) );
		if( title == null ) return;
		title = title.strip();
		if( title.isEmpty() ) return;
		
		File selected = openSaveDialog( txt.getString( "popup_txt" ), ".txt" );
		if( selected != null )
			controller.saveTXT(selected, title);
	}
	
	private void saveMarkdown() {
		var title = JOptionPane.showInputDialog( txt.getString( "get_title_dialog" ) );
		if( title == null ) return;
		title = title.strip();
		if( title.isEmpty() ) return;
		
		File selected = openSaveDialog( txt.getString( "popup_md" ), ".md" );
		if( selected != null )
			controller.saveMD(selected, title);
	}
	
	private void saveHTML() {
		var title = JOptionPane.showInputDialog( txt.getString( "get_title_dialog" ) );
		if( title == null ) return;
		title = title.strip();
		if( title.isEmpty() ) return;
		
		File selected = openSaveDialog( txt.getString( "popup_html" ), ".html" );
		if( selected != null )
			controller.saveHTML(selected, title);
	}
	
	private void saveProject() {
		File selected = openSaveDialog( txt.getString( "popup_xml" ), ".xml" );
		if( selected != null )
			controller.saveXML(selected);
	}
	
	@SuppressWarnings("unused")
	public void updateContent(boolean full) {
		summary_text.setText(
				"<html>" + controller.getSummary().replace( "\n", "<br>" ) + "</html>"
		);
	}
	
}
