
//import utilities needed for Arrays lists etc
import java.util.*;
//package should include the Definition class

/* By Craig Duncan 1.12.2017 (based on Definitions Container made 21.11.2017)
This will store Clause objects, not the 'SpriteBox' that may enclose specific Clauses.

You can use a ClauseContainer to quickly read of Clauses and then create SpriteBoxes for them in GUI.
Or create empty SpriteBoxes and populate with Clauses from a list?

*/

public class ClauseContainer {
//setup declare instance variables. shared in class if preceded by static.	
ArrayList<Clause> myClauses = new ArrayList<Clause>();  
int numClauses=0; //this will hold number of clauses
String docname=""; //to hold the container name or filename
//Stage ContainerStage = new Stage(); //to hold Stage associated with this container?

//empty constructor no arguments
public ClauseContainer() {

}

//FILE FUNCTIONS
public void setDocName(String myString) {
	this.docname=myString;
}

public String getDocName() {
	return this.docname;
}

/* STAGE SYNC
public void setStage(Stage myStage) {
	this.ContainerStage=myStage;
}

public String getStage() {
	return this.ContainerStage;
}
*/
//CLAUSE OPS
public void addClause(Clause newClause) {
	this.myClauses.add(newClause);
}

/* TO DO: remove clause 
Java ArrayList remove
"Removes the first occurrence of the specified element from this list, if it is present."
*/

public void removeClause(Clause oldClause) {
	this.myClauses.remove(oldClause);
}


/* This method makes use of the fact that an ArrayList is part of Java's collections, and as such, we can call a method that creates an iterator object, and use it.
*/

public void doPrintIteration() {
	//Do first iteration to print out only Definitions in sequence
	Iterator<Clause> myDefiterator = this.myClauses.iterator();
	while (myDefiterator.hasNext()) {
		Clause myclause = myDefiterator.next();
		String category =myclause.getCategory();
		if (category.equals("definition")) {
			String mylabel = myclause.getLabel();
			String myheading = myclause.getHeading();
			String mytext = myclause.getClause();
			//System.out.println(mylabel+"(label) "+myheading+"("+category+")"+" : "+mytext);
			System.out.println("\\\""+myheading+"\\\""+" means "+mytext+"\n");
		}
	}
	//everthing else
	Iterator<Clause> myiterator = this.myClauses.iterator();
	while (myiterator.hasNext()) {
		Clause myclause = myiterator.next();
		String category =myclause.getCategory();
		if (!category.equals("definition")) {
			String mylabel = myclause.getLabel();
			String myheading = myclause.getHeading();
			String mytext = myclause.getClause();
			System.out.println(mylabel+"(label) "+myheading+"("+category+")"+" : "+mytext);
		}
	}
}



/* 
This method returns both labels and text.  It uses the instance container. 
TO DO: store Clauses that aren't definitions on first pass, then print them in second run
(small time saver)
*/

public String getClauseAndText() {
	
	//Do first iteration to print out only Definitions
	Iterator<Clause> myDefiterator = this.myClauses.iterator();
	String output="";
	output=output+"\nLegal Roles\n\n";
	while (myDefiterator.hasNext()) {
		Clause myclause = myDefiterator.next();
		String category = myclause.getCategory();
		if (category.equals("legalrole")) {
			String mylabel = myclause.getLabel();
			String myheading = myclause.getHeading();
			String mytext = myclause.getClause();
			//output=output+myheading+" ("+category+")"+":\n----------\n"+mytext+"\n\n";
			output=output+"\""+myheading+"\""+" means "+mytext+"\n";
		}
	}
	output=output+"\nDefinitions\n\n";
	myDefiterator = this.myClauses.iterator();
	while (myDefiterator.hasNext()) {
		Clause myclause = myDefiterator.next();
		String category = myclause.getCategory();
		if (category.equals("definition")) {
			String mylabel = myclause.getLabel();
			String myheading = myclause.getHeading();
			String mytext = myclause.getClause();
			//output=output+myheading+" ("+category+")"+":\n----------\n"+mytext+"\n\n";
			output=output+"\""+myheading+"\""+" means "+mytext+"\n";
		}
	}
	output=output+"\nOperative Provisions\n\n";
	myDefiterator = this.myClauses.iterator();
	while (myDefiterator.hasNext()) {
		Clause myclause = myDefiterator.next();
		String category = myclause.getCategory();
		if (category.equals("clause")) {
			String mylabel = myclause.getLabel();
			String myheading = myclause.getHeading();
			String mytext = myclause.getClause();
			//output=output+myheading+" ("+ocategory+")"+":\n----------\n"+mytext+"\n\n";
			output=output+myheading+"\n"+mytext+"\n";
		}
	}
	output=output+"\nEvents\n\n";
	myDefiterator = this.myClauses.iterator();
	while (myDefiterator.hasNext()) {
		Clause myclause = myDefiterator.next();
		String category = myclause.getCategory();
		if (category.equals("event")) {
			String mylabel = myclause.getLabel();
			String mydate = myclause.getEventDate();
			String eventheading = myclause.getHeading();
			String mytext = myclause.getClause();
			//output=output+myheading+" ("+ocategory+")"+":\n----------\n"+mytext+"\n\n";
			output=output+mydate+" - "+eventheading+"\n"+mytext+"\n";
		}
	}
	output=output+"\nOthers:\n\n";
	//everthing else
	//Iterator<Clause> myiterator = this.myClauses.iterator();
	myDefiterator = this.myClauses.iterator();
	while (myDefiterator.hasNext()) {
		Clause myclause = myDefiterator.next();
		String category = myclause.getCategory();
		if (!category.equals("definition")&&!category.equals("event")&&!category.equals("clause")&&!category.equals("legalrole")) {
			String mylabel = myclause.getLabel();
			String myheading = myclause.getHeading();
			String mytext = myclause.getClause();
			//output=output+myheading+" ("+ocategory+")"+":\n----------\n"+mytext+"\n\n";
			output=output+myheading+"\n"+mytext+"\n";
		}
	}
	return output;
}

/*Method to set ClauseContainer's array in one step*/
public void setClauseArray(ArrayList<Clause> myArray) {
	this.myClauses = myArray;
}

public ArrayList<Clause> getClauseArray() {
	return this.myClauses;
}

/* method to return number of clauses in this Container */

public int getNumClauses() {
	return this.myClauses.size();
}

}