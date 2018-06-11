import java.net.*;
import java.io.*;
import java.util.*; //scanner, HashMap etc


public class TemplateUtil {

//File IO locations
String searchfolder = "../templates/";
//HashMap for Graph Structure and data;
HashMap<Integer,ClauseContainer> graphmap=new HashMap<Integer,ClauseContainer> ();
       

public TemplateUtil() {
	
}

//return a graph node holding the current dictionary as a graph of subnodes
public ClauseContainer getTemplate(String filename) {
	return readSimpleTemplate(filename);
}

/*
Method to allow easy setup of templates just using headings in a text file.
the first entry in each row read in as a a container (node)
the rest of the entries in each row are read in as sub-nodes of that node
The node returned is the root node which is to be placed onto the workspace.
*/

private ClauseContainer readSimpleTemplate(String filename) {
	int nodeindex=0;
	String fileref=this.searchfolder+filename+".pdt";
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


//return a graph node holding the current dictionary as a graph of subnodes, but with content included
//Node 0 will be the root node, to be returned.

public ClauseContainer getStructuredData(String filename) {
	ClauseContainer[] myNodeBase =readNodeData(filename);
	readStructure(myNodeBase,filename);
	return myNodeBase[0];
}


//fill the graph structure with the stored data, then return first entry (root data node)
private ClauseContainer[] readNodeData(String filename) {
	String fileref=this.searchfolder+filename+".pdd";
	ClauseContainer[] newNodeBase=new ClauseContainer[300];
	try {
		Scanner scanner1 = new Scanner(new File(fileref));
		if (scanner1==null) {
			System.out.println("No text/html content");
			return newNodeBase;
		}
		while (scanner1.hasNextLine()) {
			String thisRow=scanner1.nextLine();
			Scanner scanner2= new Scanner(thisRow).useDelimiter(","); //change to benign delimiter
			String hdword = scanner2.next();
			int noderef = Integer.valueOf(hdword);
			String name = scanner2.next();
			String heading = scanner2.next();
			String notes = scanner2.next();
			NodeCategory NC_templ = new NodeCategory ("template",77,"gold");
			newNodeBase[noderef] = new ClauseContainer(NC_templ);
			newNodeBase[noderef].setDocName(name);
			newNodeBase[noderef].setHeading(heading);
			newNodeBase[noderef].setNotes(notes);
			scanner2.close();
		}
		scanner1.close();
	}
	catch (Throwable t)
	{
		t.printStackTrace();
		//System.exit(0);
		return newNodeBase;
	}

	//
	return newNodeBase; //return root node, with its references to other nodes
}

//rebuild hierarchical graph structure for use with data file
private void readStructure(ClauseContainer[] nodebase, String filename) {
	//ClauseContainer[] nodebase = new ClauseContainer[300];
	int nodeindex=0;
	String fileref=this.searchfolder+filename+".pdg";
	//
	try {
		Scanner scanner1 = new Scanner(new File(fileref));
		if (scanner1==null) {
			System.out.println("No text/html content");
			//return nodebase;
		}
		while (scanner1.hasNextLine()) {
			String thisRow=scanner1.nextLine();
			Scanner scanner2= new Scanner(thisRow).useDelimiter(",");
			//create node for first node in row
			String hdword = scanner2.next();
			int nodeID = Integer.valueOf(hdword);
			while (scanner2.hasNext()) {
				String rowword = scanner2.next();
				int childref = Integer.valueOf(rowword);
				if (childref==0) {
					System.out.println("Error in graph structure at nodeID:"+nodeID+" in row :"+hdword);
				}
				nodebase[nodeID].addChildNode(nodebase[childref]);
				nodebase[childref].setParentNode(nodebase[nodeID]);
			}
		scanner2.close();
		}
		scanner1.close();
	}
	catch (Throwable t)
	{
		t.printStackTrace();
		//System.exit(0);
		//return nodebase;
	}
	//return nodebase;
}


public void saveTemplate(ClauseContainer myNode, String filename) {
	GraphUtil myGraphUtil = new GraphUtil();
	ClauseContainer[] myGraphSeq = myGraphUtil.getGraphNodeOrder(myNode);
	System.out.println("container length: "+myGraphSeq.length);
	//structure
	int u=0;
	while (myGraphSeq[u]!=null)
	{
		ClauseContainer thisNode = myGraphSeq[u];
		System.out.println(u+":"+thisNode.toString());
		writeStructOutput(thisNode,filename);
		writeDataOutput(thisNode,filename);
		u++;
	}
}

/*  Method to write graph structure to file (.pdg)
	Obtain a hashmap with the sequenced nodes with an index
	write the hashmap to the .pdg (structure file)
	write the contents of each node in the hashmap to a .pdd file with each row being index, then data
*/


private void writeStructOutput(ClauseContainer myNode, String filename) {
	String reportfile="../templates/"+filename+".pdg";
	try {
	PrintStream console = System.out;
	PrintStream outstream = new PrintStream(new FileOutputStream(reportfile,true));
	System.setOut(outstream);
	String logString = Integer.toString(myNode.getNodeRef())+","+getChildrenList(myNode);
	System.out.println(logString);
	outstream.close();
	System.setOut(console);
	}
		catch (Throwable t)
		{
			t.printStackTrace();
			return;
		}
}

private String getChildrenList(ClauseContainer thisNode) {
	if (thisNode.NodeIsLeaf()==true) {
		return "";
	}
	String output="";
	ArrayList<ClauseContainer> childrenArray = thisNode.getChildNodes();
	Iterator<ClauseContainer> iterateChildren = childrenArray.iterator();
	while (iterateChildren.hasNext()) {
		ClauseContainer nextNode = iterateChildren.next();
		output=output+Integer.toString(nextNode.getNodeRef())+",";
		}
	return output;
	}

private void writeDataOutput(ClauseContainer myNode, String filename) {
	String reportfile="../templates/"+filename+".pdd";
	try {
	PrintStream console = System.out;
	PrintStream outstream = new PrintStream(new FileOutputStream(reportfile,true));
	System.setOut(outstream);
	String logString = Integer.toString(myNode.getNodeRef())+","+myNode.getDocName()+","+myNode.getHeading()+","+myNode.getNotes()+",";
	System.out.println(logString);
	outstream.close();
	System.setOut(console);
	}
		catch (Throwable t)
		{
			t.printStackTrace();
			return;
		}
}

//method to write graph data to file (.pdd)
private void writeFill() {

}


}