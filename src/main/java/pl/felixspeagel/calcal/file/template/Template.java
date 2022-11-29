package pl.felixspeagel.calcal.file.template;

import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Template {
	
	private final String template;
	
	public Template(String content) {
		template = content;
		fields = new HashMap<>();
	}
	
	private final Map<String, String> fields;
	
	public void setContent(String of_field, String content) {
		fields.put( of_field, content );
	}
	public void setContent(String of_field, Color content) {
		String str = "#";
		str = str + String.format( "%02X", content.getRed() );
		str = str + String.format( "%02X", content.getGreen() );
		str = str + String.format( "%02X", content.getBlue() );
		fields.put( of_field, str );
	}
	public void clearContent(String of_field) {
		fields.put( of_field, "" );
	}
	public void addContent(String of_field, String content) {
		if( fields.containsKey( of_field ) ) {
			fields.put( of_field, fields.get( of_field ) + content );
		} else {
			fields.put( of_field, content );
		}
	}
	public void reset() {
		fields.clear();
	}
	
	public String getResult() {
		String result = template;
		
		for(var field : fields.keySet()) {
			result = result.replace( "{{" + field + "}}", fields.get( field ) );
		}
		
		return result;
	}
	
	public static Template getTemplate(String directory, String name) {
		File res;
		try{
			var to_load = directory + "." + Locale.getDefault().toString() + "/" + name;
			
			res = new File( ClassLoader.getSystemResource( to_load ).toURI() );
			
		} catch( URISyntaxException | NullPointerException e1 ) {
			try {
				var to_load = directory + "/" + name;
				
				res = new File( ClassLoader.getSystemResource( to_load ).toURI() );
			} catch( URISyntaxException | NullPointerException e2 ) {
				return null;
			}
		}
		
		try(BufferedReader br = new BufferedReader(new FileReader(res))) {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			return new Template( sb.toString() );
		} catch( IOException e ) {
			return null;
		}
	}
	
}
