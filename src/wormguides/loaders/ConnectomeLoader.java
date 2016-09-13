package wormguides.loaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

import wormguides.models.NeuronalSynapse;
import wormguides.models.SynapseType;

public class ConnectomeLoader {
    private static final String s_presynapticV1 = "S";
    private static final String s_presynapticV2 = "Sp";
    private static final String r_postsynapticV1 = "R";
    private static final String r_postsynapticV2 = "Rp";
    private static final String ej_electrical = "EJ";
    private static final String nmj_neuromuscular = "NMJ";
    private static final String headerLine = "Cell 1,Cell 2,Type,Nbr";
    private String filePath;

	public ArrayList<NeuronalSynapse> loadConnectome() {
        URL url = ConnectomeLoader.class.getResource("/wormguides/models/connectome_config_file/NeuronConnect.csv");

		ArrayList<NeuronalSynapse> connectome = new ArrayList<NeuronalSynapse>();
		try {
			if (url != null) {
				InputStream stream = url.openStream();
				InputStreamReader streamReader = new InputStreamReader(stream);
				BufferedReader reader = new BufferedReader(streamReader);

				String line;

				while ((line = reader.readLine()) != null) {
					// check if header line
					if (line.equals(headerLine)) {
						line = reader.readLine();
						if (line == null)
							break;
					}
					// make sure valid line
					if (line.length() <= 1)
						break;

					StringTokenizer tokenizer = new StringTokenizer(line, ",");
					// check if valid line i.e. 4 tokenss
					if (tokenizer.countTokens() == 4) {
						// gather data for a neuronal synapse: cell_1, cell_2,
						// SynapseType, number of synapses
						String cell_1 = tokenizer.nextToken();
						String cell_2 = tokenizer.nextToken();
						String synapseTypeStr = tokenizer.nextToken();
						String numberOfSynapsesStr = tokenizer.nextToken();

						// remove padding 0s from cell names
						if (cell_1.contains("0")) {
							cell_1 = removeZeroPad(cell_1);
						}

						if (cell_2.contains("0")) {
							cell_2 = removeZeroPad(cell_2);
						}

						// unchecked --> number format exception unhandled
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
							// System.out.println("Unknown synapse type: " +
							// synapseType);
							synapseType = null;
						}

						if (cell_1.length() != 0 && cell_2.length() != 0 && synapseType != null
								&& numberOfSynapsesStr.length() >= 0) {
							NeuronalSynapse neuronalSynapse = new NeuronalSynapse(cell_1, cell_2, synapseType,
									numberOfSynapses);
							connectome.add(neuronalSynapse);
						}
					}
				}
			}

			return connectome;

		} catch (IOException e) {
			System.out.println("The connectome file " + filePath + " wasn't found on the system.");
		}

		return connectome;
	}

	private String removeZeroPad(String cell) {
		if (cell.contains("0")) {
			int zeroIDX = cell.indexOf("0");
			cell = cell.substring(0, zeroIDX) + cell.substring(zeroIDX + 1);
		}

		return cell;
	}
}
