package wormguides;

/*
 * This class constructs HTML tables for connectome, parts list, and cell shapes
 */
public class HTMLTableGenerator {

	public HTMLTableGenerator() {
		
	}
	
	public String generateFullHTML(String body) {
		return htmlStart + body + htmlEnd;
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
												newLine + "table, td {" +
												newLine + "border: 1px solid black;" +
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
	public final static String openTableHeaderHTML = newLine + "<th colspan=\"2\">";
	public final static String closeTableHeaderHTML = "</th>";
	public final static String openTableDataHTML = newLine + "<td>";
	public final static String closeTableDataHTML = "</td>";
	
	public final static String breakLine = "<br>";
}
