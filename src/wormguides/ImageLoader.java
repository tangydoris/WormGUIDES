package wormguides;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageLoader {
	
	public ImageView forward, backward, play, pause;
	JarFile jarFile;
	
	public ImageLoader(String jarPath) {
		try {
			this.jarFile = new JarFile(new File(jarPath));

			Enumeration<JarEntry> entries = jarFile.entries();
			//int time = 0;
			
			JarEntry entry;
			while (entries.hasMoreElements()){
				entry = entries.nextElement();
				//String name = entry.getName();
				//System.out.println(name);
				if (entry.getName().startsWith(ENTRY_PREFIX)) {
					processImage(entry);
				}
			}

			jarFile.close();
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void processImage(JarEntry entry) throws IOException {
		InputStream input = jarFile.getInputStream(entry);
		ImageView icon = new ImageView(new Image(input));
		switch (entry.getName()) {
			case BACKWARD_PNG:
				this.backward = icon;
				break;
			case FORWARD_PNG:
				this.forward = icon;
				break;
			case PLAY_PNG:
				this.play = icon;
				break;
			case PAUSE_PNG:
				this.pause = icon;
				break;
		}
	}

	public ImageView getForwardIcon() {
		return this.forward;
	}
	
	public ImageView getBackwardIcon() {
		return this.backward;
	}
	
	public ImageView getPlayIcon() {
		return this.play;
	}
	
	public ImageView getPauseIcon() {
		return this.pause;
	}
	
	private static final String ENTRY_PREFIX = "wormguides/view/icons/",
			BACKWARD_PNG = ENTRY_PREFIX+"backward.png",
			FORWARD_PNG = ENTRY_PREFIX+"forward.png",
			PAUSE_PNG = ENTRY_PREFIX+"pause.png",
			PLAY_PNG = ENTRY_PREFIX+"play.png";
	
}
