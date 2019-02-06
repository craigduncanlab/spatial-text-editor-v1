import java.net.*;
import java.io.*;
import java.util.*; //scanner, HashMap etc


public class Config {

//File IO locations.  If running from classes folder with java -cp ./ Main use ../templates
String projectfolder = "/home/craig/Documents/powerdock";
String templatesfolder = projectfolder+"/templates/";
String recentsfolder = projectfolder+"/config/";

public Config() {
	
}

//return a graph node holding the current dictionary as a graph of subnodes
public String getProjectFolder () {
	return projectfolder;
}

public String getRecentsFolder () {
	return recentsfolder;
}

public String getTemplatesFolder () {
	return templatesfolder;
}

}