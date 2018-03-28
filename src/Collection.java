
//import utilities needed for Arrays lists etc
import java.util.*;
//package should include the Definition class

/* By Craig Duncan 27.3.18
*/

public class Collection implements java.io.Serializable {

//mark this class this to allow for changes to variables in class (refactoring)
private static final long serialVersionUID = -503004441412532L;
//setup declare instance variables. shared in class if preceded by static.	
//TO DO: Make this generic i.e. will hold different objects, even if not subclasses?
ArrayList<ClauseContainer> myCollection = new ArrayList<ClauseContainer>();  
int numItems=0; //this will hold number of clauses
String docname=""; //to hold the container name or filename
String docauthor=""; //to hold author name
String docnotes=""; //to hold Document notes
//Stage ContainerStage = new Stage(); //to hold Stage associated with this container?

//empty constructor no arguments
public Collection() {

}

//FILE FUNCTIONS
public void setDocName(String myString) {
	this.docname=myString;
}

public String getDocName() {
	return this.docname;
}

//CLAUSE OPS
public void addCC(ClauseContainer newCC) {
	this.myCollection.add(newCC);
}

public void addCount() {
	this.numItems++;
}

public int getCount() {
	return this.numItems;
}

/* TO DO: remove clause 
Java ArrayList remove
"Removes the first occurrence of the specified element from this list, if it is present."
*/

public void removeCC(ClauseContainer oldCC) {
	this.myCollection.remove(oldCC);
}

public ArrayList<ClauseContainer> getCollectionItems() {
	return this.myCollection;
}

public void setCollectionItems(ArrayList<ClauseContainer> myItems) {
	this.myCollection = myItems;
	//TO DO: iteration? 
}

}