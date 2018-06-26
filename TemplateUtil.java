import java.net.*;
import java.io.*;
import java.util.*; //scanner, HashMap etc


public class TemplateUtil {

//File IO locations
String searchfolder = "../templates/";
       

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


//return a graph node holding the current file as a graph of subnodes, but with content included
//Node 0 will be the root node, to be returned.

public ClauseContainer getStructuredData(String filename) {
	ClauseContainer[] myNodeBase = readNodeDataSetup(filename);
	return myNodeBase[0];
}


//fill the graph structure with the stored data, then return first entry (root data node)
private ClauseContainer[] readNodeData(String filename) {
	String fileref=this.searchfolder+filename+".pdd";
	ClauseContainer[] newNodeBase=new ClauseContainer[300];
	try {
		Scanner scanner1 = new Scanner(new File(fileref)).useDelimiter("@EOR"); //instead of \n
		if (scanner1==null) {
			System.out.println("No text/html content");
			return newNodeBase;
		}
		while (scanner1.hasNext()) {
			String thisRow=scanner1.next();
			Scanner scanner2= new Scanner(thisRow).useDelimiter("@@P"); //change to benign delimiter
			String hdword = scanner2.next();
			System.out.println(hdword);
			int noderef = Integer.valueOf(hdword);
			String name = scanner2.next();
			String heading = scanner2.next();
			String notes = scanner2.next();
			String htmltext = scanner2.next();
			NodeCategory NC_templ = new NodeCategory ("template",77,"gold");
			newNodeBase[noderef] = new ClauseContainer(NC_templ);
			newNodeBase[noderef].setDocName(name);
			newNodeBase[noderef].setHeading(heading);
			newNodeBase[noderef].setNotes(notes);
			newNodeBase[noderef].setHTML(htmltext);
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

/*This splits the powerdock file into the first part (structure) and second part (node data)
processes nodes and data first, then adds child nodes
*/
public ClauseContainer[] readNodeDataSetup(String filename) {
	ClauseContainer[] myNodeBase = new ClauseContainer[300];
	String fileref=this.searchfolder+filename+".pdn";
	//whole file
	try {
		String entireFileText = new Scanner(new File(fileref)).useDelimiter("\\A").next();
		String[] pdocfile = entireFileText.split("@@EndGraph@@"); //regex to keep delimeter in second part(look behind), not first. (@00P)|(?<=0@@P)
		String pds = pdocfile[0]; // 004
		String pdd = pdocfile[1];
	//process the nodes and their daa
	myNodeBase=readNodeDataString(pdd);
	System.out.println(".pdd done");
	/*System.out.println(pdd);
	System.exit(0);*/
	readStructureXYString(myNodeBase,pds);
	System.out.println(".pds done");
	}
	catch (Throwable t)
	{
		t.printStackTrace();
		//System.exit(0);
		return myNodeBase;
	}
	return myNodeBase;
	//readStructureXY(myNodeBase,filename);
	//System.out.println(".pds done");

}

//read node data pdd

private ClauseContainer[] readNodeDataString(String datastring) {
	//String fileref=this.searchfolder+filename+".pdd";
	ClauseContainer[] newNodeBase=new ClauseContainer[300];
	try {
		Scanner scanner1 = new Scanner(datastring).useDelimiter("@EOR"); //instead of \n
		if (scanner1==null) {
			System.out.println("No text/html content");
			return newNodeBase;
		}
		while (scanner1.hasNext()) {
			String thisRow=scanner1.next();
			Scanner scanner2= new Scanner(thisRow).useDelimiter("@@P"); //change to benign delimiter
			String hdword = scanner2.next();
			System.out.println(hdword);
			int noderef = Integer.valueOf(hdword);
			String docname = scanner2.next();
			String heading = scanner2.next();
			String inputtext = scanner2.next();
			String htmltext = scanner2.next();
			NodeCategory NC_templ = new NodeCategory ("template",77,"gold");
			newNodeBase[noderef] = new ClauseContainer(NC_templ);
			String outputtext="";
			newNodeBase[noderef].updateText(htmltext,docname,heading,inputtext,outputtext);
			newNodeBase[noderef].setUltimateParent(newNodeBase[0]);
			/*
			newNodeBase[noderef].setDocName(name);
			newNodeBase[noderef].setHeading(heading);
			newNodeBase[noderef].setNotes(notes);
			newNodeBase[noderef].setHTML(htmltext);
			*/
			/*if (noderef>0) {
				newNodeBase[noderef].setUltimateParent(newNodeBase[0]);//set all to root node
			}
			*/
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

//read structure with XY coordinates from a string.  Modifies existing objects passed in.

//rebuild hierarchical graph structure for use with data file
//This has X,Y position data for child nodes
private void readStructureXYString(ClauseContainer[] nodebase, String struct) {
	//ClauseContainer[] nodebase = new ClauseContainer[300];
	int nodeindex=0;
	//String fileref=this.searchfolder+filename+".pdn";
	//
	try {
		Scanner scanner1 = new Scanner(struct);
		if (scanner1==null) {
			System.out.println("No text/html content");
			//return nodebase;
		}
		//obtain max number of nodes from first line
		String firstRow=scanner1.nextLine();
		int nodeCount = Integer.valueOf(firstRow);
		int readCount=0;
		//
		while (scanner1.hasNextLine() && readCount<nodeCount) {
			String thisRow=scanner1.nextLine();
			Scanner scanner2= new Scanner(thisRow).useDelimiter(",");
			//create node for first node in row
			String hdword = scanner2.next();
			int nodeID = Integer.valueOf(hdword);
			while (scanner2.hasNext()) {
				//3 entries per child node
				String rowword = scanner2.next();
				String xpos = scanner2.next();
				String ypos = scanner2.next();
				//
				double x = Double.valueOf(xpos);
				double y = Double.valueOf(ypos);
				int childref = Integer.valueOf(rowword);
				if (nodebase==null) {
					System.out.println("Nodebase is null");
				}
				if (childref==0) {
					System.out.println("Error in graph structure at nodeID:"+nodeID+" in row :"+hdword);
				}
				nodebase[nodeID].addChildNode(nodebase[childref]);
				nodebase[childref].setParentNode(nodebase[nodeID]);
				nodebase[childref].setChildNodeXY(x,y); //position
			}
		scanner2.close();
		readCount++;
		}
		scanner1.close();
	}
	catch (Throwable t)
	{
		t.printStackTrace();
		//System.exit(0);
		//return nodebase;
	}
}

public void saveTemplateSingle(ClauseContainer myNode, String filename) {
	//
	cleantemplate(filename);
	//
	GraphUtil myGraphUtil = new GraphUtil();
	ClauseContainer[] myGraphSeq = myGraphUtil.getBFS(myNode);
	int nodeCount = myGraphUtil.getBFSnum();
	System.out.println("container length: "+myGraphSeq.length);
	System.out.println("unique nodes: "+nodeCount);
	//structure
	int u=0;
	writeFirstLineHeader(nodeCount, filename);
	while (myGraphSeq[u]!=null)
	{
		ClauseContainer thisNode = myGraphSeq[u];
		writeNewStructSingle(thisNode,filename);
		u++;
	}
	writeSplitString(filename);
	int p=0;
	while (myGraphSeq[p]!=null)
	{
		ClauseContainer thisNode = myGraphSeq[p];
		writeDataOutputNew(thisNode,filename);
		p++;
	}
}


/*  Method to write out row with node and child nodes listed as node index numbers
*/
private void cleantemplate(String filename) {
	String reportfile="../templates/"+filename+".pdn";
	cleanfile(reportfile);
}

private void cleanfile(String reportfile) {
	try {

	PrintWriter pw = new PrintWriter(reportfile);
	pw.close();
	}
		catch (Throwable t)
		{
			t.printStackTrace();
			return;
		}
}

//
private void writeNewStructSingle(ClauseContainer myNode, String filename) {
	String reportfile="../templates/"+filename+".pdn";
	try {
	PrintStream console = System.out;
	PrintStream outstream = new PrintStream(new FileOutputStream(reportfile,true));
	System.setOut(outstream);
	String logString = Integer.toString(myNode.getNodeRef())+","+getChildrenData(myNode);
	System.out.println(logString); //this needs a CR/LF so use println
	outstream.close();
	System.setOut(console);
	}
		catch (Throwable t)
		{
			t.printStackTrace();
			return;
		}
}


//write first line header with number of objects

private void writeFirstLineHeader(int entryCount, String filename) {
	String reportfile="../templates/"+filename+".pdn";
	try {
	PrintStream console = System.out;
	PrintStream outstream = new PrintStream(new FileOutputStream(reportfile,true));
	System.setOut(outstream);
	String logString = Integer.toString(entryCount);
	System.out.println(logString); //this needs a CR/LF so use println
	outstream.close();
	System.setOut(console);
	}
		catch (Throwable t)
		{
			t.printStackTrace();
			return;
		}
}

//write out structure and child node positions
//TO DO: In future, these could be binary data files
private String getChildrenData(ClauseContainer thisNode) {
	if (thisNode.NodeIsLeaf()==true) {
		return "";
	}
	String output="";
	ArrayList<ClauseContainer> childrenArray = thisNode.getChildNodes();
	Iterator<ClauseContainer> iterateChildren = childrenArray.iterator();
	String childstring = "";
	while (iterateChildren.hasNext()) {
		ClauseContainer nextNode = iterateChildren.next();
		childstring=Integer.toString(nextNode.getNodeRef())+",";
		childstring = childstring+ Double.toString(nextNode.getChildNodeX())+","+Double.toString(nextNode.getChildNodeY())+",";
		output=output+childstring;
		}
	
	return output;
	}


//write out structure only
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

//Method to write out row: node index plus data

private void writeSplitString(String filename) {
	String reportfile="../templates/"+filename+".pdn";
	String splitter="@@EndGraph@@";
	try {
	PrintStream console = System.out;
	PrintStream outstream = new PrintStream(new FileOutputStream(reportfile,true));
	System.setOut(outstream);
	System.out.print(splitter); //don't use println.  No CR needed.
	outstream.close();
	System.setOut(console);
	}
		catch (Throwable t)
		{
			t.printStackTrace();
			return;
		}
}

//Method to write out the data for the node passed as argument

private void writeDataOutputNew(ClauseContainer myNode, String filename) {
	String reportfile="../templates/"+filename+".pdn";
	try {
	PrintStream console = System.out;
	PrintStream outstream = new PrintStream(new FileOutputStream(reportfile,true));
	System.setOut(outstream);
	String logString = Integer.toString(myNode.getNodeRef())+"@@P"+myNode.getDocName()+"@@P"+myNode.getHeading()+"@@P"+myNode.getNotes()+"@@P"+myNode.getHTML()+"@@P@EOR";
	System.out.print(logString); //don't use println.  No CR needed.
	outstream.close();
	System.setOut(console);
	}
		catch (Throwable t)
		{
			t.printStackTrace();
			return;
		}
}

}