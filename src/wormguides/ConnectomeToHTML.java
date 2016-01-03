package wormguides;

import java.util.ArrayList;
import java.util.Collections;

import wormguides.model.Connectome;
import wormguides.model.NeuronalSynapse;

public class ConnectomeToHTML extends HTMLGenerator {
	private Connectome connectome;
	
	public ConnectomeToHTML(Connectome connectome) {
		super();
		this.connectome = connectome;
	}
	
	public String buildConnectomeAsHTML() {
		if (connectome != null) {
			
			//add formatted wiring partners for each cell in connectome
				
			//collect all unique cells
			ArrayList<String> cells = new ArrayList<String>();
			for (NeuronalSynapse ns: connectome.getConnectomeList()) {
				String cell_1 = ns.getCell1();
				String cell_2 = ns.getCell2();
					
				//add unique entries to list
				if (!cells.contains(cell_1)) {
					cells.add(cell_1);
				}
					
				if (!cells.contains(cell_2)) {
					cells.add(cell_2);
				}
			}
				
			//alphabetize the connectome cells
			Collections.sort(cells);
				
			//add tables of wiring partners for each unique entry
			ArrayList<String> wiringPartnersAsHTMLTables = new ArrayList<String>();
			for (String cell : cells) {
				wiringPartnersAsHTMLTables.add(queryWiringPartnersAsHTMLTable(cell, false));
			}
				
			String htmlTables = "";
			for (String table : wiringPartnersAsHTMLTables) {
				htmlTables += (table + breakLine + breakLine);
			}

			return generateCompleteHTML(htmlTables);
		}
		return "";
	}
	
