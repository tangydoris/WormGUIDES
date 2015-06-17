package wormguides;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

// Loader class to read nuclei files
public class AceTreeLoader {
	
	public static void loadNucFiles(String filePath) {
		//String classpath = System.getProperty("java.class.path");
		//System.out.println("Classpath: " + classpath);
		
		
		
		filePath += "/WormGUIDES.jar";
		
		File file = new File(filePath);
		
		System.out.println(file.getAbsolutePath());
		
		try {
			JarFile jarFile = new JarFile(new File(filePath));
			JarEntry entry = jarFile.getJarEntry("wormguides/nuclei_files/");
			System.out.println(entry.getName());
			File textFile = new File(entry.toString());
			System.out.println(textFile.isDirectory());
			for (File f : textFile.listFiles()) {
				BufferedReader br = new BufferedReader(new FileReader(f.getAbsolutePath()));
				String line;
				while ((line = br.readLine()) != null)
					System.out.println(line);
				br.close();
			}
			
			jarFile.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public static void main (String[] args) {
		loadNucFiles("./");
	}
	
}
