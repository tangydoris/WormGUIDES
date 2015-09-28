# WormGUIDES
Desktop version of the WormGUIDES iPad and Android apps.

Source code can be found at https://github.com/tangydoris/WormGUIDES.

Version currently uses JRE 1.8 version 60+. Source built using JRE 1.8 version 66 downloaded from https://jdk8.java.net/download.html.

URL feature documentation can be found in src/wormguides/model/URLDocumentation.txt.




/*****About*****/

WormGUIDES is a collaboration led by Drs. Zhirong Bao (MSKCC), Daniel Colon-Ramos (Yale), William Mohler (UConn) and Hari Shroff (NIH). For more information, visit our website at http://wormguides.org. 

The WormGUIDES app is developed and maintained by the laboratories of Dr. Zhirong Bao and Dr. William Mohler. Major contributors of the desktop app include Doris Tang (New York University) and Dr. Anthony Santella of the Bao Laboratory. For questions or comments contact support@wormguides.org.




/*****Notes on Eclipse Project Setup*****/

Eclipse Setup:
Download Eclipse Mars
Download the latest JDK (currently using early release of Java 1.8.0_66 found at https://jdk8.java.net/download.html)
Make sure environment variables point to the bin directory of the jdk (usually found in C:/Program Files/Java/jdk.../bin)
If Eclipse cannot open, try to modify the eclipse.ini configuration file in the main eclipse directory
Change the required java version in the argument -Dosgi.requiredJavaVersion from 1.7 to 1.8
Make sure the -vm argument points to the correct javaw.exe for your Java 1.8+ version

Project Setup:
Select File > Import > Existing Projects into Workspace
Choose the Git directory you cloned and hit Finish


If working with WormGUIDES:
JavaFX Runtime and Tooling is helpful in development this app in eclipse
In Eclipse, go to Help > Install New Software...
Insert the address http://download.eclipse.org/efxclipse/updates-released/2.0.0/site into the Location field and hit OK
Choose e(fx)clipse - install and pick the appropriate Eclipse IDE release
Go through the wizard and restart Eclipse when finished
(Steps taken from https://www.eclipse.org/efxclipse/install.html)


If working with AceTree:
If there are many compiler errors, it is probably due to Eclipse not recognizing the dependencies in the jar subdirectory
To fix this, right click on your project in the Package Explorer and go to Properties
Select Java Build Path and choose the Libraries tab
If there is a Java3D library listed, selected it and click Remove
Select Add Library... > User Library > User Libraries... > New...
Crease a new library with the name Java3D and hit OK
Select the new Java3D library under Defined user libraries and click Add JARs > AceTree > jars
Highlight all the jars in the directory and hit OK
Add the new library to the project's build path and exit out of Properties