	/*
	 * Method that generates a legible summary of the wiring partners given a query cell in the connectome
	 * 
	 * Used to generate a snippet of HTML for the global connectome HTML file
	 * OR to generate a complete HTML page for a single search cell
	 * 
	 * If using this method to generate an external info page for a single cell, pass a TRUE bool val to
	 * generate a full HTML page
	 */
	public String queryWiringPartnersAsHTMLTable(String queryCell, boolean generateFullHTML) {
		/*	FORMAT 
		 *   Cell Name
		 *   presynaptic:  cellname (numconnections), cellname (numconnections)
		 *   postsynaptic: ...
		 */
		ArrayList<String> presynapticPartners = new ArrayList<String>();
		ArrayList<String> postsynapticPartners = new ArrayList<String>();
		ArrayList<String> electricalPartners = new ArrayList<String>();
		ArrayList<String> neuromuscularPartners = new ArrayList<String>();
		
		//get wiring partners
		for (NeuronalSynapse ns : connectome.getConnectomeList()) {
			String cell_1 = ns.getCell1();
			String cell_2 = ns.getCell2();
			
			if (queryCell.equals(cell_1)) {
				//add cell_2 as a wiring partner
				
				//extract number of synapses
				int numberOfSynapses = ns.numberOfSynapses();
				
				//extract synapse type
				String synapseTypeDescription = ns.getSynapseType().getDescription();
				
				//format wiring partner with cell_2
				String wiringPartner = cell_2 + formatNumberOfSynapses(Integer.toString(numberOfSynapses));
				
				if (synapseTypeDescription.equals(s_presynapticDescription)) {
					presynapticPartners.add(wiringPartner);
				} else if (synapseTypeDescription.equals(r_postsynapticDescription)) {
					postsynapticPartners.add(wiringPartner);
				} else if (synapseTypeDescription.equals(ej_electricalDescription)) {
					electricalPartners.add(wiringPartner);
				} else if (synapseTypeDescription.equals(nmj_neuromuscularDescrpition)) {
					neuromuscularPartners.add(wiringPartner);
				}
				
			} else if (queryCell.equals(cell_2)) {
				//add cell_1 as a wiring partner
				
				//extract number of synapses
				int numberOfSynapses = ns.numberOfSynapses();
				
				//extract synapse type
				String synapseTypeDescription = ns.getSynapseType().getDescription();
				
				//format wiring partner with cell_1
				String wiringPartner = cell_1 + formatNumberOfSynapses(Integer.toString(numberOfSynapses));
				
				if (synapseTypeDescription.equals(s_presynapticDescription)) {
					presynapticPartners.add(wiringPartner);
				} else if (synapseTypeDescription.equals(r_postsynapticDescription)) {
					postsynapticPartners.add(wiringPartner);
				} else if (synapseTypeDescription.equals(ej_electricalDescription)) {
					electricalPartners.add(wiringPartner);
				} else if (synapseTypeDescription.equals(nmj_neuromuscularDescrpition)) {
					neuromuscularPartners.add(wiringPartner);
				}
			}
		}
		
		//format wiring partners as HTML --> only format if > 0 partners
		String queryCellTableHeaderRow = openTableRowHTML + openTableHeader2SpanHTML + 
				cellTitle + queryCell +
				closeTableHeaderHTML + closeTableRowHTML;
		
		String presynapticPartnersTableRow;
		Collections.sort(presynapticPartners); //alphabetize
		if (presynapticPartners.size() > 0) {
			presynapticPartnersTableRow = openTableRowHTML + openTableDataHTML +
					presynapticPartnersTitle + closeTableDataHTML + openTableDataHTML +
					presynapticPartners.toString() + closeTableDataHTML + closeTableRowHTML;
		} else {
			presynapticPartnersTableRow = "";
		}
		
		
		String postsynapticPartnersTableRow;
		Collections.sort(postsynapticPartners); //alphabetize
		if (postsynapticPartners.size() > 0) {
			postsynapticPartnersTableRow = openTableRowHTML + openTableDataHTML + 
					postsynapticPartnersTitle + closeTableDataHTML + openTableDataHTML +
					postsynapticPartners.toString() + closeTableDataHTML + closeTableRowHTML;
		} else {
			postsynapticPartnersTableRow = "";
		}
		
		String electricalPartnersTableRow;
		Collections.sort(electricalPartners); //alphabetize
		if (electricalPartners.size() > 0) {
			electricalPartnersTableRow = openTableRowHTML + openTableDataHTML +
					electricalPartnersTitle + closeTableDataHTML + openTableDataHTML +
					electricalPartners.toString() + closeTableDataHTML + closeTableRowHTML;
		} else {
			electricalPartnersTableRow = "";
		}
		
		String neuromuscularPartnersTableRow;
		Collections.sort(neuromuscularPartners); //alphabetize
		if (neuromuscularPartners.size() > 0) {
			neuromuscularPartnersTableRow = openTableRowHTML + openTableDataHTML +
					neuromusclarPartnersTitle + closeTableDataHTML + openTableDataHTML +
					neuromuscularPartners.toString() + closeTableDataHTML + closeTableRowHTML;
		} else {
			neuromuscularPartnersTableRow = "";
		}
 		
		String table = openTableTagHTML + queryCellTableHeaderRow + presynapticPartnersTableRow
				+ postsynapticPartnersTableRow + electricalPartnersTableRow  
				+ neuromuscularPartnersTableRow + closeTableTagHTML;
		
		//check if need to generate a full html page
		if (generateFullHTML) {
			return generateCompleteHTML(table);
		} else {
			return table;
		}
	}
		
	private String formatNumberOfSynapses(String numberOfSynapses) {
		return "(" + numberOfSynapses + ")";
	}
	
	private final static String cellTitle = "Cell: ";
	
	private final static String s_presynapticDescription = "S presynaptic";
	private final static String r_postsynapticDescription = "R postsynaptic";
	private final static String ej_electricalDescription = "EJ electrical";
	private final static String nmj_neuromuscularDescrpition = "Nmj neuromuscular";
	
	private final static String presynapticPartnersTitle = "Presynaptic: ";
	private final static String postsynapticPartnersTitle = "Postsynaptic: ";
	private final static String electricalPartnersTitle = "Electrical: ";
	private final static String neuromusclarPartnersTitle = "Neuromusclar: ";
	
}
