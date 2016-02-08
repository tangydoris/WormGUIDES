package wormguides.controllers;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

//http://java-buddy.blogspot.com/2012/05/communication-between-javafx-and.html

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
	}
	
	public void wormatlas() throws IOException, URISyntaxException {
		openLink("http://www.wormatlas.org/neurons/Individual%20Neurons/");
	}
	
	public void wormbase() throws IOException, URISyntaxException {
		openLink("http://www.wormbase.org/db/get?name=");
	}
	
	public void textpresso() throws IOException, URISyntaxException {
			openLink("http://textpresso-www.cacr.caltech.edu/cgi-bin/celegans/search?searchstring=");
	}
	
	public void wormwiring() throws IOException, URISyntaxException {
		openLink("http://wormwiring.hpc.einstein.yu.edu/data/neuronData.php?name=");
	}
	
	public void googleWormatlas() throws IOException, URISyntaxException {
		openLink("https://www.google.com/#q=site:wormatlas.org+");
	}
	
	public void google() throws IOException, URISyntaxException {
		openLink("https://www.google.com/#q=");
	}
	
	public void callFromJavascript(String msg) {
		System.out.println("passed from js: " + msg);
	}
	
	private void openLink(String host) throws IOException, URISyntaxException {
		for (String link : links) {
			if (link.startsWith(host)) {
				if (Desktop.isDesktopSupported()) {
					Desktop.getDesktop().browse(new URI(link));
					break;
				}
			}
		}
	}
}