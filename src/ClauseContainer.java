
//import utilities needed for Arrays lists etc
import java.util.*;
//package should include the Definition class

/* By Craig Duncan 1.12.2017 (based on Definitions Container made 21.11.2017)
This will store Clause objects, not the 'SpriteBox' that may enclose specific Clauses.

You can use a ClauseContainer to quickly read of Clauses and then create SpriteBoxes for them in GUI.
Or create empty SpriteBoxes and populate with Clauses from a list?

3.4.18
Design notes:
GUI effectively allows copying of nodes at any level = branch cut/copy
i.e. node inspection in GUI or copy to WS is like temporary node detachment.
although GUI Stages may closely match Node levels, they are independent of one another
i.e. Stages can be made for ease of collecting Nodes at same level.
GUI also allows setting of Node level in background. ("New" = a  node level setter).

GUINodes so far have been the 'nodes' that hold stage contents.
These can either hold Boxes (if serializable) or a copy of the data Nodes that are in the boxes on a stage.
It comes down to data separation.
So far I've enforced 'levels' by allowing nodes to move acoss stages and thereby acquire a 'level'

My first design had: data levels were associated with what the GUI displayed:
i.e. level 2 was whatever the 'CollectionNode' for CollectionStage displayed.
However the Workspace was always intended to hold Nodes at any level
(in the GUI, it became a separate Node repository).
The solution is to have Collections of the Data held in the Stages,
and the GUI will display that Node on a particular stage when needed.
However, we can still enforce Stages only showing certain levels of objects if Nodes record level
(but relax this in the case of the Workspace) 

However, if each Node stores a 'level' then it will allow some search and save for particular kinds of nodes later, regadless of where they are located in the GUI.

27.4.18
Testing the idea that every node is a functional workspace.
i.e. it has input (text data) and output (text) areas, both can be display in GUI.
The GUI can then apply any of the general operations on text to any node (do not need a specific importer window - just use the current node text area).
This repurposes any space, makes the environment flexible and nodes are functional
--> everything is local (a kind of OO design?).  
The fact that a node can be added as a child makes this scaleable.

*/

