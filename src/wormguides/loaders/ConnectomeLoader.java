package wormguides.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import wormguides.model.NeuronalSynapse;
import wormguides.model.SynapseType;

public class ConnectomeLoader {
	private String filePath;
	
	public ConnectomeLoader(String filePath) {
		this.filePath = filePath;
	}
	
	public ArrayList<NeuronalSynapse> loadConnectome() {
		ArrayList<NeuronalSynapse> connectome = new ArrayList<NeuronalSynapse>();
		try {
			JarFile jarFile = new JarFile(new File(JARname));
			Enumeration<JarEntry> entries = jarFile.entries();
			JarEntry entry;
			
			while (entries.hasMoreElements()) {
				entry = entries.nextElement();

				if (entry.getName().equals(filePath)) {
					InputStream stream = jarFile.getInputStream(entry);
					InputStreamReader streamReader = new InputStreamReader(stream);
					BufferedReader reader = new BufferedReader(streamReader);
					
					String line;
					
					while((line = reader.readLine()) != null) {
						//check if header line
						if (line.equals(headerLine)) {
							line = reader.readLine();
							if (line == null) break;
						}
						//make sure valid line
						if (line.length() <= 1) break;
						
						StringTokenizer tokenizer = new StringTokenizer(line, ",");
						//check if valid line i.e. 4 tokens
						if (tokenizer.countTokens() == 4) {							
							//gather data for a neuronal synapse: cell_1, cell_2, SynapseType, number of synapses
							String cell_1 = tokenizer.nextToken();
							String cell_2 = tokenizer.nextToken();
							String synapseTypeStr = tokenizer.nextToken();
							String numberOfSynapsesStr = tokenizer.nextToken();
							
							//unchecked --> number format exception unhandled
							Integer numberOfSynapses = Integer.parseInt(numberOfSynapsesStr);
							
							SynapseType synapseType;
							if (synapseTypeStr.equals(s_presynapticV1)) {
								synapseType = SynapseType.S_PRESYNAPTIC;
								synapseType.setMonadic();
							} else if (synapseTypeStr.equals(s_presynapticV2)) {
								synapseType = SynapseType.S_PRESYNAPTIC;
								synapseType.setPoyadic();
							} else if (synapseTypeStr.equals(r_postsynapticV1)) {
								synapseType = SynapseType.R_POSTSYNAPTIC;
								synapseType.setMonadic();
							} else if (synapseTypeStr.equals(r_postsynapticV2)) { 
								synapseType = SynapseType.R_POSTSYNAPTIC;
								synapseType.setPoyadic();
							} else if (synapseTypeStr.equals(ej_electrical)) {
								synapseType = SynapseType.EJ_ELECTRICAL;
							} else if (synapseTypeStr.equals(nmj_neuromuscular)) {
								synapseType = SynapseType.NMJ_NEUROMUSCULAR;
							} else {
								//System.out.println("Unknown synapse type: " + synapseType);
								synapseType = null;
							}
							
							if (cell_1.length() != 0 && cell_2.length() != 0
									&& synapseType != null && numberOfSynapsesStr.length() >= 0) {
								NeuronalSynapse neuronalSynapse = 
										new NeuronalSynapse(cell_1, cell_2, synapseType, numberOfSynapses);
								connectome.add(neuronalSynapse);
							}
							
						}
					}
				}
				
			}
			
			jarFile.close();
			return connectome;
			
		} catch (IOException e) {
			System.out.println("The connectome file " + filePath + " wasn't found on the system.");
		}
		
		return connectome;
	}
	
	
	
	private static final String JARname = "WormGUIDES.jar";
	private static final String s_presynapticV1 = "S";
	private static final String s_presynapticV2 = "Sp";
	private static final String r_postsynapticV1 = "R";
	private static final String r_postsynapticV2 = "Rp";
	private static final String ej_electrical = "EJ";
	private static final String nmj_neuromuscular = "NMJ";
	private static final String headerLine = "Cell 1,Cell 2,Type,Nbr";
}
