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
	
	private static ImageView forward, backward, play, pause;
	private static Image edit, eye, close; 
	private static JarFile jarFile;
	
	public static void loadImages(String jarPath) {
		try {
			jarFile = new JarFile(new File(jarPath));
			Enumeration<JarEntry> entries = jarFile.entries();
			JarEntry entry;
			while (entries.hasMoreElements()){
				entry = entries.nextElement();
				if (entry.getName().startsWith(ENTRY_PREFIX)) {
					processImage(entry);
				}
			}
			jarFile.close();
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public static void processImage(JarEntry entry) throws IOException {
		InputStream input = jarFile.getInputStream(entry);
		Image image = new Image(input);
		switch (entry.getName()) {
			case EDIT_PNG:	edit = image;
							return;
			case EYE_PNG:	eye = image;
							return;
			case CLOSE_PNG:	close = image;
							return;
		}
		ImageView icon = new ImageView(image);
		switch (entry.getName()) {
			case BACKWARD_PNG:	backward = icon;
								break;
			case FORWARD_PNG:	forward = icon;
								break;
			case PLAY_PNG:		play = icon;
								break;
			case PAUSE_PNG:		pause = icon;
								break;
		}
	}

	public static ImageView getForwardIcon() {
		return forward;
	}
	
	public static ImageView getBackwardIcon() {
		return backward;
	}
	
	public static ImageView getPlayIcon() {
		return play;
	}
	
	public static ImageView getPauseIcon() {
		return pause;
	}
	
	public static ImageView getEditIcon() {
		return new ImageView(edit);
	}
	
	public static ImageView getEyeIcon() {
		return new ImageView(eye);
	}
	
	public static ImageView getCloseIcon() {
		return new ImageView(close);
	}
	
	private static final String ENTRY_PREFIX = "wormguides/view/icons/",
			BACKWARD_PNG = ENTRY_PREFIX+"backward.png",
			FORWARD_PNG = ENTRY_PREFIX+"forward.png",
			PAUSE_PNG = ENTRY_PREFIX+"pause.png",
			PLAY_PNG = ENTRY_PREFIX+"play.png",
			EDIT_PNG = ENTRY_PREFIX+"edit.png",
			EYE_PNG = ENTRY_PREFIX+"eye.png",
			CLOSE_PNG = ENTRY_PREFIX+"close.png";
}
