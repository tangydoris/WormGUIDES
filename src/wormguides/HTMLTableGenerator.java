package wormguides;

import java.util.ArrayList;
import java.util.Collections;

import wormguides.model.NeuronalSynapse;

/*
 * This class constructs HTML tables for connectome, parts list, and cell shapes
 */
public class HTMLTableGenerator {

	//constructor
	public HTMLTableGenerator() {
		
	}
	
	//static vars
	public final static String newLine = "\n";
		
		
	//html page structuring
	public final static String htmlStart = "<!DOCTYPE html>" + 
												newLine + "<html>" +
												newLine + "<head>" +
												newLine + "<meta charset=\"utf-8\">" +
												newLine + "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">" +
												newLine + "<title>Connectome</title>" +
												newLine + "</head>" +
												newLine + "<body>" + newLine;
	public final static String htmlEnd = newLine + "</body>" +
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
}