public class ClauseContainer extends Collection implements java.io.Serializable {

//mark this class this to allow for changes to variables in class (refactoring)
private static final long serialVersionUID = -64702044414208496L;


//This node's metadata
String ContainerType=""; 
int numClauses=0; //this will hold number of clauses
//NODE INPUT FIELDS
String docname=""; //to hold the container name or filename
String docauthor=""; //to hold author name
String docnotes=""; //to hold Document notes
String shortname="";
String heading="";
String date="";
//NODE OUTPUT FIELDS
String output="";

//NODE CATEGORY
NodeCategory nodeCat = new NodeCategory();

//NODE ASSOCIATIONS: i.e. Graph nodes along edges:
ClauseContainer myParentNode; 
ArrayList<ClauseContainer> myChildNodes = new ArrayList<ClauseContainer>();

//This node's data and level in tree:
Clause dataClause = new Clause(); 

String nodecategory = "";
int nodelevel = 0; //start at root 0 (project) allows future tree expansion
int nodeGUIloc = 0; //to store Stage or GUI element where the node is located
//(nodelocation can match Stage?)

//As of 26.4.18 - Make this node hold its own text, title (for now use docnotes as node text)
//consequences: the concept of a 'clause' can be replaced by 
//a) nodeCat to hold node category b) docnotes here to hold the text itself. 

//empty constructor no arguments
public ClauseContainer() {

}

//constructor with category
public ClauseContainer(String category) {
	setNodeCategory(category);

}

//META
public void setType(String myString) {
	this.ContainerType=myString;
}

public String getType() {
	return this.ContainerType;
}

//---PARENT NODE DATA ---
public void setParentNode(ClauseContainer node) {
	this.myParentNode = node;
}

public void unsetParentNode() {
	this.myParentNode = null;
}

public ClauseContainer getParentNode() {
	return this.myParentNode;
}

//---CHILD NODES---

public void addChildNode(ClauseContainer node) {
	this.myChildNodes.add(node);
	node.setParentNode(ClauseContainer.this);
}

public void removeChildNode(ClauseContainer node) {
	this.myChildNodes.remove(node);
}

public ArrayList<ClauseContainer> getChildNodes() {
	return this.myChildNodes;
}

public Boolean NodeIsLeaf() {
	return this.myChildNodes.isEmpty();
}

//method to print clause category
public void addNodeChildren(ClauseContainer parentNode) {
	if (parentNode.getChildNodes().size()==0) {
		System.out.println("No child nodes to add");
		return;
	}
	ArrayList<ClauseContainer> childrenArray = parentNode.getChildNodes();
	Iterator<ClauseContainer> NodeCycle = childrenArray.iterator();
	while (NodeCycle.hasNext()) {
		ClauseContainer myNode = NodeCycle.next();
		addChildNode(myNode);
		}
	}

//CATEGORY DATA
public NodeCategory getNC() {
	return this.nodeCat;
}

public void setNC(NodeCategory myNC) {
	this.nodeCat=myNC;
}

//TRANSITION METHODS

//replace function name with 'addChildClause' i.e. Child node with clause
public void addClause(Clause newClause) {
	//new Container with the clause as data
	ClauseContainer newContainer = new ClauseContainer();
	newContainer.addNodeClause(newClause);
	//add Clause Container as child node
	this.myChildNodes.add(newContainer);
}

//replace function name with 'addChildClause' i.e. Child node with clause
public void removeClause(Clause oldClause) {
	this.myChildNodes.remove(oldClause);
}

//rename this function - it returns all child nodes, but not data
//redundant: use getChildNodes instead.
public ArrayList<ClauseContainer> getClauseArray() {
	return this.myChildNodes;
}

//METHODS FOR INTERNAL AND EXTERNAL UPDATES TO NODE

/*Method to set ClauseContainer's array by copying each entry
Rename this */

public void setAllChildNodes(ArrayList<ClauseContainer> myArray) {
	ArrayList<ClauseContainer> tempArray = new ArrayList<ClauseContainer>(); 
	Iterator<ClauseContainer> myIterator = myArray.iterator(); 
	while (myIterator.hasNext()) {
		ClauseContainer tempNode = myIterator.next();
		tempArray.add(tempNode);
	}
	this.myChildNodes=tempArray;
}

//set the text that will be the main descriptive or clause text in this node
public void setNotes (String myString) {
	this.docnotes = myString;
}

public String getNotes () {
	return this.docnotes;
}

//set the text that will be the main text for identifying this node
public void setDocName (String myString) {
	this.docname = myString;
}

public String getDocName () {
	return this.docname;
}

//set the text that will be the main text for identifying this node
public void setHeading (String myString) {
	this.heading = myString;
}

public String getHeading () {
	return this.heading;
}

public void setShortname (String myString) {
	this.shortname = myString;
}

public String getShortname () {
	return this.shortname;
}

//NODE'S OUTPUT TEXT FIELD
public String getOutputText() {
	return this.output;
}

public void setOutputText(String myString) {
	this.output = myString;
}

//THIS NODE'S CLAUSE DATA (OBSOLETE)

public void addNodeClause(Clause thisClause) {
	this.dataClause = thisClause;
}

public Clause getNodeClause() {
	return this.dataClause;
}

//THIS NODE'S CATEGORY


public String getNodeCategory() {
	return this.nodeCat.getCategory();
}

public void setNodeCategory(String myCat) {
	this.nodeCat.setCategory(myCat);
}

public int getNodeLevel() {
	return this.nodeCat.getLevel();
}

public void setNodeLevel(int myLevel) {
	this.nodeCat.setLevel(myLevel);
}

public int getNodeLocation() {
	return this.nodeGUIloc;
}

public void setNodeLocation(int myLoc) {
	this.nodeGUIloc=myLoc;
}

public String getNodeColour() {
	return this.nodeCat.getColour();
}

public void setNodeColour(String myCol) {
	this.nodeCat.setColour(myCol);
}

/* This method makes use of the fact that an ArrayList is part of Java's collections, and as such, we can call a method that creates an iterator object, and use it.
*/

public void doPrintIteration() {
	//Do first iteration to print out only Definitions in sequence
	if (NodeIsLeaf()==false) {
		return;
	}
	//TO DO
}

//method to print clause category
private String printClauseCategory(String testCat) {
	if (this.myChildNodes.size()==0) {
		System.out.println("No child nodes to print");
		return "";
	}
	Iterator<ClauseContainer> myNodeIt = this.myChildNodes.iterator();
	String output="";
	output=output+"\n "+testCat+" \n\n";
	//printClauseCategory(testCat);
	while (myNodeIt.hasNext()) {
		ClauseContainer myNode = myNodeIt.next();
		String category = myNode.getNodeCategory();
		//Clause myclause = myNode.getNodeClause();
		if (category.equals(testCat)) {
			String mylabel = myNode.getDocName();
			String myheading = myNode.getHeading();
			String mytext = myNode.getNotes();
			//output=output+myheading+" ("+category+")"+":\n----------\n"+mytext+"\n\n";
			output=output+"\""+myheading+"\""+" means "+mytext+"\n";
		}
	}
	return output;
}


/* Method to summarily output different categories of Clause in this Node.
*/

public String getClauseAndText() {
	
	String outText = printClauseCategory("legalrole");
	outText=outText+printClauseCategory("definition");
	outText=outText+printClauseCategory("clause");
	outText=outText+printClauseCategory("event");
	return outText;
}

/* method to return number of clauses in this Container */

public int getNumClauses() {
	return this.myChildNodes.size();
}

public ClauseContainer cloneContainer() {
	ClauseContainer clone = new ClauseContainer();
	clone.setAllChildNodes(this.myChildNodes);  
	clone.setNumClauses(this.numClauses); //this will hold number of clauses
	clone.setDocName(this.docname); //to hold the container name or filename
	clone.setAuthorName(this.docauthor); //to hold author name
	clone.setNotes(this.docnotes);
	clone.setDate(this.date);
	clone.setType(this.ContainerType);
	return clone;
}

}