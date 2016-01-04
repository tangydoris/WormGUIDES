package wormguides;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/*
 * This class constructs HTML formatted tables for connectome, parts list, and cell shapes
 */
public class HTMLGenerator {

	public HTMLGenerator() {
		
	}
	
	public String generateCompleteHTML(String body) {
		return htmlStart + body + htmlEnd;
	}
	
	public boolean isCompleteHTML(String html) {
		return html.contains(htmlStart) && html.contains(htmlEnd);
	}
	
	public File generateHTMLFile(String fileName, String body) {
		String HTMLAsString = body;
		
		//check if complete HTML file
		if (!isCompleteHTML(body)) {
			HTMLAsString = htmlStart + body + htmlEnd;
		}
		
		
		File html = null;
		fileName += htmlExt;
		
		BufferedWriter bw = null;
		try {
			html = new File(fileName);
			
			if (!html.exists()) {
				html.createNewFile();
			}
			
			Writer writer = new FileWriter(html);
			bw = new BufferedWriter(writer);
			bw.write(HTMLAsString);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return html;
	}
	
	//static vars
	public final static String newLine = "\n";
	
	//html page structuring
	private final static String htmlStart = "<!DOCTYPE html>" + 
												newLine + "<html>" +
												newLine + "<head>" +
												newLine + "<meta charset=\"utf-8\">" +
												newLine + "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">" +
												newLine + "<title>Connectome</title>" +
												newLine + "<style type=\"text/css\">" +
												newLine + "table, th, td {" +
												newLine + "border: 1px solid black;" +
												newLine + "}" +
												newLine + "body {" +
												newLine + "font-size: 13pt;" +
												newLine + "}" +
												newLine + "</style>" +
												newLine + "</head>" +
												newLine + "<body>" + newLine;
	private final static String htmlEnd = newLine + "</body>" +
											  newLine + "</html>";

	//html table tags
	public final static String openTableTagHTML = newLine + "<table>";
	public final static String closeTableTagHTML = newLine + "</table>";
	public final static String openTableRowHTML = newLine + "<tr>";
	public final static String closeTableRowHTML = newLine + "</tr>";
	public final static String openTableHeader2SpanHTML = newLine + "<th colspan=\"2\">";
	public final static String openTableHeaderHTML = newLine + "<th>";
	public final static String closeTableHeaderHTML = "</th>";
	public final static String openTableDataHTML = newLine + "<td>";
	public final static String closeTableDataHTML = "</td>";
	
	public final static String breakLine = "<br>";
	private final static String htmlExt = ".html";
}
