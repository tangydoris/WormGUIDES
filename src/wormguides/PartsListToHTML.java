package wormguides;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import wormguides.HTMLGenerator.HTMLTags;
import wormguides.model.PartsList;

public class PartsListToHTML {
	
	
	public PartsListToHTML() {
		super();
	}
	
	
	public String buildPartsListAsHTML() {
		String html = HTMLTags.openTableTagHTML;
		URL url = PartsListToHTML.class.getResource("model/partslist.txt");
		
		try {
			if (url != null) {
				InputStream input = url.openStream();
				InputStreamReader isr = new InputStreamReader(input);
				BufferedReader br = new BufferedReader(isr);
				
				String line;
				while ((line = br.readLine()) != null) {
					html += (HTMLTags.openTableRowHTML + HTMLTags.openTableDataHTML 
							+ line + HTMLTags.closeTableDataHTML + HTMLTags.closeTableRowHTML);
				}
			} else {
				System.out.println("couldn't locate partslist.txt");
			}
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		html += (HTMLTags.closeTableRowHTML + HTMLTags.closeTableTagHTML);
		
		return HTMLGenerator.generateCompleteHTML(html);
	}
}