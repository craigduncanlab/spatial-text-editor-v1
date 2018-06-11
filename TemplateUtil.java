import java.net.*;
import java.io.*;
import java.util.*; //scanner etc


public class TemplateUtil {

//File IO locations
String searchfolder = "../templates/";


public TemplateUtil() {
	
}

//return a graph node holding the current dictionary as a graph of subnodes
public ClauseContainer getTemplate(String filename) {
	return readTemplate(filename);
}

private ClauseContainer readTemplate(String filename) {
	String fileref=this.searchfolder+filename;
	String boxlabel = "Template";
	NodeCategory NC_templ = new NodeCategory ("template",77,"gold");
	ClauseContainer templateNode = new ClauseContainer(NC_templ);
	templateNode.setDocName(filename);
	try {
		Scanner scanner1 = new Scanner(new File(fileref));
		if (scanner1==null) {
			System.out.println("No text/html content");
			return null;
		}
		int nl=0;
		while (scanner1.hasNextLine()) {
			nl++;
			String thisRow=scanner1.nextLine();
			Scanner scanner2= new Scanner(thisRow).useDelimiter(",");
			//create node for first word in row
			String hdword = scanner2.next();
			ClauseContainer wordNode = new ClauseContainer(NC_templ,templateNode,hdword,hdword);
			templateNode.addChildNode(wordNode);
			//create child nodes for rest of words in row
			while (scanner2.hasNext()) {
				String rowword = scanner2.next();
				wordNode.addChildNode(new ClauseContainer(NC_templ,wordNode,rowword,rowword));
			}
		scanner2.close();
		}
		scanner1.close();
	}
	catch (Throwable t)
	{
		t.printStackTrace();
		//System.exit(0);
		return null;
	}
	return templateNode;
}


}