package wormguides;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/*
 * Callback class for our HTML page for a terminal cell case to open link in default browser
 * 
 * Because our anchors <a> cannot pass vars, we'll keep the links in
 * memory and have the anchor call the appropriate callback method
 */
public class InfoWindowLinkController {
	ArrayList<String> links;
	
	public InfoWindowLinkController(ArrayList<String> links) {
		this.links = links;
		
		for (String link : links) {
			System.out.println(link);
		}
	}
	
	public void wormatlas() throws IOException, URISyntaxException {
		openLink("www.wormatlas.org");
	}
	
	public void wormbase() throws IOException, URISyntaxException {
		openLink("www.wormbase.org");
	}
	
	private void openLink(String host) throws IOException, URISyntaxException {
		for (String link : links) {
			if (link.contains(host)) {
				if (Desktop.isDesktopSupported()) {
					Desktop.getDesktop().browse(new URI(link));
				}
			}
		}
	}
}