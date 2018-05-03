
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

28.4.18
Each node can hold 1 image see https://www.tutorialspoint.com/javafx/javafx_images.htm
(enables scalability by add child nodes if needed, in a single node)
What about sound/video?

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
ClauseContainer dataLinkParent; //the node to 'follow' for follow mode.
ArrayList<ClauseContainer> myChildNodes = new ArrayList<ClauseContainer>();

//This node's data and level in tree:
Clause dataClause = new Clause(); 

String nodecategory = "";
int nodelevel = 0; //start at root 0 (project) allows future tree expansion
int nodeGUIloc = 0; //to store Stage or GUI element where the node is located
//(nodelocation can match Stage?)
// store the node's preference for what view to start in.
String userNodeView;

//As of 26.4.18 - Make this node hold its own text, title (for now use docnotes as node text)
//consequences: the concept of a 'clause' can be replaced by 
//a) nodeCat to hold node category b) docnotes here to hold the text itself. 

/* 3.5.18 - introduce possibility of data link (parent).  
i.e. separation of 
(a) structure of related concent: child nodes for navigation/association
(b) links for association (mirroring/styling) of data from another node.
should this be all content, or just text?
in effect, this node can carry a pointer to a parent node that will override specific contents
a 'persistent' override will always override (i.e. box is just a shell: points to master copy somewhere?).
i.e. the shell is subordinate/dependant: a 'linked box'.  How to show?
Linked boxes content (displayNode) not directly editable, but can they open the parent data node?
an 'apply once' will just refresh the static data (which elements of node?).
(this is no different to a 'copy data' option for a selected target?)
a 'refresh' option can reproduce nodes and refresh them from parent data links AT THAT TIME.
if there is a parent link, when can local (static) data change?
if data links at are node level, then opening a node will display the parent node content...
----
Maybe the 'displayNode', that is currently inside a StageManager (on Open) is not the only Node 'layer'
i.e. there can be a contentNode and a display node inside a StageManager...
(a parent data link, and static content/child data link?).
The StageManager (node viewer) can decide which of these has priority at any time?
i.e. you can switch on a data override, so that a node will display a parent OR a node can be edited as if it is independent.
i.e. your GUI can help decide what state a node is on - follower node, or independent node.
follower nodes are helpful for showing data, but when in follower mode, we say not editable.
i.e.'userView' state is GUI level for layout.
the 'dataMode' is follower or editable.  You can set to 'follower', then copy once, then go to editable.
or you can leave it in follower mode.
{This is really a user-level, flexible pointer system}.
nb - if we store 'userView' in the node itself (rather than the StageManager), then even when the GUI recreates the scene, it can find the last setting for "UserView" and reinstate that as well.
this way, we can pre-save the views for presentations.

*/
//empty constructor no arguments
public ClauseContainer() {

}

//constructor with category
public ClauseContainer(String category) {
	setNodeCategory(category);

}

/*constructor with category and node text
The label is used to set document name and heading 
In turn, document name will be used for the viewer title when Node is opened. */

public ClauseContainer(NodeCategory nodecat, String nodetext, String label) {
	setNC(nodecat);
	setNotes(nodetext);
	//setDocName(nodecat.getCategory()); //default
    //setHeading("heading");
    //setShortname(nodecat.getCategory());
    setOutputText("output");
    setType(nodecat.getCategory());
    setAuthorName("Craig");
    //heading/label for this node
    setDocName(label);
    setHeading(label);
    setShortname(label);
}

//constructor 2 - a default container based on category 
//checks current docnum for this stage and advances it.
//TO DO: advance docnumber based on category.
public ClauseContainer (NodeCategory nodecat) {
	int docNum = nodecat.advanceDocCount();
    setDocName(nodecat.getCategory()+docNum);
    setNC(nodecat);
    setHeading("heading");
    setShortname(nodecat.getCategory()+docNum);
    setOutputText("output");
    setType(nodecat.getCategory());
    setAuthorName("Craig");
}

//META
public void setType(String myString) {
	this.ContainerType=myString;
}

public String getType() {
	return this.ContainerType;
}

//DEFAULT USER VIEWS
public String getUserView() {
	return this.userNodeView;
}

public void setUserView(String myView) {
	this.userNodeView=myView;
}

//---PARENT NODE DATA ---
public void setParentNode(ClauseContainer node) {
	this.myParentNode = node;
}

public void unsetParentNode() {
	/*ClauseContainer parentNode=getParentNode();
        if (parentNode==null) {
            System.out.println("No parent node to unset");
            return;
        }
        */
    if (this.myParentNode!=null) {
    	this.myParentNode.removeChildNode(ClauseContainer.this);
    }
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
//using a category object
public NodeCategory getNC() {
	return this.nodeCat;
}

public void setNC(NodeCategory myNC) {
	this.nodeCat=myNC;
}

//a query from the category object itself.
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