
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


*/

public class ClauseContainer extends Collection implements java.io.Serializable {

//mark this class this to allow for changes to variables in class (refactoring)
private static final long serialVersionUID = -64702044414208496L;

//Graph nodes along edges:
ClauseContainer myParentNode; 
ArrayList<ClauseContainer> myChildNodes = new ArrayList<ClauseContainer>();

//This node's metadata
String ContainerType=""; 
int numClauses=0; //this will hold number of clauses
String docname=""; //to hold the container name or filename
String docauthor=""; //to hold author name
String docnotes=""; //to hold Document notes
String date="";

//This node's data and level in tree:
Clause dataClause = new Clause(); 
NodeCategory nodeCat = new NodeCategory();
String nodecategory = "";
int nodelevel = 0; //start at root 0 (project) allows future tree expansion
int nodeGUIloc = 0; //to store Stage or GUI element where the node is located
//(nodelocation can match Stage?)


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

public String getDocName() {
	return this.docname;
}

public void setDocName(String doc) {
	this.docname = doc;
}

//---PARENT NODE ---
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
public ArrayList<ClauseContainer> getClauseArray() {
	return this.myChildNodes;
}

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



//THIS NODE'S DATA

public void addNodeClause(Clause thisClause) {
	this.dataClause = thisClause;
}

public Clause getNodeClause() {
	return this.dataClause;
}

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
	Iterator<ClauseContainer> myNodeIt = this.myChildNodes.iterator();
	String output="";
	output=output+"\n "+testCat+" \n\n";
	printClauseCategory(testCat);
	while (myNodeIt.hasNext()) {
		ClauseContainer myNode = myNodeIt.next();
		String category = myNode.getNodeCategory();
		Clause myclause = myNode.getNodeClause();
		if (category.equals(testCat)) {
			String mylabel = myclause.getLabel();
			String myheading = myclause.getHeading();
			String mytext = myclause.getClauseText();
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